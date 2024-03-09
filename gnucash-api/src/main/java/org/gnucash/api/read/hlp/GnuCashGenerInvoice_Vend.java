package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Vend {
    /**
     * @return what the vendor is yet to receive (incl. taxes)
     */
    FixedPointNumber getVendBllAmountUnpaidWithTaxes();

    /**
     * @return what the vendor has already received (incl. taxes)
     */
    FixedPointNumber getVendBllAmountPaidWithTaxes();

    /**
     * @return what the vendor has already received (incl. taxes)
     */
    FixedPointNumber getVendBllAmountPaidWithoutTaxes();

    /**
     * @return what the vendor receives in total (incl. taxes)
     */
    FixedPointNumber getVendBllAmountWithTaxes();

    /**
     * @return what the vendor receives in total (excl. taxes)
     */
    FixedPointNumber getVendBllAmountWithoutTaxes();

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the vendor is still to receive (incl. taxes)
     */
    String getVendBllAmountUnpaidWithTaxesFormatted();

    /**
     * @return what the vendor already has received (incl. taxes)
     */
    String getVendBllAmountPaidWithTaxesFormatted();

    /**
     * @return what the vendor already has received (incl. taxes)
     */
    String getVendBllAmountPaidWithoutTaxesFormatted();

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the vendor will receive in total (incl. taxes)
     */
    String getVendBllAmountWithTaxesFormatted();

    /**
     * @return what the vendor will receive in total (excl. taxes)
     */
    String getVendBllAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    /**
     *
     * @return For a vendor bill: How much sales-taxes are to pay.
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getVendBllTaxes();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isVendBllFullyPaid();

    /**
     * @return
     */
    boolean isNotVendBllFullyPaid();

}
