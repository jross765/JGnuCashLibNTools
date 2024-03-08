package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * An employee that can hand in expense vouchers and, obviously, receive
 * a salary
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-emply.html">GnuCash manual</a>
 *
 * @see GnuCashEmployeeVoucher
 * 
 * @see GnuCashCustomer
 * @see GnuCashVendor
 */
public interface GnuCashEmployee extends GnuCashObject,
										 HasUserDefinedAttributes
{
    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    /**
     * @return Returns the user-assigned number of this employee (may contain non-digits)
     */
    String getNumber();

    /**
     * @return Returns the user name of the employee
     */
    String getUserName();

    /**
     * @return Returns the address of this employee including his/her name
     *         (as opposed to the user name)
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

    /**
     * 
     * 
     * @return Returns the current number of Unpaid vouchers from this employee.
     *         The date is not checked so invoiced that have entered payments in the future are
     *         considered Paid.
     * @throws WrongInvoiceTypeException
     */
    int getNofOpenVouchers() throws WrongInvoiceTypeException;

    // -------------------------------------

    /**
     * @return the sum of payments for invoices to this client
     */
    FixedPointNumber getExpensesGenerated();

    /**
     * @return the sum of payments for invoices to this client
     */
    FixedPointNumber getExpensesGenerated_direct();

    /**
     * @return 
     *  
     * @see #getExpensesGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted();

    /**
     * @param lcl 
     * @return 
     *  
     * @see #getExpensesGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getExpensesGeneratedFormatted(Locale lcl);

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue() throws WrongInvoiceTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     *  
     */
    FixedPointNumber getOutstandingValue_direct() throws WrongInvoiceTypeException;

    /**
     * @return 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted() throws WrongInvoiceTypeException;

    /**
     *
     * @param lcl 
     * @return 
     * @throws WrongInvoiceTypeException
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl) throws WrongInvoiceTypeException;

    // ------------------------------------------------------------

    /**
     * @return Returns all vouchers sent to this employee, 
     *         both paid and unpaid.
     *         
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashGenerInvoice>    getVouchers() throws WrongInvoiceTypeException;

    /**
     * @return Returns all paid vouchers sent from this employee.

     * @throws WrongInvoiceTypeException
     */
    List<GnuCashEmployeeVoucher> getPaidVouchers() throws WrongInvoiceTypeException;

    /**
     * @return Returns all unpaid vouchers sent from this employee.
     * @throws WrongInvoiceTypeException
     */
    List<GnuCashEmployeeVoucher> getUnpaidVouchers() throws WrongInvoiceTypeException;

}
