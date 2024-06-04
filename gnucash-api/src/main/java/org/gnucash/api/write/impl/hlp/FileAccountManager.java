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
		// CAUTION: Do *not* instantiate with GnuCashWritableAccountImpl(jwsdpAcct, gcshFile),
		// because else there will be subtle problems with the assignment of transactions/
		// trx-splits of the GnuCashWritableAccount, and thus, e.g., getBalance() will yield 
		// wrong results.
		// E.g.:
		// - GnuCashAccount acct from GnuCashFile.getAccountByID() -> acct.getBalance() will work
		// - GnuCashWritableAccount from GnuCashWritableFile.getWritableAccountByID() acct -> acct.getBalance() will work
		// - GnuCashAccount acct from GnuCashWritableFile.getAccountByID() -> acct.getBalance() will *not* work
		// The following code fixes this problem by first calling super.createAccount() and then 
		// converting the read-only-object into a writable one by calling the other constructor.
		// NOT this:
		// GnuCashWritableAccountImpl wrtblAcct = new GnuCashWritableAccountImpl(jwsdpAcct, (GnuCashWritableFileImpl) gcshFile);
		// Instead:
		GnuCashAccountImpl roAcct = super.createAccount(jwsdpAcct);
		GnuCashWritableAccountImpl wrtblAcct = new GnuCashWritableAccountImpl((GnuCashAccountImpl) roAcct, true);
		LOGGER.debug("createAccount: Generated new writable account: " + wrtblAcct.getID());
		return wrtblAcct;
	}

}
