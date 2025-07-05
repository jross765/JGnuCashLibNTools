package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Vend {

    FixedPointNumber getVendBllAmountUnpaidWithTaxes();

    FixedPointNumber getVendBllAmountPaidWithTaxes();

    FixedPointNumber getVendBllAmountPaidWithoutTaxes();

    FixedPointNumber getVendBllAmountWithTaxes();

    FixedPointNumber getVendBllAmountWithoutTaxes();

    // ---------------------------------------------------------------

    String getVendBllAmountUnpaidWithTaxesFormatted();

    String getVendBllAmountPaidWithTaxesFormatted();

    String getVendBllAmountPaidWithoutTaxesFormatted();

    String getVendBllAmountWithTaxesFormatted();

    String getVendBllAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    GCshTaxedSumImpl[] getVendBllTaxes();

    // ---------------------------------------------------------------

    boolean isVendBllFullyPaid();

    boolean isNotVendBllFullyPaid();

}
