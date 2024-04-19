package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Cust {

    void setCustInvcPrice(String price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setCustInvcPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setCustInvcPriceFormatted(String price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------

    /**
     * @param val
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void setCustInvcTaxable(boolean val)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    /**
     * @param tax the new tax table to use. Null sets isTaxable to false.
     * @throws TaxTableNotFoundException
     */
    void setCustInvcTaxTable(GCshTaxTable tax) throws TaxTableNotFoundException;

}
