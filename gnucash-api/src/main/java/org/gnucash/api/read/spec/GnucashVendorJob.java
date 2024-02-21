package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;

/**
 * A {@link GnucashGenerJob} that belongs to a {@link GnucashVendor}
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ap-jobs1.html">GnuCash manual</a>
 * 
 * @see GnucashCustomerJob
 */
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
