package org.gnucash.api.read.spec.hlp;

import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface SpecInvoiceCommon {

    public FixedPointNumber getAmountUnpaidWithTaxes();

    public FixedPointNumber getAmountPaidWithTaxes();

    public FixedPointNumber getAmountPaidWithoutTaxes();

    public FixedPointNumber getAmountWithTaxes();
    
    public FixedPointNumber getAmountWithoutTaxes();

    // ----------------------------

    public String getAmountUnpaidWithTaxesFormatted();

    public String getAmountPaidWithTaxesFormatted();

    public String getAmountPaidWithoutTaxesFormatted();

    public String getAmountWithTaxesFormatted();

    public String getAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    public boolean isFullyPaid();

    public boolean isNotFullyPaid();

}
