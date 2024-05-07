package org.gnucash.api.write.impl.hlp;

import java.util.List;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorJobImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobManager extends org.gnucash.api.read.impl.hlp.FileJobManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);

	// ---------------------------------------------------------------

	public FileJobManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	// ::TODO
//    @Override
//    protected GnuCashGenerJobImpl createGenerJob(final GncGncJob jwsdpJob) {
//    	GnuCashWritableGenerJobImpl job = new GnuCashWritableGenerJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
//    	LOGGER.debug("Generated new generic job: " + job.getID());
//    	return job;
//    }

	// ::TODO
//	@Override
//	protected GnuCashCustomerJobImpl createCustomerJob(final GncGncJob jwsdpJob) {
//		GnuCashWritableCustomerJobImpl job = new GnuCashWritableCustomerJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
//		LOGGER.debug("createCustomerJob: Generated new writable customer job: " + job.getID());
//		return job;
//	}
//
	// ::TODO
//	@Override
//	protected GnuCashVendorJobImpl createVendorJob(final GncGncJob jwsdpJob) {
//		GnuCashWritableVendorJobImpl job = new GnuCashWritableVendorJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
//		LOGGER.debug("createVendorJob: Generated new writable vendor job: " + job.getID());
//		return job;
//	}

	// ---------------------------------------------------------------
	
	public List<GnuCashWritableCustomerJob> getWritableJobsByCustomer(final GnuCashCustomer cust) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		return FileJobManager_Customer.getJobsByCustomer(this, cust);
	}

	public List<GnuCashWritableVendorJob> getWritableJobsByVendor(final GnuCashVendor vend) {
		if ( jobMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return FileJobManager_Vendor.getJobsByVendor(this, vend);
	}

}
