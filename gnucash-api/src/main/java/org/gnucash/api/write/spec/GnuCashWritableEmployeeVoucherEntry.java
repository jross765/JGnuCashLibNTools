package org.gnucash.api.write.spec;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Employee voucher entry that can be modified.
 * 
 * @see GnuCashEmployeeVoucherEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableVendorBillEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableEmployeeVoucherEntry extends GnuCashWritableGenerInvoiceEntry, 
															 GnuCashEmployeeVoucherEntry,
                                                             GnuCashWritableObject 
{

    void setTaxable(boolean val) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

}
