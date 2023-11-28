package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * Invoice-Entry that can be modified.
 */
public interface GnucashWritableGenerInvoiceEntry extends GnucashGenerInvoiceEntry, 
                                                          GnucashWritableObject 
{

    /**
     * @see GnucashGenerInvoiceEntry#getGenerInvoice() .
     */
    GnucashWritableGenerInvoice getGenerInvoice();

    void setDate(final LocalDate date);

    /**
     * Set the description-text.
     *
     * @param desc the new description
     */
    void setDescription(final String desc);

    // -----------------------------------------------------------

    void setInvcPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setInvcPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setInvcPriceFormatted(String price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ------------------------

    void setBillPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setBillPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setBillPriceFormatted(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ------------------------

    void setVoucherPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setVoucherPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setVoucherPriceFormatted(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ------------------------

    void setJobPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setJobPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setJobPriceFormatted(String price)
	    throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // -----------------------------------------------------------

    void setAction(Action act) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setQuantity(String quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setQuantity(FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    void setQuantityFormatted(String n) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // -----------------------------------------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setInvcTaxable(boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @param tax the new taxtable to use. Null sets isTaxable to false.
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    void setInvcTaxTable(GCshTaxTable tax) throws InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, WrongInvoiceTypeException, TaxTableNotFoundException;

    // ------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setBillTaxable(boolean val) throws InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, WrongInvoiceTypeException, TaxTableNotFoundException;

    /**
     * @param tax the new taxtable to use. Null sets isTaxable to false.
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    void setBillTaxTable(GCshTaxTable tax) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // -----------------------------------------------------------

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);
}
