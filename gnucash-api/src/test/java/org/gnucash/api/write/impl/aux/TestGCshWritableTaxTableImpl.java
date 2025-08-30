package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshTaxTableImpl;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableTaxTableImpl {
	private static final GCshTaxTabID TAXTABLE_DE_1_1_ID = TestGCshTaxTableImpl.TAXTABLE_DE_1_1_ID;
	private static final GCshTaxTabID TAXTABLE_DE_1_2_ID = TestGCshTaxTableImpl.TAXTABLE_DE_1_2_ID;
	private static final GCshTaxTabID TAXTABLE_DE_2_ID = TestGCshTaxTableImpl.TAXTABLE_DE_2_ID;

	public static final GCshTaxTabID TAXTABLE_FR_1_ID = TestGCshTaxTableImpl.TAXTABLE_FR_1_ID;
	private static final GCshTaxTabID TAXTABLE_FR_2_ID = TestGCshTaxTableImpl.TAXTABLE_FR_2_ID;

	public static final GCshTaxTabID TAXTABLE_UK_1_ID = TestGCshTaxTableImpl.TAXTABLE_UK_1_ID;
	private static final GCshTaxTabID TAXTABLE_UK_2_ID = TestGCshTaxTableImpl.TAXTABLE_UK_1_ID;

	private static final GCshAcctID TAX_ACCT_ID = TestGCshTaxTableImpl.TAX_ACCT_ID;

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
		return new JUnit4TestAdapter(TestGCshWritableTaxTableImpl.class);
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
	// Cf. TestGCshTaxTableImpl.testxyz
	//
	// Check whether the GCshWritableTaxTable objects returned by
	// GnuCashWritableFileImpl.getWritableTaxTableByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getTaxTableByID().

	@Test
	public void test01_1() throws Exception {
		Collection<GCshWritableTaxTable> taxTableList = gcshInFile.getWritableTaxTables();

		assertEquals(7, taxTableList.size());

		// ::TODO: Sort array for predictability
		//      Object[] taxTableArr = taxTableList.toArray();
		//      
		//      assertEquals(TAXTABLE_UK_2_ID,   ((GCshWritableTaxTable) taxTableArr[0]).getID());
		//      assertEquals(TAXTABLE_DE_1_2_ID, ((GCshWritableTaxTable) taxTableArr[1]).getID());
		//      assertEquals(TAXTABLE_UK_1_ID,   ((GCshWritableTaxTable) taxTableArr[2]).getID());
		//      assertEquals(TAXTABLE_DE_1_1_ID, ((GCshWritableTaxTable) taxTableArr[3]).getID());
		//      assertEquals(TAXTABLE_DE_2_ID,   ((GCshWritableTaxTable) taxTableArr[4]).getID());
		//      assertEquals(TAXTABLE_FR_1_ID,   ((GCshWritableTaxTable) taxTableArr[5]).getID());
		//      assertEquals(TAXTABLE_FR_2_ID,   ((GCshWritableTaxTable) taxTableArr[6]).getID());
	}

	@Test
	public void test01_2_1_1() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_DE_1_1_ID);

		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getID());
		assertEquals("DE_USt_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_2_1_2() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByName("DE_USt_Std");

		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getID());
		assertEquals("DE_USt_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_2_2_1() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_DE_1_2_ID);

		assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID());
		assertEquals("USt_Std", taxTab.getName()); // sic, old name w/o prefix "DE_"
		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_2_2_2() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByName("USt_Std");

		assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID());
		assertEquals("USt_Std", taxTab.getName()); // sic, old name w/o prefix "DE_"
		assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(19.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_3_1() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_DE_2_ID);

		assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
		assertEquals("DE_USt_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(7.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_3_2() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByName("DE_USt_red");

		assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
		assertEquals("DE_USt_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(7.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_4_1() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_FR_1_ID);

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
		assertEquals("FR_TVA_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_4_2() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByName("FR_TVA_Std");

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
		assertEquals("FR_TVA_Std", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_5_1() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_FR_2_ID);

		assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
		assertEquals("FR_TVA_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(10.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	@Test
	public void test01_5_2() throws Exception {
		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByName("FR_TVA_red");

		assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
		assertEquals("FR_TVA_red", taxTab.getName());
		assertEquals(null, taxTab.getParentID());

		assertEquals(1, taxTab.getEntries().size());
		assertEquals(10.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType());
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GCshWritableTaxTable objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));

		GCshWritableTaxTable taxTab = gcshInFile.getWritableTaxTableByID(TAXTABLE_FR_1_ID);
		assertNotEquals(null, taxTab);

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());

		// ----------------------------
		// Modify the object

		taxTab.setName("Humptey Dumptey");

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(taxTab);

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

	private void test02_1_check_memory(GCshWritableTaxTable taxTab) throws Exception {
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID()); // unchanged
		assertEquals("Humptey Dumptey", taxTab.getName()); // changed
		assertEquals(null, taxTab.getParentID()); // unchanged

		assertEquals(1, taxTab.getEntries().size()); // unchanged
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE); // unchanged
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType()); // unchanged
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID()); // unchanged
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));

		GCshTaxTable taxTab = gcshOutFile.getTaxTableByID(TAXTABLE_FR_1_ID);
		assertNotEquals(null, taxTab);

		assertEquals(TAXTABLE_FR_1_ID, taxTab.getID()); // unchanged
		assertEquals("Humptey Dumptey", taxTab.getName()); // changed
		assertEquals(null, taxTab.getParentID()); // unchanged

		assertEquals(1, taxTab.getEntries().size()); // unchanged
		assertEquals(20.0, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
				ConstTest.DIFF_TOLERANCE); // unchanged
		assertEquals(GCshTaxTableEntry.Type.PERCENT, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getType()); // unchanged
		assertEquals(TAX_ACCT_ID, ((GCshTaxTableEntry) taxTab.getEntries().toArray()[0]).getAccountID()); // unchanged
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
