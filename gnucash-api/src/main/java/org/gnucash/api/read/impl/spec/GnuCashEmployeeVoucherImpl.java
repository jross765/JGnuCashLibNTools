package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnuCashCustomerInvoiceImpl
 * @see GnuCashVendorBillImpl
 * @see GnuCashJobInvoiceImpl
 * @see GnuCashGenerInvoiceImpl
 */
public class GnuCashEmployeeVoucherImpl extends GnuCashGenerInvoiceImpl
                                        implements GnuCashEmployeeVoucher,
                                                   SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashEmployeeVoucherImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashEmployeeVoucherImpl(final GncGncInvoice peer, final GnuCashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnuCashEmployeeVoucherImpl(final GnuCashGenerInvoice invc) {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GnuCashGenerInvoice.TYPE_EMPLOYEE
				&& invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnuCashEmployeeVoucherEntryImpl(entry));
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
	public GCshID getEmployeeID() {
		return getOwnerID();
	}

	@Override
	public GnuCashEmployee getEmployee() {
		if ( !getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnuCashGenerInvoice.TYPE_EMPLOYEE.getCode()) )
			throw new WrongInvoiceTypeException();

		GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
		return getGnuCashFile().getEmployeeByID(ownerID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashEmployeeVoucherEntry getEntryByID(GCshID id) {
		return new GnuCashEmployeeVoucherEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnuCashEmployeeVoucherEntry> getEntries() {
		Collection<GnuCashEmployeeVoucherEntry> castEntries = new HashSet<GnuCashEmployeeVoucherEntry>();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
				castEntries.add(new GnuCashEmployeeVoucherEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnuCashEmployeeVoucherEntry entry) {
		addGenerEntry(entry);
	}

	// -----------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes() {
		return getEmplVchAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes() {
		return getEmplVchAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() {
		return getEmplVchAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() {
		return getEmplVchAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() {
		return getEmplVchAmountWithoutTaxes();
	}

	@Override
	public String getAmountUnpaidWithTaxesFormatted() {
		return getEmplVchAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted() {
		return getEmplVchAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() {
		return getEmplVchAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() {
		return getEmplVchAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() {
		return getEmplVchAmountWithoutTaxesFormatted();
	}

	// ------------------------------

	@Override
	public boolean isFullyPaid() {
		return isEmplVchFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid() {
		return isNotEmplVchFullyPaid();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountUnpaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountPaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountPaidWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountUnpaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountPaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountPaidWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getCustInvcAmountWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountUnpaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountPaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountPaidWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountUnpaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountPaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountPaidWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getVendBllAmountWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountUnpaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithoutTaxes() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountUnpaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountPaidWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountPaidWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountWithTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public String getJobInvcAmountWithoutTaxesFormatted() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isCustInvcFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotCustInvcFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isVendBllFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotVendBllFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	// ------------------------------

	/**
	 * Do not use
	 */
	@Override
	public boolean isJobInvcFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	public boolean isNotInvcJobFullyPaid() {
		throw new WrongInvoiceTypeException();
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashEmployeeVoucherImpl [");

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
