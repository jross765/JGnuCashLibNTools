package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Cust {

    void setCustInvcPrice(String price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException;

    void setCustInvcPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException;

    void setCustInvcPriceFormatted(String price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------

    /**
     * @param val
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     */
    void setCustInvcTaxable(boolean val)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException;

    /**
     * @param tax the new tax table to use. Null sets isTaxable to false.
     * @throws InvalidCmdtyCurrTypeException
     * @throws TaxTableNotFoundException
     */
    void setCustInvcTaxTable(GCshTaxTable tax) throws InvalidCmdtyCurrTypeException,
	    TaxTableNotFoundException;

}
