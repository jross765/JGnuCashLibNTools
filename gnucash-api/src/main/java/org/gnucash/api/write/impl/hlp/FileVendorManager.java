package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.read.GnuCashVendor;
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

	// ---------------------------------------------------------------

	public void addVendor(GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
		}
		
		vendMap.put(vend.getID(), vend);
		LOGGER.debug("Added vendor to cache: " + vend.getID());
	}

	public void removeVendor(GnuCashVendor vend) {
		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
		}
		
		vendMap.remove(vend.getID());
		LOGGER.debug("Removed vendor from cache: " + vend.getID());
	}

}
