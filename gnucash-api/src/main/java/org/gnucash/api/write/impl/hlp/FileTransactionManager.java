package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableTransactionImpl;
import org.gnucash.api.write.impl.GnucashWritableTransactionSplitImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.gnucash.api.read.impl.hlp.FileTransactionManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);
    
    // ---------------------------------------------------------------
    
    public FileTransactionManager(GnucashWritableFileImpl gcshFile) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FileTransactionManager#createTransaction(GncTransaction)
     */
    @Override
    protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
	GnucashWritableTransactionImpl trx = new GnucashWritableTransactionImpl(jwsdpTrx, gcshFile);
	LOGGER.info("Generated new writable transaction: " + trx.getID());
	return trx;
    }

    // ::TODO
//    @Override
//    protected GnucashTransactionSplitImpl createTransactionSplit(
//	    final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
//	    final GnucashTransaction trx,
//	    final boolean addSpltToAcct,
//	    final boolean addSpltToInvc)  throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
//	GnucashWritableTransactionSplitImpl splt = new GnucashWritableTransactionSplitImpl(jwsdpTrxSplt, trx, 
//                								           addSpltToAcct, addSpltToInvc);
//	LOGGER.info("Generated new writable transaction split: " + splt.getID());
//	return splt;
//    }

}
