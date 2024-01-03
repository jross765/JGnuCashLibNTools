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
import org.gnucash.api.generated.GncCountData;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.GncV2.GncBook.GncGncBillTerm.BilltermParent;
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
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashPriceImpl;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashCommodityImpl;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashVendorImpl;
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
import org.gnucash.api.write.aux.GCshWritableTaxTable;
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
 * Extension of GnucashFileImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableFileImpl extends GnucashFileImpl 
                                     implements GnucashWritableFile 
{
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
     * @throws IOException on bsic io-problems such as a FileNotFoundException
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableFileImpl(final File file) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	super(file);
	setModified(false);
	
	acctMgr     = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
	trxMgr      = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);
	
	invcMgr     = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
	invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);

	custMgr     = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
	vendMgr     = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
	emplMgr     = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
	jobMgr      = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

	cmdtyMgr    = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
	prcMgr      = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
    }

    public GnucashWritableFileImpl(final InputStream is) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	super(is);
	
	acctMgr     = new org.gnucash.api.write.impl.hlp.FileAccountManager(this);
	trxMgr      = new org.gnucash.api.write.impl.hlp.FileTransactionManager(this);
	
	invcMgr     = new org.gnucash.api.write.impl.hlp.FileInvoiceManager(this);
	invcEntrMgr = new org.gnucash.api.write.impl.hlp.FileInvoiceEntryManager(this);
	
	custMgr     = new org.gnucash.api.write.impl.hlp.FileCustomerManager(this);
	vendMgr     = new org.gnucash.api.write.impl.hlp.FileVendorManager(this);
	emplMgr     = new org.gnucash.api.write.impl.hlp.FileEmployeeManager(this);
	jobMgr      = new org.gnucash.api.write.impl.hlp.FileJobManager(this);

	cmdtyMgr    = new org.gnucash.api.write.impl.hlp.FileCommodityManager(this);
	prcMgr      = new org.gnucash.api.write.impl.hlp.FilePriceManager(this);
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
	for (Slot slot : slots) {
	    if (slot.getSlotKey().equals(aName)) {
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
    public void setModified(final boolean pModified) {
	// boolean old = this.modified;
	modified = pModified;
	// if (propertyChange != null)
	// propertyChange.firePropertyChange("modified", old, pModified);
    }

    /**
     * @return true if this file has been modified
     */
    public boolean isModified() {
	return modified;
    }

    /**
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see {@link GnucashFileImpl#loadFile(java.io.File)}
     */
    @Override
    protected void loadFile(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	super.loadFile(pFile);
	lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
    }

    /**
     * @see GnucashWritableFile#writeFile(java.io.File)
     */
    public void writeFile(final File file) throws IOException {

	if (file == null) {
	    throw new IllegalArgumentException("null not allowed for field this file");
	}

	if (file.exists()) {
	    throw new IllegalArgumentException("Given file '" + file.getAbsolutePath() + "' does exist!");
	}

	checkAllCountData();

	setFile(file);

	OutputStream out = new FileOutputStream(file);
	out = new BufferedOutputStream(out);
	if (file.getName().endsWith(".gz")) {
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
    public long getLastWriteTime() {
	return lastWriteTime;
    }
    
    // ---------------------------------------------------------------

    /**
     * Keep the count-data up to date. The count-data is re-calculated on the fly
     * before writing but we like to keep our internal model up-to-date just to be
     * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type the type to set it for
     */
    protected void incrementCountDataFor(final String type) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
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

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
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

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (GncCountData gncCountData : l) {
	    if (type.equals(gncCountData.getCdType())) {
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
        for (Object element : bookElements) {
            if (element instanceof GncAccount) {
        	cntAccount++;
            } else if (element instanceof GncTransaction) {
        	cntTransaction++;
            } else if (element instanceof GncV2.GncBook.GncGncInvoice) {
        	cntInvoice++;
            } else if (element instanceof GncV2.GncBook.GncGncEntry) {
        	cntIncEntry++;
            } else if (element instanceof GncV2.GncBook.GncGncCustomer) {
        	cntCustomer++;
            } else if (element instanceof GncV2.GncBook.GncGncVendor) {
        	cntVendor++;
            } else if (element instanceof GncV2.GncBook.GncGncEmployee) {
        	cntEmployee++;
            } else if (element instanceof GncV2.GncBook.GncGncJob) {
        	cntJob++;
            } else if (element instanceof GncV2.GncBook.GncGncTaxTable) {
        	cntTaxTable++;
            } else if (element instanceof GncV2.GncBook.GncGncBillTerm) {
        	cntBillTerm++;
            } else if (element instanceof GncV2.GncBook.GncCommodity) {
            	cntCommodity++;
            } else if (element instanceof GncV2.GncBook.GncPricedb) {
            	cntPrice += ((GncV2.GncBook.GncPricedb) element).getPrice().size();
            } else if (element instanceof GncV2.GncBook.GncTemplateTransactions) {
        	// ::TODO
            } else if (element instanceof GncV2.GncBook.GncSchedxaction) {
        	// ::TODO
            } else if (element instanceof GncBudget) {
        	// ::TODO
            } else {
        	throw new IllegalStateException("Found unexpected element in GNC:Book: '" + element.toString() + "'");
            }
        }
    
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
     * @throws NoSuchFieldException 
     * @see GnucashFileImpl#setRootElement(GncV2)
     */
    @Override
    protected void setRootElement(final GncV2 rootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
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
    public Collection<GnucashWritableAccount> getWritableAccountsByType(final GnucashAccount.Type type) throws UnknownAccountTypeException {
	Collection<GnucashWritableAccount> retval = new ArrayList<GnucashWritableAccount>();
	for (GnucashWritableAccount acct : getWritableAccounts()) {

	    if (acct.getType() == null) {
		if (type == null) {
		    retval.add(acct);
		}
	    } else if (acct.getType() == type ) {
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
	try {
	    return new GnucashWritableAccountImpl(super.getAccountByID(acctID), true);
	} catch ( Exception exc ) {
	    LOGGER.error("getAccountByID: Could not instantiate writable account object from read-only account object (ID: " + acctID + ")");
	    throw new RuntimeException("Could not instantiate writable account object from read-only account object (ID: " + acctID + ")");
	}
    }

    /**
     * @param name the name of the account
     * @return A changeable version of the first account with that name.
     * @see GnucashFile#getAccountsByName(String)
     */
    @Override
    public GnucashWritableAccount getWritableAccountByNameUniq(final String name, final boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException {
	return (GnucashWritableAccount) super.getAccountByNameUniq(name, qualif);
    }
    
    /**
     * @return a read-only collection of all accounts
     */
    public Collection<GnucashWritableAccount> getWritableAccounts() {
	TreeSet<GnucashWritableAccount> retval = new TreeSet<GnucashWritableAccount>();
	for (GnucashAccount account : getAccounts()) {
	    retval.add((GnucashWritableAccount) account);
	}
	return retval;
    }

    /**
     * @return a read-only collection of all accounts that have no parent
     * @throws UnknownAccountTypeException 
     */
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
	Collection<? extends GnucashAccount> rootAccounts = super.getParentlessAccounts();
	if (rootAccounts.size() > 1) {
	    GnucashAccount root = null;
	    StringBuilder roots = new StringBuilder();
	    for (GnucashAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if ( gnucashAccount.getType() != null && 
	             gnucashAccount.getType() == GnucashAccount.Type.ROOT ) {
		    root = gnucashAccount;
		    continue;
		}
		roots.append(gnucashAccount.getID()).append("=\"").append(gnucashAccount.getName()).append("\" ");
	    }
	    LOGGER.warn("File has more than one root-account! Attaching excess accounts to root-account: "
		    + roots.toString());
	    ArrayList<GnucashAccount> rootAccounts2 = new ArrayList<GnucashAccount>();
	    rootAccounts2.add(root);
	    for (GnucashAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if (gnucashAccount == root) {
		    continue;
		}
		((GnucashWritableAccount) gnucashAccount).setParentAccount(root);

	    }
	    rootAccounts = rootAccounts2;
	}
	return rootAccounts;
    }
    
    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableAccount()
     */
    public GnucashWritableAccount createWritableAccount() {
	GnucashWritableAccount acct = new GnucashWritableAccountImpl(this);
	super.acctMgr.addAccount(acct);
	return acct;
    }

    /**
     * @param acct what to remove
     */
    public void removeAccount(final GnucashWritableAccount acct) {
	if (acct.getTransactionSplits().size() > 0) {
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
	try {
	    return new GnucashWritableTransactionImpl(super.getTransactionByID(trxID));
	} catch ( Exception exc ) {
	    LOGGER.error("getTransactionByID: Could not instantiate writable transaction object from read-only transaction object (ID: " + trxID + ")");
	    throw new RuntimeException("Could not instantiate writable transaction object from read-only transaction object (ID: " + trxID + ")");
	}
    }

    /**
     * @see GnucashWritableFile#getWritableTransactions()
     */
    @SuppressWarnings("unchecked")
    public Collection<? extends GnucashWritableTransaction> getWritableTransactions() {
	return (Collection<? extends GnucashWritableTransaction>) getTransactions();
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableTransaction createWritableTransaction() throws IllegalArgumentException {
	return new GnucashWritableTransactionImpl(this);
    }

    /**
     * Used by GnucashTransactionImpl.createTransaction to add a new Transaction to
     * this file.
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
     */
    protected void addTransaction(final GnucashTransactionImpl trx) throws IllegalArgumentException {
	getRootElement().getGncBook().getBookElements().add(trx.getJwsdpPeer());
	setModified(true);
	super.trxMgr.addTransaction(trx);
    }

    /**
     * @param trx what to remove
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void removeTransaction(final GnucashWritableTransaction trx) throws IllegalArgumentException {

	Collection<GnucashWritableTransactionSplit> c = new ArrayList<GnucashWritableTransactionSplit>();
	c.addAll(trx.getWritableSplits());
	for (GnucashWritableTransactionSplit element : c) {
	    element.remove();
	}

	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableTransactionImpl) trx).getJwsdpPeer());
	setModified(true);
	super.trxMgr.removeTransaction(trx);
    }

    // ---------------------------------------------------------------

    /**
     * @param invcID the unique invoice-id
     * @return A changeable version of the Invoice or null if not found.
     * @see GnucashFile#getGenerInvoiceByID(GCshID)
     */
    @Override
    public GnucashWritableGenerInvoice getWritableGenerInvoiceByID(final GCshID invcID) {
	GnucashGenerInvoice invc = super.getGenerInvoiceByID(invcID);
	return new GnucashWritableGenerInvoiceImpl((GnucashGenerInvoiceImpl) invc);
    }

    /**
     * @see GnucashWritableFile#getWritableGenerJobs()
     */
    public Collection<GnucashWritableGenerInvoice> getWritableGenerInvoices() {
	Collection<GnucashGenerInvoice> invcList = getGenerInvoices();
	
	if (invcList == null) {
	    throw new IllegalStateException("getGenerInvoices() returned null");
	}
	
	Collection<GnucashWritableGenerInvoice> retval = new ArrayList<GnucashWritableGenerInvoice>();
	for (GnucashGenerInvoice invc : invcList) {
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
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @throws  
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableCustomerInvoice createWritableCustomerInvoice(
	    final String number, 
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct,
	    final GnucashAccount receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, IllegalArgumentException {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	GnucashWritableCustomerInvoice retval = 
		new GnucashWritableCustomerInvoiceImpl(
			this, 
			number, cust,
			(GnucashAccountImpl) incomeAcct, 
			(GnucashAccountImpl) receivableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableVendorBill createWritableVendorBill(
	    final String number, 
	    final GnucashVendor vend,
	    final GnucashAccount expensesAcct,
	    final GnucashAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, IllegalArgumentException {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	GnucashWritableVendorBill retval = 
		new GnucashWritableVendorBillImpl(
			this, 
			number, vend,
			(GnucashAccountImpl) expensesAcct, 
			(GnucashAccountImpl) payableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableEmployeeVoucher createWritableEmployeeVoucher(
	    final String number, 
	    final GnucashEmployee empl,
	    final GnucashAccount expensesAcct,
	    final GnucashAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, IllegalArgumentException {
	if (empl == null) {
	    throw new IllegalArgumentException("null empl given");
	}

	GnucashWritableEmployeeVoucher retval = 
		new GnucashWritableEmployeeVoucherImpl(
			this, 
			number, empl,
			(GnucashAccountImpl) expensesAcct, 
			(GnucashAccountImpl) payableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableJobInvoice createWritableJobInvoice(
	    final String number, 
	    final GnucashGenerJob job,
	    final GnucashAccount incExpAcct,
	    final GnucashAccount recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, IllegalArgumentException {
	if (job == null) {
	    throw new IllegalArgumentException("null job given");
	}

	GnucashWritableJobInvoice retval = 
		new GnucashWritableJobInvoiceImpl(
			this, 
			number, job,
			(GnucashAccountImpl) incExpAcct, 
			(GnucashAccountImpl) recvblPayblAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * @param impl an invoice to remove
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void removeGenerInvoice(final GnucashWritableGenerInvoice impl) throws IllegalArgumentException {

	if (impl.getPayingTransactions().size() > 0) {
	    throw new IllegalArgumentException("cannot remove this invoice! It has payments!");
	}

	GnucashTransaction postTransaction = impl.getPostTransaction();
	if (postTransaction != null) {
	    ((GnucashWritableTransaction) postTransaction).remove();
	}

	super.invcMgr.removeGenerInvoice(impl);
	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableGenerInvoiceImpl) impl).getJwsdpPeer());
	this.decrementCountDataFor("gnc:GncInvoice");
	setModified(true);
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(final GCshID invcEntrID) {
	GnucashGenerInvoiceEntry invcEntr = super.getGenerInvoiceEntryByID(invcEntrID);
	return new GnucashWritableGenerInvoiceEntryImpl(invcEntr);
    }

    @Override
    public Collection<GnucashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries() {
	Collection<GnucashGenerInvoiceEntry> invcEntrList = getGenerInvoiceEntries();
	
	if (invcEntrList == null) {
	    throw new IllegalStateException("getGenerInvoiceEntries() returned null");
	}
	
	Collection<GnucashWritableGenerInvoiceEntry> retval = new ArrayList<GnucashWritableGenerInvoiceEntry>();
	for (GnucashGenerInvoiceEntry entry : invcEntrList) {
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
	GnucashCustomer cust = super.getCustomerByID(custID);
	return new GnucashWritableCustomerImpl((GnucashCustomerImpl) cust);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableCustomer()
     */
    public GnucashWritableCustomer createWritableCustomer() {
	GnucashWritableCustomerImpl cust = new GnucashWritableCustomerImpl(this);
	super.custMgr.addCustomer(cust);
	return cust;
    }

    /**
     * @param cust the customer to remove
     */
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
	GnucashVendor vend = super.getVendorByID(vendID);
	return new GnucashWritableVendorImpl((GnucashVendorImpl) vend);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableVendor()
     */
    public GnucashWritableVendor createWritableVendor() {
	GnucashWritableVendorImpl vend = new GnucashWritableVendorImpl(this);
	super.vendMgr.addVendor(vend);
	return vend;
    }

    /**
     * @param impl the vendor to remove
     */
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
	GnucashEmployee empl = super.getEmployeeByID(emplID);
	return new GnucashWritableEmployeeImpl((GnucashEmployeeImpl) empl);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableEmployee()
     */
    public GnucashWritableEmployee createWritableEmployee() {
	GnucashWritableEmployeeImpl empl = new GnucashWritableEmployeeImpl(this);
	super.emplMgr.addEmployee(empl);
	return empl;
    }

    /**
     * @param empl the employee to remove
     */
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
    public GnucashWritableGenerJob getWritableGenerJobByNumber(final String jnr) {
	for (GnucashGenerJob gnucashJob : jobMgr.getGenerJobs()) {
	    GnucashWritableGenerJob job = (GnucashWritableGenerJob) gnucashJob;
	    if (job.getNumber().equals(jnr)) {
		return job;
	    }
	}
	return null;

    }
    
    /**
     * @see GnucashWritableFile#getWritableGenerJobs()
     */
    public Collection<GnucashWritableGenerJob> getWritableGenerJobs() {

	Collection<GnucashGenerJob> jobList = getGenerJobs();
	if (jobList == null) {
	    throw new IllegalStateException("getGenerJobs() returned null");
	}
	
	Collection<GnucashWritableGenerJob> retval = new ArrayList<GnucashWritableGenerJob>();
	for (GnucashGenerJob job : jobList) {
	    retval.add((GnucashWritableGenerJob) job);
	}
	return retval;
    }
    
    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
     */
    public GnucashWritableCustomerJob createWritableCustomerJob(
	    final GnucashCustomer cust, 
	    final String number,
	    final String name) {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	GnucashWritableCustomerJobImpl job = new GnucashWritableCustomerJobImpl(this, cust, number, name);
	super.jobMgr.addGenerJob(job);
	return job;
    }

    /**
     * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
     */
    public GnucashWritableVendorJob createWritableVendorJob(
	    final GnucashVendor vend, 
	    final String number,
	    final String name) {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(this, vend, number, name);
	super.jobMgr.addGenerJob(job);
	return job;
    }

    /**
     * @param impl what to remove
     */
    public void removeGenerJob(final GnucashWritableGenerJob job) {
	super.jobMgr.removeGenerJob(job);
	getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
	setModified(true);
    }

    public void removeCustomerJob(final GnucashWritableCustomerJobImpl job) {
	super.jobMgr.removeGenerJob(job);
	getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
	setModified(true);
    }

    public void removeVendorJob(final GnucashWritableVendorJobImpl job) {
	super.jobMgr.removeGenerJob(job);
	getRootElement().getGncBook().getBookElements().remove(job.getJwsdpPeer());
	setModified(true);
    }

    // ---------------------------------------------------------------

    public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrID cmdtyID) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(cmdtyID);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByQualifID(final String nameSpace, final String id) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(nameSpace, id);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(exchange, id);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(mic, id);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(secIdType, id);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByQualifID(final String qualifID) {
	GnucashCommodity cmdty = super.getCommodityByQualifID(qualifID);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public GnucashWritableCommodity getWritableCommodityByXCode(final String xCode) {
	GnucashCommodity cmdty = super.getCommodityByXCode(xCode);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr) {
	Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

	for (GnucashCommodity cmdty : super.getCommoditiesByName(expr)) {
	    GnucashWritableCommodity newCmdty = new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	    result.add(newCmdty);
	}

	return result;
    }

    public Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr, final boolean relaxed) {
	Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

	for (GnucashCommodity cmdty : super.getCommoditiesByName(expr, relaxed)) {
	    GnucashWritableCommodity newCmdty = new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
	    result.add(newCmdty);
	}

	return result;
    }

    public GnucashWritableCommodity getWritableCommodityByNameUniq(final String expr)
	    throws NoEntryFoundException, TooManyEntriesFoundException {
	GnucashCommodity cmdty = super.getCommodityByNameUniq(expr);
	return new GnucashWritableCommodityImpl((GnucashCommodityImpl) cmdty);
    }

    public Collection<GnucashWritableCommodity> getWritableCommodities() {
	Collection<GnucashWritableCommodity> result = new ArrayList<GnucashWritableCommodity>();

	for (GnucashCommodity cmdty : super.getCommodities()) {
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
    public void removeCommodity(final GnucashWritableCommodity cmdty) throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException {
	if ( cmdty.getQualifID().toString().
		startsWith(GCshCmdtyCurrNameSpace.CURRENCY + GCshCmdtyCurrID.SEPARATOR) )
	    throw new IllegalArgumentException("Currency commodities may not be removed");
	
	if ( existPriceObjects(cmdty) )
	{
	    LOGGER.error("Commodity with ID '" + cmdty.getQualifID() + "' cannot be removed because " + 
	                 "there are price objects in the Price DB that depend on it");
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
     * @param pCmdtySpace        the namespace (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
	    final int pCmdtyNameFraction, final String pCmdtyName) {

	if (conversionFactor == null) {
	    throw new IllegalArgumentException("null conversionFactor given");
	}
	if (pCmdtySpace == null) {
	    throw new IllegalArgumentException("null comodity-space given");
	}
	if (pCmdtyId == null) {
	    throw new IllegalArgumentException("null comodity-id given");
	}
	if (pCmdtyName == null) {
	    throw new IllegalArgumentException("null comodity-name given");
	}
	if (getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyId) == null) {

	    // GncV2.GncBook.GncCommodity newCurrency = getObjectFactory().createGncV2GncBookGncCommodity();
	    GncV2.GncBook.GncCommodity newCurrency = createGncGncCommodityType();
	    newCurrency.setCmdtyFraction(pCmdtyNameFraction);
	    newCurrency.setCmdtySpace(pCmdtySpace);
	    newCurrency.setCmdtyId(pCmdtyId);
	    newCurrency.setCmdtyName(pCmdtyName);
	    newCurrency.setVersion(Const.XML_FORMAT_VERSION);
	    getRootElement().getGncBook().getBookElements().add(newCurrency);
	    // incrementCountDataFor("commodity");
	}
	// add price-quote
	GncV2.GncBook.GncPricedb.Price.PriceCommodity currency = new GncV2.GncBook.GncPricedb.Price.PriceCommodity();
	currency.setCmdtySpace(pCmdtySpace);
	currency.setCmdtyId(pCmdtyId);

	GncV2.GncBook.GncPricedb.Price.PriceCurrency baseCurrency = getObjectFactory()
		.createGncV2GncBookGncPricedbPricePriceCurrency();
	baseCurrency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	baseCurrency.setCmdtyId(getDefaultCurrencyID());

	GncV2.GncBook.GncPricedb.Price newQuote = getObjectFactory().createGncV2GncBookGncPricedbPrice();
	newQuote.setPriceSource("JGnucashLib");
	newQuote.setPriceId(getObjectFactory().createGncV2GncBookGncPricedbPricePriceId());
	newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
	newQuote.getPriceId().setValue(GCshID.getNew().toString());
	newQuote.setPriceCommodity(currency);
	newQuote.setPriceCurrency(baseCurrency);
	newQuote.setPriceTime(getObjectFactory().createGncV2GncBookGncPricedbPricePriceTime());
	newQuote.getPriceTime().setTsDate(FilePriceManager.PRICE_QUOTE_DATE_FORMAT.format(new Date()));
	newQuote.setPriceType("last");
	newQuote.setPriceValue(conversionFactor.toGnucashString());

	List<Object> bookElements = getRootElement().getGncBook().getBookElements();
	for (Object element : bookElements) {
	    if (element instanceof GncV2.GncBook.GncPricedb) {
		GncV2.GncBook.GncPricedb prices = (GncV2.GncBook.GncPricedb) element;
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
	GnucashPrice prc = super.getPriceByID(prcID);
	return new GnucashWritablePriceImpl((GnucashPriceImpl) prc);
    }

    private boolean existPriceObjects(GnucashWritableCommodity cmdty) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	int counter = 0;
	for ( GnucashPrice price : getPrices() ) {
	    if ( price.getFromCommodity().getQualifID().
		    equals(cmdty.getQualifID()) ) {
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

	for (GCshTaxTable taxTab : super.getTaxTables()) {
	    GCshWritableTaxTable newTaxTab = new GCshWritableTaxTableImpl((GCshTaxTableImpl) taxTab);
	    result.add(newTaxTab);
	}

	return result;
    }
    
    // ---------------------------------------------------------------
    // ::TODO Description
    // ---------------------------------------------------------------
    
    public Collection<GnucashWritableCustomerInvoice> getPaidWritableInvoicesForCustomer_direct(final GnucashCustomer cust) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableInvoicesForCustomer_direct(cust);
    }

    public Collection<GnucashWritableCustomerInvoice> getUnpaidWritableInvoicesForCustomer_direct(final GnucashCustomer cust) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableInvoicesForCustomer_direct(cust);
    }

    // ----------------------------
    
    public Collection<GnucashWritableVendorBill> getPaidWritableBillsForVendor_direct(final GnucashVendor vend) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableBillsForVendor_direct(vend);
    }

    public Collection<GnucashWritableVendorBill> getUnpaidWritableBillsForVendor_direct(final GnucashVendor vend) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableBillsForVendor_direct(vend);
    }
    
    // ----------------------------
    
    public Collection<GnucashWritableEmployeeVoucher> getPaidWritableVouchersForEmployee(final GnucashEmployee empl) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableVouchersForEmployee(empl);
    }

    public Collection<GnucashWritableEmployeeVoucher> getUnpaidWritableVouchersForEmployee(final GnucashEmployee empl) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getUnpaidWritableVouchersForEmployee(empl);
    }

    // ----------------------------
    
    public Collection<GnucashWritableJobInvoice> getPaidWritableInvoicesForJob(final GnucashGenerJob job) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
	return ((org.gnucash.api.write.impl.hlp.FileInvoiceManager) invcMgr).getPaidWritableInvoicesForJob(job);
    }

    public Collection<GnucashWritableJobInvoice> getUnpaidWritableInvoicesForJob(final GnucashGenerJob job) throws IllegalArgumentException, InvalidCmdtyCurrTypeException, WrongInvoiceTypeException, UnknownAccountTypeException, TaxTableNotFoundException {
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

    protected GncV2.GncBook.GncGncInvoice createGncGncInvoiceType() {
	GncV2.GncBook.GncGncInvoice retval = getObjectFactory().createGncV2GncBookGncGncInvoice();
	incrementCountDataFor("gnc:GncInvoice");
	return retval;
    }

    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncEntry createGncGncEntryType() {
	GncV2.GncBook.GncGncEntry retval = getObjectFactory().createGncV2GncBookGncGncEntry();
	incrementCountDataFor("gnc:GncEntry");
	return retval;
    }

    // ----------------------------

    protected GncV2.GncBook.GncGncCustomer createGncGncCustomerType() {
	GncV2.GncBook.GncGncCustomer retval = getObjectFactory().createGncV2GncBookGncGncCustomer();
	incrementCountDataFor("gnc:GncCustomer");
	return retval;
    }

    protected GncV2.GncBook.GncGncVendor createGncGncVendorType() {
	GncV2.GncBook.GncGncVendor retval = getObjectFactory().createGncV2GncBookGncGncVendor();
	incrementCountDataFor("gnc:GncVendor");
	return retval;
    }

    protected GncV2.GncBook.GncGncEmployee createGncGncEmployeeType() {
	GncV2.GncBook.GncGncEmployee retval = getObjectFactory().createGncV2GncBookGncGncEmployee();
	incrementCountDataFor("gnc:GncEmployee");
	return retval;
    }

    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncJob createGncGncJobType() {
 // ====== <--- sic
	GncV2.GncBook.GncGncJob retval = getObjectFactory().createGncV2GncBookGncGncJob();
	incrementCountDataFor("gnc:GncJob");
	return retval;
    }

    // ----------------------------

    
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncCommodity createGncGncCommodityType() {
	GncV2.GncBook.GncCommodity retval = getObjectFactory().createGncV2GncBookGncCommodity();
	incrementCountDataFor("commodity");
	return retval;
    }
    
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncPricedb.Price createGncGncPricedbPriceType() {
	GncV2.GncBook.GncPricedb.Price retval = getObjectFactory().createGncV2GncBookGncPricedbPrice();
	incrementCountDataFor("price");
	return retval;
    }
    
    // ----------------------------

    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncBillTerm.BilltermParent createGncGncBillTermParentType() {
	GncV2.GncBook.GncGncBillTerm.BilltermParent retval = getObjectFactory().createGncV2GncBookGncGncBillTermBilltermParent();
	return retval;
    }
    
//    @SuppressWarnings("exports")
//    public GncV2.GncBook.GncGncBillTerm.BilltermDays createGncGncBillTermDaysType() {
//	GncV2.GncBook.GncGncBillTerm.BilltermDays retval = getObjectFactory().createGncV2GncBookGncGncBillTermBilltermDays();
//	return retval;
//    }
//    
//    @SuppressWarnings("exports")
//    public GncV2.GncBook.GncGncBillTerm.BilltermProximo createGncGncBillTermProximoType() {
//	GncV2.GncBook.GncGncBillTerm.BilltermProximo retval = getObjectFactory().createGncV2GncBookGncGncBillTermBilltermProximo();
//	return retval;
//    }
    
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

	    result += "    No. of accounts:                  " + stats.getNofEntriesAccounts(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of transactions:              " + stats.getNofEntriesTransactions(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of transaction splits:        " + stats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of (generic) invoices:        " + stats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of (generic) invoice entries: " + stats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of customers:                 " + stats.getNofEntriesCustomers(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of vendors:                   " + stats.getNofEntriesVendors(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of employees:                 " + stats.getNofEntriesEmployees(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of (generic) jobs:            " + stats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW) + "\n"; 
	    result += "    No. of commodities:               " + stats.getNofEntriesCommodities(GCshFileStats.Type.RAW) + "\n";
	    result += "    No. of tax tables:                " + stats.getNofEntriesTaxTables(GCshFileStats.Type.RAW) + "\n";
	    result += "    No. of bill terms:                " + stats.getNofEntriesBillTerms(GCshFileStats.Type.RAW) + "\n";
	    result += "    No. of prices:                    " + stats.getNofEntriesPrices(GCshFileStats.Type.RAW) + "\n";
	} catch (Exception e) {
	    result += "ERROR\n"; 
	}
	
	result += "]";
	
	return result;
    }

}
