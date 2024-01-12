package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.write.impl.GnucashWritableCustomerImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCustomerManager extends org.gnucash.api.read.impl.hlp.FileCustomerManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);

	// ---------------------------------------------------------------

	public FileCustomerManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashCustomerImpl createCustomer(final GncGncCustomer jwsdpCust) {
		GnucashWritableCustomerImpl cust = new GnucashWritableCustomerImpl(jwsdpCust, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("Generated new writable customer: " + cust.getID());
		return cust;
	}

}
