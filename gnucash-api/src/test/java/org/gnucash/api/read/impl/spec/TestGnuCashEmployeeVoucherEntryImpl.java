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
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashEmployeeVoucherEntryImpl {
	private static final GCshGenerInvcEntrID EMPL_VCH_ENTR_5_ID = TestGnuCashGenerInvoiceEntryImpl.GENER_INVCENTR_5_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoiceEntry invcEntrGen = null;
	private GnuCashEmployeeVoucherEntry invcEntrSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashEmployeeVoucherEntryImpl.class);
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
	public void test02_3() throws Exception {
		invcEntrGen = gcshFile.getGenerInvoiceEntryByID(EMPL_VCH_ENTR_5_ID);
		assertNotEquals(null, invcEntrGen);
		invcEntrSpec = new GnuCashEmployeeVoucherEntryImpl(invcEntrGen);
		assertNotEquals(null, invcEntrSpec);

		assertEquals(EMPL_VCH_ENTR_5_ID, invcEntrSpec.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_EMPLOYEE, invcEntrSpec.getType());
		assertEquals("8de4467c17e04bb2895fb68cc07fc4df", invcEntrSpec.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntrSpec.getAction());
		assertEquals("Übernachtung", invcEntrSpec.getDescription());

		assertEquals(true, invcEntrGen.isEmplVchTaxable());
		assertEquals(true, invcEntrSpec.isEmplVchTaxable());
		assertEquals(true, invcEntrSpec.isTaxable());

		assertEquals(0.0, invcEntrGen.getEmplVchApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.0, invcEntrSpec.getEmplVchApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.0, invcEntrSpec.getApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(145.00, invcEntrGen.getEmplVchPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getEmplVchPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1, invcEntrGen.getQuantity().intValue());
		assertEquals(1, invcEntrSpec.getQuantity().intValue());
		assertEquals(1, invcEntrSpec.getQuantity().intValue());

		assertEquals(145.00, invcEntrGen.getEmplVchSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getEmplVchSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getSum().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(145.00, invcEntrGen.getEmplVchSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getEmplVchSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(145.00, invcEntrGen.getEmplVchSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getEmplVchSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(145.00, invcEntrSpec.getSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

}
