package org.gnucash.api.write.spec;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Customer invoice entry that can be modified.
 * 
 * @see GnuCashCustomerInvoiceEntry
 * 
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableCustomerInvoiceEntry extends GnuCashWritableGenerInvoiceEntry,
                                                             GnuCashCustomerInvoiceEntry,
                                                             GnuCashWritableObject 
{

    void setTaxable(boolean val) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
