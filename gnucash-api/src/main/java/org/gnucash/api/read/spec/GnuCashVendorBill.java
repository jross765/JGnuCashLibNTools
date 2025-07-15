package org.gnucash.api.read.spec;

import java.util.Collection;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.hlp.SpecInvoiceCommon;

/**
 * A bill that is sent from a vendor so you know what to pay him/her.<br>
 * <br>
 * Note: The correct business term is "bill" (as opposed to "invoice" or "voucher"), 
 * as used in the GnuCash documentation. However, on a technical level,  
 * customer invoices, vendor bills and employee vouchers are referred to 
 * as "GncInvoice" objects.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the 
 * bill was created and secondarily on the date it should be paid.
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashGenerInvoice
 */
public interface GnuCashVendorBill extends GnuCashGenerInvoice,
										   SpecInvoiceCommon
{

    /**
     * @return ID of vendor this invoice has been sent from 
     * 
     * @see #getVendor()
     */
    GCshID getVendorID();

    /**
     * @return Customer this invoice has been sent to.
     * 
     * @see #getVendorID()
     */
    GnuCashVendor getVendor();
	
    // ---------------------------------------------------------------

    GnuCashVendorBillEntry getEntryByID(GCshID entrID);

    Collection<GnuCashVendorBillEntry> getEntries();

    void addEntry(GnuCashVendorBillEntry entr);

}
