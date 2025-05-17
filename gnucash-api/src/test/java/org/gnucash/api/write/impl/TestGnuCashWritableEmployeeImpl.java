package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashEmployeeImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableEmployeeImpl {
	private static final GCshID EMPL_1_ID = TestGnuCashEmployeeImpl.EMPL_1_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshID newID;

	// https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
	@SuppressWarnings("exports")
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashWritableEmployeeImpl.class);
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
	// Cf. TestGnuCashEmployeeImpl.test01_1/02_2
	//
	// Check whether the GnuCashWritableEmployee objects returned by
	// GnuCashWritableFileImpl.getWritableEmployeeByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getEmployeeByID().

	@Test
	public void test01_1_1() throws Exception {
		GnuCashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(EMPL_1_ID, empl.getID());
		assertEquals("000001", empl.getNumber());
		assertEquals("otwist", empl.getUserName());
		assertEquals("Oliver Twist", empl.getAddress().getAddressName());
	}

	@Test
	public void test01_1_2() throws Exception {
		GnuCashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(1, ((GnuCashWritableEmployeeImpl) empl).getNofOpenVouchers());
		assertEquals(empl.getNofOpenVouchers(), ((GnuCashWritableEmployeeImpl) empl).getNofOpenVouchers()); // not
		// trivial

		assertEquals(0, ((GnuCashWritableEmployeeImpl) empl).getPaidVouchers().size());
		assertEquals(empl.getPaidVouchers().size(), ((GnuCashWritableEmployeeImpl) empl).getPaidVouchers().size()); // not
		// trivial

		//    vchList = (ArrayList<GnuCashEmployeeVoucher>) empl.getPaidVouchers_direct();
		//    Collections.sort(vchList);
		//    assertEquals("xxx", 
		//                 ((GnuCashVendorBill) vchList.toArray()[0]).getID() );

		assertEquals(1, ((GnuCashWritableEmployeeImpl) empl).getUnpaidVouchers().size());
		assertEquals(empl.getUnpaidVouchers().size(), ((GnuCashWritableEmployeeImpl) empl).getUnpaidVouchers().size());

		List<GnuCashEmployeeVoucher> vchList1 = empl.getUnpaidVouchers();
		Collections.sort(vchList1);
		assertEquals("8de4467c17e04bb2895fb68cc07fc4df",
				((GnuCashEmployeeVoucher) vchList1.toArray()[0]).getID().toString());
		List<GnuCashWritableEmployeeVoucher> vchList2 = ((GnuCashWritableEmployeeImpl) empl)
				.getUnpaidWritableVouchers();
		Collections.sort(vchList2);
		assertEquals("8de4467c17e04bb2895fb68cc07fc4df",
				((GnuCashWritableEmployeeVoucher) vchList2.toArray()[0]).getID().toString());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableEmployee objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		GnuCashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(EMPL_1_ID, empl.getID());

		// ----------------------------
		// Modify the object

		empl.setNumber("JOEDALTON01");
		empl.setUserName("jdalton");
		empl.getWritableAddress().setAddressName("Joe Dalon Sr.");

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(empl);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableEmployeeImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test02_1_check_persisted(outFile);
	}

	@Test
	public void test02_2() throws Exception {
		// ::TODO
	}

	private void test02_1_check_memory(GnuCashWritableEmployee empl) throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		assertEquals(EMPL_1_ID, empl.getID()); // unchanged
		assertEquals("JOEDALTON01", empl.getNumber()); // changed
		assertEquals("jdalton", empl.getUserName()); // changed
		assertEquals("Joe Dalon Sr.", empl.getAddress().getAddressName()); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_EMPL, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		GnuCashEmployee empl = gcshOutFile.getEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(EMPL_1_ID, empl.getID()); // unchanged
		assertEquals("JOEDALTON01", empl.getNumber()); // changed
		assertEquals("jdalton", empl.getUserName()); // changed
		assertEquals("Joe Dalon Sr.", empl.getAddress().getAddressName()); // changed
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		GnuCashWritableEmployee empl = gcshInFile.createWritableEmployee("Émilie Chauchoin");
		empl.setNumber(GnuCashEmployeeImpl.getNewNumber(empl));

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_1_check_memory(empl);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableEmployeeImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_1_check_persisted(outFile);
	}

	private void test03_1_1_check_memory(GnuCashWritableEmployee empl) throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		newID = empl.getID();
		assertEquals("Émilie Chauchoin", empl.getUserName());
	}

	private void test03_1_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		GnuCashEmployee empl = gcshOutFile.getEmployeeByID(newID);
		assertNotEquals(null, empl);

		assertEquals(newID, empl.getID());
		assertEquals("Émilie Chauchoin", empl.getUserName());
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	@Test
	public void test03_2_1() throws Exception {
		GnuCashWritableEmployee empl = gcshInFile.createWritableEmployee("Émilie Chauchoin");
		empl.setNumber(GnuCashEmployeeImpl.getNewNumber(empl));

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableEmployeeImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_2_1_check_1_valid(outFile);
		test03_2_1_check(outFile);
	}

	// -----------------------------------------------------------------

	//  @Test
	//  public void test03_2_2() throws Exception
	//  {
	//      assertNotEquals(null, outFileGlob);
	//      assertEquals(true, outFileGlob.exists());
	//
	//      // Check if generated document is valid
	//      // ::TODO: in fact, not even the input document is.
	//      // Build document
	//      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	//      DocumentBuilder builder = factory.newDocumentBuilder(); 
	//      Document document = builder.parse(outFileGlob);
	//      System.err.println("xxxx XML parsed");
	//
	//      // https://howtodoinjava.com/java/xml/read-xml-dom-parser-example/
	//      Schema schema = null;
	//      String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
	//      SchemaFactory factory1 = SchemaFactory.newInstance(language);
	//      schema = factory1.newSchema(outFileGlob);
	//
	//      Validator validator = schema.newValidator();
	//      DOMResult validResult = null; 
	//      validator.validate(new DOMSource(document), validResult);
	//      System.out.println("yyy: " + validResult);
	//      // assertEquals(validResult);
	//  }

	// Sort of "soft" variant of above function
	// CAUTION: Not platform-independent!
	// Tool "xmllint" must be installed and in path
	private void test03_2_1_check_1_valid(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Check if generated document is valid
		// ProcessBuilder bld = new ProcessBuilder("xmllint", outFile.getAbsolutePath() );
		ProcessBuilder bld = new ProcessBuilder("xmlstarlet", "val", outFile.getAbsolutePath() );
		Process prc = bld.start();

		if ( prc.waitFor() == 0 ) {
			assertEquals(0, 0);
		} else {
			assertEquals(0, 1);
		}
	}

	private void test03_2_1_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
		//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
		//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("gnc:GncEmployee");
		assertEquals(ConstTest.Stats.NOF_EMPL + 1, nList.getLength());

		// Last (new) node
		Node lastNode = nList.item(nList.getLength() - 1);
		assertEquals(Node.ELEMENT_NODE, lastNode.getNodeType());
		Element elt = (Element) lastNode;
		assertEquals("Émilie Chauchoin", elt.getElementsByTagName("employee:username").item(0).getTextContent());
		assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());
	}

	// -----------------------------------------------------------------

	@Test
	public void test03_2_4() throws Exception {
		GnuCashWritableEmployee empl1 = gcshInFile.createWritableEmployee("Émilie Chauchoin");
		empl1.setNumber(GnuCashEmployeeImpl.getNewNumber(empl1));

		GnuCashWritableEmployee empl2 = gcshInFile.createWritableEmployee("Shirley Beaty");
		empl2.setNumber(GnuCashEmployeeImpl.getNewNumber(empl2));

		GnuCashWritableEmployee empl3 = gcshInFile.createWritableEmployee("Stefani Germanotta");
		empl3.setNumber(GnuCashEmployeeImpl.getNewNumber(empl3));

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		//      System.err.println("Outfile for TestGnuCashWritableEmployeeImpl.test02_1: '" + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_2_4_check(outFile);
	}

	private void test03_2_4_check(File outFile) throws Exception {
		assertNotEquals(null, outFile);
		assertEquals(true, outFile.exists());

		// Build document
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(outFile);
		//      System.err.println("xxxx XML parsed");

		// Normalize the XML structure
		document.getDocumentElement().normalize();
		//      System.err.println("xxxx XML normalized");

		NodeList nList = document.getElementsByTagName("gnc:GncEmployee");
		assertEquals(ConstTest.Stats.NOF_EMPL + 3, nList.getLength());

		// Last three nodes (the new ones)
		Node node = nList.item(nList.getLength() - 3);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		Element elt = (Element) node;
		assertEquals("Émilie Chauchoin", elt.getElementsByTagName("employee:username").item(0).getTextContent());
		assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());

		node = nList.item(nList.getLength() - 2);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		elt = (Element) node;
		assertEquals("Shirley Beaty", elt.getElementsByTagName("employee:username").item(0).getTextContent());
		assertEquals("000003", elt.getElementsByTagName("employee:id").item(0).getTextContent());

		node = nList.item(nList.getLength() - 1);
		assertEquals(Node.ELEMENT_NODE, node.getNodeType());
		elt = (Element) node;
		assertEquals("Stefani Germanotta", elt.getElementsByTagName("employee:username").item(0).getTextContent());
		assertEquals("000004", elt.getElementsByTagName("employee:id").item(0).getTextContent());
	}

	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------

	@Test
	public void test04_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER)); // sic, because not persisted yet
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		GnuCashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);
		assertEquals(EMPL_1_ID, empl.getID());

		// ----------------------------
		// Delete the object

		gcshInFile.removeEmployee(empl);

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test04_1_check_memory(empl);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableEmployeeImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test04_1_check_persisted(outFile);
	}
	
	// ---------------------------------------------------------------

	private void test04_1_check_memory(GnuCashWritableEmployee empl) throws Exception {
		assertEquals(ConstTest.Stats.NOF_EMPL - 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER)); // sic, because not persisted yet
		assertEquals(ConstTest.Stats.NOF_EMPL - 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		// CAUTION / ::TODO
		// Old Object still exists and is unchanged
		// Exception: no splits any more
		// Don't know what to do about this oddity right now,
		// but it needs to be addressed at some point.
		assertEquals("000001", empl.getNumber());
		assertEquals("otwist", empl.getUserName());
		assertEquals("Oliver Twist", empl.getAddress().getAddressName());
		
		// However, the employee cannot newly be instantiated any more,
		// just as you would expect.
		try {
			GnuCashWritableEmployee emplNow1 = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		// Same for a non non-writable instance. 
		// However, due to design asymmetry, no exception is thrown here,
		// but the method just returns null.
		GnuCashEmployee emplNow2 = gcshInFile.getEmployeeByID(EMPL_1_ID);
		assertEquals(null, emplNow2);
	}

	private void test04_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_EMPL - 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_EMPL - 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_EMPL - 1, gcshOutFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

		// The transaction does not exist any more, just as you would expect.
		// However, no exception is thrown, as opposed to test04_1_check_memory()
		GnuCashEmployee empl = gcshOutFile.getEmployeeByID(EMPL_1_ID);
		assertEquals(null, empl); // sic
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------
	
	// ::EMPTY

}
