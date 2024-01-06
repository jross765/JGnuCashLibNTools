package org.gnucash.api.read;

import java.util.Collection;
import java.util.Locale;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * An employee that can hand in expense vouchers and, obviously, receive
 * a salary
 *
 * @see GnucashEmployeeVoucher
 */
public interface GnucashEmployee extends GnucashObject {

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
     * @return the user-assigned number of this employee (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the user name of the employee
     */
    String getUserName();

    /**
     * @return the address including the name
     */
    GCshAddress getAddress();

    /**
     * @return user-defined notes about the employee (may be null)
     */
    String getLanguage();

    /**
     * @return user-defined notes about the employee (may be null)
     */
    String getNotes();

    // ------------------------------------------------------------

//    /**
//     * The id of the default tax table to use with this employee (may be null).
//     * 
//     * @see {@link #getTaxTable()}
//     */
//    String getTaxTableID();
//
//    /**
//     * The default tax table to use with this employee (may be null).
//     * 
//     * @see {@link #getTaxTableID()}
//     */
//    GCshTaxTable getTaxTable();

    // ------------------------------------------------------------

//    /**
//     * The id of the default terms to use with this customer (may be null).
//     * 
//     * @see {@link #getTaxTable()}
//     */
//    String getTermsID();
//
//    /**
//     * The default terms to use with this customer (may be null).
//     * 
//     * @see {@link #getTaxTableID()}
//     */
//    GCshBillTerms getTerms();

    // ------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    int getNofOpenVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    // -------------------------------------

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getExpensesGenerated() throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted() throws UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(Locale lcl) throws UnknownAccountTypeException, IllegalArgumentException;

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getOutstandingValue() throws UnknownAccountTypeException, WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     */
    FixedPointNumber getOutstandingValue_direct() throws UnknownAccountTypeException, WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted() throws UnknownAccountTypeException, WrongInvoiceTypeException, IllegalArgumentException;

    /**
     *
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws SecurityException 
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl) throws UnknownAccountTypeException, WrongInvoiceTypeException, IllegalArgumentException;

    // ------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashGenerInvoice>    getVouchers() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashEmployeeVoucher> getPaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    Collection<GnucashEmployeeVoucher> getUnpaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

}
