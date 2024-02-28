package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCustomer;
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
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoiceEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customer invoice that can be modified if {@link #isModifiable()} returns true.
 * 
 * @see GnucashCustomerInvoice
 * 
 * @see GnucashWritableEmployeeVoucherImpl
 * @see GnucashWritableVendorBillImpl
 * @see GnucashWritableJobInvoiceImpl
 */
public class GnucashWritableCustomerInvoiceImpl extends GnucashWritableGenerInvoiceImpl 
                                                implements GnucashWritableCustomerInvoice 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerInvoiceImpl.class);

	// ---------------------------------------------------------------
	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnucashGenerInvoiceImpl#GnucashInvoiceImpl(GncGncInvoice, GnucashFile)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableCustomerInvoiceImpl(final GncGncInvoice jwsdpPeer, final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalArgumentException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnucashWritableCustomerInvoiceImpl(final GnucashWritableFileImpl file, final String number,
			final GnucashCustomer cust, final GnucashAccountImpl incomeAcct, final GnucashAccountImpl receivableAcct,
			final LocalDate openedDate, final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		super(createCustomerInvoice_int(file, number, cust, false, // <-- caution!
				incomeAcct, receivableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @param file the file we are associated with.
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnucashWritableCustomerInvoiceImpl(final GnucashWritableGenerInvoiceImpl invc)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		super(invc.getJwsdpPeer(), invc.getGnucashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER )
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
			addEntry(new GnucashWritableCustomerInvoiceEntryImpl(entry));
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
	 * {@inheritDoc}
	 */
	public void setCustomer(GnucashCustomer cust) throws WrongInvoiceTypeException {
		// ::TODO
		GnucashCustomer oldCust = getCustomer();
		if ( oldCust == cust ) {
			return; // nothing has changed
		}

		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(cust.getID().toString());
		getWritableGnucashFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("customer", oldCust, cust);
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
	public GnucashWritableCustomerInvoiceEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity);
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
	public GnucashWritableCustomerInvoiceEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity, taxTabName);
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
	public GnucashWritableCustomerInvoiceEntry createEntry(
			final GnucashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnucashWritableCustomerInvoiceEntry entry = createCustInvcEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created customer invoice entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnucashWritableCustomerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		removeInvcEntry(entry);
		LOGGER.info("removeEntry: Removed customer invoice entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnucashWritableCustomerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entr the entry to add to our internal list of customer-invoice-entries
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws
	 * @throws IllegalArgumentException
	 * @throws ClassNotFoundException
	 */
	protected void addEntry(final GnucashWritableCustomerInvoiceEntryImpl entry) throws WrongInvoiceTypeException,
			InvalidCmdtyCurrTypeException, IllegalArgumentException, TaxTableNotFoundException {
		addInvcEntry(entry);
		LOGGER.info("addEntry: Added customer invoice entry: " + entry.getID());
	}

	protected void subtractEntry(final GnucashGenerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		subtractInvcEntry(entry);
		LOGGER.info("subtractEntry: Subtracted customer invoice entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 * @throws WrongInvoiceTypeException
	 */
	@SuppressWarnings("unused")
	private GCshID getPostAccountID(final GnucashCustomerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
		return getCustInvcPostAccountID(entry);
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
	protected GCshID getEmplVchPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
			throws WrongInvoiceTypeException {
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
					"this customer invoice is NOT changeable because there are already payment for it made!");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshID)
	 */
	public GnucashWritableCustomerInvoiceEntry getWritableEntryByID(final GCshID id) {
		return new GnucashWritableCustomerInvoiceEntryImpl(getGenerEntryByID(id));
	}

	// ---------------------------------------------------------------

	/**
	 * @return 
	 */
	public GCshID getCustomerID() {
		return getOwnerID();
	}

	/**
	 * @return 
	 */
	public GnucashCustomer getCustomer() {
		return getGnucashFile().getCustomerByID(getCustomerID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnucashAccount incomeAcct, final GnucashAccount receivableAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException,
			InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		postCustomerInvoice(getGnucashFile(), this, getCustomer(), incomeAcct, receivableAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnucashCustomerInvoiceImpl toReadable(GnucashWritableCustomerInvoiceImpl invc) {
		GnucashCustomerInvoiceImpl result = new GnucashCustomerInvoiceImpl(invc.getJwsdpPeer(), invc.getGnucashFile());
		return result;
	}

}
