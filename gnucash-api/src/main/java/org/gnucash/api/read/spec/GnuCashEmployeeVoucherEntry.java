package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * One entry (line item) of a {@link GnuCashEmployeeVoucher}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashEmployeeVoucherEntry extends GnuCashGenerInvoiceEntry,
													 SpecInvoiceEntryCommon
{
	
	GCshID getVoucherID();

	GnuCashEmployeeVoucher getVoucher();

}
