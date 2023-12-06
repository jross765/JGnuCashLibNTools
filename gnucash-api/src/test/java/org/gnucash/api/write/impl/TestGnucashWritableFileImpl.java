package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableFileImpl
{
    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl         gcshOutFile = null;
    
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
    return new JUnit4TestAdapter(TestGnucashWritableFileImpl.class);  
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
  // Cf. TestGnucashFile.test01/02
  // 
  // Check whether the GnucashWritableFile objects returned by 
  // GnucashWritableFileImpl.getWritableFileByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getFileByID().

  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {    
    assertEquals(93, gcshInFile.getNofEntriesAccountMap());
  }

  @Test
  public void test02() throws Exception
  {    
    assertEquals(12, gcshInFile.getNofEntriesTransactionMap());
  }

  // ::TODO
  @Test
  public void test03() throws Exception
  {    
    assertEquals(31, gcshInFile.getNofEntriesTransactionSplitMap());
  }

  @Test
  public void test04() throws Exception
  {    
    assertEquals(7, gcshInFile.getNofEntriesGenerInvoiceMap());
  }

//  @Test
//  public void test05() throws Exception
//  {    
//    assertEquals(14, gcshInFile.getNofEntriesGenerInvoiceEntriesMap());
//  }

  @Test
  public void test06() throws Exception
  {    
    assertEquals(2, gcshInFile.getNofEntriesGenerJobMap());
  }

  @Test
  public void test07() throws Exception
  {    
    assertEquals(3, gcshInFile.getNofEntriesCustomerMap());
  }

  @Test
  public void test08() throws Exception
  {    
    assertEquals(3, gcshInFile.getNofEntriesVendorMap());
  }

  @Test
  public void test09() throws Exception
  {    
    assertEquals(1, gcshInFile.getNofEntriesEmployeeMap());
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableFile objects returned by 
  // can actually be modified -- both in memory and persisted in file.

  // ::TODO

  // -----------------------------------------------------------------
  // PART 3: Create new objects
  // -----------------------------------------------------------------
  
  // ::TODO

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }
}
