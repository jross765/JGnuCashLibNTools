package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Cust;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Empl;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Job;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoiceEntry_Vend;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Invoice-Entry that can be modified.
 * 
 * @see GnucashGenerInvoiceEntry
 */
public interface GnucashWritableGenerInvoiceEntry extends GnucashGenerInvoiceEntry,
                                                          GnucashWritableGenerInvoiceEntry_Cust,
                                                          GnucashWritableGenerInvoiceEntry_Vend,
                                                          GnucashWritableGenerInvoiceEntry_Empl,
                                                          GnucashWritableGenerInvoiceEntry_Job,
                                                          GnucashWritableObject,
                                                          HasWritableUserDefinedAttributes
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

    void setAction(Action act);

    void setQuantity(String quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    void setQuantity(FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    void setQuantityFormatted(String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * 
     */
    void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

}
