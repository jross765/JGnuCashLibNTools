package org.gnucash.api.write.spec;

import java.time.LocalDate;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;

/**
 * Employee voucher that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnucashEmployeeVoucher
 * 
 * @see GnucashWritableCustomerInvoice
 * @see GnucashWritableVendorBill
 * @see GnucashWritableJobInvoice
 */
public interface GnucashWritableEmployeeVoucher extends GnucashWritableGenerInvoice {

    GnucashWritableEmployeeVoucherEntry getWritableEntryByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     * Will throw an IllegalStateException if there are bills for this employee.<br/>
     * 
     * @param empl the employee who sent an invoice to us.
     * @throws WrongInvoiceTypeException
     */
    void setEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    GnucashWritableEmployeeVoucherEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableEmployeeVoucherEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    GnucashWritableEmployeeVoucherEntry createEntry(
	    GnucashAccount acct, 
	    FixedPointNumber singleUnitPrice,
	    FixedPointNumber quantity, 
	    GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException;

    // ---------------------------------------------------------------
    
    void post(GnucashAccount expensesAcct,
	      GnucashAccount payablAcct,
	      LocalDate postDate,
	      LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

}
