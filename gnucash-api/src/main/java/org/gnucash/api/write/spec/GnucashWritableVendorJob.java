package org.gnucash.api.write.spec;

import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerJob;

public interface GnucashWritableVendorJob extends GnucashWritableGenerJob 
{

    void remove() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void setVendor(GnucashVendor newVendor) throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

}
