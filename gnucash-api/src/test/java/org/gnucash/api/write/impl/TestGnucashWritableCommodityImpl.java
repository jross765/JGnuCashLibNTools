package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.api.basetypes.complex.GCshCmdtyID_MIC;
import org.gnucash.api.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashCommodityImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnucashWritableCommodity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableCommodityImpl {
    public static final GCshCmdtyCurrNameSpace.Exchange CMDTY_1_EXCH = TestGnucashCommodityImpl.CMDTY_1_EXCH;
    public static final String CMDTY_1_ID = TestGnucashCommodityImpl.CMDTY_1_ID;
    public static final String CMDTY_1_ISIN = TestGnucashCommodityImpl.CMDTY_1_ISIN;

    public static final GCshCmdtyCurrNameSpace.Exchange CMDTY_2_EXCH = TestGnucashCommodityImpl.CMDTY_2_EXCH;
    public static final String CMDTY_2_ID = TestGnucashCommodityImpl.CMDTY_2_ID;
    public static final String CMDTY_2_ISIN = TestGnucashCommodityImpl.CMDTY_1_ISIN;

    public static final GCshCmdtyCurrNameSpace.SecIdType CMDTY_3_SECIDTYPE = GCshCmdtyCurrNameSpace.SecIdType.ISIN;
    public static final String CMDTY_3_ID = TestGnucashCommodityImpl.CMDTY_3_ID;
    public static final String CMDTY_3_ISIN = TestGnucashCommodityImpl.CMDTY_1_ISIN;

    // ---------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl gcshOutFile = null;

    private GCshFileStats gcshInFileStats = null;
    private GCshFileStats gcshOutFileStats = null;

    private GCshCmdtyCurrID newID = new GCshCmdtyCurrID("POOPOO", "BEST");

    private GCshCmdtyCurrID cmdtyCurrID1 = null;
//    private GCshCmdtyCurrID cmdtyCurrID2 = null;
//    private GCshCmdtyCurrID cmdtyCurrID3 = null;

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
	return new JUnit4TestAdapter(TestGnucashWritableCommodityImpl.class);
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

	// ---

	cmdtyCurrID1 = new GCshCmdtyID_Exchange(CMDTY_1_EXCH, CMDTY_1_ID);
//    cmdtyCurrID2 = new GCshCmdtyID_Exchange(CMDTY_2_EXCH, CMDTY_2_ID);
//    cmdtyCurrID3 = new GCshCmdtyID_SecIdType(CMDTY_3_SECIDTYPE, CMDTY_3_ID);
    }

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestGnucashCommodityImpl.test01_1/01_4
    //
    // Check whether the GnucashWritableCustomer objects returned by
    // GnucashWritableFileImpl.getWritableCommodityByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getCommodityByID().

    @Test
    public void test01_1() throws Exception {
	GnucashWritableCommodity cmdty = gcshInFile.getWritableCommodityByQualifID(CMDTY_1_EXCH, CMDTY_1_ID);
	assertNotEquals(null, cmdty);

	assertEquals(cmdtyCurrID1.toString(), cmdty.getQualifID().toString());
	// *Not* equal because of class
	assertNotEquals(cmdtyCurrID1, cmdty.getQualifID());
	// ::TODO: Convert to CommodityID_Exchange, then it should be equal
//    assertEquals(cmdtyCurrID1, cmdty.getQualifID()); // not trivial!
	assertEquals(CMDTY_1_ISIN, cmdty.getXCode());
	assertEquals("Mercedes-Benz Group AG", cmdty.getName());
    }

    @Test
    public void test01_2() throws Exception {
	Collection<GnucashWritableCommodity> cmdtyList = gcshInFile.getWritableCommoditiesByName("mercedes");
	assertNotEquals(null, cmdtyList);
	assertEquals(1, cmdtyList.size());

	assertEquals(cmdtyCurrID1.toString(), ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID().toString());
	// *Not* equal because of class
	assertNotEquals(cmdtyCurrID1, ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID());
	// ::TODO: Convert to CommodityID_Exchange, then it should be equal
//    assertEquals(cmdtyCurrID1, 
//	        ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID()); // not trivial!
	assertEquals(CMDTY_1_ISIN, ((GnucashCommodity) cmdtyList.toArray()[0]).getXCode());
	assertEquals("Mercedes-Benz Group AG", ((GnucashCommodity) cmdtyList.toArray()[0]).getName());

	cmdtyList = gcshInFile.getWritableCommoditiesByName("BENZ");
	assertNotEquals(null, cmdtyList);
	assertEquals(1, cmdtyList.size());
	// *Not* equal because of class
	assertNotEquals(cmdtyCurrID1, ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID());
	// ::TODO: Convert to CommodityID_Exchange, then it should be equal
//    assertEquals(cmdtyCurrID1, 
//	         ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID());

	cmdtyList = gcshInFile.getWritableCommoditiesByName(" MeRceDeS-bEnZ  ");
	assertNotEquals(null, cmdtyList);
	assertEquals(1, cmdtyList.size());
	assertEquals(cmdtyCurrID1.toString(), ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID().toString());
	// *Not* equal because of class
	assertNotEquals(cmdtyCurrID1, ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID());
	// ::TODO: Convert to CommodityID_Exchange, then it should be equal
//    assertEquals(cmdtyCurrID1, 
//	         ((GnucashCommodity) cmdtyList.toArray()[0]).getQualifID()); // not trivial!
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnucashWritableCommodity objects returned by
    // can actually be modified -- both in memory and persisted in file.

    // ::TODO

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

    @Test
    public void test03_1_1() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW)); // sic + 1 for template
	// ::CHECK ???
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL - 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER)); // sic, NOT + 1 yet
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));

	GnucashWritableCommodity cmdty = gcshInFile.createWritableCommodity();
	cmdty.setQualifID(newID);
	cmdty.setName("Best Corp Ever");

	// ----------------------------
	// Check whether the object can has actually be created
	// (in memory, not in the file yet).

	test03_1_1_check_memory(cmdty);

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

    private void test03_1_1_check_memory(GnucashWritableCommodity cmdty) throws Exception {
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1 + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW)); // sic + 1 for template
	// ::CHECK ???
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER)); // sic, NOT + 1 yet
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));

	assertEquals(newID.toString(), cmdty.getQualifID().toString());
	assertEquals("Best Corp Ever", cmdty.getName());
    }

    private void test03_1_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnucashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1 + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW)); // sic + 1 for template
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER)); // dto.
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));

	GnucashCommodity cmdty = gcshOutFile.getCommodityByQualifID(newID);
	assertNotEquals(null, cmdty);

	assertEquals(newID.toString(), cmdty.getQualifID().toString());
	assertEquals("Best Corp Ever", cmdty.getName());
    }

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    @Test
    public void test03_2_1() throws Exception {
	GnucashWritableCommodity cmdty = gcshInFile.createWritableCommodity();
	cmdty.setQualifID(new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.NASDAQ, "SCAM"));
	cmdty.setName("Scam and Screw Corp.");

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableCommodityImpl.test01_1: '" + outFile.getPath() + "'");
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

	NodeList nList = document.getElementsByTagName("gnc:commodity");
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1 + 1, nList.getLength()); // <-- CAUTION: includes
										// "template:template"

	// Last (new) node
	Node lastNode = nList.item(nList.getLength() - 1);
	assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) lastNode;
	assertEquals("Scam and Screw Corp.", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
	assertEquals(GCshCmdtyCurrNameSpace.Exchange.NASDAQ.toString(),
		elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
	assertEquals("SCAM", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
    }

    // -----------------------------------------------------------------

    @Test
    public void test03_2_2() throws Exception {
	GnucashWritableCommodity cmdty1 = gcshInFile.createWritableCommodity();
	cmdty1.setQualifID(new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.NASDAQ, "SCAM"));
	cmdty1.setName("Scam and Screw Corp.");
	cmdty1.setXCode("US0123456789");

	GnucashWritableCommodity cmdty2 = gcshInFile.createWritableCommodity();
	cmdty2.setQualifID(new GCshCmdtyID_MIC(GCshCmdtyCurrNameSpace.MIC.XBRU, "CHOC"));
	cmdty2.setName("Chocolaterie de la Grande Place");
	cmdty2.setXCode("BE0123456789");

	GnucashWritableCommodity cmdty3 = gcshInFile.createWritableCommodity();
	cmdty3.setQualifID(new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "FOUS"));
	cmdty3.setName("Ils sont fous ces dingos!");
	cmdty3.setXCode("FR0123456789");

	GnucashWritableCommodity cmdty4 = gcshInFile.createWritableCommodity();
	cmdty4.setQualifID(new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, "GB10000A2222"));
	cmdty4.setName("Ye Ole National British Trade Company Ltd.");
	cmdty4.setXCode("GB10000A2222"); // sic, has to be set redundantly

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnucashWritableCommodityImpl.test02_1: '"
	// + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			          // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

	test03_2_2_check(outFile);
    }

    private void test03_2_2_check(File outFile) throws Exception {
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

	NodeList nList = document.getElementsByTagName("gnc:commodity");
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1 + 4, nList.getLength()); // <-- CAUTION: includes
										// "template:template"

	// Last three nodes (the new ones)
	Node node = nList.item(nList.getLength() - 4);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	Element elt = (Element) node;
	assertEquals("Scam and Screw Corp.", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
	assertEquals(GCshCmdtyCurrNameSpace.Exchange.NASDAQ.toString(),
		elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
	assertEquals("SCAM", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
	assertEquals("US0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

	node = nList.item(nList.getLength() - 3);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Chocolaterie de la Grande Place",
		elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
	assertEquals(GCshCmdtyCurrNameSpace.MIC.XBRU.toString(),
		elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
	assertEquals("CHOC", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
	assertEquals("BE0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

	node = nList.item(nList.getLength() - 2);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Ils sont fous ces dingos!", elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
	assertEquals(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString(),
		elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
	assertEquals("FOUS", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
	assertEquals("FR0123456789", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());

	node = nList.item(nList.getLength() - 1);
	assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
	elt = (Element) node;
	assertEquals("Ye Ole National British Trade Company Ltd.",
		elt.getElementsByTagName("cmdty:name").item(0).getTextContent());
	assertEquals(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString(),
		elt.getElementsByTagName("cmdty:space").item(0).getTextContent());
	assertEquals("GB10000A2222", elt.getElementsByTagName("cmdty:id").item(0).getTextContent());
	assertEquals("GB10000A2222", elt.getElementsByTagName("cmdty:xcode").item(0).getTextContent());
    }

}
