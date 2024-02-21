package org.gnucash.api.write.hlp;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public interface GnucashWritableGenerInvoiceEntry_Job {

    void setJobInvcPrice(String price) throws NumberFormatException, WrongInvoiceTypeException, TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException, InvalidCmdtyCurrTypeException,
	    IllegalArgumentException;

    void setJobInvcPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException,
	    NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException,
	    InvalidCmdtyCurrTypeException;

    void setJobInvcPriceFormatted(String price) throws NumberFormatException, WrongInvoiceTypeException,
	    TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException,
	    InvalidCmdtyCurrTypeException;
}
