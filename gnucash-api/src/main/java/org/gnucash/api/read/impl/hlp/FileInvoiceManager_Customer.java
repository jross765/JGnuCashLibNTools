package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);

	// ---------------------------------------------------------------

	public static List<GnuCashCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getInvoices_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getPaidInvoices_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(cust.getID().toString()) ) {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getUnpaidInvoices_viaAllJobs(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

}
