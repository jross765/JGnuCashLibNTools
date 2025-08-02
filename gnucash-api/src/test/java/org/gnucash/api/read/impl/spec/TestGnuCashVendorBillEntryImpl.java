package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashVendorBillEntryImpl {
	private static final GCshGenerInvcEntrID VEND_BLL_ENTR_1_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_1_ID;
	private static final GCshGenerInvcEntrID VEND_BLL_ENTR_2_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_2_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoiceEntry invcEntrGen = null;
	private GnuCashVendorBillEntry invcEntrSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashVendorBillEntryImpl.class);
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
	public void test02_1() throws Exception {
		invcEntrGen = gcshFile.getGenerInvoiceEntryByID(VEND_BLL_ENTR_1_ID);
		assertNotEquals(null, invcEntrGen);
		invcEntrSpec = new GnuCashVendorBillEntryImpl(invcEntrGen);
		assertNotEquals(null, invcEntrSpec);

		assertEquals(VEND_BLL_ENTR_1_ID, invcEntrSpec.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntrSpec.getType());
		assertEquals("286fc2651a7848038a23bb7d065c8b67", invcEntrSpec.getGenerInvoiceID().toString());
		assertEquals(null, invcEntrSpec.getAction());
		assertEquals("Item 1", invcEntrSpec.getDescription());

		assertEquals(true, invcEntrGen.isVendBllTaxable());
		assertEquals(true, invcEntrSpec.isVendBllTaxable());
		assertEquals(true, invcEntrSpec.isTaxable());

		assertEquals(0.19, invcEntrGen.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(12.50, invcEntrGen.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntrSpec.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(12.50, invcEntrSpec.getPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(3, invcEntrGen.getQuantity().intValue());
		assertEquals(3, invcEntrSpec.getQuantity().intValue());

		assertEquals(37.50, invcEntrGen.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(37.50, invcEntrSpec.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(37.50, invcEntrSpec.getSum().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(37.50, invcEntrGen.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(37.50, invcEntrSpec.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(37.50, invcEntrSpec.getSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(31.51260, invcEntrGen.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(31.51260, invcEntrSpec.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(31.51260, invcEntrSpec.getSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_2() throws Exception {
		invcEntrGen = gcshFile.getGenerInvoiceEntryByID(VEND_BLL_ENTR_2_ID);
		assertNotEquals(null, invcEntrGen);
		invcEntrSpec = new GnuCashVendorBillEntryImpl(invcEntrGen);
		assertNotEquals(null, invcEntrSpec);

		assertEquals(VEND_BLL_ENTR_2_ID, invcEntrSpec.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, invcEntrSpec.getType());
		assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4", invcEntrSpec.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, invcEntrSpec.getAction());
		assertEquals("Gefälligkeiten", invcEntrSpec.getDescription());

		assertEquals(true, invcEntrGen.isVendBllTaxable());
		assertEquals(true, invcEntrSpec.isVendBllTaxable());
		assertEquals(true, invcEntrSpec.isTaxable());

		// Following: sic, because there is no tax table entry assigned
		// (this is an error in real life, but we have done it on purpose here
		// for the tests).
		assertEquals(0.00, invcEntrGen.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.00, invcEntrSpec.getVendBllApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.00, invcEntrSpec.getApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(13.80, invcEntrGen.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(13.80, invcEntrSpec.getVendBllPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(13.80, invcEntrSpec.getPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(3, invcEntrGen.getQuantity().intValue());
		assertEquals(3, invcEntrSpec.getQuantity().intValue());

		assertEquals(41.40, invcEntrGen.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getVendBllSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getSum().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(41.40, invcEntrGen.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getVendBllSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(41.40, invcEntrGen.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getVendBllSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, invcEntrSpec.getSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

}
