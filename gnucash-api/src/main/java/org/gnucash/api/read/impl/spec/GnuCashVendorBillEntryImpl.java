package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

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
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @see GnuCashCustomerInvoiceEntryImpl
 * @see GnuCashEmployeeVoucherEntryImpl
 * @see GnuCashJobInvoiceEntryImpl
 * @see GnuCashGenerInvoiceEntryImpl
 */
public class GnuCashVendorBillEntryImpl extends GnuCashGenerInvoiceEntryImpl
                                        implements GnuCashVendorBillEntry 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashVendorBillEntryImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashVendorBillEntryImpl(final GnuCashVendorBill invoice, final GncGncEntry peer) {
		super(invoice, peer, true);
	}

	@SuppressWarnings("exports")
	public GnuCashVendorBillEntryImpl(final GnuCashGenerInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invoice.getType() != GCshOwner.Type.VENDOR )
			throw new WrongInvoiceTypeException();
	}

	@SuppressWarnings("exports")
	public GnuCashVendorBillEntryImpl(final GncGncEntry peer, final GnuCashFileImpl gcshFile) {
		super(peer, gcshFile, true);
	}

	public GnuCashVendorBillEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( entry.getType() != GnuCashGenerInvoice.TYPE_VENDOR )
			throw new WrongInvoiceTypeException();
	}

	public GnuCashVendorBillEntryImpl(final GnuCashVendorBillEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
	}

	// ---------------------------------------------------------------

	public GCshID getBillID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashVendorBill getBill() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.VENDOR )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException(
						"No vendor bill with id '" + getBillID() + "' for bill entry with id '" + getID() + "'");
			}
		}

		return new GnuCashVendorBillImpl(myInvoice);
	}

	// ---------------------------------------------------------------

	@Override
	public GCshID getAccountID() throws AccountNotFoundException {
		return getVendBllAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getPrice() {
		return getVendBllPrice();
	}

	@Override
	public String getPriceFormatted() {
		return getVendBllPriceFormatted();
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

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchPrice() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchPriceFormatted() {
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
		return isVendBllTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return getVendBllTaxTable();
	}

	// ----------------------------
	
	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return getVendBllApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return getVendBllApplicableTaxPercentFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public FixedPointNumber getSum() {
		return getVendBllSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return getVendBllSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return getVendBllSumExclTaxes();
	}

	// ----------------------------
	
	@Override
	public String getSumFormatted() {
		return getVendBllSumFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return getVendBllSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return getVendBllSumExclTaxesFormatted();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashVendorBillEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", bill-id=");
		buffer.append(getBillID());

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
