package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerJob;

/**
 * Customer job that can be modified.
 * 
 * @see GnucashCustomerJob
 */
public interface GnucashWritableCustomerJob extends GnucashWritableGenerJob 
{

    void remove() throws WrongInvoiceTypeException;

    /**
     * Not used.
     * 
     * @param a not used.
     * @see GnucashGenerJob#JOB_TYPE
     */
//    void setCustomerType(String a);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param newCustomer the customer who issued this job.
     * @throws WrongInvoiceTypeException
     *  
     */
    void setCustomer(GnucashCustomer newCustomer) throws WrongInvoiceTypeException;

}
