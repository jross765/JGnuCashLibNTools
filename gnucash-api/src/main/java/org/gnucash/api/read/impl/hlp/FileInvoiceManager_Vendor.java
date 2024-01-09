package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnucashVendorBillImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Vendor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Vendor.class);

	// ---------------------------------------------------------------

	public static Collection<GnucashVendorBill> getBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend) throws WrongInvoiceTypeException {
		Collection<GnucashVendorBill> retval = new ArrayList<GnucashVendorBill>();

		for ( GnucashGenerInvoice invc : invMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					retval.add(new GnucashVendorBillImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getBills_direct: Cannot instantiate GnucashVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashJobInvoice> getBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException {
		Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static Collection<GnucashVendorBill> getPaidBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashVendorBill> retval = new ArrayList<GnucashVendorBill>();

		for ( GnucashGenerInvoice invc : invMgr.getPaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					retval.add(new GnucashVendorBillImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidBills_direct: Cannot instantiate GnucashVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashJobInvoice> getPaidBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

	public static Collection<GnucashVendorBill> getUnpaidBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashVendorBill> retval = new ArrayList<GnucashVendorBill>();

		for ( GnucashGenerInvoice invc : invMgr.getUnpaidGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					retval.add(new GnucashVendorBillImpl(invc));
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidBills_direct: Cannot instantiate GnucashVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashJobInvoice> getUnpaidBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add(jobInvc);
			}
		}

		return retval;
	}

}
