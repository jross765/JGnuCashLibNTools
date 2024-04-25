package org.gnucash.api.write.spec;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnuCashJobInvoiceEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 */
public interface GnuCashWritableJobInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
														GnuCashJobInvoiceEntry,
                                                        GnuCashWritableObject 
{

    void setTaxable(boolean val) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

}
