package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Empl {
    /**
     * @return what the employee is yet to receive (incl. taxes)
     */
    FixedPointNumber getEmplVchAmountUnpaidWithTaxes();

    /**
     * @return what the employee has already received (incl. taxes)
     */
    FixedPointNumber getEmplVchAmountPaidWithTaxes();

    /**
     * @return what the employee has already received (incl. taxes)
     */
    FixedPointNumber getEmplVchAmountPaidWithoutTaxes();

    /**
     * @return what the employee receives in total (incl. taxes)
     */
    FixedPointNumber getEmplVchAmountWithTaxes();

    /**
     * @return what the employee receives in total (excl. taxes)
     */
    FixedPointNumber getEmplVchAmountWithoutTaxes();

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the employee is still to receive (incl. taxes)
     */
    String getEmplVchAmountUnpaidWithTaxesFormatted();

    /**
     * @return what the employee already has received (incl. taxes)
     */
    String getEmplVchAmountPaidWithTaxesFormatted();

    /**
     * @return what the employee already has received (incl. taxes)
     */
    String getEmplVchAmountPaidWithoutTaxesFormatted();

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the employee will receive in in total (incl. taxes)
     */
    String getEmplVchAmountWithTaxesFormatted();

    /**
     * @return what the employee will receive in total (excl. taxes)
     */
    String getEmplVchAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    /**
     *
     * @return For a vendor bill: How much sales-taxes are to pay.
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getEmplVchTaxes();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isEmplVchFullyPaid();

    /**
     * @return
     */
    boolean isNotEmplVchFullyPaid();

}
