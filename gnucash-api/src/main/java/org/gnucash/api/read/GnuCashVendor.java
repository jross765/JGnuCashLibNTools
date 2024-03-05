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
     *
     * @return the user-assigned number of this vendor (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the name of the vendor
     */
    String getName();

    /**
     * @return the address including the name
     */
    GCshAddress getAddress();

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
     * @throws IllegalArgumentException 
     *  
     */
    int getNofOpenBills() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getExpensesGenerated_viaAllJobs() throws UnknownAccountTypeException;

    /**
     * @param readVar 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     * @see #getExpensesGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException;

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     * @see #getExpensesGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getOutstandingValue_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    FixedPointNumber getOutstandingValue_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param readVar 
     * @return 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, Locale lcl) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ------------------------------------------------------------

    /**
     * @return the UNMODIFIABLE collection of jobs that have this vendor associated
     *         with them.
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashVendorJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     */
    List<GnuCashGenerInvoice> getBills() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    List<GnuCashVendorBill>   getPaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    List<GnuCashJobInvoice>   getPaidBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    List<GnuCashVendorBill>   getUnpaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    List<GnuCashJobInvoice>   getUnpaidBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException;

}
