package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerJob;

public interface GnucashWritableCustomerJob extends GnucashWritableGenerJob 
{

    void remove() throws WrongInvoiceTypeException, IllegalArgumentException;

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
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setCustomer(GnucashCustomer newCustomer) throws WrongInvoiceTypeException, IllegalArgumentException;

}
