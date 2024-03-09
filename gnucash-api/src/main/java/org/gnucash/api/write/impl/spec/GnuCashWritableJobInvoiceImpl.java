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
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job invoice that can be modified {@link #isModifiable()} returns true.
 * 
 * @see GnuCashJobInvoice
 * 
 * @see GnuCashWritableCustomerInvoiceImpl
 * @see GnuCashWritableEmployeeVoucherImpl
 * @see GnuCashWritableVendorBillImpl
 */
public class GnuCashWritableJobInvoiceImpl extends GnuCashWritableGenerInvoiceImpl 
                                           implements GnuCashWritableJobInvoice 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableJobInvoiceImpl.class);

	// ---------------------------------------------------------------

	/**
	 * Create an editable invoice facading an existing JWSDP-peer.
	 *
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @param gcshFile      the file to register under
	 * @see GnuCashGenerInvoiceImpl#GnuCashInvoiceImpl(GncGncInvoice, GnuCashFile)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableJobInvoiceImpl(final GncGncInvoice jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param file the file we are associated with.
	 * @param number 
	 * @param job 
	 * @param incExpAcct 
	 * @param recvblPayblAcct 
	 * @param openedDate 
	 * @param postDate 
	 * @param dueDate 
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalTransactionSplitActionException
	 */
	public GnuCashWritableJobInvoiceImpl(final GnuCashWritableFileImpl file, final String number,
			final GnuCashGenerJob job, final GnuCashAccountImpl incExpAcct, final GnuCashAccountImpl recvblPayblAcct,
			final LocalDate openedDate, final LocalDate postDate, final LocalDate dueDate)
			throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
		super(createJobInvoice_int(file, number, job, false, // <-- caution!
				incExpAcct, recvblPayblAcct, openedDate, postDate, dueDate), file);
	}

	/**
	 * @param invc 
	 * @param file the file we are associated with.
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableJobInvoiceImpl(final GnuCashWritableGenerInvoiceImpl invc)
			throws TaxTableNotFoundException {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.JOB )
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
			addEntry(new GnuCashWritableJobInvoiceEntryImpl(entry));
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
	@Override
	public void setGenerJob(GnuCashGenerJob job) {
		// ::TODO
		GnuCashGenerJob oldJob = getJob();
		if ( oldJob == job ) {
			return; // nothing has changed
		}

		getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(job.getID().toString());
		getWritableFile().setModified(true);

		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("job", oldJob, job);
		}
	}

	@Override
	public void setCustomerJob(GnuCashCustomerJob job) {
		setGenerJob(job);
	}

	@Override
	public void setVendorJob(GnuCashVendorJob job) {
		setGenerJob(job);
	}

	// -----------------------------------------------------------

	/**
	 * create and add a new entry.
	 * 
	 * @throws TaxTableNotFoundException
	 * @throws UnknownInvoiceTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableJobInvoiceEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity) throws TaxTableNotFoundException,
			UnknownInvoiceTypeException {
		GnuCashWritableJobInvoiceEntry entry = createJobInvcEntry(acct, singleUnitPrice, quantity);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 * The entry will use the accounts of the SKR03.
	 * 
	 * @throws TaxTableNotFoundException
	 * @throws UnknownInvoiceTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableJobInvoiceEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final String taxTabName) throws TaxTableNotFoundException, 
		UnknownInvoiceTypeException {
		GnuCashWritableJobInvoiceEntry entry = createJobInvcEntry(acct, singleUnitPrice, quantity, taxTabName);
		return entry;
	}

	/**
	 * create and add a new entry.<br/>
	 *
	 * @return an entry using the given Tax-Table
	 * @throws TaxTableNotFoundException
	 * @throws UnknownInvoiceTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnuCashWritableJobInvoiceEntry createEntry(
			final GnuCashAccount acct, 
			final FixedPointNumber singleUnitPrice,
			final FixedPointNumber quantity, 
			final GCshTaxTable taxTab) throws TaxTableNotFoundException, 
		UnknownInvoiceTypeException {
		GnuCashWritableJobInvoiceEntry entry = createJobInvcEntry(acct, singleUnitPrice, quantity, taxTab);
		LOGGER.info("createEntry: Created job invoice entry: " + entry.getID());
		return entry;
	}

	// -----------------------------------------------------------

	/**
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws ClassNotFoundException
	 * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
	 */
	protected void removeEntry(final GnuCashWritableJobInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {

		removeInvcEntry(entry);
		LOGGER.info("removeEntry: Removed job invoice entry: " + entry.getID());
	}

	/**
	 * Called by
	 * ${@link GnuCashWritableJobInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableJobInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
	 *
	 * @param entry the entry to add to our internal list of job-invoice-entries
	 * @throws TaxTableNotFoundException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws ClassNotFoundException
	 */
	protected void addEntry(final GnuCashWritableJobInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {

		addJobEntry(entry);
		LOGGER.info("addEntry: Added job invoice entry: " + entry.getID());
	}

	protected void subtractEntry(final GnuCashGenerInvoiceEntryImpl entry)
			throws TaxTableNotFoundException {
		subtractInvcEntry(entry);
		LOGGER.info("subtractEntry: Subtracted job invoice entry: " + entry.getID());
	}

	// ---------------------------------------------------------------
	
	/**
	 * @return the ID of the Account to transfer the money from
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unused")
	private GCshID getPostAccountID(final GnuCashJobInvoiceEntryImpl entry) {
		return getJobInvcPostAccountID(entry);
	}

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/*
//	 * Do not use
//	 */
//	@Override
//	protected GCshID getCustInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
//		throw new WrongInvoiceTypeException();
//	}

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/*
//	 * Do not use
//	 */
//	@Override
//	protected GCshID getVendBllPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
//		throw new WrongInvoiceTypeException();
//	}

	/*
	 * Do not use
	 */
	@Override
	protected GCshID getEmplVchPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
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
					"this job invoice is NOT changeable because there are already payment for it made!");
		}
	}

	/**
	 * @see #getGenerEntryByID(GCshID)
	 */
	public GnuCashWritableJobInvoiceEntry getWritableEntryByID(final GCshID id) {
		return new GnuCashWritableJobInvoiceEntryImpl(getGenerEntryByID(id));
	}

	// ---------------------------------------------------------------

	/**
	 * @return
	 */
	public GCshID getJobID() {
		return getOwnerID();
	}

	/**
	 * @return
	 */
	public GnuCashGenerJob getJob() {
		return getGnuCashFile().getGenerJobByID(getJobID());
	}

	// ---------------------------------------------------------------

	@Override
	public void post(final GnuCashAccount incExpAcct, final GnuCashAccount recvblPayablAcct, final LocalDate postDate,
			final LocalDate dueDate) throws WrongOwnerTypeException,
			IllegalTransactionSplitActionException {
		postJobInvoice(getGnuCashFile(), this, getJob(), incExpAcct, recvblPayablAcct, postDate, dueDate);
	}

	// ---------------------------------------------------------------

	public static GnuCashJobInvoiceImpl toReadable(GnuCashWritableJobInvoiceImpl invc) {
		GnuCashJobInvoiceImpl result = new GnuCashJobInvoiceImpl(invc.getJwsdpPeer(), invc.getGnuCashFile());
		return result;
	}

}
