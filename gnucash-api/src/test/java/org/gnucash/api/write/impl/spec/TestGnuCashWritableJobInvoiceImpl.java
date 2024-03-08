package org.gnucash.api.write.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
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

public class TestGnuCashWritableJobInvoiceImpl {
	private static final GCshID JOB_1_ID = TestGnuCashGenerJobImpl.JOB_1_ID;
	private static final GCshID JOB_2_ID = TestGnuCashGenerJobImpl.JOB_2_ID;

	private static final GCshID INCOME_ACCT_ID = TestGnuCashWritableCustomerInvoiceImpl.INCOME_ACCT_ID;
	private static final GCshID EXPENSES_ACCT_ID = TestGnuCashWritableVendorBillImpl.EXPENSES_ACCT_ID;
	private static final GCshID RECEIVABLE_ACCT_ID = TestGnuCashWritableCustomerInvoiceImpl.RECEIVABLE_ACCT_ID;
	private static final GCshID PAYABLE_ACCT_ID = TestGnuCashWritableVendorBillImpl.PAYABLE_ACCT_ID;

	// ----------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GnuCashGenerJob job1 = null;
	private GnuCashGenerJob job2 = null;

	private GnuCashAccount incomeAcct = null;
	private GnuCashAccount expensesAcct = null;
	private GnuCashAccount receivableAcct = null;
	private GnuCashAccount payableAcct = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableJobInvoiceImpl.class);
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

		job1 = gcshInFile.getGenerJobByID(JOB_1_ID);
		job2 = gcshInFile.getGenerJobByID(JOB_2_ID);

		incomeAcct = gcshInFile.getAccountByID(INCOME_ACCT_ID);
		expensesAcct = gcshInFile.getAccountByID(EXPENSES_ACCT_ID);
		receivableAcct = gcshInFile.getAccountByID(RECEIVABLE_ACCT_ID);
		payableAcct = gcshInFile.getAccountByID(PAYABLE_ACCT_ID);
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		LocalDate postDate = LocalDate.of(2023, 8, 1);
		LocalDate openedDate = LocalDate.of(2023, 8, 3);
		LocalDate dueDate = LocalDate.of(2023, 8, 10);
		GnuCashWritableJobInvoice invc = gcshInFile.createWritableJobInvoice("19327", job1, incomeAcct, receivableAcct,
				openedDate, postDate, dueDate);

		//      GnuCashWritableCustomerInvoiceEntry entr = invc.createEntry(acct2, 
		//                                                                  new FixedPointNumber(12), 
		//                                                                  new FixedPointNumber(13));

		assertNotEquals(null, invc);
		GCshID newInvcID = invc.getID();
		//      System.out.println("New Invoice ID (1): " + newInvcID);

		assertEquals("19327", invc.getNumber());

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		//      System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test01_2_check_1_valid(outFile);
		test01_3(outFile, newInvcID);
		test01_4(outFile, newInvcID);

		// post invoice
		invc.post(incomeAcct, receivableAcct, postDate, dueDate);

		// write to file
		outFile.delete();
		gcshInFile.writeFile(outFile);

		test01_5(outFile, newInvcID);
	}

	// Sort of "soft" variant of validity check
	// CAUTION: Not platform-independent!
	// Tool "xmllint" must be installed and in path
	private void test01_2_check_1_valid(File outFile) throws Exception {
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

	private void test01_3(File outFile, GCshID newInvcID)
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

		NodeList nList = document.getElementsByTagName("gnc:GncInvoice");
		assertEquals(8, nList.getLength());

		// Last (new) node
		Node lastNode = nList.item(nList.getLength() - 1);
		assertEquals(Node.ELEMENT_NODE, lastNode.getNodeType());

		Element elt = (Element) lastNode;
		assertEquals("19327", elt.getElementsByTagName("invoice:id").item(0).getTextContent());
		String locNewInvcID = elt.getElementsByTagName("invoice:guid").item(0).getTextContent();
		//      System.out.println("New Invoice ID (2): " + locNewInvcID);
		assertEquals(newInvcID.toString(), locNewInvcID);
	}

	// Before post
	private void test01_4(File outFile, GCshID newInvcID) throws Exception {
		//      assertNotEquals(null, outFileGlob);
		//      assertEquals(true, outFileGlob.exists());

		gcshOutFile = new GnuCashFileImpl(outFile);

		//      System.out.println("New Invoice ID (3): " + newInvcID);
		GnuCashGenerInvoice invcGener = gcshOutFile.getGenerInvoiceByID(newInvcID);
		assertNotEquals(null, invcGener);
		GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGener);
		assertNotEquals(null, invcSpec);

		assertEquals("19327", invcSpec.getNumber());
		assertEquals(null, invcSpec.getPostAccountID());
		assertEquals(null, invcSpec.getPostTransactionID());
	}

	// After post
	private void test01_5(File outFile, GCshID newInvcID) throws Exception {
		//      assertNotEquals(null, outFileGlob);
		//      assertEquals(true, outFileGlob.exists());

		gcshOutFile = new GnuCashFileImpl(outFile);

		//      System.out.println("New Invoice ID (3): " + newInvcID);
		GnuCashGenerInvoice invcGener = gcshOutFile.getGenerInvoiceByID(newInvcID);
		assertNotEquals(null, invcGener);
		GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGener);
		assertNotEquals(null, invcSpec);

		assertEquals("19327", invcSpec.getNumber());
		assertEquals(RECEIVABLE_ACCT_ID, invcSpec.getPostAccountID());

		assertNotEquals(null, invcSpec.getPostTransactionID());
		GnuCashTransaction postTrx = gcshOutFile.getTransactionByID(invcSpec.getPostTransactionID());
		assertNotEquals(null, postTrx);
		assertEquals(2, postTrx.getSplits().size());
		GCshID postTrxFirstSpltId = postTrx.getFirstSplit().getID();
		assertNotEquals(postTrxFirstSpltId, postTrx);
		GCshID postTrxFirstSpltAcctId = postTrx.getFirstSplit().getAccount().getID();
		assertNotEquals(postTrxFirstSpltAcctId, postTrx);
		GCshID postTrxSecondSpltAcctId = postTrx.getSecondSplit().getAccount().getID();
		assertNotEquals(postTrxSecondSpltAcctId, postTrx);
		//      System.out.println("ptrx1 " + postTrxFirstSpltAcctId);
		//      System.out.println("ptrx2 " + postTrxSecondSpltAcctId);
	}

}
