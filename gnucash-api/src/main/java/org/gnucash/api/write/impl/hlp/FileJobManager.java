package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorJobImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobManager extends org.gnucash.api.read.impl.hlp.FileJobManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);
    
    // ---------------------------------------------------------------
    
    public FileJobManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FileJobManager#createGenerJob(GncV2.GncBook.GncGncJob)
     */
    // ::TODO
//    @Override
//    protected GnucashCustomerJobImpl createGenerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
//	// ::TODO: CUSTOMER job?
//	GnucashWritableGenerJobImpl job = new GnucashWritableGenerJobImpl(jwsdpJob, gcshFile);
//	return job;
//    }
    
    @Override
    protected GnucashCustomerJobImpl createCustomerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashWritableCustomerJobImpl job = new GnucashWritableCustomerJobImpl(jwsdpJob, gcshFile);
	return job;
    }

    @Override
    protected GnucashVendorJobImpl createVendorJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(jwsdpJob, gcshFile);
	return job;
    }

}
