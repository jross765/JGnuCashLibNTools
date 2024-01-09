package org.gnucash.api.read.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerJob;

/**
 * A {@link GnucashGenerJob} that belongs to a {@link GnucashCustomer}
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
