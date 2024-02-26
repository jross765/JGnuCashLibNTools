package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.hlp.GnucashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * An employee that can hand in expense vouchers and, obviously, receive
 * a salary
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-emply.html">GnuCash manual</a>
 *
 * @see GnucashEmployeeVoucher
 * 
 * @see GnucashCustomer
 * @see GnucashVendor
 */
public interface GnucashEmployee extends GnucashObject,
										 HasUserDefinedAttributes
{
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
     *  
     */
    int getNofOpenVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getExpensesGenerated() throws UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted() throws UnknownAccountTypeException;

    /**
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(Locale lcl) throws UnknownAccountTypeException;

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue() throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue_direct() throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted() throws UnknownAccountTypeException, WrongInvoiceTypeException;

    /**
     *
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl) throws UnknownAccountTypeException, WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    List<GnucashGenerInvoice>    getVouchers() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    List<GnucashEmployeeVoucher> getPaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    List<GnucashEmployeeVoucher> getUnpaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException;

}
