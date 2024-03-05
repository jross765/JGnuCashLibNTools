package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnuCashWritableGenerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Customer.class);
    
	// ---------------------------------------------------------------

	public static List<GnuCashWritableCustomerJob> getJobsByCustomer(final FileJobManager jobMgr, final GnuCashCustomer cust) {
		List<GnuCashWritableCustomerJob> retval = new ArrayList<GnuCashWritableCustomerJob>();

		for ( GnuCashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(cust.getID()) ) {
				try {
					GnuCashWritableCustomerJobImpl wrtblJob = new GnuCashWritableCustomerJobImpl((GnuCashWritableGenerJobImpl) job);
					retval.add(wrtblJob);
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByCustomer: Cannot instantiate GnuCashWritableCustomerJobImpl");
				}
			}
		}

		return retval;
	}

}
