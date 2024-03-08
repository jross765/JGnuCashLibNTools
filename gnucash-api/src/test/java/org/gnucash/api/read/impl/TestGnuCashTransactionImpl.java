package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashTransactionImpl {
	public static final GCshID TRX_1_ID = new GCshID("32b216aa73a44137aa5b041ab8739058");
	public static final GCshID TRX_2_ID = new GCshID("c97032ba41684b2bb5d1391c9d7547e9");
	public static final GCshID TRX_3_ID = new GCshID("d465b802d5c940c9bba04b87b63ba23f");

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashTransaction trx = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashTransactionImpl.class);
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
		trx = gcshFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx);

		assertEquals(TRX_1_ID, trx.getID());
		assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("Dividenderl", trx.getDescription());
		assertEquals("2023-08-06T10:59Z", trx.getDatePosted().toString());
		assertEquals("2023-08-06T08:21:44Z", trx.getDateEntered().toString());

		assertEquals(3, trx.getSplitsCount());
		assertEquals("7abf90fe15124254ac3eb7ec33f798e7", trx.getSplits().get(0).getID().toString());
		assertEquals("ea08a144322146cea38b39d134ca6fc1", trx.getSplits().get(1).getID().toString());
		assertEquals("5c5fa881869843d090a932f8e6b15af2", trx.getSplits().get(2).getID().toString());
	}

	@Test
	public void test02() throws Exception {
		trx = gcshFile.getTransactionByID(TRX_2_ID);
		assertNotEquals(null, trx);

		assertEquals(TRX_2_ID, trx.getID());
		assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("Unfug und Quatsch GmbH", trx.getDescription());
		assertEquals("2023-07-29T10:59Z", trx.getDatePosted().toString());
		assertEquals("2023-09-13T08:36:54Z", trx.getDateEntered().toString());

		assertEquals(2, trx.getSplitsCount());
		assertEquals("f2a67737458d4af4ade616a23db32c2e", trx.getSplits().get(0).getID().toString());
		assertEquals("d17361e4c5a14e84be4553b262839a7b", trx.getSplits().get(1).getID().toString());
	}

	@Test
	public void test03() throws Exception {
		trx = gcshFile.getTransactionByID(TRX_3_ID);
		assertNotEquals(null, trx);
		assertEquals("https://my.transaction.link.01", trx.getURL());
	}
}
