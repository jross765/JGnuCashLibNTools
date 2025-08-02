package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
			}
		}

		return retval;
	}

	public static List<GnuCashWritableEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
			}
		}

		return retval;
	}

}
