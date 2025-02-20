package org.gnucash.api.write;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.write.aux.GCshWritableAccountLot;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Account that can be modified.
 * 
 * @see GnuCashAccount
 */
public interface GnuCashWritableAccount extends GnuCashAccount, 
                                                GnuCashWritableObject,
                                                HasWritableUserDefinedAttributes
{

    /**
     * @return the file we belong to
     */
    GnuCashWritableFile getWritableGnuCashFile();

    /**
     * Change the user-definable name. It should contain no newlines but may contain
     * non-ascii and non-western characters.
     *
     * @param name the new name (not null)
     */
    void setName(String name);

    /**
     * Change the user-definable account-number. It should contain no newlines but
     * may contain non-ascii and non-western characters.
     *
     * @param code the new code (not null)
     */
    void setAccountCode(String code);

    /**
     * @param desc the user-defined description (may contain multiple lines and
     *             non-ascii-characters)
     */
    void setDescription(String desc);

    /**
     * Get the sum of all transaction-splits affecting this account in the given
     * time-frame.
     *
     * @param from when to start, inclusive
     * @param to   when to stop, exlusive.
     * @return the sum of all transaction-splits affecting this account in the given
     *         time-frame.
     */
    FixedPointNumber getBalanceChange(LocalDate from, LocalDate to);

    /**
     * Set the type of the account (income, ...).
     *
     * @param type the new type.
     * @see {@link GnuCashAccount#getType()}
     */
    void setType(Type type);

    /**
     * @param id the new currency
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnuCashAccount#getCurrencyID()}
     */
    void setCmdtyCurrID(GCshCmdtyCurrID cmdtyCurrID);

    /**
     * @param newparent the new account or null to make it a top-level-account
     */
    void setParentAccount(GnuCashAccount newparent);

    /**
     * If the accountId is invalid, make this a top-level-account.
     *
     * @see {@link #setParentAccount(GnuCashAccount)}
     */
    void setParentAccountID(GCshID newParentID);
    
    /**
     *  
     * @return 
     * @see GnuCashAccount#getLotByID(GCshID)
     */
    GCshWritableAccountLot getWritableLotByID(GCshID id);

    /**
     *  
     * @see GnuCashAccount#getLots()
     */
    List<GCshWritableAccountLot> getWritableLots();

    /**
     * Create a new split, already atached to this transaction.
     * 
     * @param account the account for the new split
     * @return a new split, already atached to this transaction
     *  
     */
    GCshWritableAccountLot createWritableLot();

    /**
     * Removes the given lot from this account.
     * 
     * @param impl the lot to be removed from this account
     *  
     */
    void remove(GCshWritableAccountLot lot);

    /**
     * Remove this account from the system.<br/>
     * Throws IllegalStateException if this account has splits or childres.
     */
    void remove();

}
