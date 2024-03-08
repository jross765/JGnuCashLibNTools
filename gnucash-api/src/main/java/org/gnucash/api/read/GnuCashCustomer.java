package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * A customer that can issue jobs and receive invoices by us
 * (and hopefully pay them).
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-customers1.html">GnuCash manual</a>
 *
 * @see GnuCashCustomerJob
 * @see GnuCashCustomerInvoice
 * 
 * @see GnuCashEmployee
 * @see GnuCashVendor
 */
public interface GnuCashCustomer extends GnuCashObject,
                                         HasUserDefinedAttributes
{
    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    /**
     * @return Returns the user-assigned number of this customer (may contain non-digits)
     */
    String getNumber();

    /**
     * @return Returns the name of the customer
     */
    String getName();

    /**
     * @return Returns the address of this customer including its name
     */
    GCshAddress getAddress();

    /**
     * @return Returns the shipping-address of this customer including the name
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
     * @return 
     * @returns Returns the ID of the default tax table to use with this customer (may be null). 
     * 
     * @see #getTaxTable()
     */
    GCshID getTaxTableID();

    /**
     * @returns Returns The default tax table to use with this customer (may be null). 
     * 
     * @see #getTaxTableID()
     */
    @SuppressWarnings("javadoc")
	GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

    /**
     * @return Returns the id of the default terms to use with this customer (may be null). 
     * 
     * @see #getTaxTable()
     */
    GCshID getTermsID();

    /**
     * @return Returns the default terms to use with this customer (may be null). 
     * 
     * @see #getTaxTableID()
     */
    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * @return Returns the current number of Unpaid invoices.
     *         The date is not checked so invoices that have entered payments in the future are
     *         considered paid.
     * @throws WrongInvoiceTypeException
     */
    int getNofOpenInvoices() throws WrongInvoiceTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return Returns the sum of payments for invoices to this customer
     * 
     * @see #getIncomeGenerated_direct()
     * @see #getIncomeGenerated_viaAllJobs()
     *  
     */
    FixedPointNumber getIncomeGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGenerated_viaAllJobs()
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    FixedPointNumber getIncomeGenerated_direct();

    /**
     * @return Returns the sum of payments for invoices to this customer
     *  
     * @see #getIncomeGenerated_direct()
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    FixedPointNumber getIncomeGenerated_viaAllJobs();

    /**
     * @param readVar 
     * @return  
     *  
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    FixedPointNumber getOutstandingValue_direct();

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     */
    FixedPointNumber getOutstandingValue_viaAllJobs();

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // ------------------------------------------------------------

    /**
     * @return Returns the <strong>unmodifiable</strong> collection of jobs that have this 
     *         customer associated with them.
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashCustomerJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return Returns all invoices sent to this customer, both with
     *         and without job, both paid and unpaid.
     * @throws WrongInvoiceTypeException
     * 
     * @see #getPaidInvoices_direct()
     * @see #getPaidInvoices_viaAllJobs()
     */
    List<GnuCashGenerInvoice>    getInvoices() throws WrongInvoiceTypeException;

    /**
     * @return Returns all paid invoices sent to this customer (only those
     *         that were assigned to a job).

     * @throws WrongInvoiceTypeException
     * 
     * @see #getUnpaidInvoices_direct()
     * @see #getPaidInvoices_viaAllJobs()
     * @see #getInvoices()
     */
    List<GnuCashCustomerInvoice> getPaidInvoices_direct() throws WrongInvoiceTypeException;

    /**
     * @return Returns all paid invoices sent to this customer (only those
     *         that were assigned directly to that customer, not to a job).
     *         
     * @throws WrongInvoiceTypeException
     * 
     * @see #getUnpaidInvoices_viaAllJobs()
     * @see #getPaidInvoices_direct()
     * @see #getInvoices()
     */
    List<GnuCashJobInvoice>      getPaidInvoices_viaAllJobs() throws WrongInvoiceTypeException;

    /**
     * @return Returns all unpaid invoices sent to this customer (only those
     *         that were assigned to a job).
     *         
     * @throws WrongInvoiceTypeException
     * 
     * @see #getPaidInvoices_direct()
     * @see #getUnpaidInvoices_viaAllJobs()
     * @see #getInvoices()
     */
    List<GnuCashCustomerInvoice> getUnpaidInvoices_direct() throws WrongInvoiceTypeException;

    /**
     * @return Returns all unpaid invoices sent to this customer (only those
     *         that were assigned directly to that customer, not to a job).
     *         
     * @throws 
     * @throws WrongInvoiceTypeException
     * 
     * @see #getPaidInvoices_viaAllJobs()
     * @see #getUnpaidInvoices_direct()
     * @see #getInvoices()
     */
    List<GnuCashJobInvoice>      getUnpaidInvoices_viaAllJobs() throws WrongInvoiceTypeException;

}
