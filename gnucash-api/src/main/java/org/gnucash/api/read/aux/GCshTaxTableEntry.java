package org.gnucash.api.read.aux;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;

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
     * @return Returns the accountID.
     * @see ${@link #myAccountID}
     */
    GCshID getAccountID();

    /**
     * @return Returns the account.
     * @see ${@link #myAccount}
     */
    GnucashAccount getAccount();
    
    /**
     * @return the amount the tax is ("16" for "16%")
     * @see ${@link #getType()}
     */
    FixedPointNumber getAmount();

}
