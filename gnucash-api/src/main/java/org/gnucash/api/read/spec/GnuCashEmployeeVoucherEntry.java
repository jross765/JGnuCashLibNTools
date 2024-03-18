package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;

/**
 * One entry (line item) of a {@link GnuCashEmployeeVoucher}
 * 
 *  @see GnuCashCustomerInvoiceEntry
 *  @see GnuCashVendorBillEntry
 *  @see GnuCashJobInvoiceEntry
 *  @see GnuCashGenerInvoiceEntry
 */
public interface GnuCashEmployeeVoucherEntry extends GnuCashGenerInvoiceEntry 
{
  GCshID getVoucherID();

  GnuCashEmployeeVoucher getVoucher();
  
  // -----------------------------------------------------------------

  GCshID getAccountID() throws AccountNotFoundException;

  GnuCashAccount getAccount() throws AccountNotFoundException;
  
  // -----------------------------------------------------------------

  FixedPointNumber getPrice();

  String getPriceFormatted();
  
}
