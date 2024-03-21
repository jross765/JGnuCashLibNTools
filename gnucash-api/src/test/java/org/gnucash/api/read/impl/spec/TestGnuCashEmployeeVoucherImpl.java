package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashEmployeeVoucherImpl {
	private static final GCshID INVC_7_ID = TestGnuCashGenerInvoiceImpl.INVC_7_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoice invcGen = null;
	private GnuCashEmployeeVoucher vchSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashEmployeeVoucherImpl.class);
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
		invcGen = gcshFile.getGenerInvoiceByID(INVC_7_ID);
		assertNotEquals(null, invcGen);
		vchSpec = new GnuCashEmployeeVoucherImpl(invcGen);
		assertNotEquals(null, vchSpec);

		assertEquals(true, vchSpec instanceof GnuCashEmployeeVoucherImpl);
		assertEquals(INVC_7_ID, vchSpec.getID());
		assertEquals(GCshOwner.Type.EMPLOYEE, vchSpec.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("000001", vchSpec.getNumber());
		assertEquals("Spesen Geschäftsreise", vchSpec.getDescription());

		assertEquals("2023-11-13T10:59Z", vchSpec.getDateOpened().toString());
		assertEquals("2023-11-13T10:59Z", vchSpec.getDatePosted().toString());
	}

	@Test
	public void test02_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_7_ID);
		assertNotEquals(null, invcGen);
		vchSpec = new GnuCashEmployeeVoucherImpl(invcGen);
		assertNotEquals(null, vchSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(2, invcGen.getGenerEntries().size());
		assertEquals(2, vchSpec.getGenerEntries().size());
		assertEquals(2, vchSpec.getEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(vchSpec.getEntries());
		assertEquals("b6e2313e32d44bb4a8a701c1063e03a7",
				((GnuCashEmployeeVoucherEntry) entrList.toArray()[0]).getID().toString());
		assertEquals("9218e269422d4c08b3b9a8c27fbd051a",
				((GnuCashEmployeeVoucherEntry) entrList.toArray()[1]).getID().toString());
	}

	@Test
	public void test03_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_7_ID);
		assertNotEquals(null, invcGen);
		vchSpec = new GnuCashEmployeeVoucherImpl(invcGen);
		assertNotEquals(null, vchSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(230.0, invcGen.getEmplVchAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(230.0, vchSpec.getEmplVchAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(230.0, vchSpec.getAmountWithoutTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(230.0, invcGen.getEmplVchAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(230.0, vchSpec.getEmplVchAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(230.0, vchSpec.getAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test04_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(INVC_7_ID);
		assertNotEquals(null, invcGen);
		vchSpec = new GnuCashEmployeeVoucherImpl(invcGen);
		assertNotEquals(null, vchSpec);

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		// ::TODO
		assertEquals(null, invcGen.getPostTransaction());
		assertEquals(null, vchSpec.getPostTransaction());

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(0, invcGen.getPayingTransactions().size());
		assertEquals(0, vchSpec.getPayingTransactions().size());

		List<GnuCashTransaction> trxList = (List<GnuCashTransaction>) vchSpec.getPayingTransactions();
		// ::TODO
		assertEquals(0, trxList.size());
		//    Collections.sort(trxList);
		//    assertEquals("29557cfdf4594eb68b1a1b710722f991", 
		//                 ((GnuCashTransaction) trxList.toArray()[0]).getID());

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(false, invcGen.isEmplVchFullyPaid());
		assertEquals(false, vchSpec.isEmplVchFullyPaid());
		assertEquals(false, vchSpec.isFullyPaid());

		assertEquals(!invcGen.isEmplVchFullyPaid(), invcGen.isNotEmplVchFullyPaid());
		assertEquals(!vchSpec.isEmplVchFullyPaid(), vchSpec.isNotEmplVchFullyPaid());
		assertEquals(!vchSpec.isFullyPaid(), vchSpec.isNotFullyPaid());
	}

	//  @Test
	//  public void test05() throws Exception
	//  {
	//    invcGen = gcshFile.getGenerInvoiceByID(INVC_6_ID);
	//    assertNotEquals(null, invcGen);
	//    vchSpec = new GnuCashCustomerInvoiceImpl(invcGen);
	//    assertNotEquals(null, vchSpec);
	//
	//    assertEquals("https://my.customer.invoice.link.01", invcGen.getURL());
	//    assertEquals(invcGen.getURL(), vchSpec.getURL());
	//  }
}
