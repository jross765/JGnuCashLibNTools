package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashCommodityImpl;
import org.gnucash.api.write.impl.GnucashWritableCommodityImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCommodityManager extends org.gnucash.api.read.impl.hlp.FileCommodityManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCommodityManager.class);
    
    // ---------------------------------------------------------------
    
    public FileCommodityManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FileCommodityManager#createAccount(GncAccount)
     */
    @Override
    protected GnucashCommodityImpl createCommodity(final GncV2.GncBook.GncCommodity jwsdpCmdty) {
	GnucashWritableCommodityImpl cmdty = new GnucashWritableCommodityImpl(jwsdpCmdty, (GnucashWritableFileImpl) gcshFile);
	LOGGER.debug("Generated new writable commodity: " + cmdty.getQualifID());
	return cmdty;
    }

}
