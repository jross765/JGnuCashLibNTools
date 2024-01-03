package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.impl.TestGnucashGenerInvoiceEntryImpl;
import org.gnucash.api.write.GnucashWritableGenerInvoiceEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableGenerInvoiceEntryImpl
{
    private static final GCshID INVCENTR_1_ID = TestGnucashGenerInvoiceEntryImpl.INVCENTR_1_ID;
    private static final GCshID INVCENTR_2_ID = TestGnucashGenerInvoiceEntryImpl.INVCENTR_2_ID;
    private static final GCshID INVCENTR_3_ID = TestGnucashGenerInvoiceEntryImpl.INVCENTR_3_ID;

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
    return new JUnit4TestAdapter(TestGnucashWritableGenerInvoiceEntryImpl.class);  
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
  // Cf. TestGnucashGenerInvoiceEntryImpl.test02_x
  // 
  // Check whether the GnucashWritableGenerInvoiceEntry objects returned by 
  // GnucashWritableFileImpl.getWritableGenerInvoiceEntryByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getGenerInvoiceEntryByID().
  
  @Test
  public void test01_2_1() throws Exception
  {
    GnucashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_1_ID);
    assertNotEquals(null, invcEntr);

    assertEquals(INVCENTR_1_ID, invcEntr.getID());
    assertEquals(GnucashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
    assertEquals("286fc2651a7848038a23bb7d065c8b67", invcEntr.getGenerInvoiceID().toString());
    assertEquals(null, invcEntr.getAction());
    assertEquals("Item 1", invcEntr.getDescription());
    
    assertEquals(true, invcEntr.isBillTaxable());
    assertEquals(0.19, invcEntr.getBillApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(12.50, invcEntr.getBillPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(3, invcEntr.getQuantity().intValue());
  }

  @Test
  public void test01_2_2() throws Exception
  {
    GnucashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_2_ID);
    assertNotEquals(null, invcEntr);

    assertEquals(INVCENTR_2_ID, invcEntr.getID());
    assertEquals(GnucashGenerInvoice.TYPE_VENDOR, invcEntr.getType());
    assertEquals("4eb0dc387c3f4daba57b11b2a657d8a4", invcEntr.getGenerInvoiceID().toString());
    assertEquals(GnucashGenerInvoiceEntry.Action.HOURS, invcEntr.getAction());
    assertEquals("Gefälligkeiten", invcEntr.getDescription());
    
    assertEquals(true, invcEntr.isBillTaxable());
    // Following: sic, because there is n	o tax table entry assigned
    // (this is an error in real life, but we have done it on purpose here
    // for the tests).
    assertEquals(0.00, invcEntr.getBillApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(13.80, invcEntr.getBillPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(3, invcEntr.getQuantity().intValue());
  }

  @Test
  public void test01_2_3() throws Exception
  {
    GnucashWritableGenerInvoiceEntry invcEntr = gcshInFile.getWritableGenerInvoiceEntryByID(INVCENTR_3_ID);
    assertNotEquals(null, invcEntr);

    assertEquals(INVCENTR_3_ID, invcEntr.getID());
    assertEquals(GnucashGenerInvoice.TYPE_CUSTOMER, invcEntr.getType());
    assertEquals("6588f1757b9e4e24b62ad5b37b8d8e07", invcEntr.getGenerInvoiceID().toString());
    assertEquals(GnucashGenerInvoiceEntry.Action.MATERIAL, invcEntr.getAction());
    assertEquals("Posten 3", invcEntr.getDescription());
    
    assertEquals(true, invcEntr.isInvcTaxable());
    assertEquals(0.19, invcEntr.getInvcApplicableTaxPercent().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(120.00, invcEntr.getInvcPrice().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(10, invcEntr.getQuantity().intValue());
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
  
  // ::TODO
  
//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }

}