package org.gnucash.api.write.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashGenerJobImpl;
import org.gnucash.api.read.impl.TestGnucashVendorImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableVendorJobImpl {
    private static final GCshID JOB_2_ID = TestGnucashGenerJobImpl.JOB_2_ID;

    private static final GCshID VEND_1_ID = TestGnucashVendorImpl.VEND_1_ID;
//    private static final GCshID VEND_2_ID = TestGnucashVendorImpl.VEND_2_ID;
//    private static final GCshID VEND_3_ID = TestGnucashVendorImpl.VEND_3_ID;

    // ----------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl gcshOutFile = null;

    private GnucashVendor vend1 = null;

    // ----------------------------

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
	return new JUnit4TestAdapter(TestGnucashWritableVendorJobImpl.class);
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

	// ----------------------------

	vend1 = gcshInFile.getVendorByID(VEND_1_ID);
    }

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestGnucashVendorJobImpl.test01/02
    //
    // Check whether the GnucashWritableVendorJob objects returned by
    // GnucashWritableFileImpl.getWritableGenerJobByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getGenerJobByID().

    @Test
    public void test01_1() throws Exception {
	GnucashWritableVendorJob jobSpec = (GnucashWritableVendorJob) gcshInFile.getWritableGenerJobByID(JOB_2_ID);
	assertNotEquals(null, jobSpec);

	assertTrue(jobSpec instanceof GnucashWritableVendorJob);
	assertEquals(JOB_2_ID, jobSpec.getID());
	assertEquals("000002", jobSpec.getNumber());
	assertEquals("Let's buy help", jobSpec.getName());
    }

    @Test
    public void test01_2() throws Exception {
	GnucashWritableVendorJob jobSpec = (GnucashWritableVendorJob) gcshInFile.getWritableGenerJobByID(JOB_2_ID);
	assertNotEquals(null, jobSpec);

	assertEquals(1, jobSpec.getNofOpenInvoices());
	assertEquals(jobSpec.getNofOpenInvoices(), jobSpec.getNofOpenInvoices());

	assertEquals(0, jobSpec.getPaidInvoices().size());
	assertEquals(jobSpec.getPaidInvoices().size(), jobSpec.getPaidInvoices().size());
	assertEquals(jobSpec.getPaidInvoices().size(),
		((GnucashWritableVendorJobImpl) jobSpec).getPaidWritableInvoices().size());

	assertEquals(1, jobSpec.getUnpaidInvoices().size());
	assertEquals(jobSpec.getUnpaidInvoices().size(), jobSpec.getUnpaidInvoices().size());
	assertEquals(jobSpec.getUnpaidInvoices().size(),
		((GnucashWritableVendorJobImpl) jobSpec).getUnpaidWritableInvoices().size());
    }

    @Test
    public void test01_3() throws Exception {
	GnucashWritableVendorJob jobSpec = (GnucashWritableVendorJob) gcshInFile.getWritableGenerJobByID(JOB_2_ID);
	assertNotEquals(null, jobSpec);

	// Note: That the following three return the same result
	// is *not* trivial (in fact, a serious implementation error was
	// found with this test)
	GCshID vendID = new GCshID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
	assertEquals(vendID, jobSpec.getOwnerID());
	// ::TODO
//    assertEquals(vendID, jobSpec.getVendorID());
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GnucashWritableVendorJob objects returned by
    // can actually be modified -- both in memory and persisted in file.

    // ::TODO

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

    // ::TODO

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    @Test
    public void test03_1() throws Exception {
	GnucashWritableVendorJob job = gcshInFile.createWritableVendorJob(vend1, "J456", "New job for vendor 1");

	assertNotEquals(null, job);
	GCshID newJobID = job.getID();
//      System.out.println("New Job ID (1): " + newJobID);

	assertEquals("J456", job.getNumber());

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableVendorImpl.test01_1: '" + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			          // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

	// test01_2();
	test03_3(outFile, newJobID);
	test03_4(outFile, newJobID);
    }

    private void test03_2(File outFile, String newJobID)
	    throws ParserConfigurationException, SAXException, IOException {
	// ::TODO
	// Check if generated XML file is valid
    }

    private void test03_3(File outFile, GCshID newJobID)
	    throws ParserConfigurationException, SAXException, IOException {
	// assertNotEquals(null, outFileGlob);
	// assertEquals(true, outFileGlob.exists());

	// Build document
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	DocumentBuilder builder = factory.newDocumentBuilder();
	Document document = builder.parse(outFile);
//      System.err.println("xxxx XML parsed");

	// Normalize the XML structure
	document.getDocumentElement().normalize();
//      System.err.println("xxxx XML normalized");

	NodeList nList = document.getElementsByTagName("gnc:GncJob");
	assertEquals(3, nList.getLength());

	// Last (new) node
	Node lastNode = nList.item(nList.getLength() - 1);
	assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);

	Element elt = (Element) lastNode;
	assertEquals("J456", elt.getElementsByTagName("job:id").item(0).getTextContent());
	String locNewJobID = elt.getElementsByTagName("job:guid").item(0).getTextContent();
//      System.out.println("New Job ID (2): " + locNewJobID);
	assertEquals(newJobID.toString(), locNewJobID);
    }

    private void test03_4(File outFile, GCshID newInvcID) throws Exception {
//      assertNotEquals(null, outFileGlob);
//      assertEquals(true, outFileGlob.exists());

	gcshOutFile = new GnucashFileImpl(outFile);

//      System.out.println("New Job ID (3): " + newJobID);
	GnucashGenerJob jobGener = gcshOutFile.getGenerJobByID(newInvcID);
	assertNotEquals(null, jobGener);
	GnucashVendorJob jobSpec = new GnucashVendorJobImpl(jobGener);
	assertNotEquals(null, jobSpec);

	assertEquals(newInvcID, jobGener.getID());
	assertEquals(newInvcID, jobSpec.getID());

	assertEquals(VEND_1_ID, jobGener.getOwnerID());
	assertEquals(VEND_1_ID, jobSpec.getOwnerID());
	assertEquals(VEND_1_ID, jobSpec.getVendorID());

	assertEquals("J456", jobGener.getNumber());
	assertEquals("J456", jobSpec.getNumber());

	assertEquals("New job for vendor 1", jobGener.getName());
	assertEquals("New job for vendor 1", jobSpec.getName());
    }

}
