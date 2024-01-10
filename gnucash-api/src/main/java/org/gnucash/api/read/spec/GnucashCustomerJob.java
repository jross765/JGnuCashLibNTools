package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerJob;

/**
 * A {@link GnucashGenerJob} that belongs to a {@link GnucashCustomer}
 * <br>
 * Cf. <a href=" https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-jobs1.html">GnuCash manual</a>
 * 
 * @see GnucashVendorJob
 */
public interface GnucashCustomerJob extends GnucashGenerJob {

	/**
	 *
	 * @return the customer this job is from.
	 */
	GnucashCustomer getCustomer();

	/**
	 *
	 * @return the id of the customer this job is from.
	 * @see #getCustomer()
	 */
	GCshID getCustomerID();
	
}
