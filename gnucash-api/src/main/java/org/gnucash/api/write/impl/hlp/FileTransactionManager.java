package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableTransactionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.gnucash.api.read.impl.hlp.FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	public FileTransactionManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
		GnucashWritableTransactionImpl trx = new GnucashWritableTransactionImpl(jwsdpTrx, gcshFile);
		LOGGER.debug("Generated new writable transaction: " + trx.getID());
		return trx;
	}

	// ::TODO
//    @Override
//    protected GnucashTransactionSplitImpl createTransactionSplit(
//	    final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
//	    final GnucashTransaction trx,
//	    final boolean addSpltToAcct,
//	    final boolean addSpltToInvc)  throws IllegalArgumentException {
//	GnucashWritableTransactionSplitImpl splt = new GnucashWritableTransactionSplitImpl(jwsdpTrxSplt, trx, 
//                								           addSpltToAcct, addSpltToInvc);
//	LOGGER.debug("Generated new writable transaction split: " + splt.getID());
//	return splt;
//    }

}
