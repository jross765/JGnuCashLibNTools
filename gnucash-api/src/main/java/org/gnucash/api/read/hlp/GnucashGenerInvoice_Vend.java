package org.gnucash.api.read.hlp;

import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnucashGenerInvoice_Vend {
    /**
     * @return what the vendor is yet to receive (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    FixedPointNumber getVendBllAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the vendor has already received (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    FixedPointNumber getVendBllAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the vendor has already received (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the vendor receives in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllAmountWithTaxes() throws WrongInvoiceTypeException;

    /**
     * @return what the vendor receives in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getVendBllAmountWithoutTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the vendor is still to receive (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    String getVendBllAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the vendor already has received (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * 
     */
    String getVendBllAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return what the vendor already has received (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getVendBllAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the vendor will receive in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getVendBllAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

    /**
     * @return what the vendor will receive in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     */
    String getVendBllAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     *
     * @return For a vendor bill: How much sales-taxes are to pay.
     * @throws WrongInvoiceTypeException
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getVendBllTaxes() throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    boolean isVendBllFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     */
    boolean isNotVendBllFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException;

}
