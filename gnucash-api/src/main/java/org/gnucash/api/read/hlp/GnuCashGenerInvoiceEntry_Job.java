package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Job {
    /**
     * @return For a job invoice, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     */
    FixedPointNumber getJobInvcPrice();

    /**
     * @return As ${@link #getJobInvcPrice()}, but formatted.
     */
    String getJobInvcPriceFormatted();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isJobInvcTaxable();

    /**
     * @return
     * @throws TaxTableNotFoundException
     */
    public GCshTaxTable getJobInvcTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    /**
     * @return e.g. "0.16" for "16%"
     */
    FixedPointNumber getJobInvcApplicableTaxPercent();

    /**
     * @return never null, "0%" if no taxtable is there
     */
    String getJobInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /**
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getJobInvcSum();

    /**
     * @return count*single-unit-price including taxes.
     */
    FixedPointNumber getJobInvcSumInclTaxes();

    /**
     * @return count*single-unit-price excluding taxes.
     */
    FixedPointNumber getJobInvcSumExclTaxes();

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getJobInvcSumFormatted();

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     */
    String getJobInvcSumInclTaxesFormatted();

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     */
    String getJobInvcSumExclTaxesFormatted();

}
