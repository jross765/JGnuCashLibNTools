package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnucashCustomerInvoiceImpl
 * @see GnucashVendorBillImpl
 * @see GnucashJobInvoiceImpl
 * @see GnucashGenerInvoiceImpl
 */
public class GnucashEmployeeVoucherImpl extends GnucashGenerInvoiceImpl
                                        implements GnucashEmployeeVoucher,
                                                   SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashEmployeeVoucherImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnucashEmployeeVoucherImpl(final GncGncInvoice peer, final GnucashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnucashEmployeeVoucherImpl(final GnucashGenerInvoice invc)
			throws WrongInvoiceTypeException, IllegalArgumentException {
		super(invc.getJwsdpPeer(), invc.getFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_EMPLOYEE
				&& invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnucashEmployeeVoucherEntryImpl(entry));
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
	public GCshID getEmployeeID() {
		return getOwnerID();
	}

	@Override
	public GnucashEmployee getEmployee() throws WrongInvoiceTypeException {
		if ( !getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnucashGenerInvoice.TYPE_EMPLOYEE.getCode()) )
			throw new WrongInvoiceTypeException();

		GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
		return gcshFile.getEmployeeByID(ownerID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashEmployeeVoucherEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException {
		return new GnucashEmployeeVoucherEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnucashEmployeeVoucherEntry> getEntries() throws WrongInvoiceTypeException {
		Collection<GnucashEmployeeVoucherEntry> castEntries = new HashSet<GnucashEmployeeVoucherEntry>();

		for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
				castEntries.add(new GnucashEmployeeVoucherEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnucashEmployeeVoucherEntry entry) {
		addGenerEntry(entry);
	}

	// -----------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getEmplVchAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getEmplVchAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		return getEmplVchAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException {
		return getEmplVchAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException {
		return getEmplVchAmountWithoutTaxes();
	}

	@Override
	public String getAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getEmplVchAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return getEmplVchAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getEmplVchAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return getEmplVchAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return getEmplVchAmountWithoutTaxesFormatted();
	}

	// ------------------------------

	@Override
	public boolean isFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isEmplVchFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
		return isNotEmplVchFullyPaid();
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
		buffer.append("GnucashEmployeeVoucherImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", employee-id=");
		buffer.append(getEmployeeID());

		buffer.append(", voucher-number='");
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
