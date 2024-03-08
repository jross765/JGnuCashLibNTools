package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws WrongInvoiceTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getInvoices_direct: Cannot instantiate GnuCashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws WrongInvoiceTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidInvoices_direct: Cannot instantiate GnuCashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getPaidInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnuCashCustomer cust) throws WrongInvoiceTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableCustomerInvoice> retval = new ArrayList<GnuCashWritableCustomerInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnuCashWritableCustomerInvoiceImpl wrtblInvc = new GnuCashWritableCustomerInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidInvoices_direct: Cannot instantiate GnuCashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getUnpaidInvoices_viaAllJobs(final GnuCashCustomer cust)
			throws WrongInvoiceTypeException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashCustomerJob job : cust.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add((GnuCashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

}
