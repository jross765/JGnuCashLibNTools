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
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashJobInvoiceEntryImpl {
	private static final GCshID INVCENTR_4_ID = TestGnuCashGenerInvoiceEntryImpl.INVCENTR_4_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoiceEntry invcEntrGen = null;
	private GnuCashJobInvoiceEntry invcEntrSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashJobInvoiceEntryImpl.class);
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
		invcEntrGen = gcshFile.getGenerInvoiceEntryByID(INVCENTR_4_ID);
		assertNotEquals(null, invcEntrGen);
		invcEntrSpec = new GnuCashJobInvoiceEntryImpl(invcEntrGen);
		assertNotEquals(null, invcEntrSpec);

		assertEquals(INVCENTR_4_ID, invcEntrSpec.getID());
		assertEquals(GnuCashGenerInvoice.TYPE_JOB, invcEntrSpec.getType());
		assertEquals("b1e981f796b94ca0b17a9dccb91fedc0", invcEntrSpec.getGenerInvoiceID().toString());
		assertEquals(GnuCashGenerInvoiceEntry.Action.JOB, invcEntrSpec.getAction());
		assertEquals("Item 1", invcEntrSpec.getDescription());

		assertEquals(true, invcEntrGen.isJobInvcTaxable());
		assertEquals(true, invcEntrSpec.isJobInvcTaxable());
		assertEquals(true, invcEntrSpec.isTaxable());

		assertEquals(0.19, invcEntrGen.getJobInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getJobInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(0.19, invcEntrSpec.getApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(2450.00, invcEntrGen.getJobInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getJobInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(1, invcEntrGen.getQuantity().intValue());
		assertEquals(1, invcEntrSpec.getQuantity().intValue());
		assertEquals(1, invcEntrSpec.getQuantity().intValue());

		assertEquals(2450.00, invcEntrGen.getJobInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getJobInvcSum().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getSum().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(2915.50, invcEntrGen.getJobInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2915.50, invcEntrSpec.getJobInvcSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2915.50, invcEntrSpec.getSumInclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);

		assertEquals(2450.00, invcEntrGen.getJobInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getJobInvcSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(2450.00, invcEntrSpec.getSumExclTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

}
