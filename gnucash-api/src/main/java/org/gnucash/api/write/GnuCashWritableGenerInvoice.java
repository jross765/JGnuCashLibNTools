package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.WrongOwnerJITypeException;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Cust;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Empl;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Job;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Vend;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAttachment;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Invoice that can be modified.</br>
 * 
 * Note: As opposed to the other "Writable"-classes, there is an additional
 * condition here: the method {@link #isModifiable()} must return true.
 *
 * @see GnuCashGenerInvoice
 */
public interface GnuCashWritableGenerInvoice extends GnuCashGenerInvoice,
                                                     GnuCashWritableGenerInvoice_Cust,
                                                     GnuCashWritableGenerInvoice_Vend,
                                                     GnuCashWritableGenerInvoice_Empl,
                                                     GnuCashWritableGenerInvoice_Job,
                                                     GnuCashWritableObject,
                                                     HasWritableAttachment,
                                                     HasWritableUserDefinedAttributes
{

    /**
     * @return false if already payments have been made or this invoice sent to a
     *         customer!
     */
    boolean isModifiable();

    // -----------------------------------------------------------

    // ::TODO
    // void setOwnerID(String ownerID);

    void setOwner(GCshOwner owner);

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
    GnuCashTransaction getPostTransaction();

    // ------------------------

    /**
     * @param entrID the id to look for
     * @return the modifiable version of the entry
     * @see GnuCashGenerInvoice#getGenerInvcEntryByID(GCshID)
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerEntryByID(GCshID entrID);

    /**
     * remove this invoice from the system.
     * 
* 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void remove() throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // -----------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will have 16% salex-tax and use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
* 
     * @throws TaxTableNotFoundException
     * 
     */
    GnuCashWritableGenerInvoiceEntry createGenerEntry(GnuCashAccount acct, FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity)
	    throws TaxTableNotFoundException;

}
