package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnucashEmployeeVoucher}
 * 
 *  @see GnucashCustomerInvoiceEntry
 *  @see GnucashVendorBillEntry
 *  @see GnucashJobInvoiceEntry
 *  @see GnucashGenerInvoiceEntry
 */
public interface GnucashEmployeeVoucherEntry extends GnucashGenerInvoiceEntry 
{
  GCshID getVoucherID();

  GnucashEmployeeVoucher getVoucher() throws WrongInvoiceTypeException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice() throws WrongInvoiceTypeException;

  String getPriceFormatted() throws WrongInvoiceTypeException;
  
}
