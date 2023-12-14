package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.write.impl.GnucashWritableEmployeeImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEmployeeManager extends org.gnucash.api.read.impl.hlp.FileEmployeeManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileEmployeeManager.class);
    
    // ---------------------------------------------------------------
    
    public FileEmployeeManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new employee
     * @see FileEmployeeManager#createEmployee(GncV2.GncBook.GncGncCustomer)
     */
    @Override
    protected GnucashEmployeeImpl createEmployee(final GncV2.GncBook.GncGncEmployee jwsdpEmpl) {
	GnucashWritableEmployeeImpl empl = new GnucashWritableEmployeeImpl(jwsdpEmpl, (GnucashWritableFileImpl) gcshFile);
	LOGGER.debug("Generated new writable employee: " + empl.getID());
	return empl;
    }

}
