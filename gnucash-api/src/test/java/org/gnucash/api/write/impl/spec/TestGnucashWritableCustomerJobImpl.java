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
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashCustomerImpl;
import org.gnucash.api.read.impl.TestGnucashGenerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
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

public class TestGnucashWritableCustomerJobImpl
{
    private static final GCshID JOB_1_ID = TestGnucashGenerJobImpl.JOB_1_ID;

    private static final GCshID CUST_1_ID = TestGnucashCustomerImpl.CUST_1_ID;
//    private static final GCshID CUST_2_ID = TestGnucashCustomerImpl.CUST_2_ID;
//    private static final GCshID CUST_3_ID = TestGnucashCustomerImpl.CUST_3_ID;

    // ----------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl         gcshOutFile = null;

    private GnucashCustomer cust1 = null;
    
    // ----------------------------

    // https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
    @SuppressWarnings("exports")
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashWritableCustomerJobImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
    InputStream gcshInFileStream = null;
    try 
    {
      gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
    } 
    catch ( Exception exc ) 
    {
      System.err.println("Cannot generate input stream from resource");
      return;
    }
    
    try
    {
      gcshInFile = new GnucashWritableFileImpl(gcshInFileStream);
    }
    catch ( Exception exc )
    {
      System.err.println("Cannot parse GnuCash in-file");
      exc.printStackTrace();
    }
    
    // ----------------------------
    
    cust1 = gcshInFile.getCustomerByID(CUST_1_ID);
  }

  // -----------------------------------------------------------------
  // PART 1: Read existing objects as modifiable ones
  //         (and see whether they are fully symmetrical to their read-only
  //         counterparts)
  // -----------------------------------------------------------------
  // Cf. TestGnucashCustomerJobImpl.test01/02
  // 
  // Check whether the GnucashWritableCustomerJob objects returned by 
  // GnucashWritableFileImpl.getWritableGenerJobByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getGenerJobByID().
  
  @Test
  public void test01_1() throws Exception
  {
    GnucashWritableCustomerJob jobSpec = (GnucashWritableCustomerJob) gcshInFile.getGenerJobByID(JOB_1_ID);
    assertNotEquals(null, jobSpec);

    assertTrue(jobSpec instanceof GnucashCustomerJob);
    assertEquals(JOB_1_ID, jobSpec.getID());
    assertEquals("000001", jobSpec.getNumber());
    assertEquals("Do more for others", jobSpec.getName());
  }

  @Test
  public void test01_2() throws Exception
  {
    GnucashWritableCustomerJob jobSpec = (GnucashWritableCustomerJob) gcshInFile.getGenerJobByID(JOB_1_ID);
    assertNotEquals(null, jobSpec);
      
    assertEquals(1, jobSpec.getNofOpenInvoices());
    assertEquals(((GnucashCustomerJob) jobSpec).getNofOpenInvoices(), jobSpec.getNofOpenInvoices());

    assertEquals(0, jobSpec.getPaidInvoices().size());
    assertEquals(jobSpec.getPaidInvoices().size(), ((GnucashCustomerJob) jobSpec).getPaidInvoices().size());
    assertEquals(jobSpec.getPaidInvoices().size(), ((GnucashWritableCustomerJobImpl) jobSpec).getPaidWritableInvoices().size());

    assertEquals(1, jobSpec.getUnpaidInvoices().size());
    assertEquals(jobSpec.getUnpaidInvoices().size(), ((GnucashCustomerJob) jobSpec).getUnpaidInvoices().size());
    assertEquals(jobSpec.getUnpaidInvoices().size(), ((GnucashWritableCustomerJobImpl) jobSpec).getUnpaidWritableInvoices().size());
  }

  @Test
  public void test01_3() throws Exception
  {
    GnucashWritableCustomerJob jobSpec = (GnucashWritableCustomerJob) gcshInFile.getGenerJobByID(JOB_1_ID);
    assertNotEquals(null, jobSpec);
      
    GCshID custID = new GCshID("f44645d2397946bcac90dff68cc03b76");
    assertEquals(custID, jobSpec.getOwnerID());
    // ::TODO
    // assertEquals(custID, jobSpec.getCustomerID());
  }
  
  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableCustomerJob objects returned by 
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
  public void test03_1() throws Exception
  {
      GnucashWritableCustomerJob job = gcshInFile.createWritableCustomerJob(
	      						cust1, "J123", 
	      						"New job for customer 1");
      
      assertNotEquals(null, job);
      GCshID newJobID = job.getID();
//      System.out.println("New Job ID (1): " + newJobID);
      
      assertEquals("J123", job.getNumber());

      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
      
      // test01_2();
      test03_3(outFile, newJobID);
      test03_4(outFile, newJobID);
  }

  private void test03_2(File outFile, String newJobID) throws ParserConfigurationException, SAXException, IOException 
  {
      // ::TODO
      // Check if generated XML file is valid
  }
  
  private void test03_3(File outFile, GCshID newJobID) throws ParserConfigurationException, SAXException, IOException 
  {
      //    assertNotEquals(null, outFileGlob);
      //    assertEquals(true, outFileGlob.exists());

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
      assertEquals("J123", elt.getElementsByTagName("job:id").item(0).getTextContent());
      String locNewJobID = elt.getElementsByTagName("job:guid").item(0).getTextContent();
//      System.out.println("New Job ID (2): " + locNewJobID);
      assertEquals(newJobID.toString(), locNewJobID);
  }

  private void test03_4(File outFile, GCshID newInvcID) throws Exception
  {
//      assertNotEquals(null, outFileGlob);
//      assertEquals(true, outFileGlob.exists());

      gcshOutFile = new GnucashFileImpl(outFile);
      
//      System.out.println("New Job ID (3): " + newJobID);
      GnucashGenerJob jobGener = gcshOutFile.getGenerJobByID(newInvcID);
      assertNotEquals(null, jobGener);
      GnucashCustomerJob jobSpec = new GnucashCustomerJobImpl(jobGener);
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

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }
}
