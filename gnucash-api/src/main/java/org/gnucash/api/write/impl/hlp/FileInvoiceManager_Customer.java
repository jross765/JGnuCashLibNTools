package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getInvoices_viaAllJobs(final GnuCashCustomer cust) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getPaidInvoices_viaAllJobs(final GnuCashCustomer cust) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getUnpaidInvoices_viaAllJobs(final GnuCashCustomer cust) {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

}
