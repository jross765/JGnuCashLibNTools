package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Employee voucher that can be modified {@link #isModifiable()} returns true.
 * 
 * @see GnucashEmployeeVoucher
 * 
 * @see GnucashWritableCustomerInvoiceImpl
 * @see GnucashWritableVendorBillImpl
 * @see GnucashWritableJobInvoiceImpl
 */
public class GnucashWritableEmployeeVoucherImpl extends GnucashWritableGenerInvoiceImpl 
                                                implements GnucashWritableEmployeeVoucher
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeVoucherImpl.class);

	// ---------------------------------------------------------------

	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnucashGenerInvoiceImpl#GnucashInvoiceImpl(GncGncInvoice, GnucashFile)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableEmployeeVoucherImpl(final GncGncInvoice jwsdpPeer, final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalArgumentException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnucashWritableEmployeeVoucherImpl(final GnucashWritableFileImpl file, final String number,
			final GnucashEmployee empl, final GnucashAccountImpl expensesAcct, final GnucashAccountImpl payableAcct,
			final LocalDate openedDate, final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		super(createEmployeeVoucher_int(file, number, empl, false, // <-- caution!
				expensesAcct, payableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @param file the file we are associated with.
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalArgumentException
	 */
	public GnucashWritableEmployeeVoucherImpl(final GnucashWritableGenerInvoiceImpl invc)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		super(invc.getJwsdpPeer(), invc.getFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.EMPLOYEE )
			throw new WrongInvoiceTypeException();

		// Caution: In the following two loops, we may *not* iterate directly over
		// invc.getGenerEntries(), because else, we will produce a
		// ConcurrentModificationException.
		// (It only works if the invoice has one single entry.)
		// Hence the indirection via the redundant "entries" hash set.
		Collection<GnucashGenerInvoiceEntry> entries = new HashSet<GnucashGenerInvoiceEntry>();
		for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			entries.add(entry);
		}

		for ( GnucashGenerInvoiceEntry entry : entries ) {
			addEntry(new GnucashWritableEmployeeVoucherEntryImpl(entry));
		}

		// Caution: Indirection via a redundant "trxs" hash set.
		// Same reason as above.
		Collection<GnucashTransaction> trxs = new HashSet<GnucashTransaction>();
		for ( GnucashTransaction trx : invc.getPayingTransactions() ) {
			trxs.add(trx);
		}

		for ( GnucashTransaction trx : trxs ) {
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

	/**
	 * The gnucash-file is the top-level class to contain everything.
	 *
	 * @return the file we are associated with
	 */
	protected GnucashWritableFileImpl getWritableFile() {
		return (GnucashWritableFileImpl) getFile();
	}

	/**
	 * support for firing PropertyChangeEvents. (gets initialized only if we really
	 * have listeners)
	 */
	private volatile PropertyChangeSupport myPropertyChange = null;

	/**
	 * Returned value may be null if we never had listeners.
	 *
	 * @return Our support for firing PropertyChangeEvents
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return myPropertyChange;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPropertyChange == null ) {
			myPropertyChange = new PropertyChangeSupport(this);
		}
		myPropertyChange.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 *
	 * @param propertyName The name of the property to listen on.
	 * @param listener     The PropertyChangeListener to be added
	 */
	@SuppressWarnings("exports")
	public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPropertyChange == null ) {
			myPropertyChange = new PropertyChangeSupport(this);
		}
		myPropertyChange.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 *
	 * @param propertyName The name of the property that was listened on.
	 * @param listener     The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPropertyChange != null ) {
			myPropertyChange.removePropertyChangeListener(propertyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	@SuppressWarnings("exports")
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPropertyChange != null ) {
			myPropertyChange.removePropertyChangeListener(listener);
		}
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public void setEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException {
		// ::TODO
		GnucashEmployee oldEmpl = getEmployee();
		if ( oldEmpl == empl ) {
			return; // nothing has changed
		}

		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(empl.getID().toString());
		getWritableFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("employee", oldEmpl, empl);
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
	public GnucashWritableEmployeeVoucherEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity);
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
	public GnucashWritableEmployeeVoucherEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTabName);
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
	public GnucashWritableEmployeeVoucherEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created employee voucher entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnucashWritableEmployeeVoucherEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		removeVoucherEntry(entry);
		LOGGER.info("removeEntry: Removed employee voucher entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnucashWritableEmployeeVoucherEntryImpl#createEmplVoucherEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of
	 *              employee-voucher-entries
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	protected void addEntry(final GnucashWritableEmployeeVoucherEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		addVoucherEntry(entry);
		LOGGER.info("addEntry: Added employee voucher entry: " + entry.getID());
	}

	protected void subtractEntry(final GnucashGenerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		subtractVoucherEntry(entry);
		LOGGER.info("addEntry: Subtracted employee voucher entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 * @throws WrongInvoiceTypeException
	 */
	@SuppressWarnings("unused")
	private GCshID getPostAccountID(final GnucashEmployeeVoucherEntryImpl entry) throws WrongInvoiceTypeException {
		return getEmplVchPostAccountID(entry);
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getCustInvcPostAccountID(final GnucashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getVendBllPostAccountID(final GnucashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
		throw new WrongInvoiceTypeException();
	}

	/**
	 * Do not use
	 */
	@Override
	protected GCshID getJobInvcPostAccountID(final GnucashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
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
					"this employee voucher is NOT changeable because there are already payment for it made!");
		}
	}

	/**
	 * @see GnucashWritableGenerInvoice#getWritableGenerEntryByID(java.lang.String)
	 */
	@Override
	public GnucashWritableEmployeeVoucherEntry getWritableEntryByID(final GCshID id) {
		return new GnucashWritableEmployeeVoucherEntryImpl(getGenerEntryByID(id));
	}

	// ---------------------------------------------------------------

	/**
	 * @return 
	 */
	public GCshID getEmployeeID() {
		return getOwnerID();
	}

	/**
	 * @return
	 */
	public GnucashEmployee getEmployee() {
		return getFile().getEmployeeByID(getEmployeeID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnucashAccount expensesAcct, final GnucashAccount payablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException,
			InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		postEmployeeVoucher(getFile(), this, getEmployee(), expensesAcct, payablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnucashEmployeeVoucherImpl toReadable(GnucashWritableEmployeeVoucherImpl invc) {
		GnucashEmployeeVoucherImpl result = new GnucashEmployeeVoucherImpl(invc.getJwsdpPeer(), invc.getFile());
		return result;
	}

}
