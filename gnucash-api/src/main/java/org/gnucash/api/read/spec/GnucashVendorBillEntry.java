package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnucashVendorBill}
 * 
 *  @see GnucashCustomerInvoiceEntry
 *  @see GnucashEmployeeVoucherEntry
 *  @see GnucashJobInvoiceEntry
 *  @see GnucashGenerInvoiceEntry
 */
public interface GnucashVendorBillEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getBillID();

  GnucashVendorBill getBill() throws WrongInvoiceTypeException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
