package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncCommodity;
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
    
	/*
	 * Creates the writable version of the returned object.
	 */
    @Override
    protected GnucashCommodityImpl createCommodity(final GncCommodity jwsdpCmdty) {
	GnucashWritableCommodityImpl cmdty = new GnucashWritableCommodityImpl(jwsdpCmdty, (GnucashWritableFileImpl) gcshFile);
	LOGGER.debug("Generated new writable commodity: " + cmdty.getQualifID());
	return cmdty;
    }

}
