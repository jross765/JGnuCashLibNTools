package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.write.impl.GnucashWritableCustomerImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCustomerManager extends org.gnucash.api.read.impl.hlp.FileCustomerManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);
    
    // ---------------------------------------------------------------
    
    public FileCustomerManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see FileCustomerManager#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    @Override
    protected GnucashCustomerImpl createCustomer(final GncV2.GncBook.GncGncCustomer jwsdpCust) {
	GnucashWritableCustomerImpl cust = new GnucashWritableCustomerImpl(jwsdpCust, (GnucashWritableFileImpl) gcshFile);
	LOGGER.info("Generated new writable customer: " + cust.getID());
	return cust;
    }

}
