package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnucashWritableGenerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Customer.class);
    
	// ---------------------------------------------------------------

	public static List<GnucashWritableCustomerJob> getJobsByCustomer(final FileJobManager jobMgr, final GnucashCustomer cust) {
		List<GnucashWritableCustomerJob> retval = new ArrayList<GnucashWritableCustomerJob>();

		for ( GnucashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(cust.getID()) ) {
				try {
					GnucashWritableCustomerJobImpl wrtblJob = new GnucashWritableCustomerJobImpl((GnucashWritableGenerJobImpl) job);
					retval.add(wrtblJob);
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByCustomer: Cannot instantiate GnucashWritableCustomerJobImpl");
				}
			}
		}

		return retval;
	}

}
