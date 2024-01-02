package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.GCshCmdtyID;
import org.gnucash.api.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashPrice;
import org.gnucash.api.read.GnucashPrice.Type;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashPriceImpl
{
  // DE
  // Note the funny parent/child pair.
  public static final GCshID PRC_1_ID = new GCshID("b7fe7eb916164f1d9d43f41262530381"); // MBG/EUR
  public static final GCshID PRC_2_ID = new GCshID("8f2d1e3263aa4efba4a8e0e892c166b3"); // SAP/EUR
  public static final GCshID PRC_3_ID = new GCshID("d2db5e4108b9413aa678045ca66b205f"); // SAP/EUR
  public static final GCshID PRC_4_ID = new GCshID("037c268b47fb46d385360b1c9788a459"); // USD/EUR

  // -----------------------------------------------------------------
  
  private GnucashFile  gcshFile = null;
  private GnucashPrice    prc = null;
  
  GCshCmdtyID          cmdtyID11 = null;
  GCshCmdtyID_Exchange cmdtyID12 = null;

  GCshCmdtyID          cmdtyID21 = null;
  GCshCmdtyID_Exchange cmdtyID22 = null;
  
  GCshCurrID           currID1   = null;
  
  // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashPriceImpl.class);  
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
    
    // ---
    
    cmdtyID11 = new GCshCmdtyID("EURONEXT", "MBG");
    cmdtyID12 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG");

    cmdtyID21 = new GCshCmdtyID("EURONEXT", "SAP");
    cmdtyID22 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "SAP");
    
    currID1   = new GCshCurrID("USD");
  }

  // -----------------------------------------------------------------

  @Test
  public void test01() throws Exception
  {
      Collection<GnucashPrice> priceList = gcshFile.getPrices();
      
      assertEquals(9, priceList.size());

      // ::TODO: Sort array for predictability
//      Object[] priceArr = priceList.toArray();
//      
//      assertEquals(PRICE_1_ID, ((GCshPrice) priceArr[0]).getID());
//      assertEquals(PRICE_2_ID, ((GCshPrice) priceArr[1]).getID());
//      assertEquals(PRICE_3_ID, ((GCshPrice) priceArr[2]).getID());
  }

  @Test
  public void test02_1() throws Exception
  {
      prc = gcshFile.getPriceByID(PRC_1_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRC_1_ID, prc.getID());
      assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString());
      assertEquals(cmdtyID11.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID12.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID11, prc.getFromCommodityQualifID());
      assertNotEquals(cmdtyID12, prc.getFromCommodityQualifID()); // sic
      assertEquals("Mercedes-Benz Group AG", prc.getFromCommodity().getName());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals(Type.TRANSACTION, prc.getType());
      assertEquals(LocalDate.of(2023, 7, 1), prc.getDate());
      assertEquals(22.53, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      try {
	  GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  String dummy = prc.getFromCurrencyCode(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  GnucashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
  }

  @Test
  public void test02_2() throws Exception
  {
      prc = gcshFile.getPriceByID(PRC_2_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRC_2_ID, prc.getID());
      assertEquals(cmdtyID21.toString(), prc.getFromCmdtyCurrQualifID().toString());
      assertEquals(cmdtyID21.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID22.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID21, prc.getFromCommodityQualifID());
      assertNotEquals(cmdtyID22, prc.getFromCommodityQualifID()); // sic
      assertEquals("SAP SE", prc.getFromCommodity().getName());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals(Type.UNKNOWN, prc.getType());
      assertEquals(LocalDate.of(2023, 7, 20), prc.getDate());
      assertEquals(145.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
      try {
	  GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  String dummy = prc.getFromCurrencyCode(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  GnucashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
  }
  
  @Test
  public void test02_3() throws Exception
  {
      prc = gcshFile.getPriceByID(PRC_3_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRC_3_ID, prc.getID());
      assertEquals(cmdtyID21.toString(), prc.getFromCmdtyCurrQualifID().toString());
      assertEquals(cmdtyID21.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID22.toString(), prc.getFromCommodityQualifID().toString());
      assertEquals(cmdtyID21, prc.getFromCommodityQualifID());
      assertNotEquals(cmdtyID22, prc.getFromCommodityQualifID()); // sic
      assertEquals("SAP SE", prc.getFromCommodity().getName());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals(Type.TRANSACTION, prc.getType());
      assertEquals(LocalDate.of(2023, 7, 18), prc.getDate());
      assertEquals(125.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      try {
	  GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  String dummy = prc.getFromCurrencyCode(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  GnucashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
  }

  @Test
  public void test02_4() throws Exception
  {
      prc = gcshFile.getPriceByID(PRC_4_ID);
      assertNotEquals(null, prc);
      
      assertEquals(PRC_4_ID, prc.getID());
      assertEquals(currID1.toString(), prc.getFromCmdtyCurrQualifID().toString());
      assertEquals(currID1.toString(), prc.getFromCurrencyQualifID().toString());
      assertEquals("USD", prc.getFromCurrencyCode());
      assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
      assertEquals("EUR", prc.getToCurrencyCode());
      assertEquals(null, prc.getType());
      assertEquals(LocalDate.of(2023, 10, 1), prc.getDate());
      assertEquals(new FixedPointNumber("100/93").doubleValue(), 
	           prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      try {
	  GCshCmdtyID dummy = prc.getFromCommodityQualifID(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
      
      try {
	  GnucashCommodity dummy = prc.getFromCommodity(); // illegal call in this context
	  assertEquals(1, 0);
      } catch (Exception exc) {
	  assertEquals(0, 0);
      }
  }

  // ::TODO
  /*
  @Test
  public void test02_2_2() throws Exception
  {
      taxTab = gcshFile.getPriceByName("USt_Std");
      
      assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID());
      assertEquals("USt_Std", taxTab.getName()); // sic, old name w/o prefix "DE_"
      assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(19.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test03_1() throws Exception
  {
      taxTab = gcshFile.getPriceByID(TAXTABLE_DE_2_ID);
      
      assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
      assertEquals("DE_USt_red", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(7.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test03_2() throws Exception
  {
      taxTab = gcshFile.getPriceByName("DE_USt_red");
      
      assertEquals(TAXTABLE_DE_2_ID, taxTab.getID());
      assertEquals("DE_USt_red", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(7.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test04_1() throws Exception
  {
      taxTab = gcshFile.getPriceByID(TAXTABLE_FR_1_ID);
      
      assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
      assertEquals("FR_TVA_Std", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(20.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test04_2() throws Exception
  {
      taxTab = gcshFile.getPriceByName("FR_TVA_Std");
      
      assertEquals(TAXTABLE_FR_1_ID, taxTab.getID());
      assertEquals("FR_TVA_Std", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(20.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test05_1() throws Exception
  {
      taxTab = gcshFile.getPriceByID(TAXTABLE_FR_2_ID);
      
      assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
      assertEquals("FR_TVA_red", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(10.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }

  @Test
  public void test05_2() throws Exception
  {
      taxTab = gcshFile.getPriceByName("FR_TVA_red");
      
      assertEquals(TAXTABLE_FR_2_ID, taxTab.getID());
      assertEquals("FR_TVA_red", taxTab.getName());
      assertEquals(null, taxTab.getParentID());

      assertEquals(1, taxTab.getEntries().size());
      assertEquals(10.0, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAmount().doubleValue(), ConstTest.DIFF_TOLERANCE );
      assertEquals(GCshPriceEntry.TYPE_PERCENT, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
      assertEquals(TAX_ACCT_ID, ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getAccountID() );
  }
  */
}
