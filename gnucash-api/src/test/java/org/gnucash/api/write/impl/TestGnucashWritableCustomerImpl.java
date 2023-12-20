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
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.TestGnucashCustomerImpl;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableCustomerImpl
{
    private static final GCshID CUST_1_ID = TestGnucashCustomerImpl.CUST_1_ID;
//    private static final GCshID CUST_2_ID = TestGnucashCustomerImpl.CUST_2_ID;
    
//    private static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
    private static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
    
    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;

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
    return new JUnit4TestAdapter(TestGnucashWritableCustomerImpl.class);  
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
  }

  // -----------------------------------------------------------------
  // PART 1: Read existing objects as modifiable ones
  //         (and see whether they are fully symmetrical to their read-only
  //         counterparts)
  // -----------------------------------------------------------------
  // Cf. TestGnucashCustomerImpl.test01_1/02_1
  // 
  // Check whether the GnucashWritableCustomer objects returned by 
  // GnucashWritableFileImpl.getWritableCustomerByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getCustomerByID().
  
  @Test
  public void test01_1_1() throws Exception
  {
    GnucashWritableCustomer cust = gcshInFile.getCustomerByID(CUST_1_ID);
    assertNotEquals(null, cust);
    
    assertEquals(CUST_1_ID, cust.getID());
    assertEquals("000001", cust.getNumber());
    assertEquals("Unfug und Quatsch GmbH", cust.getName());

    assertEquals(0.0, cust.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(0.0, cust.getCredit().doubleValue(), ConstTest.DIFF_TOLERANCE);

    assertEquals(null, cust.getNotes());

    assertEquals(null, cust.getTaxTableID());
    
    assertEquals(BLLTRM_2_ID, cust.getTermsID());
    assertEquals("30-10-3", cust.getTerms().getName());
    assertEquals(GCshBillTerms.Type.DAYS, cust.getTerms().getType());
    // etc., cf. class TestGCshBillTermsImpl
  }

  @Test
  public void test01_1_2() throws Exception
  {
      GnucashWritableCustomer cust = gcshInFile.getCustomerByID(CUST_1_ID);
      assertNotEquals(null, cust);

      assertEquals(1, ((GnucashWritableCustomerImpl) cust).getNofOpenInvoices());
      assertEquals(cust.getNofOpenInvoices(), ((GnucashWritableCustomerImpl) cust).getNofOpenInvoices()); // not trivial

      assertEquals(1, ((GnucashWritableCustomerImpl) cust).getPaidWritableInvoices_direct().size());
      assertEquals(cust.getPaidInvoices_direct().size(), ((GnucashWritableCustomerImpl) cust).getPaidWritableInvoices_direct().size()); // not trivial!
      
      Collection<GnucashCustomerInvoice> invcList1 = cust.getPaidInvoices_direct();
      Collections.sort((ArrayList<GnucashCustomerInvoice>) invcList1);
      assertEquals("d9967c10fdf1465e9394a3e4b1e7bd79", 
                   ((GnucashCustomerInvoice) invcList1.toArray()[0]).getID().toString());
      Collection<GnucashWritableCustomerInvoice> invcList2 = ((GnucashWritableCustomerImpl) cust).getPaidWritableInvoices_direct();
      Collections.sort((ArrayList<GnucashWritableCustomerInvoice>) invcList2);
      assertEquals("d9967c10fdf1465e9394a3e4b1e7bd79", 
                   ((GnucashWritableCustomerInvoice) invcList2.toArray()[0]).getID().toString());
      
      invcList1 = cust.getUnpaidInvoices_direct();
      Collections.sort((ArrayList<GnucashCustomerInvoice>) invcList1);
      assertEquals(1, ((GnucashWritableCustomerImpl) cust).getUnpaidWritableInvoices_direct().size());
      assertEquals(cust.getUnpaidInvoices_direct().size(), ((GnucashWritableCustomerImpl) cust).getUnpaidWritableInvoices_direct().size()); // not trivial
      assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", 
                   ((GnucashCustomerInvoice) invcList1.toArray()[0]).getID().toString());
      invcList2 = ((GnucashWritableCustomerImpl) cust).getUnpaidWritableInvoices_direct();
      Collections.sort((ArrayList<GnucashWritableCustomerInvoice>) invcList2);
      assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", 
                   ((GnucashWritableCustomerInvoice) invcList2.toArray()[0]).getID().toString());
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableCustomer objects returned by 
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
      GnucashWritableCustomer cust = gcshInFile.createWritableCustomer();
      cust.setNumber(GnucashCustomerImpl.getNewNumber(cust));
      cust.setName("Frederic Austerlitz");
      
      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
      
      test03_1_check(outFile);
  }

  // -----------------------------------------------------------------

//  @Test
//  public void test03_2() throws Exception
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

  private void test03_1_check(File outFile) throws Exception
  {
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
      
      NodeList nList = document.getElementsByTagName("gnc:GncCustomer");
      assertEquals(ConstTest.Stats.NOF_CUST + 1, nList.getLength());

      // Last (new) node
      Node lastNode = nList.item(nList.getLength() - 1);
      assertEquals(lastNode.getNodeType(), Node.ELEMENT_NODE);
      Element elt = (Element) lastNode;
      assertEquals("Frederic Austerlitz", elt.getElementsByTagName("cust:name").item(0).getTextContent());
      assertEquals("000004", elt.getElementsByTagName("cust:id").item(0).getTextContent());
  }

  // -----------------------------------------------------------------

  @Test
  public void test03_4() throws Exception
  {
      GnucashWritableCustomer cust1 = gcshInFile.createWritableCustomer();
      cust1.setNumber(GnucashCustomerImpl.getNewNumber(cust1));
      cust1.setName("Frederic Austerlitz");
      
      GnucashWritableCustomer cust2 = gcshInFile.createWritableCustomer();
      cust2.setNumber(GnucashCustomerImpl.getNewNumber(cust2));
      cust2.setName("Doris Kappelhoff");
      
      GnucashWritableCustomer cust3 = gcshInFile.createWritableCustomer();
      cust3.setNumber(GnucashCustomerImpl.getNewNumber(cust3));
      cust3.setName("Georgios Panayiotou");
      
      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
      // System.err.println("Outfile for TestGnucashWritableCustomerImpl.test02_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
      
      test03_4_check(outFile);
  }
  
  private void test03_4_check(File outFile) throws Exception
  {
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
      
      NodeList nList = document.getElementsByTagName("gnc:GncCustomer");
      assertEquals(ConstTest.Stats.NOF_CUST + 3, nList.getLength());

      // Last three nodes (the new ones)
      Node node = nList.item(nList.getLength() - 3);
      assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
      Element elt = (Element) node;
      assertEquals("Frederic Austerlitz", elt.getElementsByTagName("cust:name").item(0).getTextContent());
      assertEquals("000004", elt.getElementsByTagName("cust:id").item(0).getTextContent());

      node = nList.item(nList.getLength() - 2);
      assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
      elt = (Element) node;
      assertEquals("Doris Kappelhoff", elt.getElementsByTagName("cust:name").item(0).getTextContent());
      assertEquals("000005", elt.getElementsByTagName("cust:id").item(0).getTextContent());

      node = nList.item(nList.getLength() - 1);
      assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
      elt = (Element) node;
      assertEquals("Georgios Panayiotou", elt.getElementsByTagName("cust:name").item(0).getTextContent());
      assertEquals("000006", elt.getElementsByTagName("cust:id").item(0).getTextContent());
  }

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }

}
