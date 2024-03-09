package org.gnucash.api.read.hlp;

import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Cust {
    /**
     * @return what the customer must still pay (incl. taxes)
     */
    FixedPointNumber getCustInvcAmountUnpaidWithTaxes();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    FixedPointNumber getCustInvcAmountPaidWithTaxes();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    FixedPointNumber getCustInvcAmountPaidWithoutTaxes();

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     */
    FixedPointNumber getCustInvcAmountWithTaxes();

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     */
    FixedPointNumber getCustInvcAmountWithoutTaxes();

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     */
    String getCustInvcAmountUnpaidWithTaxesFormatted();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    String getCustInvcAmountPaidWithTaxesFormatted();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    String getCustInvcAmountPaidWithoutTaxesFormatted();

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     */
    String getCustInvcAmountWithTaxesFormatted();

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     */
    String getCustInvcAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    /**
     *
     * @return For a customer invoice: How much sales-taxes are to pay.
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getCustInvcTaxes();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isCustInvcFullyPaid();

    /**
     * @return
     */
    boolean isNotCustInvcFullyPaid();

}
