package org.gnucash.api.read.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoiceEntry_Job {

    FixedPointNumber getJobInvcPrice();

    String getJobInvcPriceFormatted();

    // ---------------------------------------------------------------
    
    GCshAcctID getJobInvcAccountID() throws AccountNotFoundException;

    // ---------------------------------------------------------------

    boolean isJobInvcTaxable();

    public GCshTaxTable getJobInvcTaxTable() throws TaxTableNotFoundException;

    // ---------------------------------------------------------------

    FixedPointNumber getJobInvcApplicableTaxPercent();

    String getJobInvcApplicableTaxPercentFormatted();

    // ---------------------------------------------------------------

    /*
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     */
    FixedPointNumber getJobInvcSum();

    FixedPointNumber getJobInvcSumInclTaxes();

    FixedPointNumber getJobInvcSumExclTaxes();

    // ----------------------------

    String getJobInvcSumFormatted();

    String getJobInvcSumInclTaxesFormatted();

    String getJobInvcSumExclTaxesFormatted();

}
