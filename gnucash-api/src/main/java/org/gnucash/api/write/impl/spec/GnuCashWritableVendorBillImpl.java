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
import org.gnucash.api.read.IllegalTransactionSplitActionException;
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
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * @see GnuCashGenerInvoiceImpl#GnuCashInvoiceImpl(GncGncInvoice, GnuCashFile)
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
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableVendorBillImpl(final GnuCashWritableFileImpl file, final String number,
			final GnuCashVendor vend, final GnuCashAccountImpl expensesAcct, final GnuCashAccountImpl payableAcct,
			final LocalDate openedDate, final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		super(createVendorBill_int(file, number, vend, false, // <-- caution!
				expensesAcct, payableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableVendorBillImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
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
	public void setVendor(GnuCashVendor vend) throws WrongInvoiceTypeException {
		// ::TODO
		GnuCashVendor oldVend = getVendor();
		if ( oldVend == vend ) {
			return; // nothing has changed
		}

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
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 * The entry will use the accounts of the SKR03.
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity, taxTabName);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 *
	 * @return an entry using the given Tax-Table
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableVendorBillEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableVendorBillEntry entry = createVendBllEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created vendor bill entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws ClassNotFoundException
	 * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnuCashWritableVendorBillEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		removeBillEntry(entry);
		LOGGER.info("removeEntry: Removed vendor bill entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableVendorBillEntryImpl#createVendBillEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of vendor-bill-entries
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws ClassNotFoundException
	 */
	protected void addEntry(final GnuCashWritableVendorBillEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		addBillEntry(entry);
		LOGGER.info("addEntry: Added vendor bill entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		subtractBillEntry(entry);
		LOGGER.info("subtractEntry: Subtracted vendor bill entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 * @throws WrongInvoiceTypeException
	 */
	@SuppressWarnings("unused")
	private GCshID getPostAccountID(final GnuCashVendorBillEntryImpl entry) throws WrongInvoiceTypeException {
		return getVendBllPostAccountID(entry);
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getCustInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getEmplVchPostAccountID(final GnuCashGenerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getJobInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
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
					"this vendor bill is NOT changeable because there are already payment for it made!");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshID)
	 */
	public GnuCashWritableVendorBillEntry getWritableEntryByID(final GCshID id) {
		return new GnuCashWritableVendorBillEntryImpl(getGenerEntryByID(id));
	}

	// ---------------------------------------------------------------

	/**
	 * @return
	 */
	public GCshID getVendorID() {
		return getOwnerID();
	}

	/**
	 * @return
	 */
	public GnuCashVendor getVendor() {
		return getGnuCashFile().getVendorByID(getVendorID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount expensesAcct, final GnuCashAccount payablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException,
			InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		postVendorBill(getGnuCashFile(), this, getVendor(), expensesAcct, payablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashVendorBillImpl toReadable(GnuCashWritableVendorBillImpl invc) {
		GnuCashVendorBillImpl result = new GnuCashVendorBillImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

}
