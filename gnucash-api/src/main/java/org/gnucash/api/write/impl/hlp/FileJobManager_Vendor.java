package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnucashWritableGenerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Vendor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Vendor.class);
    
	// ---------------------------------------------------------------

	public static List<GnucashWritableVendorJob> getJobsByVendor(final FileJobManager jobMgr, final GnucashVendor vend) {
		List<GnucashWritableVendorJob> retval = new ArrayList<GnucashWritableVendorJob>();

		for ( GnucashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(vend.getID()) ) {
				try {
					GnucashWritableVendorJobImpl wrtblJob = new GnucashWritableVendorJobImpl((GnucashWritableGenerJobImpl) job);
					retval.add(wrtblJob);
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByVendor: Cannot instantiate GnucashWritableVendorJobImpl");
				}
			}
		}

		return retval;
	}

}
