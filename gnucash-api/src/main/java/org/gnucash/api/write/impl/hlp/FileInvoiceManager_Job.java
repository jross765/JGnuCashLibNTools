package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Job {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Job.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableJobInvoice> getInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws WrongInvoiceTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
				try {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getInvoices: Cannot instantiate GnuCashWritableJobInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws WrongInvoiceTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
				try {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					// This really should not happen, one can almost
					// throw a fatal log here.
					LOGGER.error("getPaidInvoices: Cannot instantiate GnuCashWritableJobInvoiceImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws WrongInvoiceTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
				try {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidInvoices: Cannot instantiate GnuCashWritableJobInvoiceImpl");
				}
			}
		}

		return retval;
	}

}
