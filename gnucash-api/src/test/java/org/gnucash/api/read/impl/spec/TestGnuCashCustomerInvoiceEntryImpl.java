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
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashCustomerInvoiceEntryImpl {
	private static final GCshID INVCENTR_3_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_3_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoiceEntry invcEntrGen = null;
	private GnuCashCustomerInvoiceEntry invcEntrSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashCustomerInvoiceEntryImpl.class);
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
		invcEntrGen = gcshFile.getGenerInvoiceEntryByID(INVCENTR_3_ID);
		assertNotEquals(null, invcEntrGen);
		invcEntrSpec = new GnuCashCustomerInvoiceEntryImpl(invcEntrGen);
		assertNotEquals(null, invcEntrSpec);

		assertEquals(INVCENTR_3_ID, invcEntrSpec.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, invcEntrSpec.getType());
		assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", invcEntrSpec.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, invcEntrSpec.getAction());
		assertEquals("Posten 3", invcEntrSpec.getDescription());

		assertEquals(true, invcEntrGen.isCustInvcTaxable());
		assertEquals(true, invcEntrSpec.isCustInvcTaxable());
		assertEquals(true, invcEntrSpec.isTaxable());
		
		assertEquals(0.19, invcEntrGen.getCustInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getCustInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(120.00, invcEntrGen.getCustInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.00, invcEntrSpec.getCustInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(120.00, invcEntrSpec.getPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(10, invcEntrGen.getQuantity().intValue());
		assertEquals(10, invcEntrSpec.getQuantity().intValue());
		
		assertEquals(1200.00, invcEntrGen.getCustInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1200.00, invcEntrSpec.getCustInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1200.00, invcEntrSpec.getSum().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1428.00, invcEntrGen.getCustInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1428.00, invcEntrSpec.getCustInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1428.00, invcEntrSpec.getSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1200.00, invcEntrGen.getCustInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1200.00, invcEntrSpec.getCustInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(1200.00, invcEntrSpec.getSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

}
