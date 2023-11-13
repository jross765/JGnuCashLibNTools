package org.gnucash.read.spec;

import java.util.Collection;

import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.GnucashEmployee;

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
    String getEmployeeId();

    /**
     * @return Customer this invoice has been sent to.
     * @throws WrongInvoiceTypeException 
     */
    GnucashEmployee getEmployee() throws WrongInvoiceTypeException;
	
    // ---------------------------------------------------------------

    GnucashEmployeeVoucherEntry getEntryById(String id) throws WrongInvoiceTypeException;

    Collection<GnucashEmployeeVoucherEntry> getEntries() throws WrongInvoiceTypeException;

    void addEntry(final GnucashEmployeeVoucherEntry entry);

}
