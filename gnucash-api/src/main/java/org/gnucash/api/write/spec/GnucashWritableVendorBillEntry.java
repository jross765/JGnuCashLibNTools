package org.gnucash.api.write.spec;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnucashWritableObject;

/**
 * Vendor bill entry  that can be modified.
 */
public interface GnucashWritableVendorBillEntry extends GnucashWritableGenerInvoiceEntry, 
                                                        GnucashWritableObject 
{

    void setTaxable(boolean val) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setTaxTable(GCshTaxTable taxTab) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

}
