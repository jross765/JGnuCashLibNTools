package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashGenerJobImpl {
	public static final GCshGenerJobID GENER_JOB_1_ID = new GCshGenerJobID("e91b99cd6fbb48a985cbf1e8041f378c");
	public static final GCshGenerJobID GENER_JOB_2_ID = new GCshGenerJobID("028cfb5993ef4d6b83206bc844e2fe56");
	public static final GCshGenerJobID GENER_JOB_3_ID = new GCshGenerJobID("e1ffc66e574447de963c13c0465e74e8");
	public static final GCshGenerJobID GENER_JOB_4_ID = new GCshGenerJobID("bdb7cfdd002b4ea28c8c0080f051479b");
	
	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerJob job = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashGenerJobImpl.class);
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
	public void testCust01() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		assertEquals(GENER_JOB_1_ID, job.getID());
		assertEquals("000001", job.getNumber());
		assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, job.getOwnerType());
		assertEquals("Do more for others", job.getName());
	}

	@Test
	public void testCust02() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		assertEquals(0, job.getPaidInvoices().size());
		assertEquals(1, job.getUnpaidInvoices().size());
	}

	@Test
	public void testCust03() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		GCshID custID = new GCshID("f44645d2397946bcac90dff68cc03b76");
		assertEquals(custID, job.getOwnerID());
	}

	// -----------------------------------------------------------------

	@Test
	public void testVend01() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		assertEquals(GENER_JOB_2_ID, job.getID());
		assertEquals("000002", job.getNumber());
		assertEquals(GnuCashGenerJob.TYPE_VENDOR, job.getOwnerType());
		assertEquals("Let's buy help", job.getName());
	}

	@Test
	public void testVend02() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		assertEquals(0, job.getPaidInvoices().size());
		assertEquals(1, job.getUnpaidInvoices().size());
	}

	@Test
	public void testVend03() throws Exception {
		job = gcshFile.getGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		GCshID vendID = new GCshID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
		assertEquals(vendID, job.getOwnerID());
	}
}
