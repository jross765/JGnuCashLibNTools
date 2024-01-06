package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Cust;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Empl;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Job;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Vend;

/**
 * Invoice-Entry that can be modified.
 */
public interface GnucashWritableGenerInvoiceEntry extends GnucashGenerInvoiceEntry,
                                                          GnucashWritableGenerInvoiceEntry_Cust,
                                                          GnucashWritableGenerInvoiceEntry_Vend,
                                                          GnucashWritableGenerInvoiceEntry_Empl,
                                                          GnucashWritableGenerInvoiceEntry_Job,
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

    // ---------------------------------------------------------------

    void setAction(Action act) throws IllegalArgumentException;

    void setQuantity(String quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setQuantity(FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setQuantityFormatted(String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);
}
