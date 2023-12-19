package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;

/**
 * Job invoice that can be modified if isModifiable() returns true
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
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    GnucashWritableJobInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    GnucashWritableJobInvoiceEntry createEntry(
	    GnucashAccount acct, 
	    final FixedPointNumber singleUnitPrice,
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------
    
    void post(final GnucashAccount incExpAcct,
	      final GnucashAccount recvblPayablAcct,
	      final LocalDate postDate,
	      final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, IllegalArgumentException;

}
