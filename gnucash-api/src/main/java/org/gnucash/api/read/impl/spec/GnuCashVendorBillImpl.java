package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnuCashCustomerInvoiceImpl
 * @see GnuCashEmployeeVoucherImpl
 * @see GnuCashGenerInvoiceImpl
 */
public class GnuCashVendorBillImpl extends GnuCashGenerInvoiceImpl
                                   implements GnuCashVendorBill,
                                              SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashVendorBillImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashVendorBillImpl(final GncGncInvoice peer, final GnuCashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnuCashVendorBillImpl(final GnuCashGenerInvoice invc)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GnuCashGenerInvoice.TYPE_VENDOR
				&& invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnuCashVendorBillEntryImpl(entry));
		}

		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
				GCshID lot = splt.getLotID();
				if ( lot != null ) {
					for ( GnuCashGenerInvoice invc1 : splt.getTransaction().getGnuCashFile().getGenerInvoices() ) {
						GCshID lotID = invc1.getLotID();
						if ( lotID != null && lotID.equals(lot) ) {
							// Check if it's a payment transaction.
							// If so, add it to the invoice's list of payment transactions.
							if ( splt.getAction() == GnuCashTransactionSplit.Action.PAYMENT ) {
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
	public GnuCashVendor getVendor() throws WrongInvoiceTypeException {
		return getVendor_direct();
	}

	public GnuCashVendor getVendor_direct() throws WrongInvoiceTypeException {
		if ( !getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnuCashGenerInvoice.TYPE_VENDOR.getCode()) )
			throw new WrongInvoiceTypeException();

		GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
		return getGnuCashFile().getVendorByID(ownerID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashVendorBillEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException {
		return new GnuCashVendorBillEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnuCashVendorBillEntry> getEntries() throws WrongInvoiceTypeException {
		Collection<GnuCashVendorBillEntry> castEntries = new HashSet<GnuCashVendorBillEntry>();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
				castEntries.add(new GnuCashVendorBillEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnuCashVendorBillEntry entry) {
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
		buffer.append("GnuCashVendorBillImpl [");

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
