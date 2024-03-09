package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Job {

    void setJobInvcPrice(String price) throws NumberFormatException, TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException,
	    IllegalArgumentException;

    void setJobInvcPrice(FixedPointNumber price) throws TaxTableNotFoundException,
	    NumberFormatException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException,
	    InvalidCmdtyCurrTypeException;

    void setJobInvcPriceFormatted(String price) throws NumberFormatException,
	    TaxTableNotFoundException, UnknownInvoiceTypeException, IllegalTransactionSplitActionException,
	    InvalidCmdtyCurrTypeException;
}
