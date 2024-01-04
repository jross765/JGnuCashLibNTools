package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashEmployeeImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.write.GnucashWritableEmployee;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableEmployeeImpl {
    private static final GCshID EMPL_1_ID = TestGnucashEmployeeImpl.EMPL_1_ID;

    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl gcshOutFile = null;

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
	return new JUnit4TestAdapter(TestGnucashWritableEmployeeImpl.class);
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
	    gcshInFile = new GnucashWritableFileImpl(gcshInFileStream);
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
    // Cf. TestGnucashEmployeeImpl.test01_1/02_2
    //
    // Check whether the GnucashWritableEmployee objects returned by
    // GnucashWritableFileImpl.getWritableEmployeeByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getEmployeeByID().

    @Test
    public void test01_1_1() throws Exception {
	GnucashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
	assertNotEquals(null, empl);

	assertEquals(EMPL_1_ID, empl.getID());
	assertEquals("000001", empl.getNumber());
	assertEquals("otwist", empl.getUserName());
	assertEquals("Oliver Twist", empl.getAddress().getAddressName());
    }

    @Test
    public void test01_1_2() throws Exception {
	GnucashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
	assertNotEquals(null, empl);

	assertEquals(1, ((GnucashWritableEmployeeImpl) empl).getNofOpenVouchers());
	assertEquals(empl.getNofOpenVouchers(), ((GnucashWritableEmployeeImpl) empl).getNofOpenVouchers()); // not
													    // trivial

	assertEquals(0, ((GnucashWritableEmployeeImpl) empl).getPaidVouchers().size());
	assertEquals(empl.getPaidVouchers().size(), ((GnucashWritableEmployeeImpl) empl).getPaidVouchers().size()); // not
														    // trivial

//    vchList = (ArrayList<GnucashEmployeeVoucher>) empl.getPaidVouchers_direct();
//    Collections.sort(vchList);
//    assertEquals("xxx", 
//                 ((GnucashVendorBill) vchList.toArray()[0]).getID() );

	assertEquals(1, ((GnucashWritableEmployeeImpl) empl).getUnpaidVouchers().size());
	assertEquals(empl.getUnpaidVouchers().size(), ((GnucashWritableEmployeeImpl) empl).getUnpaidVouchers().size());

	Collection<GnucashEmployeeVoucher> vchList1 = empl.getUnpaidVouchers();
	Collections.sort((ArrayList<GnucashEmployeeVoucher>) vchList1);
	assertEquals("8de4467c17e04bb2895fb68cc07fc4df",
		((GnucashEmployeeVoucher) vchList1.toArray()[0]).getID().toString());
	Collection<GnucashWritableEmployeeVoucher> vchList2 = ((GnucashWritableEmployeeImpl) empl)
		.getUnpaidWritableVouchers();
	Collections.sort((ArrayList<GnucashWritableEmployeeVoucher>) vchList2);
	assertEquals("8de4467c17e04bb2895fb68cc07fc4df",
		((GnucashWritableEmployeeVoucher) vchList2.toArray()[0]).getID().toString());
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnucashWritableEmployee objects returned by
    // can actually be modified -- both in memory and persisted in file.

    @Test
    public void test02_1() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

	GnucashWritableEmployee empl = gcshInFile.getWritableEmployeeByID(EMPL_1_ID);
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
	// System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '"
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

    private void test02_1_check_memory(GnucashWritableEmployee empl) throws Exception {
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

	assertEquals(EMPL_1_ID, empl.getID()); // unchanged
	assertEquals("JOEDALTON01", empl.getNumber()); // changed
	assertEquals("jdalton", empl.getUserName()); // changed
	assertEquals("Joe Dalon Sr.", empl.getAddress().getAddressName()); // changed
    }

    private void test02_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnucashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

	GnucashEmployee empl = gcshOutFile.getEmployeeByID(EMPL_1_ID);
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

	GnucashWritableEmployee empl = gcshInFile.createWritableEmployee();
	empl.setNumber(GnucashEmployeeImpl.getNewNumber(empl));
	empl.setUserName("Émilie Chauchoin");

	// ----------------------------
	// Check whether the object can has actually be created
	// (in memory, not in the file yet).

	test03_1_1_check_memory(empl);

	// ----------------------------
	// Now, check whether the created object can be written to the
	// output file, then re-read from it, and whether is is what
	// we expect it is.

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '"
	// + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			  // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

	test03_1_1_check_persisted(outFile);
    }

    private void test03_1_1_check_memory(GnucashWritableEmployee empl) throws Exception {
	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

	newID = empl.getID();
	assertEquals("Émilie Chauchoin", empl.getUserName());
    }

    private void test03_1_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnucashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL + 1, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));

	GnucashEmployee empl = gcshOutFile.getEmployeeByID(newID);
	assertNotEquals(null, empl);

	assertEquals(newID, empl.getID());
	assertEquals("Émilie Chauchoin", empl.getUserName());
    }

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    @Test
    public void test03_2_1() throws Exception {
	GnucashWritableEmployee empl = gcshInFile.createWritableEmployee();
	empl.setNumber(GnucashEmployeeImpl.getNewNumber(empl));
	empl.setUserName("Émilie Chauchoin");

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnucashWritableEmployeeImpl.test01_1: '"
	// + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			  // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

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
	assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) lastNode;
	assertEquals("Émilie Chauchoin", elt.getElementsByTagName("employee:username").item(0).getTextContent());
	assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());
    }

    // -----------------------------------------------------------------

    @Test
    public void test03_2_4() throws Exception {
	GnucashWritableEmployee empl1 = gcshInFile.createWritableEmployee();
	empl1.setNumber(GnucashEmployeeImpl.getNewNumber(empl1));
	empl1.setUserName("Émilie Chauchoin");

	GnucashWritableEmployee empl2 = gcshInFile.createWritableEmployee();
	empl2.setNumber(GnucashEmployeeImpl.getNewNumber(empl2));
	empl2.setUserName("Shirley Beaty");

	GnucashWritableEmployee empl3 = gcshInFile.createWritableEmployee();
	empl3.setNumber(GnucashEmployeeImpl.getNewNumber(empl3));
	empl3.setUserName("Stefani Germanotta");

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableEmployeeImpl.test02_1: '" + outFile.getPath() + "'");
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
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) node;
	assertEquals("Émilie Chauchoin", elt.getElementsByTagName("employee:username").item(0).getTextContent());
	assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());

	node = nList.item(nList.getLength() - 2);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Shirley Beaty", elt.getElementsByTagName("employee:username").item(0).getTextContent());
	assertEquals("000003", elt.getElementsByTagName("employee:id").item(0).getTextContent());

	node = nList.item(nList.getLength() - 1);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Stefani Germanotta", elt.getElementsByTagName("employee:username").item(0).getTextContent());
	assertEquals("000004", elt.getElementsByTagName("employee:id").item(0).getTextContent());
    }

}
