package org.gnucash.api.read.spec;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.base.basetypes.simple.GCshCustID;

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
	 * 
	 * @see #getCustomerID()
	 */
	GnuCashCustomer getCustomer();

	/**
	 *
	 * @return the id of the customer this job is from.
	 * 
	 * @see #getCustomer()
	 */
	GCshCustID getCustomerID();
	
}
