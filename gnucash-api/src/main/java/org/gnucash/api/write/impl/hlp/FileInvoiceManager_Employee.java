package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static List<GnuCashWritableEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getVouchers: Cannot instantiate GnuCashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidVouchers: Cannot instantiate GnuCashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnuCashWritableEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) throws InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnuCashWritableEmployeeVoucher> retval = new ArrayList<GnuCashWritableEmployeeVoucher>();

		for ( GnuCashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnuCashWritableEmployeeVoucherImpl wrtblVch = new GnuCashWritableEmployeeVoucherImpl((GnuCashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidVouchers: Cannot instantiate GnuCashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

}
