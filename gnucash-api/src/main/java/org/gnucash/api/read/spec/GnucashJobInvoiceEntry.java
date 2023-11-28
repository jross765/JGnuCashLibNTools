package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

public interface GnucashJobInvoiceEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getInvoiceID();

  GnucashJobInvoice getInvoice() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

  String getPriceFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;
  
}
