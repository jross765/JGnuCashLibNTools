package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.Price;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritablePriceImpl;
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

}
