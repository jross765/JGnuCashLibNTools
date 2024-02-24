package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableFileImpl {
    private GnucashWritableFileImpl gcshInFile  = null;
    private GnucashWritableFileImpl gcshOutFile = null;

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
		return new JUnit4TestAdapter(TestGnucashWritableFileImpl.class);
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
			return;
		}

		try {
			gcshInFile = new GnucashWritableFileImpl(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}

		gcshInFileStats = new GCshFileStats(gcshInFile);
	}

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestGnucashFile.test01/02
    //
    // Check whether the GnucashWritableFile objects returned by
    // GnucashWritableFileImpl.getWritableFileByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getFileByID().

	@Test
	public void test01() throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test02() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test03() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
				gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// This one is an exception:
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		// gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
				gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test04() throws Exception {
		assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test05() throws Exception {
		assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
				gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test06() throws Exception {
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test07() throws Exception {
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test08() throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test09() throws Exception {
		assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test10() throws Exception {
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
	public void test11() throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));
	}

	// ------------------------------

	@Test
	public void test12() throws Exception {
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));
	}

	@Test
	public void test13() throws Exception {
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));
	}

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnucashWritableFile objects returned by
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
		// System.err.println("Outfile for TestKMyMoneyWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
                          // and the KMyMoney file writer does not like that.
		gcshInFile.writeFile(outFile);

		gcshOutFile = new GnucashWritableFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);
		
		test_04_1_check_1();
		test_04_1_check_2();
	}

	private void test_04_1_check_1() {
		// Does not work:
		// assertEquals(gcshFileStats, gcshFileStats2);
		// Works:
		assertEquals(true, gcshInFileStats.equals(gcshOutFileStats));
	}

	private void test_04_1_check_2() {
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

}
