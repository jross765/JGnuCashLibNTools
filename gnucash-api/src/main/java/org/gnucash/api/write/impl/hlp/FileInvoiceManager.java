package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager extends org.gnucash.api.read.impl.hlp.FileInvoiceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager.class);

	// ---------------------------------------------------------------

	public FileInvoiceManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashGenerInvoiceImpl createGenerInvoice(final GncGncInvoice jwsdpInvc) {
		GnucashWritableGenerInvoiceImpl invc = new GnucashWritableGenerInvoiceImpl(jwsdpInvc, gcshFile);
		LOGGER.debug("Generated new writable generic invoice: " + invc.getID());
		return invc;
	}

	// ---------------------------------------------------------------
	// The following two methods are very important: One might think
	// that they are redundant and/or that one could implement them
	// more elegantly by calling the according methods in the super
	// class, but that's not the case, in fact.
	// The most important aspect of their implementation is the instantiation
	// of GnucashWritableGenerInvoiceImpl, which ensures that the results
	// of the methods isXYZFullyPaid() are actually correct (I have
	// not fully understood in detail why this is so, to be honest, but
	// that how it is.)
	// Cf. comments in GnucashWritableCustomerImpl.

	public Collection<GnucashWritableGenerInvoice> getPaidWritableGenerInvoices()
			throws UnknownAccountTypeException, IllegalArgumentException {
		Collection<GnucashWritableGenerInvoice> retval = new ArrayList<GnucashWritableGenerInvoice>();

		for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
			// Important: instantiate writable invoice
			// Cf. comment above.
			GnucashWritableGenerInvoiceImpl wrtblInvc = new GnucashWritableGenerInvoiceImpl((GnucashGenerInvoiceImpl) invc);
			if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
				try {
					if ( wrtblInvc.isCustInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
				try {
					if ( wrtblInvc.isVendBllFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
				try {
					if ( wrtblInvc.isEmplVchFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_JOB ) {
				try {
					if ( wrtblInvc.isJobInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getPaidWritableGenerInvoices: Serious error");
				}
			}
		}

		return retval;
	}

	public Collection<GnucashWritableGenerInvoice> getUnpaidWritableGenerInvoices()
			throws UnknownAccountTypeException, IllegalArgumentException {
		Collection<GnucashWritableGenerInvoice> retval = new ArrayList<GnucashWritableGenerInvoice>();

		for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
			// Important: instantiate writable invoice
			// Cf. comments above.
			GnucashWritableGenerInvoiceImpl wrtblInvc = new GnucashWritableGenerInvoiceImpl((GnucashGenerInvoiceImpl) invc);
			if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
				try {
					if ( wrtblInvc.isNotCustInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
				try {
					if ( wrtblInvc.isNotVendBllFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
				try {
					if ( wrtblInvc.isNotEmplVchFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidWritableGenerInvoices: Serious error");
				}
			} else if ( wrtblInvc.getType() == GnucashGenerInvoice.TYPE_JOB ) {
				try {
					if ( wrtblInvc.isNotInvcJobFullyPaid() ) {
						retval.add(wrtblInvc);
					}
				} catch (WrongInvoiceTypeException e) {
					// This should not happen
					LOGGER.error("getUnpaidWritableGenerInvoices: Serious error");
				}
			}
		}

		return retval;
	}

	// ----------------------------

	public Collection<GnucashWritableCustomerInvoice> getWritableInvoicesForCustomer_direct(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException,
			TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getInvoices_direct(this, cust);
	}

	public Collection<GnucashWritableJobInvoice> getWritableInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		return FileInvoiceManager_Customer.getInvoices_viaAllJobs(cust);
	}

	public Collection<GnucashWritableCustomerInvoice> getPaidWritableInvoicesForCustomer_direct(
			final GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getPaidInvoices_direct(this, cust);
	}

	public Collection<GnucashWritableJobInvoice> getPaidWritableInvoicesForCustomer_viaAllJobs(
			final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return FileInvoiceManager_Customer.getPaidInvoices_viaAllJobs(cust);
	}

	public Collection<GnucashWritableCustomerInvoice> getUnpaidWritableInvoicesForCustomer_direct(
			final GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getUnpaidInvoices_direct(this, cust);
	}

	public Collection<GnucashWritableJobInvoice> getUnpaidWritableInvoicesForCustomer_viaAllJobs(
			final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return FileInvoiceManager_Customer.getUnpaidInvoices_viaAllJobs(cust);
	}

	// ----------------------------

	public Collection<GnucashWritableVendorBill> getWritableBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException,
			TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getBills_direct(this, vend);
	}

	public Collection<GnucashWritableJobInvoice> getWritableBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		return FileInvoiceManager_Vendor.getBills_viaAllJobs(vend);
	}

	public Collection<GnucashWritableVendorBill> getPaidWritableBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getPaidBills_direct(this, vend);
	}

	public Collection<GnucashWritableJobInvoice> getPaidWritableBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return FileInvoiceManager_Vendor.getPaidBills_viaAllJobs(vend);
	}

	public Collection<GnucashWritableVendorBill> getUnpaidWritableBillsForVendor_direct(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getUnpaidBills_direct(this, vend);
	}

	public Collection<GnucashWritableJobInvoice> getUnpaidWritableBillsForVendor_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return FileInvoiceManager_Vendor.getUnpaidBills_viaAllJobs(vend);
	}

	// ----------------------------

	public Collection<GnucashWritableEmployeeVoucher> getWritableVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException,
			TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getVouchers(this, empl);
	}

	public Collection<GnucashWritableEmployeeVoucher> getPaidWritableVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getPaidVouchers(this, empl);
	}

	public Collection<GnucashWritableEmployeeVoucher> getUnpaidWritableVouchersForEmployee(final GnucashEmployee empl)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getUnpaidVouchers(this, empl);
	}

	// ----------------------------

	public Collection<GnucashWritableJobInvoice> getWritableInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException,
			TaxTableNotFoundException {
		return FileInvoiceManager_Job.getInvoices(this, job);
	}

	public Collection<GnucashWritableJobInvoice> getPaidWritableInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Job.getPaidInvoices(this, job);
	}

	public Collection<GnucashWritableJobInvoice> getUnpaidWritableInvoicesForJob(final GnucashGenerJob job)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return FileInvoiceManager_Job.getUnpaidInvoices(this, job);
	}

}
