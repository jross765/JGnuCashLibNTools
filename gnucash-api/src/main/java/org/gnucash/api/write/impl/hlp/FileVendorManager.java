package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableVendorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileVendorManager extends org.gnucash.api.read.impl.hlp.FileVendorManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileVendorManager.class);
    
    // ---------------------------------------------------------------
    
    public FileVendorManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new vendor
     * @see FileVendorManager#createVendor(GncV2.GncBook.GncGncCustomer)
     */
    @Override
    protected GnucashVendorImpl createVendor(final GncV2.GncBook.GncGncVendor jwsdpVend) {
	GnucashWritableVendorImpl vend = new GnucashWritableVendorImpl(jwsdpVend, (GnucashWritableFileImpl) gcshFile);
	return vend;
    }

}
