package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.ArrayList;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.TestGnucashGenerInvoiceImpl;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableGenerInvoiceImpl
{
    public static final GCshID INVC_1_ID = TestGnucashGenerInvoiceImpl.INVC_1_ID;
    public static final GCshID INVC_2_ID = TestGnucashGenerInvoiceImpl.INVC_2_ID;
    public static final GCshID INVC_3_ID = TestGnucashGenerInvoiceImpl.INVC_3_ID;
    public static final GCshID INVC_4_ID = TestGnucashGenerInvoiceImpl.INVC_4_ID;
    public static final GCshID INVC_5_ID = TestGnucashGenerInvoiceImpl.INVC_5_ID;
    public static final GCshID INVC_6_ID = TestGnucashGenerInvoiceImpl.INVC_6_ID;
    public static final GCshID INVC_7_ID = TestGnucashGenerInvoiceImpl.INVC_7_ID;
    
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
    return new JUnit4TestAdapter(TestGnucashWritableGenerInvoiceImpl.class);  
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
  // Cf. TestGnucashGenerInvoiceImpl.testXYZ (all)
  // 
  // Check whether the GnucashWritableGenerInvoice objects returned by 
  // GnucashWritableFileImpl.getWritableTransactionByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getTransactionByID().
  
  @Test
  public void testCust01_1() throws Exception
  {
    GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_1_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_1_ID, invc.getID());
    assertEquals(GCshOwner.Type.CUSTOMER, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("R1730", invc.getNumber());
    assertEquals("Alles ohne Steuern / voll bezahlt", invc.getDescription());

    assertEquals("2023-07-29T10:59Z", invc.getDateOpened().toString());
    assertEquals("2023-07-29T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testCust02_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals(2, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("92e54c04b66f4682a9afb48e27dfe397", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
    assertEquals("3c67a99b5fe34387b596bb1fbab21a74", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[1]).getID().toString());
  }

  @Test
  public void testCust03_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals(1327.60, invc.getInvcAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(1327.60, invc.getInvcAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testCust04_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_1_ID);
      assertNotEquals(null, invc);

    assertEquals("c97032ba41684b2bb5d1391c9d7547e9", invc.getPostTransaction().getID().toString());
    assertEquals(1, invc.getPayingTransactions().size());

    ArrayList<GnucashTransaction> trxList = (ArrayList<GnucashTransaction>) invc.getPayingTransactions();
    Collections.sort(trxList);
    assertEquals("29557cfdf4594eb68b1a1b710722f991", 
                 ((GnucashTransaction) trxList.toArray()[0]).getID().toString());

    assertEquals(true, invc.isInvcFullyPaid());
  }

  // -----------------------------------------------------------------

  @Test
  public void testVend01_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_4_ID, invc.getID());
    assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("1730-383/2", invc.getNumber());
    assertEquals("Sie wissen schon: Gefälligkeiten, ne?", invc.getDescription());

    assertEquals("2023-08-31T10:59Z", invc.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-31T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testVend01_2() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);
    
    assertEquals(INVC_2_ID, invc.getID());
    assertEquals(GCshOwner.Type.VENDOR, invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("2740921", invc.getNumber());
    assertEquals("Dat isjamaol eine schöne jepflejgte Reschnung!", invc.getDescription());

    assertEquals("2023-08-30T10:59Z", invc.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-30T10:59Z", invc.getDatePosted().toString());
  }

  @Test
  public void testVend02_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

    assertEquals(1, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("0041b8d397f04ae4a2e9e3c7f991c4ec", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
  }

  @Test
  public void testVend02_2() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals(2, invc.getGenerEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(invc.getGenerEntries());
    assertEquals("513589a11391496cbb8d025fc1e87eaa", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[1]).getID().toString());
    assertEquals("dc3c53f07ff64199ad4ea38988b3f40a", 
                 ((GnucashGenerInvoiceEntry) entrList.toArray()[0]).getID().toString());
  }

  @Test
  public void testVend03_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

    assertEquals(41.40, invc.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    // Note: due to (purposefully) incorrect booking, the gross amount
    // of this bill is *not* 49.27 EUR, but 41.40 EUR (its net amount).
    assertEquals(41.40, invc.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testVend03_2() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals(79.11, invc.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(94.14, invc.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void testVend04_1() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_4_ID);
    assertNotEquals(null, invc);

//    assertEquals("xxx", invc.getPostTransaction());
    
    // ::TODO
    assertEquals(0, invc.getPayingTransactions().size());
    
//    ArrayList<GnucashTransaction> trxList = (ArrayList<GnucashTransaction>) bllSpec.getPayingTransactions();
//    Collections.sort(trxList);
//    assertEquals("xxx", 
//                 ((GnucashTransaction) bllSpec.getPayingTransactions().toArray()[0]).getID());

    assertEquals(false, invc.isBillFullyPaid());
  }

  @Test
  public void testVend04_2() throws Exception
  {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_2_ID);
    assertNotEquals(null, invc);

    assertEquals("aa64d862bb5e4d749eb41f198b28d73d", invc.getPostTransaction().getID().toString());   
    assertEquals(1, invc.getPayingTransactions().size());
    
    ArrayList<GnucashTransaction> trxList = (ArrayList<GnucashTransaction>) invc.getPayingTransactions();
    Collections.sort(trxList);
    assertEquals("ccff780b18294435bf03c6cb1ac325c1", 
                 ((GnucashTransaction) trxList.toArray()[0]).getID().toString());
    
    assertEquals(true, invc.isBillFullyPaid());
  }

  @Test
  public void test06_1() throws Exception {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_4_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.vendor.bill.link.01", invc.getURL());
  }

  @Test
  public void test06_2() throws Exception {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_5_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.job.invoice.link.01", invc.getURL());
  }
  
  @Test
  public void test06_3() throws Exception {
      GnucashWritableGenerInvoice invc = gcshInFile.getWritableGenerInvoiceByID(INVC_6_ID);
      assertNotEquals(null, invc);
      assertEquals("https://my.customer.invoice.link.01", invc.getURL());
  }
  
  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableGenerInvoice objects returned by 
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
