package org.gnucash.api.read.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;

/**
 * A {@link GnuCashGenerJob} that belongs to a {@link GnuCashCustomer}
 * <br>
 * Cf. <a href=" https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-jobs1.html">GnuCash manual</a>
 * 
 * @see GnuCashVendorJob
 */
public interface GnuCashCustomerJob extends GnuCashGenerJob {

	/**
	 *
	 * @return the customer this job is from.
	 */
	GnuCashCustomer getCustomer();

	/**
	 *
	 * @return the id of the customer this job is from.
	 * @see #getCustomer()
	 */
	GCshID getCustomerID();
	
}
