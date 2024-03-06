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
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.read.impl.TestGnuCashVendorImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableVendorImpl {
    public static final GCshID VEND_1_ID = TestGnuCashVendorImpl.VEND_1_ID;
//  public static final GCshID VEND_2_ID = TestGnuCashVendorImpl.VEND_2_ID;
//  public static final GCshID VEND_3_ID = TestGnuCashVendorImpl.VEND_3_ID;

//  private static final GCshID TAXTABLE_UK_1_ID   = TestGCshTaxTableImpl.TAXTABLE_UK_1_ID;
//
    private static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
//  private static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
//  private static final GCshID BLLTRM_3_ID = TestGCshBillTermsImpl.BLLTRM_3_ID;

    // -----------------------------------------------------------------

    private GnuCashWritableFileImpl gcshInFile = null;
    private GnuCashFileImpl gcshOutFile = null;

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
	return new JUnit4TestAdapter(TestGnuCashWritableVendorImpl.class);
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
    // Cf. TestGnuCashVendorImpl.test01_1/02_1
    //
    // Check whether the GnuCashWritableVendor objects returned by
    // GnuCashWritableFileImpl.getWritableVendorByID() are actually
    // complete (as complete as returned be GnuCashFileImpl.getVendorByID().

    @Test
    public void test01_1_1() throws Exception {
	GnuCashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
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
	GnuCashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
	assertNotEquals(null, vend);

	assertEquals(1, ((GnuCashWritableVendorImpl) vend).getNofOpenBills());
	assertEquals(vend.getNofOpenBills(), ((GnuCashWritableVendorImpl) vend).getNofOpenBills()); // not trivial

	assertEquals(1, ((GnuCashWritableVendorImpl) vend).getPaidWritableBills_direct().size());
	assertEquals(vend.getPaidBills_direct().size(),
		((GnuCashWritableVendorImpl) vend).getPaidWritableBills_direct().size()); // not trivial

	List<GnuCashVendorBill> bllList1 = vend.getPaidBills_direct();
	Collections.sort(bllList1);
	assertEquals("286fc2651a7848038a23bb7d065c8b67",
		((GnuCashVendorBill) bllList1.toArray()[0]).getID().toString());
	List<GnuCashWritableVendorBill> bllList2 = ((GnuCashWritableVendorImpl) vend).getPaidWritableBills_direct();
	Collections.sort(bllList2);
	assertEquals("286fc2651a7848038a23bb7d065c8b67",
		((GnuCashWritableVendorBill) bllList2.toArray()[0]).getID().toString());

	assertEquals(1, ((GnuCashWritableVendorImpl) vend).getUnpaidWritableBills_direct().size());
	assertEquals(vend.getUnpaidBills_direct().size(),
		((GnuCashWritableVendorImpl) vend).getUnpaidWritableBills_direct().size()); // not trivial

	bllList1 = vend.getUnpaidBills_direct();
	Collections.sort(bllList1);
	assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4",
		((GnuCashVendorBill) bllList1.toArray()[0]).getID().toString());
	bllList2 = ((GnuCashWritableVendorImpl) vend).getUnpaidWritableBills_direct();
	Collections.sort(bllList2);
	assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4",
		((GnuCashWritableVendorBill) bllList2.toArray()[0]).getID().toString());
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnuCashWritableVendor objects returned by
    // can actually be modified -- both in memory and persisted in file.

    @Test
    public void test02_1() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnuCashWritableVendor vend = gcshInFile.getWritableVendorByID(VEND_1_ID);
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
	// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
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

    private void test02_1_check_memory(GnuCashWritableVendor vend) throws Exception {
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
	gcshOutFile = new GnuCashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnuCashVendor vend = gcshOutFile.getVendorByID(VEND_1_ID);
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

	GnuCashWritableVendor vend = gcshInFile.createWritableVendor("Norma Jean Baker");
	vend.setNumber(GnuCashVendorImpl.getNewNumber(vend));

	// ----------------------------
	// Check whether the object can has actually be created
	// (in memory, not in the file yet).

	test03_1_1_check_memory(vend);

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

	test03_1_1_check_persisted(outFile);
    }

    private void test03_1_1_check_memory(GnuCashWritableVendor vend) throws Exception {
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	newID = vend.getID();
	assertEquals("Norma Jean Baker", vend.getName());
    }

    private void test03_1_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnuCashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND + 1, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));

	GnuCashVendor vend = gcshOutFile.getVendorByID(newID);
	assertNotEquals(null, vend);

	assertEquals(newID, vend.getID());
	assertEquals("Norma Jean Baker", vend.getName());
    }

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    @Test
    public void test03_2_1() throws Exception {
	GnuCashWritableVendor vend = gcshInFile.createWritableVendor("Norma Jean Baker");
	vend.setNumber(GnuCashVendorImpl.getNewNumber(vend));

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnuCashWritableVendorImpl.test01_1: '" +
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
	GnuCashWritableVendor vend1 = gcshInFile.createWritableVendor("Norma Jean Baker");
	vend1.setNumber(GnuCashVendorImpl.getNewNumber(vend1));

	GnuCashWritableVendor vend2 = gcshInFile.createWritableVendor("Madonna Louise Ciccone");
	vend2.setNumber(GnuCashVendorImpl.getNewNumber(vend2));

	GnuCashWritableVendor vend3 = gcshInFile.createWritableVendor("Rowan Atkinson");
	vend3.setNumber(GnuCashVendorImpl.getNewNumber(vend3));

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnuCashWritableVendorImpl.test02_1: '" + outFile.getPath() + "'");
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
