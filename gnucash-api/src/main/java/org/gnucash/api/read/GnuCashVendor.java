package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * A vendor that can issue jobs and send bills paid by us
 * (and hopefully pay them).
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ap-vendors1.html">GnuCash manual</a>
 *
 * @see GnuCashVendorJob
 * @see GnuCashVendorBill
 * 
 * @see GnuCashCustomer
 * @see GnuCashEmployee
 */
public interface GnuCashVendor extends GnuCashObject,
									   HasUserDefinedAttributes
{
    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    /**
     * @return Returns the user-assigned number of this vendor (may contain non-digits)
     */
    String getNumber();

    /**
     * @return Returns the name of the vendor
     */
    String getName();

    /**
     * @return Returns the address of this vendor including its name
     */
    GCshAddress getAddress();

    /**
     * @return user-defined notes about the vendor (may be null)
     */
    String getNotes();

    // ------------------------------------------------------------

    /**
     * @return 
     * @returns Returns the ID of the default tax table to use with this vendor (may be null). 
     * 
     * @see #getTaxTable()
     */
    GCshID getTaxTableID();

    /**
     * @returns Returns The default tax table to use with this vendor (may be null). 
     * 
     * @see #getTaxTableID()
     */
    @SuppressWarnings("javadoc")
    GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

    /**
     * @return Returns the id of the default terms to use with this vendor (may be null).t
     * 
     * @see #getTaxTable()
     */
    GCshID getTermsID();

    /**
     * @return Returns the default terms to use with this vendor (may be null). 
     * 
     * @see #getTaxTableID()
     */
    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * @return Returns the current number of Unpaid bills.
     *         The date is not checked, so bills that have entered payments in the future are
     *         considered paid.
     * @throws WrongInvoiceTypeException
     */
    int getNofOpenBills() throws WrongInvoiceTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return Returns the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @return Returns the sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_direct();

    /**
     * @return REturn sthe sum of payments for invoices to this vendor
     */
    FixedPointNumber getExpensesGenerated_viaAllJobs();

    /**
     * @param readVar 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar);

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     *  
     * @see #getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl);

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue_direct() throws WrongInvoiceTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue_viaAllJobs() throws WrongInvoiceTypeException;

    /**
     * @param readVar 
     * @return 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException;

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl) throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return Returns the <strong>unmodifiable</strong> collection of jobs that have this 
     *         vendor associated with them.
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashVendorJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return Returns all bills sent to this vendor, both with
     *         and without job, both paid and unpaid.
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashGenerInvoice> getBills() throws WrongInvoiceTypeException;

    /**
     * @return Returns all paid bills sent from this vendor (only those
     *         that were assigned to a job).

     * @throws WrongInvoiceTypeException
     */
    List<GnuCashVendorBill>   getPaidBills_direct() throws WrongInvoiceTypeException;

    /**
     * @return Returns all paid bills sent from this vendor (only those
     *         that were assigned directly to that vendor, not to a job).
     *         
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashJobInvoice>   getPaidBills_viaAllJobs() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashVendorBill>   getUnpaidBills_direct() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashJobInvoice>   getUnpaidBills_viaAllJobs() throws WrongInvoiceTypeException;

}
