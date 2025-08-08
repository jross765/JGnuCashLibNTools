package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.gnucash.base.basetypes.simple.spec.GCshVendBllEntrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Vendor bill that can be modified {@link #isModifiable()} returns true.
 * 
 * @see GnuCashVendorBill
 * 
 * @see GnuCashWritableCustomerInvoiceImpl
 * @see GnuCashWritableEmployeeVoucherImpl
 * @see GnuCashWritableJobInvoiceImpl
 */
public class GnuCashWritableVendorBillImpl extends GnuCashWritableGenerInvoiceImpl 
                                           implements GnuCashWritableVendorBill
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableVendorBillImpl.class);

	// ---------------------------------------------------------------

	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnuCashGenerInvoiceImpl
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableVendorBillImpl(final GncGncInvoice jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @param number 
	 * @param vend 
	 * @param expensesAcct 
	 * @param payableAcct 
	 * @param openedDate 
	 * @param postDate 
	 * @param dueDate 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableVendorBillImpl(
			final GnuCashWritableFileImpl file, 
			final String number,
			final GnuCashVendor vend, 
			final GnuCashAccountImpl expensesAcct, 
			final GnuCashAccountImpl payableAcct,
			final LocalDate openedDate, 
			final LocalDate postDate, 
			final LocalDate dueDate)
			throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
		super(createVendorBill_int(file, number, vend, false, // <-- caution!
				expensesAcct, payableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableVendorBillImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws TaxTableNotFoundException {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.VENDOR )
			throw new WrongInvoiceTypeException();

		// Caution: In the following two loops, we may *not* iterate directly over
		// invc.getGenerEntries(), because else, we will produce a
		// ConcurrentModificationException.
		// (It only works if the invoice has one single entry.)
		// Hence the indirection via the redundant "entries" hash set.
		Collection<GnuCashGenerInvoiceEntry> entries = new HashSet<GnuCashGenerInvoiceEntry>();
		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			entries.add(entry);
		}

		for ( GnuCashGenerInvoiceEntry entry : entries ) {
			addEntry(new GnuCashWritableVendorBillEntryImpl(entry));
		}

		// Caution: Indirection via a redundant "trxs" hash set.
		// Same reason as above.
		Collection<GnuCashTransaction> trxs = new HashSet<GnuCashTransaction>();
		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			trxs.add(trx);
		}

		for ( GnuCashTransaction trx : trxs ) {
			for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
				GCshLotID lot = splt.getLotID();
				if ( lot != null ) {
					for ( GnuCashGenerInvoice invc1 : splt.getTransaction().getGnuCashFile().getGenerInvoices() ) {
						GCshLotID lotID = invc1.getLotID();
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

	/**
	 * The GnuCash file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	protected GnuCashWritableFileImpl getWritableFile() {
		return (GnuCashWritableFileImpl) getGnuCashFile();
	}

	

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public void setVendor(GnuCashVendor vend) {
    	if ( vend == null ) {
    	    throw new IllegalArgumentException("argument <vend> is null");
    	}
    	
		GnuCashVendor oldVend = getVendor();
		if ( oldVend == vend ||
			 oldVend.getID().equals(getID()) ) {
			return; // nothing has changed
		}

    	attemptChange();
		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(vend.getID().toString());
		getWritableFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("vendor", oldVend, vend);
		}
	}

	// -----------------------------------------------------------

	/**
	 * create and add a new entry.
	 * 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity)
			throws TaxTableNotFoundException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 * The entry will use the accounts of the SKR03.
	 * 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws TaxTableNotFoundException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity, taxTabName);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 *
	 * @return an entry using the given Tax-Table
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created vendor bill entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws TaxTableNotFoundException
	 * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnuCashWritableVendorBillEntryImpl entry)
			throws TaxTableNotFoundException {

		removeBillEntry(entry);
		LOGGER.info("removeEntry: Removed vendor bill entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableVendorBillEntryImpl#createVendBillEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of vendor-bill-entries
	 * @throws TaxTableNotFoundException
	 */
	protected void addEntry(final GnuCashWritableVendorBillEntryImpl entry)
			throws TaxTableNotFoundException {

		addBillEntry(entry);
		LOGGER.info("addEntry: Added vendor bill entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {
		subtractBillEntry(entry);
		LOGGER.info("subtractEntry: Subtracted vendor bill entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 */
	@SuppressWarnings("unused")
	private GCshAcctID getPostAccountID(final GnuCashVendorBillEntryImpl entry) {
		return getVendBllPostAccountID(entry);
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getCustInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getEmplVchPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshAcctID getJobInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
		throw new WrongInvoiceTypeException();
	}

	// ---------------------------------------------------------------
	
	/**
	 * Throw an IllegalStateException if we are not modifiable.
	 *
	 * @see #isModifiable()
	 */
	protected void attemptChange() {
		if ( !isModifiable() ) {
			throw new IllegalStateException(
					"this vendor bill is NOT modifiable because there are already payment for it made");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshGenerInvcEntrID)
	 */
	public GnuCashWritableVendorBillEntry getWritableEntryByID(final GCshGenerInvcEntrID entrID) {
		return new GnuCashWritableVendorBillEntryImpl(getGenerEntryByID(entrID));
	}

	// ---------------------------------------------------------------

	/**
	 * @return the ID of the vendor who/that owns the bill
	 */
	public GCshVendID getVendorID() {
		return new GCshVendID( getOwnerID() );
	}

	/**
	 * @return the vendor who/that owns the bill
	 */
	public GnuCashVendor getVendor() {
		return getGnuCashFile().getVendorByID(getVendorID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount expensesAcct, final GnuCashAccount payablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		postVendorBill(getGnuCashFile(), this, getVendor(), expensesAcct, payablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashVendorBillImpl toReadable(GnuCashWritableVendorBillImpl invc) {
		GnuCashVendorBillImpl result = new GnuCashVendorBillImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashVendorBillEntry getEntryByID(GCshVendBllEntrID entrID) {
		return new GnuCashVendorBillEntryImpl(getGenerEntryByID(entrID));
	}

	@Override
	public Collection<GnuCashVendorBillEntry> getEntries() {
		Collection<GnuCashVendorBillEntry> castEntries = new HashSet<GnuCashVendorBillEntry>();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
				castEntries.add(new GnuCashVendorBillEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(GnuCashVendorBillEntry entry) {
		addGenerEntry(entry);
	}

	// ---------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes() {
		return getVendBllAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes() {
		return getVendBllAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() {
		return getVendBllAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() {
		return getVendBllAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() {
		return getVendBllAmountWithoutTaxes();
	}

	// ----------------------------

	@Override
	public String getAmountUnpaidWithTaxesFormatted() {
		return getVendBllAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted() {
		return getVendBllAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() {
		return getVendBllAmountPaidWithoutTaxesFormatted();
	}

	@Override
	public String getAmountWithTaxesFormatted() {
		return getVendBllAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() {
		return getVendBllAmountWithoutTaxesFormatted();
	}

	// ---------------------------------------------------------------

	@Override
	public boolean isFullyPaid() {
		return isVendBllFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid() {
		return isNotVendBllFullyPaid();
	}

}
