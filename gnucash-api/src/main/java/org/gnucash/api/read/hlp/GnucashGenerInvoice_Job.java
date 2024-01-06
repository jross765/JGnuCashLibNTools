package org.gnucash.api.read.hlp;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashGenerInvoice_Job {
    
    /**
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcAmountWithTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcAmountWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    boolean isJobInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws IllegalArgumentException
     */
    boolean isNotInvcJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

}
