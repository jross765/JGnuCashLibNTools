package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableVendorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileVendorManager extends org.gnucash.api.read.impl.hlp.FileVendorManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileVendorManager.class);

	// ---------------------------------------------------------------

	public FileVendorManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashVendorImpl createVendor(final GncGncVendor jwsdpVend) {
		GnuCashWritableVendorImpl vend = new GnuCashWritableVendorImpl(jwsdpVend, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createVendor: Generated new writable vendor: " + vend.getID());
		return vend;
	}

}
