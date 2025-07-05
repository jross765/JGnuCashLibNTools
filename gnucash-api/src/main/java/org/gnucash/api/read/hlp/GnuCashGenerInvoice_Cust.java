package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Cust {

    FixedPointNumber getCustInvcAmountUnpaidWithTaxes();


    FixedPointNumber getCustInvcAmountPaidWithTaxes();

    FixedPointNumber getCustInvcAmountPaidWithoutTaxes();

    FixedPointNumber getCustInvcAmountWithTaxes();

    FixedPointNumber getCustInvcAmountWithoutTaxes();

    // ---------------------------------------------------------------

    String getCustInvcAmountUnpaidWithTaxesFormatted();

    String getCustInvcAmountPaidWithTaxesFormatted();

    String getCustInvcAmountPaidWithoutTaxesFormatted();

    String getCustInvcAmountWithTaxesFormatted();

    String getCustInvcAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    GCshTaxedSumImpl[] getCustInvcTaxes();

    // ---------------------------------------------------------------

    boolean isCustInvcFullyPaid();

    boolean isNotCustInvcFullyPaid();

}
