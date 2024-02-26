package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.SplitNotFoundException;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
 * 
 * @see GnucashTransaction
 */
public interface GnucashWritableTransaction extends GnucashTransaction,
													GnucashWritableObject,
													HasWritableUserDefinedAttributes
{

    /**
     * @param id the new currency
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnucashTransaction#getCurrencyID()}
     */
    void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID);

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashWritableFile getWritableFile();

    /**
     * @param dateEntered the day (time is ignored) that this transaction has been
     *                    entered into the system
     * @see {@link #setDatePosted(LocalDateTime)}
     */
    void setDateEntered(final LocalDateTime dateEntered); // sic, not LocalDate

    /**
     * @param datePosted the day (time is ignored) that the money was transfered
     * @see {@link #setDateEntered(LocalDateTime)}
     */
    void setDatePosted(final LocalDate datePosted);

    void setDescription(final String desc);

    void setNumber(String string);

    /**
     * @throws SplitNotFoundException 
     *  
     * @see GnucashTransaction#getFirstSplit()
     */
    GnucashWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException;

    /**
     * @throws SplitNotFoundException 
     * @throws IllegalArgumentException 
     *  
     * @see GnucashTransaction#getSecondSplit()
     */
    GnucashWritableTransactionSplit getWritableSecondSplit() throws SplitNotFoundException;

    /**
     *  
     * @see GnucashTransaction#getSplitByID(GCshID)
     */
    GnucashWritableTransactionSplit getWritableSplitByID(GCshID id);

    /**
     *
     * @return the first split of this transaction or null.
     */
    GnucashWritableTransactionSplit getFirstSplit() throws SplitNotFoundException;

    /**
     * @return the second split of this transaction or null.
     */
    GnucashWritableTransactionSplit getSecondSplit() throws SplitNotFoundException;

    /**
     *  
     * @see GnucashTransaction#getSplits()
     */
    List<GnucashWritableTransactionSplit> getWritableSplits();

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     *  
     */
    GnucashWritableTransactionSplit createWritableSplit(GnucashAccount account);

    /**
     * Also removes the split from it's account.
     * 
     * @param impl the split to remove from this transaction
     *  
     */
    void remove(GnucashWritableTransactionSplit impl);

    /**
     * remove this transaction.
     *  
     */
    void remove();

}
