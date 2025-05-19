package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableGenerInvoiceImpl {
	public static final GCshID GENER_INVC_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	public static final GCshID GENER_INVC_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;
	public static final GCshID GENER_INVC_3_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_3_ID;
	public static final GCshID GENER_INVC_4_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	public static final GCshID GENER_INVC_5_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_5_ID;
	public static final GCshID GENER_INVC_6_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;
	public static final GCshID GENER_INVC_7_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_7_ID;

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
		return new JUnit4TestAdapter(TestGnuCashWritableGenerInvoiceImpl.class);
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
	// Cf. TestGnuCashGenerInvoiceImpl.testXYZ (all)
	//
	// Check whether the GnuCashWritableGenerInvoice objects returned by
	// GnuCashWritableFileImpl.getWritableGenerInvoiceByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getGenerInvoiceByID().

	@Test
	public void testCust01_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_1_ID);
		assertNotEquals(null, invc);

		assertEquals(GENER_INVC_1_ID, invc.getID());
		assertEquals(GCshOwner.Type.CUSTOMER, invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("R1730", invc.getNumber());
		assertEquals("Alles ohne Steuern / voll bezahlt", invc.getDescription());

		assertEquals("2023-07-29T10:59Z", invc.getDateOpened().toString());
		assertEquals("2023-07-29T10:59Z", invc.getDatePosted().toString());
	}

	@Test
	public void testCust02_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_1_ID);
		assertNotEquals(null, invc);

		assertEquals(2, invc.getGenerEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(invc.getGenerEntries());
		assertEquals("92e54c04b66f4682a9afb48e27dfe397",
				((GnuCashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
		assertEquals("3c67a99b5fe34387b596bb1fbab21a74",
				((GnuCashGenerInvoiceEntry) entrList.toArray()[1]).getID().toString());
	}

	@Test
	public void testCust03_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_1_ID);
		assertNotEquals(null, invc);

		assertEquals(1327.60, invc.getCustInvcAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1327.60, invc.getCustInvcAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void testCust04_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_1_ID);
		assertNotEquals(null, invc);

		assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invc.getPostTransaction().getID().toString());
		assertEquals(1, invc.getPayingTransactions().size());

		List<GnuCashTransaction> trxList = (ArrayList<GnuCashTransaction>) invc.getPayingTransactions();
		Collections.sort(trxList);
		assertEquals("29557cfdf4594eb68b1a1b710722f991",
				((GnuCashTransaction) trxList.toArray()[0]).getID().toString());

		assertEquals(true, invc.isCustInvcFullyPaid());
	}

	// -----------------------------------------------------------------

	@Test
	public void testVend01_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_4_ID);
		assertNotEquals(null, invc);

		assertEquals(GENER_INVC_4_ID, invc.getID());
		assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("1730-383/2", invc.getNumber());
		assertEquals("Sie wissen schon: Gefälligkeiten, ne?", invc.getDescription());

		assertEquals("2023-08-31T10:59Z", invc.getDateOpened().toString());
		// ::TODO
		assertEquals("2023-08-31T10:59Z", invc.getDatePosted().toString());
	}

	@Test
	public void testVend01_2() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals(GENER_INVC_2_ID, invc.getID());
		assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("2740921", invc.getNumber());
		assertEquals("Dat isjamaol eine schöne jepflejgte Reschnung!", invc.getDescription());

		assertEquals("2023-08-30T10:59Z", invc.getDateOpened().toString());
		// ::TODO
		assertEquals("2023-08-30T10:59Z", invc.getDatePosted().toString());
	}

	@Test
	public void testVend02_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_4_ID);
		assertNotEquals(null, invc);

		assertEquals(1, invc.getGenerEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(invc.getGenerEntries());
		assertEquals("0041b8d397f04ae4a2e9e3c7f991c4ec",
				((GnuCashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
	}

	@Test
	public void testVend02_2() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals(2, invc.getGenerEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(invc.getGenerEntries());
		assertEquals("513589a11391496cbb8d025fc1e87eaa",
				((GnuCashGenerInvoiceEntry) entrList.toArray()[1]).getID().toString());
		assertEquals("dc3c53f07ff64199ad4ea38988b3f40a",
				((GnuCashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
	}

	@Test
	public void testVend03_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_4_ID);
		assertNotEquals(null, invc);

		assertEquals(41.40, invc.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		// Note: due to (purposefully) incorrect booking, the gross amount
		// of this bill is *not* 49.27 EUR, but 41.40 EUR (its net amount).
		assertEquals(41.40, invc.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void testVend03_2() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals(79.11, invc.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(94.14, invc.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void testVend04_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_4_ID);
		assertNotEquals(null, invc);

		//    assertEquals("xxx", invc.getPostTransaction());

		// ::TODO
		assertEquals(0, invc.getPayingTransactions().size());

		//    ArrayList<GnuCashTransaction> trxList = (ArrayList<GnuCashTransaction>) bllSpec.getPayingTransactions();
		//    Collections.sort(trxList);
		//    assertEquals("xxx", 
		//                 ((GnuCashTransaction) bllSpec.getPayingTransactions().toArray()[0]).getID());

		assertEquals(false, invc.isVendBllFullyPaid());
	}

	@Test
	public void testVend04_2() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals("aa64d862bb5e4d749eb41f198b28d73d", invc.getPostTransaction().getID().toString());
		assertEquals(1, invc.getPayingTransactions().size());

		List<GnuCashTransaction> trxList = (ArrayList<GnuCashTransaction>) invc.getPayingTransactions();
		Collections.sort(trxList);
		assertEquals("ccff780b18294435bf03c6cb1ac325c1",
				((GnuCashTransaction) trxList.toArray()[0]).getID().toString());

		assertEquals(true, invc.isVendBllFullyPaid());
	}

	@Test
	public void test06_1() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_4_ID);
		assertNotEquals(null, invc);
		assertEquals("https://my.vendor.bill.link.01", invc.getURL());
	}

	@Test
	public void test06_2() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_5_ID);
		assertNotEquals(null, invc);
		assertEquals("https://my.job.invoice.link.01", invc.getURL());
	}

	@Test
	public void test06_3() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(GENER_INVC_6_ID);
		assertNotEquals(null, invc);
		assertEquals("https://my.customer.invoice.link.01", invc.getURL());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableGenerInvoice objects returned by
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
