package org.gnucash.api.read.impl.hlp;

import java.io.IOException;

import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Cache implements FileStats {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Cache.class);

	// ---------------------------------------------------------------

	private FileAccountManager      acctMgr = null;
	private FileTransactionManager  trxMgr = null;
	private FileInvoiceManager      invcMgr = null;
	private FileInvoiceEntryManager invcEntrMgr = null;
	private FileCustomerManager     custMgr = null;
	private FileVendorManager       vendMgr = null;
	private FileEmployeeManager     emplMgr = null;
	private FileJobManager          jobMgr = null;

	private FileCommodityManager    cmdtyMgr = null;
	private FilePriceManager        prcMgr = null;

	private FileTaxTableManager     taxTabMgr = null;
	private FileBillTermsManager    bllTrmMgr = null;

	// ---------------------------------------------------------------

	public FileStats_Cache(final FileAccountManager acctMgr, final FileTransactionManager trxMgr,
			final FileInvoiceManager invcMgr, final FileInvoiceEntryManager invcEntrMgr,
			final FileCustomerManager custMgr, final FileVendorManager vendMgr, final FileEmployeeManager emplMgr,
			final FileJobManager jobMgr, final FileCommodityManager cmdtyMgr, final FilePriceManager prcMgr,
			final FileTaxTableManager taxTabMgr, final FileBillTermsManager bllTrmMgr) {
		this.acctMgr = acctMgr;
		this.trxMgr = trxMgr;
		this.invcMgr = invcMgr;
		this.invcEntrMgr = invcEntrMgr;
		this.custMgr = custMgr;
		this.vendMgr = vendMgr;
		this.emplMgr = emplMgr;
		this.jobMgr = jobMgr;
		this.cmdtyMgr = cmdtyMgr;
		this.prcMgr = prcMgr;
		this.taxTabMgr = taxTabMgr;
		this.bllTrmMgr = bllTrmMgr;
	}

	public FileStats_Cache(final GnuCashFileImpl gcshFile) throws IOException {
		this.acctMgr = gcshFile.getAcctMgr();
		this.trxMgr = gcshFile.getTrxMgr();
		this.invcMgr = gcshFile.getInvcMgr();
		this.invcEntrMgr = gcshFile.getInvcEntrMgr();
		this.custMgr = gcshFile.getCustMgr();
		this.vendMgr = gcshFile.getVendMgr();
		this.emplMgr = gcshFile.getEmplMgr();
		this.jobMgr = gcshFile.getJobMgr();
		this.cmdtyMgr = gcshFile.getCmdtyMgr();
		this.prcMgr = gcshFile.getPrcMgr();
		this.taxTabMgr = gcshFile.getTaxTabMgr();
		this.bllTrmMgr = gcshFile.getBllTrmMgr();
	}

	// ---------------------------------------------------------------

	@Override
	public int getNofEntriesAccounts() {
		return acctMgr.getNofEntriesAccountMap();
	}

	@Override
	public int getNofEntriesTransactions() {
		return trxMgr.getNofEntriesTransactionMap();
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return trxMgr.getNofEntriesTransactionSplitMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesGenerInvoices() {
		return invcMgr.getNofEntriesGenerInvoiceMap();
	}

	@Override
	public int getNofEntriesGenerInvoiceEntries() {
		return invcEntrMgr.getNofEntriesGenerInvoiceEntriesMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesCustomers() {
		return custMgr.getNofEntriesCustomerMap();
	}

	@Override
	public int getNofEntriesVendors() {
		return vendMgr.getNofEntriesVendorMap();
	}

	@Override
	public int getNofEntriesEmployees() {
		return emplMgr.getNofEntriesCustomerMap();
	}

	@Override
	public int getNofEntriesGenerJobs() {
		return jobMgr.getNofEntriesGenerJobMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesCommodities() {
		return cmdtyMgr.getNofEntriesCommodityMap();
	}

	@Override
	public int getNofEntriesPrices() {
		return prcMgr.getNofEntriesPriceMap();
	}

	// ----------------------------

	@Override
	public int getNofEntriesTaxTables() {
		return taxTabMgr.getNofEntriesTaxTableMap();
	}

	@Override
	public int getNofEntriesBillTerms() {
		return bllTrmMgr.getNofEntriesBillTermsMap();
	}

}
