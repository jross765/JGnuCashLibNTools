package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.Price;
import org.gnucash.api.read.impl.GnucashPriceImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritablePriceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePriceManager extends org.gnucash.api.read.impl.hlp.FilePriceManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);

	// ---------------------------------------------------------------

	public FilePriceManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashPriceImpl createPrice(final Price jwsdpPrc) {
		GnucashWritablePriceImpl prc = new GnucashWritablePriceImpl(jwsdpPrc, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("createPrice: Generated new writable price: " + prc.getID());
		return prc;
	}

}
