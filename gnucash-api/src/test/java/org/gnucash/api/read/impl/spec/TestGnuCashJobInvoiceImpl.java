package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashJobInvoiceImpl {
	private static final GCshID INVC_3_ID = TestGnuCashGenerInvoiceImpl.INVC_3_ID;
	private static final GCshID INVC_5_ID = TestGnuCashGenerInvoiceImpl.INVC_5_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoice invcGen = null;
	private GnuCashJobInvoice invcSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashJobInvoiceImpl.class);
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
		invcGen = gcshFile.getGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashJobInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		assertEquals(true, invcSpec instanceof GnuCashJobInvoiceImpl);
		assertEquals(INVC_3_ID, invcSpec.getID());
		assertEquals(GCshOwner.Type.JOB, invcSpec.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("R94871", invcSpec.getNumber());
		assertEquals("With customer job / with taxes", invcSpec.getDescription());

		assertEquals("2023-09-20T10:59Z", invcSpec.getDateOpened().toString());
		assertEquals("2023-09-20T10:59Z", invcSpec.getDatePosted().toString());
	}

	@Test
	public void test02_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashJobInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(3, invcGen.getGenerEntries().size());
		assertEquals(3, invcSpec.getGenerEntries().size());
		assertEquals(3, invcSpec.getEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(invcSpec.getEntries());
		assertEquals("fa483972d10a4ce0abf2a7e1319706e7",
				((GnuCashJobInvoiceEntry) entrList.toArray()[0]).getID().toString());
		assertEquals("eb5eb3b7c1e34965b36fb6d5af183e82",
				((GnuCashJobInvoiceEntry) entrList.toArray()[1]).getID().toString());
		assertEquals("993eae09ce664094adf63b85509de2bc",
				((GnuCashJobInvoiceEntry) entrList.toArray()[2]).getID().toString());
	}

	@Test
	public void test03_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashJobInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		// ::TODO
		//    assertEquals(1327.60, invcGen.getJobAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//    assertEquals(1327.60, invcSpec.getJobAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(4125.0, invcSpec.getAmountWithoutTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		// ::TODO
		//    assertEquals(1327.60, invcGen.getJobAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		//    assertEquals(1327.60, invcSpec.getJobAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(4908.75, invcSpec.getAmountWithTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);
	}

	// ::TODO
	@Test
	public void test04_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashJobInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		// ::TODO
		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		//    assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invcGen.getPostTransaction().getID());
		//    assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invcSpec.getPostTransaction().getID());

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(0, invcGen.getPayingTransactions().size());
		assertEquals(0, invcSpec.getPayingTransactions().size());

		//    List<GnuCashTransaction> trxList = (List<GnuCashTransaction>) invcSpec.getPayingTransactions();
		//    Collections.sort(trxList);
		//    assertEquals("29557cfdf4594eb68b1a1b710722f991", 
		//                 ((GnuCashTransaction) trxList.toArray()[0]).getID());

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(false, invcGen.isJobInvcFullyPaid());
		assertEquals(false, invcSpec.isJobInvcFullyPaid());
		assertEquals(false, invcSpec.isFullyPaid());
	}

	@Test
	public void test05() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_5_ID);
		assertNotEquals(null, invcGen);
		invcSpec = new GnuCashJobInvoiceImpl(invcGen);
		assertNotEquals(null, invcSpec);

		assertEquals("https://my.job.invoice.link.01", invcGen.getURL());
		assertEquals(invcGen.getURL(), invcSpec.getURL());
	}
}
