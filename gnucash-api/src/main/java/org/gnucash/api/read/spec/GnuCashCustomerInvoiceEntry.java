package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon;
import org.gnucash.base.basetypes.simple.spec.GCshCustInvcID;

/**
 * One entry (line item) of a {@link GnuCashCustomerInvoice}
 * 
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashCustomerInvoiceEntry extends GnuCashGenerInvoiceEntry,
													 SpecInvoiceEntryCommon
{
	
	GCshCustInvcID getInvoiceID();

	GnuCashCustomerInvoice getInvoice();

}
