package org.gnucash.api.write.hlp;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashWritableGenerInvoiceEntry_Empl {

    void setEmplVchPrice(String price)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setEmplVchPrice(FixedPointNumber price)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

    void setEmplVchPriceFormatted(String price)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NumberFormatException,
	    IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException, IllegalArgumentException;

}
