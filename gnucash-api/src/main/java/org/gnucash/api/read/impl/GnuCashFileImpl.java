package org.gnucash.api.read.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.gnucash.api.Const;
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
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashAccount.Type;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
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
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.GnuCashPubIDManager;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.NamespaceRemoverReader;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashFile that can only read but not modify
 * GnuCash-Files. <br/>
 * 
 * @see GnuCashFile
 */
public class GnuCashFileImpl implements GnuCashFile, GnuCashPubIDManager {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(GnuCashFileImpl.class);

    // ---------------------------------------------------------------
    // ::MAGIC
	
	// Cf. https://en.wikipedia.org/wiki/List_of_file_signatures
	private static final int GZIP_HEADER_BYTE_1 = 31;
    private static final int GZIP_HEADER_BYTE_2 = -117;

    protected static final String FILE_EXT_ZIPPED_1 = ".gz";

    // ---------------------------------------------------------------

	private File file;

	// ----------------------------

	private GncV2 rootElement;
	private GnuCashObjectImpl myGnuCashObject;

	// ----------------------------

	private volatile ObjectFactory myJAXBFactory;
	private volatile JAXBContext myJAXBContext;

	// ----------------------------

	protected FileAccountManager acctMgr = null;
	protected FileTransactionManager trxMgr = null;
	protected FileInvoiceManager invcMgr = null;
	protected FileInvoiceEntryManager invcEntrMgr = null;
	protected FileCustomerManager custMgr = null;
	protected FileVendorManager vendMgr = null;
	protected FileEmployeeManager emplMgr = null;
	protected FileJobManager jobMgr = null;
	protected FileCommodityManager cmdtyMgr = null;

	// ----------------------------

	protected FileTaxTableManager taxTabMgr = null;
	protected FileBillTermsManager bllTrmMgr = null;

	// ----------------------------

	private final ComplexPriceTable currencyTable = new ComplexPriceTable();
	protected FilePriceManager prcMgr = null;

	// ---------------------------------------------------------------

	/**
	 * @param pFile the file to load and initialize from
	 * @throws IOException                   on low level reading-errors
	 *                                       (FileNotFoundException if not found)
	 * @see #loadFile(File)
	 */
	public GnuCashFileImpl(final File pFile) throws IOException {
		super();
		loadFile(pFile);
	}

	/**
	 * @param pFile the file to load and initialize from
	 * @throws IOException                   on low level reading-errors
	 *                                       (FileNotFoundException if not found)
	 * @see #loadFile(File)
	 */
	public GnuCashFileImpl(final InputStream is) throws IOException {
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
		if ( pFile == null ) {
			throw new IllegalArgumentException("null not allowed for field this.file");
		}
		file = pFile;
	}

	// ----------------------------

	/**
	 * loads the file and calls setRootElement.
	 *
	 * @param pFile the file to read
	 * @throws IOException                   on low level reading-errors
	 *                                       (FileNotFoundException if not found)
	 * @see #setRootElement(GncV2)
	 */
	protected void loadFile(final File pFile) throws IOException {

		long start = System.currentTimeMillis();

		if ( pFile == null ) {
			throw new IllegalArgumentException("null not allowed for field this.file");
		}

		if ( !pFile.exists() ) {
			throw new IllegalArgumentException("Given file '" + pFile.getAbsolutePath() + "' does not exist!");
		}

		setFile(pFile);

		InputStream in = new FileInputStream(pFile);
		if ( pFile.getName().endsWith(FILE_EXT_ZIPPED_1) ) {
			in = new BufferedInputStream(in);
			in = new GZIPInputStream(in);
		} else {
			// determine if it's gzipped by the magic bytes
			byte[] magic = new byte[2];
			in.read(magic);
			in.close();

			in = new FileInputStream(pFile);
			in = new BufferedInputStream(in);
			if ( magic[0] == GZIP_HEADER_BYTE_1 && 
				 magic[1] == GZIP_HEADER_BYTE_2 ) {
				in = new GZIPInputStream(in);
			}
		}

		loadInputStream(in);

		long end = System.currentTimeMillis();
		LOGGER.info("loadFile: GnuCashFileImpl.loadFile took " + (end - start) + " ms (total) ");

	}

	protected void loadInputStream(InputStream in) throws UnsupportedEncodingException, IOException,
			InvalidCmdtyCurrIDException {
		long start = System.currentTimeMillis();

		NamespaceRemoverReader reader = new NamespaceRemoverReader(new InputStreamReader(in, "utf-8"));
		try {
			JAXBContext myContext = getJAXBContext();
			Unmarshaller unmarshaller = myContext.createUnmarshaller();

			GncV2 obj = (GncV2) unmarshaller.unmarshal(new InputSource(new BufferedReader(reader)));
			long start2 = System.currentTimeMillis();
			setRootElement(obj);
			long end = System.currentTimeMillis();
			LOGGER.info("loadInputStream: Took " + (end - start) + " ms (total), " + (start2 - start)
					+ " ms (jaxb-loading), " + (end - start2) + " ms (building facades)");

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
	 * @param type the type to set it for
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
		if ( root == null ) {
			return Const.DEFAULT_CURRENCY;
		}

		for ( Object bookElement : getRootElement().getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncAccount) ) {
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
	public GnuCashAccount getAccountByID(final GCshID acctID) {
		return acctMgr.getAccountByID(acctID);
	}

	/**
	 * @param prntAcctID if null, gives all account that have no parent
	 * @return the sorted collection of children of that account
	 * 
	 * @see #getAccountByID(GCshID)
	 */
	@Override
	public List<GnuCashAccount> getAccountsByParentID(final GCshID prntAcctID) {
		return acctMgr.getAccountsByParentID(prntAcctID);
	}

	@Override
	public Collection<GnuCashAccount> getAccountsByName(final String name) {
		return acctMgr.getAccountsByName(name);
	}

	/**
	 * @see GnuCashFile#getAccountsByName(java.lang.String)
	 */
	@Override
	public Collection<GnuCashAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
		return acctMgr.getAccountsByName(expr, qualif, relaxed);
	}

	@Override
	public GnuCashAccount getAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
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
	public GnuCashAccount getAccountByNameEx(final String nameRegEx)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return acctMgr.getAccountByNameEx(nameRegEx);
	}

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 *
	 * @param acctID the id to look for
	 * @param name   the name to look for if nothing is found for the id
	 * @return null if not found
	 * @throws TooManyEntriesFoundException
	 * @throws NoEntryFoundException
	 * @see #getAccountByID(GCshID)
	 * @see #getAccountsByName(String)
	 */
	@Override
	public GnuCashAccount getAccountByIDorName(final GCshID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return acctMgr.getAccountByIDorName(acctID, name);
	}

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 *
	 * @param acctID the id to look for
	 * @param name   the regular expression of the name to look for if nothing is
	 *               found for the id
	 * @return null if not found
	 * @throws TooManyEntriesFoundException
	 * @throws NoEntryFoundException
	 * @see #getAccountByID(GCshID)
	 * @see #getAccountsByName(String)
	 */
	@Override
	public GnuCashAccount getAccountByIDorNameEx(final GCshID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return acctMgr.getAccountByIDorNameEx(acctID, name);
	}

	@Override
	public Collection<GnuCashAccount> getAccountsByType(Type type) {
		return acctMgr.getAccountsByType(type);
	}

	@Override
	public Collection<GnuCashAccount> getAccountsByTypeAndName(Type type, String acctName, boolean qualif,
			boolean relaxed) {
		return acctMgr.getAccountsByTypeAndName(type, acctName, qualif, relaxed);
	}

	/**
	 * @return a read-only collection of all accounts
	 */
	@Override
	public Collection<GnuCashAccount> getAccounts() {
		return acctMgr.getAccounts();
	}

	/**
	 * @return a read-only collection of all accounts that have no parent (the
	 *         result is sorted)
	 */
	@Override
	public GCshID getRootAccountID() {
		return acctMgr.getRootAccountID();
	}

	/**
	 * @return a read-only collection of all accounts that have no parent (the
	 *         result is sorted)
	 */
	@Override
	public GnuCashAccount getRootAccount() {
		return acctMgr.getRootAccount();
	}

	/**
	 * @return a read-only collection of all accounts that have no parent (the
	 *         result is sorted)
	 */
	@Override
	public Collection<? extends GnuCashAccount> getParentlessAccounts() {
		return acctMgr.getParentlessAccounts();
	}

	@Override
	public Collection<GCshID> getTopAccountIDs() {
		return acctMgr.getTopAccountIDs();
	}

	@Override
	public Collection<GnuCashAccount> getTopAccounts() {
		return acctMgr.getTopAccounts();
	}

	// ---------------------------------------------------------------

	public GnuCashTransaction getTransactionByID(final GCshID trxID) {
		return trxMgr.getTransactionByID(trxID);
	}

	public Collection<? extends GnuCashTransaction> getTransactions() {
		return trxMgr.getTransactions();
	}

	public Collection<GnuCashTransactionImpl> getTransactions_readAfresh() {
		return trxMgr.getTransactions_readAfresh();
	}

	public Collection<? extends GnuCashTransaction> getTransactions(final LocalDate fromDate, final LocalDate toDate) {
		ArrayList<GnuCashTransaction> result = new ArrayList<GnuCashTransaction>();
		
		for ( GnuCashTransaction trx : getTransactions() ) {
			 if ( ( trx.getDatePosted().toLocalDate().isEqual( fromDate ) ||
				    trx.getDatePosted().toLocalDate().isAfter( fromDate ) ) &&
			      ( trx.getDatePosted().toLocalDate().isEqual( toDate ) ||
					trx.getDatePosted().toLocalDate().isBefore( toDate ) ) ) {
				 result.add(trx);
			 }
		}
		
		return Collections.unmodifiableCollection(result);
	}

	// ---------------------------------------------------------------

	public GnuCashTransactionSplit getTransactionSplitByID(final GCshID spltID) {
		return trxMgr.getTransactionSplitByID(spltID);
	}

	public List<GnuCashTransactionSplit> getTransactionSplitsByAccountLotID(final GCshID acctLotID) {
		return trxMgr.getTransactionSplitsByAccountLotID(acctLotID);
	}

	public Collection<GnuCashTransactionSplit> getTransactionSplits() {
		return trxMgr.getTransactionSplits();
	}

	public Collection<GnuCashTransactionSplitImpl> getTransactionSplits_readAfresh() {
		return trxMgr.getTransactionSplits_readAfresh();
	}

	public Collection<GnuCashTransactionSplitImpl> getTransactionSplits_readAfresh(final GCshID trxID) {
		return trxMgr.getTransactionSplits_readAfresh(trxID);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashGenerInvoice getGenerInvoiceByID(final GCshID invcID) {
		return invcMgr.getGenerInvoiceByID(invcID);
	}

	@Override
	public List<GnuCashGenerInvoice> getGenerInvoicesByType(final GCshOwner.Type type) {
		return invcMgr.getGenerInvoicesByType(type);
	}

	/**
	 * @see #getPaidGenerInvoices()
	 * @see #getUnpaidGenerInvoices()
	 */
	@Override
	public List<GnuCashGenerInvoice> getGenerInvoices() {
		return invcMgr.getGenerInvoices();
	}

	// ----------------------------

	/**
	 * @see #getUnpaidGenerInvoices()
	 */
	@Override
	public List<GnuCashGenerInvoice> getPaidGenerInvoices() {
		return invcMgr.getPaidGenerInvoices();
	}

	/**
	 * @see #getPaidGenerInvoices()
	 */
	@Override
	public List<GnuCashGenerInvoice> getUnpaidGenerInvoices() {
		return invcMgr.getUnpaidGenerInvoices();
	}

	// ----------------------------

	/**
	 * @see #getPaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashCustomerInvoice> getInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		return invcMgr.getInvoicesForCustomer_direct(cust);
	}

	/**
	 * @see #getPaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		return invcMgr.getInvoicesForCustomer_viaAllJobs(cust);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		return invcMgr.getPaidInvoicesForCustomer_direct(cust);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		return invcMgr.getPaidInvoicesForCustomer_viaAllJobs(cust);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnuCashCustomer cust) {
		return invcMgr.getUnpaidInvoicesForCustomer_direct(cust);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnuCashCustomer cust) {
		return invcMgr.getUnpaidInvoicesForCustomer_viaAllJobs(cust);
	}

	// ----------------------------

	/**
	 * @see #getBillsForVendor_viaAllJobs(GnuCashVendor)
	 * @see #getPaidBillsForVendor_direct(GnuCashVendor)
	 * @see #getUnpaidBillsForVendor_direct(GnuCashVendor)
	 */
	@Override
	public List<GnuCashVendorBill> getBillsForVendor_direct(final GnuCashVendor vend) {
		return invcMgr.getBillsForVendor_direct(vend);
	}

	/**
	 * @see #getBillsForVendor_direct(GnuCashVendor)
	 * @see #getPaidBillsForVendor_viaAllJobs(GnuCashVendor)
	 * @see #getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor)
	 */
	@Override
	public List<GnuCashJobInvoice> getBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return invcMgr.getBillsForVendor_viaAllJobs(vend);
	}

	/**
	 * @see #getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor)
	 */
	@Override
	public List<GnuCashVendorBill> getPaidBillsForVendor_direct(final GnuCashVendor vend) {
		return invcMgr.getPaidBillsForVendor_direct(vend);
	}

	/**
	 * @see #getPaidBillsForVendor_direct(GnuCashVendor)
	 */
	@Override
	public List<GnuCashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return invcMgr.getPaidBillsForVendor_viaAllJobs(vend);
	}

	/**
	 * @see #getPaidBillsForVendor_viaAllJobs(GnuCashVendor)
	 */
	@Override
	public List<GnuCashVendorBill> getUnpaidBillsForVendor_direct(final GnuCashVendor vend) {
		return invcMgr.getUnpaidBillsForVendor_direct(vend);
	}

	/**
	 * @see #getPaidBillsForVendor_direct(GnuCashVendor)
	 */
	@Override
	public List<GnuCashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnuCashVendor vend) {
		return invcMgr.getUnpaidBillsForVendor_viaAllJobs(vend);
	}

	// ----------------------------

	/**
	 * @see #getPaidVouchersForEmployee(GnuCashEmployee)
	 * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
	 */
	@Override
	public List<GnuCashEmployeeVoucher> getVouchersForEmployee(final GnuCashEmployee empl) {
		return invcMgr.getVouchersForEmployee(empl);
	}

	/**
	 * @see #getUnpaidVouchersForEmployee(GnuCashEmployee)
	 */
	@Override
	public List<GnuCashEmployeeVoucher> getPaidVouchersForEmployee(final GnuCashEmployee empl) {
		return invcMgr.getPaidVouchersForEmployee(empl);
	}

	/**
	 * @see #getPaidVouchersForEmployee(GnuCashEmployee)
	 */
	@Override
	public List<GnuCashEmployeeVoucher> getUnpaidVouchersForEmployee(final GnuCashEmployee empl) {
		return invcMgr.getUnpaidVouchersForEmployee(empl);
	}

	// ----------------------------

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getInvoicesForJob(final GnuCashGenerJob job) {
		return invcMgr.getInvoicesForJob(job);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getPaidInvoicesForJob(final GnuCashGenerJob job) {
		return invcMgr.getPaidInvoicesForJob(job);
	}

	/**
	 * @see GnuCashFile#getUnpaidInvoicesForCustomer_direct(GnuCashCustomer)
	 */
	@Override
	public List<GnuCashJobInvoice> getUnpaidInvoicesForJob(final GnuCashGenerJob job) {
		return invcMgr.getUnpaidInvoicesForJob(job);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshID id) {
		return invcEntrMgr.getGenerInvoiceEntryByID(id);
	}

	public Collection<GnuCashGenerInvoiceEntry> getGenerInvoiceEntries() {
		return invcEntrMgr.getGenerInvoiceEntries();
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashCustomer getCustomerByID(final GCshID custID) {
		return custMgr.getCustomerByID(custID);
	}

	@Override
	public Collection<GnuCashCustomer> getCustomersByName(final String name) {
		return custMgr.getCustomersByName(name);
	}

	@Override
	public Collection<GnuCashCustomer> getCustomersByName(final String expr, boolean relaxed) {
		return custMgr.getCustomersByName(expr, relaxed);
	}

	@Override
	public GnuCashCustomer getCustomerByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return custMgr.getCustomerByNameUniq(name);
	}

	@Override
	public Collection<GnuCashCustomer> getCustomers() {
		return custMgr.getCustomers();
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashVendor getVendorByID(GCshID vendID) {
		return vendMgr.getVendorByID(vendID);
	}

	@Override
	public Collection<GnuCashVendor> getVendorsByName(final String name) {
		return vendMgr.getVendorsByName(name);
	}

	@Override
	public Collection<GnuCashVendor> getVendorsByName(final String expr, final boolean relaxed) {
		return vendMgr.getVendorsByName(expr, relaxed);
	}

	@Override
	public GnuCashVendor getVendorByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return vendMgr.getVendorByNameUniq(name);
	}

	@Override
	public Collection<GnuCashVendor> getVendors() {
		return vendMgr.getVendors();
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashEmployee getEmployeeByID(final GCshID emplID) {
		return emplMgr.getEmployeeByID(emplID);
	}

	@Override
	public Collection<GnuCashEmployee> getEmployeesByUserName(final String userName) {
		return emplMgr.getEmployeesByUserName(userName);
	}

	@Override
	public Collection<GnuCashEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {
		return emplMgr.getEmployeesByUserName(expr, relaxed);
	}

	@Override
	public GnuCashEmployee getEmployeeByUserNameUniq(final String userName)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return emplMgr.getEmployeeByUserNameUniq(userName);
	}

	@Override
	public Collection<GnuCashEmployee> getEmployees() {
		return emplMgr.getEmployees();
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashGenerJob getGenerJobByID(final GCshID jobID) {
		return jobMgr.getGenerJobByID(jobID);
	}

	@Override
	public Collection<GnuCashGenerJob> getGenerJobsByType(final GCshOwner.Type type) {
		return jobMgr.getGenerJobsByType(type);
	}

	@Override
	public Collection<GnuCashGenerJob> getGenerJobsByName(final String name) {
		return jobMgr.getGenerJobsByName(name);
	}

	@Override
	public Collection<GnuCashGenerJob> getGenerJobsByName(final String expr, final boolean relaxed) {
		return jobMgr.getGenerJobsByName(expr, relaxed);
	}

	@Override
	public GnuCashGenerJob getGenerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return jobMgr.getGenerJobByNameUniq(name);
	}

	@Override
	public Collection<GnuCashGenerJob> getGenerJobs() {
		return jobMgr.getGenerJobs();
	}

	// ----------------------------

	@Override
	public GnuCashCustomerJob getCustomerJobByID(final GCshID custID) {
		return jobMgr.getCustomerJobByID(custID);
	}

	@Override
	public Collection<GnuCashCustomerJob> getCustomerJobsByName(final String name) {
		return jobMgr.getCustomerJobsByName(name);
	}

	@Override
	public Collection<GnuCashCustomerJob> getCustomerJobsByName(final String expr, final boolean relaxed) {
		return jobMgr.getCustomerJobsByName(expr, relaxed);
	}

	@Override
	public GnuCashCustomerJob getCustomerJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return jobMgr.getCustomerJobByNameUniq(name);
	}

	@Override
	public Collection<GnuCashCustomerJob> getCustomerJobs() {
		return jobMgr.getCustomerJobs();
	}

	/**
	 * @param cust the customer to look for.
	 * @return all jobs that have this customer, never null
	 */
	public Collection<GnuCashCustomerJob> getJobsByCustomer(final GnuCashCustomer cust) {
		return jobMgr.getJobsByCustomer(cust);
	}

	// ----------------------------

	@Override
	public GnuCashVendorJob getVendorJobByID(final GCshID vendID) {
		return jobMgr.getVendorJobByID(vendID);
	}

	@Override
	public Collection<GnuCashVendorJob> getVendorJobsByName(final String name) {
		return jobMgr.getVendorJobsByName(name);
	}

	@Override
	public Collection<GnuCashVendorJob> getVendorJobsByName(final String expr, final boolean relaxed) {
		return jobMgr.getVendorJobsByName(expr, relaxed);
	}

	@Override
	public GnuCashVendorJob getVendorJobByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return jobMgr.getVendorJobByNameUniq(name);
	}

	@Override
	public Collection<GnuCashVendorJob> getVendorJobs() {
		return jobMgr.getVendorJobs();
	}

	/**
	 * @param vend the customer to look for.
	 * @return all jobs that have this customer, never null
	 */
	public Collection<GnuCashVendorJob> getJobsByVendor(final GnuCashVendor vend) {
		return jobMgr.getJobsByVendor(vend);
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrID qualifID) {
		return cmdtyMgr.getCommodityByQualifID(qualifID);
	}

	@Override
	public GnuCashCommodity getCommodityByQualifID(final String nameSpace, final String id) {
		return cmdtyMgr.getCommodityByQualifID(nameSpace, id);
	}

	@Override
	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id) {
		return cmdtyMgr.getCommodityByQualifID(exchange, id);
	}

	@Override
	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
		return cmdtyMgr.getCommodityByQualifID(mic, id);
	}

	@Override
	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
		return cmdtyMgr.getCommodityByQualifID(secIdType, id);
	}

	@Override
	public GnuCashCommodity getCommodityByQualifID(final String qualifID) {
		return cmdtyMgr.getCommodityByQualifID(qualifID);
	}

	@Override
	public GnuCashCommodity getCommodityByXCode(final String xCode) {
		return cmdtyMgr.getCommodityByXCode(xCode);
	}

	@Override
	public List<GnuCashCommodity> getCommoditiesByName(final String expr) {
		return cmdtyMgr.getCommoditiesByName(expr);
	}

	@Override
	public List<GnuCashCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
		return cmdtyMgr.getCommoditiesByName(expr, relaxed);
	}

	@Override
	public GnuCashCommodity getCommodityByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		return cmdtyMgr.getCommodityByNameUniq(expr);
	}

	@Override
	public Collection<GnuCashCommodity> getCommodities() {
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
	@Override
	public GnuCashPrice getPriceByID(GCshID prcID) {
		return prcMgr.getPriceByID(prcID);
	}
	
	// ---------------------------------------------------------------

	public GnuCashPrice getPriceByCmdtyIDDate(final GCshCmdtyID cmdtyID, final LocalDate date) {
		return prcMgr.getPriceByCmdtyIDDate(cmdtyID, date);
	}
	
	public GnuCashPrice getPriceByCurrIDDate(final GCshCurrID currID, final LocalDate date) {
		return prcMgr.getPriceByCurrIDDate(currID, date);
	}
	
	public GnuCashPrice getPriceByCurrDate(final Currency curr, final LocalDate date) {
		return prcMgr.getPriceByCurrDate(curr, date);
	}
	
	public GnuCashPrice getPriceByCmdtyCurrIDDate(final GCshCmdtyCurrID cmdtyCurrID, final LocalDate date) {
		return prcMgr.getPriceByCmdtyCurrIDDate(cmdtyCurrID, date);
    }

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<GnuCashPrice> getPrices() {
		return prcMgr.getPrices();
	}

	@Override
	public List<GnuCashPrice> getPricesByCmdtyID(final GCshCmdtyID cmdtyID) {
		return prcMgr.getPricesByCmdtyCurrID(cmdtyID);
	}

	@Override
	public List<GnuCashPrice> getPricesByCurrID(final GCshCurrID currID) {
		return prcMgr.getPricesByCmdtyCurrID(currID);
	}

	@Override
	public List<GnuCashPrice> getPricesByCurr(final Currency curr) {
		return prcMgr.getPricesByCmdtyCurr(curr);
	}

	@Override
	public List<GnuCashPrice> getPricesByCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) {
		return prcMgr.getPricesByCmdtyCurrID(cmdtyCurrID);
	}

	//    public FixedPointNumber getLatestPrice(final String cmdtyCurrIDStr) {
//      return prcMgr.getLatestPrice(cmdtyCurrIDStr);
//    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) {
		return prcMgr.getLatestPrice(cmdtyCurrID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) {
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
	 */
	protected void setRootElement(final GncV2 pRootElement) {
		if ( pRootElement == null ) {
			throw new IllegalArgumentException("null not allowed for field this.rootElement");
		}

		LOGGER.debug("setRootElement (read-version)");

		rootElement = pRootElement;

		// ---
		// Prices
		// Caution: the price manager has to be instantiated
		// *before* loading the price database

		prcMgr = new FilePriceManager(this);

		loadPriceDatabase(pRootElement);
//	if (pRootElement.getGncBook().getBookSlots() == null) {
//	    pRootElement.getGncBook().setBookSlots((new ObjectFactory()).createSlotsType());
//	}

		// ---

		myGnuCashObject = new GnuCashObjectImpl(this);

		// ---
		// Init helper entity managers / fill maps

		acctMgr = new FileAccountManager(this);

		invcMgr = new FileInvoiceManager(this);

		// Caution: invoice entries refer to invoices,
		// therefore they have to be loaded after them
		invcEntrMgr = new FileInvoiceEntryManager(this);

		// Caution: transactions refer to invoices,
		// therefore they have to be loaded after them
		trxMgr = new FileTransactionManager(this);

		custMgr = new FileCustomerManager(this);

		vendMgr = new FileVendorManager(this);

		emplMgr = new FileEmployeeManager(this);

		jobMgr = new FileJobManager(this);

		cmdtyMgr = new FileCommodityManager(this);

		// ---

		taxTabMgr = new FileTaxTableManager(this);

		bllTrmMgr = new FileBillTermsManager(this);

		// ---

		// check for unknown book-elements
		for ( Object bookElement : getRootElement().getGncBook().getBookElements() ) {
			if ( bookElement instanceof GncTransaction ) {
				continue;
			} else if ( bookElement instanceof GncSchedxaction ) {
				continue;
			} else if ( bookElement instanceof GncTemplateTransactions ) {
				continue;
			} else if ( bookElement instanceof GncAccount ) {
				continue;
			} else if ( bookElement instanceof GncTransaction ) {
				continue;
			} else if ( bookElement instanceof GncGncInvoice ) {
				continue;
			} else if ( bookElement instanceof GncGncEntry ) {
				continue;
			} else if ( bookElement instanceof GncGncCustomer ) {
				continue;
			} else if ( bookElement instanceof GncGncVendor ) {
				continue;
			} else if ( bookElement instanceof GncGncEmployee ) {
				continue;
			} else if ( bookElement instanceof GncGncJob ) {
				continue;
			} else if ( bookElement instanceof GncCommodity ) {
				continue;
			} else if ( bookElement instanceof GncGncTaxTable ) {
				continue;
			} else if ( bookElement instanceof GncGncBillTerm ) {
				continue;
			} else if ( bookElement instanceof GncPricedb ) {
				continue;
			} else if ( bookElement instanceof GncBudget ) {
				continue;
			}

			throw new IllegalArgumentException(
					"<gnc:book> contains unknown element [" + bookElement.getClass().getName() + "]");
		}
	}

	// ---------------------------------------------------------------

	/**
	 * @param pRootElement the root-element of the GnuCash file
	 */
	protected void loadPriceDatabase(final GncV2 pRootElement) {
		boolean noPriceDB = true;

		GncPricedb priceDB = prcMgr.getPriceDB();
		if ( priceDB.getPrice().size() > 0 )
			noPriceDB = false;

		if ( priceDB.getVersion() != 1 ) {
			LOGGER.warn("loadPriceDatabase: The library only supports the price-DB format V. 1, "
					+ "but the file has version " + priceDB.getVersion() + ". " + "Prices will not be loaded.");
		} else {
			loadPriceDatabaseCore(priceDB);
		}

		if ( noPriceDB ) {
			// no price DB in file
			getCurrencyTable().clear();
		}
	}

	private void loadPriceDatabaseCore(GncPricedb priceDB) {
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
			if ( getCurrencyTable().getConversionFactor(fromCmdtyCurr.getCmdtySpace(),
					fromCmdtyCurr.getCmdtyId()) != null ) {
				continue;
			}

			if ( fromCmdtyCurr.getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY)
					&& fromCmdtyCurr.getCmdtyId().equals(baseCurrency) ) {
				LOGGER.warn("loadPriceDatabaseCore: Ignoring price-quote for " + baseCurrency + " because "
						+ baseCurrency + " is our base-currency.");
				continue;
			}

			// get the latest price in the file and insert it into
			// our currency table
			FixedPointNumber factor = getLatestPrice(
					new GCshCmdtyCurrID(fromCmdtyCurr.getCmdtySpace(), fromCmdtyCurr.getCmdtyId()));

			if ( factor != null ) {
				getCurrencyTable().setConversionFactor(fromCmdtyCurr.getCmdtySpace(), fromCmdtyCurr.getCmdtyId(),
						factor);
			} else {
				LOGGER.warn("loadPriceDatabaseCore: The GnuCash file defines a factor for a commodity '"
						+ fromCmdtyCurr.getCmdtySpace() + ":" + fromCmdtyCurr.getCmdtyId()
						+ "' but has no commodity for it");
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
		if ( myJAXBFactory == null ) {
			myJAXBFactory = new ObjectFactory();
		}
		return myJAXBFactory;
	}

	/**
	 * @return the JAXB-context
	 */
	protected JAXBContext getJAXBContext() {
		if ( myJAXBContext == null ) {
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
	public GnuCashFileImpl getGnuCashFile() {
		return this;
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getUserDefinedAttribute(final String aName) {
		return HasUserDefinedAttributesImpl.getUserDefinedAttributeCore(getRootElement().getGncBook().getBookSlots(),
				aName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeKeysCore(getRootElement().getGncBook().getBookSlots());
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

		for ( GnuCashCustomer cust : custMgr.getCustomers() ) {
			try {
				int newNum = Integer.parseInt(cust.getNumber());
				if ( newNum > highest )
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

		for ( GnuCashVendor vend : vendMgr.getVendors() ) {
			try {
				int newNum = Integer.parseInt(vend.getNumber());
				if ( newNum > highest )
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
	 * Assuming that all employee numbers (manually set IDs, not GUIDs) are numeric
	 * as generated by default.
	 * 
	 * @return
	 */
	@Override
	public int getHighestEmployeeNumber() {
		int highest = -1;

		for ( GnuCashEmployee empl : emplMgr.getEmployees() ) {
			try {
				int newNum = Integer.parseInt(empl.getNumber());
				if ( newNum > highest )
					highest = newNum;
			} catch (Exception exc) {
				// Cf. .getHighestCustomerNumber() above.
				// ==> ::TODO Adapt how a employee object is created.
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

		for ( GnuCashGenerJob job : jobMgr.getGenerJobs() ) {
			try {
				int newNum = Integer.parseInt(job.getNumber());
				if ( newNum > highest )
					highest = newNum;
			} catch (Exception exc) {
				// We run into this exception even when we stick to the
				// automatically generated numbers, because this API's
				// createWritableCustomer() method at first generates
				// an object whose number is equal to its GUID.
				// ==> ::TODO Adapt how a job object is created.
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
		String newNoStrPadded = GnuCashPubIDManager.PADDING_TEMPLATE + newNoStr;
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
		String newNoStrPadded = GnuCashPubIDManager.PADDING_TEMPLATE + newNoStr;
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
		String newNoStrPadded = GnuCashPubIDManager.PADDING_TEMPLATE + newNoStr;
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
		String newNoStrPadded = GnuCashPubIDManager.PADDING_TEMPLATE + newNoStr;
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
		String result = "GnuCashFileImpl [\n";

		result += "  Stats (raw):\n";
		GCshFileStats stats;
		try {
			stats = new GCshFileStats(this);

			result += "    No. of accounts:                  " + stats.getNofEntriesAccounts(GCshFileStats.Type.RAW) + "\n";
			result += "    No. of account lots:              " + stats.getNofEntriesAccountLots(GCshFileStats.Type.RAW) + "\n";
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
