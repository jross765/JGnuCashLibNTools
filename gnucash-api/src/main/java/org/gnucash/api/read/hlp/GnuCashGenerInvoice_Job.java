package org.gnucash.api.read.hlp;

import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Job {
    
    /**
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getJobInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcAmountWithTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcAmountWithoutTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getJobInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getJobInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    boolean isJobInvcFullyPaid() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    boolean isNotInvcJobFullyPaid() throws WrongInvoiceTypeException;

}
