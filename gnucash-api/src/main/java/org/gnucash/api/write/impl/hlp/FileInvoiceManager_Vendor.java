package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Vendor {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Vendor.class);

	// ---------------------------------------------------------------

	public static Collection<GnucashWritableVendorBill> getBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend) throws WrongInvoiceTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		Collection<GnucashWritableVendorBill> retval = new ArrayList<GnucashWritableVendorBill>();

		for ( GnucashGenerInvoice invc : invMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					GnucashWritableVendorBillImpl wrtblInvc = new GnucashWritableVendorBillImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getBills_direct: Cannot instantiate GnucashWritableVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashWritableJobInvoice> getBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static Collection<GnucashWritableVendorBill> getPaidBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		Collection<GnucashWritableVendorBill> retval = new ArrayList<GnucashWritableVendorBill>();

		for ( GnucashWritableGenerInvoice invc : invMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					GnucashWritableVendorBillImpl wrtblInvc = new GnucashWritableVendorBillImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidBills_direct: Cannot instantiate GnucashWritableVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashWritableJobInvoice> getPaidBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getPaidInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

	public static Collection<GnucashWritableVendorBill> getUnpaidBills_direct(final FileInvoiceManager invMgr,
			final GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		Collection<GnucashWritableVendorBill> retval = new ArrayList<GnucashWritableVendorBill>();

		for ( GnucashWritableGenerInvoice invc : invMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getID()) ) {
				try {
					GnucashWritableVendorBillImpl wrtblInvc = new GnucashWritableVendorBillImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblInvc);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidBills_direct: Cannot instantiate GnucashWritableVendorBillImpl");
				}
			}
		}

		return retval;
	}

	public static Collection<GnucashWritableJobInvoice> getUnpaidBills_viaAllJobs(final GnucashVendor vend)
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

		for ( GnucashVendorJob job : vend.getJobs() ) {
			for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
				retval.add((GnucashWritableJobInvoice) jobInvc);
			}
		}

		return retval;
	}

}
