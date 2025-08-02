package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Vendor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Vendor.class);

	// ---------------------------------------------------------------

	public static List<GnuCashVendorBill> getBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashVendorBill> retval = new ArrayList<GnuCashVendorBill>();

		for ( GnuCashGenerInvoice invc : invMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					retval.add(new GnuCashVendorBillImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getBills_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashVendorBill> getPaidBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashVendorBill> retval = new ArrayList<GnuCashVendorBill>();

		for ( GnuCashGenerInvoice invc : invMgr.getPaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					retval.add(new GnuCashVendorBillImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getPaidBills_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static List<GnuCashVendorBill> getUnpaidBills_direct(final FileInvoiceManager invMgr,
			final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashVendorBill> retval = new ArrayList<GnuCashVendorBill>();

		for ( GnuCashGenerInvoice invc : invMgr.getUnpaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(vend.getID().toString()) ) {
					retval.add(new GnuCashVendorBillImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashJobInvoice> getUnpaidBills_viaAllJobs(final GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
		}
		
		List<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashVendorJob job : vend.getJobs() ) {
			for ( GnuCashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

}
