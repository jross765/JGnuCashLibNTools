package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Job {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Job.class);

	// ---------------------------------------------------------------

	public static List<GnuCashJobInvoice> getInvoices(final FileInvoiceManager invcMgr, final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
					retval.add(new GnuCashJobInvoiceImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
					retval.add(new GnuCashJobInvoiceImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr,
			final GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
					retval.add(new GnuCashJobInvoiceImpl(invc));
			}
		}

		return retval;
	}

}
