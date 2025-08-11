package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAcctLot;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshAccountLotImpl;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.aux.GCshWritableAccountLot;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableAccountLotImpl {
	public  static final GCshAcctID ACCT_8_ID = TestGCshAccountLotImpl.ACCT_8_ID;
	public  static final GCshLotID ACCTLOT_1_ID = TestGCshAccountLotImpl.ACCTLOT_1_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
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
		return new JUnit4TestAdapter(TestGCshWritableAccountLotImpl.class);
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
			gcshInFile = new GnuCashWritableFileImpl(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGCshBillTermsImpl.testxyz
	//
	// Check whether the GCshWritableBillTerms objects returned by
	// GnuCashWritableFileImpl.getWritableTaxTableByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getBillTermsByID().

	@Test
	public void test01_1() throws Exception {
		GnuCashWritableAccount stockAcct = gcshInFile.getWritableAccountByID(ACCT_8_ID);
		List<GCshAcctLot> lotList = stockAcct.getLots();

		assertEquals(1, lotList.size());
	}


	@Test
	public void test01_2() throws Exception {
		GnuCashWritableAccount stockAcct = gcshInFile.getWritableAccountByID(ACCT_8_ID);
		GCshWritableAccountLot lot = stockAcct.getWritableLotByID(ACCTLOT_1_ID);

		assertEquals("Charge 0", lot.getTitle());
		assertEquals("Zur korrekten Vorbereitung des Jahresabschlusses ist ein vollständiges Abbilden der Posten-Logik notwendig.", lot.getNotes());
	}
	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GCshWritableBillTerms objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.CACHE));

		GnuCashWritableAccount stockAcct = gcshInFile.getWritableAccountByID(ACCT_8_ID);
		assertNotEquals(null, stockAcct);
		GCshWritableAccountLot lot = stockAcct.getWritableLotByID(ACCTLOT_1_ID);
		assertNotEquals(null, lot);

		assertEquals(ACCTLOT_1_ID, lot.getID());

		// ----------------------------
		// Modify the object

		lot.setTitle("Wutzi der Hammervogel");
		lot.setNotes("Buale, Buale, Buale... mein Gott, ist unser Buale sueass!");
		
		try {
			GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(new GCshSpltID("980706f1ead64460b8205f093472c855"));
			lot.addTransactionSplit(splt); // illegal arg.
			assertEquals(0, 1);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}

		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(new GCshSpltID("c3ae14400ec843f9bf63f5ef69a31528"));
		lot.addTransactionSplit(splt); // valid arg.

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(lot);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	// ---------------------------------------------------------------

	private void test02_1_check_memory(GCshAcctLot lot) throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.CACHE));

		assertEquals(ACCTLOT_1_ID, lot.getID()); // unchanged
		assertEquals("Wutzi der Hammervogel", lot.getTitle()); // changed
		assertEquals("Buale, Buale, Buale... mein Gott, ist unser Buale sueass!", lot.getNotes()); // changed

		assertEquals(3, lot.getTransactionSplits().size()); // unchanged
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT_LOT, gcshInFileStats.getNofEntriesAccountLots(GCshFileStats.Type.CACHE));

		GnuCashAccount stockAcct = gcshOutFile.getAccountByID(ACCT_8_ID);
		assertNotEquals(null, stockAcct);
		GCshAcctLot lot = stockAcct.getLotByID(ACCTLOT_1_ID);
		assertNotEquals(null, lot);

		assertEquals(ACCTLOT_1_ID, lot.getID()); // unchanged
		assertEquals("Wutzi der Hammervogel", lot.getTitle()); // changed
		assertEquals("Buale, Buale, Buale... mein Gott, ist unser Buale sueass!", lot.getNotes()); // changed
		
		assertEquals(3, lot.getTransactionSplits().size()); // unchanged
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	// ::TODO

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	// ::TODO
	
}
