package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashJobInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @see GnucashCustomerInvoiceEntryImpl
 * @see GnucashEmployeeVoucherEntryImpl
 * @see GnucashVendorBillEntryImpl
 * @see GnucashGenerInvoiceEntryImpl
 */
public class GnucashJobInvoiceEntryImpl extends GnucashGenerInvoiceEntryImpl
                                        implements GnucashJobInvoiceEntry 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashJobInvoiceEntryImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnucashJobInvoiceEntryImpl(final GnucashJobInvoice invoice, final GncGncEntry peer) {
		super(invoice, peer, true);
	}

	@SuppressWarnings("exports")
	public GnucashJobInvoiceEntryImpl(final GnucashGenerInvoice invoice, final GncGncEntry peer)
			throws WrongInvoiceTypeException {
		super(invoice, peer, true);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invoice.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
	}

	@SuppressWarnings("exports")
	public GnucashJobInvoiceEntryImpl(final GncGncEntry peer, final GnucashFileImpl gcshFile) {
		super(peer, gcshFile, true);
	}

	public GnucashJobInvoiceEntryImpl(final GnucashGenerInvoiceEntry entry) throws WrongInvoiceTypeException {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( entry.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
	}

	public GnucashJobInvoiceEntryImpl(final GnucashJobInvoiceEntry entry) {
		super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
	}

	// ---------------------------------------------------------------

	public GCshID getInvoiceID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnucashJobInvoice getInvoice() throws WrongInvoiceTypeException, IllegalArgumentException {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.JOB )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException(
						"No job invoice with id '" + getInvoiceID() + "' for invoice entry with id '" + getID() + "'");
			}
		}

		return new GnucashJobInvoiceImpl(myInvoice);
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getPrice() throws WrongInvoiceTypeException {
		return getJobInvcPrice();
	}

	@Override
	public String getPriceFormatted() throws WrongInvoiceTypeException {
		return getJobInvcPriceFormatted();
	}

	// ---------------------------------------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcPrice() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcPriceFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchPrice() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchPriceFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllPrice() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllPriceFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnucashJobInvoiceEntryImpl [");

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
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
	}

}
