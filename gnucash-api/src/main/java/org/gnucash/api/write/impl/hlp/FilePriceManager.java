package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePriceManager extends org.gnucash.api.read.impl.hlp.FilePriceManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);
    
    // ---------------------------------------------------------------
    
    public FilePriceManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FilePriceManager#createAccount(GncAccount)
     */
    // ::TODO
//    @Override
//    protected GCshPriceImpl createPrice(final GncV2.GncBook.GncPricedb.Price jwsdpPrc) {
//	GnucashWritablePriceImpl cmdty = new GnucashWritablePriceImpl(jwsdpPrc, (GnucashWritableFileImpl) gcshFile);
//	return cmdty;
//    }

}
