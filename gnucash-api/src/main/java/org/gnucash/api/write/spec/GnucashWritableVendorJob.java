package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerJob;

/**
 * Vendor job that can be modified.
 * 
 * @see GnucashVendorJob
 * 
 * @see GnucashWritableCustomerJob
 */
public interface GnucashWritableVendorJob extends GnucashWritableGenerJob 
{

    void remove() throws WrongInvoiceTypeException;

    /**
     * Not used.
     * 
     * @param a not used.
     * @see GnucashGenerJob#JOB_TYPE
     */
//    void setVendorType(String a);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param newVendor the vendor who issued this job.
     * @throws WrongInvoiceTypeException
     *  
     */
    void setVendor(GnucashVendor newVendor) throws WrongInvoiceTypeException;

}
