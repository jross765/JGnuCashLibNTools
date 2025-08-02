package org.gnucash.api.write;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Cust;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Empl;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Job;
import org.gnucash.api.write.hlp.GnuCashWritableGenerInvoice_Vend;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAttachment;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

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

    /**
     * 
     * @param ownID
     * 
     * @see #getOwnerID(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    void setOwnerID(GCshID ownID);

    /**
     * 
     * @param own
     * 
     * @see #getOwner()
     */
    void setOwner(GCshOwner own);

    // -----------------------------------------------------------

    /**
     * 
     * @param ddat
     * 
     * @see #getDatePosted()
     * @see #setDatePosted(String)
     */
    void setDatePosted(LocalDate ddat);

    /**
     * 
     * @param dat
     * @throws java.text.ParseException
     * 
     * @see #getDatePosted()
     * @see #setDatePosted(LocalDate)
     */
    void setDatePosted(String dat) throws java.text.ParseException;

    /**
     * 
     * @param dat
     * 
     * @see #getDateOpened()
     * @see #setDateOpened(String)
     */
    void setDateOpened(LocalDate dat);

    /**
     * 
     * @param dat
     * @throws java.text.ParseException
     * 
     * @see #getDateOpened()
     * @see #setDateOpened(LocalDate)
     */
    void setDateOpened(String dat) throws java.text.ParseException;

    // -----------------------------------------------------------

    /**
     * 
     * @param number
     * 
     * @see #getNumber()
     */
    void setNumber(String number);

    /**
     * 
     * @param descr
     * 
     * @see #getDescription()
     */
    void setDescription(String descr);

    // -----------------------------------------------------------

    /**
     * @return the transaction that adds this invoice's sum to the expected money.
     */
    GnuCashTransaction getPostTransaction();

    // -----------------------------------------------------------

    /**
     * 
     * @return
     * 
     * @see #getGenerEntries()
     */
    List<GnuCashWritableGenerInvoiceEntry> getWritableGenerEntries();

    /**
     * @param entrID the id to look for
     * @return the modifiable version of the entry
     * @see #getGenerEntryByID(GCshGenerInvcEntrID)
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerEntryByID(GCshGenerInvcEntrID entrID);

    /**
     * Deletes this invoice.
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void remove() throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * remove this invoice from the system.
     * @param withEntries 
     * 
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void remove(boolean withEntries) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // -----------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will have 16% salex-tax and use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * 
     */
    GnuCashWritableGenerInvoiceEntry createGenerEntry(GnuCashAccount acct, FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity)
	    throws TaxTableNotFoundException;

}
