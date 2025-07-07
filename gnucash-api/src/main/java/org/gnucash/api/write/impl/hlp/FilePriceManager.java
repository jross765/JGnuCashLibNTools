package org.gnucash.api.write.impl.hlp;

import java.util.List;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.Price;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritablePriceImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePriceManager extends org.gnucash.api.read.impl.hlp.FilePriceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);

	// ---------------------------------------------------------------

	public FilePriceManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashPriceImpl createPrice(final Price jwsdpPrc) {
		GnuCashWritablePriceImpl prc = new GnuCashWritablePriceImpl(jwsdpPrc, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createPrice: Generated new writable price: " + prc.getID());
		return prc;
	}

	// ---------------------------------------------------------------

	public void addPrice(GnuCashPrice prc) {
		if ( prc == null ) {
			throw new IllegalArgumentException("argument <prc> is null");
		}
		
		prcMap.put(prc.getID(), prc);
		LOGGER.debug("Added price to cache: " + prc.getID());
	}

	public void removePrice(GnuCashPrice prc) {
		if ( prc == null ) {
			throw new IllegalArgumentException("argument <prc> is null");
		}
		
		prcMap.remove(prc.getID());
		LOGGER.debug("Removed price from cache: " + prc.getID());
	}

	// ----------------------------
	
	public void removePrice_raw(final GCshID prcID) {
		for ( int i = 0; i < priceDB.getPrice().size(); i++ ) {
			Price jwsdpPrc = priceDB.getPrice().get(i); 
			if ( jwsdpPrc.getPriceId().getValue().equals(prcID.toString()) ) {
				priceDB.getPrice().remove(i);
				i--;
			}
		}
	}

}
