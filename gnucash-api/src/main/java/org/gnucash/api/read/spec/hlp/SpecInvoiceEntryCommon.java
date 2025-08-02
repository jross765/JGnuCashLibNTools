package org.gnucash.api.read.spec.hlp;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.base.basetypes.simple.GCshAcctID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * Methods common to all specialized variants of invoices (and only those).
 *
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
public interface SpecInvoiceEntryCommon extends GnuCashGenerInvoiceEntry {
	
	GCshAcctID getAccountID() throws AccountNotFoundException;

	GnuCashAccount getAccount() throws AccountNotFoundException;

	// ---------------------------------------------------------------

	FixedPointNumber getPrice();

	String getPriceFormatted();

	// ---------------------------------------------------------------

	boolean isTaxable();

	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException;

	// ----------------------------

	FixedPointNumber getApplicableTaxPercent();

	String getApplicableTaxPercentFormatted();

	// ---------------------------------------------------------------

	FixedPointNumber getSum();

	FixedPointNumber getSumInclTaxes();

	FixedPointNumber getSumExclTaxes();

	// ----------------------------

	String getSumFormatted();

	String getSumInclTaxesFormatted();

	String getSumExclTaxesFormatted();

}
