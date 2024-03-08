package org.gnucash.api.read.hlp;

import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashGenerInvoice_Cust {
    /**
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcAmountWithTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getCustInvcAmountWithoutTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException;

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getCustInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return For a customer invoice: How much sales-taxes are to pay.
     * @throws WrongInvoiceTypeException
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getCustInvcTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    boolean isCustInvcFullyPaid() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    boolean isNotCustInvcFullyPaid()
	    throws WrongInvoiceTypeException;

}
