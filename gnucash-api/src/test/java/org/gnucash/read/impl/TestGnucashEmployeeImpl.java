package org.gnucash.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;

import org.gnucash.ConstTest;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashVendor;
import org.gnucash.read.aux.GCshBillTerms;
import org.gnucash.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.read.spec.GnucashEmployeeVoucher;
import org.gnucash.read.spec.GnucashVendorBill;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashEmployeeImpl
{
  private GnucashFile     gcshFile = null;
  private GnucashEmployee empl = null;
  
  public static final GCshID EMPL_1_ID = new GCshID("7f70b352dcf44a5d8085767a53a9bc37");

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashEmployeeImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
    InputStream gcshFileStream = null;
    try 
    {
      gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
    } 
    catch ( Exception exc ) 
    {
      System.err.println("Cannot generate input stream from resource");
      return;
    }
    
    try
    {
      gcshFile = new GnucashFileImpl(gcshFileStream);
    }
    catch ( Exception exc )
    {
      System.err.println("Cannot parse GnuCash file");
      exc.printStackTrace();
    }
  }

  // -----------------------------------------------------------------

  @Test
  public void test01_1() throws Exception
  {
    empl = gcshFile.getEmployeeByID(EMPL_1_ID);
    assertNotEquals(null, empl);
    
    assertEquals(EMPL_1_ID, empl.getId());
    assertEquals("000001", empl.getNumber());
    assertEquals("otwist", empl.getUserName());
    assertEquals("Oliver Twist", empl.getName());
  }

  @Test
  public void test02_1() throws Exception
  {
    empl = gcshFile.getEmployeeByID(EMPL_1_ID);
    assertNotEquals(null, empl);
    
    assertEquals(1, empl.getNofOpenVouchers());
    assertEquals(1, empl.getUnpaidVouchers_direct().size());
    assertEquals(0, empl.getPaidVouchers_direct().size());
    
    LinkedList<GnucashEmployeeVoucher> vchList = (LinkedList<GnucashEmployeeVoucher>) empl.getUnpaidVouchers_direct();
    Collections.sort(vchList);
    assertEquals("8de4467c17e04bb2895fb68cc07fc4df", 
                 ((GnucashEmployeeVoucher) vchList.toArray()[0]).getId().toString() );

//    vchList = (LinkedList<GnucashEmployeeVoucher>) empl.getPaidVouchers_direct();
//    Collections.sort(vchList);
//    assertEquals("xxx", 
//                 ((GnucashVendorBill) vchList.toArray()[0]).getId() );
  }
}
