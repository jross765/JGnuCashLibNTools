package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Employee voucher that can be modified {@link #isModifiable()} returns true.
 * 
 * @see GnuCashEmployeeVoucher
 * 
 * @see GnuCashWritableCustomerInvoiceImpl
 * @see GnuCashWritableVendorBillImpl
 * @see GnuCashWritableJobInvoiceImpl
 */
public class GnuCashWritableEmployeeVoucherImpl extends GnuCashWritableGenerInvoiceImpl 
                                                implements GnuCashWritableEmployeeVoucher
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableEmployeeVoucherImpl.class);

	// ---------------------------------------------------------------

	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnuCashGenerInvoiceImpl#GnuCashInvoiceImpl(GncGncInvoice, GnuCashFile)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableEmployeeVoucherImpl(final GncGncInvoice jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableEmployeeVoucherImpl(final GnuCashWritableFileImpl file, final String number,
			final GnuCashEmployee empl, final GnuCashAccountImpl expensesAcct, final GnuCashAccountImpl payableAcct,
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
	 */
	public GnuCashWritableEmployeeVoucherImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.EMPLOYEE )
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
			addEntry(new GnuCashWritableEmployeeVoucherEntryImpl(entry));
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
	 * {@inheritDoc}
	 */
	public void setEmployee(GnuCashEmployee empl) throws WrongInvoiceTypeException {
		// ::TODO
		GnuCashEmployee oldEmpl = getEmployee();
		if ( oldEmpl == empl ) {
			return; // nothing has changed
		}

		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(empl.getID().toString());
		getWritableGnuCashFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
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
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity);
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
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTabName);
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
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created employee voucher entry: " + entry.getID());
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
	protected void removeEntry(final GnuCashWritableEmployeeVoucherEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		removeVoucherEntry(entry);
		LOGGER.info("removeEntry: Removed employee voucher entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableEmployeeVoucherEntryImpl#createEmplVoucherEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of
	 *              employee-voucher-entries
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws ClassNotFoundException
	 */
	protected void addEntry(final GnuCashWritableEmployeeVoucherEntryImpl entry)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

		addVoucherEntry(entry);
		LOGGER.info("addEntry: Added employee voucher entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
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
	private GCshID getPostAccountID(final GnuCashEmployeeVoucherEntryImpl entry) throws WrongInvoiceTypeException {
		return getEmplVchPostAccountID(entry);
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
	protected GCshID getVendBllPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) throws WrongInvoiceTypeException {
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
					"this employee voucher is NOT changeable because there are already payment for it made!");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshID)
	 */
	@Override
	public GnuCashWritableEmployeeVoucherEntry getWritableEntryByID(final GCshID id) {
		return new GnuCashWritableEmployeeVoucherEntryImpl(getGenerEntryByID(id));
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
	public GnuCashEmployee getEmployee() {
		return getGnuCashFile().getEmployeeByID(getEmployeeID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount expensesAcct, final GnuCashAccount payablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException,
			InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
		postEmployeeVoucher(getGnuCashFile(), this, getEmployee(), expensesAcct, payablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashEmployeeVoucherImpl toReadable(GnuCashWritableEmployeeVoucherImpl invc) {
		GnuCashEmployeeVoucherImpl result = new GnuCashEmployeeVoucherImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

}
