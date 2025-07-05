package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Empl {

    FixedPointNumber getEmplVchAmountUnpaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithTaxes();

    FixedPointNumber getEmplVchAmountPaidWithoutTaxes();

    FixedPointNumber getEmplVchAmountWithTaxes();

    FixedPointNumber getEmplVchAmountWithoutTaxes();

    // ---------------------------------------------------------------

    String getEmplVchAmountUnpaidWithTaxesFormatted();

    String getEmplVchAmountPaidWithTaxesFormatted();

    String getEmplVchAmountPaidWithoutTaxesFormatted();

    String getEmplVchAmountWithTaxesFormatted();

    String getEmplVchAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    GCshTaxedSumImpl[] getEmplVchTaxes();

    // ---------------------------------------------------------------

    boolean isEmplVchFullyPaid();

    boolean isNotEmplVchFullyPaid();

}
