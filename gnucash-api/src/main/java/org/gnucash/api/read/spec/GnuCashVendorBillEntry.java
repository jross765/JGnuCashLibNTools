package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon;
import org.gnucash.base.basetypes.simple.spec.GCshVendBllID;

/**
 * One entry (line item) of a {@link GnuCashVendorBill}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashJobInvoiceEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashVendorBillEntry extends GnuCashGenerInvoiceEntry,
												SpecInvoiceEntryCommon
{
	
	GCshVendBllID getBillID();

	GnuCashVendorBill getBill();

}
