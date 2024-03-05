package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);

	// ---------------------------------------------------------------

	public static List<GnuCashCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws WrongInvoiceTypeException {
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getInvoices_direct: Cannot instantiate GnuCashCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException {
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidInvoices_direct: Cannot instantiate GnuCashCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getPaidInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnuCashCustomerInvoice> retval = new ArrayList<GnuCashCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					retval.add(new GnuCashCustomerInvoiceImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidInvoices_direct: Cannot instantiate GnuCashCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getUnpaidInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

}
