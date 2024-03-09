package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  @see GnuCashEmployeeVoucherEntryImpl
 *  @see GnuCashVendorBillEntryImpl
 *  @see GnuCashJobInvoiceEntryImpl
 *  @see GnuCashGenerInvoiceEntryImpl
 */
public class GnuCashCustomerInvoiceEntryImpl extends GnuCashGenerInvoiceEntryImpl
                                             implements GnuCashCustomerInvoiceEntry 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCustomerInvoiceEntryImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashCustomerInvoiceEntryImpl(final GnuCashCustomerInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);
	}

	@SuppressWarnings("exports")
	public GnuCashCustomerInvoiceEntryImpl(final GnuCashGenerInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invoice.getType() != GCshOwner.Type.CUSTOMER )
			throw new WrongInvoiceTypeException();
	}

	@SuppressWarnings("exports")
	public GnuCashCustomerInvoiceEntryImpl(final GncGncEntry peer, final GnuCashFileImpl gcshFile) {
		super(peer, gcshFile, true);
	}

	public GnuCashCustomerInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( entry.getType() != GCshOwner.Type.CUSTOMER )
			throw new WrongInvoiceTypeException();
	}

	public GnuCashCustomerInvoiceEntryImpl(final GnuCashCustomerInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
	}

	// ---------------------------------------------------------------

	public GCshID getInvoiceID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashCustomerInvoice getInvoice() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.CUSTOMER )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException("No customer invoice with id '" + getInvoiceID()
						+ "' for invoice entry with id '" + getID() + "'");
			}
		}

		return new GnuCashCustomerInvoiceImpl(myInvoice);
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getPrice() {
		return getCustInvcPrice();
	}

	@Override
	public String getPriceFormatted() {
		return getCustInvcPriceFormatted();
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

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashCustomerInvoiceEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", invoice-id=");
		buffer.append(getInvoiceID());

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
