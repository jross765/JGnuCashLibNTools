package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorJobImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileJobManager extends org.gnucash.api.read.impl.hlp.FileJobManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager.class);

	// ---------------------------------------------------------------

	public FileJobManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	// ::TODO
//    @Override
//    protected GnucashCustomerJobImpl createGenerJob(final GncGncJob jwsdpJob) {
//	// ::TODO: CUSTOMER job?
//	GnucashWritableGenerJobImpl job = new GnucashWritableGenerJobImpl(jwsdpJob, (GnucashWritableFileImpl) gcshFile);
//	LOGGER.debug("Generated new generic job: " + job.getID());
//	return job;
//    }

	@Override
	protected GnucashCustomerJobImpl createCustomerJob(final GncGncJob jwsdpJob) {
		GnucashWritableCustomerJobImpl job = new GnucashWritableCustomerJobImpl(jwsdpJob, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("createCustomerJob: Generated new writable customer job: " + job.getID());
		return job;
	}

	@Override
	protected GnucashVendorJobImpl createVendorJob(final GncGncJob jwsdpJob) {
		GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(jwsdpJob, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("createVendorJob: Generated new writable vendor job: " + job.getID());
		return job;
	}

}
