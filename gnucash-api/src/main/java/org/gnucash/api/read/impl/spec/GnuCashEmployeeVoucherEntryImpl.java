package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * 
 * @see GnuCashCustomerInvoiceEntryImpl
 * @see GnuCashVendorBillEntryImpl
 * @see GnuCashJobInvoiceEntryImpl
 * @see GnuCashGenerInvoiceEntryImpl
 */
public class GnuCashEmployeeVoucherEntryImpl extends GnuCashGenerInvoiceEntryImpl
                                             implements GnuCashEmployeeVoucherEntry 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashEmployeeVoucherEntryImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashEmployeeVoucherEntryImpl(final GnuCashEmployeeVoucher invoice, final GncGncEntry peer) {
		super(invoice, peer, true);
	}

	@SuppressWarnings("exports")
	public GnuCashEmployeeVoucherEntryImpl(final GnuCashGenerInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invoice.getType() != GCshOwner.Type.EMPLOYEE )
			throw new WrongInvoiceTypeException();
	}

	@SuppressWarnings("exports")
	public GnuCashEmployeeVoucherEntryImpl(final GncGncEntry peer, final GnuCashFileImpl gcshFile) {
		super(peer, gcshFile, true);
	}

	public GnuCashEmployeeVoucherEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( entry.getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE )
			throw new WrongInvoiceTypeException();
	}

	public GnuCashEmployeeVoucherEntryImpl(final GnuCashEmployeeVoucherEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
	}

	// ---------------------------------------------------------------

	public GCshID getVoucherID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashEmployeeVoucher getVoucher() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.EMPLOYEE )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException("No employee voucher with id '" + getVoucherID()
						+ "' for voucher entry with id '" + getID() + "'");
			}
		}

		return new GnuCashEmployeeVoucherImpl(myInvoice);
	}

	// ---------------------------------------------------------------

	@Override
	public GCshID getAccountID() throws AccountNotFoundException {
		return getEmplVchAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getPrice() {
		return getEmplVchPrice();
	}

	@Override
	public String getPriceFormatted() {
		return getEmplVchPriceFormatted();
	}

	// ---------------------------------------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcPriceFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------
	
	@Override
	public boolean isTaxable() {
		return isEmplVchTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return getEmplVchTaxTable();
	}

	// ----------------------------
	
	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return getEmplVchApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return getEmplVchApplicableTaxPercentFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public FixedPointNumber getSum() {
		return getEmplVchSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return getEmplVchSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return getEmplVchSumExclTaxes();
	}

	// ----------------------------
	
	@Override
	public String getSumFormatted() {
		return getEmplVchSumFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return getEmplVchSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return getEmplVchSumExclTaxesFormatted();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashEmployeeVoucherEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", voucher-id=");
		buffer.append(getVoucherID());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", date=");
		try {
			buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDate().toLocalDate().toString());
		}

		buffer.append(", action='");
		try {
			buffer.append(getAction() + "'");
		} catch (Exception e) {
			buffer.append("ERROR" + "'");
		}

		buffer.append(", account-id=");
		try {
			buffer.append(getAccountID());
		} catch (Exception e) {
		    buffer.append("ERROR");
		}
		
		buffer.append(", price=");
		try {
			buffer.append(getPrice());
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
	}

}
