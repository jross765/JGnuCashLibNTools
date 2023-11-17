package org.gnucash.read.spec;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashGenerInvoiceEntry;

public interface GnucashEmployeeVoucherEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getVoucherID();

  GnucashEmployeeVoucher getVoucher() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}