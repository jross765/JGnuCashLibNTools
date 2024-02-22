package org.gnucash.api.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.gnucash.api.Const;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.currency.ComplexPriceTable;
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
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Price;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashAccount.Type;
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
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.hlp.FileAccountManager;
import org.gnucash.api.read.impl.hlp.FileBillTermsManager;
import org.gnucash.api.read.impl.hlp.FileCommodityManager;
import org.gnucash.api.read.impl.hlp.FileCustomerManager;
import org.gnucash.api.read.impl.hlp.FileEmployeeManager;
import org.gnucash.api.read.impl.hlp.FileInvoiceEntryManager;
import org.gnucash.api.read.impl.hlp.FileInvoiceManager;
import org.gnucash.api.read.impl.hlp.FileJobManager;
import org.gnucash.api.read.impl.hlp.FilePriceManager;
import org.gnucash.api.read.impl.hlp.FileTaxTableManager;
import org.gnucash.api.read.impl.hlp.FileTransactionManager;
import org.gnucash.api.read.impl.hlp.FileVendorManager;
import org.gnucash.api.read.impl.hlp.GnucashObjectImpl;
import org.gnucash.api.read.impl.hlp.NamespaceRemoverReader;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

/**
 * Implementation of GnucashFile that can only
 * read but not modify Gnucash-Files. <br/>
 * @see GnucashFile
 */
public class GnucashFileImpl implements GnucashFile,
                                        GnucashPubIDManager
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(GnucashFileImpl.class);

    // ---------------------------------------------------------------

    private File file;
    
    // ----------------------------

    private GncV2 rootElement;
    private GnucashObjectImpl myGnucashObject;

    // ----------------------------

    private volatile ObjectFactory myJAXBFactory;
    private volatile JAXBContext myJAXBContext;

    // ----------------------------
    
    protected FileAccountManager      acctMgr     = null;
    protected FileTransactionManager  trxMgr      = null;
    protected FileInvoiceManager      invcMgr     = null;
    protected FileInvoiceEntryManager invcEntrMgr = null;
    protected FileCustomerManager     custMgr     = null;
    protected FileVendorManager       vendMgr     = null;
    protected FileEmployeeManager     emplMgr     = null;
    protected FileJobManager          jobMgr      = null;
    protected FileCommodityManager    cmdtyMgr    = null;
    
    // ----------------------------

    protected FileTaxTableManager     taxTabMgr   = null;
    protected FileBillTermsManager    bllTrmMgr   = null; 
    
    // ----------------------------

    private final ComplexPriceTable   currencyTable = new ComplexPriceTable();
    protected FilePriceManager        prcMgr        = null;

    // ---------------------------------------------------------------

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws ClassNotFoundException 
     * @see #loadFile(File)
     */
    public GnucashFileImpl(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	super();
	loadFile(pFile);
    }

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @see #loadFile(File)
     */
    public GnucashFileImpl(final InputStream is) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	super();
	loadInputStream(is);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
	return file;
    }

    /**
     * Internal method, just sets this.file .
     *
     * @param pFile the file loaded
     */
    protected void setFile(final File pFile) {
	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}
	file = pFile;
    }

    // ----------------------------

    /**
     * loads the file and calls setRootElement.
     *
     * @param pFile the file to read
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     * @throws ClassNotFoundException 
     * @see #setRootElement(GncV2)
     */
    protected void loadFile(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {

	long start = System.currentTimeMillis();

	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}

	if (!pFile.exists()) {
	    throw new IllegalArgumentException("Given file '" + pFile.getAbsolutePath() + "' does not exist!");
	}

	setFile(pFile);

	InputStream in = new FileInputStream(pFile);
	if ( pFile.getName().endsWith(".gz") ) {
	    in = new BufferedInputStream(in);
	    in = new GZIPInputStream(in);
	} else {
	    // determine if it's gzipped by the magic bytes
	    byte[] magic = new byte[2];
	    in.read(magic);
	    in.close();

	    in = new FileInputStream(pFile);
	    in = new BufferedInputStream(in);
	    if (magic[0] == 31 && magic[1] == -117) {
		in = new GZIPInputStream(in);
	    }
	}

	loadInputStream(in);

	long end = System.currentTimeMillis();
	LOGGER.info("loadFile: GnucashFileImpl.loadFile took " + (end - start) + " ms (total) ");

    }

    protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	long start = System.currentTimeMillis();

	NamespaceRemoverReader reader = new NamespaceRemoverReader(new InputStreamReader(in, "utf-8"));
	try {
	    JAXBContext myContext = getJAXBContext();
	    Unmarshaller unmarshaller = myContext.createUnmarshaller();

	    GncV2 obj = (GncV2) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
	    long start2 = System.currentTimeMillis();
	    setRootElement(obj);
	    long end = System.currentTimeMillis();
	    LOGGER.info("loadInputStream: Took " + 
	                (end - start) + " ms (total), " + 
		        (start2 - start) + " ms (jaxb-loading), " + 
	                (end - start2) + " ms (building facades)");

	} catch (JAXBException e) {
	    LOGGER.error("loadInputStream: " + e.getMessage(), e);
	    throw new IllegalStateException(e);
	} finally {
	    reader.close();
	}
    }

    // ---------------------------------------------------------------
    
	/**
	 * Get count data for specific type.
	 *
	 * @param type  the type to set it for
	 */
	protected int getCountDataFor(final String type) {
	
		if ( type == null ) {
			throw new IllegalArgumentException("null type given");
		}
	
		if ( type.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty type given");
		}
	
		List<GncCountData> cdList = getRootElement().getGncBook().getGncCountData();
		for ( GncCountData gncCountData : cdList ) {
			if ( type.equals(gncCountData.getCdType()) ) {
				return gncCountData.getValue();
			}
		}
		
		throw new IllegalArgumentException("Unknown type '" + type + "'");
	}

    // ---------------------------------------------------------------

    /**
     * @return Returns the currencyTable.
     */
    public ComplexPriceTable getCurrencyTable() {
	return currencyTable;
    }

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "CURRENCY" .
     *
     * @return the default-currencyID to use.
     */
    public String getDefaultCurrencyID() {
	GncV2 root = getRootElement();
	if (root == null) {
	    return Const.DEFAULT_CURRENCY;
	}
	
	for (Iterator<Object> iter = getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncAccount)) {
		continue;
	    }
	    
	    GncAccount jwsdpAccount = (GncAccount) bookElement;
	    if ( jwsdpAccount.getActCommodity() != null ) {
		 if ( jwsdpAccount.getActCommodity().getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
		     return jwsdpAccount.getActCommodity().getCmdtyId();
		 }
	    }
	}
	
	// not found
	return Const.DEFAULT_CURRENCY;
    }

    // ---------------------------------------------------------------

    /**
     * @see #getAccountsByParentID(GCshID)
     */
    @Override
    public GnucashAccount getAccountByID(final GCshID acctID) {
	return acctMgr.getAccountByID(acctID);
    }

    /**
     * @param prntAcctID if null, gives all account that have no parent
     * @return the sorted collection of children of that account
     * 
     * @see #getAccountByID(GCshID)
     */
    @Override
    public Collection<GnucashAccount> getAccountsByParentID(final GCshID prntAcctID) {
        return acctMgr.getAccountsByParentID(prntAcctID);
    }

    @Override
    public Collection<GnucashAccount> getAccountsByName(final String name) {
	return acctMgr.getAccountsByName(name);
    }
    
    /**
     * @see GnucashFile#getAccountsByName(java.lang.String)
     */
    @Override
    public Collection<GnucashAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
	return acctMgr.getAccountsByName(expr, qualif, relaxed);
    }

    @Override
    public GnucashAccount getAccountByNameUniq(final String name, final boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByNameUniq(name, qualif);
    }
    
    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param nameRegEx the regular expression of the name to look for
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    @Override
    public GnucashAccount getAccountByNameEx(final String nameRegEx) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByNameEx(nameRegEx);
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param acctID   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    @Override
    public GnucashAccount getAccountByIDorName(final GCshID acctID, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorName(acctID, name);
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param acctID   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    @Override
    public GnucashAccount getAccountByIDorNameEx(final GCshID acctID, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorNameEx(acctID, name);
    }

    @Override
    public Collection<GnucashAccount> getAccountsByType(Type type) throws UnknownAccountTypeException {
    	return acctMgr.getAccountsByType(type);
    }

    @Override
    public Collection<GnucashAccount> getAccountsByTypeAndName(Type type, String acctName, 
	    						       boolean qualif, boolean relaxed) throws UnknownAccountTypeException {
	return acctMgr.getAccountsByTypeAndName(type, acctName,
						qualif, relaxed);
    }

    /**
     * @return a read-only collection of all accounts
     */
    @Override
    public Collection<GnucashAccount> getAccounts() {
        return acctMgr.getAccounts();
    }

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     * @throws UnknownAccountTypeException 
     */
    @Override
    public GnucashAccount getRootAccount() throws UnknownAccountTypeException {
	return acctMgr.getRootAccount();
    }

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     */
    @Override
    public Collection<? extends GnucashAccount> getParentlessAccounts() throws UnknownAccountTypeException {
	return acctMgr.getParentlessAccounts();
    }

    @Override
    public Collection<GCshID> getTopAccountIDs() throws UnknownAccountTypeException {
	return acctMgr.getTopAccountIDs();
    }

    @Override
    public Collection<GnucashAccount> getTopAccounts() throws UnknownAccountTypeException {
	return acctMgr.getTopAccounts();
    }

    // ---------------------------------------------------------------

    public GnucashTransaction getTransactionByID(final GCshID trxID) {
	return trxMgr.getTransactionByID(trxID);
    }

    public Collection<? extends GnucashTransaction> getTransactions() {
	return trxMgr.getTransactions();
    }

    public Collection<GnucashTransactionImpl> getTransactions_readAfresh() {
	return trxMgr.getTransactions_readAfresh();
    }

    // ---------------------------------------------------------------

    public GnucashTransactionSplit getTransactionSplitByID(final GCshID spltID) {
        return trxMgr.getTransactionSplitByID(spltID);
    }

    public Collection<GnucashTransactionSplit> getTransactionSplits() {
	return trxMgr.getTransactionSplits();
    }

    public Collection<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh() {
	return trxMgr.getTransactionSplits_readAfresh();
    }

    public Collection<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh(final GCshID trxID) {
	return trxMgr.getTransactionSplits_readAfresh(trxID);
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashGenerInvoice getGenerInvoiceByID(final GCshID invcID) {
	return invcMgr.getGenerInvoiceByID(invcID);
    }

    @Override
    public Collection<GnucashGenerInvoice> getGenerInvoicesByType(final GCshOwner.Type type) {
    	return invcMgr.getGenerInvoicesByType(type);
    }

    /**
     * @see #getPaidGenerInvoices()
     * @see #getUnpaidGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getGenerInvoices() {
	return invcMgr.getGenerInvoices();
    }
    
    // ----------------------------

    /**
     * @throws UnknownAccountTypeException 
     *  
     * @see #getUnpaidGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getPaidGenerInvoices() throws UnknownAccountTypeException {
	return invcMgr.getPaidGenerInvoices();
    }

    /**
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getUnpaidGenerInvoices() throws UnknownAccountTypeException {
	return invcMgr.getUnpaidGenerInvoices();
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidInvoicesForCustomer_viaAllJobs(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException {
	return invcMgr.getInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException {
	return invcMgr.getInvoicesForCustomer_viaAllJobs(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidInvoicesForCustomer_viaAllJobs(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidInvoicesForCustomer_viaAllJobs(cust);
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * 
     * @see #getBillsForVendor_viaAllJobs(GnucashVendor)
     * @see #getPaidBillsForVendor_direct(GnucashVendor)
     * @see #getUnpaidBillsForVendor_direct(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException {
	return invcMgr.getBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     *  
     * @see #getBillsForVendor_direct(GnucashVendor)
     * @see #getPaidBillsForVendor_viaAllJobs(GnucashVendor)
     * @see #getUnpaidBillsForVendor_viaAllJobs(GnucashVendor)
     */
    @Override
    public Collection<GnucashJobInvoice> getBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException {
	return invcMgr.getBillsForVendor_viaAllJobs(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getUnpaidBillsForVendor_viaAllJobs(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getPaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidBillsForVendor_direct(GnucashVendor)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidBillsForVendor_viaAllJobs(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidBillsForVendor_viaAllJobs(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getUnpaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidBillsForVendor_direct(GnucashVendor)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidBillsForVendor_viaAllJobs(vend);
    }
    
    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidVouchersForEmployee(GnucashEmployee)
     * @see #getUnpaidVouchersForEmployee(GnucashEmployee)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getVouchersForEmployee(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException {
	return invcMgr.getVouchersForEmployee(empl);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getUnpaidVouchersForEmployee(GnucashEmployee)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getPaidVouchersForEmployee(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidVouchersForEmployee(empl);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidVouchersForEmployee(GnucashEmployee)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getUnpaidVouchersForEmployee(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidVouchersForEmployee(empl);
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException {
	return invcMgr.getInvoicesForJob(job);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getPaidInvoicesForJob(job);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return invcMgr.getUnpaidInvoicesForJob(job);
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshID id) {
	return invcEntrMgr.getGenerInvoiceEntryByID(id);
    }

    public Collection<GnucashGenerInvoiceEntry> getGenerInvoiceEntries() {
	return invcEntrMgr.getGenerInvoiceEntries();
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashCustomer getCustomerByID(final GCshID custID) {
	return custMgr.getCustomerByID(custID);
    }

    @Override
    public Collection<GnucashCustomer> getCustomersByName(final String name) {
	return custMgr.getCustomersByName(name);
    }

    @Override
    public Collection<GnucashCustomer> getCustomersByName(final String expr, boolean relaxed) {
	return custMgr.getCustomersByName(expr, relaxed);
    }

    @Override
    public GnucashCustomer getCustomerByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return custMgr.getCustomerByNameUniq(name);
    }
    
    @Override
    public Collection<GnucashCustomer> getCustomers() {
	return custMgr.getCustomers();
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashVendor getVendorByID(GCshID vendID) {
	return vendMgr.getVendorByID(vendID);
    }

    @Override
    public Collection<GnucashVendor> getVendorsByName(final String name) {
	return vendMgr.getVendorsByName(name);
    }

    @Override
    public Collection<GnucashVendor> getVendorsByName(final String expr, final boolean relaxed) {
	return vendMgr.getVendorsByName(expr, relaxed);
    }

    @Override
    public GnucashVendor getVendorByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return vendMgr.getVendorByNameUniq(name);
    }
    
    @Override
    public Collection<GnucashVendor> getVendors() {
	return vendMgr.getVendors();
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashEmployee getEmployeeByID(final GCshID emplID) {
	return emplMgr.getEmployeeByID(emplID);
    }

    @Override
    public Collection<GnucashEmployee> getEmployeesByUserName(final String userName) {
	return emplMgr.getEmployeesByUserName(userName);
    }

    @Override
    public Collection<GnucashEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {
	return emplMgr.getEmployeesByUserName(expr, relaxed);
    }

    @Override
    public GnucashEmployee getEmployeeByUserNameUniq(final String userName) throws NoEntryFoundException, TooManyEntriesFoundException {
	return emplMgr.getEmployeeByUserNameUniq(userName);
    }
    
    @Override
    public Collection<GnucashEmployee> getEmployees() {
	return emplMgr.getEmployees();
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashGenerJob getGenerJobByID(final GCshID jobID) {
	return jobMgr.getGenerJobByID(jobID);
    }

    @Override
    public Collection<GnucashGenerJob> getGenerJobsByName(String name) {
	return jobMgr.getGenerJobsByName(name);
    }
    
    @Override
    public Collection<GnucashGenerJob> getGenerJobsByName(final String expr, final boolean relaxed) {
	return jobMgr.getGenerJobsByName(expr, relaxed);
    }
    
    @Override
    public GnucashGenerJob getGenerJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return jobMgr.getGenerJobByNameUniq(name);
    }

    @Override
    public Collection<GnucashGenerJob> getGenerJobs() {
	return jobMgr.getGenerJobs();
    }

    // ----------------------------

    @Override
    public GnucashCustomerJob getCustomerJobByID(final GCshID custID) {
	return jobMgr.getCustomerJobByID(custID);
    }

    @Override
    public Collection<GnucashCustomerJob> getCustomerJobsByName(String name) {
	return jobMgr.getCustomerJobsByName(name);
    }
    
    @Override
    public Collection<GnucashCustomerJob> getCustomerJobsByName(final String expr, final boolean relaxed) {
	return jobMgr.getCustomerJobsByName(expr, relaxed);
    }
    
    @Override
    public GnucashCustomerJob getCustomerJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return jobMgr.getCustomerJobByNameUniq(name);
    }

    @Override
    public Collection<GnucashCustomerJob> getCustomerJobs() {
	return jobMgr.getCustomerJobs();
    }

    /**
     * @param cust the customer to look for.
     * @return all jobs that have this customer, never null
     */
    public Collection<GnucashCustomerJob> getJobsByCustomer(final GnucashCustomer cust) {
	return jobMgr.getJobsByCustomer(cust);
    }

    // ----------------------------

    @Override
    public GnucashVendorJob getVendorJobByID(final GCshID vendID) {
	return jobMgr.getVendorJobByID(vendID);
    }

    @Override
    public Collection<GnucashVendorJob> getVendorJobsByName(String name) {
	return jobMgr.getVendorJobsByName(name);
    }
    
    @Override
    public Collection<GnucashVendorJob> getVendorJobsByName(final String expr, final boolean relaxed) {
	return jobMgr.getVendorJobsByName(expr, relaxed);
    }
    
    @Override
    public GnucashVendorJob getVendorJobByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return jobMgr.getVendorJobByNameUniq(name);
    }

    @Override
    public Collection<GnucashVendorJob> getVendorJobs() {
	return jobMgr.getVendorJobs();
    }

    /**
     * @param vend the customer to look for.
     * @return all jobs that have this customer, never null
     */
    public Collection<GnucashVendorJob> getJobsByVendor(final GnucashVendor vend) {
	return jobMgr.getJobsByVendor(vend);
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrID qualifID) {
	return cmdtyMgr.getCommodityByQualifID(qualifID);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final String nameSpace, final String id) {
	return cmdtyMgr.getCommodityByQualifID(nameSpace, id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id) {
	return cmdtyMgr.getCommodityByQualifID(exchange, id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
	return cmdtyMgr.getCommodityByQualifID(mic, id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
	return cmdtyMgr.getCommodityByQualifID(secIdType, id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final String qualifID) {
	return cmdtyMgr.getCommodityByQualifID(qualifID);
    }

    @Override
    public GnucashCommodity getCommodityByXCode(final String xCode) {
	return cmdtyMgr.getCommodityByXCode(xCode);
    }

    @Override
    public Collection<GnucashCommodity> getCommoditiesByName(final String expr) {
	return cmdtyMgr.getCommoditiesByName(expr);
    }
    
    @Override
    public Collection<GnucashCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
	return cmdtyMgr.getCommoditiesByName(expr, relaxed);
    }

    @Override
    public GnucashCommodity getCommodityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
	return cmdtyMgr.getCommodityByNameUniq(expr);
    }

    @Override
    public Collection<GnucashCommodity> getCommodities() {
	return cmdtyMgr.getCommodities();
    }

    // ---------------------------------------------------------------

    /**
     * @param taxTabID ID of a tax table
     * @return the identified tax table or null
     */
    @Override
    public GCshTaxTable getTaxTableByID(final GCshID taxTabID) {
	return taxTabMgr.getTaxTableByID(taxTabID);
    }

    /**
     * @param name Name of a tax table
     * @return the identified tax table or null
     */
    @Override
    public GCshTaxTable getTaxTableByName(final String name) {
	return taxTabMgr.getTaxTableByName(name);
    }

    /**
     * @return all TaxTables defined in the book
     */
    @Override
    public Collection<GCshTaxTable> getTaxTables() {
	return taxTabMgr.getTaxTables();
    }

    // ---------------------------------------------------------------

    /**
     * @param bllTrmID ID of a bill terms item
     * @return the identified bill terms item or null
     */
    @Override
    public GCshBillTerms getBillTermsByID(final GCshID bllTrmID) {
        return bllTrmMgr.getBillTermsByID(bllTrmID);
    }

    /**
     * @param name Name of a bill terms item
     * @return the identified bill-terms item or null
     */
    @Override
    public GCshBillTerms getBillTermsByName(final String name) {
	return bllTrmMgr.getBillTermsByName(name);
    }

    /**
     * @return all TaxTables defined in the book
     */
    public Collection<GCshBillTerms> getBillTerms() {
        return bllTrmMgr.getBillTerms();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GnucashPrice getPriceByID(GCshID prcID) {
        return prcMgr.getPriceByID(prcID);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GnucashPrice> getPrices() {
        return prcMgr.getPrices();
    }

//    public FixedPointNumber getLatestPrice(final String cmdtyCurrIDStr) throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException {
//      return prcMgr.getLatestPrice(cmdtyCurrIDStr);
//    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    public FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return prcMgr.getLatestPrice(cmdtyCurrID);
    }

    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    @Deprecated
    public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return prcMgr.getLatestPrice(pCmdtySpace, pCmdtyId);
    }

    // ---------------------------------------------------------------

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    public GncV2 getRootElement() {
	return rootElement;
    }

    /**
     * Set the new root-element and load all accounts, transactions,... from it.
     *
     * @param pRootElement the new root-element
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     * @throws ClassNotFoundException 
     */
    protected void setRootElement(final GncV2 pRootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	if (pRootElement == null) {
	    throw new IllegalArgumentException("null not allowed for field this.rootElement");
	}
	
	LOGGER.debug("setRootElement (read-version)");
	
	rootElement = pRootElement;

	// ---
	// Prices
	// Caution: the price manager has to be instantiated 
	// *before* loading the price database
	
        prcMgr    = new FilePriceManager(this);

	loadPriceDatabase(pRootElement);
	if (pRootElement.getGncBook().getBookSlots() == null) {
	    pRootElement.getGncBook().setBookSlots((new ObjectFactory()).createSlotsType());
	}
	
	// ---

	myGnucashObject = new GnucashObjectImpl(pRootElement.getGncBook().getBookSlots(), this);

	// ---
	// Init helper entity managers / fill maps
	
	acctMgr  = new FileAccountManager(this);

	invcMgr  = new FileInvoiceManager(this);

	// Caution: invoice entries refer to invoices, 
	// therefore they have to be loaded after them
	invcEntrMgr = new FileInvoiceEntryManager(this);

	// Caution: transactions refer to invoices, 
	// therefore they have to be loaded after them
	trxMgr   = new FileTransactionManager(this);

	custMgr  = new FileCustomerManager(this);

	vendMgr  = new FileVendorManager(this);

	emplMgr  = new FileEmployeeManager(this);

	jobMgr   = new FileJobManager(this);

	cmdtyMgr = new FileCommodityManager(this);
	
	// ---
	
	taxTabMgr = new FileTaxTableManager(this);

	bllTrmMgr = new FileBillTermsManager(this);

        // ---
	
	// check for unknown book-elements
	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    
	    if (bookElement instanceof GncTransaction) {
		continue;
	    } else if (bookElement instanceof GncSchedxaction) {
		continue;
	    } else if (bookElement instanceof GncTemplateTransactions) {
		continue;
	    } else if (bookElement instanceof GncAccount) {
		continue;
	    } else if (bookElement instanceof GncTransaction) {
		continue;
	    } else if (bookElement instanceof GncGncInvoice) {
		continue;
	    } else if (bookElement instanceof GncGncEntry) {
		continue;
	    } else if (bookElement instanceof GncGncCustomer) {
		continue;
	    } else if (bookElement instanceof GncGncVendor) {
		continue;
	    } else if (bookElement instanceof GncGncEmployee) {
		continue;
	    } else if (bookElement instanceof GncGncJob) {
		continue;
	    } else if (bookElement instanceof GncCommodity) {
		continue;
	    } else if (bookElement instanceof GncGncTaxTable) {
		continue;
	    } else if (bookElement instanceof GncGncBillTerm) {
		continue;
	    } else if (bookElement instanceof GncPricedb) {
		continue;
	    } else if (bookElement instanceof GncBudget) {
		continue;
	    }
	    
	    throw new IllegalArgumentException(
		    "<gnc:book> contains unknown element [" + bookElement.getClass().getName() + "]");
	}
    }
    
    // ---------------------------------------------------------------

    /**
     * @param pRootElement the root-element of the Gnucash-file
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    protected void loadPriceDatabase(final GncV2 pRootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	boolean noPriceDB = true;

	GncPricedb priceDB = prcMgr.getPriceDB();
	if ( priceDB.getPrice().size() > 0 )
	    noPriceDB = false;
	    
	if ( priceDB.getVersion() != 1 ) {
	    LOGGER.warn("loadPriceDatabase: The library only supports the price-DB format V. 1, " 
		    + "but the file has version " + priceDB.getVersion() + ". " 
		    + "Prices will not be loaded.");
	} else {
	    loadPriceDatabaseCore(priceDB);
	}

	if ( noPriceDB ) {
	    // no price DB in file
	    getCurrencyTable().clear();
	}
    }

    private void loadPriceDatabaseCore(GncPricedb priceDB)
	    throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
//	getCurrencyTable().clear();
//	getCurrencyTable().setConversionFactor(GCshCmdtyCurrNameSpace.CURRENCY, 
//		                               getDefaultCurrencyID(), 
//		                               new FixedPointNumber(1));

	String baseCurrency = getDefaultCurrencyID();
	
	for ( Price price : priceDB.getPrice() ) {
	    Price.PriceCommodity fromCmdtyCurr = price.getPriceCommodity();
//	    Price.PriceCurrency  toCurr = price.getPriceCurrency();
//	    System.err.println("tt " + fromCmdtyCurr.getCmdtySpace() + ":" + fromCmdtyCurr.getCmdtyID() + 
//	                       " --> " + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyID());

	    // Check if we already have a latest price for this commodity
	    // (= currency, fund, ...)
	    if ( getCurrencyTable().getConversionFactor(fromCmdtyCurr.getCmdtySpace(), fromCmdtyCurr.getCmdtyId()) != null ) {
		continue;
	    }

	    if ( fromCmdtyCurr.getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) && 
	         fromCmdtyCurr.getCmdtyId().equals(baseCurrency) ) {
		LOGGER.warn("loadPriceDatabaseCore: Ignoring price-quote for " + baseCurrency 
		    + " because " + baseCurrency + " is our base-currency.");
		continue;
	    }

	    // get the latest price in the file and insert it into
	    // our currency table
	    FixedPointNumber factor = getLatestPrice(new GCshCmdtyCurrID(fromCmdtyCurr.getCmdtySpace(), fromCmdtyCurr.getCmdtyId()));

	    if ( factor != null ) {
		getCurrencyTable().setConversionFactor(fromCmdtyCurr.getCmdtySpace(), fromCmdtyCurr.getCmdtyId(), 
			                               factor);
	    } else {
		LOGGER.warn("loadPriceDatabaseCore: The GnuCash file defines a factor for a commodity '" 
	    + fromCmdtyCurr.getCmdtySpace() + ":" + fromCmdtyCurr.getCmdtyId() + "' but has no commodity for it");
	    }
	} // for price
    }

    // ---------------------------------------------------------------

    /**
     * @return the jaxb object-factory used to create new peer-objects to extend
     *         this
     */
    @SuppressWarnings("exports")
    public ObjectFactory getObjectFactory() {
	if (myJAXBFactory == null) {
	    myJAXBFactory = new ObjectFactory();
	}
	return myJAXBFactory;
    }

    /**
     * @return the JAXB-context
     */
    protected JAXBContext getJAXBContext() {
	if (myJAXBContext == null) {
	    try {
		myJAXBContext = JAXBContext.newInstance("org.gnucash.api.generated", this.getClass().getClassLoader());
	    } catch (JAXBException e) {
		LOGGER.error("getJAXBContext: " + e.getMessage(), e);
	    }
	}
	return myJAXBContext;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public GnucashFile getGnucashFile() {
	return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserDefinedAttribute(final String aName) {
	return myGnucashObject.getUserDefinedAttribute(aName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<String> getUserDefinedAttributeKeys() {
	return myGnucashObject.getUserDefinedAttributeKeys();
    }

    // ---------------------------------------------------------------
    // In this section, we assume that all customer, vendor and job numbers
    // (internally, the IDs, not the GUIDs) are purely numeric, resp. (as
    // automatically generated by default).
    // CAUTION:
    // For customers and vendors, this may typically be usual and effective.
    // For jobs, however, things are typically different, so think twice
    // before using the job-methods!

    /**
     * Assuming that all customer numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @return
     */
    @Override
    public int getHighestCustomerNumber() {
	int highest = -1;

	for (GnucashCustomer cust : custMgr.getCustomers()) {
	    try {
		int newNum = Integer.parseInt(cust.getNumber());
		if (newNum > highest)
		    highest = newNum;
	    } catch (Exception exc) {
		// We run into this exception even when we stick to the
		// automatically generated numbers, because this API's
		// createWritableCustomer() method at first generates
		// an object whose number is equal to its GUID.
		// ==> ::TODO Adapt how a customer object is created.
		LOGGER.warn("getHighestCustomerNumber: Found customer with non-numerical number");
	    }
	}

	return highest;
    }

    /**
     * Assuming that all vendor numbers (manually set IDs, not GUIDs) are numeric as
     * generated by default.
     * 
     * @param gcshFile
     * @return
     */
    @Override
    public int getHighestVendorNumber() {
	int highest = -1;

	for (GnucashVendor vend : vendMgr.getVendors()) {
	    try {
		int newNum = Integer.parseInt(vend.getNumber());
		if (newNum > highest)
		    highest = newNum;
	    } catch (Exception exc) {
		// Cf. .getHighestCustomerNumber() above.
		// ==> ::TODO Adapt how a vendor object is created.
		LOGGER.warn("getHighestVendorNumber: Found vendor with non-numerical number");
	    }
	}

	return highest;
    }

    /**
     * Assuming that all employee numbers (manually set IDs, not GUIDs) are numeric as
     * generated by default.
     * 
     * @return
     */
    @Override
    public int getHighestEmployeeNumber() {
	int highest = -1;

	for (GnucashEmployee empl : emplMgr.getEmployees()) {
	    try {
		int newNum = Integer.parseInt(empl.getNumber());
		if (newNum > highest)
		    highest = newNum;
	    } catch (Exception exc) {
		// Cf. .getHighestCustomerNumber() above.
		// ==> ::TODO Adapt how a vendor object is created.
		LOGGER.warn("getHighestEmployeeNumber: Found employee with non-numerical number");
	    }
	}

	return highest;
    }

    /**
     * Assuming that all job numbers (manually set IDs, not GUIDs) are numeric as
     * generated by default.
     * 
     * CAUTION: As opposed to customers and vendors, it may not be a good idea to
     * actually have the job numbers generated automatically.
     * 
     * @return
     */
    @Override
    public int getHighestJobNumber() {
	int highest = -1;

	for (GnucashGenerJob job : jobMgr.getGenerJobs()) {
	    try {
		int newNum = Integer.parseInt(job.getNumber());
		if (newNum > highest)
		    highest = newNum;
	    } catch (Exception exc) {
		// We run into this exception even when we stick to the
		// automatically generated numbers, because this API's
		// createWritableCustomer() method at first generates
		// an object whose number is equal to its GUID.
		// ==> ::TODO Adapt how a customer object is created.
		LOGGER.warn("getHighestJobNumber: Found job with non-numerical number");
	    }
	}

	return highest;
    }

    // ----------------------------

    /**
     * Assuming that all customer numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @return
     */
    @Override
    public String getNewCustomerNumber() {
	int newNo = getHighestCustomerNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = GnucashPubIDManager.PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

    /**
     * Assuming that all customer numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @return
     */
    @Override
    public String getNewVendorNumber() {
	int newNo = getHighestVendorNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = GnucashPubIDManager.PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

    /**
     * Assuming that all employee numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @return
     */
    @Override
    public String getNewEmployeeNumber() {
	int newNo = getHighestEmployeeNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = GnucashPubIDManager.PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

    /**
     * Assuming that all job numbers (manually set IDs, not GUIDs) are numeric as
     * generated by default.
     * 
     * CAUTION: As opposed to customers and vendors, it may not be a good idea to
     * actually have the job numbers generated automatically.
     * 
     * @return
     */
    @Override
    public String getNewJobNumber() {
	int newNo = getHighestJobNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = GnucashPubIDManager.PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }
    
    // ---------------------------------------------------------------
    // Helpers for class FileStats_Cache
    
    @SuppressWarnings("exports")
    public FileAccountManager getAcctMgr() {
	return acctMgr;
    }
    
    @SuppressWarnings("exports")
    public FileTransactionManager getTrxMgr() {
	return trxMgr;
    }
    
    @SuppressWarnings("exports")
    public FileInvoiceManager getInvcMgr() {
	return invcMgr;
    }
    
    @SuppressWarnings("exports")
    public FileInvoiceEntryManager getInvcEntrMgr() {
	return invcEntrMgr;
    }
    
    @SuppressWarnings("exports")
    public FileCustomerManager getCustMgr() {
	return custMgr;
    }
    
    @SuppressWarnings("exports")
    public FileVendorManager getVendMgr() {
	return vendMgr;
    }
    
    @SuppressWarnings("exports")
    public FileEmployeeManager getEmplMgr() {
	return emplMgr;
    }
    
    @SuppressWarnings("exports")
    public FileJobManager getJobMgr() {
	return jobMgr;
    }
    
    @SuppressWarnings("exports")
    public FileCommodityManager getCmdtyMgr() {
	return cmdtyMgr;
    }
    
    @SuppressWarnings("exports")
    public FilePriceManager getPrcMgr() {
	return prcMgr;
    }
    
    @SuppressWarnings("exports")
    public FileTaxTableManager getTaxTabMgr() {
	return taxTabMgr;
    }
    
    @SuppressWarnings("exports")
    public FileBillTermsManager getBllTrmMgr() {
	return bllTrmMgr;
    }
    
    // ---------------------------------------------------------------
    
    public String toString() {
	String result = "GnucashFileImpl [\n";
	
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
