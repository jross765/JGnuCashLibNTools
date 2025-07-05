package org.gnucash.api.read.aux;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshTaxTableEntry {

    public enum Type {
	VALUE,
	PERCENT
    }
    
    // ---------------------------------------------------------------

    /*
     * usually PERCENT.
     */
    Type getType();

    /**
     * @return Returns the accountID.
     */
    GCshID getAccountID();

    /**
     * @return Returns the account.
     */
    GnuCashAccount getAccount();
    
    /**
     * @return the amount the tax is ("16" for "16%")
     */
    FixedPointNumber getAmount();

}
