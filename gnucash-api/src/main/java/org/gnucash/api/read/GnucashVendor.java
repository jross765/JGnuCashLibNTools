package org.gnucash.api.read;

import java.util.Collection;
import java.util.Locale;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * A vendor that can issue jobs and send bills paid by us
 * (and hopefully pay them).
 *
 * @see GnucashVendorJob
 * @see GnucashVendorBill
 */
public interface GnucashVendor extends GnucashObject {

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
     * @throws 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    int getNofOpenBills() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getExpensesGenerated(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getExpensesGenerated_viaAllJobs() throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @param readVar 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getExpensesGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getExpensesGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws UnknownAccountTypeException, IllegalArgumentException;

    // -------------------------------------

    /**
     * @param readVar 
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getOutstandingValue(GnucashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getOutstandingValue_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getOutstandingValue_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @param readVar 
     * @return 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar) throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     *
     * @param readVar 
     * @param lcl 
     * @return 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar, Locale lcl) throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    // ------------------------------------------------------------

    /**
     * @return the UNMODIFIABLE collection of jobs that have this vendor associated
     *         with them.
     * @throws WrongInvoiceTypeException
     */
    Collection<GnucashVendorJob> getJobs() throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashGenerInvoice> getBills() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashVendorBill>   getPaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashJobInvoice>   getPaidBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashVendorBill>   getUnpaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashJobInvoice>   getUnpaidBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

}
