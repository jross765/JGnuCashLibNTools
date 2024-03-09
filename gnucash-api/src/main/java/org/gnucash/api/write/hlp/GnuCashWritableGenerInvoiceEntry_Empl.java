package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Empl {

    void setEmplVchPrice(String price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setEmplVchPrice(FixedPointNumber price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

    void setEmplVchPriceFormatted(String price)
	    throws TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException;

}
