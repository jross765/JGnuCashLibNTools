package org.gnucash.api.read.hlp;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Job {
    
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes();

    FixedPointNumber getJobInvcAmountPaidWithTaxes();

    FixedPointNumber getJobInvcAmountPaidWithoutTaxes();

    FixedPointNumber getJobInvcAmountWithTaxes();

    FixedPointNumber getJobInvcAmountWithoutTaxes();

    // ---------------------------------------------------------------

    String getJobInvcAmountUnpaidWithTaxesFormatted();

    String getJobInvcAmountPaidWithTaxesFormatted();

    String getJobInvcAmountPaidWithoutTaxesFormatted();

    String getJobInvcAmountWithTaxesFormatted();

    String getJobInvcAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    boolean isJobInvcFullyPaid();

    boolean isNotJobInvcFullyPaid();

}
