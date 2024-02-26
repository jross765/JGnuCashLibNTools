package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncGncInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnucashVendorBillImpl
 * @see GnucashEmployeeVoucherImpl
 * @see GnucashJobInvoiceImpl
 * @see GnucashGenerInvoiceImpl
*/
public class GnucashCustomerInvoiceImpl extends GnucashGenerInvoiceImpl
                                        implements GnucashCustomerInvoice,
                                                   SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCustomerInvoiceImpl.class);
	
	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnucashCustomerInvoiceImpl(final GncGncInvoice peer, final GnucashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnucashCustomerInvoiceImpl(final GnucashGenerInvoice invc)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		super(invc.getJwsdpPeer(), invc.getGnucashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER
				&& invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnucashCustomerInvoiceEntryImpl(entry));
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
	public GCshID getCustomerID() {
		return getOwnerID();
	}

	@Override
	public GnucashCustomer getCustomer() throws WrongInvoiceTypeException {
		return getCustomer_direct();
	}

	public GnucashCustomer getCustomer_direct() throws WrongInvoiceTypeException {
		if ( !getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnucashGenerInvoice.TYPE_CUSTOMER.getCode()) )
			throw new WrongInvoiceTypeException();

		GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
		return getGnucashFile().getCustomerByID(ownerID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashCustomerInvoiceEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException {
		return new GnucashCustomerInvoiceEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnucashCustomerInvoiceEntry> getEntries() throws WrongInvoiceTypeException {
		Collection<GnucashCustomerInvoiceEntry> castEntries = new HashSet<GnucashCustomerInvoiceEntry>();

		for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GCshOwner.Type.CUSTOMER ) {
				castEntries.add(new GnucashCustomerInvoiceEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnucashCustomerInvoiceEntry entry) {
		addGenerEntry(entry);
	}

	// -----------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getCustInvcAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getCustInvcAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		return getCustInvcAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException {
		return getCustInvcAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException {
		return getCustInvcAmountWithoutTaxes();
	}

	@Override
	public String getAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getCustInvcAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getCustInvcAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getCustInvcAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return getCustInvcAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getCustInvcAmountWithoutTaxesFormatted();
	}

	// ------------------------------

	@Override
	public boolean isFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isCustInvcFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isNotCustInvcFullyPaid();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountUnpaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountPaidWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithoutTaxes() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
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
	public boolean isVendBllFullyPaid() throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotVendBllFullyPaid() throws WrongInvoiceTypeException {
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
		buffer.append("GnucashCustomerInvoiceImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", customer-id=");
		buffer.append(getCustomerID());

		buffer.append(", invoice-number='");
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
