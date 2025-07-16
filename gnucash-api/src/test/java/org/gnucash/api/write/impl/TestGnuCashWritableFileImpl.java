package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.TestGnuCashTransactionImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableFileImpl {

	private static final GCshID ACCT_1_ID     = TestGnuCashAccountImpl.ACCT_1_ID;
	private static final GCshID TRX_1_ID      = TestGnuCashTransactionImpl.TRX_1_ID;
	private static final GCshID INVC_1_ID     = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	private static final String CMDTY_4_ISIN  = "DE000BASF111";
	
	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile  = null;
	private GnuCashWritableFileImpl gcshOutFile = null;
	private GnuCashFileImpl         gcshROFile = null;

	private GCshFileStats gcshInFileStats  = null;
	private GCshFileStats gcshOutFileStats = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashWritableFileImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshInFileStream = null;
		try {
			gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			throw exc;
		}

		try {
			gcshInFile = new GnuCashWritableFileImpl(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			throw exc;
		}

		gcshInFileStats = new GCshFileStats(gcshInFile);

		try {
			InputStream gcshInFileStream2 = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
			gcshROFile = new GnuCashFileImpl(gcshInFileStream2);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash read-only file");
			throw exc;
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGnuCashFile.test01/02
	//
	// Check whether the GnuCashWritableFile objects returned by
	// GnuCashWritableFileImpl.getWritableFileByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getFileByID().

	@Test
	public void test01_01() throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_02() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_03() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
				gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// This one is an exception:
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		// gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
				gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_04() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_05() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test01_06() throws Exception {
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_07() throws Exception {
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_08() throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_09() throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test01_10() throws Exception {
		// CAUTION: This one is an exception:
		// There is one additional commodity object on the "raw" level:
		// the "template".
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1,
				gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW));
		// ::CHECK ???
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL - 1,
				gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_11() throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test01_12() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test01_13() throws Exception {
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableFile objects returned by
	// can actually be modified -- both in memory and persisted in file.

	// ::TODO

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ::TODO

	// -----------------------------------------------------------------
	// PART 4: Idempotency
	// 
	// Check that a GnuCash file which has been loaded by the lib and
	// written into another file without having changed anything produces
	// exactly the same output (i.e., can be loaded into another GnuCash file
	// object, and both produce the same objects). "Equal" or "the same",
	// in this specific context, does not necessarily means "low-level-equal",
	// i.e. both files are the same byte-for-byte, but rather "high-level-equal",
	// i.e. they can be parsed into another structure in memory, and both
	// have identical contents.
	// 
	// And no, this test is not trivial, absolutely not.
	// -----------------------------------------------------------------

	@Test
	public void test04_1() throws Exception {
		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		gcshOutFile = new GnuCashWritableFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(true, outFile.exists());
		assertEquals(false, isGZipped(outFile));

		test04_1_check_1();
		test04_1_check_2();
	}

	private void test04_1_check_1() {
		// Does not work:
		// assertEquals(gcshFileStats, gcshFileStats2);
		// Works:
		assertEquals(true, gcshInFileStats.equals(gcshOutFileStats));
	}

	private void test04_1_check_2() {
		assertEquals(gcshInFile.getAccounts().toString(), gcshOutFile.getAccounts().toString());
		assertEquals(gcshInFile.getTransactions().toString(), gcshOutFile.getTransactions().toString());
		assertEquals(gcshInFile.getTransactionSplits().toString(), gcshOutFile.getTransactionSplits().toString());
		assertEquals(gcshInFile.getGenerInvoices().toString(), gcshOutFile.getGenerInvoices().toString());
		assertEquals(gcshInFile.getGenerInvoiceEntries().toString(), gcshOutFile.getGenerInvoiceEntries().toString());
		assertEquals(gcshInFile.getCustomers().toString(), gcshOutFile.getCustomers().toString());
		assertEquals(gcshInFile.getVendors().toString(), gcshOutFile.getVendors().toString());
		assertEquals(gcshInFile.getEmployees().toString(), gcshOutFile.getEmployees().toString());
		assertEquals(gcshInFile.getGenerJobs().toString(), gcshOutFile.getGenerJobs().toString());
		assertEquals(gcshInFile.getCommodities().toString(), gcshOutFile.getCommodities().toString());
		assertEquals(gcshInFile.getPrices().toString(), gcshOutFile.getPrices().toString());
		assertEquals(gcshInFile.getTaxTables().toString(), gcshOutFile.getTaxTables().toString());
		assertEquals(gcshInFile.getBillTerms().toString(), gcshOutFile.getBillTerms().toString());
	}
	
	// Same as test04_1, but with compressed file
	@Test
	public void test04_2() throws Exception {
		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile, GnuCashWritableFile.CompressMode.COMPRESS );

		gcshOutFile = new GnuCashWritableFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);
		
		assertEquals(true, outFile.exists());
		assertEquals(true, isGZipped(outFile));

		test04_1_check_1();
		test04_1_check_2();
	}

	// -----------------------------------------------------------------
	// PART 5: Symmetry of read-only objects gotten from a) GnucashFile
	// and b) GnuCashWritableFile (esp. sub-objects)
	
	@Test
	public void test05_1() throws Exception {
		// CAUTION: This test case is not trivial! It checks for a subtle
		// bug that long went unnoticed. 
		// Notice that the first line calls the *read-only*-method of the *writable* 
		// file object.
		// Cf. comments in org.gnucash.api.*write*.FileAccountManager.createAccount()
		GnuCashAccount acct11 = gcshInFile.getAccountByID(ACCT_1_ID);
		GnuCashAccount acct12 = gcshROFile.getAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct11);
		assertNotEquals(null, acct12);
		
		// transactions
		// The first comparison is not problematic, it just ensures that the
		// two account objects really belong to the same account. 
		// The following ones are the real test: They check the correct handling 
		// of transactions and trx-splits in GnuCash*Writable*Account.
		assertEquals(acct11.getQualifiedName(), acct12.getQualifiedName());
		assertTrue(acct11.getTransactions().size() > 0);
		assertEquals(acct11.getTransactions().size(), acct12.getTransactions().size());
		assertTrue(acct11.getBalance().getBigDecimal().doubleValue() > 0);
		assertEquals(acct11.getBalance(), acct12.getBalance());
		assertTrue(acct11.getBalanceRecursive().getBigDecimal().doubleValue() > 0);
		assertEquals(acct11.getBalanceRecursive(), acct12.getBalanceRecursive());
	}

	@Test
	public void test05_2() throws Exception {
		// Analogous to test05_1, but with transactions
		GnuCashTransaction trx11 = gcshInFile.getTransactionByID(TRX_1_ID);
		GnuCashTransaction trx12 = gcshROFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx11);
		assertNotEquals(null, trx12);
		
		// splits
		assertEquals(trx11.getID(), trx12.getID());
		assertTrue(trx11.getSplits().size() > 0);
		assertEquals(trx11.getSplits().size(), trx12.getSplits().size());
		assertEquals(trx11.getBalance(), trx12.getBalance());
		assertEquals(trx11.getSplits().get(0).getValue(), trx12.getSplits().get(0).getValue());
		assertEquals(trx11.getSplits().get(1).getValue(), trx12.getSplits().get(1).getValue());
	}

	@Test
	public void test05_3() throws Exception {
		// Analogous to test05_1, but with invoices
		GnuCashGenerInvoice invc11 = gcshInFile.getGenerInvoiceByID(INVC_1_ID);
		GnuCashGenerInvoice invc12 = gcshROFile.getGenerInvoiceByID(INVC_1_ID);
		assertNotEquals(null, invc11);
		assertNotEquals(null, invc12);
		
		// entries
		assertEquals(invc11.getID(), invc12.getID());
		assertTrue(invc11.getGenerEntries().size() > 0);
		assertEquals(invc11.getGenerEntries().size(), invc12.getGenerEntries().size());
		assertTrue(invc11.getCustInvcAmountWithTaxes().getBigDecimal().doubleValue() > 0);
		assertEquals(invc11.getCustInvcAmountWithTaxes(), invc12.getCustInvcAmountWithTaxes());

		// paying transactions
		assertTrue(invc11.getPayingTransactions().size() > 0);
		assertEquals(invc11.getPayingTransactions().size(), invc12.getPayingTransactions().size());
		assertTrue(invc11.getCustInvcAmountPaidWithTaxes().getBigDecimal().doubleValue() > 0);
		assertEquals(invc11.getCustInvcAmountPaidWithTaxes(), invc12.getCustInvcAmountPaidWithTaxes());
	}

	@Test
	public void test05_4() throws Exception {
		// Analogous to test05_1, but with commodities
		GCshCmdtyID_SecIdType cmdtyCurrID3 = new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, CMDTY_4_ISIN);
		GnuCashCommodity cmdty11 = gcshInFile.getCommodityByQualifID(cmdtyCurrID3);
		GnuCashCommodity cmdty12 = gcshROFile.getCommodityByQualifID(cmdtyCurrID3);
		assertNotEquals(null, cmdty11);
		assertNotEquals(null, cmdty12);
		
		// quotes
		assertEquals(cmdty11.getName(), cmdty12.getName());
		assertTrue(cmdty11.getQuotes().size() > 0);
		assertEquals(cmdty11.getQuotes().size(), cmdty12.getQuotes().size());
	}

	// ---------------------------------------------------------------
	
	// https://stackoverflow.com/questions/30507653/how-to-check-whether-file-is-gzip-or-not-in-java
	public boolean isGZipped(File f) {
		int magic = 0;
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			magic = raf.read() & 0xff | ((raf.read() << 8) & 0xff00);
			raf.close();
		} catch (Throwable e) {
			e.printStackTrace(System.err);
		}
		return magic == GZIPInputStream.GZIP_MAGIC;
	}
}
