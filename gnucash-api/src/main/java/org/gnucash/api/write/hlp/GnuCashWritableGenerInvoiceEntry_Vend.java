package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Vend {

    void setVendBllPrice(String price) throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setVendBllPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setVendBllPriceFormatted(String price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    // -----------------------------------------------------------

    /**
     * @param val
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException
     */
    void setVendBllTaxable(boolean val) throws TaxTableNotFoundException;

    /**
     * @param tax the new tax table to use. Null sets isTaxable to false.
     * @throws TaxTableNotFoundException
     */
    void setVendBllTaxTable(GCshTaxTable tax) throws TaxTableNotFoundException;

}
