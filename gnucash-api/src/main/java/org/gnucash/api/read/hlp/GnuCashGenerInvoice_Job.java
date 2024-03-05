package org.gnucash.api.read.hlp;

import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Job {
    
    /**
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    FixedPointNumber getJobInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @throws UnknownAccountTypeException
     * 
     */
    String getJobInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    String getJobInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @throws UnknownAccountTypeException
     */
    boolean isJobInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    boolean isNotInvcJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException;

}
