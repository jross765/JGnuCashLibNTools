package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.TestGCshTaxTableImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashCustomerImpl {
	public static final GCshID CUST_1_ID = new GCshID("5d1dd9afa7554553988669830cc1f696"); // Unfug und Quatsch GmbH
	public static final GCshID CUST_2_ID = new GCshID("f44645d2397946bcac90dff68cc03b76"); // Is That So Ltd.
	public static final GCshID CUST_3_ID = new GCshID("1d2081e8a10e4d5e9312d9fff17d470d"); // N'importe Quoi S.A.

	private static final GCshID TAXTABLE_FR_1_ID = TestGCshTaxTableImpl.TAXTABLE_FR_1_ID;

	private static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
	private static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
	private static final GCshID BLLTRM_3_ID = TestGCshBillTermsImpl.BLLTRM_3_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashCustomer cust = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashCustomerImpl.class);
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
		cust = gcshFile.getCustomerByID(CUST_1_ID);
		assertNotEquals(null, cust);

		assertEquals(CUST_1_ID, cust.getID());
		assertEquals("000001", cust.getNumber());
		assertEquals("Unfug und Quatsch GmbH", cust.getName());

		assertEquals(0.0, cust.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.0, cust.getCredit().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(null, cust.getNotes());

		assertEquals(null, cust.getTaxTableID());

		assertEquals(BLLTRM_2_ID, cust.getTermsID());
		assertEquals("30-10-3", cust.getTerms().getName());
		assertEquals(GCshBillTerms.Type.DAYS, cust.getTerms().getType());
		// etc., cf. class TestGCshBillTermsImpl
	}

	@Test
	public void test01_2() throws Exception {
		cust = gcshFile.getCustomerByID(CUST_2_ID);
		assertNotEquals(null, cust);

		assertEquals(CUST_2_ID, cust.getID());
		assertEquals("000002", cust.getNumber());
		assertEquals("Is That So Ltd.", cust.getName());

		assertEquals(3.0, cust.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2000.0, cust.getCredit().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals("So you want to sell to us, right?", cust.getNotes());

		assertEquals(null, cust.getTaxTableID());
		assertEquals(null, cust.getTermsID());
	}

	@Test
	public void test01_3() throws Exception {
		cust = gcshFile.getCustomerByID(CUST_3_ID);
		assertNotEquals(null, cust);

		assertEquals(CUST_3_ID, cust.getID());
		assertEquals("000003", cust.getNumber());
		assertEquals("N'importe Quoi S.A.", cust.getName());

		assertEquals(0.0, cust.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.0, cust.getCredit().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals("Nous n'achetons rien!", cust.getNotes());

		assertEquals(TAXTABLE_FR_1_ID, cust.getTaxTableID());
		assertEquals("FR_TVA_Std", cust.getTaxTable().getName());
		assertEquals(1, cust.getTaxTable().getEntries().size());
		// etc., cf. class TestGCshTaxTableImpl

		assertEquals(null, cust.getTermsID());
	}

	@Test
	public void test02_1() throws Exception {
		cust = gcshFile.getCustomerByID(CUST_1_ID);
		assertNotEquals(null, cust);

		assertEquals(1, cust.getNofOpenInvoices());

		assertEquals(1, cust.getPaidInvoices_direct().size());
		assertEquals("d9967c10fdf1465e9394a3e4b1e7bd79",
				((GnuCashCustomerInvoice) cust.getPaidInvoices_direct().toArray()[0]).getID().toString());

		assertEquals(1, cust.getUnpaidInvoices_direct().size());
		assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07",
				((GnuCashCustomerInvoice) cust.getUnpaidInvoices_direct().toArray()[0]).getID().toString());
	}

	@Test
	public void test02_2() throws Exception {
		cust = gcshFile.getCustomerByID(CUST_2_ID);
		assertNotEquals(null, cust);

		assertEquals(0, cust.getNofOpenInvoices());

		assertEquals(0, cust.getPaidInvoices_direct().size());

		assertEquals(0, cust.getUnpaidInvoices_direct().size());
		//    assertEquals("[GnuCashCustomerInvoiceImpl: id: d9967c10fdf1465e9394a3e4b1e7bd79 customer-id (dir.): 5d1dd9afa7554553988669830cc1f696 invoice-number: 'R1730' description: 'null' #entries: 0 date-opened: 2023-07-29]", 
		//                 cust.getUnpaidInvoices(GnuCashGenerInvoice.ReadVariant.DIRECT).toArray()[0].toString());
	}

	@Test
	public void test02_3() throws Exception {
		cust = gcshFile.getCustomerByID(CUST_3_ID);
		assertNotEquals(null, cust);

		assertEquals(0, cust.getNofOpenInvoices());

		assertEquals(0, cust.getPaidInvoices_direct().size());

		assertEquals(0, cust.getUnpaidInvoices_direct().size());
		//    assertEquals("[GnuCashCustomerInvoiceImpl: id: d9967c10fdf1465e9394a3e4b1e7bd79 customer-id (dir.): 5d1dd9afa7554553988669830cc1f696 invoice-number: 'R1730' description: 'null' #entries: 0 date-opened: 2023-07-29]", 
		//                 cust.getUnpaidInvoices(GnuCashGenerInvoice.ReadVariant.DIRECT).toArray()[0].toString());
	}
}
