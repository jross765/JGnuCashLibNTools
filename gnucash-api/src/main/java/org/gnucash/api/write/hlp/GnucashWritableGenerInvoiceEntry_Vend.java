package org.gnucash.api.write.hlp;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashWritableGenerInvoiceEntry_Vend {

    void setVendBllPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setVendBllPrice(FixedPointNumber price)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setVendBllPriceFormatted(String price)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    // -----------------------------------------------------------

    /**
     * @param val
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    void setVendBllTaxable(boolean val) throws InvalidCmdtyCurrTypeException, IllegalArgumentException,
	    WrongInvoiceTypeException, TaxTableNotFoundException;

    /**
     * @param tax the new tax table to use. Null sets isTaxable to false.
     * @throws InvalidCmdtyCurrTypeException
     * @throws IllegalArgumentException
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    void setVendBllTaxTable(GCshTaxTable tax) throws WrongInvoiceTypeException, TaxTableNotFoundException,
	    InvalidCmdtyCurrTypeException, IllegalArgumentException;

}
