package org.gnucash.api.read.spec;

import java.util.Collection;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * A special variant of a customer invoice or a vendor bill 
 * (<strong>not</strong> of an employee voucher):
 * As opposed to {@link GnucashCustomerInvoice} and {@link GnucashVendorBill}, this one 
 * does <strong>not directly</strong> belong to a customer
 * or a vendor, but is attached to a customer/vendor <strong>job</strong>.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the Invoice was
 * created and secondarily on the date it should be paid.
 *
 * @see GnucashCustomerInvoice
 * @see GnucashEmployeeVoucher
 * @see GnucashVendorBill
 * @see GnucashGenerJob
 * @see GnucashCustomer
 * @see GnucashVendor
 */
public interface GnucashJobInvoice extends GnucashGenerInvoice {

    /**
     * @return ID of customer this invoice/bill has been sent to.
     * 
     * Note that a job may lead to multiple o no invoices.
     * (e.g. a monthly payment for a long lasting contract.)
     * @return the ID of the job this invoice is for.
     */
    GCshID getJobID();

    GCshOwner.Type getJobType();

    // ----------------------------

    /**
     * @return ID of customer this invoice has been sent to.
     */
    GCshID getCustomerID() throws WrongInvoiceTypeException;

    /**
     * @return ID of vendor this bill has been sent from.
     */
    GCshID getVendorID() throws WrongInvoiceTypeException;
    
    // ----------------------------

    /**
     * @return the job this invoice is for
     */
    GnucashGenerJob getGenerJob();
	
    /**
     * @return Job of customer this invoice has been sent to.
     * @throws WrongInvoiceTypeException 
     */
    GnucashCustomerJob getCustJob() throws WrongJobTypeException;
	
    /**
     * @return Job of vendor this bill has been sent from.
     * @throws WrongInvoiceTypeException 
     */
    GnucashVendorJob getVendJob() throws WrongJobTypeException;
	
    // ----------------------------

    /**
     * @return Customer this invoice has been sent to.
     * @throws WrongInvoiceTypeException 
     */
    GnucashCustomer getCustomer() throws WrongInvoiceTypeException;
	
    /**
     * @return Vendor this bill has been sent from.
     * @throws WrongInvoiceTypeException 
     */
    GnucashVendor getVendor() throws WrongInvoiceTypeException;
	
    // ---------------------------------------------------------------

    GnucashJobInvoiceEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException;

    Collection<GnucashJobInvoiceEntry> getEntries() throws WrongInvoiceTypeException;

    void addEntry(GnucashJobInvoiceEntry entry);
    
}
