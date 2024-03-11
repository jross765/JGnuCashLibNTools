package org.gnucash.api.write.hlp;

import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoiceEntry_Job {

    void setJobInvcPrice(String price) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    void setJobInvcPrice(FixedPointNumber price) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    void setJobInvcPriceFormatted(String price) throws TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;
}
