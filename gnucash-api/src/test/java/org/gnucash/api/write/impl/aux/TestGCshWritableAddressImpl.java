package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshAddressImpl;
import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableAddressImpl {
	
	// -----------------------------------------------------------------

	public static final GCshCustID CUST_1_ID = TestGCshAddressImpl.CUST_1_ID;
	public static final GCshCustID CUST_2_ID = TestGCshAddressImpl.CUST_2_ID;
	
	public static final GCshVendID VEND_1_ID = TestGCshAddressImpl.VEND_1_ID;
	public static final GCshVendID VEND_2_ID = TestGCshAddressImpl.VEND_2_ID;

	public static final GCshEmplID EMPL_1_ID = TestGCshAddressImpl.EMPL_1ID;
	
	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private GnuCashWritableCustomer cust1 = null;
	private GnuCashWritableCustomer cust2 = null;
	
	private GnuCashWritableVendor   vend1 = null;
	private GnuCashWritableVendor   vend2 = null;
	
	private GnuCashWritableEmployee empl1 = null;
	
	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshWritableAddressImpl.class);
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
		
		// ---
		
		cust1 = gcshInFile.getWritableCustomerByID(CUST_1_ID);
		cust2 = gcshInFile.getWritableCustomerByID(CUST_2_ID);
		
		vend1 = gcshInFile.getWritableVendorByID(VEND_1_ID);
		vend2 = gcshInFile.getWritableVendorByID(VEND_2_ID);
		
		empl1 = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGCshBillTermsImpl.testxyz
	//
	// Check whether the GCshWritableBillTerms objects returned by
	// GnuCashWritableFileImpl.getWritableTaxTableByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getBillTermsByID().

	@Test
	public void test01_1() throws Exception {
		GCshWritableAddress addr = cust1.getWritableAddress();
		assertNotEquals(null, addr);
		
		assertEquals("Herr SchwervonBegriff", addr.getAddressName());
		
		assertEquals("Nixkapier-Str. 9", addr.getAddressLine1());
		assertEquals("12345 Berlin", addr.getAddressLine2());
		assertEquals("", addr.getAddressLine3());
		assertEquals("", addr.getAddressLine4());
		
		assertEquals("", addr.getTel());
		assertEquals("", addr.getFax());
		assertEquals("", addr.getEmail());
	}

	@Test
	public void test01_2() throws Exception {
		GCshWritableAddress addr = vend1.getWritableAddress();
		assertNotEquals(null, addr);
		
		assertEquals("Frau Schbörzel-Schnurrenburgen", addr.getAddressName());
		
		assertEquals("Über den Linden 81", addr.getAddressLine1());
		assertEquals("12345 Berlin", addr.getAddressLine2());
		assertEquals("", addr.getAddressLine3());
		assertEquals("", addr.getAddressLine4());
		
		assertEquals("", addr.getTel());
		assertEquals("", addr.getFax());
		assertEquals("", addr.getEmail());
	}

	@Test
	public void test01_3() throws Exception {
		GCshWritableAddress addr = empl1.getWritableAddress();
		assertNotEquals(null, addr);
		
		assertEquals("Oliver Twist", addr.getAddressName());
		
		assertEquals("Street Gang's Area", addr.getAddressLine1());
		assertEquals("Bad Neighbourhood", addr.getAddressLine2());
		assertEquals("London", addr.getAddressLine3());
		assertEquals("United Kingdom", addr.getAddressLine4());
		
		assertEquals("+44 - 12 - 345 678 9", addr.getTel());
		assertEquals("", addr.getFax());
		assertEquals("otwist@myco.com", addr.getEmail());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GCshWritableBillTerms objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		GCshWritableAddress addr = cust1.getWritableAddress();
		assertNotEquals(null, addr);

		// ----------------------------
		// Modify the object

		addr.setAddressName("Herr Jetztdochkapiert");
		
		addr.setAddressLine1("Zehnerl-g'rutscht-Str. 10");
		addr.setAddressLine2("76543 Stuttgart");
		addr.setAddressLine3("abc");
		addr.setAddressLine4("def");
		
		addr.setTel("+49-711-27 17 44 00");
		addr.setFax("+49-711-27 17 44 11");
		addr.setEmail("jetztdoch@unfugquatsch.de");

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(addr);

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

		test02_1_check_persisted(outFile);
	}

	@Test
	public void test02_2() throws Exception {
		GCshWritableAddress addr = vend1.getWritableAddress();
		assertNotEquals(null, addr);

		// ----------------------------
		// Modify the object

		addr.setAddressName("Frau Brüllaffe-Honigbär");
		
		addr.setAddressLine1("Unter der alten Brücke");
		addr.setAddressLine2("12645 Berlin");
		addr.setAddressLine3("ghi");
		addr.setAddressLine4("jkl");
		
		addr.setTel("+49-171-27349376409374934 + 1");
		addr.setFax("+49-171-27349376409374934 + 2");
		addr.setEmail("bruellaff.usw@lieferfanto");

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_2_check_memory(addr);

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

		test02_2_check_persisted(outFile);
	}
	
	// ---------------------------------------------------------------

	private void test02_1_check_memory(GCshWritableAddress addr) throws Exception {
		assertEquals("Herr Jetztdochkapiert", addr.getAddressName()); // changed
		
		assertEquals("Zehnerl-g'rutscht-Str. 10", addr.getAddressLine1()); // changed
		assertEquals("76543 Stuttgart", addr.getAddressLine2()); // changed
		assertEquals("abc", addr.getAddressLine3()); // changed
		assertEquals("def", addr.getAddressLine4()); // changed
		
		assertEquals("+49-711-27 17 44 00", addr.getTel()); // changed
		assertEquals("+49-711-27 17 44 11", addr.getFax()); // changed
		assertEquals("jetztdoch@unfugquatsch.de", addr.getEmail()); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashCustomer cust11 = gcshOutFile.getCustomerByID(CUST_1_ID);
		GCshAddress addr = cust11.getAddress();
		assertNotEquals(null, addr);

		assertEquals("Herr Jetztdochkapiert", addr.getAddressName()); // changed
		
		assertEquals("Zehnerl-g'rutscht-Str. 10", addr.getAddressLine1()); // changed
		assertEquals("76543 Stuttgart", addr.getAddressLine2()); // changed
		assertEquals("abc", addr.getAddressLine3()); // changed
		assertEquals("def", addr.getAddressLine4()); // changed
		
		assertEquals("+49-711-27 17 44 00", addr.getTel()); // changed
		assertEquals("+49-711-27 17 44 11", addr.getFax()); // changed
		assertEquals("jetztdoch@unfugquatsch.de", addr.getEmail()); // changed
	}

	// ----------------------------

	private void test02_2_check_memory(GCshWritableAddress addr) throws Exception {
		assertEquals("Frau Brüllaffe-Honigbär", addr.getAddressName()); // changed
		
		assertEquals("Unter der alten Brücke", addr.getAddressLine1()); // changed
		assertEquals("12645 Berlin", addr.getAddressLine2()); // changed
		assertEquals("ghi", addr.getAddressLine3()); // changed
		assertEquals("jkl", addr.getAddressLine4()); // changed
		
		assertEquals("+49-171-27349376409374934 + 1", addr.getTel()); // changed
		assertEquals("+49-171-27349376409374934 + 2", addr.getFax()); // changed
		assertEquals("bruellaff.usw@lieferfanto", addr.getEmail()); // changed
	}

	private void test02_2_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashVendor vend11 = gcshOutFile.getVendorByID(VEND_1_ID);
		GCshAddress addr = vend11.getAddress();
		assertNotEquals(null, addr);

		assertEquals("Frau Brüllaffe-Honigbär", addr.getAddressName()); // changed
		
		assertEquals("Unter der alten Brücke", addr.getAddressLine1()); // changed
		assertEquals("12645 Berlin", addr.getAddressLine2()); // changed
		assertEquals("ghi", addr.getAddressLine3()); // changed
		assertEquals("jkl", addr.getAddressLine4()); // changed
		
		assertEquals("+49-171-27349376409374934 + 1", addr.getTel()); // changed
		assertEquals("+49-171-27349376409374934 + 2", addr.getFax()); // changed
		assertEquals("bruellaff.usw@lieferfanto", addr.getEmail()); // changed
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1() throws Exception {
		GCshWritableAddress addr1 = cust2.getWritableAddress();
		GCshWritableAddress addr2 = cust2.getWritableShippingAddress();

		// ---

		addr1.setAddressName("Roderich der Schreckliche");
		
		addr1.setAddressLine1("Am alten Burggemäuer 1");
		addr1.setAddressLine2("Altburgen-Schnodderhausen, hinter dem Walde");
		addr1.setAddressLine3("mno");
		addr1.setAddressLine4("pqr");
		
		addr1.setTel("Tele-was?");
		addr1.setFax("Schick' er einen berittenen Boten!");
		addr1.setEmail("Verzeihung?");
		
		// ---

		addr2.setAddressName("Galvine die Liebliche");
		
		addr2.setAddressLine1("Bergfried");
		addr2.setAddressLine2("Neuenburgen-Schnodderhausen, auch hinter dem Walde");
		addr2.setAddressLine3("stu");
		addr2.setAddressLine4("vwx");
		
		addr2.setTel("Er fragt merkwürd'ge Dinge");
		addr2.setFax("Faxen geziemen sich nicht für ein Burgfräulein");
		addr2.setEmail("Wir haben kein Badezimmer");

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_check_memory(addr1, addr2);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_check_persisted(outFile);
	}

	@Test
	public void test03_2() throws Exception {
		GCshWritableAddress addr1 = vend2.getWritableAddress();

		// ---

		addr1.setAddressName("Capt'n Kork");
		
		addr1.setAddressLine1("Traumschiff Surprise");
		addr1.setAddressLine2("Irgendwo im WELTALL-all-all...");
		addr1.setAddressLine3("H2O2");
		addr1.setAddressLine4("ZPOx");
		
		addr1.setTel("1384315432841354384131514354422663184");
		addr1.setFax("1384315432841354384131514354422663185");
		addr1.setEmail("kork@surprise.worldgov");
		
		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_2_check_memory(addr1);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_2_check_persisted(outFile);
	}
	
	@Test
	public void test03_3() throws Exception {
		GCshWritableAddress addr1 = empl1.getWritableAddress();

		// ---

		addr1.setAddressName("Come-on baby, let's do the Oliver Twist!");
		
		addr1.setAddressLine1("sdfg");
		addr1.setAddressLine2("aödslfkja");
		addr1.setAddressLine3("aölkdjf");
		addr1.setAddressLine4("asdöfk");
		
		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_3_check_memory(addr1);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_3_check_persisted(outFile);
	}
	
	// ---------------------------------------------------------------

	private void test03_1_check_memory(GCshWritableAddress addr1, GCshWritableAddress addr2) throws Exception {
		assertEquals("Roderich der Schreckliche", addr1.getAddressName()); // changed
		
		assertEquals("Am alten Burggemäuer 1", addr1.getAddressLine1()); // changed
		assertEquals("Altburgen-Schnodderhausen, hinter dem Walde", addr1.getAddressLine2()); // changed
		assertEquals("mno", addr1.getAddressLine3()); // changed
		assertEquals("pqr", addr1.getAddressLine4()); // changed
		
		assertEquals("Tele-was?", addr1.getTel()); // changed
		assertEquals("Schick' er einen berittenen Boten!", addr1.getFax()); // changed
		assertEquals("Verzeihung?", addr1.getEmail()); // changed
		
		// ---
		
		assertEquals("Galvine die Liebliche", addr2.getAddressName()); // changed
		
		assertEquals("Bergfried", addr2.getAddressLine1()); // changed
		assertEquals("Neuenburgen-Schnodderhausen, auch hinter dem Walde", addr2.getAddressLine2()); // changed
		assertEquals("stu", addr2.getAddressLine3()); // changed
		assertEquals("vwx", addr2.getAddressLine4()); // changed
		
		assertEquals("Er fragt merkwürd'ge Dinge", addr2.getTel()); // changed
		assertEquals("Faxen geziemen sich nicht für ein Burgfräulein", addr2.getFax()); // changed
		assertEquals("Wir haben kein Badezimmer", addr2.getEmail()); // changed		
	}

	private void test03_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashCustomer cust11 = gcshOutFile.getCustomerByID(CUST_2_ID);
		GCshAddress addr1 = cust11.getAddress();
		GCshAddress addr2 = cust11.getShippingAddress();
		assertNotEquals(null, addr1);
		assertNotEquals(null, addr2);

		// ---
		
		assertEquals("Roderich der Schreckliche", addr1.getAddressName()); // changed
		
		assertEquals("Am alten Burggemäuer 1", addr1.getAddressLine1()); // changed
		assertEquals("Altburgen-Schnodderhausen, hinter dem Walde", addr1.getAddressLine2()); // changed
		assertEquals("mno", addr1.getAddressLine3()); // changed
		assertEquals("pqr", addr1.getAddressLine4()); // changed
		
		assertEquals("Tele-was?", addr1.getTel()); // changed
		assertEquals("Schick' er einen berittenen Boten!", addr1.getFax()); // changed
		assertEquals("Verzeihung?", addr1.getEmail()); // changed
		
		// ---
		
		assertEquals("Galvine die Liebliche", addr2.getAddressName()); // changed
		
		assertEquals("Bergfried", addr2.getAddressLine1()); // changed
		assertEquals("Neuenburgen-Schnodderhausen, auch hinter dem Walde", addr2.getAddressLine2()); // changed
		assertEquals("stu", addr2.getAddressLine3()); // changed
		assertEquals("vwx", addr2.getAddressLine4()); // changed
		
		assertEquals("Er fragt merkwürd'ge Dinge", addr2.getTel()); // changed
		assertEquals("Faxen geziemen sich nicht für ein Burgfräulein", addr2.getFax()); // changed
		assertEquals("Wir haben kein Badezimmer", addr2.getEmail()); // changed		
	}

	// ----------------------------

	private void test03_2_check_memory(GCshWritableAddress addr1) throws Exception {
		assertEquals("Capt'n Kork", addr1.getAddressName()); // changed
		
		assertEquals("Traumschiff Surprise", addr1.getAddressLine1()); // changed
		assertEquals("Irgendwo im WELTALL-all-all...", addr1.getAddressLine2()); // changed
		assertEquals("H2O2", addr1.getAddressLine3()); // changed
		assertEquals("ZPOx", addr1.getAddressLine4()); // changed
		
		assertEquals("1384315432841354384131514354422663184", addr1.getTel()); // changed
		assertEquals("1384315432841354384131514354422663185", addr1.getFax()); // changed
		assertEquals("kork@surprise.worldgov", addr1.getEmail()); // changed
	}

	private void test03_2_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashVendor vend11 = gcshOutFile.getVendorByID(VEND_2_ID);
		GCshAddress addr1 = vend11.getAddress();
		assertNotEquals(null, addr1);

		// ---
		
		assertEquals("Capt'n Kork", addr1.getAddressName()); // changed
		
		assertEquals("Traumschiff Surprise", addr1.getAddressLine1()); // changed
		assertEquals("Irgendwo im WELTALL-all-all...", addr1.getAddressLine2()); // changed
		assertEquals("H2O2", addr1.getAddressLine3()); // changed
		assertEquals("ZPOx", addr1.getAddressLine4()); // changed
		
		assertEquals("1384315432841354384131514354422663184", addr1.getTel()); // changed
		assertEquals("1384315432841354384131514354422663185", addr1.getFax()); // changed
		assertEquals("kork@surprise.worldgov", addr1.getEmail()); // changed
	}

	// ----------------------------

	private void test03_3_check_memory(GCshWritableAddress addr1) throws Exception {
		assertEquals("Come-on baby, let's do the Oliver Twist!", addr1.getAddressName()); // changed
		
		assertEquals("sdfg", addr1.getAddressLine1()); // changed
		assertEquals("aödslfkja", addr1.getAddressLine2()); // changed
		assertEquals("aölkdjf", addr1.getAddressLine3()); // changed
		assertEquals("asdöfk", addr1.getAddressLine4()); // changed
		
		assertEquals("+44 - 12 - 345 678 9", addr1.getTel()); // unchanged
		assertEquals("", addr1.getFax()); // unchanged
		assertEquals("otwist@myco.com", addr1.getEmail()); // unchanged
	}

	private void test03_3_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		GnuCashEmployee empl11 = gcshOutFile.getEmployeeByID(EMPL_1_ID);
		GCshAddress addr1 = empl11.getAddress();
		assertNotEquals(null, addr1);

		// ---
		
		assertEquals("Come-on baby, let's do the Oliver Twist!", addr1.getAddressName()); // changed
		
		assertEquals("sdfg", addr1.getAddressLine1()); // changed
		assertEquals("aödslfkja", addr1.getAddressLine2()); // changed
		assertEquals("aölkdjf", addr1.getAddressLine3()); // changed
		assertEquals("asdöfk", addr1.getAddressLine4()); // changed
		
		assertEquals("+44 - 12 - 345 678 9", addr1.getTel()); // unchanged
		assertEquals("", addr1.getFax()); // unchanged
		assertEquals("otwist@myco.com", addr1.getEmail()); // unchanged
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	// ::TODO

}
