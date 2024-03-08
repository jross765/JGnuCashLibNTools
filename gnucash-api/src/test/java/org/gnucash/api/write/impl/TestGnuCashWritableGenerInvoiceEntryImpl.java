package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableGenerInvoiceEntryImpl {
	private static final GCshID INVCENTR_1_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_1_ID;
	private static final GCshID INVCENTR_2_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_2_ID;
	private static final GCshID INVCENTR_3_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_3_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;

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
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_1_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_1_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals("286fc2651a7848038a23bb7d065c8b67", invcEntr.getGenerInvoiceID().toString());
		assertEquals(null, invcEntr.getAction());
		assertEquals("Item 1", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		assertEquals(0.19, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
	}

	@Test
	public void test01_2_2() throws Exception {
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_2_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_2_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4", invcEntr.getGenerInvoiceID().toString());
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
		GnuCashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_3_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(INVCENTR_3_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, invcEntr.getType());
		assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", invcEntr.getGenerInvoiceID().toString());
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

}
