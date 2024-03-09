package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnuCashJobInvoice}
 * 
 *  @see GnuCashCustomerInvoiceEntry
 *  @see GnuCashEmployeeVoucherEntry
 *  @see GnuCashVendorBillEntry
 *  @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashJobInvoiceEntry extends GnuCashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnuCashJobInvoice getInvoice();
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice();

  String getPriceFormatted();
  
}
