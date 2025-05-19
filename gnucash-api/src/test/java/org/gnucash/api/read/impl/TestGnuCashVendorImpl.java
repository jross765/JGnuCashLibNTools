package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.TestGCshTaxTableImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashVendorImpl {
	public static final GCshID VEND_1_ID = new GCshID("087e1a3d43fa4ef9a9bdd4b4797c4231");
	public static final GCshID VEND_2_ID = new GCshID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
	public static final GCshID VEND_3_ID = new GCshID("bc1c7a6d0a6c4b4ea7dd9f8eb48f79f7");

	private static final GCshID TAXTABLE_UK_1_ID = TestGCshTaxTableImpl.TAXTABLE_UK_1_ID;

	private static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
	private static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
	private static final GCshID BLLTRM_3_ID = TestGCshBillTermsImpl.BLLTRM_3_ID;

	private static final GCshID INVC_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;
	private static final GCshID INVC_4_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	private static final GCshID INVC_12_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_12_ID;
	private static final GCshID INVC_13_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_13_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashVendor vend = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashVendorImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_1() throws Exception {
		vend = gcshFile.getVendorByID(VEND_1_ID);
		assertNotEquals(null, vend);

		assertEquals(VEND_1_ID, vend.getID());
		assertEquals("000001", vend.getNumber());
		assertEquals("Lieferfanto AG", vend.getName());

		assertEquals(null, vend.getTaxTableID());

		assertEquals(BLLTRM_1_ID, vend.getTermsID());
		assertEquals("sofort", vend.getTerms().getName());
		assertEquals(GCshBillTerms.Type.DAYS, vend.getTerms().getType());
		assertEquals(null, vend.getNotes());
		// etc., cf. class TestGCshBillTermsImpl
	}

	@Test
	public void test01_2() throws Exception {
		vend = gcshFile.getVendorByID(VEND_2_ID);
		assertNotEquals(null, vend);

		assertEquals(VEND_2_ID, vend.getID());
		assertEquals("000002", vend.getNumber());
		assertEquals("Super Suppliers Corp.", vend.getName());
		assertEquals("We are so super, man!", vend.getNotes());

		assertEquals(TAXTABLE_UK_1_ID, vend.getTaxTableID());

		assertEquals(null, vend.getTermsID());
	}

	@Test
	public void test01_3() throws Exception {
		vend = gcshFile.getVendorByID(VEND_3_ID);
		assertNotEquals(null, vend);

		assertEquals(VEND_3_ID, vend.getID());
		assertEquals("000003", vend.getNumber());
		assertEquals("Achetez Chez Nous S.A.", vend.getName());
		assertEquals("Nous sommes vraiment les meilleurs! Venez chez nous!", vend.getNotes());

		assertEquals(null, vend.getTaxTableID());

		assertEquals(BLLTRM_2_ID, vend.getTermsID());
		assertEquals("30-10-3", vend.getTerms().getName());
		assertEquals(GCshBillTerms.Type.DAYS, vend.getTerms().getType());
		// etc., cf. class TestGCshBillTermsImpl
	}

	@Test
	public void test02_1() throws Exception {
		vend = gcshFile.getVendorByID(VEND_1_ID);
		assertNotEquals(null, vend);

		assertEquals(2, vend.getNofOpenBills());

		assertEquals(2, vend.getPaidBills_direct().size());

		List<GnuCashVendorBill> bllList = vend.getPaidBills_direct();
		Collections.sort(bllList);
		assertEquals(INVC_12_ID.toString(), ((GnuCashVendorBill) bllList.toArray()[0]).getID().toString());
		assertEquals(INVC_2_ID.toString(),  ((GnuCashVendorBill) bllList.toArray()[1]).getID().toString());

		assertEquals(2, vend.getUnpaidBills_direct().size());

		bllList = vend.getUnpaidBills_direct();
		Collections.sort(bllList);
		assertEquals(INVC_13_ID.toString(), ((GnuCashVendorBill) bllList.toArray()[0]).getID().toString());
		assertEquals(INVC_4_ID.toString(),  ((GnuCashVendorBill) bllList.toArray()[1]).getID().toString());
	}

	@Test
	public void test02_2() throws Exception {
		vend = gcshFile.getVendorByID(VEND_2_ID);
		assertNotEquals(null, vend);

		assertEquals(0, vend.getNofOpenBills());

		assertEquals(0, vend.getPaidBills_direct().size());

		assertEquals(0, vend.getUnpaidBills_direct().size());
		//    assertEquals("[GnuCashVendorBillImpl: id: 4eb0dc387c3f4daba57b11b2a657d8a4 vendor-id (dir.): 087e1a3d43fa4ef9a9bdd4b4797c4231 bill-number: '1730-383/2' description: 'Sie wissen schon: Gefälligkeiten, ne?' #entries: 1 date-opened: 2023-08-31]", 
		//                 vend.getUnpaidInvoices().toArray()[0].toString());
		//    assertEquals("[GnuCashVendorBillImpl: id: 286fc2651a7848038a23bb7d065c8b67 vendor-id (dir.): 087e1a3d43fa4ef9a9bdd4b4797c4231 bill-number: null description: 'Dat isjamaol eine schöne jepflejgte Reschnung!' #entries: 1 date-opened: 2023-08-30]", 
		//                 vend.getUnpaidInvoices().toArray()[1].toString());
	}

	@Test
	public void test02_3() throws Exception {
		vend = gcshFile.getVendorByID(VEND_3_ID);
		assertNotEquals(null, vend);

		assertEquals(0, vend.getNofOpenBills());

		assertEquals(0, vend.getPaidBills_direct().size());

		assertEquals(0, vend.getUnpaidBills_direct().size());
		//    assertEquals("[GnuCashVendorBillImpl: id: 4eb0dc387c3f4daba57b11b2a657d8a4 vendor-id (dir.): 087e1a3d43fa4ef9a9bdd4b4797c4231 bill-number: '1730-383/2' description: 'Sie wissen schon: Gefälligkeiten, ne?' #entries: 1 date-opened: 2023-08-31]", 
		//                 vend.getUnpaidInvoices().toArray()[0].toString());
		//    assertEquals("[GnuCashVendorBillImpl: id: 286fc2651a7848038a23bb7d065c8b67 vendor-id (dir.): 087e1a3d43fa4ef9a9bdd4b4797c4231 bill-number: null description: 'Dat isjamaol eine schöne jepflejgte Reschnung!' #entries: 1 date-opened: 2023-08-30]", 
		//                 vend.getUnpaidInvoices().toArray()[1].toString());
	}
}
