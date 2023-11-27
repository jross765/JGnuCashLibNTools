package org.gnucash.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import org.gnucash.Const;
import org.gnucash.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.basetypes.complex.GCshCmdtyID;
import org.gnucash.basetypes.complex.GCshCurrID;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.currency.ComplexPriceTable;
import org.gnucash.generated.GncAccount;
import org.gnucash.generated.GncBudget;
import org.gnucash.generated.GncCountData;
import org.gnucash.generated.GncTransaction;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.GncV2.GncBook.GncPricedb.Price.PriceCommodity;
import org.gnucash.generated.GncV2.GncBook.GncPricedb.Price.PriceCurrency;
import org.gnucash.generated.ObjectFactory;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashAccount;
import org.gnucash.read.GnucashCommodity;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerInvoiceEntry;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.GnucashTransaction;
import org.gnucash.read.GnucashTransactionSplit;
import org.gnucash.read.GnucashVendor;
import org.gnucash.read.NoEntryFoundException;
import org.gnucash.read.TooManyEntriesFoundException;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshBillTerms;
import org.gnucash.read.aux.GCshPrice;
import org.gnucash.read.aux.GCshTaxTable;
import org.gnucash.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.read.impl.aux.GCshPriceImpl;
import org.gnucash.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.read.impl.hlp.FileAccountManager;
import org.gnucash.read.impl.hlp.FileCustomerManager;
import org.gnucash.read.impl.hlp.FileEmployeeManager;
import org.gnucash.read.impl.hlp.FileGenerInvoiceManager;
import org.gnucash.read.impl.hlp.FileJobManager;
import org.gnucash.read.impl.hlp.FileVendorManager;
import org.gnucash.read.impl.hlp.NamespaceRemoverReader;
import org.gnucash.read.spec.GnucashCustomerInvoice;
import org.gnucash.read.spec.GnucashCustomerJob;
import org.gnucash.read.spec.GnucashEmployeeVoucher;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.GnucashVendorBill;
import org.gnucash.read.spec.GnucashVendorJob;
import org.gnucash.read.spec.WrongInvoiceTypeException;
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
                                        GnucashFileStats 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(GnucashFileImpl.class);

    protected static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    private static final String PADDING_TEMPLATE = "000000";

    // ---------------------------------------------------------------

    private File file;
    
    // ----------------------------

    private GncV2 rootElement;
    private GnucashObjectImpl myGnucashObject;

    // ----------------------------

    private volatile ObjectFactory myJAXBFactory;
    private volatile JAXBContext myJAXBContext;

    // ----------------------------

    protected Map<GCshID, GCshTaxTable>  taxTablesById = null;
    protected Map<GCshID, GCshBillTerms> billTermsByID = null;
    
    // ----------------------------

    protected Map<GCshID, GCshPrice>     priceById = null;

    // ----------------------------
    
    protected FileAccountManager      acctMgr = null;
    protected FileGenerInvoiceManager invcMgr = null;
    protected FileCustomerManager     custMgr = null;
    protected FileVendorManager       vendMgr = null;
    protected FileEmployeeManager     emplMgr = null;
    protected FileJobManager          jobMgr  = null;

    // ----------------------------

    protected Map<GCshID, GnucashTransaction>       transactionID2transaction;
    protected Map<GCshID, GnucashTransactionSplit>  transactionSplitID2transactionSplit;
    protected Map<GCshID, GnucashGenerInvoiceEntry> invoiceEntryID2invoiceEntry;
    protected Map<String, GnucashCommodity>         cmdtyQualifID2Cmdty; // Keys: Sic String not CmdtyCurrID
    protected Map<String, String>                   cmdtyXCode2QualifID; // Values: Sic String not CmdtyCurrID,
                                                                         // dto.
    
    // ----------------------------

    /**
     * my CurrencyTable.
     */
    private final ComplexPriceTable currencyTable = new ComplexPriceTable();

    // ---------------------------------------------------------------

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see #loadFile(File)
     */
    public GnucashFileImpl(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super();
	loadFile(pFile);
    }

    /**
     * @param pFile the file to load and initialize from
     * @throws IOException on low level reading-errors (FileNotFoundException if not
     *                     found)
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see #loadFile(File)
     */
    public GnucashFileImpl(final InputStream is) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
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
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see #setRootElement(GncV2)
     */
    protected void loadFile(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {

	long start = System.currentTimeMillis();

	if (pFile == null) {
	    throw new IllegalArgumentException("null not allowed for field this.file");
	}

	if (!pFile.exists()) {
	    throw new IllegalArgumentException("Given file '" + pFile.getAbsolutePath() + "' does not exist!");
	}

	setFile(pFile);

	InputStream in = new FileInputStream(pFile);
	if (pFile.getName().endsWith(".gz")) {
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

    protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	long start = System.currentTimeMillis();

	NamespaceRemoverReader reader = new NamespaceRemoverReader(new InputStreamReader(in, "utf-8"));
	try {
	    JAXBContext myContext = getJAXBContext();
	    Unmarshaller unmarshaller = myContext.createUnmarshaller();

	    GncV2 o = (GncV2) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
	    long start2 = System.currentTimeMillis();
	    setRootElement(o);
	    long end = System.currentTimeMillis();
	    LOGGER.info("loadInputStream: GnucashFileImpl.loadFileInputStream took " + (end - start) + " ms (total) " + (start2 - start)
		    + " ms (jaxb-loading)" + (end - start2) + " ms (building facades)");

	} catch (JAXBException e) {
	    LOGGER.error("loadInputStream: " + e.getMessage(), e);
	    throw new IllegalStateException(e);
	} finally {
	    reader.close();
	}
    }

    // ---------------------------------------------------------------

    /**
     * @return Returns the currencyTable.
     * @link #currencyTable
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
	
	return Const.DEFAULT_CURRENCY;
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getAccountByID(java.lang.String)
     */
    public GnucashAccount getAccountByID(final GCshID id) {
	return acctMgr.getAccountByID(id);
    }

    /**
     * @param id if null, gives all account that have no parent
     * @return the sorted collection of children of that account
     */
    @Override
    public Collection<GnucashAccount> getAccountsByParentID(final GCshID id) {
        return acctMgr.getAccountsByParentID(id);
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
     * @param id   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    @Override
    public GnucashAccount getAccountByIDorName(final GCshID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorName(id, name);
    }

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    @Override
    public GnucashAccount getAccountByIDorNameEx(final GCshID id, final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	return acctMgr.getAccountByIDorNameEx(id, name);
    }

    /**
     * @return a read-only collection of all accounts
     */
    public Collection<GnucashAccount> getAccounts() {
        return acctMgr.getAccounts();
    }

    // ---------------------------------------------------------------
    
    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     * @throws UnknownAccountTypeException 
     */
    public Collection<? extends GnucashAccount> getRootAccounts() throws UnknownAccountTypeException {
        try {
            Collection<GnucashAccount> retval = new TreeSet<GnucashAccount>();
    
            for (GnucashAccount account : getAccounts()) {
        	if (account.getParentAccountId() == null) {
        	    retval.add(account);
        	}
    
            }
    
            return retval;
        } catch (RuntimeException e) {
            LOGGER.error("getRootAccounts: Problem getting all root-account", e);
            throw e;
        } catch (Throwable e) {
            LOGGER.error("getRootAccounts: SERIOUS Problem getting all root-account", e);
            return new LinkedList<GnucashAccount>();
        }
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getTransactionByID(java.lang.String)
     */
    public GnucashTransaction getTransactionByID(final GCshID id) {
	if (transactionID2transaction == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashTransaction retval = transactionID2transaction.get(id);
	if (retval == null) {
	    LOGGER.warn("getTransactionByID: No Transaction with id '" + id + "'. We know " + transactionID2transaction.size()
		    + " transactions.");
	}
	return retval;
    }

    /**
     * @see GnucashFile#getTransactions()
     */
    public Collection<? extends GnucashTransaction> getTransactions() {
	if (transactionID2transaction == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(transactionID2transaction.values());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getTransactionByID(java.lang.String)
     */
    public GnucashTransactionSplit getTransactionSplitByID(final GCshID id) {
        if (transactionSplitID2transactionSplit == null) {
            throw new IllegalStateException("no root-element loaded");
        }
    
        GnucashTransactionSplit retval = transactionSplitID2transactionSplit.get(id);
        if (retval == null) {
            LOGGER.warn("getTransactionSplitByID: No Transaction-Split with id '" + id + "'. We know "
        	    + transactionSplitID2transactionSplit.size() + " transaction splits.");
        }
        return retval;
    }

    public Collection<GnucashTransactionSplit> getTransactionSplits() {
	if (transactionSplitID2transactionSplit == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(transactionSplitID2transactionSplit.values());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerInvoiceByID(java.lang.String)
     */
    @Override
    public GnucashGenerInvoice getGenerInvoiceByID(final GCshID id) {
	return invcMgr.getGenerInvoiceByID(id);
    }

    /**
     * @see GnucashFile#getGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getGenerInvoices() {
	return invcMgr.getGenerInvoices();
    }
    
    // ----------------------------

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see GnucashFile#getPaidGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getPaidGenerInvoices() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidGenerInvoices();
    }

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see GnucashFile#getUnpaidGenerInvoices()
     */
    @Override
    public Collection<GnucashGenerInvoice> getUnpaidGenerInvoices() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidGenerInvoices();
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getInvoicesForCustomer_viaAllJobs(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidInvoicesForCustomer_viaAllJobs(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidInvoicesForCustomer_direct(cust);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidInvoicesForCustomer_viaAllJobs(cust);
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getBillsForVendor_viaAllJobs(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getPaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidBillsForVendor_viaAllJobs(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashVendorBill> getUnpaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidBillsForVendor_direct(vend);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidBillsForVendor_viaAllJobs(vend);
    }
    
    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getVouchersForEmployee_direct(empl);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getPaidVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidVouchersForEmployee_direct(empl);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    @Override
    public Collection<GnucashEmployeeVoucher> getUnpaidVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidVouchersForEmployee_direct(empl);
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getInvoicesForJob(job);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getPaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getPaidInvoicesForJob(job);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return invcMgr.getUnpaidInvoicesForJob(job);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerInvoiceByID(java.lang.String)
     */
    @Override
    public GnucashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshID id) {
	if (invoiceEntryID2invoiceEntry == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashGenerInvoiceEntry retval = invoiceEntryID2invoiceEntry.get(id);
	if (retval == null) {
	    LOGGER.error("No (generic) Invoice-Entry with id '" + id + "'. " + 
	                 "We know " + invoiceEntryID2invoiceEntry.size() + " accounts.");
	}

	return retval;
    }

    /**
     * @see GnucashFile#getGenerInvoices()
     */
    public Collection<GnucashGenerInvoiceEntry> getGenerInvoiceEntries() {

	Collection<GnucashGenerInvoiceEntry> c = invoiceEntryID2invoiceEntry.values();

	ArrayList<GnucashGenerInvoiceEntry> retval = new ArrayList<GnucashGenerInvoiceEntry>(c);
	Collections.sort(retval);

	return retval;
    }

    // ---------------------------------------------------------------

    @Override
    public GnucashCustomer getCustomerByID(final GCshID id) {
	return custMgr.getCustomerByID(id);
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
    public GnucashVendor getVendorByID(GCshID id) {
	return vendMgr.getVendorByID(id);
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
    public GnucashEmployee getEmployeeByID(final GCshID id) {
	return emplMgr.getEmployeeByID(id);
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
    public GnucashGenerJob getGenerJobByID(final GCshID id) {
	return jobMgr.getGenerJobByID(id);
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
    public GnucashCustomerJob getCustomerJobByID(final GCshID id) {
	return jobMgr.getCustomerJobByID(id);
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

    // ----------------------------

    @Override
    public GnucashVendorJob getVendorJobByID(final GCshID id) {
	return jobMgr.getVendorJobByID(id);
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

    // ---------------------------------------------------------------

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrID qualifID) {
	return getCommodityByQualifID(qualifID.toString());
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final String nameSpace, final String id) {
	return getCommodityByQualifID(nameSpace + GCshCmdtyCurrID.SEPARATOR + id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id) {
	return getCommodityByQualifID(exchange.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
	return getCommodityByQualifID(mic.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
	return getCommodityByQualifID(secIdType.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    @Override
    public GnucashCommodity getCommodityByQualifID(final String qualifID) {
	if (qualifID == null) {
	    throw new IllegalStateException("null string given");
	}

	if (qualifID.trim().equals("")) {
	    throw new IllegalStateException("Search string is empty");
	}

	if (cmdtyQualifID2Cmdty == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashCommodity retval = cmdtyQualifID2Cmdty.get(qualifID.trim());
	if (retval == null) {
	    LOGGER.warn("getCommodityByQualifID: No Commodity with qualified id '" + qualifID + "'. We know " + cmdtyQualifID2Cmdty.size()
		    + " commodities.");
	}
	
	return retval;
    }

    @Override
    public GnucashCommodity getCommodityByXCode(final String xCode) {
	if ( cmdtyQualifID2Cmdty == null ||
             cmdtyXCode2QualifID == null ) {
	    throw new IllegalStateException("no root-element(s) loaded");
	}

	if ( cmdtyQualifID2Cmdty.size() != cmdtyXCode2QualifID.size() ) {
	    // CAUTION: Don't throw an exception, at least not in all cases,
	    // because this is not necessarily an error: Only if the GnuCash
	    // file does not contain quotes for foreign currencies (i.e. currency-
	    // commodities but only security-commodities is this an error.
	    // throw new IllegalStateException("Sizes of root elements are not equal");
	    LOGGER.debug("getCommodityByXCode: Sizes of root elements are not equal.");
	}
	
	String qualifIDStr = cmdtyXCode2QualifID.get(xCode);
	if (qualifIDStr == null) {
	    LOGGER.warn("getCommodityByXCode: No Commodity with X-Code '" + xCode + "'. We know " + cmdtyXCode2QualifID.size() + " commodities in map 2.");
	}
	
	GnucashCommodity retval = cmdtyQualifID2Cmdty.get(qualifIDStr);
	if (retval == null) {
	    LOGGER.warn("getCommodityByXCode: No Commodity with qualified ID '" + qualifIDStr + "'. We know " + cmdtyQualifID2Cmdty.size() + " commodities in map 1.");
	}
	
	return retval;
    }

    @Override
    public Collection<GnucashCommodity> getCommoditiesByName(final String expr) {
	return getCommoditiesByName(expr, true);
    }
    
    @Override
    public Collection<GnucashCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
	if (cmdtyQualifID2Cmdty == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	Collection<GnucashCommodity> result = new ArrayList<GnucashCommodity>();

	for ( GnucashCommodity cmdty : getCommodities() ) {
	    if ( cmdty.getName() != null ) // yes, that can actually happen! 
	    {
		if ( relaxed ) {
		    if ( cmdty.getName().trim().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(cmdty);
		    }
		} else {
		    if ( cmdty.getName().equals(expr) ) {
			result.add(cmdty);
		    }
		}
	    }
	}
	
	return result;
    }

    @Override
    public GnucashCommodity getCommodityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashCommodity> cmdtyList = getCommoditiesByName(expr, false);
	if ( cmdtyList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( cmdtyList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return cmdtyList.iterator().next();
    }

    @Override
    public Collection<GnucashCommodity> getCommodities() {
	if (cmdtyQualifID2Cmdty == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	return cmdtyQualifID2Cmdty.values();
    }

    // ---------------------------------------------------------------

    /**
     * @param id ID of a tax table
     * @return the identified tax table or null
     */
    @Override
    public GCshTaxTable getTaxTableByID(final GCshID id) {
	if (taxTablesById == null) {
	    getTaxTables();
	}
	
	return taxTablesById.get(id);
    }

    /**
     * @param name Name of a tax table
     * @return the identified tax table or null
     */
    @Override
    public GCshTaxTable getTaxTableByName(final String name) {
	if (taxTablesById == null) {
	    getTaxTables();
	}
	
	for (GCshTaxTable taxTab : taxTablesById.values()) {
	    if (taxTab.getName().equals(name)) {
		return taxTab;
	    }
	}

	return null;
    }

    /**
     * @return all TaxTables defined in the book
     * @link GnucashTaxTable
     */
    @Override
    public Collection<GCshTaxTable> getTaxTables() {
	if (taxTablesById == null) {
	    taxTablesById = new HashMap<GCshID, GCshTaxTable>();

	    List<Object> bookElements = this.getRootElement().getGncBook().getBookElements();
	    for (Object bookElement : bookElements) {
		if (!(bookElement instanceof GncV2.GncBook.GncGncTaxTable)) {
		    continue;
		}
		GncV2.GncBook.GncGncTaxTable jwsdpPeer = (GncV2.GncBook.GncGncTaxTable) bookElement;
		GCshTaxTableImpl taxTab = new GCshTaxTableImpl(jwsdpPeer, this);
		taxTablesById.put(taxTab.getId(), taxTab);
	    }
	}

	return taxTablesById.values();
    }

    // ---------------------------------------------------------------

    /**
     * @param id ID of a bill terms item
     * @return the identified bill terms item or null
     */
    @Override
    public GCshBillTerms getBillTermsByID(final GCshID id) {
        if (billTermsByID == null) {
            getBillTerms();
        }
        
        return billTermsByID.get(id);
    }

    /**
     * @param name Name of a bill terms item
     * @return the identified bill-terms item or null
     */
    @Override
    public GCshBillTerms getBillTermsByName(final String name) {
	if (billTermsByID == null) {
	    getBillTerms();
	}
	
	for (GCshBillTerms billTerms : billTermsByID.values()) {
	    if (billTerms.getName().equals(name)) {
		return billTerms;
	    }
	}

	return null;
    }

    /**
     * @return all TaxTables defined in the book
     * @link GnucashTaxTable
     */
    public Collection<GCshBillTerms> getBillTerms() {
        if (billTermsByID == null) {
            billTermsByID = new HashMap<GCshID, GCshBillTerms>();

            List<Object> bookElements = this.getRootElement().getGncBook().getBookElements();
            for (Object bookElement : bookElements) {
                if (!(bookElement instanceof GncV2.GncBook.GncGncBillTerm)) {
                    continue;
                }
                GncV2.GncBook.GncGncBillTerm jwsdpPeer = (GncV2.GncBook.GncGncBillTerm) bookElement;
                GCshBillTermsImpl billTerms = new GCshBillTermsImpl(jwsdpPeer);
                billTermsByID.put(billTerms.getId(), billTerms);
            }
        }

        return billTermsByID.values();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshPrice getPriceByID(GCshID id) {
        if (priceById == null) {
            getPrices();
        }
        
        return priceById.get(id);
    }

    protected GncV2.GncBook.GncPricedb getPriceDB() {
	List<Object> bookElements = this.getRootElement().getGncBook().getBookElements();
	for ( Object bookElement : bookElements ) {
	    if ( bookElement instanceof GncV2.GncBook.GncPricedb ) {
		return (GncV2.GncBook.GncPricedb) bookElement;
	    } 
	}
	
	return null; // Compiler happy
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GCshPrice> getPrices() {
        if (priceById == null) {
            priceById = new HashMap<GCshID, GCshPrice>();

            GncV2.GncBook.GncPricedb priceDB = getPriceDB();
            List<GncV2.GncBook.GncPricedb.Price> prices = priceDB.getPrice();
            for ( GncV2.GncBook.GncPricedb.Price jwsdpPeer : prices ) {
        	GCshPriceImpl price = new GCshPriceImpl(jwsdpPeer, this);
        	priceById.put(price.getId(), price);
            }
        } 

        return priceById.values();
    }

//    public FixedPointNumber getLatestPrice(final String cmdtyCurrIDStr) throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException {
//      try {
//        // See if it's a currency
//        GCshCurrID currID = new GCshCurrID(cmdtyCurrIDStr);
//	    return getLatestPrice(currID);
//      } catch ( Exception exc ) {
//        // It's a security
//	    GCshCmdtyID cmdtyID = new GCshCmdtyID(GCshCmdtyCurrID.Type.SECURITY_GENERAL, cmdtyCurrIDStr);
//	    return getLatestPrice(cmdtyID);
//      }
//    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    public FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return getLatestPrice(cmdtyCurrID, 0);
    }

    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    @Deprecated
    public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return getLatestPrice(new GCshCmdtyCurrID(pCmdtySpace, pCmdtyId), 0);
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
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     */
    protected void setRootElement(final GncV2 pRootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	if (pRootElement == null) {
	    throw new IllegalArgumentException("null not allowed for field this.rootElement");
	}
	rootElement = pRootElement;

	// fill prices

	loadPriceDatabase(pRootElement);
	if (pRootElement.getGncBook().getBookSlots() == null) {
	    pRootElement.getGncBook().setBookSlots((new ObjectFactory()).createSlotsType());
	}
	myGnucashObject = new GnucashObjectImpl(pRootElement.getGncBook().getBookSlots(), this);

	// Init helper entiy managers / fill maps
	acctMgr = new FileAccountManager(this);

	invcMgr = new FileGenerInvoiceManager(this);

	// Caution: invoice entries refer to invoices, therefore they must be loaded after
	// them
	initGenerInvoiceEntryMap(pRootElement);

	// Caution: transactions refer to invoices, therefore they must be 
	// loaded after them
	initTransactionMap(pRootElement);

	custMgr = new FileCustomerManager(this);

	vendMgr = new FileVendorManager(this);

	emplMgr = new FileEmployeeManager(this);

	jobMgr  = new FileJobManager(this);

	initCommodityMap(pRootElement);

	// check for unknown book-elements
	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    
	    if (bookElement instanceof GncTransaction) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncSchedxaction) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncTemplateTransactions) {
		continue;
	    } else if (bookElement instanceof GncAccount) {
		continue;
	    } else if (bookElement instanceof GncTransaction) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncInvoice) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncEntry) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncCustomer) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncVendor) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncEmployee) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncJob) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncCommodity) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncPricedb) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncTaxTable) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncBillTerm) {
		continue;
	    } else if (bookElement instanceof GncV2.GncBook.GncGncVendor.VendorTerms) {
		continue;
	    } else if (bookElement instanceof GncBudget) {
		continue;
	    }
	    
	    throw new IllegalArgumentException(
		    "<gnc:book> contains unknown element [" + bookElement.getClass().getName() + "]");
	}
    }

    private void initTransactionMap(final GncV2 pRootElement) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	transactionID2transaction = new HashMap<GCshID, GnucashTransaction>();
	transactionSplitID2transactionSplit = new HashMap<GCshID, GnucashTransactionSplit>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncTransaction)) {
		continue;
	    }
	    GncTransaction jwsdpTrx = (GncTransaction) bookElement;

	    try {
		GnucashTransactionImpl trx = createTransaction(jwsdpTrx);
		transactionID2transaction.put(trx.getId(), trx);
		for (GnucashTransactionSplit splt : trx.getSplits()) {
		    transactionSplitID2transactionSplit.put(splt.getId(), splt);
		}
	    } catch (RuntimeException e) {
		LOGGER.error("initTransactionMap: [RuntimeException] Problem in " + getClass().getName() + ".initTransactionMap: "
			+ "ignoring illegal Transaction-Entry with id=" + jwsdpTrx.getTrnId().getValue(), e);
	    }
	} // for

	LOGGER.debug("initTransactionMap: No. of entries in transaction map: " + transactionID2transaction.size());
    }

    private void initGenerInvoiceEntryMap(final GncV2 pRootElement) {
	invoiceEntryID2invoiceEntry = new HashMap<GCshID, GnucashGenerInvoiceEntry>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncEntry)) {
		continue;
	    }
	    GncV2.GncBook.GncGncEntry jwsdpInvcEntr = (GncV2.GncBook.GncGncEntry) bookElement;

	    try {
		GnucashGenerInvoiceEntry invcEntr = createGenerInvoiceEntry(jwsdpInvcEntr);
		invoiceEntryID2invoiceEntry.put(invcEntr.getId(), invcEntr);
	    } catch (RuntimeException e) {
		LOGGER.error("initGenerInvoiceEntryMap: [RuntimeException] Problem in " + getClass().getName() + ".initInvoiceEntryMap: "
			+ "ignoring illegal (generic) Invoice-Entry-Entry with id="
			+ jwsdpInvcEntr.getEntryGuid().getValue(), e);
	    }
	} // for

	LOGGER.debug("initGenerInvoiceEntryMap: No. of entries in (generic) invoice-entry map: " + invoiceEntryID2invoiceEntry.size());
    }

    private void initCommodityMap(final GncV2 pRootElement) {
	initCommodityMap1(pRootElement);
	initCommodityMap2(pRootElement);
    }
    
    private void initCommodityMap1(final GncV2 pRootElement) {
    cmdtyQualifID2Cmdty = new HashMap<String, GnucashCommodity>();

    for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
        Object bookElement = iter.next();
        if (!(bookElement instanceof GncV2.GncBook.GncCommodity)) {
        continue;
        }
        GncV2.GncBook.GncCommodity jwsdpCmdty = (GncV2.GncBook.GncCommodity) bookElement;

        try {
            GnucashCommodityImpl cmdty = createCommodity(jwsdpCmdty);
            try {
        	cmdtyQualifID2Cmdty.put(cmdty.getQualifId().toString(), cmdty);
            } catch (InvalidCmdtyCurrTypeException e) {
        	LOGGER.error("initCommodityMap1: Could not add Commodity to map: " + cmdty.toString());
            }
        } catch (RuntimeException e) {
            LOGGER.error("initCommodityMap1: [RuntimeException] Problem in " + getClass().getName() + ".initCommodityMap: "
        	    + "ignoring illegal Commodity entry with id=" + jwsdpCmdty.getCmdtyId(), e);
        }
    } // for

    LOGGER.debug("initCommodityMap1: No. of entries in Commodity map (1): " + cmdtyQualifID2Cmdty.size());
    }

    private void initCommodityMap2(final GncV2 pRootElement) {
    cmdtyXCode2QualifID = new HashMap<String, String>();

    for ( String qualifID : cmdtyQualifID2Cmdty.keySet() ) {
	GnucashCommodity cmdty = cmdtyQualifID2Cmdty.get(qualifID);
	try {
	    cmdtyXCode2QualifID.put(cmdty.getXCode(), cmdty.getQualifId().toString());
	} catch (InvalidCmdtyCurrTypeException e) {
	    LOGGER.error("initCommodityMap2: Could not add element to map: " + cmdty.getXCode());
	}
    } 

    LOGGER.debug("initCommodityMap2: No. of entries in Commodity map (2): " + cmdtyXCode2QualifID.size());
    }

    /**
     * @param pRootElement the root-element of the Gnucash-file
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    private void loadPriceDatabase(final GncV2 pRootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	boolean noPriceDB = true;

	GncV2.GncBook.GncPricedb priceDB = getPriceDB();
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

    private void loadPriceDatabaseCore(GncV2.GncBook.GncPricedb priceDB)
	    throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
//	getCurrencyTable().clear();
//	getCurrencyTable().setConversionFactor(GCshCmdtyCurrNameSpace.CURRENCY, 
//		                               getDefaultCurrencyID(), 
//		                               new FixedPointNumber(1));

	String baseCurrency = getDefaultCurrencyID();
	
	for ( GncV2.GncBook.GncPricedb.Price price : priceDB.getPrice() ) {
	    GncV2.GncBook.GncPricedb.Price.PriceCommodity fromCmdtyCurr = price.getPriceCommodity();
//	    GncV2.GncBook.GncPricedb.Price.PriceCurrency  toCurr = price.getPriceCurrency();
//	    System.err.println("tt " + fromCmdtyCurr.getCmdtySpace() + ":" + fromCmdtyCurr.getCmdtyId() + 
//	                       " --> " + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId());

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

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @param depth       used for recursion. Allways call with '0' for aborting
     *                    recursive quotes (quotes to other then the base- currency)
     *                    we abort if the depth reached 6.
     * @return the latest price-quote in the gnucash-file in the default-currency
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @see {@link GnucashFile#getLatestPrice(String, String)}
     * @see #getDefaultCurrencyID()
     */
    private FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID, final int depth) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	if (cmdtyCurrID == null) {
	    throw new IllegalArgumentException("null parameter 'cmdtyCurrID' given");
	}
	// System.err.println("depth: " + depth);

	Date latestDate = null;
	FixedPointNumber latestQuote = null;
	FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
	final int maxRecursionDepth = 5; // ::MAGIC

	GncV2.GncBook.GncPricedb priceDB = getPriceDB();
	for ( GncV2.GncBook.GncPricedb.Price priceQuote : priceDB.getPrice() ) {
	    if (priceQuote == null) {
		LOGGER.warn("getLatestPrice: GnuCash file contains null price-quotes - there may be a problem with JWSDP");
		continue;
	    }
		    
	    PriceCommodity fromCmdtyCurr = priceQuote.getPriceCommodity();
	    PriceCurrency  toCurr        = priceQuote.getPriceCurrency();

	    if ( fromCmdtyCurr == null ) {
		LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without from-commodity/currency: '"
			+ priceQuote.toString() + "'");
		continue;
	    }
				
	    if ( toCurr == null ) {
		LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without to-currency: '"
			+ priceQuote.toString() + "'");
		continue;
	    }

	    try {
		if (fromCmdtyCurr.getCmdtySpace() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without namespace: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
			    
		if (fromCmdtyCurr.getCmdtyId() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without code: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
				    
		if (toCurr.getCmdtySpace() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without namespace: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
					    
		if (toCurr.getCmdtyId() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without code: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		if (priceQuote.getPriceTime() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without timestamp id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		if (priceQuote.getPriceValue() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without value id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		/*
		 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
		 * priceQuote.getPriceType() == null) {
		 * LOGGER.warn("getLatestPrice: GnuCash file contains FUND-price-quotes" + " with no type id='"
		 * + priceQuote.getPriceId().getValue() + "'"); continue; }
		 */
		    
		if ( ! ( fromCmdtyCurr.getCmdtySpace().equals(cmdtyCurrID.getNameSpace()) && 
		         fromCmdtyCurr.getCmdtyId().equals(cmdtyCurrID.getCode()) ) ) {
		    continue;
		}
		    
		/*
		 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
		 * (priceQuote.getPriceType() == null ||
		 * !priceQuote.getPriceType().equals("last") )) {
		 * LOGGER.warn("getLatestPrice: ignoring FUND-price-quote of unknown type '" +
		 * priceQuote.getPriceType() + "' expecting 'last' "); continue; }
		 */

		// BEGIN core
		if ( ! toCurr.getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
		    // is commodity
		    if ( depth > maxRecursionDepth ) {
			LOGGER.warn("getLatestPrice: Ignoring price-quote that is not in an ISO4217-currency" 
				+ " but in '" + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId() + "'");
			continue;
		    }
		    factor = getLatestPrice(new GCshCmdtyID(toCurr.getCmdtySpace(), toCurr.getCmdtyId()), depth + 1);
		} else {
		    // is currency
		    if ( ! toCurr.getCmdtyId().equals(getDefaultCurrencyID()) ) {
			if ( depth > maxRecursionDepth ) {
			    LOGGER.warn("Ignoring price-quote that is not in " + getDefaultCurrencyID()
			    + " but in '" + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId() + "'");
			    continue;
			}
			factor = getLatestPrice(new GCshCurrID(toCurr.getCmdtyId()), depth + 1);
		    }
		}
		// END core

		Date date = PRICE_QUOTE_DATE_FORMAT.parse(priceQuote.getPriceTime().getTsDate());

		if (latestDate == null || latestDate.before(date)) {
		    latestDate = date;
		    latestQuote = new FixedPointNumber(priceQuote.getPriceValue());
		    LOGGER.debug("getLatestPrice: getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
		    	+ "') converted " + latestQuote + " <= " + priceQuote.getPriceValue());
		}

	    } catch (NumberFormatException e) {
		LOGGER.error("getLatestPrice: [NumberFormatException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    } catch (ParseException e) {
		LOGGER.error("getLatestPrice: [ParseException] Problem in " + getClass().getName() + " "
			+ cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '"
			+ priceQuote + "'", e);
	    } catch (NullPointerException e) {
		LOGGER.error("getLatestPrice: [NullPointerException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    } catch (ArithmeticException e) {
		LOGGER.error("getLatestPrice: [ArithmeticException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    }
	} // for priceQuote

	LOGGER.debug("getLatestPrice: " + getClass().getName() + ".getLatestPrice(pCmdtyCurrID='"
		+ cmdtyCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

	if (latestQuote == null) {
	    return null;
	}

	if (factor == null) {
	    factor = new FixedPointNumber(1);
	}

	return factor.multiply(latestQuote);
    }

    // ---------------------------------------------------------------

    /**
     * @param jwsdpTrx the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashTransaction to wrap the given jaxb-object.
     */
    protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
	GnucashTransactionImpl trx = new GnucashTransactionImpl(jwsdpTrx, this);
	return trx;
    }

    /**
     * @param jwsdpInvcEntr the JWSDP-peer (parsed xml-element) to fill our object
     *                      with
     * @return the new GnucashInvoiceEntry to wrap the given jaxb-object.
     */
    protected GnucashGenerInvoiceEntry createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	GnucashGenerInvoiceEntry entr = new GnucashGenerInvoiceEntryImpl(jwsdpInvcEntr, this);
	return entr;
    }

    /**
     * @param jwsdpCmdty the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashCommodity to wrap the given JAXB object.
     */
    protected GnucashCommodityImpl createCommodity(final GncV2.GncBook.GncCommodity jwsdpCmdty) {
      GnucashCommodityImpl cmdty = new GnucashCommodityImpl(jwsdpCmdty, this);
    return cmdty;
    }

    // ----------------------------

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
		myJAXBContext = JAXBContext.newInstance("org.gnucash.generated", this.getClass().getClassLoader());
	    } catch (JAXBException e) {
		LOGGER.error("getJAXBContext: " + e.getMessage(), e);
	    }
	}
	return myJAXBContext;
    }

    /**
     * @param type the type-string to look for
     * @return the count-data saved in the xml-file
     */
    protected GncCountData findCountDataByType(final String type) {
	for (Iterator<GncCountData> iter = getRootElement().getGncBook().getGncCountData().iterator(); iter.hasNext();) {
	    GncCountData count = (GncCountData) iter.next();
	    if (count.getCdType().equals(type)) {
		return count;
	    }
	}
	return null;
    }

    // ---------------------------------------------------------------

    /**
     * @param cust the customer to look for.
     * @return all jobs that have this customer, never null
     */
    public Collection<GnucashCustomerJob> getJobsByCustomer(final GnucashCustomer cust) {
	return jobMgr.getJobsByCustomer(cust);
    }

    /**
     * @param vend the customer to look for.
     * @return all jobs that have this customer, never null
     */
    public Collection<GnucashVendorJob> getJobsByVendor(final GnucashVendor vend) {
	return jobMgr.getJobsByVendor(vend);
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
    // Statistics (for test purposes)

    @Override
    public int getNofEntriesAccountMap() {
	return acctMgr.getNofEntriesAccountMap();
    }

    @Override
    public int getNofEntriesTransactionMap() {
	return transactionID2transaction.size();
    }

    @Override
    public int getNofEntriesTransactionSplitMap() {
	return transactionSplitID2transactionSplit.size();
    }

    @Override
    public int getNofEntriesGenerInvoiceMap() {
	return invcMgr.getNofEntriesGenerInvoiceMap();
    }

    @Override
    public int getNofEntriesGenerInvoiceEntriesMap() {
	return invoiceEntryID2invoiceEntry.size();
    }

    @Override
    public int getNofEntriesCustomerMap() {
	return custMgr.getNofEntriesCustomerMap();
    }

    @Override
    public int getNofEntriesVendorMap() {
	return vendMgr.getNofEntriesVendorMap();
    }

    @Override
    public int getNofEntriesEmployeeMap() {
	return emplMgr.getNofEntriesCustomerMap();
    }

    @Override
    public int getNofEntriesGenerJobMap() {
	return jobMgr.getNofEntriesGenerJobMap();
    }

    @Override
    public int getNofEntriesCommodityMap() {
    return cmdtyQualifID2Cmdty.size();
    }
    
    // ----------------------------
    // Statistics, var 2 (low-level)
    
    /**
     * @return the number of transactions
     */
    protected int getTransactionCount() {
	GncCountData count = findCountDataByType("transaction");
	return count.getValue();
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
     * @param gcshFile
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
     * @param gcshFile
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
     * @param gcshFile
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
     * @param gcshFile
     * @return
     */
    @Override
    public String getNewCustomerNumber() {
	int newNo = getHighestCustomerNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

    /**
     * Assuming that all customer numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @param gcshFile
     * @return
     */
    @Override
    public String getNewVendorNumber() {
	int newNo = getHighestVendorNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

    /**
     * Assuming that all employee numbers (manually set IDs, not GUIDs) are numeric
     * as generated by default.
     * 
     * @param gcshFile
     * @return
     */
    @Override
    public String getNewEmployeeNumber() {
	int newNo = getHighestEmployeeNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = PADDING_TEMPLATE + newNoStr;
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
     * @param gcshFile
     * @return
     */
    @Override
    public String getNewJobNumber() {
	int newNo = getHighestJobNumber() + 1;
	String newNoStr = Integer.toString(newNo);
	String newNoStrPadded = PADDING_TEMPLATE + newNoStr;
	// 10 zeroes if you need a string of length 10 in the end
	newNoStrPadded = newNoStrPadded.substring(newNoStr.length());

	return newNoStrPadded;
    }

}
