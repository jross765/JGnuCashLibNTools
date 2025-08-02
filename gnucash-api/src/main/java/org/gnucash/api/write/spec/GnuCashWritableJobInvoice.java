package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Job invoice that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnuCashJobInvoice
 * 
 * @see GnuCashWritableCustomerInvoice
 * @see GnuCashWritableEmployeeVoucher
 * @see GnuCashWritableVendorBill
 */
public interface GnuCashWritableJobInvoice extends GnuCashWritableGenerInvoice,
												   GnuCashJobInvoice,
												   GnuCashWritableObject
{

    GnuCashWritableJobInvoiceEntry getWritableEntryByID(GCshGenerInvcEntrID entrID);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the customer/vendor job that we link this invoice to.
     *
     * @see #getGenerJob()
     */
    void setGenerJob(GnuCashGenerJob job);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the customer job that we link this invoice to.
     *
     * @see #getGenerJob()
     * @see #getCustJob()
     */
    void setCustJob(GnuCashCustomerJob job);

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the vendor job that we link this invoice to.
     *
     * @see #getGenerJob()
     * @see #getVendJob()
     */
    void setVendJob(GnuCashVendorJob job);

    // ---------------------------------------------------------------

    GnuCashWritableJobInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    GnuCashWritableJobInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    GnuCashWritableJobInvoiceEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------
    
    void post(GnuCashAccount incExpAcct,
	      GnuCashAccount recvblPayablAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException;

}
