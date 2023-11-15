package org.gnucash.read.aux;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashAccount;

public interface GCshTaxTableEntry {

    /**
     * @see ${@link #getType()}
     */
    public enum Type {
	VALUE,
	PERCENT
    }
    
    // ---------------------------------------------------------------

    /**
     * usually ${@link GCshTaxTableEntry#TYPE_PERCENT}.
     * @see ${@link #getAmount())
     */
    Type getType();

    /**
     * @return the amount the tax is ("16" for "16%")
     * @see ${@link #getType()}
     */
    FixedPointNumber getAmount();

    /**
     * @return Returns the accountID.
     * @see ${@link #myAccountID}
     */
    GCshID getAccountID();

    /**
     * @return Returns the account.
     * @see ${@link #myAccount}
     */
    GnucashAccount getAccount();
    
    // ---------------------------------------------------------------

    /**
     * @return Returns the accountID.
     * @see ${@link #myAccountID}
     */
    void setAccountID(final GCshID acctID);

    /**
     * @return Returns the account.
     * @see ${@link #myAccount}
     */
    void setAccount(final GnucashAccount acct);
}
