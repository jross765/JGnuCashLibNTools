package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Empl {
    
    /**
     * @return For an employee voucher, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     */
    FixedPointNumber getEmplVchPrice();

    /**
     * @return As ${@link #getEmplVchPrice()}, but formatted.
     */
    String getEmplVchPriceFormatted();

    // ---------------------------------------------------------------

    /**
     * @return true if any sales-tax applies at all to this item.
     */
    boolean isEmplVchTaxable();

    /**
     * @return
     * @throws TaxTableNotFoundException
     */
    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    /**
     * @return e.g. "0.16" for "16%"
     */
    FixedPointNumber getEmplVchApplicableTaxPercent();

    /**
     * @return never null, "0%" if no taxtable is there
     */
    String getEmplVchApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /**
     * This is the employee voucher sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getEmplVchSum();

    /**
     * @return count*single-unit-price including taxes.
     */
    FixedPointNumber getEmplVchSumInclTaxes();

    /**
     * @return count*single-unit-price excluding taxes.
     */
    FixedPointNumber getEmplVchSumExclTaxes();

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.

     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getEmplVchSumFormatted();

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     */
    String getEmplVchSumInclTaxesFormatted();

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     */
    String getEmplVchSumExclTaxesFormatted();

}
