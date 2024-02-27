package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;

/**
 * Customer invoice that can be modified if {@link #isModifiable()} returns true
 * 
 * @see GnucashCustomerInvoice
 * 
 * @see GnucashWritableEmployeeVoucher
 * @see GnucashWritableVendorBill
 * @see GnucashWritableJobInvoice
 */
public interface GnucashWritableCustomerInvoice extends GnucashWritableGenerInvoice {

    GnucashWritableCustomerInvoiceEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are invoices for this customer.<br/>
     * 
     * @param cust the customer to whom we send an invoice to
     * @throws WrongInvoiceTypeException
     */
    void setCustomer(GnucashCustomer cust) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    GnucashWritableCustomerInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableCustomerInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableCustomerInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------
    
    void post(GnucashAccount incomeAcct,
	      GnucashAccount receivableAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

}
