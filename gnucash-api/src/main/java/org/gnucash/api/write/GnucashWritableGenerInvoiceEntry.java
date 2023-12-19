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

    void setInvcPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setInvcPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setInvcPriceFormatted(String price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ------------------------

    void setBillPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setBillPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setBillPriceFormatted(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ------------------------

    void setVoucherPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setVoucherPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setVoucherPriceFormatted(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ------------------------

    void setJobPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setJobPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setJobPriceFormatted(String price)
	    throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // -----------------------------------------------------------

    void setAction(Action act) throws IllegalArgumentException;

    void setQuantity(String quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setQuantity(FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setQuantityFormatted(String n) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // -----------------------------------------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setInvcTaxable(boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    /**
     * @param tax the new taxtable to use. Null sets isTaxable to false.
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    void setInvcTaxTable(GCshTaxTable tax) throws InvalidCmdtyCurrTypeException, IllegalArgumentException, WrongInvoiceTypeException, TaxTableNotFoundException;

    // ------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setBillTaxable(boolean val) throws InvalidCmdtyCurrTypeException, IllegalArgumentException, WrongInvoiceTypeException, TaxTableNotFoundException;

    /**
     * @param tax the new taxtable to use. Null sets isTaxable to false.
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    void setBillTaxTable(GCshTaxTable tax) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // -----------------------------------------------------------

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);
}
