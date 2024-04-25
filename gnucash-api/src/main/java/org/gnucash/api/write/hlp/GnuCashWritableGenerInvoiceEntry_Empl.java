package org.gnucash.api.write.hlp;

import org.gnucash.api.read.TaxTableNotFoundException;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Empl {

    void setEmplVchPrice(String price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setEmplVchPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

    void setEmplVchPriceFormatted(String price)
	    throws TaxTableNotFoundException,
	    IllegalTransactionSplitActionException;

}
