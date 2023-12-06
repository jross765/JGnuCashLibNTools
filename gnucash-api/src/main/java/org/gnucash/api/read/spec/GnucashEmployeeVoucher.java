package org.gnucash.api.read.spec;

import java.util.Collection;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;

/**
 * This class represents a voucher that is sent from an employee
 * so you know what to pay him/her.<br>
 * <br>
 * Note: The correct business term is "voucher" (as opposed to "invoice" or "bill"), 
 * as used in the GnuCash documentation. However, on a technical level, both 
 * customer invoices, vendor bills and employee vouchers are referred to as 
 * "GncInvoice" objects.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the 
 * voucher was created and secondarily on the date it should be paid.
 *
 * @see GnucashGenerJob
 * @see GnucashEmployee
 */
public interface GnucashEmployeeVoucher extends GnucashGenerInvoice {

    /**
     * @return ID of employee this invoice has been sent from 
     */
    GCshID getEmployeeID();

    /**
     * @return Customer this invoice has been sent to.
     * @throws WrongInvoiceTypeException 
     */
    GnucashEmployee getEmployee() throws WrongInvoiceTypeException;
	
    // ---------------------------------------------------------------

    GnucashEmployeeVoucherEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException;

    Collection<GnucashEmployeeVoucherEntry> getEntries() throws WrongInvoiceTypeException;

    void addEntry(final GnucashEmployeeVoucherEntry entry);

}
