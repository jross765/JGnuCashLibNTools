package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;

/**
 * Vendor bill that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnucashVendorBill
 * 
 * @see GnucashWritableCustomerInvoice
 * @see GnucashWritableEmployeeVoucher
 * @see GnucashWritableJobInvoice
 */
public interface GnucashWritableVendorBill extends GnucashWritableGenerInvoice {

    GnucashWritableVendorBillEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are bills for this vendor.<br/>
     * 
     * @param vend the vendor who sent an invoice to us.
     * @throws WrongInvoiceTypeException
     */
    void setVendor(GnucashVendor vend) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    GnucashWritableVendorBillEntry createEntry(
	    GnucashAccount acct, 
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableVendorBillEntry createEntry(
	    GnucashAccount acct, 
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableVendorBillEntry createEntry(
	    GnucashAccount acct, 
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------
    
    void post(final GnucashAccount expensesAcct,
	      final GnucashAccount payablAcct,
	      final LocalDate postDate,
	      final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

}
