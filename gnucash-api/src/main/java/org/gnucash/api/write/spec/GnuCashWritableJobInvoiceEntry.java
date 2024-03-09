package org.gnucash.api.write.spec;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

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
                                                        GnuCashWritableObject 
{

    void setTaxable(boolean val) throws NumberFormatException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setTaxTable(GCshTaxTable taxTab) throws NumberFormatException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws NumberFormatException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

}
