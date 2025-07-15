package org.gnucash.api.write.aux;

import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.aux.GCshTaxTableEntry;

public interface GCshWritableTaxTableEntry extends GCshTaxTableEntry {

	/**
	 * 
	 * @param type
	 * 
	 * @see #getType()
	 * @see #setTypeStr(String)
	 */
    void setType(Type type);

    /**
     * 
     * @param typeStr
     * 
     * @see #setType(org.gnucash.api.read.aux.GCshTaxTableEntry.Type)
     */
    void setTypeStr(String typeStr);

    /**
     * 
     * @param acctID
     * 
     * @see #getAccountID()
     * @see #setAccount(GnuCashAccount)
     */
    void setAccountID(GCshID acctID);

    /**
     * 
     * @param acct
     * 
     * @see #getAccount()
     * @see #setAccountID(GCshID)
     */
    void setAccount(GnuCashAccount acct);

    /**
     * 
     * @param amt
     * 
     * @see #getAmount()
     */
    void setAmount(FixedPointNumber amt);
}
