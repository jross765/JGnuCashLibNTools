package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnucashJobInvoice}
 * 
 *  @see GnucashCustomerInvoiceEntry
 *  @see GnucashEmployeeVoucherEntry
 *  @see GnucashVendorBillEntry
 *  @see GnucashGenerInvoiceEntry
 */
public interface GnucashJobInvoiceEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnucashJobInvoice getInvoice() throws WrongInvoiceTypeException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
