package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableTransactionImpl;
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
	return trx;
    }

}
