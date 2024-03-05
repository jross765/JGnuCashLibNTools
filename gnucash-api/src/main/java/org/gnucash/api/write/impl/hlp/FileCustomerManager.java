package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.write.impl.GnuCashWritableCustomerImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCustomerManager extends org.gnucash.api.read.impl.hlp.FileCustomerManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);

	// ---------------------------------------------------------------

	public FileCustomerManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashCustomerImpl createCustomer(final GncGncCustomer jwsdpCust) {
		GnuCashWritableCustomerImpl cust = new GnuCashWritableCustomerImpl(jwsdpCust, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createCustomer: Generated new writable customer: " + cust.getID());
		return cust;
	}

}
