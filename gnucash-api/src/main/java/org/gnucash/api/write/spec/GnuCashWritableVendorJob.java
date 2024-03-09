package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.write.GnuCashWritableGenerJob;

/**
 * Vendor job that can be modified.
 * 
 * @see GnuCashVendorJob
 * 
 * @see GnuCashWritableCustomerJob
 */
public interface GnuCashWritableVendorJob extends GnuCashWritableGenerJob {

    void remove();

    /**
     * Not used.
     * 
     * @param a not used.
     * @see GnuCashGenerJob#JOB_TYPE
     */
//    void setVendorType(String a);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param newVendor the vendor who issued this job.
* 
     *  
     */
    void setVendor(GnuCashVendor newVendor);

}
