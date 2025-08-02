package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashVendorBillImpl {
	private static final GCshGenerInvcID VEND_BLL_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	private static final GCshGenerInvcID VEND_BLL_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashGenerInvoice invcGen = null;
	private GnuCashVendorBill bllSpec = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashVendorBillImpl.class);
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
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_1_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		assertEquals(true, bllSpec instanceof GnuCashVendorBillImpl);
		assertEquals(VEND_BLL_1_ID, bllSpec.getID());
		assertEquals(GCshOwner.Type.VENDOR, bllSpec.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("1730-383/2", bllSpec.getNumber());
		assertEquals("Sie wissen schon: Gefälligkeiten, ne?", bllSpec.getDescription());

		assertEquals("2023-08-31T10:59Z", bllSpec.getDateOpened().toString());
		// ::TODO
		assertEquals("2023-08-31T10:59Z", bllSpec.getDatePosted().toString());
	}

	@Test
	public void test01_2() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_2_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		assertEquals(true, bllSpec instanceof GnuCashVendorBillImpl);
		assertEquals(VEND_BLL_2_ID, bllSpec.getID());
		assertEquals(GCshOwner.Type.VENDOR, bllSpec.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
		assertEquals("2740921", bllSpec.getNumber());
		assertEquals("Dat isjamaol eine schöne jepflejgte Reschnung!", bllSpec.getDescription());

		assertEquals("2023-08-30T10:59Z", bllSpec.getDateOpened().toString());
		// ::TODO
		assertEquals("2023-08-30T10:59Z", bllSpec.getDatePosted().toString());
	}

	@Test
	public void test02_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_1_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1, invcGen.getGenerEntries().size());
		assertEquals(1, bllSpec.getGenerEntries().size());
		assertEquals(1, bllSpec.getEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(bllSpec.getEntries());
		assertEquals("0041b8d397f04ae4a2e9e3c7f991c4ec",
				((GnuCashVendorBillEntry) entrList.toArray()[0]).getID().toString());
	}

	@Test
	public void test02_2() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_2_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(2, invcGen.getGenerEntries().size());
		assertEquals(2, bllSpec.getGenerEntries().size());
		assertEquals(2, bllSpec.getEntries().size());

		TreeSet entrList = new TreeSet(); // sort elements of HashSet
		entrList.addAll(bllSpec.getEntries());
		assertEquals("513589a11391496cbb8d025fc1e87eaa",
				((GnuCashVendorBillEntry) entrList.toArray()[1]).getID().toString());
		assertEquals("dc3c53f07ff64199ad4ea38988b3f40a",
				((GnuCashVendorBillEntry) entrList.toArray()[0]).getID().toString());
	}

	@Test
	public void test03_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_1_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(41.40, invcGen.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, bllSpec.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, bllSpec.getAmountWithoutTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		// Note: due to (purposefully) incorrect booking, the gross amount
		// of this bill is *not* 49.27 EUR, but 41.40 EUR (its net amount).
		assertEquals(41.40, invcGen.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, bllSpec.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(41.40, bllSpec.getAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test03_2() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_2_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(79.11, invcGen.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(79.11, bllSpec.getVendBllAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(79.11, bllSpec.getAmountWithoutTaxes().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(94.14, invcGen.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(94.14, bllSpec.getVendBllAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(94.14, bllSpec.getAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test04_1() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_1_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		//    assertEquals("xxx", invcGen.getPostTransaction());
		//    assertEquals("xxx", invcSpec.getPostTransaction());

		// ::TODO
		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(0, bllSpec.getPayingTransactions().size());
		assertEquals(0, bllSpec.getPayingTransactions().size());

		//    List<GnuCashTransaction> trxList = (List<GnuCashTransaction>) bllSpec.getPayingTransactions();
		//    Collections.sort(trxList);
		//    assertEquals("xxx", 
		//                 ((GnuCashTransaction) bllSpec.getPayingTransactions().toArray()[0]).getID());

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(false, invcGen.isVendBllFullyPaid());
		assertEquals(false, bllSpec.isVendBllFullyPaid());
		assertEquals(false, bllSpec.isFullyPaid());
	}

	@Test
	public void test04_2() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_2_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals("aa64d862bb5e4d749eb41f198b28d73d", invcGen.getPostTransaction().getID().toString());
		assertEquals("aa64d862bb5e4d749eb41f198b28d73d", bllSpec.getPostTransaction().getID().toString());

		// Note: That the following two return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(1, invcGen.getPayingTransactions().size());
		assertEquals(1, bllSpec.getPayingTransactions().size());

		List<GnuCashTransaction> trxList = (List<GnuCashTransaction>) bllSpec.getPayingTransactions();
		Collections.sort(trxList);
		assertEquals("ccff780b18294435bf03c6cb1ac325c1",
				((GnuCashTransaction) trxList.toArray()[0]).getID().toString());

		// Note: That the following three return the same result
		// is *not* trivial (in fact, a serious implementation error was
		// found with this test)
		assertEquals(true, invcGen.isVendBllFullyPaid());
		assertEquals(true, bllSpec.isVendBllFullyPaid());
		assertEquals(true, bllSpec.isFullyPaid());

		assertEquals(!invcGen.isVendBllFullyPaid(), invcGen.isNotVendBllFullyPaid());
		assertEquals(!bllSpec.isVendBllFullyPaid(), bllSpec.isNotVendBllFullyPaid());
		assertEquals(!bllSpec.isFullyPaid(), bllSpec.isNotFullyPaid());
	}

	@Test
	public void test05() throws Exception {
		invcGen = gcshFile.getGenerInvoiceByID(VEND_BLL_1_ID);
		assertNotEquals(null, invcGen);
		bllSpec = new GnuCashVendorBillImpl(invcGen);
		assertNotEquals(null, bllSpec);

		assertEquals("https://my.vendor.bill.link.01", invcGen.getURL());
		assertEquals(invcGen.getURL(), bllSpec.getURL());
	}
}
