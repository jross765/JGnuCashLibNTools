package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableGenerJob;

/**
 * Customer job that can be modified.
 * 
 * @see GnuCashCustomerJob
 * 
 * @see GnuCashWritableVendorJob
 */
public interface GnuCashWritableCustomerJob extends GnuCashWritableGenerJob {

    void remove() throws WrongInvoiceTypeException;

    /**
     * Not used.
     * 
     * @param a not used.
     * @see GnuCashGenerJob#JOB_TYPE
     */
//    void setCustomerType(String a);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param newCustomer the customer who issued this job.
     * @throws WrongInvoiceTypeException
     *  
     */
    void setCustomer(GnuCashCustomer newCustomer) throws WrongInvoiceTypeException;

}
