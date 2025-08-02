package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Customer invoice that can be modified if {@link #isModifiable()} returns true
 * 
 * @see GnuCashCustomerInvoice
 * 
 * @see GnuCashWritableEmployeeVoucher
 * @see GnuCashWritableVendorBill
 * @see GnuCashWritableJobInvoice
 */
public interface GnuCashWritableCustomerInvoice extends GnuCashWritableGenerInvoice,
													    GnuCashCustomerInvoice,
													    GnuCashWritableObject
{

    GnuCashWritableCustomerInvoiceEntry getWritableEntryByID(GCshGenerInvcEntrID entrID);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are invoices for this customer.<br/>
     * 
     * @param cust the customer to whom we send an invoice to
     *
     * @see #getCustomer()
     * @see #getCustomerID()
     */
    void setCustomer(GnuCashCustomer cust);

    // ---------------------------------------------------------------

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    GnuCashWritableCustomerInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------
    
    void post(GnuCashAccount incomeAcct,
	      GnuCashAccount receivableAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException;

}
