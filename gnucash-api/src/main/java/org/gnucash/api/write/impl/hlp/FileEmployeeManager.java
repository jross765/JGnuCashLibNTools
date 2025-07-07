package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.read.GnuCashEmployee;
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

	// ---------------------------------------------------------------

	public void addEmployee(GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		emplMap.put(empl.getID(), empl);
		LOGGER.debug("Added employee to cache: " + empl.getID());
	}

	public void removeEmployee(GnuCashEmployee empl) {
		if ( empl == null ) {
			throw new IllegalArgumentException("argument <empl> is null");
		}
		
		emplMap.remove(empl.getID());
		LOGGER.debug("Removed employee from cache: " + empl.getID());
	}

}
