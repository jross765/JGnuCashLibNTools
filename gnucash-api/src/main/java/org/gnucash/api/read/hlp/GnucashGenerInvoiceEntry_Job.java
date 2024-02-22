package org.gnucash.api.read.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnucashGenerInvoiceEntry_Job {
    /**
     * @return For a job invoice, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcPrice() throws WrongInvoiceTypeException;

    /**
     * @return As ${@link #getJobInvcPrice()}, but formatted.
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcPriceFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     */
    boolean isJobInvcTaxable() throws WrongInvoiceTypeException;

    /**
     * @return
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    public GCshTaxTable getJobInvcTaxTable()
	    throws TaxTableNotFoundException, WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcApplicableTaxPercent() throws WrongInvoiceTypeException;

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcApplicableTaxPercentFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getJobInvcSum() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcSumInclTaxes() throws WrongInvoiceTypeException;

    /**
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     * 
     */
    FixedPointNumber getJobInvcSumExclTaxes() throws WrongInvoiceTypeException;

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * 
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getJobInvcSumFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcSumInclTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     * 
     */
    String getJobInvcSumExclTaxesFormatted() throws WrongInvoiceTypeException;

}
