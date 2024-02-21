package org.gnucash.api.read;

import java.util.Collection;
import java.util.Locale;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnucashObject;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * A customer that can issue jobs and receive invoices by us
 * (and hopefully pay them).
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-customers1.html">GnuCash manual</a>
 *
 * @see GnucashCustomerJob
 * @see GnucashCustomerInvoice
 * 
 * @see GnucashEmployee
 * @see GnucashVendor
 */
public interface GnucashCustomer extends GnucashObject {

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashFile getGnucashFile();

    // ------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    /**
     *
     * @return the user-assigned number of this customer (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the name of the customer
     */
    String getName();

    /**
     * @return the address including the name
     */
    GCshAddress getAddress();

    /**
     * @return the shipping-address including the name
     */
    GCshAddress getShippingAddress();

    /**
     *
     * @return The customer-specific discount
     */
    FixedPointNumber getDiscount();

    /**
     *
     * @return the customer-specific credit
     */
    FixedPointNumber getCredit();

    /**
     * @return user-defined notes about the customer (may be null)
     */
    String getNotes();

    // ------------------------------------------------------------

    /**
     * The id of the default tax table to use with this customer (may be null).
     * @return 
     * 
     * @see {@link #getTaxTable()}
     */
    GCshID getTaxTableID();

    /**
     * The default tax table to use with this customer (may be null).
     * @return 
     * 
     * @see {@link #getTaxTableID()}
     */
    GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

    /**
     * The id of the default terms to use with this customer (may be null).
     * @return 
     * 
     * @see {@link #getTaxTable()}
     */
    GCshID getTermsID();

    /**
     * The default terms to use with this customer (may be null).
     * @return 
     * 
     * @see {@link #getTaxTableID()}
     */
    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     */
    int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getIncomeGenerated(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getIncomeGenerated_direct() throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getIncomeGenerated_viaAllJobs() throws UnknownAccountTypeException;

    /**
     * @param readVar 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see {@link #getIncomeGenerated(org.gnucash.api.read.GnucashGenerInvoice.ReadVariant)}
     */
    String getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see {@link #getIncomeGenerated(org.gnucash.api.read.GnucashGenerInvoice.ReadVariant)}
     */
    String getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getOutstandingValue(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getOutstandingValue_direct() throws UnknownAccountTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getOutstandingValue_viaAllJobs() throws UnknownAccountTypeException;

    /**
     * @param readVar 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see {@link #getOutstandingValue(org.gnucash.api.read.GnucashGenerInvoice.ReadVariant)}
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see {@link #getOutstandingValue(org.gnucash.api.read.GnucashGenerInvoice.ReadVariant)}
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException;

    // ------------------------------------------------------------

    /**
     * @return the UNMODIFIABLE collection of jobs that have this customer associated 
     *         with them.
     * @throws WrongInvoiceTypeException
     */
    Collection<GnucashCustomerJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    Collection<GnucashGenerInvoice>    getInvoices() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashCustomerInvoice> getPaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashJobInvoice>      getPaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashCustomerInvoice> getUnpaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashJobInvoice>      getUnpaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

}
