package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Job {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Job.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableJobInvoice> getInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(job.getID().toString()) ) {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(job.getID().toString()) ) {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) throws TaxTableNotFoundException {
		List<GnuCashWritableJobInvoice> retval = new ArrayList<GnuCashWritableJobInvoice>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(job.getID().toString()) ) {
					GnuCashWritableJobInvoiceImpl wrtblInvc = new GnuCashWritableJobInvoiceImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
			}
		}

		return retval;
	}

}
