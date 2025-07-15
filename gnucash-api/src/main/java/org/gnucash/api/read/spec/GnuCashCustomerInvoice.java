package org.gnucash.api.read.spec;

import java.util.Collection;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.spec.hlp.SpecInvoiceCommon;

/**
 * An invoice that is sent to a customer so (s)he knows what to pay you. <br>
 * <br>
 * Note: The correct business term is "invoice" (as opposed to "bill" or "voucher"), 
 * as used in the GnuCash documentation. However, on a technical level, 
 * customer invoices, vendor bills and employee vouchers are referred to 
 * as "GncInvoice" objects.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the 
 * invoice was created and secondarily on the date it should be paid.
 *
 * @see GnuCashVendorBill
 * @see GnuCashEmployeeVoucher
 * @see GnuCashJobInvoice
 * @see GnuCashGenerInvoice
 */
public interface GnuCashCustomerInvoice extends GnuCashGenerInvoice,
												SpecInvoiceCommon
{

    /**
     * @return ID of customer this invoice has been sent to.
     * 
     * @see #getCustomer()
     */
    GCshID getCustomerID();

    /**
     * @return Customer this invoice has been sent to.
     * 
     * @see #getCustomerID()
     */
    GnuCashCustomer getCustomer();
	
    // ---------------------------------------------------------------

    GnuCashCustomerInvoiceEntry getEntryByID(GCshID entrID);

    Collection<GnuCashCustomerInvoiceEntry> getEntries();

    void addEntry(GnuCashCustomerInvoiceEntry entr);
    
}
