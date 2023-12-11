package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashFileImpl
{
  private GnucashFileImpl gcshFile = null;
  private GCshFileStats   gcshFileStats = null;

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashFileImpl.class);  
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
    
    gcshFileStats = new GCshFileStats(gcshFile);
  }

  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
      assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test02() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test03() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
      // This one is an exception:
      // assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX_SPLT, gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test04() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test05() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_INVC_ENTR, gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test06() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test07() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test08() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));
  }

  @Test
  public void test09() throws Exception
  {    
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));
  }
}
