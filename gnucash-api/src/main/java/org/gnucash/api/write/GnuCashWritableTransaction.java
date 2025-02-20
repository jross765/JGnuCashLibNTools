package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAttachment;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * Transaction that can be modified.<br/>
 * For PropertyChange-Listeners we support the properties:
 * "description" and "splits".
 * 
 * @see GnuCashTransaction
 */
public interface GnuCashWritableTransaction extends GnuCashTransaction,
													GnuCashWritableObject,
													HasWritableAttachment,
													HasWritableUserDefinedAttributes
{

    /**
     * @param id the new currency
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnuCashTransaction#getCurrencyID()}
     */
    void setCmdtyCurrID(GCshCmdtyCurrID cmdtyCurrID);

    /**
     * The GnuCash file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnuCashWritableFile getWritableFile();

    /**
     * @param dateEntered the day (time is ignored) that this transaction has been
     *                    entered into the system
     * @see {@link #setDatePosted(LocalDateTime)}
     */
    void setDateEntered(LocalDateTime dateEntered); // sic, not LocalDate

    /**
     * @param datePosted the day (time is ignored) that the money was transfered
     * @see {@link #setDateEntered(LocalDateTime)}
     */
    void setDatePosted(LocalDate datePosted);

    void setDescription(String desc);

    void setNumber(String string);

    /**
     * @return 
     * @throws TransactionSplitNotFoundException 
     *  
     * @see GnuCashTransaction#getFirstSplit()
     */
    GnuCashWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException;

    /**
     * @return 
     * @throws SplitNotFoundException 
     *  
     * @see GnuCashTransaction#getSecondSplit()
     */
    GnuCashWritableTransactionSplit getWritableSecondSplit() throws TransactionSplitNotFoundException;

    /**
     *  
     * @return 
     * @see GnuCashTransaction#getSplitByID(GCshID)
     */
    GnuCashWritableTransactionSplit getWritableSplitByID(GCshID id);

    /**
     *
     * @return the first split of this transaction or null.
     */
    GnuCashWritableTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException;

    /**
     * @return the second split of this transaction or null.
     */
    GnuCashWritableTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException;

    /**
     *  
     * @see GnuCashTransaction#getSplits()
     */
    List<GnuCashWritableTransactionSplit> getWritableSplits();

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     *  
     */
    GnuCashWritableTransactionSplit createWritableSplit(GnuCashAccount account);

    /**
     * Removes the given split from this transaction.
     * 
     * @param impl the split to be removed from this transaction
     *  
     */
    void remove(GnuCashWritableTransactionSplit impl);

    /**
     * remove this transaction.
     *  
     */
    void remove();

}
