package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Vend {
  
    FixedPointNumber getVendBllPrice();

    String getVendBllPriceFormatted();

    // ---------------------------------------------------------------
    
    GCshID getVendBllAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isVendBllTaxable();

    public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    FixedPointNumber getVendBllApplicableTaxPercent();

    String getVendBllApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getVendBllSum();

    FixedPointNumber getVendBllSumInclTaxes();

    FixedPointNumber getVendBllSumExclTaxes();

    // ----------------------------

    String getVendBllSumFormatted();

    String getVendBllSumInclTaxesFormatted();

    String getVendBllSumExclTaxesFormatted();

}
