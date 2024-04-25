package org.gnucash.api.write.hlp;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoice_Job {

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity) throws TaxTableNotFoundException,
	    UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTabName 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
			String taxTabName) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoiceEntry createJobInvcEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		GCshTaxTable taxTab) throws  TaxTableNotFoundException, 
    	UnknownInvoiceTypeException, IllegalTransactionSplitActionException;
}
