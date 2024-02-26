package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static List<GnucashWritableEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl) throws WrongInvoiceTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableEmployeeVoucher> retval = new ArrayList<GnucashWritableEmployeeVoucher>();

		for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnucashWritableEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableEmployeeVoucher> retval = new ArrayList<GnucashWritableEmployeeVoucher>();

		for ( GnucashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getPaidVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

	public static List<GnucashWritableEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnucashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException,
			IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		List<GnucashWritableEmployeeVoucher> retval = new ArrayList<GnucashWritableEmployeeVoucher>();

		for ( GnucashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
			if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
				try {
					GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
					retval.add(wrtblVch);
				} catch (WrongInvoiceTypeException e) {
					LOGGER.error("getUnpaidVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
				}
			}
		}

		return retval;
	}

}
