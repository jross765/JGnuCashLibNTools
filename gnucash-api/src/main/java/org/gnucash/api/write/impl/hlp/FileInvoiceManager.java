package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager extends org.gnucash.api.read.impl.hlp.FileInvoiceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager.class);

	// ---------------------------------------------------------------

	public FileInvoiceManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashGenerInvoiceImpl createGenerInvoice(final GncGncInvoice jwsdpInvc) {
		GnuCashWritableGenerInvoiceImpl invc = new GnuCashWritableGenerInvoiceImpl(jwsdpInvc, (GnuCashWritableFileImpl)  gcshFile);
		LOGGER.debug("createGenerInvoice: Generated new writable generic invoice: " + invc.getID());
		return invc;
	}

	// ---------------------------------------------------------------
	// The following two methods are very important: One might think
	// that they are redundant and/or that one could implement them
	// more elegantly by calling the according methods in the super
	// class, but that's not the case, in fact.
	// The most important aspect of their implementation is the instantiation
	// of GnuCashWritableGenerInvoiceImpl, which ensures that the results
	// of the methods isXYZFullyPaid() are actually correct (I have
	// not fully understood in detail why this is so, to be honest, but
	// that how it is.)
	// Cf. comments in GnuCashWritableCustomerImpl.

	public Collection<GnuCashWritableGenerInvoice> getPaidWritableGenerInvoices() {
		Collection<GnuCashWritableGenerInvoice> retval = new ArrayList<GnuCashWritableGenerInvoice>();

		for ( GnuCashGenerInvoice invc : getGenerInvoices() ) {
			// Important: instantiate writable invoice
			// Cf. comment above.
			GnuCashWritableGenerInvoiceImpl wrtblInvc = new GnuCashWritableGenerInvoiceImpl((GnuCashGenerInvoiceImpl) invc);
			if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
					if ( wrtblInvc.isCustInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
					if ( wrtblInvc.isVendBllFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
					if ( wrtblInvc.isEmplVchFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
					if ( wrtblInvc.isJobInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			}
		}

		return retval;
	}

	public Collection<GnuCashWritableGenerInvoice> getUnpaidWritableGenerInvoices() {
		Collection<GnuCashWritableGenerInvoice> retval = new ArrayList<GnuCashWritableGenerInvoice>();

		for ( GnuCashGenerInvoice invc : getGenerInvoices() ) {
			// Important: instantiate writable invoice
			// Cf. comments above.
			GnuCashWritableGenerInvoiceImpl wrtblInvc = new GnuCashWritableGenerInvoiceImpl((GnuCashGenerInvoiceImpl) invc);
			if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
					if ( wrtblInvc.isNotCustInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
					if ( wrtblInvc.isNotVendBllFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
					if ( wrtblInvc.isNotEmplVchFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			} else if ( wrtblInvc.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
					if ( wrtblInvc.isNotJobInvcFullyPaid() ) {
						retval.add(wrtblInvc);
					}
			}
		}

		return retval;
	}

	// ----------------------------

	public List<GnuCashWritableCustomerInvoice> getWritableInvoicesForCustomer_direct(final GnuCashCustomer cust)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getInvoices_direct(this, cust);
	}

	public List<GnuCashWritableJobInvoice> getWritableInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		return FileInvoiceManager_Customer.getInvoices_viaAllJobs(cust);
	}

	public List<GnuCashWritableCustomerInvoice> getPaidWritableInvoicesForCustomer_direct(
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getPaidInvoices_direct(this, cust);
	}

	public List<GnuCashWritableJobInvoice> getPaidWritableInvoicesForCustomer_viaAllJobs(
			final GnuCashCustomer cust) {
		return FileInvoiceManager_Customer.getPaidInvoices_viaAllJobs(cust);
	}

	public List<GnuCashWritableCustomerInvoice> getUnpaidWritableInvoicesForCustomer_direct(
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		return FileInvoiceManager_Customer.getUnpaidInvoices_direct(this, cust);
	}

	public List<GnuCashWritableJobInvoice> getUnpaidWritableInvoicesForCustomer_viaAllJobs(
			final GnuCashCustomer cust) {
		return FileInvoiceManager_Customer.getUnpaidInvoices_viaAllJobs(cust);
	}

	// ----------------------------

	public List<GnuCashWritableVendorBill> getWritableBillsForVendor_direct(final GnuCashVendor vend)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getBills_direct(this, vend);
	}

	public List<GnuCashWritableJobInvoice> getWritableBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return FileInvoiceManager_Vendor.getBills_viaAllJobs(vend);
	}

	public List<GnuCashWritableVendorBill> getPaidWritableBillsForVendor_direct(final GnuCashVendor vend)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getPaidBills_direct(this, vend);
	}

	public List<GnuCashWritableJobInvoice> getPaidWritableBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return FileInvoiceManager_Vendor.getPaidBills_viaAllJobs(vend);
	}

	public List<GnuCashWritableVendorBill> getUnpaidWritableBillsForVendor_direct(final GnuCashVendor vend)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Vendor.getUnpaidBills_direct(this, vend);
	}

	public List<GnuCashWritableJobInvoice> getUnpaidWritableBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return FileInvoiceManager_Vendor.getUnpaidBills_viaAllJobs(vend);
	}

	// ----------------------------

	public List<GnuCashWritableEmployeeVoucher> getWritableVouchersForEmployee(final GnuCashEmployee empl)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getVouchers(this, empl);
	}

	public List<GnuCashWritableEmployeeVoucher> getPaidWritableVouchersForEmployee(final GnuCashEmployee empl)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getPaidVouchers(this, empl);
	}

	public List<GnuCashWritableEmployeeVoucher> getUnpaidWritableVouchersForEmployee(final GnuCashEmployee empl)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Employee.getUnpaidVouchers(this, empl);
	}

	// ----------------------------

	public List<GnuCashWritableJobInvoice> getWritableInvoicesForJob(final GnuCashGenerJob job)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Job.getInvoices(this, job);
	}

	public List<GnuCashWritableJobInvoice> getPaidWritableInvoicesForJob(final GnuCashGenerJob job)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Job.getPaidInvoices(this, job);
	}

	public List<GnuCashWritableJobInvoice> getUnpaidWritableInvoicesForJob(final GnuCashGenerJob job)
			throws TaxTableNotFoundException {
		return FileInvoiceManager_Job.getUnpaidInvoices(this, job);
	}

}
