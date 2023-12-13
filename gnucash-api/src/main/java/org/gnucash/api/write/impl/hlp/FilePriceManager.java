package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.aux.GCshPriceImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.aux.GCshWritablePriceImpl;
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
    @Override
    protected GCshPriceImpl createPrice(final GncV2.GncBook.GncPricedb.Price jwsdpPrc) {
	GCshWritablePriceImpl prc = new GCshWritablePriceImpl(jwsdpPrc, (GnucashWritableFileImpl) gcshFile);
	LOGGER.info("Generated new writable price: " + prc.getID());
	return prc;
    }

}
