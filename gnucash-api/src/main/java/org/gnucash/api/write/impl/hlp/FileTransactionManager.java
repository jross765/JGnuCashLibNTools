package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.gnucash.api.read.impl.hlp.FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	public FileTransactionManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
		GnuCashWritableTransactionImpl trx = new GnuCashWritableTransactionImpl(jwsdpTrx, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createTransaction: Generated new writable transaction: " + trx.getID());
		return trx;
	}

	// ::TODO
//    @Override
//    protected GnuCashTransactionSplitImpl createTransactionSplit(
//	    final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
//	    final GnuCashTransaction trx,
//	    final boolean addSpltToAcct,
//	    final boolean addSpltToInvc)  throws IllegalArgumentException {
//	GnuCashWritableTransactionSplitImpl splt = new GnuCashWritableTransactionSplitImpl(jwsdpTrxSplt, trx, 
//                								           addSpltToAcct, addSpltToInvc);
//	LOGGER.debug("createTransactionSplit: Generated new writable transaction split: " + splt.getID());
//	return splt;
//    }

}
