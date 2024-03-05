package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Cust {

    /**
     * @return For a customer invoice, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcPrice() throws WrongInvoiceTypeException;

    /**
     * @return As ${@link #getCustInvcPrice()}, but formatted.
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcPriceFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return true if any sales-tax applies at all to this item.
     * @throws WrongInvoiceTypeException
     */
    boolean isCustInvcTaxable() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcApplicableTaxPercent() throws WrongInvoiceTypeException;

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcApplicableTaxPercentFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * This is the customer invoice sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getCustInvcSum() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcSumInclTaxes() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcSumExclTaxes() throws WrongInvoiceTypeException;

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getCustInvcSumFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcSumInclTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcSumExclTaxesFormatted() throws WrongInvoiceTypeException;

}
