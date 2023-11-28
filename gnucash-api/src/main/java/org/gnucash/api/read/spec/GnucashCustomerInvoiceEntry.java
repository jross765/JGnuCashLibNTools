package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

public interface GnucashCustomerInvoiceEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnucashCustomerInvoice getInvoice() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}