package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshGenerInvcID, GnuCashGenerInvoice> invcMap;

	// ---------------------------------------------------------------

	public FileInvoiceManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		invcMap = new HashMap<GCshGenerInvcID, GnuCashGenerInvoice>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncInvoice) ) {
				continue;
			}
			GncGncInvoice jwsdpInvc = (GncGncInvoice) bookElement;

			try {
				GnuCashGenerInvoice invc = createGenerInvoice(jwsdpInvc);
				invcMap.put(invc.getID(), invc);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal (generic) Invoice-Entry with id=" + jwsdpInvc.getInvoiceId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in (generic) invoice map: " + invcMap.size());
	}

	protected GnuCashGenerInvoiceImpl createGenerInvoice(final GncGncInvoice jwsdpInvc) {
		GnuCashGenerInvoiceImpl invc = new GnuCashGenerInvoiceImpl(jwsdpInvc, gcshFile);
		LOGGER.debug("createGenerInvoice: Generated new generic invoice: " + invc.getID());
		return invc;
	}

	// ---------------------------------------------------------------

	public GnuCashGenerInvoice getGenerInvoiceByID(final GCshGenerInvcID invcID) {
		if ( invcID == null ) {
			throw new IllegalArgumentException("argument <invcID> is null");
		}
		
		if ( ! invcID.isSet() ) {
			throw new IllegalArgumentException("argument <invcID> is not set");
		}
		
		if ( invcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashGenerInvoice retval = invcMap.get(invcID);
		if ( retval == null ) {
			LOGGER.error("getGenerInvoiceByID: No (generic) Invoice with id '" + invcID + "'. " + "We know "
					+ invcMap.size() + " accounts.");
		}

		return retval;
	}

	public List<GnuCashGenerInvoice> getGenerInvoicesByType(final GCshOwner.Type type) {
		if ( type == GCshOwner.Type.UNDEFINED ) {
			throw new IllegalArgumentException("undefined type given");
		}
			
		if ( type != GnuCashGenerInvoice.TYPE_CUSTOMER &&
			 type != GnuCashGenerInvoice.TYPE_VENDOR &&
			 type != GnuCashGenerInvoice.TYPE_EMPLOYEE &&
			 type != GnuCashGenerInvoice.TYPE_JOB )
		{
			throw new IllegalArgumentException("Illegal owner type for invoice");
		}
		
		List<GnuCashGenerInvoice> result = new ArrayList<GnuCashGenerInvoice>();

		for ( GnuCashGenerInvoice invc : getGenerInvoices() ) {
			if ( invc.getType() == type ) {
				result.add(invc);
			}
		}

		return result;
	}

	public List<GnuCashGenerInvoice> getGenerInvoices() {
		if ( invcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		Collection<GnuCashGenerInvoice> c = invcMap.values();

		ArrayList<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>(c);
		Collections.sort(retval);

		return Collections.unmodifiableList(retval);
	}

	// ----------------------------

	public List<GnuCashGenerInvoice> getPaidGenerInvoices() {
		List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();
		for ( GnuCashGenerInvoice invc : getGenerInvoices() ) {
			if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
					if ( invc.isCustInvcFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
					if ( invc.isVendBllFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
					if ( invc.isEmplVchFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
					if ( invc.isJobInvcFullyPaid() ) {
						retval.add(invc);
					}
			}
		}

		return retval;
	}

	public List<GnuCashGenerInvoice> getUnpaidGenerInvoices() {
		List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();
		for ( GnuCashGenerInvoice invc : getGenerInvoices() ) {
			if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
					if ( invc.isNotCustInvcFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
					if ( invc.isNotVendBllFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
					if ( invc.isNotEmplVchFullyPaid() ) {
						retval.add(invc);
					}
			} else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
					if ( invc.isNotJobInvcFullyPaid() ) {
						retval.add(invc);
					}
			}
		}

		return retval;
	}

	// ----------------------------

	public List<GnuCashCustomerInvoice> getInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		return FileInvoiceManager_Customer.getInvoices_direct(this, cust);
	}

	public List<GnuCashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		return FileInvoiceManager_Customer.getInvoices_viaAllJobs(cust);
	}

	public List<GnuCashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		return FileInvoiceManager_Customer.getPaidInvoices_direct(this, cust);
	}

	public List<GnuCashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		return FileInvoiceManager_Customer.getPaidInvoices_viaAllJobs(cust);
	}

	public List<GnuCashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		return FileInvoiceManager_Customer.getUnpaidInvoices_direct(this, cust);
	}

	public List<GnuCashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		return FileInvoiceManager_Customer.getUnpaidInvoices_viaAllJobs(cust);
	}

	// ----------------------------

	public List<GnuCashVendorBill> getBillsForVendor_direct(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getBills_direct(this, vend);
	}

	public List<GnuCashJobInvoice> getBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getBills_viaAllJobs(vend);
	}

	public List<GnuCashVendorBill> getPaidBillsForVendor_direct(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getPaidBills_direct(this, vend);
	}

	public List<GnuCashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getPaidBills_viaAllJobs(vend);
	}

	public List<GnuCashVendorBill> getUnpaidBillsForVendor_direct(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getUnpaidBills_direct(this, vend);
	}

	public List<GnuCashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		return FileInvoiceManager_Vendor.getUnpaidBills_viaAllJobs(vend);
	}

	// ----------------------------

	public List<GnuCashEmployeeVoucher> getVouchersForEmployee(final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		return FileInvoiceManager_Employee.getVouchers(this, empl);
	}

	public List<GnuCashEmployeeVoucher> getPaidVouchersForEmployee(final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		return FileInvoiceManager_Employee.getPaidVouchers(this, empl);
	}

	public List<GnuCashEmployeeVoucher> getUnpaidVouchersForEmployee(final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		return FileInvoiceManager_Employee.getUnpaidVouchers(this, empl);
	}

	// ----------------------------

	public List<GnuCashJobInvoice> getInvoicesForJob(final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		return FileInvoiceManager_Job.getInvoices(this, job);
	}

	public List<GnuCashJobInvoice> getPaidInvoicesForJob(final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		return FileInvoiceManager_Job.getPaidInvoices(this, job);
	}

	public List<GnuCashJobInvoice> getUnpaidInvoicesForJob(final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		return FileInvoiceManager_Job.getUnpaidInvoices(this, job);
	}

	// ---------------------------------------------------------------

	public int getNofEntriesGenerInvoiceMap() {
		return invcMap.size();
	}

}
