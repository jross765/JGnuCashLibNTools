package org.gnucash.api.write.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncBudget;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.GncCountData;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncSchedxaction;
import org.gnucash.api.generated.GncTemplateTransactions;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashPrice;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashCommodityImpl;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.impl.GnucashPriceImpl;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableAccount;
import org.gnucash.api.write.GnucashWritableCommodity;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.GnucashWritableEmployee;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.GnucashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnucashWritableGenerJob;
import org.gnucash.api.write.GnucashWritablePrice;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.write.GnucashWritableVendor;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.impl.aux.GCshWritableBillTermsImpl;
import org.gnucash.api.write.impl.aux.GCshWritableTaxTableImpl;
import org.gnucash.api.write.impl.hlp.BookElementsSorter;
import org.gnucash.api.write.impl.hlp.FilePriceManager;
import org.gnucash.api.write.impl.hlp.NamespaceAdderWriter;
import org.gnucash.api.write.impl.hlp.WritingContentHandler;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorBillImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Extension of GnucashFileImpl to allow read-write access instead of read-only
 * access.
 */
public class GnucashWritableFileImpl extends GnucashFileImpl implements GnucashWritableFile {
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableFileImpl.class);

	// ::MAGIC
	private static final String CODEPAGE = "UTF-8";

	// ---------------------------------------------------------------

	/**
	 * true if this file has been modified.
	 */
	private boolean modified = false;

	/**
	 * @see {@link #getLastWriteTime()}
	 */
	private long lastWriteTime = 0;

	// ---------------------------------------------------------------

	/**
	 * @param file the file to load
	 * @throws IOException                   on bsic io-problems such as a
	 *                                       FileNotFoundException
	 * @throws InvalidCmdtyCurrIDException
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public GnucashWritableFileImpl(final File file)
			throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		super(file);
		setModified(false);

		acctMgr = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
		trxMgr = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);

		invcMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
		invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);

		custMgr = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
		vendMgr = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
		emplMgr = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
		jobMgr = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

		cmdtyMgr = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
		prcMgr = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
	}

	public GnucashWritableFileImpl(final InputStream is)
			throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		super(is);

		acctMgr = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
		trxMgr = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);

		invcMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
		invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);

		custMgr = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
		vendMgr = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
		emplMgr = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
		jobMgr = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

		cmdtyMgr = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
		prcMgr = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnucashWritableFile getWritableGnucashFile() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUserDefinedAttribute(final String aName, final String aValue) {
		List<Slot> slots = getRootElement().getGncBook().getBookSlots().getSlot();
		for ( Slot slot : slots ) {
			if ( slot.getSlotKey().equals(aName) ) {
				slot.getSlotValue().getContent().clear();
				slot.getSlotValue().getContent().add(aValue);
				return;
			}
		}

		// create new slot
		Slot newSlot = getObjectFactory().createSlot();
		newSlot.setSlotKey(aName);
		newSlot.setSlotValue(getObjectFactory().createSlotValue());
		newSlot.getSlotValue().getContent().add(aValue);
		newSlot.getSlotValue().setType(Const.XML_DATA_TYPE_STRING);
		getRootElement().getGncBook().getBookSlots().getSlot().add(newSlot);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * @param pModified true if this file has been modified false after save, load
	 *                  or undo of changes
	 */
	@Override
	public void setModified(final boolean pModified) {
		// boolean old = this.modified;
		modified = pModified;
		// if (propertyChange != null)
		// propertyChange.firePropertyChange("modified", old, pModified);
	}

	/**
	 * @return true if this file has been modified
	 */
	@Override
	public boolean isModified() {
		return modified;
	}

	/**
	 * @throws InvalidCmdtyCurrIDException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @see {@link GnucashFileImpl#loadFile(java.io.File)}
	 */
	@Override
	protected void loadFile(final File pFile)
			throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		super.loadFile(pFile);
		lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @see GnucashWritableFile#writeFile(java.io.File)
	 */
	@Override
	public void writeFile(final File file) throws IOException {

		if ( file == null ) {
			throw new IllegalArgumentException("null not allowed for field this file");
		}

		if ( file.exists() ) {
			throw new IllegalArgumentException("Given file '" + file.getAbsolutePath() + "' already exists!");
		}

		checkAllCountData();
		clean();

		setFile(file);

		OutputStream out = new FileOutputStream(file);
		out = new BufferedOutputStream(out);
		if ( file.getName().endsWith(".gz") ) {
			out = new GZIPOutputStream(out);
		}

		Writer writer = new NamespaceAdderWriter(new OutputStreamWriter(out, CODEPAGE));
		try {
			JAXBContext context = getJAXBContext();
			Marshaller marsh = context.createMarshaller();

			// marsh.marshal(getRootElement(), writer);
			// marsh.marshal(getRootElement(), new PrintWriter( System.out ) );
			marsh.marshal(getRootElement(), new WritingContentHandler(writer));

			setModified(false);
		} catch (JAXBException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			writer.close();
		}

		out.close();

		lastWriteTime = Math.max(file.lastModified(), System.currentTimeMillis());
	}

	/**
	 * @return the time in ms (compatible with File.lastModified) of the last
	 *         write-operation
	 */
	@Override
	public long getLastWriteTime() {
		return lastWriteTime;
	}

	// ---------------------------------------------------------------

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
	 * <gnc:count-data cd:type="account">394</gnc:count-data> ... (etc.)
	 *
	 * @param type the type to set it for
	 */
	protected void incrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
		for ( Iterator<GncCountData> iter = l.iterator(); iter.hasNext(); ) {
			GncCountData gncCountData = (GncCountData) iter.next();

			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(gncCountData.getValue() + 1);
				setModified(true);
			}
		}
	}

	/**
	 * Keep the count-data up to date. The count-data is re-calculated on the fly
	 * before writing but we like to keep our internal model up-to-date just to be
	 * defensive.
	 *
	 * @param type the type to set it for
	 */
	protected void decrementCountDataFor(final String type) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
		for ( Iterator<GncCountData> iter = l.iterator(); iter.hasNext(); ) {
			GncCountData gncCountData = (GncCountData) iter.next();

			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(gncCountData.getValue() - 1);
				setModified(true);
			}
		}
	}

	/**
	 * Keep the count-data up to date.
	 *
	 * @param type  the type to set it for
	 * @param count the value
	 */
	protected void setCountDataFor(final String type, final int count) {

		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}

		if ( count < 0 ) {
			throw new IllegalArgumentException("count < 0 given");
		}

		List<GncCountData> cdList = getRootElement().getGncBook().getGncCountData();
		for ( GncCountData gncCountData : cdList ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				gncCountData.setValue(count);
				setModified(true);
			}
		}
	}

	/**
	 * Calculate and set the correct valued for all the following count-data.<br/>
	 * Also check the that only valid elements are in the book-element and that they
	 * have the correct order.
	 */
	private void checkAllCountData() {

		int cntAccount = 0;
		int cntTransaction = 0;
		int cntInvoice = 0;
		int cntIncEntry = 0;
		int cntCustomer = 0;
		int cntVendor = 0;
		int cntEmployee = 0;
		int cntJob = 0;
		int cntTaxTable = 0;
		int cntBillTerm = 0;
		int cntCommodity = 0;
		int cntPrice = 0;

		/**
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link GncTemplateTransactions} {@link GncGncInvoice} {@link GncGncEntry}
		 * {@link GncGncJob} {@link GncGncTaxTable} {@link GncCommodity}
		 * {@link GncGncCustomer} {@link GncSchedxaction} {@link GncBudget}
		 * {@link GncAccount} {@link GncPricedb} {@link GncTransaction}
		 */
		List<Object> bookElements = getRootElement().getGncBook().getBookElements();
		for ( Object element : bookElements ) {
			if ( element instanceof GncAccount ) {
				cntAccount++;
			} else if ( element instanceof GncTransaction ) {
				cntTransaction++;
			} else if ( element instanceof GncGncInvoice ) {
				cntInvoice++;
			} else if ( element instanceof GncGncEntry ) {
				cntIncEntry++;
			} else if ( element instanceof GncGncCustomer ) {
				cntCustomer++;
			} else if ( element instanceof GncGncVendor ) {
				cntVendor++;
			} else if ( element instanceof GncGncEmployee ) {
				cntEmployee++;
			} else if ( element instanceof GncGncJob ) {
				cntJob++;
			} else if ( element instanceof GncGncTaxTable ) {
				cntTaxTable++;
			} else if ( element instanceof GncGncBillTerm ) {
				cntBillTerm++;
			} else if ( element instanceof GncCommodity ) {
				cntCommodity++;
			} else if ( element instanceof GncPricedb ) {
				cntPrice += ((GncPricedb) element).getPrice().size();
			} else if ( element instanceof GncTemplateTransactions ) {
				// ::TODO
			} else if ( element instanceof GncSchedxaction ) {
				// ::TODO
			} else if ( element instanceof GncBudget ) {
				// ::TODO
			} else {
				throw new IllegalStateException("Found unexpected element in GNC:Book: '" + element.toString() + "'");
			}
		}
		
		// Special case commoditiy-counter: 
		// The template entry is not accounted for.
		cntCommodity--;

		setCountDataFor("account", cntAccount);
		setCountDataFor("transaction", cntTransaction);
		setCountDataFor("gnc:GncInvoice", cntInvoice);
		setCountDataFor("gnc:GncEntry", cntIncEntry);
		setCountDataFor("gnc:GncCustomer", cntCustomer);
		setCountDataFor("gnc:GncVendor", cntVendor);
		setCountDataFor("gnc:GncEmployee", cntEmployee);
		setCountDataFor("gnc:GncJob", cntJob);
		setCountDataFor("gnc:GncTaxTable", cntTaxTable);
		setCountDataFor("gnc:GncBillTerm", cntBillTerm);
		setCountDataFor("commodity", cntCommodity);
		setCountDataFor("price", cntPrice);

		// Make sure the correct sort-order of the entity-types is honored
		// (we do not enforce this in the XML schema to allow for reading files
		// that do not honor that order).
		java.util.Collections.sort(bookElements, new BookElementsSorter());
	}

	// ---------------------------------------------------------------

	/**
	 * @return the underlying JAXB-element
	 * @see GnucashWritableFile#getRootElement()
	 */
	@SuppressWarnings("exports")
	@Override
	public GncV2 getRootElement() {
		return super.getRootElement();
	}

	/**
	 * @throws InvalidCmdtyCurrIDException
	 * @throws InvalidCmdtyCurrTypeException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @see GnucashFileImpl#setRootElement(GncV2)
	 */
	@Override
	protected void setRootElement(final GncV2 rootElement)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		super.setRootElement(rootElement);
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	/**
	 * @param type the type to look for
	 * @return A changeable version of all accounts of that type.
	 * @throws UnknownAccountTypeException
	 * @see {@link GnucashWritableFile#getAccountsByType(String)}
	 */
	@Override
	public Collection<GnucashWritableAccount> getWritableAccountsByType(final GnucashAccount.Type type)
			throws UnknownAccountTypeException {
		Collection<GnucashWritableAccount> retval = new ArrayList<GnucashWritableAccount>();
		for ( GnucashWritableAccount acct : getWritableAccounts() ) {

			if ( acct.getType() == null ) {
				if ( type == null ) {
					retval.add(acct);
				}
			} else if ( acct.getType() == type ) {
				retval.add(acct);
			}

		}
		return retval;
	}

	/**
	 * @param acctID the unique account-id
	 * @return A changeable version of the account or null if not found.
	 * @see GnucashFile#getAccountByID(GCshID)
	 */
	@Override
	public GnucashWritableAccount getWritableAccountByID(final GCshID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}

		if ( !acctID.isSet() ) {
			throw new IllegalArgumentException("account ID is not set");
		}

		try {
			return new GnucashWritableAccountImpl(super.getAccountByID(acctID), true);
		} catch (Exception exc) {
			LOGGER.error(
					"getWritableAccountByID: Could not instantiate writable account object from read-only account object (ID: "
							+ acctID + ")");
			throw new RuntimeException(
					"Could not instantiate writable account object from read-only account object (ID: " + acctID + ")");
		}
	}

	/**
	 * @param name the name of the account
	 * @return A changeable version of the first account with that name.
	 * @see GnucashFile#getAccountsByName(String)
	 */
	@Override
	public GnucashWritableAccount getWritableAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return (GnucashWritableAccount) super.getAccountByNameUniq(name, qualif);
	}

	/**
	 * @return a read-write collection of all accounts
	 */
	@Override
	public Collection<GnucashWritableAccount> getWritableAccounts() {
		TreeSet<GnucashWritableAccount> retval = new TreeSet<GnucashWritableAccount>();

		for ( GnucashAccount acct : getAccounts() ) {
			retval.add((GnucashWritableAccount) acct);
		}

		return retval;
	}

	/**
	 * @return a read-only collection of all accounts that have no parent
	 * @throws UnknownAccountTypeException
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends GnucashWritableAccount> getWritableRootAccounts() throws UnknownAccountTypeException {
		return (Collection<? extends GnucashWritableAccount>) getParentlessAccounts();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gnucash.write.jwsdpimpl.GnucashFileImpl#getRootAccounts()
	 */
	@Override
	public Collection<? extends GnucashAccount> getParentlessAccounts() throws UnknownAccountTypeException {
		// TODO Auto-generated method stub
		Collection<? extends GnucashAccount> rootAcctList = super.getParentlessAccounts();
		if ( rootAcctList.size() > 1 ) {
			GnucashAccount root = null;
			StringBuilder roots = new StringBuilder();
			for ( GnucashAccount gnucashAccount : rootAcctList ) {
				if ( gnucashAccount == null ) {
					continue;
				}
				if ( gnucashAccount.getType() != null && gnucashAccount.getType() == GnucashAccount.Type.ROOT ) {
					root = gnucashAccount;
					continue;
				}
				roots.append(gnucashAccount.getID()).append("=\"").append(gnucashAccount.getName()).append("\" ");
			}
			LOGGER.warn(
					"getParentlessAccounts: File has more than one root-account! Attaching excess accounts to root-account: "
							+ roots.toString());
			ArrayList<GnucashAccount> rootAccounts2 = new ArrayList<GnucashAccount>();
			rootAccounts2.add(root);
			for ( GnucashAccount acct : rootAcctList ) {
				if ( acct == null ) {
					continue;
				}
				if ( acct == root ) {
					continue;
				}
				((GnucashWritableAccount) acct).setParentAccount(root);

			}
			rootAcctList = rootAccounts2;
		}
		return rootAcctList;
	}

	// ----------------------------

	/**
	 * @see GnucashWritableFile#createWritableAccount()
	 */
	@Override
	public GnucashWritableAccount createWritableAccount() {
		GnucashWritableAccount acct = new GnucashWritableAccountImpl(this);
		super.acctMgr.addAccount(acct);
		return acct;
	}

	/**
	 * @param acct what to remove
	 */
	@Override
	public void removeAccount(final GnucashWritableAccount acct) {
		if ( acct.getTransactionSplits().size() > 0 ) {
			throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
		}

		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableAccountImpl) acct).getJwsdpPeer());
		setModified(true);
		super.acctMgr.removeAccount(acct);
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnucashWritableFile#getTransactionByID(java.lang.String)
	 */
	@Override
	public GnucashWritableTransaction getWritableTransactionByID(final GCshID trxID) {
		if ( trxID == null ) {
			throw new IllegalArgumentException("null transaction ID given");
		}

		if ( !trxID.isSet() ) {
			throw new IllegalArgumentException("transaction ID is not set");
		}

		try {
			return new GnucashWritableTransactionImpl(super.getTransactionByID(trxID));
		} catch (Exception exc) {
			LOGGER.error(
					"getWritableTransactionByID: Could not instantiate writable transaction object from read-only transaction object (ID: "
							+ trxID + ")");
			throw new RuntimeException(
					"Could not instantiate writable transaction object from read-only transaction object (ID: " + trxID
							+ ")");
		}
	}

	/**
	 * @see GnucashWritableFile#getWritableTransactions()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Collection<? extends GnucashWritableTransaction> getWritableTransactions() {
		Collection<GnucashWritableTransaction> result = new ArrayList<GnucashWritableTransaction>();

		for ( GnucashTransaction trx : super.getTransactions() ) {
			GnucashWritableTransaction newTrx = new GnucashWritableTransactionImpl(
					(GnucashWritableTransactionImpl) trx);
			result.add(newTrx);
		}

		return result;
	}

	// ----------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * 
	 */
	@Override
	public GnucashWritableTransaction createWritableTransaction() {
		return new GnucashWritableTransactionImpl(this);
	}

	/**
	 * Used by GnucashTransactionImpl.createTransaction to add a new Transaction to
	 * this file.
	 * 
	 * @throws
	 * @throws ClassNotFoundException
	 * @see GnucashTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
	 */
	protected void addTransaction(final GnucashTransactionImpl trx) {
		getRootElement().getGncBook().getBookElements().add(trx.getJwsdpPeer());
		setModified(true);
		super.trxMgr.addTransaction(trx);
	}

	/**
	 * @param trx what to remove
	 * 
	 */
	@Override
	public void removeTransaction(final GnucashWritableTransaction trx) {

		Collection<GnucashWritableTransactionSplit> c = new ArrayList<GnucashWritableTransactionSplit>();
		c.addAll(trx.getWritableSplits());
		for ( GnucashWritableTransactionSplit element : c ) {
			element.remove();
		}

		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableTransactionImpl) trx).getJwsdpPeer());
		setModified(true);
		super.trxMgr.removeTransaction(trx);
	}

	// ---------------------------------------------------------------

	/**
	 * @param spltID
	 * @return
	 */
	@Override
	public GnucashWritableTransactionSplit getWritableTransactionSplitByID(final GCshID spltID) {
		if ( spltID == null ) {
			throw new IllegalArgumentException("null transaction split ID given");
		}

		if ( !spltID.isSet() ) {
			throw new IllegalArgumentException("transaction split ID is not set");
		}

		GnucashTransactionSplit splt = super.getTransactionSplitByID(spltID);
		return new GnucashWritableTransactionSplitImpl((GnucashTransactionSplitImpl) splt);
	}

	/**
	 * @return
	 */
	@Override
	public Collection<GnucashWritableTransactionSplit> getWritableTransactionSplits() {
		Collection<GnucashWritableTransactionSplit> result = new ArrayList<GnucashWritableTransactionSplit>();

		for ( GnucashTransactionSplit trx : super.getTransactionSplits() ) {
			GnucashWritableTransactionSplit newTrx = new GnucashWritableTransactionSplitImpl(
					(GnucashWritableTransactionSplitImpl) trx);
			result.add(newTrx);
		}

		return result;
	}

	// ---------------------------------------------------------------

	/**
	 * @param invcID the unique invoice-id
	 * @return A changeable version of the Invoice or null if not found.
	 * @see GnucashFile#getGenerInvoiceByID(GCshID)
	 */
	@Override
	public GnucashWritableGenerInvoice getWritableGenerInvoiceByID(final GCshID invcID) {
		if ( invcID == null ) {
			throw new IllegalArgumentException("null invoice ID given");
		}

		if ( !invcID.isSet() ) {
			throw new IllegalArgumentException("invoice ID is not set");
		}

		GnucashGenerInvoice invc = super.getGenerInvoiceByID(invcID);
		return new GnucashWritableGenerInvoiceImpl((GnucashGenerInvoiceImpl) invc);
	}

	/**
	 * @see GnucashWritableFile#getWritableGenerJobs()
	 */
	@Override
	public Collection<GnucashWritableGenerInvoice> getWritableGenerInvoices() {
		Collection<GnucashGenerInvoice> invcList = getGenerInvoices();

		if ( invcList == null ) {
			throw new IllegalStateException("getGenerInvoices() returned null");
		}

		Collection<GnucashWritableGenerInvoice> retval = new ArrayList<GnucashWritableGenerInvoice>();
		for ( GnucashGenerInvoice invc : invcList ) {
			retval.add((GnucashWritableGenerInvoice) invc);
		}

		return retval;
	}

	// ----------------------------

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * 
	 * @throws IllegalTransactionSplitActionException
	 * @see GnucashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnucashWritableCustomerInvoice createWritableCustomerInvoice(final String number, final GnucashCustomer cust,
			final GnucashAccount incomeAcct, final GnucashAccount receivableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException,
			IllegalTransactionSplitActionException {
		if ( cust == null ) {
			throw new IllegalArgumentException("null customer given");
		}

		if ( incomeAcct == null ) {
			throw new IllegalArgumentException("null income account given");
		}

		if ( receivableAcct == null ) {
			throw new IllegalArgumentException("null receivable account given");
		}

		GnucashWritableCustomerInvoice retval = new GnucashWritableCustomerInvoiceImpl(this, number, cust,
				(GnucashAccountImpl) incomeAcct, (GnucashAccountImpl) receivableAcct, openedDate, postDate, dueDate);

		super.invcMgr.addGenerInvoice(retval);
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * 
	 * @throws IllegalTransactionSplitActionException
	 * @see GnucashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnucashWritableVendorBill createWritableVendorBill(final String number, final GnucashVendor vend,
			final GnucashAccount expensesAcct, final GnucashAccount payableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException,
			IllegalTransactionSplitActionException {
		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
		}

		if ( expensesAcct == null ) {
			throw new IllegalArgumentException("null income account given");
		}

		if ( payableAcct == null ) {
			throw new IllegalArgumentException("null receivable account given");
		}

		GnucashWritableVendorBill retval = new GnucashWritableVendorBillImpl(this, number, vend,
				(GnucashAccountImpl) expensesAcct, (GnucashAccountImpl) payableAcct, openedDate, postDate, dueDate);

		super.invcMgr.addGenerInvoice(retval);
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * 
	 * @throws IllegalTransactionSplitActionException
	 * @see GnucashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnucashWritableEmployeeVoucher createWritableEmployeeVoucher(final String number, final GnucashEmployee empl,
			final GnucashAccount expensesAcct, final GnucashAccount payableAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException,
			IllegalTransactionSplitActionException {
		if ( empl == null ) {
			throw new IllegalArgumentException("null empl given");
		}

		if ( expensesAcct == null ) {
			throw new IllegalArgumentException("null income account given");
		}

		if ( payableAcct == null ) {
			throw new IllegalArgumentException("null receivable account given");
		}

		GnucashWritableEmployeeVoucher retval = new GnucashWritableEmployeeVoucherImpl(this, number, empl,
				(GnucashAccountImpl) expensesAcct, (GnucashAccountImpl) payableAcct, openedDate, postDate, dueDate);

		super.invcMgr.addGenerInvoice(retval);
		return retval;
	}

	/**
	 * FOR USE BY EXTENSIONS ONLY!
	 * 
	 * @throws WrongInvoiceTypeException
	 * @throws WrongOwnerTypeException
	 * @throws InvalidCmdtyCurrTypeException
	 * 
	 * @throws IllegalTransactionSplitActionException
	 * @see GnucashWritableFile#createWritableTransaction()
	 */
	@Override
	public GnucashWritableJobInvoice createWritableJobInvoice(final String number, final GnucashGenerJob job,
			final GnucashAccount incExpAcct, final GnucashAccount recvblPayblAcct, final LocalDate openedDate,
			final LocalDate postDate, final LocalDate dueDate)
			throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException,
			IllegalTransactionSplitActionException {
		if ( job == null ) {
			throw new IllegalArgumentException("null job given");
		}

		if ( incExpAcct == null ) {
			throw new IllegalArgumentException("null income/expenses account given");
		}

		if ( recvblPayblAcct == null ) {
			throw new IllegalArgumentException("null receivable/payable account given");
		}

		GnucashWritableJobInvoice retval = new GnucashWritableJobInvoiceImpl(this, number, job,
				(GnucashAccountImpl) incExpAcct, (GnucashAccountImpl) recvblPayblAcct, openedDate, postDate, dueDate);

		super.invcMgr.addGenerInvoice(retval);
		return retval;
	}

	/**
	 * @param invc an invoice to remove
	 * @throws IllegalArgumentException
	 * 
	 */
	@Override
	public void removeGenerInvoice(final GnucashWritableGenerInvoice invc) throws IllegalArgumentException {

		if ( invc.getPayingTransactions().size() > 0 ) {
			throw new IllegalArgumentException("cannot remove this invoice! It has payments!");
		}

		GnucashTransaction postTransaction = invc.getPostTransaction();
		if ( postTransaction != null ) {
			((GnucashWritableTransaction) postTransaction).remove();
		}

		super.invcMgr.removeGenerInvoice(invc);
		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableGenerInvoiceImpl) invc).getJwsdpPeer());
		this.decrementCountDataFor("gnc:GncInvoice");
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(final GCshID invcEntrID) {
		if ( invcEntrID == null ) {
			throw new IllegalArgumentException("null invoice entry ID given");
		}

		if ( !invcEntrID.isSet() ) {
			throw new IllegalArgumentException("invoice entry ID is not set");
		}

		GnucashGenerInvoiceEntry invcEntr = super.getGenerInvoiceEntryByID(invcEntrID);
		return new GnucashWritableGenerInvoiceEntryImpl(invcEntr);
	}

	@Override
	public Collection<GnucashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries() {
		Collection<GnucashGenerInvoiceEntry> invcEntrList = getGenerInvoiceEntries();

		if ( invcEntrList == null ) {
			throw new IllegalStateException("getGenerInvoiceEntries() returned null");
		}

		Collection<GnucashWritableGenerInvoiceEntry> retval = new ArrayList<GnucashWritableGenerInvoiceEntry>();
		for ( GnucashGenerInvoiceEntry entry : invcEntrList ) {
			retval.add((GnucashWritableGenerInvoiceEntry) entry);
		}

		return retval;
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnucashFile#getWritableCustomerByID(java.lang.String)
	 */
	@Override
	public GnucashWritableCustomer getWritableCustomerByID(final GCshID custID) {
		if ( custID == null ) {
			throw new IllegalArgumentException("null customer ID given");
		}

		if ( !custID.isSet() ) {
			throw new IllegalArgumentException("customer ID is not set");
		}

		GnucashCustomer cust = super.getCustomerByID(custID);
		return new GnucashWritableCustomerImpl((GnucashCustomerImpl) cust);
	}

	@Override
	public Collection<GnucashWritableCustomer> getWritableCustomers() {
		Collection<GnucashWritableCustomer> result = new ArrayList<GnucashWritableCustomer>();

		for ( GnucashCustomer cust : super.getCustomers() ) {
			GnucashWritableCustomer newCust = new GnucashWritableCustomerImpl((GnucashWritableCustomerImpl) cust);
			result.add(newCust);
		}

		return result;
	}

	// ----------------------------

	/**
	 * @see GnucashWritableFile#createWritableCustomer()
	 */
	@Override
	public GnucashWritableCustomer createWritableCustomer() {
		GnucashWritableCustomerImpl cust = new GnucashWritableCustomerImpl(this);
		super.custMgr.addCustomer(cust);
		return cust;
	}

	/**
	 * @param cust the customer to remove
	 */
	@Override
	public void removeCustomer(final GnucashWritableCustomer cust) {
		super.custMgr.removeCustomer(cust);
		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableCustomerImpl) cust).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnucashFile#getWritableCustomerByID(java.lang.String)
	 */
	@Override
	public GnucashWritableVendor getWritableVendorByID(final GCshID vendID) {
		if ( vendID == null ) {
			throw new IllegalArgumentException("null vendor ID given");
		}

		if ( !vendID.isSet() ) {
			throw new IllegalArgumentException("vendor ID is not set");
		}

		GnucashVendor vend = super.getVendorByID(vendID);
		return new GnucashWritableVendorImpl((GnucashVendorImpl) vend);
	}

	@Override
	public Collection<GnucashWritableVendor> getWritableVendors() {
		Collection<GnucashWritableVendor> result = new ArrayList<GnucashWritableVendor>();

		for ( GnucashVendor vend : super.getVendors() ) {
			GnucashWritableVendor newVend = new GnucashWritableVendorImpl((GnucashWritableVendorImpl) vend);
			result.add(newVend);
		}

		return result;
	}

	// ----------------------------

	/**
	 * @see GnucashWritableFile#createWritableVendor()
	 */
	@Override
	public GnucashWritableVendor createWritableVendor() {
		GnucashWritableVendorImpl vend = new GnucashWritableVendorImpl(this);
		super.vendMgr.addVendor(vend);
		return vend;
	}

	/**
	 * @param impl the vendor to remove
	 */
	@Override
	public void removeVendor(final GnucashWritableVendor vend) {
		super.vendMgr.removeVendor(vend);
		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableVendorImpl) vend).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnucashFile#getWritableCustomerByID(java.lang.String)
	 */
	@Override
	public GnucashWritableEmployee getWritableEmployeeByID(final GCshID emplID) {
		if ( emplID == null ) {
			throw new IllegalArgumentException("null employee ID given");
		}

		if ( !emplID.isSet() ) {
			throw new IllegalArgumentException("employee ID is not set");
		}

		GnucashEmployee empl = super.getEmployeeByID(emplID);
		return new GnucashWritableEmployeeImpl((GnucashEmployeeImpl) empl);
	}

	@Override
	public Collection<GnucashWritableEmployee> getWritableEmployees() {
		Collection<GnucashWritableEmployee> result = new ArrayList<GnucashWritableEmployee>();

		for ( GnucashEmployee empl : super.getEmployees() ) {
			GnucashWritableEmployee newEmpl = new GnucashWritableEmployeeImpl((GnucashWritableEmployeeImpl) empl);
			result.add(newEmpl);
		}

		return result;
	}

	// ----------------------------

	/**
	 * @see GnucashWritableFile#createWritableEmployee()
	 */
	@Override
	public GnucashWritableEmployee createWritableEmployee() {
		GnucashWritableEmployeeImpl empl = new GnucashWritableEmployeeImpl(this);
		super.emplMgr.addEmployee(empl);
		return empl;
	}

	/**
	 * @param empl the employee to remove
	 */
	@Override
	public void removeEmployee(final GnucashWritableEmployee empl) {
		emplMgr.removeEmployee(empl);
		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableEmployeeImpl) empl).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * @param jobID the id of the job to fetch
	 * @return A changeable version of the job or null of not found.
	 * @see GnucashFile#getGenerJobByID(GCshID)
	 * @see GnucashWritableFile#getGenerJobByID(GCshID)
	 */
	@Override
	public GnucashWritableGenerJob getWritableGenerJobByID(final GCshID jobID) {
		if ( jobID == null ) {
			throw new IllegalArgumentException("null job ID given");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("job ID is not set");
		}

		GnucashGenerJob generJob = super.getGenerJobByID(jobID);
		if ( generJob.getOwnerType() == GnucashGenerJob.TYPE_CUSTOMER ) {
			GnucashCustomerJob custJob = super.getCustomerJobByID(jobID);
			return new GnucashWritableCustomerJobImpl((GnucashCustomerJobImpl) custJob);
		} else if ( generJob.getOwnerType() == GnucashGenerJob.TYPE_VENDOR ) {
			GnucashVendorJob vendJob = super.getVendorJobByID(jobID);
			return new GnucashWritableVendorJobImpl((GnucashVendorJobImpl) vendJob);
		}

		return null; // Compiler happy
	}

	/**
	 * @param jnr the job-number to look for.
	 * @return the (first) jobs that have this number or null if not found
	 */
	@Override
	public GnucashWritableGenerJob getWritableGenerJobByNumber(final String jnr) {
		for ( GnucashGenerJob gnucashJob : jobMgr.getGenerJobs() ) {
			GnucashWritableGenerJob job = (GnucashWritableGenerJob) gnucashJob;
			if ( job.getNumber().equals(jnr) ) {
				return job;
			}
		}
		return null;

	}

	/**
	 * @see GnucashWritableFile#getWritableGenerJobs()
	 */
	@Override
	public Collection<GnucashWritableGenerJob> getWritableGenerJobs() {

		Collection<GnucashGenerJob> jobList = getGenerJobs();
		if ( jobList == null ) {
			throw new IllegalStateException("getGenerJobs() returned null");
		}

		Collection<GnucashWritableGenerJob> retval = new ArrayList<GnucashWritableGenerJob>();
		for ( GnucashGenerJob job : jobList ) {
			retval.add((GnucashWritableGenerJob) job);
		}
		return retval;
	}

	// ----------------------------

	/**
	 * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
	 */
	@Override
	public GnucashWritableCustomerJob createWritableCustomerJob(final GnucashCustomer cust, final String number,
			final String name) {
		if ( cust == null ) {
			throw new IllegalArgumentException("null customer given");
		}

		GnucashWritableCustomerJobImpl job = new GnucashWritableCustomerJobImpl(this, cust, number, name);
		super.jobMgr.addGenerJob(job);
		return job;
	}

	/**
	 * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
	 */
	@Override
	public GnucashWritableVendorJob createWritableVendorJob(final GnucashVendor vend, final String number,
			final String name) {
		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
		}

		GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(this, vend, number, name);
		super.jobMgr.addGenerJob(job);
		return job;
	}

	/**
	 * @param impl what to remove
	 */
	@Override
	public void removeGenerJob(final GnucashWritableGenerJob job) {
		if ( job == null ) {
			throw new IllegalArgumentException("null job given");
		}

		super.jobMgr.removeGenerJob(job);
		getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
		setModified(true);
	}

	@Override
	public void removeCustomerJob(final GnucashWritableCustomerJobImpl job) {
		super.jobMgr.removeGenerJob(job);
		getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
		setModified(true);
	}

	@Override
	public void removeVendorJob(final GnucashWritableVendorJobImpl job) {
		super.jobMgr.removeGenerJob(job);
		getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("null commodity ID given");
		}

//	if ( ! cmdtyID.isSet() ) {
//	    throw new IllegalArgumentException("commodity ID is not set");
//	}

		GnucashCommodity cmdty = super.getCommodityByQualifID(cmdtyID);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final String nameSpace, final String id) {
		GnucashCommodity cmdty = super.getCommodityByQualifID(nameSpace, id);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange,
			String id) {
		GnucashCommodity cmdty = super.getCommodityByQualifID(exchange, id);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
		GnucashCommodity cmdty = super.getCommodityByQualifID(mic, id);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType,
			String id) {
		GnucashCommodity cmdty = super.getCommodityByQualifID(secIdType, id);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByQualifID(final String qualifID) {
		GnucashCommodity cmdty = super.getCommodityByQualifID(qualifID);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByXCode(final String xCode) {
		GnucashCommodity cmdty = super.getCommodityByXCode(xCode);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr) {
		Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

		for ( GnucashCommodity cmdty : super.getCommoditiesByName(expr) ) {
			GnucashWritableCommodity newCmdty = new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	@Override
	public Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr, final boolean relaxed) {
		Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

		for ( GnucashCommodity cmdty : super.getCommoditiesByName(expr, relaxed) ) {
			GnucashWritableCommodity newCmdty = new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	@Override
	public GnucashWritableCommodity getWritableCommodityByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		GnucashCommodity cmdty = super.getCommodityByNameUniq(expr);
		return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	}

	@Override
	public Collection<GnucashWritableCommodity> getWritableCommodities() {
		Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

		for ( GnucashCommodity cmdty : super.getCommodities() ) {
			GnucashWritableCommodity newCmdty = new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
			result.add(newCmdty);
		}

		return result;
	}

	// ----------------------------

	@Override
	public GnucashWritableCommodity createWritableCommodity() {
		GnucashWritableCommodityImpl cmdty = new GnucashWritableCommodityImpl(this);
		super.cmdtyMgr.addCommodity(cmdty);
		return cmdty;
	}

	@Override
	public void removeCommodity(final GnucashWritableCommodity cmdty)
			throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException {
		if ( cmdty == null ) {
			throw new IllegalArgumentException("null commodity given");
		}

		if ( cmdty.getQualifID().toString().startsWith(GCshCmdtyCurrNameSpace.CURRENCY + GCshCmdtyCurrID.SEPARATOR) ) {
			throw new IllegalArgumentException("Currency commodities may not be removed");
		}

		if ( existPriceObjects(cmdty) ) {
			LOGGER.error("removeCommodity: Commodity with ID '" + cmdty.getQualifID() + "' cannot be removed because "
					+ "there are price objects in the Price DB that depend on it");
			throw new ObjectCascadeException();
		}

		super.cmdtyMgr.removeCommodity(cmdty);

		getRootElement().getGncBook().getBookElements().remove(((GnucashWritableCommodityImpl) cmdty).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	/**
	 * Add a new currency.<br/>
	 * If the currency already exists, add a new price-quote for it.
	 *
	 * @param pCmdtySpace        the name space (e.g. "GOODS" or "CURRENCY")
	 * @param pCmdtyId           the currency-name
	 * @param conversionFactor   the conversion-factor from the base-currency (EUR).
	 * @param pCmdtyNameFraction number of decimal-places after the comma
	 * @param pCmdtyName         common name of the new currency
	 */
	@Override
	public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
			final int pCmdtyNameFraction, final String pCmdtyName) {

		if ( conversionFactor == null ) {
			throw new IllegalArgumentException("null conversionFactor given");
		}
		if ( pCmdtySpace == null ) {
			throw new IllegalArgumentException("null comodity-space given");
		}
		if ( pCmdtyId == null ) {
			throw new IllegalArgumentException("null comodity-id given");
		}
		if ( pCmdtyName == null ) {
			throw new IllegalArgumentException("null comodity-name given");
		}
		if ( getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyId) == null ) {

			// GncCommodity newCurrency =
			// getObjectFactory().createGncV2GncBookGncCommodity();
			GncCommodity newCurrency = createGncGncCommodityType();
			newCurrency.setCmdtyFraction(pCmdtyNameFraction);
			newCurrency.setCmdtySpace(pCmdtySpace);
			newCurrency.setCmdtyId(pCmdtyId);
			newCurrency.setCmdtyName(pCmdtyName);
			newCurrency.setVersion(Const.XML_FORMAT_VERSION);
			getRootElement().getGncBook().getBookElements().add(newCurrency);
			// incrementCountDataFor("commodity");
		}
		// add price-quote
		Price.PriceCommodity currency = new Price.PriceCommodity();
		currency.setCmdtySpace(pCmdtySpace);
		currency.setCmdtyId(pCmdtyId);

		Price.PriceCurrency baseCurrency = getObjectFactory().createPricePriceCurrency();
		baseCurrency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
		baseCurrency.setCmdtyId(getDefaultCurrencyID());

		Price newQuote = getObjectFactory().createPrice();
		newQuote.setPriceSource("JGnucashLib");
		newQuote.setPriceId(getObjectFactory().createPricePriceId());
		newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
		newQuote.getPriceId().setValue(GCshID.getNew().toString());
		newQuote.setPriceCommodity(currency);
		newQuote.setPriceCurrency(baseCurrency);
		newQuote.setPriceTime(getObjectFactory().createPricePriceTime());
		newQuote.getPriceTime().setTsDate(FilePriceManager.PRICE_QUOTE_DATE_FORMAT.format(new Date()));
		newQuote.setPriceType("last");
		newQuote.setPriceValue(conversionFactor.toGnucashString());

		List<Object> bookElements = getRootElement().getGncBook().getBookElements();
		for ( Object element : bookElements ) {
			if ( element instanceof GncPricedb ) {
				GncPricedb prices = (GncPricedb) element;
				prices.getPrice().add(newQuote);
				getCurrencyTable().setConversionFactor(pCmdtySpace, pCmdtyId, conversionFactor);
				return;
			}
		}
		throw new IllegalStateException("No priceDB in Book in Gnucash-file");
	}

	// ---------------------------------------------------------------

	@Override
	public GnucashWritablePrice getWritablePriceByID(final GCshID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("null price ID given");
		}

		if ( !prcID.isSet() ) {
			throw new IllegalArgumentException("price ID is not set");
		}

		GnucashPrice prc = super.getPriceByID(prcID);
		return new GnucashWritablePriceImpl((GnucashPriceImpl) prc);
	}

	@Override
	public Collection<GnucashWritablePrice> getWritablePrices() {
		Collection<GnucashWritablePrice> result = new ArrayList<GnucashWritablePrice>();

		for ( GnucashPrice prc : super.getPrices() ) {
			GnucashWritablePrice newPrc = new GnucashWritablePriceImpl((GnucashPriceImpl) prc);
			result.add(newPrc);
		}

		return result;
	}

	private boolean existPriceObjects(GnucashWritableCommodity cmdty)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		int counter = 0;
		for ( GnucashPrice price : getPrices() ) {
			if ( price.getFromCommodity().getQualifID().equals(cmdty.getQualifID()) ) {
				counter++;
			}
		}

		if ( counter > 0 )
			return true;
		else
			return false;
	}

	// ----------------------------

	@Override
	public GnucashWritablePrice createWritablePrice() {
		GnucashWritablePrice prc = new GnucashWritablePriceImpl(this);
		super.prcMgr.addPrice(prc);
		return prc;
	}

	@Override
	public void removePrice(final GnucashWritablePrice prc) {
		super.prcMgr.removePrice(prc);

		getRootElement().getGncBook().getBookElements().remove(((GnucashWritablePriceImpl) prc).getJwsdpPeer());
		setModified(true);
	}

	// ---------------------------------------------------------------

	@Override
	public GCshWritableTaxTable getWritableTaxTableByID(GCshID taxTabID) {
		if ( taxTabID == null ) {
			throw new IllegalArgumentException("null tax table ID given");
		}

		if ( !taxTabID.isSet() ) {
			throw new IllegalArgumentException("tax table ID is not set");
		}

		GCshTaxTable taxTab = super.getTaxTableByID(taxTabID);
		return new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
	}

	@Override
	public GCshWritableTaxTable getWritableTaxTableByName(final String name) {
		GCshTaxTable taxTab = super.getTaxTableByName(name);
		return new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
	}

	/**
	 * @return all TaxTables defined in the book
	 * @see {@link GCshTaxTable}
	 */
	@Override
	public Collection<GCshWritableTaxTable> getWritableTaxTables() {
		Collection<GCshWritableTaxTable> result = new ArrayList<GCshWritableTaxTable>();

		for ( GCshTaxTable taxTab : super.getTaxTables() ) {
			GCshWritableTaxTable newTaxTab = new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
			result.add(newTaxTab);
		}

		return result;
	}

	// ---------------------------------------------------------------

	@Override
	public GCshWritableBillTerms getWritableBillTermsByID(GCshID bllTrmID) {
		if ( bllTrmID == null ) {
			throw new IllegalArgumentException("null bill terms ID given");
		}

		if ( !bllTrmID.isSet() ) {
			throw new IllegalArgumentException("tax bill terms ID is not set");
		}

		GCshBillTerms bllTrm = super.getBillTermsByID(bllTrmID);
		return new GCshWritableBillTermsImpl((GCshBillTermsImpl) bllTrm);
	}

	@Override
	public GCshWritableBillTerms getWritableBillTermsByName(final String name) {
		GCshBillTerms bllTrm = super.getBillTermsByName(name);
		return new GCshWritableBillTermsImpl((GCshBillTermsImpl) bllTrm);
	}

	/**
	 * @return all TaxTables defined in the book
	 * @see {@link GCshBillTerms}
	 */
	@Override
	public Collection<GCshWritableBillTerms> getWritableBillTerms() {
		Collection<GCshWritableBillTerms> result = new ArrayList<GCshWritableBillTerms>();

		for ( GCshBillTerms taxTab : super.getBillTerms() ) {
			GCshWritableBillTerms newTaxTab = new GCshWritableBillTermsImpl((GCshBillTermsImpl) taxTab);
			result.add(newTaxTab);
		}

		return result;
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	public Collection<GnucashWritableCustomerInvoice> getPaidWritableInvoicesForCustomer_direct(
			final GnucashCustomer cust) throws InvalidCmdtyCurrTypeException,
			WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getPaidWritableInvoicesForCustomer_direct(cust);
	}

	public Collection<GnucashWritableCustomerInvoice> getUnpaidWritableInvoicesForCustomer_direct(
			final GnucashCustomer cust) throws InvalidCmdtyCurrTypeException,
			WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getUnpaidWritableInvoicesForCustomer_direct(cust);
	}

	// ----------------------------

	public Collection<GnucashWritableVendorBill> getPaidWritableBillsForVendor_direct(final GnucashVendor vend)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableBillsForVendor_direct(vend);
	}

	public Collection<GnucashWritableVendorBill> getUnpaidWritableBillsForVendor_direct(final GnucashVendor vend)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr)
				.getUnpaidWritableBillsForVendor_direct(vend);
	}

	// ----------------------------

	public Collection<GnucashWritableEmployeeVoucher> getPaidWritableVouchersForEmployee(final GnucashEmployee empl)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableVouchersForEmployee(empl);
	}

	public Collection<GnucashWritableEmployeeVoucher> getUnpaidWritableVouchersForEmployee(final GnucashEmployee empl)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableVouchersForEmployee(empl);
	}

	// ----------------------------

	public Collection<GnucashWritableJobInvoice> getPaidWritableInvoicesForJob(final GnucashGenerJob job)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableInvoicesForJob(job);
	}

	public Collection<GnucashWritableJobInvoice> getUnpaidWritableInvoicesForJob(final GnucashGenerJob job)
			throws InvalidCmdtyCurrTypeException, WrongInvoiceTypeException,
			UnknownAccountTypeException, TaxTableNotFoundException {
		return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableInvoicesForJob(job);
	}

	// ---------------------------------------------------------------
	// Internal Helpers
	// ---------------------------------------------------------------

	protected GncAccount createGncAccountType() {
		GncAccount retval = getObjectFactory().createGncAccount();
		incrementCountDataFor("account");
		return retval;
	}

	protected GncTransaction createGncTransactionType() {
		GncTransaction retval = getObjectFactory().createGncTransaction();
		incrementCountDataFor("transaction");
		return retval;
	}

	protected GncTransaction.TrnSplits.TrnSplit createGncTransactionTypeTrnSplitsTypeTrnSplitType() {
		GncTransaction.TrnSplits.TrnSplit retval = getObjectFactory().createGncTransactionTrnSplitsTrnSplit();
		// Does not apply:
		// incrementCountDataFor();
		return retval;
	}

	// ----------------------------

	protected GncGncInvoice createGncGncInvoiceType() {
		GncGncInvoice retval = getObjectFactory().createGncGncInvoice();
		incrementCountDataFor("gnc:GncInvoice");
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncEntry createGncGncEntryType() {
		GncGncEntry retval = getObjectFactory().createGncGncEntry();
		incrementCountDataFor("gnc:GncEntry");
		return retval;
	}

	// ----------------------------

	protected GncGncCustomer createGncGncCustomerType() {
		GncGncCustomer retval = getObjectFactory().createGncGncCustomer();
		incrementCountDataFor("gnc:GncCustomer");
		return retval;
	}

	protected GncGncVendor createGncGncVendorType() {
		GncGncVendor retval = getObjectFactory().createGncGncVendor();
		incrementCountDataFor("gnc:GncVendor");
		return retval;
	}

	protected GncGncEmployee createGncGncEmployeeType() {
		GncGncEmployee retval = getObjectFactory().createGncGncEmployee();
		incrementCountDataFor("gnc:GncEmployee");
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncJob createGncGncJobType() {
		// ====== <--- sic
		GncGncJob retval = getObjectFactory().createGncGncJob();
		incrementCountDataFor("gnc:GncJob");
		return retval;
	}

	// ----------------------------

	@SuppressWarnings("exports")
	public GncCommodity createGncGncCommodityType() {
		GncCommodity retval = getObjectFactory().createGncCommodity();
		incrementCountDataFor("commodity");
		return retval;
	}

	@SuppressWarnings("exports")
	public Price createGncGncPricedbPriceType() {
		Price retval = getObjectFactory().createPrice();
		incrementCountDataFor("price");
		return retval;
	}

	// ----------------------------

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermParent createGncGncBillTermParentType() {
		GncGncBillTerm.BilltermParent retval = getObjectFactory().createGncGncBillTermBilltermParent();
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermDays createGncGncBillTermDaysType() {
		GncGncBillTerm.BilltermDays retval = getObjectFactory().createGncGncBillTermBilltermDays();
		return retval;
	}

	@SuppressWarnings("exports")
	public GncGncBillTerm.BilltermProximo createGncGncBillTermProximoType() {
		GncGncBillTerm.BilltermProximo retval = getObjectFactory().createGncGncBillTermBilltermProximo();
		return retval;
	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	private void clean() {
		for ( GnucashWritableAccount acct : getWritableAccounts() ) {
			((GnucashWritableAccountImpl) acct).clean();
		}

		for ( GnucashWritableTransaction trx : getWritableTransactions() ) {
			((GnucashWritableTransactionImpl) trx).clean();
		}

		// ::TODO: Some funny behavior here
//        for ( GnucashWritableTransactionSplit splt : getWritableTransactionSplits() ) {
//            ((GnucashWritableTransactionSplitImpl) splt).clean();
//        }

		// ------------------------

		for ( GnucashWritableGenerInvoice invc : getWritableGenerInvoices() ) {
			((GnucashWritableGenerInvoiceImpl) invc).clean();
		}

		for ( GnucashWritableGenerInvoiceEntry entr : getWritableGenerInvoiceEntries() ) {
			((GnucashWritableGenerInvoiceEntryImpl) entr).clean();
		}

		// ------------------------

		for ( GnucashWritableCustomer cust : getWritableCustomers() ) {
			((GnucashWritableCustomerImpl) cust).clean();
		}

		for ( GnucashWritableVendor vend : getWritableVendors() ) {
			((GnucashWritableVendorImpl) vend).clean();
		}

		for ( GnucashWritableEmployee empl : getWritableEmployees() ) {
			((GnucashWritableEmployeeImpl) empl).clean();
		}

		// ::TODO ?
//        for ( GnucashWritableGenerJob job : getWritableGenerJobs() ) {
//            ((GnucashWritableGenerJobImpl) job).clean();
//        }

		// ------------------------

		for ( GnucashWritableCommodity cmdty : getWritableCommodities() ) {
			((GnucashWritableCommodityImpl) cmdty).clean();
		}

		for ( GnucashWritablePrice prc : getWritablePrices() ) {
			((GnucashWritablePriceImpl) prc).clean();
		}

	}

	// ---------------------------------------------------------------
	// ::TODO Description
	// ---------------------------------------------------------------

	@Override
	public String toString() {
		String result = "GnucashWritableFileImpl [\n";

		result += "  Stats (raw):\n";
		GCshFileStats stats;
		try {
			stats = new GCshFileStats(this);

			result += "    No. of accounts:                  " + stats.getNofEntriesAccounts(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of transactions:              " + stats.getNofEntriesTransactions(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of transaction splits:        "
					+ stats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of (generic) invoices:        "
					+ stats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of (generic) invoice entries: "
					+ stats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of customers:                 " + stats.getNofEntriesCustomers(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of vendors:                   " + stats.getNofEntriesVendors(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of employees:                 " + stats.getNofEntriesEmployees(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of (generic) jobs:            " + stats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of commodities:               " + stats.getNofEntriesCommodities(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of tax tables:                " + stats.getNofEntriesTaxTables(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of bill terms:                " + stats.getNofEntriesBillTerms(GCshFileStats.Type.RAW)
					+ "\n";
			result += "    No. of prices:                    " + stats.getNofEntriesPrices(GCshFileStats.Type.RAW)
					+ "\n";
		} catch (Exception e) {
			result += "ERROR\n";
		}

		result += "]";

		return result;
	}

}
