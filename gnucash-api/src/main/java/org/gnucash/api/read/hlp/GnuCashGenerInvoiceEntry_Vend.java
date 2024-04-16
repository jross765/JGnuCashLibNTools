package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Vend {
  
    /**
     * @return For a vendor bill, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     */
    FixedPointNumber getVendBllPrice();

    /**
     * @return As ${@link #getVendBllPrice()}, but formatted.
     */
    String getVendBllPriceFormatted();

    // ---------------------------------------------------------------
    
    GCshID getVendBllAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    /**
     * @return true if any sales-tax applies at all to this item.
     */
    boolean isVendBllTaxable();

    /**
     * @return
     * @throws TaxTableNotFoundException
     */
    public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     */
    FixedPointNumber getVendBllApplicableTaxPercent();

    /**
     * @return never null, "0%" if no taxtable is there
     */
    String getVendBllApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /**
     * This is the vendor bill sum as entered by the user. The user can decide
     * to include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getVendBllSum();

    /**
     * @return count*single-unit-price including taxes.
     */
    FixedPointNumber getVendBllSumInclTaxes();

    /**
     * @return count*single-unit-price excluding taxes.
     */
    FixedPointNumber getVendBllSumExclTaxes();

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getVendBllSumFormatted();

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     */
    String getVendBllSumInclTaxesFormatted();

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     */
    String getVendBllSumExclTaxesFormatted();

}
