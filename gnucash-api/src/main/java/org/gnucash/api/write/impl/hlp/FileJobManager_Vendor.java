package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnuCashWritableGenerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileJobManager_Vendor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileJobManager_Vendor.class);
    
	// ---------------------------------------------------------------

	public static List<GnuCashWritableVendorJob> getJobsByVendor(final FileJobManager jobMgr, final GnuCashVendor vend) {
		List<GnuCashWritableVendorJob> retval = new ArrayList<GnuCashWritableVendorJob>();

		for ( GnuCashGenerJob job : jobMgr.getGenerJobs() ) {
			if ( job.getOwnerID().equals(vend.getID()) ) {
				try {
					GnuCashWritableVendorJobImpl wrtblJob = new GnuCashWritableVendorJobImpl((GnuCashWritableGenerJobImpl) job);
					retval.add(wrtblJob);
				} catch (WrongJobTypeException e) {
					LOGGER.error("getJobsByVendor: Cannot instantiate GnuCashWritableVendorJobImpl");
				}
			}
		}

		return retval;
	}

}
