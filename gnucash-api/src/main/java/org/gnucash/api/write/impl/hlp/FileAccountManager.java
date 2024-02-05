package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.write.impl.GnucashWritableAccountImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccountManager extends org.gnucash.api.read.impl.hlp.FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	public FileAccountManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashAccountImpl createAccount(final GncAccount jwsdpAcct) {
		GnucashWritableAccountImpl acct = new GnucashWritableAccountImpl(jwsdpAcct, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("createAccount: Generated new writable account: " + acct.getID());
		return acct;
	}

}
