package org.gnucash.api.write;

import java.time.LocalDate;

import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Account that can be modified.
 * 
 * @see GnucashAccount
 */
public interface GnucashWritableAccount extends GnucashAccount, 
                                                GnucashWritableObject,
                                                HasWritableUserDefinedAttributes
{

    /**
     * @return the file we belong to
     */
    GnucashWritableFile getWritableGnucashFile();

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
     * @throws UnknownAccountTypeException 
     * @see {@link GnucashAccount#getType()}
     */
    void setType(Type type) throws UnknownAccountTypeException;

    /**
     * @param id the new currency
     * @throws InvalidCmdtyCurrTypeException 
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnucashAccount#getCurrencyID()}
     */
    void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException;

    /**
     * @param newparent the new account or null to make it a top-level-account
     */
    void setParentAccount(GnucashAccount newparent);

    /**
     * If the accountId is invalid, make this a top-level-account.
     *
     * @see {@link #setParentAccount(GnucashAccount)}
     */
    void setParentAccountID(GCshID newParentID);

    /**
     * Remove this account from the sytem.<br/>
     * Throws IllegalStateException if this account has splits or childres.
     */
    void remove();

}
