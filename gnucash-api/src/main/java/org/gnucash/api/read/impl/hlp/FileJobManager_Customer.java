package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Customer.class);
    
	// ---------------------------------------------------------------

	public static List<GnucashCustomerJob> getJobsByCustomer(final FileJobManager jobMgr, final GnucashCustomer cust) {
		List<GnucashCustomerJob> retval = new ArrayList<GnucashCustomerJob>();

		for ( GnucashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(cust.getID()) ) {
				try {
					retval.add(new GnucashCustomerJobImpl(job));
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByCustomer: Cannot instantiate GnucashCustomerJobImpl");
				}
			}
		}

		return retval;
	}

}
