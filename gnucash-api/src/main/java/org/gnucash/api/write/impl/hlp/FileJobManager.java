package org.gnucash.api.write.impl.hlp;

import java.util.List;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
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
	// CAUTION: This method cannot exist, because GnuCashWritableGenerJobImpl is abstract
//    protected GnuCashWritableGenerJobImpl createWritableGenerJob(final GncGncJob jwsdpJob) {
//    	GnuCashWritableGenerJobImpl job = new GnuCashWritableGenerJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
//    	LOGGER.debug("createWritableGenerJob: Generated new generic job: " + job.getID());
//    	return job;
//    }

	protected GnuCashWritableCustomerJobImpl createWritableCustomerJob(final GncGncJob jwsdpJob) {
		GnuCashWritableCustomerJobImpl job = new GnuCashWritableCustomerJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createWritableCustomerJob: Generated new writable customer job: " + job.getID());
		return job;
	}

	protected GnuCashWritableVendorJobImpl createWritableVendorJob(final GncGncJob jwsdpJob) {
		GnuCashWritableVendorJobImpl job = new GnuCashWritableVendorJobImpl(jwsdpJob, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createWritableVendorJob: Generated new writable vendor job: " + job.getID());
		return job;
	}

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

	// ---------------------------------------------------------------

	public void addGenerJob(GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		jobMap.put(job.getID(), job);

		LOGGER.debug("Added (generic) jop to cache: " + job.getID());
	}

	public void removeGenerJob(GnuCashGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("argument <job> is null");
		}
		
		jobMap.remove(job.getID());

		LOGGER.debug("removeGenerJob: No. of generic jobs: " + jobMap.size());
	}

}
