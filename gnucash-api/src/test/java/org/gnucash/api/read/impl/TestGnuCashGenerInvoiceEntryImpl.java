package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashGenerInvoiceEntryImpl {
	// private static final GCshID GENER_INVC_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	private static final GCshID GENER_INVC_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;
	// private static final GCshID GENER_INVC_3_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_3_ID;
	private static final GCshID GENER_INVC_4_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	// private static final GCshID GENER_INVC_5_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_5_ID;
	private static final GCshID GENER_INVC_6_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;

	public static final GCshID GENER_INVCENTR_1_ID = new GCshID("513589a11391496cbb8d025fc1e87eaa"); // vendor bill entry
	public static final GCshID GENER_INVCENTR_2_ID = new GCshID("0041b8d397f04ae4a2e9e3c7f991c4ec"); // vendor bill entry
	public static final GCshID GENER_INVCENTR_3_ID = new GCshID("83e78ce224d94c3eafc55e33d3d5f3e6"); // customer invoice entry
	public static final GCshID GENER_INVCENTR_4_ID = new GCshID("fa483972d10a4ce0abf2a7e1319706e7"); // job invoice entry
	public static final GCshID GENER_INVCENTR_5_ID = new GCshID("b6e2313e32d44bb4a8a701c1063e03a7"); // employee voucher entry
	public static final GCshID GENER_INVCENTR_6_ID = new GCshID("220af9aa331947078954cd8ad31916c2"); // vendor bill entry (gg9nixlos)
	public static final GCshID GENER_INVCENTR_7_ID = new GCshID("d1e64a86c4004ef7b87e2610d762456d");
	
	// -----------------------------------------------------------------

	private GnuCashFileImpl gcshFile = null;
	private GnuCashGenerInvoiceEntry invcEntr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashGenerInvoiceEntryImpl.class);
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

		// gcshFileStats = new GCshFileStats(gcshFile);
	}

	// -----------------------------------------------------------------

	// redundant:
	//  @Test
	//  public void test01() throws Exception
	//  {
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
	//      assertEquals(ConstTest.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
	//  }

	@Test
	public void test02_1() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(GENER_INVCENTR_1_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_1_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals(GENER_INVC_2_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals(null, invcEntr.getAction());
		assertEquals("Item 1", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		assertEquals(0.19, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
		assertEquals(37.50, invcEntr.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(37.50, invcEntr.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(31.51260, invcEntr.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}
	

	@Test
	public void test02_2() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(GENER_INVCENTR_2_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_2_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
		assertEquals(GENER_INVC_4_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, invcEntr.getAction());
		assertEquals("Gefälligkeiten", invcEntr.getDescription());

		assertEquals(true, invcEntr.isVendBllTaxable());
		// Following: sic, because there is n o tax table entry assigned
		// (this is an error in real life, but we have done it on purpose here
		// for the tests).
		assertEquals(0.00, invcEntr.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(13.80, invcEntr.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(3, invcEntr.getQuantity().intValue());
		assertEquals(41.40, invcEntr.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntr.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntr.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_3() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(GENER_INVCENTR_3_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_3_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, invcEntr.getType());
		assertEquals(GENER_INVC_6_ID.toString(), invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntr.getAction());
		assertEquals("Posten 3", invcEntr.getDescription());

		assertEquals(true, invcEntr.isCustInvcTaxable());
		assertEquals(0.19, invcEntr.getCustInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.00, invcEntr.getCustInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(10, invcEntr.getQuantity().intValue());
		// ::TODO: entry does not contain tax?
		assertEquals(1200.00, invcEntr.getCustInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1428.00, invcEntr.getCustInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1200.00, invcEntr.getCustInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_4() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(GENER_INVCENTR_4_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_4_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_JOB, invcEntr.getType());
		assertEquals("b1e981f796b94ca0b17a9dccb91fedc0", invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.JOB, invcEntr.getAction());
		assertEquals("Item 1", invcEntr.getDescription());

		assertEquals(true, invcEntr.isJobInvcTaxable());
		assertEquals(0.19, invcEntr.getJobInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntr.getJobInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1, invcEntr.getQuantity().intValue());
		// ::TODO: entry does not contain tax?
		assertEquals(2450.00, invcEntr.getJobInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2915.50, invcEntr.getJobInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntr.getJobInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}


	@Test
	public void test02_5() throws Exception {
		invcEntr = gcshFile.getGenerInvoiceEntryByID(GENER_INVCENTR_5_ID);
		assertNotEquals(null, invcEntr);

		assertEquals(GENER_INVCENTR_5_ID, invcEntr.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_EMPLOYEE, invcEntr.getType());
		assertEquals("8de4467c17e04bb2895fb68cc07fc4df", invcEntr.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntr.getAction());
		assertEquals("Übernachtung", invcEntr.getDescription());

		assertEquals(true, invcEntr.isEmplVchTaxable());
		assertEquals(0.0, invcEntr.getEmplVchApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntr.getEmplVchPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1, invcEntr.getQuantity().intValue());
		assertEquals(145.00, invcEntr.getEmplVchSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntr.getEmplVchSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntr.getEmplVchSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03() throws Exception {
		// Works only in German locale:
		// assertEquals("Material",
		// GnuCashGenerInvoiceEntry.Action.MATERIAL.getLocaleString());

		assertEquals("Hours", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.ENGLISH));
		assertEquals("Stunden", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.GERMAN));
		assertEquals("Heures", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.FRENCH));
		// Locale.SPANISH does not exist (funny...)
		// assertEquals("Horas",
		// GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.SPANISH));

		assertEquals("Hours", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("EN")));
		assertEquals("Stunden", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("DE")));
		assertEquals("Heures", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("FR")));
		assertEquals("Horas", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.forLanguageTag("ES")));
	}

}
