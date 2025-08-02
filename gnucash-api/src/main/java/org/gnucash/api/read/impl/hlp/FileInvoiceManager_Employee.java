package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);

	// ---------------------------------------------------------------

	public static List<GnuCashEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
			}
		}

		return retval;
	}

	public static List<GnuCashEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr,
			final GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		List<GnuCashEmployeeVoucher> retval = new ArrayList<GnuCashEmployeeVoucher>();

		for ( GnuCashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
			// Important: compare strings, not objects
			if ( invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT).toString()
					.equals(empl.getID().toString()) ) {
					retval.add(new GnuCashEmployeeVoucherImpl(invc));
			}
		}

		return retval;
	}

}
