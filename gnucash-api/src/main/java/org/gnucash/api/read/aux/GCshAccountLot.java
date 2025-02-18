package org.gnucash.api.read.aux;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;

/**
 * ::TOOD
 */
public interface GCshAccountLot extends GnuCashObject
{
    // -----------------------------------------------------------------
    
    public static String SEPARATOR = ":";

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    GncAccount.ActLots.GncLot getJwsdpPeer();

    // -----------------------------------------------------------------

    /**
     * @return the unique id for that account (not meaningfull to human users)
     */
    GCshID getID();

    /**
     * @return user-readable title of this account lot 
     * (usually pre-defined by GnuCash, but can be changed by user).
     */
    String getTitle();

    /**
     * @return user-defined name of this account-lot.
     */
    String getNotes();

    // -----------------------------------------------------------------

    /**
     * @return null if the account is below the root
     */
    GCshID getAccountID();
    
    /**
     * @return the account this lot belongs to.
     */
    GnuCashAccount getAccount();

    // -----------------------------------------------------------------

    /**
     * The returned list ist sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     * @throws GCshIDNotSetException 
     */
    List<GnuCashTransactionSplit> getTransactionSplits() throws GCshIDNotSetException;

    /**
     * Gets the list of transactions-split before (or at) the given date.
     */
    List<GnuCashTransactionSplit> getSplitsBefore(LocalDate date);

    /**
     * Gets the last transaction-split before the given date.
     */
    List<GnuCashTransactionSplit> getSplitsAfterBefore(LocalDate fromDate, LocalDate toDate);

    /**
     * @param split split to add to this transaction
     */
    void addTransactionSplit(final GnuCashTransactionSplit split);

    // ----------------------------

    /**
     * @return true if ${@link #getTransactionSplits()}.size() &gt; 0
     */
    boolean hasTransactions();

    /**
     * The returned list ist sorted by the natural order of the Transaction-Splits.
     *
     * @return all splits
     */
    List<GnuCashTransaction> getTransactions();

    List<GnuCashTransaction> getTransactions(LocalDate fromDate, LocalDate toDate);

}
