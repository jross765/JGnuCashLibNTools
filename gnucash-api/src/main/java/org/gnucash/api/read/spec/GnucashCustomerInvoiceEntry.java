package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnucashCustomerInvoice}
 * 
 *  @see GnucashEmployeeVoucherEntry
 *  @see GnucashVendorBillEntry
 *  @see GnucashJobInvoiceEntry
 *  @see GnucashGenerInvoiceEntry
 */
public interface GnucashCustomerInvoiceEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnucashCustomerInvoice getInvoice() throws WrongInvoiceTypeException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
