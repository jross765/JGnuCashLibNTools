package org.gnucash.api.read.hlp;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashGenerInvoice_Empl {
    /**
     * @return what the employee is yet to receive (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getEmplVchAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the employee has already received (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getEmplVchAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the employee has already received (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the employee receives in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchAmountWithTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the employee receives in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchAmountWithoutTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the employee is still to receive (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getEmplVchAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the employee already has received (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getEmplVchAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the employee already has received (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the employee will receive in in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * @return what the employee will receive in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return For a vendor bill: How much sales-taxes are to pay.
     * @throws WrongInvoiceTypeException
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getEmplVchTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    boolean isEmplVchFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    boolean isNotEmplVchFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

}
