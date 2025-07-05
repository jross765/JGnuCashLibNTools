package org.gnucash.api.write.aux;

import java.util.List;

import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

public interface GCshWritableAccountLot extends GCshAccountLot, 
                                                GnuCashWritableObject
{

	/**
	 * Remove this lot from the account.
	 *  
	 */
	void remove();

    // -----------------------------------------------------------------

    void setTitle(String title);

    void setNotes(String notes);

    // -----------------------------------------------------------------

	/**
	 * @return the account this object is a lot of.
	 */
	GnuCashWritableAccount getAccount();

    // -----------------------------------------------------------------

    void clearTransactionSplits();

    void addTransactionSplit(GnuCashWritableTransactionSplit split);

    void setTransactionSplits(List<GnuCashWritableTransactionSplit> splitList);

}

