package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.api.read.impl.TestGnuCashTransactionSplitImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableTransactionSplitImpl {
	public static final GCshID ACCT_1_ID = TestGnuCashTransactionSplitImpl.ACCT_1_ID;
	public static final GCshID ACCT_2_ID = TestGnuCashAccountImpl.ACCT_2_ID;
	public static final GCshID ACCT_8_ID = TestGnuCashTransactionSplitImpl.ACCT_8_ID;

	public static final GCshID TRX_1_ID = TestGnuCashTransactionSplitImpl.TRX_1_ID;
	public static final GCshID TRX_2_ID = TestGnuCashTransactionSplitImpl.TRX_2_ID;

	public static final GCshID TRXSPLT_1_ID = TestGnuCashTransactionSplitImpl.TRXSPLT_1_ID;
	public static final GCshID TRXSPLT_2_ID = TestGnuCashTransactionSplitImpl.TRXSPLT_2_ID;

	public static final GCshID ACCTLOT_1_ID = TestGnuCashTransactionSplitImpl.ACCTLOT_1_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshID newTrxID = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableTransactionSplitImpl.class);
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
	// Cf. TestGnuCashTransaction.test01/02
	//
	// Check whether the GnuCashWritableTransaction objects returned by
	// GnuCashWritableFileImpl.getWritableTransactionByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getTransactionByID().

	@Test
	public void test01_1() throws Exception {
		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_1_ID);

		assertEquals(TRXSPLT_1_ID, splt.getID());
		assertEquals(TRX_1_ID, splt.getTransactionID());
		assertEquals(ACCT_1_ID, splt.getAccountID());
		assertEquals(null, splt.getAction());
		assertEquals(-2253.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("-2.253,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("-2.253,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(-2253.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		// ::TODO: The following two do not work!
//		assertEquals("-2.253", splt.getQuantityFormatted());
//		assertEquals("-2.253", splt.getQuantityFormattedForHTML());
		assertEquals("", splt.getDescription());
		assertEquals(null, splt.getLotID());
		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	@Test
	public void test01_2() throws Exception {
		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_2_ID);

		assertEquals(TRXSPLT_2_ID, splt.getID());
		assertEquals(TRX_2_ID, splt.getTransactionID());
		assertEquals(ACCT_8_ID, splt.getAccountID());
		assertEquals(GnuCashTransactionSplit.Action.BUY, splt.getAction());
		assertEquals(1875.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.875,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("1.875,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(15.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("15 EURONEXT:SAP", splt.getQuantityFormatted()); // ::CHECK -- wieso hier Euro-Zeichen?
		assertEquals("15 EURONEXT:SAP", splt.getQuantityFormattedForHTML()); // ::TODO: locale-specific!
		assertEquals("", splt.getDescription());
		assertEquals(ACCTLOT_1_ID, splt.getLotID());
		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableTransaction objects returned by
	// can actually be modified -- both in memory and persisted in file.
	
	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_1_ID);
		assertNotEquals(null, splt);

		assertEquals(TRXSPLT_1_ID, splt.getID());

		// ----------------------------
		// Modify the object

		splt.setAccountID(ACCT_2_ID);
		splt.setValue(new FixedPointNumber("-123.45"));
		splt.setQuantity(new FixedPointNumber("-67.8901"));
		splt.setDescription("Alle meine Entchen");

		// ::TODO not possible yet
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").remove()
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").setXYZ()

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(splt);

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
	
	// ---------------------------------------------------------------

	private void test02_1_check_memory(GnuCashWritableTransactionSplit splt) throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		assertEquals(TRX_1_ID, splt.getTransactionID()); // unchanged
		assertEquals(ACCT_2_ID, splt.getAccountID()); // changed
		assertEquals(null, splt.getAction()); // unchanged
		assertEquals(-123.45, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-67.8901, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals("Alle meine Entchen", splt.getDescription()); // changed
		assertEquals(null, splt.getLotID()); // unchanged
		assertEquals(null, splt.getUserDefinedAttributeKeys()); // unchanged
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		GnuCashTransactionSplit splt = gcshOutFile.getTransactionSplitByID(TRXSPLT_1_ID);
		assertNotEquals(null, splt);

		assertEquals(TRX_1_ID, splt.getTransactionID()); // unchanged
		assertEquals(ACCT_2_ID, splt.getAccountID()); // changed
		assertEquals(null, splt.getAction()); // unchanged
		assertEquals(-123.45, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals(-67.8901, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
		assertEquals("Alle meine Entchen", splt.getDescription()); // changed
		assertEquals(null, splt.getLotID()); // unchanged
		assertEquals(null, splt.getUserDefinedAttributeKeys()); // unchanged
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
