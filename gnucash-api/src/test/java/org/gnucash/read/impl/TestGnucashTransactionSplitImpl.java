package org.gnucash.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.ConstTest;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashTransactionSplit;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashTransactionSplitImpl
{
  private GnucashFile             gcshFile = null;
  private GnucashTransactionSplit splt = null;
  
  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashTransactionSplitImpl.class);  
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
  public void test03() throws Exception
  {
    // Works only in German locale:
    // assertEquals("Rechnung",   GnucashTransactionSplit.Action.INVOICE.getLocaleString());
    
    assertEquals("Bill",                GnucashTransactionSplit.Action.BILL.getLocaleString(Locale.ENGLISH));
    assertEquals("Lieferantenrechnung", GnucashTransactionSplit.Action.BILL.getLocaleString(Locale.GERMAN));
    assertEquals("Facture fournisseur", GnucashTransactionSplit.Action.BILL.getLocaleString(Locale.FRENCH));
  }

  @Test
  public void test04() throws Exception
  {
    assertEquals(31, ((GnucashFileStats) gcshFile).getNofEntriesTransactionSplitMap());
  }

}
