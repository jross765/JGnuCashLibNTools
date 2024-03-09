package org.gnucash.api.write.hlp;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GnuCashWritableGenerInvoice_Vend {

    void setVendor(GnuCashVendor vend);

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

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
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBillEntry createVendBllEntry(
    		GnuCashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

}
