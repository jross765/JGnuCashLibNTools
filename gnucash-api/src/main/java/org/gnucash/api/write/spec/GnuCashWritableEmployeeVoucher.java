package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Employee voucher that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnuCashEmployeeVoucher
 * 
 * @see GnuCashWritableCustomerInvoice
 * @see GnuCashWritableVendorBill
 * @see GnuCashWritableJobInvoice
 */
public interface GnuCashWritableEmployeeVoucher extends GnuCashWritableGenerInvoice,
														GnuCashEmployeeVoucher,
														GnuCashWritableObject
{

    GnuCashWritableEmployeeVoucherEntry getWritableEntryByID(GCshID entrID);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are bills for this employee.<br/>
     * 
     * @param empl the employee who sent an invoice to us.
     *
     * @see #getEmployee()
     * @see #getEmployeeID()
     */
    void setEmployee(GnuCashEmployee empl);

    // ---------------------------------------------------------------

    GnuCashWritableEmployeeVoucherEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    GnuCashWritableEmployeeVoucherEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    GnuCashWritableEmployeeVoucherEntry createEntry(
	    GnuCashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException;

    // ---------------------------------------------------------------
    
    void post(GnuCashAccount expensesAcct,
	      GnuCashAccount payablAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException;

}
