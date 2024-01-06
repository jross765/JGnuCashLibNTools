package org.gnucash.api.read.hlp;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashGenerInvoiceEntry_Job {
    /**
     * @return For a job invoice, return the price of one single of the
     *         ${@link #getQuantity()} items of type ${@link #getAction()}.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcPrice() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return As ${@link #getJobInvcPrice()}, but formatted.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcPriceFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     */
    boolean isJobInvcTaxable() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     */
    public GCshTaxTable getJobInvcTaxTable()
	    throws TaxTableNotFoundException, WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     *
     * @return e.g. "0.16" for "16%"
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcApplicableTaxPercent() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcApplicableTaxPercentFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    /**
     * This is the vendor bill sum as entered by the user. The user can decide to
     * include or exclude taxes.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    FixedPointNumber getJobInvcSum() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcSumInclTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    FixedPointNumber getJobInvcSumExclTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ----------------------------

    /**
     * As ${@link #getCustInvcSum()}. but formatted.
     * 
     * @return count*single-unit-price excluding or including taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @see #getCustInvcSumExclTaxes()
     * @see #getCustInvcSumInclTaxes()
     */
    String getJobInvcSumFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * As ${@link #getCustInvcSumInclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price including taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcSumInclTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    /**
     * As ${@link #getCustInvcSumExclTaxes()}. but formatted.
     * 
     * @return count*single-unit-price excluding taxes.
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    String getJobInvcSumExclTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

}
