package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.WrongOwnerJITypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoice_Cust;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoice_Empl;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoice_Job;
import org.gnucash.api.write.hlp.GnucashWritableGenerInvoice_Vend;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Invoice that can be modified.</br>
 * 
 * Note: As opposed to the other "Writable"-classes, there is an additional
 * condition here: the method {@link #isModifiable()} must return true.
 *
 * @see GnucashGenerInvoice
 */
public interface GnucashWritableGenerInvoice extends GnucashGenerInvoice,
                                                     GnucashWritableGenerInvoice_Cust,
                                                     GnucashWritableGenerInvoice_Vend,
                                                     GnucashWritableGenerInvoice_Empl,
                                                     GnucashWritableGenerInvoice_Job,
                                                     GnucashWritableObject,
                                                     HasWritableUserDefinedAttributes
{

    /**
     * The gnucash-file is tohe top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    GnucashWritableFile getFile();

    // -----------------------------------------------------------

    /**
     * @return false if already payments have been made or this invoice sent to a
     *         customer!
     */
    boolean isModifiable();

    // -----------------------------------------------------------

    // ::TODO
    // void setOwnerID(String ownerID);

    void setOwner(GCshOwner owner) throws WrongOwnerJITypeException;

    // -----------------------------------------------------------

    void setDatePosted(LocalDate d);

    void setDatePosted(String d) throws java.text.ParseException;

    void setDateOpened(LocalDate d);

    void setDateOpened(String d) throws java.text.ParseException;

    // -----------------------------------------------------------

    void setNumber(String number);

    void setDescription(String descr);

    // -----------------------------------------------------------

    /**
     * @return the transaction that adds this invoice's sum to the expected money.
     */
    GnucashTransaction getPostTransaction();

    // ------------------------

    /**
     * @param entrID the id to look for
     * @return the modifiable version of the entry
     * @see GnucashGenerInvoice#getGenerInvcEntryByID(GCshID)
     */
    GnucashWritableGenerInvoiceEntry getWritableGenerEntryByID(GCshID entrID);

    /**
     * remove this invoice from the system.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * 
     *
     */
    void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    // -----------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will have 16% salex-tax and use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * 
     */
    GnucashWritableGenerInvoiceEntry createGenerEntry(GnucashAccount acct, FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException;

}
