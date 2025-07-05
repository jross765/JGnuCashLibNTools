package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Empl {
    
    FixedPointNumber getEmplVchPrice();

    String getEmplVchPriceFormatted();

    // ---------------------------------------------------------------
    
    GCshID getEmplVchAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isEmplVchTaxable();

    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    FixedPointNumber getEmplVchApplicableTaxPercent();

    String getEmplVchApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /*
     * This is the employee voucher sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getEmplVchSum();

    FixedPointNumber getEmplVchSumInclTaxes();

    FixedPointNumber getEmplVchSumExclTaxes();

    // ----------------------------

    String getEmplVchSumFormatted();

    String getEmplVchSumInclTaxesFormatted();

    String getEmplVchSumExclTaxesFormatted();

}
