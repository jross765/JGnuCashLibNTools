package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager.class);

	// ---------------------------------------------------------------

	protected GnucashFileImpl gcshFile;

	private Map<GCshID, GnucashGenerInvoice> invcMap;

	// ---------------------------------------------------------------

	public FileInvoiceManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		invcMap = new HashMap<GCshID, GnucashGenerInvoice>();

		for ( Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext(); ) {
			Object bookElement = iter.next();
			if ( !(bookElement instanceof GncGncInvoice) ) {
				continue;
			}
			GncGncInvoice jwsdpInvc = (GncGncInvoice) bookElement;

			try {
				GnucashGenerInvoice invc = createGenerInvoice(jwsdpInvc);
				invcMap.put(invc.getID(), invc);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal (generic) Invoice-Entry with id=" + jwsdpInvc.getInvoiceId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in (generic) invoice map: " + invcMap.size());
	}

	protected GnucashGenerInvoiceImpl createGenerInvoice(final GncGncInvoice jwsdpInvc) {
		GnucashGenerInvoiceImpl invc = new GnucashGenerInvoiceImpl(jwsdpInvc, gcshFile);
		LOGGER.debug("createGenerInvoice: Generated new generic invoice: " + invc.getID());
		return invc;
	}

	// ---------------------------------------------------------------

	public void addGenerInvoice(GnucashGenerInvoice invc) {
		invcMap.put(invc.getID(), invc);
		LOGGER.debug("addGenerInvoice: Added (generic) invoice to cache: " + invc.getID());
	}

	public void removeGenerInvoice(GnucashGenerInvoice invc) {
		invcMap.remove(invc.getID());
		LOGGER.debug("removeGenerInvoice: Removed (generic) invoice from cache: " + invc.getID());
	}

	// ---------------------------------------------------------------

	public GnucashGenerInvoice getGenerInvoiceByID(final GCshID id) {
		if ( invcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashGenerInvoice retval = invcMap.get(id);
		if ( retval == null ) {
			LOGGER.error("getGenerInvoiceByID: No (generic) Invoice with id '" + id + "'. " + "We know "
					+ invcMap.size() + " accounts.");
		}

		return retval;
	}

	public Collection<GnucashGenerInvoice> getGenerInvoices() {

		Collection<GnucashGenerInvoice> c = invcMap.values();

		ArrayList<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>(c);
		Collections.sort(retval);

		return retval;
	}

	// ----------------------------

	public Collection<GnucashGenerInvoice> getPaidGenerInvoices()
			throws UnknownAccountTypeException {
		Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
		for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
			if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
				try {
					if ( invc.isCustInvcFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
				try {
					if ( invc.isVendBllFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
				try {
					if ( invc.isEmplVchFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB ) {
				try {
					if ( invc.isJobInvcFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidGenerInvoices: Serious error");
				}
			}
		}

		return retval;
	}

	public Collection<GnucashGenerInvoice> getUnpaidGenerInvoices()
			throws UnknownAccountTypeException {
		Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
		for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
			if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
				try {
					if ( invc.isNotCustInvcFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
				try {
					if ( invc.isNotVendBllFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
				try {
					if ( invc.isNotEmplVchFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidGenerInvoices: Serious error");
				}
			} else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB ) {
				try {
					if ( invc.isNotInvcJobFullyPaid() ) {
						retval.add(invc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidGenerInvoices: Serious error");
				}
			}
		}

		return retval;
	}

	// ----------------------------

	public Collection<GnucashCustomerInvoice> getInvoicesForCustomer_direct(final GnucashCustomer cust)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Customer.getInvoices_direct(this, cust);
	}

	public Collection<GnucashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Customer.getInvoices_viaAllJobs(cust);
	}

	public Collection<GnucashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Customer.getPaidInvoices_direct(this, cust);
	}

	public Collection<GnucashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Customer.getPaidInvoices_viaAllJobs(cust);
	}

	public Collection<GnucashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Customer.getUnpaidInvoices_direct(this, cust);
	}

	public Collection<GnucashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Customer.getUnpaidInvoices_viaAllJobs(cust);
	}

	// ----------------------------

	public Collection<GnucashVendorBill> getBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Vendor.getBills_direct(this, vend);
	}

	public Collection<GnucashJobInvoice> getBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Vendor.getBills_viaAllJobs(vend);
	}

	public Collection<GnucashVendorBill> getPaidBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Vendor.getPaidBills_direct(this, vend);
	}

	public Collection<GnucashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Vendor.getPaidBills_viaAllJobs(vend);
	}

	public Collection<GnucashVendorBill> getUnpaidBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Vendor.getUnpaidBills_direct(this, vend);
	}

	public Collection<GnucashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Vendor.getUnpaidBills_viaAllJobs(vend);
	}

	// ----------------------------

	public Collection<GnucashEmployeeVoucher> getVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Employee.getVouchers(this, empl);
	}

	public Collection<GnucashEmployeeVoucher> getPaidVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Employee.getPaidVouchers(this, empl);
	}

	public Collection<GnucashEmployeeVoucher> getUnpaidVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Employee.getUnpaidVouchers(this, empl);
	}

	// ----------------------------

	public Collection<GnucashJobInvoice> getInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException {
		return FileInvoiceManager_Job.getInvoices(this, job);
	}

	public Collection<GnucashJobInvoice> getPaidInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Job.getPaidInvoices(this, job);
	}

	public Collection<GnucashJobInvoice> getUnpaidInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		return FileInvoiceManager_Job.getUnpaidInvoices(this, job);
	}

	// ---------------------------------------------------------------

	public int getNofEntriesGenerInvoiceMap() {
		return invcMap.size();
	}

}
