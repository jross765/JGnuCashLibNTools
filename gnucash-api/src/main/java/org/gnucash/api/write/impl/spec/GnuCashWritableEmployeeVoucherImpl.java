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
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.gnucash.base.basetypes.simple.spec.GCshEmplVchEntrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

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
	 * @see GnuCashGenerInvoiceImpl
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableEmployeeVoucherImpl(final GncGncInvoice jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @param number 
	 * @param empl 
	 * @param expensesAcct 
	 * @param payableAcct 
	 * @param openedDate 
	 * @param postDate 
	 * @param dueDate 
	 * @throws WrongOwnerTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableEmployeeVoucherImpl(
			final GnuCashWritableFileImpl file, 
			final String number,
			final GnuCashEmployee empl, 
			final GnuCashAccountImpl expensesAcct, 
			final GnuCashAccountImpl payableAcct,
			final LocalDate openedDate, 
			final LocalDate postDate, 
			final LocalDate dueDate)
			throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
		super(createEmployeeVoucher_int(file, number, empl, false, // <-- caution!
				expensesAcct, payableAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableEmployeeVoucherImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws TaxTableNotFoundException {
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
	 * {@inheritDoc}
	 */
	public void setEmployee(GnuCashEmployee empl) {
    	if ( empl == null ) {
    	    throw new IllegalArgumentException("argument <empl> is null");
    	}
    	
		GnuCashEmployee oldEmpl = getEmployee();
		if ( oldEmpl == empl ||
			 oldEmpl.getID().equals(getID()) ) {
			return; // nothing has changed
		}

    	attemptChange();
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
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity)
			throws TaxTableNotFoundException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 * The entry will use the accounts of the SKR03.
	 * 
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final String taxTabName)
			throws TaxTableNotFoundException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTabName);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 *
	 * @return an entry using the given Tax-Table
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableEmployeeVoucherEntry createEntry(
			final GnuCashAccount acct,
			final FixedPointNumber singleUnitPrice, 
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		GnuCashWritableEmployeeVoucherEntry entry = createEmplVchEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created employee voucher entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws TaxTableNotFoundException
	 * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnuCashWritableEmployeeVoucherEntryImpl entry)
			throws TaxTableNotFoundException {

		removeVoucherEntry(entry);
		LOGGER.info("removeEntry: Removed employee voucher entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableEmployeeVoucherEntryImpl#createEmplVoucherEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of
	 *              employee-voucher-entries
	 * @throws TaxTableNotFoundException
	 */
	protected void addEntry(final GnuCashWritableEmployeeVoucherEntryImpl entry)
			throws TaxTableNotFoundException {

		addVoucherEntry(entry);
		LOGGER.info("addEntry: Added employee voucher entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {
		subtractVoucherEntry(entry);
		LOGGER.info("addEntry: Subtracted employee voucher entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 */
	@SuppressWarnings("unused")
	private GCshAcctID getPostAccountID(final GnuCashEmployeeVoucherEntryImpl entry) {
		return getEmplVchPostAccountID(entry);
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
	protected GCshAcctID getVendBllPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
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
					"this employee voucher is NOT modifiable because there are already payment for it made");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshGenerInvcEntrID)
	 */
	@Override
	public GnuCashWritableEmployeeVoucherEntry getWritableEntryByID(final GCshGenerInvcEntrID entrID) {
		return new GnuCashWritableEmployeeVoucherEntryImpl(getGenerEntryByID(entrID));
	}

	// ---------------------------------------------------------------

	/**
	 * @return the ID of the employee who owns the voucher
	 */
	public GCshEmplID getEmployeeID() {
		return new GCshEmplID( getOwnerID() );
	}

	/**
	 * @return the employee who owns the voucher
	 */
	public GnuCashEmployee getEmployee() {
		return getGnuCashFile().getEmployeeByID(getEmployeeID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount expensesAcct, final GnuCashAccount payablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		postEmployeeVoucher(getGnuCashFile(), this, getEmployee(), expensesAcct, payablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashEmployeeVoucherImpl toReadable(GnuCashWritableEmployeeVoucherImpl invc) {
		GnuCashEmployeeVoucherImpl result = new GnuCashEmployeeVoucherImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashEmployeeVoucherEntry getEntryByID(GCshEmplVchEntrID entrID) {
		return new GnuCashEmployeeVoucherEntryImpl(getGenerEntryByID(entrID));
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
	public void addEntry(GnuCashEmployeeVoucherEntry entry) {
		addGenerEntry(entry);
	}

	// ---------------------------------------------------------------

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

	// ---------------------------------------------------------------

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

	// ---------------------------------------------------------------

	@Override
	public boolean isFullyPaid() {
		return isEmplVchFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid() {
		return isNotEmplVchFullyPaid();
	}

}
