package org.gnucash.api.read.hlp;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashGenerInvoiceEntry_Empl {
    
    /**
     * @return For an employee voucher, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchPrice() throws WrongInvoiceTypeException;

    /**
     * @return As ${@link #getEmplVchPrice()}, but formatted.
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchPriceFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return true if any sales-tax applies at all to this item.
     * @throws WrongInvoiceTypeException
     */
    boolean isEmplVchTaxable() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchApplicableTaxPercent() throws WrongInvoiceTypeException;

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchApplicableTaxPercentFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * This is the employee voucher sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getEmplVchSum() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchSumInclTaxes() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getEmplVchSumExclTaxes() throws WrongInvoiceTypeException;

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getEmplVchSumFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchSumInclTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    String getEmplVchSumExclTaxesFormatted() throws WrongInvoiceTypeException;

}
