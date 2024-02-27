package org.gnucash.api.write.hlp;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;

public interface GnucashWritableGenerInvoice_Empl {

    void setEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @return 
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * @throws IllegalArgumentException
     * 
     */
    GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
    		GnucashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * @throws IllegalArgumentException
     * 
     */
    GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
    		GnucashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

    /**
     * create and add a new entry.<br/>
     * @param acct 
     * @param singleUnitPrice 
     * @param quantity 
     * @param taxTab 
     *
     * @return an entry using the given Tax-Table
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * @throws IllegalTransactionSplitActionException
     * @throws IllegalArgumentException
     * 
     */
    GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
    		GnucashAccount acct,
    		FixedPointNumber singleUnitPrice,
    		FixedPointNumber quantity,
    		GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException,
	    NumberFormatException, InvalidCmdtyCurrTypeException;

}
