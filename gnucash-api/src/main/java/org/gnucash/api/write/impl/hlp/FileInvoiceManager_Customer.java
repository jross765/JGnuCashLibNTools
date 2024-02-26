package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);

	// ---------------------------------------------------------------

	public static List<GnucashWritableCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr,
			final GnucashCustomer cust) throws WrongInvoiceTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableCustomerInvoice> retval = new ArrayList<GnucashWritableCustomerInvoice>();

		for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnucashWritableJobInvoice> getInvoices_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException {
		List<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashCustomerJob job : cust.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnucashWritableCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableCustomerInvoice> retval = new ArrayList<GnucashWritableCustomerInvoice>();

		for ( GnucashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnucashWritableJobInvoice> getPaidInvoices_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashCustomerJob job : cust.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static List<GnucashWritableCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr,
			final GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableCustomerInvoice> retval = new ArrayList<GnucashWritableCustomerInvoice>();

		for ( GnucashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
				try {
					GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnucashWritableJobInvoice> getUnpaidInvoices_viaAllJobs(final GnucashCustomer cust)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashCustomerJob job : cust.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

}
