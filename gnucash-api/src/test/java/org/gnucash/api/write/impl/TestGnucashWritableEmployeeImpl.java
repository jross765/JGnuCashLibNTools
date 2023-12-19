package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.TestGnucashEmployeeImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.write.GnucashWritableEmployee;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableEmployeeImpl
{
    private static final GCshID EMPL_1_ID = TestGnucashEmployeeImpl.EMPL_1_ID;
  
    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private String outFileGlobNameAbs = null;
    private File outFileGlob = null;

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
    return new JUnit4TestAdapter(TestGnucashWritableEmployeeImpl.class);  
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
    
    URL outFileNameAbsURL = classLoader.getResource(ConstTest.GCSH_FILENAME_IN); // sic
//    System.err.println("Out file name (glob, URL): '" + outFileNameAbsURL + "'");
    outFileGlobNameAbs = outFileNameAbsURL.getPath();
    outFileGlobNameAbs = outFileGlobNameAbs.replace(ConstTest.GCSH_FILENAME_IN, ConstTest.GCSH_FILENAME_OUT);
//    System.err.println("Out file name (glob): '" + outFileGlobNameAbs + "'");
    outFileGlob = new File(outFileGlobNameAbs);
  }

  // -----------------------------------------------------------------
  // PART 1: Read existing objects as modifiable ones
  //         (and see whether they are fully symmetrical to their read-only
  //         counterparts)
  // -----------------------------------------------------------------
  // Cf. TestGnucashEmployeeImpl.test01_1/02_2
  // 
  // Check whether the GnucashWritableEmployee objects returned by 
  // GnucashWritableFileImpl.getWritableEmployeeByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getEmployeeByID().
  
  @Test
  public void test01_1_1() throws Exception
  {
    GnucashWritableEmployee empl = gcshInFile.getEmployeeByID(EMPL_1_ID);
    assertNotEquals(null, empl);
    
    assertEquals(EMPL_1_ID, empl.getID());
    assertEquals("000001", empl.getNumber());
    assertEquals("otwist", empl.getUserName());
    assertEquals("Oliver Twist", empl.getAddress().getAddressName());
  }

  @Test
  public void test01_1_2() throws Exception
  {
      GnucashWritableEmployee empl = gcshInFile.getEmployeeByID(EMPL_1_ID);
      assertNotEquals(null, empl);
      
      assertEquals(1, ((GnucashWritableEmployeeImpl) empl).getNofOpenVouchers());
      assertEquals(empl.getNofOpenVouchers(), ((GnucashWritableEmployeeImpl) empl).getNofOpenVouchers()); // not trivial

      assertEquals(0, ((GnucashWritableEmployeeImpl) empl).getPaidVouchers().size());
      assertEquals(empl.getPaidVouchers().size(), ((GnucashWritableEmployeeImpl) empl).getPaidVouchers().size()); // not trivial
      
//    vchList = (LinkedList<GnucashEmployeeVoucher>) empl.getPaidVouchers_direct();
//    Collections.sort(vchList);
//    assertEquals("xxx", 
//                 ((GnucashVendorBill) vchList.toArray()[0]).getID() );

      assertEquals(1, ((GnucashWritableEmployeeImpl) empl).getUnpaidVouchers().size());
      assertEquals(empl.getUnpaidVouchers().size(), ((GnucashWritableEmployeeImpl) empl).getUnpaidVouchers().size());
      
      ArrayList<GnucashEmployeeVoucher> vchList = (ArrayList<GnucashEmployeeVoucher>) empl.getUnpaidVouchers();
      Collections.sort(vchList);
      assertEquals("8de4467c17e04bb2895fb68cc07fc4df", 
                   ((GnucashEmployeeVoucher) vchList.toArray()[0]).getID().toString() );
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableEmployee objects returned by 
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
      GnucashWritableEmployee empl = gcshInFile.createWritableEmployee();
      empl.setNumber(GnucashEmployeeImpl.getNewNumber(empl));
      empl.setUserName("Norma Jean Baker");
      
      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
      // System.err.println("Outfile for TestGnucashWritableEmployeeImpl.test01_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
      
      // copy file
      if ( outFileGlob.exists() )
	  FileUtils.delete(outFileGlob);
      FileUtils.copyFile(outFile, outFileGlob);
  }

  // -----------------------------------------------------------------

//  @Test
//  public void test01_2() throws Exception
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

  @Test
  public void test03_3() throws Exception
  {
      assertNotEquals(null, outFileGlob);
      assertEquals(true, outFileGlob.exists());

      // Build document
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(outFileGlob);
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
      assertEquals("Norma Jean Baker", elt.getElementsByTagName("employee:username").item(0).getTextContent());
      assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());
  }

  // -----------------------------------------------------------------

  @Test
  public void test03_4() throws Exception
  {
      GnucashWritableEmployee empl1 = gcshInFile.createWritableEmployee();
      empl1.setNumber(GnucashEmployeeImpl.getNewNumber(empl1));
      empl1.setUserName("Norma Jean Baker");
      
      GnucashWritableEmployee empl2 = gcshInFile.createWritableEmployee();
      empl2.setNumber(GnucashEmployeeImpl.getNewNumber(empl2));
      empl2.setUserName("Madonna Louise Ciccone");
      
      GnucashWritableEmployee empl3 = gcshInFile.createWritableEmployee();
      empl3.setNumber(GnucashEmployeeImpl.getNewNumber(empl3));
      empl3.setUserName("Rowan Atkinson");
      
      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
//      System.err.println("Outfile for TestGnucashWritableEmployeeImpl.test02_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
      
      // copy file
      if ( outFileGlob.exists() )
	  FileUtils.delete(outFileGlob);
      FileUtils.copyFile(outFile, outFileGlob);
  }
  
  @Test
  public void test03_5() throws Exception
  {
      assertNotEquals(null, outFileGlob);
      assertEquals(true, outFileGlob.exists());

      // Build document
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(outFileGlob);
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
      assertEquals("Norma Jean Baker", elt.getElementsByTagName("employee:username").item(0).getTextContent());
      assertEquals("000002", elt.getElementsByTagName("employee:id").item(0).getTextContent());

      node = nList.item(nList.getLength() - 2);
      assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
      elt = (Element) node;
      assertEquals("Madonna Louise Ciccone", elt.getElementsByTagName("employee:username").item(0).getTextContent());
      assertEquals("000003", elt.getElementsByTagName("employee:id").item(0).getTextContent());

      node = nList.item(nList.getLength() - 1);
      assertEquals(node.getNodeType(), Node.ELEMENT_NODE);
      elt = (Element) node;
      assertEquals("Rowan Atkinson", elt.getElementsByTagName("employee:username").item(0).getTextContent());
      assertEquals("000004", elt.getElementsByTagName("employee:id").item(0).getTextContent());
  }

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }

}
