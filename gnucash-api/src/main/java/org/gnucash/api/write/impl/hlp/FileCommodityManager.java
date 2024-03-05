package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.write.impl.GnuCashWritableCommodityImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCommodityManager extends org.gnucash.api.read.impl.hlp.FileCommodityManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCommodityManager.class);
    
    // ---------------------------------------------------------------
    
    public FileCommodityManager(GnuCashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
	/*
	 * Creates the writable version of the returned object.
	 */
    @Override
    protected GnuCashCommodityImpl createCommodity(final GncCommodity jwsdpCmdty) {
	GnuCashWritableCommodityImpl cmdty = new GnuCashWritableCommodityImpl(jwsdpCmdty, (GnuCashWritableFileImpl) gcshFile);
	LOGGER.debug("createCommodity: Generated new writable commodity: " + cmdty.getQualifID());
	return cmdty;
    }

}
