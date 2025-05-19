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
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashCustomerImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
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

public class TestGnuCashWritableCustomerJobImpl {
	private static final GCshID CUST_JOB_1_ID = TestGnuCashGenerJobImpl.GENER_JOB_1_ID;

	private static final GCshID CUST_1_ID = TestGnuCashCustomerImpl.CUST_1_ID;
	//    private static final GCshID CUST_2_ID = TestGnuCashCustomerImpl.CUST_2_ID;
	//    private static final GCshID CUST_3_ID = TestGnuCashCustomerImpl.CUST_3_ID;

	// ----------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GnuCashCustomer cust1 = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableCustomerJobImpl.class);
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

		// ----------------------------

		cust1 = gcshInFile.getWritableCustomerByID(CUST_1_ID);
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGnuCashCustomerJobImpl.test01/02
	//
	// Check whether the GnuCashWritableCustomerJob objects returned by
	// GnuCashWritableFileImpl.getWritableGenerJobByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getGenerJobByID().

	@Test
	public void test01_1() throws Exception {
		GnuCashWritableCustomerJob jobSpec = (GnuCashWritableCustomerJob) gcshInFile.getWritableGenerJobByID(CUST_JOB_1_ID);
		assertNotEquals(null, jobSpec);

		assertTrue(jobSpec instanceof GnuCashWritableCustomerJob);
		assertEquals(CUST_JOB_1_ID, jobSpec.getID());
		assertEquals("000001", jobSpec.getNumber());
		assertEquals("Do more for others", jobSpec.getName());
	}

	@Test
	public void test01_2() throws Exception {
		GnuCashWritableCustomerJob jobSpec = (GnuCashWritableCustomerJob) gcshInFile.getWritableGenerJobByID(CUST_JOB_1_ID);
		assertNotEquals(null, jobSpec);

		assertEquals(1, jobSpec.getNofOpenInvoices());
		assertEquals(jobSpec.getNofOpenInvoices(), jobSpec.getNofOpenInvoices());

		assertEquals(0, jobSpec.getPaidInvoices().size());
		assertEquals(jobSpec.getPaidInvoices().size(), jobSpec.getPaidInvoices().size());
		assertEquals(jobSpec.getPaidInvoices().size(),
				((GnuCashWritableCustomerJobImpl) jobSpec).getPaidWritableInvoices().size());

		assertEquals(1, jobSpec.getUnpaidInvoices().size());
		assertEquals(jobSpec.getUnpaidInvoices().size(), jobSpec.getUnpaidInvoices().size());
		assertEquals(jobSpec.getUnpaidInvoices().size(),
				((GnuCashWritableCustomerJobImpl) jobSpec).getUnpaidWritableInvoices().size());
	}

	@Test
	public void test01_3() throws Exception {
		GnuCashWritableCustomerJob jobSpec = (GnuCashWritableCustomerJob) gcshInFile.getWritableGenerJobByID(CUST_JOB_1_ID);
		assertNotEquals(null, jobSpec);

		GCshID custID = new GCshID("f44645d2397946bcac90dff68cc03b76");
		assertEquals(custID, jobSpec.getOwnerID());
		// ::TODO
		// assertEquals(custID, jobSpec.getCustomerID());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableCustomerJob objects returned by
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
		GnuCashWritableCustomerJob job = gcshInFile.createWritableCustomerJob(cust1, "J123", "New job for customer 1");

		assertNotEquals(null, job);
		GCshID newJobID = job.getID();
		//      System.out.println("New Job ID (1): " + newJobID);

		assertEquals("J123", job.getNumber());

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		//      System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		// test01_2();
		test03_1_check_1_valid(outFile);
		test03_3(outFile, newJobID);
		test03_4(outFile, newJobID);
	}

	// Sort of "soft" variant of validity check
	// CAUTION: Not platform-independent!
	// Tool "xmllint" must be installed and in path
	private void test03_1_check_1_valid(File outFile) throws Exception {
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
		assertEquals(Node.ELEMENT_NODE, lastNode.getNodeType());

		Element elt = (Element) lastNode;
		assertEquals("J123", elt.getElementsByTagName("job:id").item(0).getTextContent());
		String locNewJobID = elt.getElementsByTagName("job:guid").item(0).getTextContent();
		//      System.out.println("New Job ID (2): " + locNewJobID);
		assertEquals(newJobID.toString(), locNewJobID);
	}

	private void test03_4(File outFile, GCshID newInvcID) throws Exception {
		//      assertNotEquals(null, outFileGlob);
		//      assertEquals(true, outFileGlob.exists());

		gcshOutFile = new GnuCashFileImpl(outFile);

		//      System.out.println("New Job ID (3): " + newJobID);
		GnuCashGenerJob jobGener = gcshOutFile.getGenerJobByID(newInvcID);
		assertNotEquals(null, jobGener);
		GnuCashCustomerJob jobSpec = new GnuCashCustomerJobImpl(jobGener);
		assertNotEquals(null, jobSpec);

		assertEquals(newInvcID, jobGener.getID());
		assertEquals(newInvcID, jobSpec.getID());

		assertEquals(CUST_1_ID, jobGener.getOwnerID());
		assertEquals(CUST_1_ID, jobSpec.getOwnerID());
		assertEquals(CUST_1_ID, jobSpec.getCustomerID());

		assertEquals("J123", jobGener.getNumber());
		assertEquals("J123", jobSpec.getNumber());

		assertEquals("New job for customer 1", jobGener.getName());
		assertEquals("New job for customer 1", jobSpec.getName());
	}

}
