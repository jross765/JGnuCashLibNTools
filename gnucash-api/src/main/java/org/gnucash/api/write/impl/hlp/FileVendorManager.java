package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableVendorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileVendorManager extends org.gnucash.api.read.impl.hlp.FileVendorManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileVendorManager.class);

	// ---------------------------------------------------------------

	public FileVendorManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashVendorImpl createVendor(final GncGncVendor jwsdpVend) {
		GnucashWritableVendorImpl vend = new GnucashWritableVendorImpl(jwsdpVend, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("Generated new writable vendor: " + vend.getID());
		return vend;
	}

}
