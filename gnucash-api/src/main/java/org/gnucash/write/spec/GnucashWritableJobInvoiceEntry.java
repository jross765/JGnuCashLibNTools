package org.gnucash.write.spec;

import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.IllegalTransactionSplitActionException;
import org.gnucash.read.TaxTableNotFoundException;
import org.gnucash.read.aux.GCshTaxTable;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.gnucash.write.GnucashWritableGenerInvoiceEntry;
import org.gnucash.write.GnucashWritableObject;
import org.gnucash.write.impl.UnknownInvoiceTypeException;

/**
 * Invoice-Entry that can be modified.
 */
public interface GnucashWritableJobInvoiceEntry extends GnucashWritableGenerInvoiceEntry, 
                                                        GnucashWritableObject 
{

    void setTaxable(boolean val) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setTaxTable(GCshTaxTable taxTab) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ---------------------------------------------------------------

    void setPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

}
