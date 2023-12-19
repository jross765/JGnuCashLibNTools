package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

public interface GnucashVendorBillEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getBillID();

  GnucashVendorBill getBill() throws WrongInvoiceTypeException, IllegalArgumentException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
