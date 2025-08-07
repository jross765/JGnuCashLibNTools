package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Cust {

    FixedPointNumber getCustInvcPrice();

    String getCustInvcPriceFormatted();

    // ---------------------------------------------------------------
    
    GCshAcctID getCustInvcAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isCustInvcTaxable();

    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    FixedPointNumber getCustInvcApplicableTaxPercent();

    String getCustInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /*
     * This is the customer invoice sum as entered by the user. The user can decide
     * to include or exclude taxes.
     */
    FixedPointNumber getCustInvcSum();

    FixedPointNumber getCustInvcSumInclTaxes();

    FixedPointNumber getCustInvcSumExclTaxes();

    // ----------------------------

    String getCustInvcSumFormatted();

    String getCustInvcSumInclTaxesFormatted();

    String getCustInvcSumExclTaxesFormatted();

}
