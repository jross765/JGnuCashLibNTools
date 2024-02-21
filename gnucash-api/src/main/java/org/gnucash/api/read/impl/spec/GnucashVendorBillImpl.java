package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnucashCustomerInvoiceImpl
 * @see GnucashEmployeeVoucherImpl
 * @see GnucashGenerInvoiceImpl
 */
public class GnucashVendorBillImpl extends GnucashGenerInvoiceImpl
                                   implements GnucashVendorBill,
                                              SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashVendorBillImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnucashVendorBillImpl(final GncGncInvoice peer, final GnucashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnucashVendorBillImpl(final GnucashGenerInvoice invc)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		super(invc.getJwsdpPeer(), invc.getFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_VENDOR
				&& invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnucashVendorBillEntryImpl(entry));
		}

		for ( GnucashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnucashTransactionSplit splt : trx.getSplits() ) {
				GCshID lot = splt.getLotID();
				if ( lot != null ) {
					for ( GnucashGenerInvoice invc1 : splt.getTransaction().getGnucashFile().getGenerInvoices() ) {
						GCshID lotID = invc1.getLotID();
						if ( lotID != null && lotID.equals(lot) ) {
							// Check if it's a payment transaction.
							// If so, add it to the invoice's list of payment transactions.
							if ( splt.getAction() == GnucashTransactionSplit.Action.PAYMENT ) {
								addPayingTransaction(splt);
							}
						} // if lotID
					} // for invc
				} // if lot
			} // for splt
		} // for trx
	}

	// ---------------------------------------------------------------

	@Override
	public GCshID getVendorID() {
		return getOwnerID();
	}

	@Override
	public GnucashVendor getVendor() throws WrongInvoiceTypeException {
		return getVendor_direct();
	}

	public GnucashVendor getVendor_direct() throws WrongInvoiceTypeException {
		if ( !getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnucashGenerInvoice.TYPE_VENDOR.getCode()) )
			throw new WrongInvoiceTypeException();

		GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
		return gcshFile.getVendorByID(ownerID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashVendorBillEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException {
		return new GnucashVendorBillEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnucashVendorBillEntry> getEntries() throws WrongInvoiceTypeException {
		Collection<GnucashVendorBillEntry> castEntries = new HashSet<GnucashVendorBillEntry>();

		for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
				castEntries.add(new GnucashVendorBillEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnucashVendorBillEntry entry) {
		addGenerEntry(entry);
	}

	// -----------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getVendBllAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getVendBllAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		return getVendBllAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException {
		return getVendBllAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException {
		return getVendBllAmountWithoutTaxes();
	}

	@Override
	public String getAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getVendBllAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getVendBllAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getVendBllAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return getVendBllAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getVendBllAmountWithoutTaxesFormatted();
	}

	// ------------------------------

	@Override
	public boolean isFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isVendBllFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isNotVendBllFullyPaid();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchAmountUnpaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchAmountPaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchAmountWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getEmplVchAmountWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getEmplVchAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isCustInvcFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotCustInvcFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isEmplVchFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotEmplVchFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isJobInvcFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotInvcJobFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnucashVendorBillImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", vendor-id=");
		buffer.append(getVendorID());

		buffer.append(", bill-number='");
		buffer.append(getNumber() + "'");

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", #entries=");
		try {
			buffer.append(getEntries().size());
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", date-opened=");
		try {
			buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDateOpened().toLocalDate().toString());
		}

		buffer.append("]");
		return buffer.toString();
	}

}
