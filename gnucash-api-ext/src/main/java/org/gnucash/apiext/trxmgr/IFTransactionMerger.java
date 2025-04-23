package org.gnucash.apiext.trxmgr;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.base.basetypes.simple.GCshID;

interface IFTransactionMerger {

	public void merge(GCshID survivorID, GCshID dierID) throws MergePlausiCheckException;

	public void merge(GnuCashTransaction survivor, GnuCashWritableTransaction dier) throws MergePlausiCheckException;

}