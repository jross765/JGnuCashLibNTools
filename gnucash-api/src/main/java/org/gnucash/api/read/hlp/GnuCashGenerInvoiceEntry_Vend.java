package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Vend {
  
    /**
     * @return For a vendor bill, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllPrice() throws WrongInvoiceTypeException;

    /**
     * @return As ${@link #getVendBllPrice()}, but formatted.
     * @throws WrongInvoiceTypeException
     */
    String getVendBllPriceFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return true if any sales-tax applies at all to this item.
     * @throws WrongInvoiceTypeException
     */
    boolean isVendBllTaxable() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllApplicableTaxPercent() throws WrongInvoiceTypeException;

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    String getVendBllApplicableTaxPercentFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * This is the vendor bill sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getVendBllSum() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllSumInclTaxes() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllSumExclTaxes() throws WrongInvoiceTypeException;

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getVendBllSumFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     */
    String getVendBllSumInclTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     */
    String getVendBllSumExclTaxesFormatted() throws WrongInvoiceTypeException;

}
