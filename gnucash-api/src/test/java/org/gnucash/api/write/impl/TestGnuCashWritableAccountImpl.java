package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableAccountImpl {
	private static final GCshID ACCT_1_ID = TestGnuCashAccountImpl.ACCT_1_ID;
	private static final GCshID ACCT_2_ID = TestGnuCashAccountImpl.ACCT_2_ID;
	//    private static final GCshID ACCT_3_ID = TestGnuCashAccountImpl.ACCT_3_ID;
	//    private static final GCshID ACCT_4_ID = TestGnuCashAccountImpl.ACCT_4_ID;
	//    private static final GCshID ACCT_5_ID = TestGnuCashAccountImpl.ACCT_5_ID;
	//    private static final GCshID ACCT_6_ID = TestGnuCashAccountImpl.ACCT_6_ID;
	//    private static final GCshID ACCT_7_ID = TestGnuCashAccountImpl.ACCT_7_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshID newAcctID = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableAccountImpl.class);
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
	// Cf. TestGnuCashAccount.test01/02
	//
	// Check whether the GnuCashWritableAccount objects returned by
	// GnuCashWritableFileImpl.getWritableAccountByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getAccountByID().

	@Test
	public void test01_1() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID());
		assertEquals(GnuCashAccount.Type.BANK, acct.getType());
		assertEquals("Giro RaiBa", acct.getName());
		assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa", acct.getQualifiedName());
		assertEquals("Girokonto 1", acct.getDescription());
		assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

		assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

		// ::TODO (throws exception when you try to call that)
		//    assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//    assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(10, acct.getTransactions().size());
		assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
		assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(1).getID().toString());
		assertEquals("4307689faade47d8aab4db87c8ce3aaf", acct.getTransactions().get(2).getID().toString());
		assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(3).getID().toString());
		assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(4).getID().toString());
		assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(5).getID().toString());

    	List<GCshAccountLot> lotList = acct.getLots();
    	// Collections.sort(trxList, Comparator.reverseOrder()); // not necessary
    	assertEquals(null, lotList);
    	// assertEquals(1, lotList.size());
    	// assertEquals("xyz", lotList.get(0).getID().toString());
	}

	@Test
	public void test01_2() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_2_ID, acct.getID());
		assertEquals(GnuCashAccount.Type.ASSET, acct.getType());
		assertEquals("Depot RaiBa", acct.getName());
		assertEquals("Root Account:Aktiva:Depots:Depot RaiBa", acct.getQualifiedName());
		assertEquals("Aktiendepot 1", acct.getDescription());
		assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

		assertEquals("7ee6fe4de6db46fd957f3513c9c6f983", acct.getParentAccountID().toString());

		// ::TODO
		// ::TODO (throws exception when you try to call that)
		//    assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//    assertEquals(4428.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		// ::TODO
		assertEquals(0, acct.getTransactions().size());
		//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID());
		//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getID());

    	assertEquals(null, acct.getLots());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableAccount objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID());

		// ----------------------------
		// Modify the object

		acct.setName("Giro Bossa Nova");
		acct.setDescription("Buffda Duffda Deuf");
		acct.setCmdtyCurrID(new GCshCurrID("CAD"));

		// ::TODO not possible yet
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").remove()
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").setXYZ()

		// ----------------------------
		// Check whether the object has actually been modified
		// (in memory, not in the file yet).

		test02_1_check_memory(acct);

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

	@Test
	public void test02_2() throws Exception {
		// ::TODO
	}

	private void test02_1_check_memory(GnuCashWritableAccount acct) throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		assertEquals(ACCT_1_ID, acct.getID());
		assertEquals(GnuCashAccount.Type.BANK, acct.getType());
		assertEquals("Giro Bossa Nova", acct.getName());
		assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro Bossa Nova", acct.getQualifiedName());
		assertEquals("Buffda Duffda Deuf", acct.getDescription());
		assertEquals("CURRENCY:CAD", acct.getCmdtyCurrID().toString());

		assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

		// ::TODO (throws exception when you try to call that)
		//      assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//      assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(10, acct.getTransactions().size());
		assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
		assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(1).getID().toString());
		assertEquals("4307689faade47d8aab4db87c8ce3aaf", acct.getTransactions().get(2).getID().toString());
		assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(3).getID().toString());
		assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(4).getID().toString());
		assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(5).getID().toString());

    	assertEquals(null, acct.getLots());
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_ACCT, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		GnuCashAccount acct = gcshOutFile.getAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID());
		assertEquals(GnuCashAccount.Type.BANK, acct.getType());
		assertEquals("Giro Bossa Nova", acct.getName());
		assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro Bossa Nova", acct.getQualifiedName());
		assertEquals("Buffda Duffda Deuf", acct.getDescription());
		assertEquals("CURRENCY:CAD", acct.getCmdtyCurrID().toString());

		assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

		// ::TODO (throws exception when you try to call that)
		//     assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//     assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(10, acct.getTransactions().size());
		assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
		assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(1).getID().toString());
		assertEquals("4307689faade47d8aab4db87c8ce3aaf", acct.getTransactions().get(2).getID().toString());
		assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(3).getID().toString());
		assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(4).getID().toString());
		assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(5).getID().toString());

    	assertEquals(null, acct.getLots());
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		// ----------------------------
		// Bare naked object

		GnuCashWritableAccount acct = gcshInFile.createWritableAccount();
		assertNotEquals(null, acct);
		newAcctID = acct.getID();
		assertEquals(true, newAcctID.isSet());

		// ----------------------------
		// Modify the object

		acct.setType(GnuCashAccount.Type.BANK);
		acct.setParentAccountID(new GCshID("fdffaa52f5b04754901dfb1cf9221494")); // Root Account:Aktiva:Sichteinlagen:KK
		acct.setName("Giro Rhumba");
		acct.setDescription("Cha-cha-cha");
		acct.setCmdtyCurrID(new GCshCurrID("JPY"));

		// ----------------------------
		// Check whether the object has actually been created
		// (in memory, not in the file yet).

		test03_1_check_memory(acct);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_check_persisted(outFile);
	}

	private void test03_1_check_memory(GnuCashWritableAccount acct) throws Exception {
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		assertEquals(newAcctID, acct.getID());
		assertEquals(GnuCashAccount.Type.BANK, acct.getType());
		assertEquals("Giro Rhumba", acct.getName());
		assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro Rhumba", acct.getQualifiedName());
		assertEquals("Cha-cha-cha", acct.getDescription());
		assertEquals("CURRENCY:JPY", acct.getCmdtyCurrID().toString());

		assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

		// ::TODO (throws exception when you try to call that)
		//      assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//      assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	private void test03_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		// Here, all 3 stats variants must have been updated
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_ACCT + 1, gcshOutFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));

		GnuCashAccount acct = gcshOutFile.getAccountByID(newAcctID);
		assertNotEquals(null, acct);

		assertEquals(newAcctID, acct.getID());
		assertEquals(GnuCashAccount.Type.BANK, acct.getType());
		assertEquals("Giro Rhumba", acct.getName());
		assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro Rhumba", acct.getQualifiedName());
		assertEquals("Cha-cha-cha", acct.getDescription());
		assertEquals("CURRENCY:JPY", acct.getCmdtyCurrID().toString());

		assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

		// ::TODO (throws exception when you try to call that)
		//     assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//     assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(0, acct.getTransactions().size());
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	// ::TODO

}
