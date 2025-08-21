package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.GnuCashCommodity;
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

	// ---------------------------------------------------------------

	public void addCommodity(GnuCashCommodity cmdty) {
		if ( cmdty == null ) {
			throw new IllegalArgumentException("argument <cmdty> is null");
		}
		
		cmdtyMap.put(cmdty.getQualifID().toString(), cmdty);

		if ( cmdty.getXCode() != null )
			xCodeMap.put(cmdty.getXCode(), cmdty.getQualifID().toString());

		LOGGER.debug("Added commodity to cache: " + cmdty.getQualifID());
	}

	public void removeCommodity(GnuCashCommodity cmdty) {
		if ( cmdty == null ) {
			throw new IllegalArgumentException("argument <cmdty> is null");
		}
		
		cmdtyMap.remove(cmdty.getQualifID().toString());
		
		if ( cmdty.getXCode() != null ) {
			// xCodeMap.keySet().remove(cmdty.getXCode());
			// xCodeMap.values().remove(cmdty.getQualifID().toString());
			xCodeMap.remove(cmdty.getXCode());
		}

		LOGGER.debug("Removed commodity from cache: " + cmdty.getQualifID());
	}

}
