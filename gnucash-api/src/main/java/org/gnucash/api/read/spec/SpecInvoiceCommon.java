package org.gnucash.api.read.spec;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;

/**
 * This class represents a bill that is sent from a vendor
 * so you know what to pay him/her.<br>
 * <br>
 * Note: The correct business term is "bill" (as opposed to "invoice"), 
 * as used in the GnuCash documentation. However, on a technical level, both 
 * customer invoices and vendor bills are referred to as "GncInvoice" objects.
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the Invoice was
 * created and secondarily on the date it should be paid.
 *
 * @see GnucashGenerJob
 * @see GnucashVendor
 */
public interface SpecInvoiceCommon {

    public FixedPointNumber getAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    public FixedPointNumber getAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;
    
    public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ----------------------------

    public String getAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    public String getAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

    public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

    // ---------------------------------------------------------------

    public boolean isFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

    public boolean isNotFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

}
