package org.gnucash.api.write.aux;

import java.util.List;

import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

public interface GCshWritableAccountLot extends GCshAcctLot, 
                                                GnuCashWritableObject
{

	/**
	 * Remove this lot from the account.
	 *  
	 */
	void remove();

    // -----------------------------------------------------------------

	/**
	 * 
	 * @param title
	 * 
	 * @see #getTitle()
	 */
    void setTitle(String title);

    /**
     * 
     * @param notes
     * 
     * @see #getNotes()
     */
    void setNotes(String notes);

    // -----------------------------------------------------------------

	/**
	 * @return the account this object is a lot of.
	 */
	GnuCashWritableAccount getAccount();

    // -----------------------------------------------------------------

    void clearTransactionSplits();

    /**
     * 
     * @param split
     * 
     * @see #getTransactionSplits()
     */
    void addTransactionSplit(GnuCashWritableTransactionSplit split);

    /**
     * 
     * @param splitList
     * 
     * @see #getTransactionSplits()
     */
    void setTransactionSplits(List<GnuCashWritableTransactionSplit> splitList);

}

