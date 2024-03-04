package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.read.impl.TestGnucashVendorImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.write.GnucashWritableVendor;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableVendorImpl {
    public static final GCshID VEND_1_ID = TestGnucashVendorImpl.VEND_1_ID;
//  public static final GCshID VEND_2_ID = TestGnucashVendorImpl.VEND_2_ID;
//  public static final GCshID VEND_3_ID = TestGnucashVendorImpl.VEND_3_ID;

//  private static final GCshID TAXTABLE_UK_1_ID   = TestGCshTaxTableImpl.TAXTABLE_UK_1_ID;
//
    private static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
//  private static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
//  private static final GCshID BLLTRM_3_ID = TestGCshBillTermsImpl.BLLTRM_3_ID;

    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl gcshOutFile = null;

    private GCshFileStats gcshInFileStats = null;
    private GCshFileStats gcshOutFileStats = null;

    private GCshID newID = null;

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
	return new JUnit4TestAdapter(TestGnucashWritableVendorImpl.class);
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
    // Cf. TestGnucashVendorImpl.test01_1/02_1
    //
    // Check whether the GnucashWritableVendor objects returned by
    // GnucashWritableFileImpl.getWritableVendorByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getVendorByID().

    @Test
    public void test01_1_1() throws Exception {
	GnucashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
	assertNotEquals(null, vend);

	assertEquals(VEND_1_ID, vend.getID());
	assertEquals("000001", vend.getNumber());
	assertEquals("Lieferfanto AG", vend.getName());

	assertEquals(null, vend.getTaxTableID());

	assertEquals(BLLTRM_1_ID, vend.getTermsID());
	assertEquals("sofort", vend.getTerms().getName());
	assertEquals(GCshBillTerms.Type.DAYS, vend.getTerms().getType());
	assertEquals(null, vend.getNotes());
	// etc., cf. class TestGCshBillTermsImpl
    }

    @Test
    public void test01_2_1() throws Exception {
	GnucashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
	assertNotEquals(null, vend);

	assertEquals(1, ((GnucashWritableVendorImpl) vend).getNofOpenBills());
	assertEquals(vend.getNofOpenBills(), ((GnucashWritableVendorImpl) vend).getNofOpenBills()); // not trivial

	assertEquals(1, ((GnucashWritableVendorImpl) vend).getPaidWritableBills_direct().size());
	assertEquals(vend.getPaidBills_direct().size(),
		((GnucashWritableVendorImpl) vend).getPaidWritableBills_direct().size()); // not trivial

	List<GnucashVendorBill> bllList1 = vend.getPaidBills_direct();
	Collections.sort(bllList1);
	assertEquals("286fc2651a7848038a23bb7d065c8b67",
		((GnucashVendorBill) bllList1.toArray()[0]).getID().toString());
	List<GnucashWritableVendorBill> bllList2 = ((GnucashWritableVendorImpl) vend).getPaidWritableBills_direct();
	Collections.sort(bllList2);
	assertEquals("286fc2651a7848038a23bb7d065c8b67",
		((GnucashWritableVendorBill) bllList2.toArray()[0]).getID().toString());

	assertEquals(1, ((GnucashWritableVendorImpl) vend).getUnpaidWritableBills_direct().size());
	assertEquals(vend.getUnpaidBills_direct().size(),
		((GnucashWritableVendorImpl) vend).getUnpaidWritableBills_direct().size()); // not trivial

	bllList1 = vend.getUnpaidBills_direct();
	Collections.sort(bllList1);
	assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4",
		((GnucashVendorBill) bllList1.toArray()[0]).getID().toString());
	bllList2 = ((GnucashWritableVendorImpl) vend).getUnpaidWritableBills_direct();
	Collections.sort(bllList2);
	assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4",
		((GnucashWritableVendorBill) bllList2.toArray()[0]).getID().toString());
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnucashWritableVendor objects returned by
    // can actually be modified -- both in memory and persisted in file.

    @Test
    public void test02_1() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnucashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
	assertNotEquals(null, vend);

	assertEquals(VEND_1_ID, vend.getID());

	// ----------------------------
	// Modify the object

	vend.setNumber("RTP01");
	vend.setName("Rantanplan");
	vend.setNotes("World's most intelligent canine being");

	// ----------------------------
	// Check whether the object can has actually be modified
	// (in memory, not in the file yet).

	test02_1_check_memory(vend);

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

    private void test02_1_check_memory(GnucashWritableVendor vend) throws Exception {
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	assertEquals(VEND_1_ID, vend.getID()); // unchanged
	assertEquals("RTP01", vend.getNumber()); // changed
	assertEquals("Rantanplan", vend.getName()); // changed

	assertEquals(null, vend.getTaxTableID()); // unchanged

	assertEquals(BLLTRM_1_ID, vend.getTermsID()); // unchanged
	assertEquals("sofort", vend.getTerms().getName()); // unchanged
	assertEquals(GCshBillTerms.Type.DAYS, vend.getTerms().getType()); // unchanged
	assertEquals("World's most intelligent canine being", vend.getNotes()); // changed
    }

    private void test02_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnucashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnucashVendor vend = gcshOutFile.getVendorByID(VEND_1_ID);
	assertNotEquals(null, vend);

	assertEquals(VEND_1_ID, vend.getID()); // unchanged
	assertEquals("RTP01", vend.getNumber()); // changed
	assertEquals("Rantanplan", vend.getName()); // changed

	assertEquals(null, vend.getTaxTableID()); // unchanged

	assertEquals(BLLTRM_1_ID, vend.getTermsID()); // unchanged
	assertEquals("sofort", vend.getTerms().getName()); // unchanged
	assertEquals(GCshBillTerms.Type.DAYS, vend.getTerms().getType()); // unchanged
	assertEquals("World's most intelligent canine being", vend.getNotes()); // changed
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

	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnucashWritableVendor vend = gcshInFile.createWritableVendor();
	vend.setNumber(GnucashVendorImpl.getNewNumber(vend));
	vend.setName("Norma Jean Baker");

	// ----------------------------
	// Check whether the object can has actually be created
	// (in memory, not in the file yet).

	test03_1_1_check_memory(vend);

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

    private void test03_1_1_check_memory(GnucashWritableVendor vend) throws Exception {
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	newID = vend.getID();
	assertEquals("Norma Jean Baker", vend.getName());
    }

    private void test03_1_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnucashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnucashVendor vend = gcshOutFile.getVendorByID(newID);
	assertNotEquals(null, vend);

	assertEquals(newID, vend.getID());
	assertEquals("Norma Jean Baker", vend.getName());
    }

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    @Test
    public void test03_2_1() throws Exception {
	GnucashWritableVendor vend = gcshInFile.createWritableVendor();
	vend.setNumber(GnucashVendorImpl.getNewNumber(vend));
	vend.setName("Norma Jean Baker");

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnucashWritableVendorImpl.test01_1: '" +
	// outFile.getPath() + "'");
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

	NodeList nList = document.getElementsByTagName("gnc:GncVendor");
	assertEquals(ConstTest.Stats.NOF_VEND + 1, nList.getLength());

	// Last (new) node
	Node lastNode = nList.item(nList.getLength() - 1);
	assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) lastNode;
	assertEquals("Norma Jean Baker", elt.getElementsByTagName("vendor:name").item(0).getTextContent());
	assertEquals("000004", elt.getElementsByTagName("vendor:id").item(0).getTextContent());
    }

    // -----------------------------------------------------------------

    @Test
    public void test03_2_4() throws Exception {
	GnucashWritableVendor vend1 = gcshInFile.createWritableVendor();
	vend1.setNumber(GnucashVendorImpl.getNewNumber(vend1));
	vend1.setName("Norma Jean Baker");

	GnucashWritableVendor vend2 = gcshInFile.createWritableVendor();
	vend2.setNumber(GnucashVendorImpl.getNewNumber(vend2));
	vend2.setName("Madonna Louise Ciccone");

	GnucashWritableVendor vend3 = gcshInFile.createWritableVendor();
	vend3.setNumber(GnucashVendorImpl.getNewNumber(vend3));
	vend3.setName("Rowan Atkinson");

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableVendorImpl.test02_1: '" + outFile.getPath() + "'");
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

	NodeList nList = document.getElementsByTagName("gnc:GncVendor");
	assertEquals(ConstTest.Stats.NOF_VEND + 3, nList.getLength());

	// Last three nodes (the new ones)
	Node node = nList.item(nList.getLength() - 3);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) node;
	assertEquals("Norma Jean Baker", elt.getElementsByTagName("vendor:name").item(0).getTextContent());
	assertEquals("000004", elt.getElementsByTagName("vendor:id").item(0).getTextContent());

	node = nList.item(nList.getLength() - 2);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Madonna Louise Ciccone", elt.getElementsByTagName("vendor:name").item(0).getTextContent());
	assertEquals("000005", elt.getElementsByTagName("vendor:id").item(0).getTextContent());

	node = nList.item(nList.getLength() - 1);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Rowan Atkinson", elt.getElementsByTagName("vendor:name").item(0).getTextContent());
	assertEquals("000006", elt.getElementsByTagName("vendor:id").item(0).getTextContent());
    }

}
