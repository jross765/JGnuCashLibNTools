package org.gnucash.api.write.impl.hlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.hlp.TestHasUserDefinedAttributesImpl;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableCommodity;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestHasWritableUserDefinedAttributesImpl {
	public static final GCshAcctID ACCT_1_ID = TestHasUserDefinedAttributesImpl.ACCT_1_ID;
	public static final GCshAcctID ACCT_2_ID = TestHasUserDefinedAttributesImpl.ACCT_2_ID;
	public static final GCshAcctID ACCT_3_ID = TestHasUserDefinedAttributesImpl.ACCT_3_ID;

	public static final GCshTrxID TRX_1_ID = TestHasUserDefinedAttributesImpl.TRX_1_ID;
	public static final GCshTrxID TRX_2_ID = TestHasUserDefinedAttributesImpl.TRX_2_ID;
	public static final GCshTrxID TRX_3_ID = TestHasUserDefinedAttributesImpl.TRX_3_ID;

	public static final GCshGenerInvcID INVC_2_ID = TestHasUserDefinedAttributesImpl.INVC_2_ID;
	public static final GCshGenerInvcID INVC_3_ID = TestHasUserDefinedAttributesImpl.INVC_3_ID;

	public static final GCshCmdtyID_Exchange  CMDTY_1_ID = TestHasUserDefinedAttributesImpl.CMDTY_1_ID;
	public static final GCshCmdtyID_SecIdType CMDTY_2_ID = TestHasUserDefinedAttributesImpl.CMDTY_2_ID;

	// ---------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestHasWritableUserDefinedAttributesImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshInFileStream = null;
		try {
			gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshInFile = new GnuCashWritableFileImpl(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestHasUserDefinedAttributesImpl.test_xyz
	//
	// Check whether the user attributes returned by
	// HasWritableUserDefinedAttributesImpl.getXYZ() are actually
	// complete (as complete as returned be HasUserDefinedAttributesImpl.getXYZ().
	// -----------------------------------------------------------------

	// -----------------------------------------------------------------
	// Book
	// -----------------------------------------------------------------

	// Several slots
	@Test
	public void test_01_book_01() throws Exception {
		assertNotEquals(null, gcshInFile.getUserDefinedAttributeKeys());

		assertEquals(5, gcshInFile.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS, gcshInFile.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTERS, gcshInFile.getUserDefinedAttributeKeys().get(1));
		assertEquals(ConstTest.SLOT_KEY_BOOK_FEATURES, gcshInFile.getUserDefinedAttributeKeys().get(2));
		assertEquals(ConstTest.SLOT_KEY_BOOK_OPTIONS, gcshInFile.getUserDefinedAttributeKeys().get(3));
		assertEquals(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS, gcshInFile.getUserDefinedAttributeKeys().get(4));

		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		assertEquals("1", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals("3", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		// CAUTION: This one does not work because the key contains a dot ("."),
		// which we have chosen as the hierarchy-separator 
		// (it's a very badly chosen key, b.t.w.).
		//    	assertEquals("Store the register sort and filter settings in .gcm metadata file (requires at least GnuCash 3.3)", 
		//    				 gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
		//    			                                      HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
		//                                                    "Register sort and filter settings stored in .gcm file"));
		assertEquals("Use a dedicated opening balance account identified by an 'equity-type' slot (requires at least Gnucash 4.3)", 
				gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Use a dedicated opening balance account identified by an 'equity-type' slot"));

		assertEquals("f", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Accounts"+ 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Use Trading Accounts"));
		assertEquals("83b1859fd415421cb24f8c72eb755fcc", 
				gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Business.Default Customer TaxTable"));
		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax" + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax Number"));

		assertEquals("true", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS));
	}

	// -----------------------------------------------------------------
	// Account
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_01_acct_01() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(1, acct.getUserDefinedAttributeKeys().size());
		assertEquals("lot-mgmt", acct.getUserDefinedAttributeKeys().get(0));
	}

	// One slot
	@Test
	public void test_acct_02() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertNotEquals(null, acct.getUserDefinedAttributeKeys());
		assertEquals(1, acct.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER, acct.getUserDefinedAttributeKeys().get(0));
		assertEquals("true", acct.getUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER));
	}

	// Account, several slots
	// Such a case does not exist, at least not with "organic" data

	// -----------------------------------------------------------------
	// Transaction
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_01_trx_01() throws Exception {
		GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx);

		assertEquals(null, trx.getUserDefinedAttributeKeys());
	}

	// One slot
	@Test
	public void test_01_trx_02() throws Exception {
		GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(TRX_2_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(1, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals("2023-07-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
	}

	// Several slots
	@Test
	public void test_01_trx_03() throws Exception {
		GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(TRX_3_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(2, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(1));
		assertEquals("https://my.transaction.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
		assertEquals("2023-10-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
	}

	// -----------------------------------------------------------------
	// Transaction Split
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Invoice
	// -----------------------------------------------------------------

	// No slots
	// Such a case does not exist, at least not with "organic" data

	// One slot
	@Test
	public void test_01_invc_02() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
		assertNotEquals(null, invc);

		assertNotEquals(null, invc.getUserDefinedAttributeKeys());
		assertEquals(1, invc.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, invc.getUserDefinedAttributeKeys().get(0));
		assertEquals("0", invc.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
	}

	// Several slots
	@Test
	public void test_01_invc_03() throws Exception {
		GnuCashWritableGenerInvoice trx = gcshInFile.getWritableGenerInvoiceByID(INVC_3_ID);
		assertNotEquals(null, trx);

		assertNotEquals(null, trx.getUserDefinedAttributeKeys());
		assertEquals(2, trx.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, trx.getUserDefinedAttributeKeys().get(1));
		assertEquals("https://my.job.invoice.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
		assertEquals("0", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
	}

	// -----------------------------------------------------------------
	// Invoice Entry
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Commodity
	// -----------------------------------------------------------------

	// No slots
	@Test
	public void test_01_cmdty_01() throws Exception {
		GnuCashWritableCommodity cmdty = gcshInFile.getWritableCommodityByQualifID(CMDTY_1_ID);
		assertNotEquals(null, cmdty);

		assertEquals(null, cmdty.getUserDefinedAttributeKeys());
	}

	// One slot
	@Test
	public void test_01_cmdty_02() throws Exception {
		GnuCashWritableCommodity cmdty = gcshInFile.getWritableCommodityByQualifID(CMDTY_2_ID);
		assertNotEquals(null, cmdty);

		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys());
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL, cmdty.getUserDefinedAttributeKeys().get(0));
		assertEquals("BAS", cmdty.getUserDefinedAttribute(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL));
	}

	// Several slots
	// There are none with several slots, at least not "organically"

	// -----------------------------------------------------------------
	// Customer
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Vendor
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Employee
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the values accessed by HasWritableUserDefinedAttributesImpl
	// can actually be modified -- both in memory and persisted in file.
	// -----------------------------------------------------------------

	// -----------------------------------------------------------------
	// Book
	// -----------------------------------------------------------------

	// Several slots
	@Test
	public void test_02_book_01() throws Exception {
		assertNotEquals(null, gcshInFile.getUserDefinedAttributeKeys());

		assertEquals(5, gcshInFile.getUserDefinedAttributeKeys().size());
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS, gcshInFile.getUserDefinedAttributeKeys().get(0));
		assertEquals(ConstTest.SLOT_KEY_BOOK_COUNTERS, gcshInFile.getUserDefinedAttributeKeys().get(1));
		assertEquals(ConstTest.SLOT_KEY_BOOK_FEATURES, gcshInFile.getUserDefinedAttributeKeys().get(2));
		assertEquals(ConstTest.SLOT_KEY_BOOK_OPTIONS, gcshInFile.getUserDefinedAttributeKeys().get(3));
		assertEquals(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS, gcshInFile.getUserDefinedAttributeKeys().get(4));

		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTER_FORMATS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		assertEquals("1", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncBill"));
		assertEquals("3", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_COUNTERS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"gncCustomer"));
		// etc.

		// CAUTION: This one does not work because the key contains a dot ("."),
		// which we have chosen as the hierarchy-separator 
		// (it's a very badly chosen key, b.t.w.).
		//    	assertEquals("Store the register sort and filter settings in .gcm metadata file (requires at least GnuCash 3.3)", 
		//    				 gcshFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
		//    			                                      HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
		//                                                    "Register sort and filter settings stored in .gcm file"));
		assertEquals("Use a dedicated opening balance account identified by an 'equity-type' slot (requires at least Gnucash 4.3)", 
				gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_FEATURES + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Use a dedicated opening balance account identified by an 'equity-type' slot"));

		assertEquals("f", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Accounts"+ 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Use Trading Accounts"));
		assertEquals("83b1859fd415421cb24f8c72eb755fcc", 
				gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
						HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
						"Business.Default Customer TaxTable"));
		assertEquals(null, gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_OPTIONS + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax" + 
				HasUserDefinedAttributesImpl.HIERARCHY_SEPARATOR + 
				"Tax Number"));

		assertEquals("true", gcshInFile.getUserDefinedAttribute(ConstTest.SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS));
	}

	// -----------------------------------------------------------------
	// Account
	// -----------------------------------------------------------------

	// ----------------------------
	// No slots

	@Test
	public void test_02_acct_01() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID());

		// ----------------------------
		// Modify the object

		try {
			acct.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			acct.addUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI, "abc", "http://bore.dom");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_acct_01_check_memory(acct);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_acct_01_check_persisted(outFile);
	}

	private void test_02_acct_01_check_memory(GnuCashWritableAccount acct) throws Exception {
		assertEquals(ACCT_1_ID, acct.getID()); // unchanged
		assertNotEquals(null, acct.getUserDefinedAttributeKeys()); // changed
		assertEquals(2, acct.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", acct.getUserDefinedAttribute("abc")); // changed
	}

	private void test_02_acct_01_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashAccount acct = gcshOutFile.getAccountByID(ACCT_1_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_1_ID, acct.getID()); // unchanged
		assertNotEquals(null, acct.getUserDefinedAttributeKeys()); // changed
		assertEquals(2, acct.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", acct.getUserDefinedAttribute("abc")); // changed
	}

	// ----------------------------
	// One or more slots

	@Test
	public void test_02_acct_02() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_2_ID, acct.getID());

		// ----------------------------
		// Modify the object

		try {
			acct.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			assertEquals(0, 0);
			acct.setUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER, "false");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_acct_02_check_memory(acct);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_acct_02_check_persisted(outFile);
	}

	private void test_02_acct_02_check_memory(GnuCashWritableAccount acct) throws Exception {
		assertEquals(ACCT_2_ID, acct.getID()); // unchanged
		assertNotEquals(null, acct.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, acct.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("false", acct.getUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER)); // changed
	}

	private void test_02_acct_02_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashAccount acct = gcshOutFile.getAccountByID(ACCT_2_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_2_ID, acct.getID()); // unchanged
		assertNotEquals(null, acct.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, acct.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("false", acct.getUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER)); // changed
	}

	// ----------------------------
	// Top-level account

	@Test
	public void test_02_acct_03() throws Exception {
		GnuCashWritableAccount acct = gcshInFile.getWritableAccountByID(ACCT_3_ID);
		assertNotEquals(null, acct);

		assertEquals(ACCT_3_ID, acct.getID());

		// ----------------------------
		// Modify the object

		try {
			acct.setUserDefinedAttribute("abc", "def"); // illegal call, because is top-level account
			assertEquals(0, 1);
		} catch ( UnsupportedOperationException exc ) {
			assertEquals(0, 0);
		}
	}

	// -----------------------------------------------------------------
	// Transaction
	// -----------------------------------------------------------------

	// ----------------------------
	// No slots

	@Test
	public void test_02_trx_01() throws Exception {
		GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx);

		assertEquals(TRX_1_ID, trx.getID());

		// ----------------------------
		// Modify the object

		try {
			trx.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			trx.addUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI, "abc", "http://bore.dom");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_trx_01_check_memory(trx);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_trx_01_check_persisted(outFile);
	}

	private void test_02_trx_01_check_memory(GnuCashWritableTransaction trx) throws Exception {
		assertEquals(TRX_1_ID, trx.getID()); // unchanged
		assertNotEquals(null, trx.getUserDefinedAttributeKeys()); // changed
		assertEquals(1, trx.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", trx.getUserDefinedAttribute("abc")); // changed
	}

	private void test_02_trx_01_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashTransaction trx = gcshOutFile.getTransactionByID(TRX_1_ID);
		assertNotEquals(null, trx);

		assertEquals(TRX_1_ID, trx.getID()); // unchanged
		assertNotEquals(null, trx.getUserDefinedAttributeKeys()); // changed
		assertEquals(1, trx.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", trx.getUserDefinedAttribute("abc")); // changed
	}

	// ----------------------------
	// One or more slots

	// ::TODO / Still with errors:

	//    @Test
	//    public void test_02_trx_02() throws Exception {
	//    	GnuCashWritableTransaction trx = gcshInFile.getWritableTransactionByID(TRX_2_ID);
	//    	assertNotEquals(null, trx);
	//
	//    	assertEquals(TRX_2_ID, trx.getID());
	//
	//    	// ----------------------------
	//    	// Modify the object
	//
	//    	try {
	//        	trx.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
	//        	assertEquals(0, 1);
	//    	} catch ( SlotListDoesNotContainKeyException exc ) {
	//        	trx.setUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED, "2024-01-05");
	//    	}
	//
	//    	// ----------------------------
	//    	// Check whether the object can has actually be modified
	//    	// (in memory, not in the file yet).
	//
	//    	test_02_trx_02_check_memory(trx);
	//
	//    	// ----------------------------
	//    	// Now, check whether the modified object can be written to the
	//    	// output file, then re-read from it, and whether is is what
	//    	// we expect it is.
	//
	//    	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	//    	// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
	//    	// + outFile.getPath() + "'");
	//    	outFile.delete(); // sic, the temp. file is already generated (empty),
	//    			          // and the GnuCash file writer does not like that.
	//    	gcshInFile.writeFile(outFile);
	//
	//    	test_02_trx_02_check_persisted(outFile);
	//    }
	//    
	//    private void test_02_trx_02_check_memory(GnuCashWritableTransaction trx) throws Exception {
	//    	assertEquals(TRX_2_ID, trx.getID()); // unchanged
	//    	assertNotEquals(null, trx.getUserDefinedAttributeKeys()); // unchanged
	//    	assertEquals(1, trx.getUserDefinedAttributeKeys().size()); // unchanged
	//    	assertEquals("Fri Jan 05 00:00:00 CET 2024", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED)); // changed
	//    }
	//
	//	private void test_02_trx_02_check_persisted(File outFile) throws Exception {
	//		gcshOutFile = new GnuCashFileImpl(outFile);
	//
	//		GnuCashTransaction trx = gcshOutFile.getTransactionByID(TRX_2_ID);
	//		assertNotEquals(null, trx);
	//
	//    	assertEquals(TRX_2_ID, trx.getID()); // unchanged
	//    	assertNotEquals(null, trx.getUserDefinedAttributeKeys()); // unchanged
	//    	assertEquals(1, trx.getUserDefinedAttributeKeys().size()); // unchanged
	//    	// sic:
	//    	assertEquals("Fri Jan 05 00:00:00 CET 2024", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED)); // changed
	//	}

	// -----------------------------------------------------------------
	// Transaction Split
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Invoice
	// -----------------------------------------------------------------

	// ----------------------------
	// No slots

	// No such case

	// ----------------------------
	// One or more slots

	@Test
	public void test_02_invc_02() throws Exception {
		GnuCashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals(INVC_2_ID, invc.getID());

		// ----------------------------
		// Modify the object

		try {
			invc.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			invc.setUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, "1");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_invc_02_check_memory(invc);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_invc_02_check_persisted(outFile);
	}

	private void test_02_invc_02_check_memory(GnuCashWritableGenerInvoice invc) throws Exception {
		assertEquals(INVC_2_ID, invc.getID()); // unchanged
		assertNotEquals(null, invc.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, invc.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("1", invc.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE)); // changed
	}

	private void test_02_invc_02_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashGenerInvoice invc = gcshOutFile.getGenerInvoiceByID(INVC_2_ID);
		assertNotEquals(null, invc);

		assertEquals(INVC_2_ID, invc.getID()); // unchanged
		assertNotEquals(null, invc.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, invc.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("1", invc.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE)); // changed
	}

	// -----------------------------------------------------------------
	// Invoice Entry
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Commodity
	// -----------------------------------------------------------------

	// ----------------------------
	// No slots

	@Test
	public void test_02_cmdty_01() throws Exception {
		GnuCashWritableCommodity cmdty = gcshInFile.getWritableCommodityByQualifID(CMDTY_1_ID);
		assertNotEquals(null, cmdty);

		assertEquals(CMDTY_1_ID.toString(), cmdty.getQualifID().toString());

		// ----------------------------
		// Modify the object

		try {
			cmdty.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			cmdty.addUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI, "abc", "http://bore.dom");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_cmdty_01_check_memory(cmdty);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_cmdty_01_check_persisted(outFile);
	}

	private void test_02_cmdty_01_check_memory(GnuCashWritableCommodity cmdty) throws Exception {
		assertEquals(CMDTY_1_ID.toString(), cmdty.getQualifID().toString()); // unchanged
		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys()); // changed
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", cmdty.getUserDefinedAttribute("abc")); // changed
	}

	private void test_02_cmdty_01_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashCommodity cmdty = gcshOutFile.getCommodityByQualifID(CMDTY_1_ID);
		assertNotEquals(null, cmdty);

		assertEquals(CMDTY_1_ID.toString(), cmdty.getQualifID().toString()); // unchanged
		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys()); // changed
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size()); // changed
		assertEquals("http://bore.dom", cmdty.getUserDefinedAttribute("abc")); // changed
	}

	// ----------------------------
	// One or more slots

	@Test
	public void test_02_cmdty_02() throws Exception {
		GnuCashWritableCommodity cmdty = gcshInFile.getWritableCommodityByQualifID(CMDTY_2_ID);
		assertNotEquals(null, cmdty);

		assertEquals(CMDTY_2_ID.toString(), cmdty.getQualifID().toString());

		// ----------------------------
		// Modify the object

		try {
			cmdty.setUserDefinedAttribute("abc", "def"); // illegal call, because does not exist
			assertEquals(0, 1);
		} catch ( SlotListDoesNotContainKeyException exc ) {
			cmdty.setUserDefinedAttribute(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL, "KUXYZPP082");
		}

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test_02_cmdty_02_check_memory(cmdty);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test_02_cmdty_02_check_persisted(outFile);
	}

	private void test_02_cmdty_02_check_memory(GnuCashWritableCommodity cmdty) throws Exception {
		assertEquals(CMDTY_2_ID.toString(), cmdty.getQualifID().toString()); // unchanged
		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("KUXYZPP082", cmdty.getUserDefinedAttribute(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL)); // changed
	}

	private void test_02_cmdty_02_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);

		GnuCashCommodity cmdty = gcshOutFile.getCommodityByQualifID(CMDTY_2_ID);
		assertNotEquals(null, cmdty);

		assertEquals(CMDTY_2_ID.toString(), cmdty.getQualifID().toString()); // unchanged
		assertNotEquals(null, cmdty.getUserDefinedAttributeKeys()); // unchanged
		assertEquals(1, cmdty.getUserDefinedAttributeKeys().size()); // unchanged
		assertEquals("KUXYZPP082", cmdty.getUserDefinedAttribute(ConstTest.SLOT_KEY_CMDTY_USER_SYMBOL)); // changed
	}

	// -----------------------------------------------------------------
	// Customer
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Vendor
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

	// -----------------------------------------------------------------
	// Employee
	// -----------------------------------------------------------------

	// ::TODO
	// There are none with slots

}
