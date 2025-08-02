package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerJobImpl;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashVendorJobImpl {
	private static final GCshGenerJobID VEND_JOB_2_ID = TestGnuCashGenerJobImpl.GENER_JOB_2_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerJob jobGener = null;
	private GnuCashVendorJob jobSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashVendorJobImpl.class);
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
	public void test01() throws Exception {
		jobGener = gcshFile.getGenerJobByID(VEND_JOB_2_ID);
		assertNotEquals(null, jobGener);
		jobSpec = new GnuCashVendorJobImpl(jobGener);
		assertNotEquals(null, jobSpec);

		assertTrue(jobSpec instanceof GnuCashVendorJob);
		assertEquals(VEND_JOB_2_ID, jobSpec.getID());
		assertEquals("000002", jobSpec.getNumber());
		assertEquals("Let's buy help", jobSpec.getName());
	}

	@Test
	public void test02() throws Exception {
		jobGener = gcshFile.getGenerJobByID(VEND_JOB_2_ID);
		assertNotEquals(null, jobGener);
		jobSpec = new GnuCashVendorJobImpl(jobGener);
		assertNotEquals(null, jobSpec);

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1, jobGener.getNofOpenInvoices());
		assertEquals(1, jobSpec.getNofOpenInvoices());

		// ::TODO
		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(0, jobGener.getPaidInvoices().size());
		assertEquals(0, jobSpec.getPaidInvoices().size());

		// ::TODO
		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1, jobGener.getUnpaidInvoices().size());
		assertEquals(1, jobSpec.getUnpaidInvoices().size());
	}

	@Test
	public void test03() throws Exception {
		jobGener = gcshFile.getGenerJobByID(VEND_JOB_2_ID);
		assertNotEquals(null, jobGener);
		jobSpec = new GnuCashVendorJobImpl(jobGener);
		assertNotEquals(null, jobSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		GCshVendID vendID = new GCshVendID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
		assertEquals(vendID.getRawID(), jobGener.getOwnerID());
		assertEquals(vendID.getRawID(), jobSpec.getOwnerID());
		assertEquals(vendID, jobSpec.getVendorID());
	}
}
