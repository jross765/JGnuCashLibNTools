package org.gnucash.api.read.hlp;

import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Job {
    
    /**
     * @return what the customer must still pay (incl. taxes)
     */
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    FixedPointNumber getJobInvcAmountPaidWithTaxes();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    FixedPointNumber getJobInvcAmountPaidWithoutTaxes();

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     */
    FixedPointNumber getJobInvcAmountWithTaxes();

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     */
    FixedPointNumber getJobInvcAmountWithoutTaxes();

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     */
    String getJobInvcAmountUnpaidWithTaxesFormatted();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    String getJobInvcAmountPaidWithTaxesFormatted();

    /**
     * @return what the customer has already pay (incl. taxes)
     */
    String getJobInvcAmountPaidWithoutTaxesFormatted();

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     */
    String getJobInvcAmountWithTaxesFormatted();

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     */
    String getJobInvcAmountWithoutTaxesFormatted();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    boolean isJobInvcFullyPaid();

    /**
     * @return
     */
    boolean isNotInvcJobFullyPaid();

}
