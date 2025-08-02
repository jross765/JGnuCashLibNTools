package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Customer.class);
    
	// ---------------------------------------------------------------

	public static List<GnuCashCustomerJob> getJobsByCustomer(final FileJobManager jobMgr, final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}
		
		List<GnuCashCustomerJob> retval = new ArrayList<GnuCashCustomerJob>();

		for ( GnuCashGenerJob job : jobMgr.getGenerJobs() ) {
			// Important: compare strings, not objects
			if ( job.getOwnerID().toString().equals(cust.getID().toString()) ) {
					retval.add(new GnuCashCustomerJobImpl(job));
			}
		}

		return retval;
	}

}
