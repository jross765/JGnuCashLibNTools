package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Cust {

    /**
     * @return For a customer invoice, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     */
    FixedPointNumber getCustInvcPrice();

    /**
     * @return As ${@link #getCustInvcPrice()}, but formatted.
     */
    String getCustInvcPriceFormatted();

    // ---------------------------------------------------------------

    /**
     *
     * @return true if any sales-tax applies at all to this item.
     */
    boolean isCustInvcTaxable();

    /**
     * @return
     * @throws TaxTableNotFoundException
     */
    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     */
    FixedPointNumber getCustInvcApplicableTaxPercent();

    /**
     * @return never null, "0%" if no taxtable is there
     */
    String getCustInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /**
     * This is the customer invoice sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getCustInvcSum();

    /**
     * @return count*single-unit-price including taxes.
     */
    FixedPointNumber getCustInvcSumInclTaxes();

    /**
     * @return count*single-unit-price excluding taxes.
     */
    FixedPointNumber getCustInvcSumExclTaxes();

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getCustInvcSumFormatted();

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     */
    String getCustInvcSumInclTaxesFormatted();

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     */
    String getCustInvcSumExclTaxesFormatted();

}
