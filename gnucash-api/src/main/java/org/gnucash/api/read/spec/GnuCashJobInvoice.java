package org.gnucash.api.read.spec;

import java.util.Collection;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.spec.hlp.SpecInvoiceCommon;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * A special variant of a customer invoice or a vendor bill 
 * (<strong>not</strong> of an employee voucher):
 * As opposed to {@link GnuCashCustomerInvoice} and {@link GnuCashVendorBill}, this one 
 * does <strong>not directly</strong> belong to a customer
 * or a vendor, but is attached to a customer/vendor <strong>job</strong>.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the Invoice was
 * created and secondarily on the date it should be paid.
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashGenerJob
 * @see GnuCashCustomer
 * @see GnuCashVendor
 */
public interface GnuCashJobInvoice extends GnuCashGenerInvoice,
										   SpecInvoiceCommon
{

    /**
     * @return ID of customer this invoice/bill has been sent to.
     * 
     * Note that a job may lead to multiple o no invoices.
     * (e.g. a monthly payment for a long lasting contract.)
     */
    GCshID getJobID();

    GCshOwner.Type getJobType();

    // ----------------------------

    /**
     * @return ID of customer this invoice has been sent to.
     * 
     * @see #getGenerJob()
     */
    GCshID getCustomerID();

    /**
     * @return ID of vendor this bill has been sent from.
     * 
     * @see #getGenerJob()
     */
    GCshID getVendorID();
    
    // ----------------------------

    /**
     * @return the job this invoice is for
     */
    GnuCashGenerJob getGenerJob();
	
    /**
     * @return Job of customer this invoice has been sent to.
     */
    GnuCashCustomerJob getCustJob();
	
    /**
     * @return Job of vendor this bill has been sent from.
     */
    GnuCashVendorJob getVendJob();
	
    // ----------------------------

    /**
     * @return Customer this invoice has been sent to.
     */
    GnuCashCustomer getCustomer();
	
    /**
     * @return Vendor this bill has been sent from.
     */
    GnuCashVendor getVendor();
	
    // ---------------------------------------------------------------

    GnuCashJobInvoiceEntry getEntryByID(GCshID entrID);

    Collection<GnuCashJobInvoiceEntry> getEntries();

    void addEntry(GnuCashJobInvoiceEntry entr);
    
}
