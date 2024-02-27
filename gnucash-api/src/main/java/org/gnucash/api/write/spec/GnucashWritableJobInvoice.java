package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;

/**
 * Job invoice that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnucashJobInvoice
 * 
 * @see GnucashWritableCustomerInvoice
 * @see GnucashWritableEmployeeVoucher
 * @see GnucashWritableVendorBill
 */
public interface GnucashWritableJobInvoice extends GnucashWritableGenerInvoice {

    GnucashWritableJobInvoiceEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the customer/vendor job that we link this invoice to.
     * @throws WrongInvoiceTypeException
     */
    void setGenerJob(GnucashGenerJob job) throws WrongInvoiceTypeException;

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the customer job that we link this invoice to.
     * @throws WrongInvoiceTypeException
     */
    void setCustomerJob(GnucashCustomerJob job) throws WrongInvoiceTypeException;

    /**
     * Will throw an IllegalStateException if there are invoices for this job.<br/>
     * 
     * @param job the vendor job that we link this invoice to.
     * @throws WrongInvoiceTypeException
     */
    void setVendorJob(GnucashVendorJob job) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    GnucashWritableJobInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableJobInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableJobInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------
    
    void post(GnucashAccount incExpAcct,
	      GnucashAccount recvblPayablAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

}
