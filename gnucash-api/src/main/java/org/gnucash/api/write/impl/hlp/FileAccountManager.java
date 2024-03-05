package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.write.impl.GnuCashWritableAccountImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccountManager extends org.gnucash.api.read.impl.hlp.FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	public FileAccountManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashAccountImpl createAccount(final GncAccount jwsdpAcct) {
		GnuCashWritableAccountImpl acct = new GnuCashWritableAccountImpl(jwsdpAcct, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createAccount: Generated new writable account: " + acct.getID());
		return acct;
	}

}
