package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableFileImpl
{
    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl         gcshOutFile = null;
    
    private GCshFileStats gcshInFileStats = null;

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

    gcshInFileStats = new GCshFileStats(gcshInFile);
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
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshInFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test02() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test03() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
      // This one is an exception:
      // assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshInFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test04() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_INVC, gcshInFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test05() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
      // ::TODO
      // The following does not work!
      // assertEquals(ConstTest.NOF_INVC_ENTR, gcshInFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
  }

  // ------------------------------

  @Test
  public void test06() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_CUST, gcshInFileStats.getNofEntriesCustomers(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test07() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_VEND, gcshInFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test08() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshInFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test09() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));
  }

  // ------------------------------

  @Test
  public void test10() throws Exception
  {    
      // CAUTION: This one is an exception: 
      // There is one additional commoditiy object on the "raw" level: 
      // the "template".
      assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshInFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test11() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));
  }
  
  // ------------------------------

  @Test
  public void test12() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshInFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test13() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));
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
