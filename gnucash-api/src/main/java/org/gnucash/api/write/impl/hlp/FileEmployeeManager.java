package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.write.impl.GnuCashWritableEmployeeImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEmployeeManager extends org.gnucash.api.read.impl.hlp.FileEmployeeManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileEmployeeManager.class);

	// ---------------------------------------------------------------

	public FileEmployeeManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashEmployeeImpl createEmployee(final GncGncEmployee jwsdpEmpl) {
		GnuCashWritableEmployeeImpl empl = new GnuCashWritableEmployeeImpl(jwsdpEmpl, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createEmployee: Generated new writable employee: " + empl.getID());
		return empl;
	}

}
