package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.api.read.impl.TestGnuCashTransactionSplitImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritableTransactionSplitImpl {
	public static final GCshAcctID ACCT_1_ID = TestGnuCashTransactionSplitImpl.ACCT_1_ID;
	public static final GCshAcctID ACCT_2_ID = TestGnuCashAccountImpl.ACCT_2_ID;
	public static final GCshAcctID ACCT_8_ID = TestGnuCashTransactionSplitImpl.ACCT_8_ID;

	public static final GCshTrxID TRX_4_ID = TestGnuCashTransactionSplitImpl.TRX_4_ID;
	public static final GCshTrxID TRX_5_ID = TestGnuCashTransactionSplitImpl.TRX_5_ID;

	public static final GCshSpltID TRXSPLT_1_ID = TestGnuCashTransactionSplitImpl.TRXSPLT_1_ID;
	public static final GCshSpltID TRXSPLT_2_ID = TestGnuCashTransactionSplitImpl.TRXSPLT_2_ID;

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
		assertEquals(TRX_4_ID, splt.getTransactionID());
		assertEquals(ACCT_1_ID, splt.getAccountID());
		assertEquals(null, splt.getAction());
		assertEquals(-2253.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("-2.253,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals(-2253.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("-2.253,00 €", splt.getQuantityFormatted());
		assertEquals("", splt.getDescription());
		assertEquals(null, splt.getLotID());
		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	@Test
	public void test01_2() throws Exception {
		GnuCashWritableTransactionSplit splt = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_2_ID);

		assertEquals(TRXSPLT_2_ID, splt.getID());
		assertEquals(TRX_5_ID, splt.getTransactionID());
		assertEquals(ACCT_8_ID, splt.getAccountID());
		assertEquals(GnuCashTransactionSplit.Action.BUY, splt.getAction());
		assertEquals(1875.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.875,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals(15.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("15 EURONEXT:SAP", splt.getQuantityFormatted()); // ::CHECK -- wieso hier Euro-Zeichen?
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

	// ---------------------------------------------------------------

	private void test02_1_check_memory(GnuCashWritableTransactionSplit splt) throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		assertEquals(TRX_4_ID, splt.getTransactionID()); // unchanged
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

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshOutFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshOutFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		GnuCashTransactionSplit splt = gcshOutFile.getTransactionSplitByID(TRXSPLT_1_ID);
		assertNotEquals(null, splt);

		assertEquals(TRX_4_ID, splt.getTransactionID()); // unchanged
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

	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------

	@Test
	public void test04_2() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		// Variant 1
		GnuCashWritableTransaction trx1 = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		GnuCashWritableTransactionSplit splt1 = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_1_ID);
		assertNotEquals(null, trx1);
		assertNotEquals(null, splt1);
		trx1.remove(splt1);

		// Variant 2
		GnuCashWritableTransaction trx2 = gcshInFile.getWritableTransactionByID(TRX_5_ID);
		GnuCashWritableTransactionSplit splt2 = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_2_ID);
		assertNotEquals(null, splt2);
		splt2.remove();

		// ----------------------------
		// Check whether the object have actually been deleted
		// (in memory, not in the file yet).

		test04_2_check_memory(splt1, splt2, 
							  trx1, trx2);

		// ----------------------------
		// Now, check whether the deletions have been written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test04_2_check_persisted(outFile,
								 trx1, trx2);
	}
	
	// ---------------------------------------------------------------

	private void test04_2_check_memory(GnuCashWritableTransactionSplit splt1, 
									   GnuCashWritableTransactionSplit splt2, 
									   GnuCashTransaction trx1,
									   GnuCashTransaction trx2) throws Exception {
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 2, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 2, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		// ---
		// First split:
		
		GnuCashWritableTransaction trx1Now = gcshInFile.getWritableTransactionByID(TRX_4_ID);
		// CAUTION / ::TODO
		// Don't know what to do about this oddity right now,
		// but it needs to be addressed at some point.
		assertEquals(3, trx2.getSplitsCount()); // sic, 3, because it's not persisted yet
		assertNotEquals(null, trx1Now); // still there
		try {
			GnuCashWritableTransactionSplit splt1Now1 = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_1_ID);
			assertEquals(1, 0);
		} catch ( NullPointerException exc ) {
			assertEquals(0, 0);
		}
		// Same for a non non-writable instance. 
		// However, due to design asymmetry, no exception is thrown here,
		// but the method just returns null.
		GnuCashTransactionSplit splt1Now2 = gcshInFile.getTransactionSplitByID(TRXSPLT_1_ID);
		assertEquals(null, splt1Now2);
		
		assertEquals(2, trx1.getSplitsCount());
		assertEquals("980706f1ead64460b8205f093472c855", trx1.getSplits().get(0).getID().toString());
		assertEquals("22e449ac0a864d4fae7c58171bdcfcfc", trx1.getSplits().get(1).getID().toString());

		// ---
		// Second split, same as above:
		
		GnuCashWritableTransaction trx2Now = gcshInFile.getWritableTransactionByID(TRX_5_ID);
		// Cf. above
		assertEquals(3, trx2.getSplitsCount()); // sic, 3, because it's not persisted yet
		assertNotEquals(null, trx2Now); // still there
		try {
			GnuCashWritableTransactionSplit splt2Now1 = gcshInFile.getWritableTransactionSplitByID(TRXSPLT_2_ID);
			assertEquals(1, 0);
		} catch ( NullPointerException exc ) {
			assertEquals(0, 0);
		}
		// Cf. above
		GnuCashTransactionSplit splt2Now2 = gcshInFile.getTransactionSplitByID(TRXSPLT_2_ID);
		assertEquals(null, splt2Now2);
		
		assertEquals(3, trx2.getSplitsCount());
		assertEquals("65539ddefc34439d80925275226e7849", trx2.getSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", trx2.getSplits().get(1).getID().toString());
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", trx2.getSplits().get(2).getID().toString());
	}

	private void test04_2_check_persisted(File outFile,
										  GnuCashTransaction trx1,
										  GnuCashTransaction trx2) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 2, gcshOutFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
		// assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_TRX_SPLT - 2, gcshOutFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));

		// ---
		// First split:
		
		GnuCashTransactionSplit splt1 = gcshOutFile.getTransactionSplitByID(TRXSPLT_1_ID);
		assertEquals(null, splt1); // sic

		assertEquals(TRX_4_ID, trx1.getID()); // unchanged
		assertEquals(2, trx1.getSplitsCount()); // changed
		assertEquals("980706f1ead64460b8205f093472c855", trx1.getSplits().get(0).getID().toString());
		assertEquals("22e449ac0a864d4fae7c58171bdcfcfc", trx1.getSplits().get(1).getID().toString());

		// ---
		// Second split, same as above:
		
		GnuCashTransactionSplit splt2 = gcshOutFile.getTransactionSplitByID(TRXSPLT_2_ID);
		assertEquals(null, splt2); // sic

		assertEquals(TRX_5_ID, trx2.getID()); // unchanged
		assertEquals(3, trx2.getSplitsCount()); // changed
		assertEquals("65539ddefc34439d80925275226e7849", trx2.getSplits().get(0).getID().toString());
		assertEquals("4cd194156b014823ab4fea16c3947fcb", trx2.getSplits().get(1).getID().toString());
		assertEquals("ffdc46ece30042baa3657af57eabe6ee", trx2.getSplits().get(2).getID().toString());
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
