package org.gnucash.api.read.impl.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeSet;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashVendorBillImpl
{
  private GnucashFile            gcshFile = null;
  private GnucashGenerInvoice invcGen = null;
  private GnucashVendorBill      bllSpec = null;
  
  private static final GCshID BLL_1_ID = TestGnucashGenerInvoiceImpl.INVC_4_ID;
  private static final GCshID BLL_2_ID = TestGnucashGenerInvoiceImpl.INVC_2_ID;

  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashVendorBillImpl.class);  
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
    invcGen = gcshFile.getGenerInvoiceByID(BLL_1_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);
    
    assertEquals(true, bllSpec instanceof GnucashVendorBillImpl);
    assertEquals(BLL_1_ID, bllSpec.getId());
    assertEquals(GCshOwner.Type.VENDOR, bllSpec.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("1730-383/2", bllSpec.getNumber());
    assertEquals("Sie wissen schon: Gefälligkeiten, ne?", bllSpec.getDescription());

    assertEquals("2023-08-31T10:59Z", bllSpec.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-31T10:59Z", bllSpec.getDatePosted().toString());
  }

  @Test
  public void test01_2() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_2_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);
    
    assertEquals(true, bllSpec instanceof GnucashVendorBillImpl);
    assertEquals(BLL_2_ID, bllSpec.getId());
    assertEquals(GCshOwner.Type.VENDOR, bllSpec.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
    assertEquals("2740921", bllSpec.getNumber());
    assertEquals("Dat isjamaol eine schöne jepflejgte Reschnung!", bllSpec.getDescription());

    assertEquals("2023-08-30T10:59Z", bllSpec.getDateOpened().toString());
    // ::TODO
    assertEquals("2023-08-30T10:59Z", bllSpec.getDatePosted().toString());
  }

  @Test
  public void test02_1() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_1_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(1, invcGen.getGenerEntries().size());
    assertEquals(1, bllSpec.getGenerEntries().size());
    assertEquals(1, bllSpec.getEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(bllSpec.getEntries());
    assertEquals("0041b8d397f04ae4a2e9e3c7f991c4ec", 
                 ((GnucashVendorBillEntry) entrList.toArray()[0]).getId().toString());
  }

  @Test
  public void test02_2() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_2_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(2, invcGen.getGenerEntries().size());
    assertEquals(2, bllSpec.getGenerEntries().size());
    assertEquals(2, bllSpec.getEntries().size());

    TreeSet entrList = new TreeSet(); // sort elements of HashSet
    entrList.addAll(bllSpec.getEntries());
    assertEquals("513589a11391496cbb8d025fc1e87eaa", 
                 ((GnucashVendorBillEntry) entrList.toArray()[1]).getId().toString());
    assertEquals("dc3c53f07ff64199ad4ea38988b3f40a", 
                 ((GnucashVendorBillEntry) entrList.toArray()[0]).getId().toString());
  }

  @Test
  public void test03_1() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_1_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(41.40, invcGen.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(41.40, bllSpec.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(41.40, ((SpecInvoiceCommon) bllSpec).getAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    // Note: due to (purposefully) incorrect booking, the gross amount
    // of this bill is *not* 49.27 EUR, but 41.40 EUR (its net amount).
    assertEquals(41.40, invcGen.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(41.40, bllSpec.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(41.40, ((SpecInvoiceCommon) bllSpec).getAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void test03_2() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_2_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(79.11, invcGen.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(79.11, bllSpec.getBillAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(79.11, ((SpecInvoiceCommon) bllSpec).getAmountWithoutTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(94.14, invcGen.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(94.14, bllSpec.getBillAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals(94.14, ((SpecInvoiceCommon) bllSpec).getAmountWithTaxes().doubleValue(), ConstTest.DIFF_TOLERANCE);
  }

  @Test
  public void test04_1() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_1_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following two return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
//    assertEquals("xxx", invcGen.getPostTransaction());
//    assertEquals("xxx", invcSpec.getPostTransaction());
    
    // ::TODO
    // Note: That the following two return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(0, bllSpec.getPayingTransactions().size());
    assertEquals(0, bllSpec.getPayingTransactions().size());
    
//    LinkedList<GnucashTransaction> trxList = (LinkedList<GnucashTransaction>) bllSpec.getPayingTransactions();
//    Collections.sort(trxList);
//    assertEquals("xxx", 
//                 ((GnucashTransaction) bllSpec.getPayingTransactions().toArray()[0]).getId());

    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(false, invcGen.isBillFullyPaid());
    assertEquals(false, bllSpec.isBillFullyPaid());
    assertEquals(false, ((SpecInvoiceCommon) bllSpec).isFullyPaid());
  }

  @Test
  public void test04_2() throws Exception
  {
    invcGen = gcshFile.getGenerInvoiceByID(BLL_2_ID);
    assertNotEquals(null, invcGen);
    bllSpec = new GnucashVendorBillImpl(invcGen);
    assertNotEquals(null, bllSpec);

    // Note: That the following two return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals("aa64d862bb5e4d749eb41f198b28d73d", invcGen.getPostTransaction().getId().toString());
    assertEquals("aa64d862bb5e4d749eb41f198b28d73d", bllSpec.getPostTransaction().getId().toString());
    
    // Note: That the following two return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(1, invcGen.getPayingTransactions().size());
    assertEquals(1, bllSpec.getPayingTransactions().size());
    
    LinkedList<GnucashTransaction> trxList = (LinkedList<GnucashTransaction>) bllSpec.getPayingTransactions();
    Collections.sort(trxList);
    assertEquals("ccff780b18294435bf03c6cb1ac325c1", 
                 ((GnucashTransaction) trxList.toArray()[0]).getId().toString());
    
    // Note: That the following three return the same result
    // is *not* trivial (in fact, a serious implementation error was
    // found with this test)
    assertEquals(true, invcGen.isBillFullyPaid());
    assertEquals(true, bllSpec.isBillFullyPaid());
    assertEquals(true, ((SpecInvoiceCommon) bllSpec).isFullyPaid());

    assertEquals(! invcGen.isBillFullyPaid(), invcGen.isNotBillFullyPaid());
    assertEquals(! bllSpec.isBillFullyPaid(), bllSpec.isNotBillFullyPaid());
    assertEquals(! ((SpecInvoiceCommon) bllSpec).isFullyPaid(), ((SpecInvoiceCommon) bllSpec).isNotFullyPaid());
  }

  @Test
  public void test05() throws Exception
  {
      invcGen = gcshFile.getGenerInvoiceByID(BLL_1_ID);
      assertNotEquals(null, invcGen);
      bllSpec = new GnucashVendorBillImpl(invcGen);
      assertNotEquals(null, bllSpec);

      assertEquals("https://my.vendor.bill.link.01", invcGen.getURL());
      assertEquals(invcGen.getURL(), bllSpec.getURL());
  }
}
