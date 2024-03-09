package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Customer invoice that can be modified if {@link #isModifiable()} returns true
 * 
 * @see GnuCashCustomerInvoice
 * 
 * @see GnuCashWritableEmployeeVoucher
 * @see GnuCashWritableVendorBill
 * @see GnuCashWritableJobInvoice
 */
public interface GnuCashWritableCustomerInvoice extends GnuCashWritableGenerInvoice {

    GnuCashWritableCustomerInvoiceEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are invoices for this customer.<br/>
     * 
     * @param cust the customer to whom we send an invoice to
* 
     */
    void setCustomer(GnuCashCustomer cust);

    // ---------------------------------------------------------------

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException;

    // ---------------------------------------------------------------
    
    void post(GnuCashAccount incomeAcct,
	      GnuCashAccount receivableAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongOwnerTypeException, NumberFormatException, IllegalTransactionSplitActionException;

}
