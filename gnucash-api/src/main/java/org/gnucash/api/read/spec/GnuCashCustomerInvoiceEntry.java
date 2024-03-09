package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnuCashCustomerInvoice}
 * 
 *  @see GnuCashEmployeeVoucherEntry
 *  @see GnuCashVendorBillEntry
 *  @see GnuCashJobInvoiceEntry
 *  @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashCustomerInvoiceEntry extends GnuCashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnuCashCustomerInvoice getInvoice();
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice();

  String getPriceFormatted();
  
}
