package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;

public interface GnucashVendorJob extends GnucashGenerJob {

	/**
	 *
	 * @return the vendor this job is from.
	 */
	GnucashVendor getVendor();

	/**
	 *
	 * @return the id of the vendor this job is from.
	 * @see #getVendor()
	 */
	GCshID getVendorID();
	
}
