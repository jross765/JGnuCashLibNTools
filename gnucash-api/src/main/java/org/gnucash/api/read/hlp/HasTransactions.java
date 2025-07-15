package org.gnucash.api.read.hlp;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshID;

public interface HasTransactions
{
	
    List<GnuCashTransactionSplit> getTransactionSplits();

    GnuCashTransactionSplit getTransactionSplitByID(final GCshID spltID);

    void addTransactionSplit(final GnuCashTransactionSplit splt);

    // ----------------------------

    boolean hasTransactions();

    List<GnuCashTransaction> getTransactions();

    List<GnuCashTransaction> getTransactions(LocalDate fromDate, LocalDate toDate);

}
