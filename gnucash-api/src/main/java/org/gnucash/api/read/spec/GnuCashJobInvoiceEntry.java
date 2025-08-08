package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.spec.hlp.SpecInvoiceEntryCommon;
import org.gnucash.base.basetypes.simple.spec.GCshJobInvcID;

/**
 * One entry (line item) of a {@link GnuCashJobInvoice}
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashJobInvoiceEntry extends GnuCashGenerInvoiceEntry,
												SpecInvoiceEntryCommon
{
	
	GCshJobInvcID getInvoiceID();

	GnuCashJobInvoice getInvoice();

}
