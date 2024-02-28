package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Vendor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Vendor.class);
    
	// ---------------------------------------------------------------

	public static List<GnucashVendorJob> getJobsByVendor(final FileJobManager jobMgr, final GnucashVendor vend) {
		List<GnucashVendorJob> retval = new ArrayList<GnucashVendorJob>();

		for ( GnucashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(vend.getID()) ) {
				try {
					retval.add(new GnucashVendorJobImpl(job));
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByVendor: Cannot instantiate GnucashVendorJobImpl");
				}
			}
		}

		return retval;
	}

}
