package org.gnucash.api.write.spec;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Vendor bill entry  that can be modified.
 * 
 * @see GnuCashVendorBillEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntry
 * @see GnuCashWritableEmployeeVoucherEntry
 * @see GnuCashWritableJobInvoiceEntry
 */
public interface GnuCashWritableVendorBillEntry extends GnuCashWritableGenerInvoiceEntry, 
                                                        GnuCashWritableObject 
{

    void setTaxable(boolean val) throws NumberFormatException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setTaxTable(GCshTaxTable taxTab) throws NumberFormatException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws NumberFormatException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setPrice(FixedPointNumber price) throws TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

}
