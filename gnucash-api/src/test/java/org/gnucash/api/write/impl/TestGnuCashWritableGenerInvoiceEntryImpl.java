package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableGenerInvoiceEntryImpl {
	// private static final GCshGenerInvcID GENER_INVC_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	private static final GCshGenerInvcID GENER_INVC_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;
	// private static final GCshGenerInvcID GENER_INVC_3_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_3_ID;
	private static final GCshGenerInvcID GENER_INVC_4_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	// private static final GCshGenerInvcID GENER_INVC_5_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_5_ID;
	private static final GCshGenerInvcID GENER_INVC_6_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;
	
	// private static final GCshGenerInvcID GENER_INVC_10_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_10_ID;
	// private static final GCshGenerInvcID GENER_INVC_11_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_11_ID;
	public  static final GCshGenerInvcID GENER_INVC_12_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_12_ID;
	public  static final GCshGenerInvcID GENER_INVC_13_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_13_ID;

	private static final GCshGenerInvcEntrID GENER_INVCENTR_1_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_1_ID;
	private static final GCshGenerInvcEntrID GENER_INVCENTR_2_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_2_ID;
	private static final GCshGenerInvcEntrID GENER_INVCENTR_3_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_3_ID;
	private static final GCshGenerInvcEntrID GENER_INVCENTR_6_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_6_ID;
	// private static final GCshID INVCENTR_7_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_7_ID;

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
		return new JUnit4TestAdapter(TestGnuCashWritableGenerInvoiceEntryImpl.class);
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
	// Cf. TestGnuCashGenerInvoiceEntryImpl.test02_x
	//
	// Check whether the GnuCashWritableGenerInvoiceEntry objects returned by
	// GnuCashWritableFileImpl.getWritableGenerInvoiceEntryByID() are actually
	// complete (as complete as returned be
	// GnuCashFileImpl.getGenerInvoiceEntryByID().

	@Test
	public void test01_2_1() throws Exception {
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_1_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_1_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals(GENER_INVC_2_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals(null, invcEntr.getActionStr());
		assertEquals(null, invcEntr.getAction());
		assertEquals("Item 1", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		assertEquals(0.19, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test01_2_2() throws Exception {
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_2_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_2_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals(GENER_INVC_4_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals("Stunden", invcEntr.getActionStr());
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, invcEntr.getAction());
		assertEquals("Gefälligkeiten", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		// Following: sic, because there is n o tax table entry assigned
		// (this is an error in real life, but we have done it on purpose here
		// for the tests).
		assertEquals(0.00, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(13.80, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test01_2_3() throws Exception {
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_3_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_3_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, invcEntr.getType());
		assertEquals(GENER_INVC_6_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals("Material", invcEntr.getActionStr());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntr.getAction());
		assertEquals("Posten 3", invcEntr.getDescription());

		assertEquals(true, invcEntr.isCustInvcTaxable());
		assertEquals(0.19, invcEntr.getCustInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.00, invcEntr.getCustInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(10, invcEntr.getQuantity().intValue());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableEmployee objects returned by
	// can actually be modified -- both in memory and persisted in file.

	// ::TODO

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
	public void test04_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_2_ID);
		GnuCashWritableGenerInvoiceEntry entr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_1_ID);
		assertNotEquals(null, invc);
		assertNotEquals(null, entr);
		
		// Check if modifiable
		assertEquals(false, invc.isModifiable());
		assertNotEquals(0, invc.getPayingTransactions().size()); // there are payments

		// Variant 1
		try {
			gcshInFile.removeGenerInvoiceEntry(entr); // Correctly fails because invoice in not modifiable
			assertEquals(1, 0);
		} catch ( IllegalStateException exc ) {
			assertEquals(0, 0);
		}

		// Variant 2
		try {
			entr.remove(); // Correctly fails because invoice in not modifiable
			assertEquals(1, 0);
		} catch ( IllegalStateException exc ) {
			assertEquals(0, 0);
		}
	}
	
	@Test
	public void test04_2_var1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_13_ID);
		GnuCashWritableGenerInvoiceEntry entr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_6_ID);
		assertNotEquals(null, invc);
		assertNotEquals(null, entr);

		// Check if modifiable
		assertEquals(true, invc.isModifiable());
		assertEquals(0, invc.getPayingTransactions().size()); // there are no payments

		// Core (variant-specific):
		gcshInFile.removeGenerInvoiceEntry(entr); // Correctly fails because invoice in not modifiable

		// ----------------------------
		// Check whether the object have actually been deleted
		// (in memory, not in the file yet).

		test04_2_check_memory(entr, invc);

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

		test04_2_check_persisted(outFile);
	}
	
	@Test
	public void test04_2_var2() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_13_ID);
		GnuCashWritableGenerInvoiceEntry entr = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_6_ID);
		assertNotEquals(null, invc);
		assertNotEquals(null, entr);

		// Check if modifiable
		assertEquals(true, invc.isModifiable());
		assertEquals(0, invc.getPayingTransactions().size()); // there are no payments
		
		// Core (variant-specific):
		entr.remove();

		// ----------------------------
		// Check whether the object have actually been deleted
		// (in memory, not in the file yet).

		test04_2_check_memory(entr, invc);

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

		test04_2_check_persisted(outFile);
	}
	
	// ---------------------------------------------------------------

	private void test04_2_check_memory(GnuCashWritableGenerInvoiceEntry entr, 
									   GnuCashGenerInvoice invc) throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR - 1, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR    , gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR - 1, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerInvoice invcNow = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_13_ID);
		// CAUTION / ::TODO
		// Don't know what to do about this oddity right now,
		// but it needs to be addressed at some point.
		assertEquals(1, invc.getGenerEntries().size()); // sic, 3, because it's not persisted yet
		assertNotEquals(null, invcNow); // still there
		try {
			GnuCashWritableGenerInvoiceEntry entrNow = gcshInFile.getWritableGenerInvoiceEntryByID(GENER_INVCENTR_6_ID);
			assertEquals(1, 0);
		} catch ( NullPointerException exc ) {
			assertEquals(0, 0);
		}
		// Same for a non non-writable instance. 
		// However, due to design asymmetry, no exception is thrown here,
		// but the method just returns null.
		GnuCashGenerInvoiceEntry entrNow = gcshInFile.getGenerInvoiceEntryByID(GENER_INVCENTR_6_ID);
		assertEquals(null, entrNow);
		
		assertEquals(1, invc.getGenerEntries().size());
		assertEquals(GENER_INVCENTR_6_ID.toString(), invc.getGenerEntries().get(0).getID().toString());
	}

	private void test04_2_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR - 1, gcshOutFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR - 1, gcshOutFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_INVC_ENTR - 1, gcshOutFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));

		GnuCashGenerInvoice invc = gcshOutFile.getGenerInvoiceByID(GENER_INVC_13_ID);
		GnuCashGenerInvoiceEntry entr = gcshOutFile.getGenerInvoiceEntryByID(GENER_INVCENTR_6_ID);
		assertEquals(null, entr); // sic

		assertEquals(GENER_INVC_13_ID, invc.getID()); // unchanged
		assertEquals(0, invc.getGenerEntries().size()); // changed
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------

	// ::TODO

}
