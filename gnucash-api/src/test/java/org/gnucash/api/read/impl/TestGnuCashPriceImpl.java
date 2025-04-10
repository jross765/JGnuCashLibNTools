package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.GnuCashPrice.Type;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashPriceImpl {
	// DE
	// Note the funny parent/child pair.
	public static final GCshID PRC_1_ID = new GCshID("b7fe7eb916164f1d9d43f41262530381"); // MBG/EUR
	public static final GCshID PRC_2_ID = new GCshID("8f2d1e3263aa4efba4a8e0e892c166b3"); // SAP/EUR
	public static final GCshID PRC_3_ID = new GCshID("d2db5e4108b9413aa678045ca66b205f"); // SAP/EUR
	public static final GCshID PRC_4_ID = new GCshID("037c268b47fb46d385360b1c9788a459"); // USD/EUR

	public static final GCshID PRC_10_ID = new GCshID("0f9b0c306c1f490c92f3fb96d03f40c3");
	public static final GCshID PRC_11_ID = new GCshID("f2806739b34a4f55a86d9c83f2061606");
	public static final GCshID PRC_12_ID = new GCshID("232625da7b4b4f55ba5e0e81b6ab4cac");
	public static final GCshID PRC_13_ID = new GCshID("144582489ce24f3d934699b77d634977");
	
	public static final GCshID PRC_14_ID = new GCshID("3d7b1c6ca678483985cda54863f62ef5");
	public static final GCshID PRC_15_ID = new GCshID("d9204b5e5b724562b99ae6d24ac06883");
	public static final GCshID PRC_16_ID = new GCshID("6e651c2d5a934a7998efa3ea0a94f1ee");
	public static final GCshID PRC_17_ID = new GCshID("861b7b820a644f48a9ebc32577ce9720");
	public static final GCshID PRC_18_ID = new GCshID("f011f1d79cb844d88aa21a622ecebba8");
	public static final GCshID PRC_19_ID = new GCshID("7c6ab10fde9943ccbfb1623d58a9448a");
	
	public static final String CMDTY_2_ID = TestGnuCashCommodityImpl.CMDTY_2_ID;
	public static final String CMDTY_2_ISIN = TestGnuCashCommodityImpl.CMDTY_2_ISIN;

	public static final String CMDTY_4_ISIN = "DE000BASF111";
	
	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashPrice prc = null;

	GCshCmdtyID cmdtyID11 = null;
	GCshCmdtyID_Exchange cmdtyID12 = null;

	GCshCmdtyID cmdtyID21 = null;
	GCshCmdtyID_Exchange cmdtyID22 = null;

	GCshCurrID currID1 = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashPriceImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}

		// ---

		cmdtyID11 = new GCshCmdtyID("EURONEXT", "MBG");
		cmdtyID12 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG");

		cmdtyID21 = new GCshCmdtyID("EURONEXT", "SAP");
		cmdtyID22 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "SAP");

		currID1 = new GCshCurrID("USD");
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		Collection<GnuCashPrice> prcColl = gcshFile.getPrices();
		List<GnuCashPrice> prcList = new ArrayList<GnuCashPrice>(prcColl);
		prcList.sort(Comparator.naturalOrder());

		//		System.err.println("=============");
		//		for ( GnuCashPrice prc : prcList ) {
		//			System.err.println(prc.toString());
		//		}
		//		System.err.println("=============");

		assertEquals(ConstTest.Stats.NOF_PRC, prcList.size());
		assertEquals(PRC_10_ID, prcList.get(0).getID());
		assertEquals(PRC_11_ID, prcList.get(1).getID());
		assertEquals(PRC_12_ID, prcList.get(2).getID()); // <-- CAUTION index!
		assertEquals(PRC_13_ID, prcList.get(3).getID()); // <-- CAUTION index!
	}

	// ---------------------------------------------------------------

	@Test
	public void test02_1() throws Exception {
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
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02_2() throws Exception {
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
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02_3() throws Exception {
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
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test02_4() throws Exception {
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
		assertEquals(new FixedPointNumber("100/93").doubleValue(), prc.getValue().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		try {
			GCshCmdtyID dummy = prc.getFromCommodityQualifID(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			GnuCashCommodity dummy = prc.getFromCommodity(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}
	
	// ---------------------------------------------------------------

	@Test
	public void test03_1() throws Exception {
		GCshCmdtyID_Exchange cmdty21ID = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, CMDTY_2_ID);
		prc = gcshFile.getPriceByCmdtyCurrIDDate(cmdty21ID, LocalDate.of(2012, 3, 5));
		assertNotEquals(null, prc);
		assertEquals(PRC_12_ID, prc.getID());
		
		GCshCmdtyID_SecIdType cmdty22ID = new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, CMDTY_2_ISIN);
		prc = gcshFile.getPriceByCmdtyCurrIDDate(cmdty22ID, LocalDate.of(2012, 3, 5));
		assertEquals(null, prc); // sic, cannot be found by ISIN (in this particular case)
	}
	
	@Test
	public void test03_2() throws Exception {
		GCshCmdtyID_SecIdType cmdty4ID = new GCshCmdtyID_SecIdType(GCshCmdtyCurrNameSpace.SecIdType.ISIN, CMDTY_4_ISIN);
		prc = gcshFile.getPriceByCmdtyCurrIDDate(cmdty4ID, LocalDate.of(2023, 4, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_14_ID, prc.getID());
		
		prc = gcshFile.getPriceByCmdtyCurrIDDate(cmdty4ID, LocalDate.of(2023, 7, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_15_ID, prc.getID());
		
		prc = gcshFile.getPriceByCmdtyCurrIDDate(cmdty4ID, LocalDate.of(2023, 10, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_16_ID, prc.getID());
	}
	
	@Test
	public void test04_1() throws Exception {
		GCshCurrID currID = new GCshCurrID("USD");
		prc = gcshFile.getPriceByCurrIDDate(currID, LocalDate.of(2023, 12, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_17_ID, prc.getID());
		
		Currency curr = Currency.getInstance("USD");
		prc = gcshFile.getPriceByCurrDate(curr, LocalDate.of(2023, 12, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_17_ID, prc.getID());
		
		prc = gcshFile.getPriceByCmdtyCurrIDDate(currID, LocalDate.of(2023, 12, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_17_ID, prc.getID());
	}
	
	@Test
	public void test04_2() throws Exception {
		Currency curr = Currency.getInstance("USD");
		prc = gcshFile.getPriceByCurrDate(curr, LocalDate.of(2024, 1, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_18_ID, prc.getID());
		
		prc = gcshFile.getPriceByCurrDate(curr, LocalDate.of(2023, 11, 1));
		assertNotEquals(null, prc);
		assertEquals(PRC_19_ID, prc.getID());
	}
	
	// ---------------------------------------------------------------
	
	// ::TODO
	/*
	 * @Test public void test02_2_2() throws Exception { taxTab =
	 * gcshFile.getPriceByName("USt_Std");
	 * 
	 * assertEquals(TAXTABLE_DE_1_2_ID, taxTab.getID()); assertEquals("USt_Std",
	 * taxTab.getName()); // sic, old name w/o prefix "DE_"
	 * assertEquals(TAXTABLE_DE_1_1_ID, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(19.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test03_1() throws Exception { taxTab =
	 * gcshFile.getPriceByID(TAXTABLE_DE_2_ID);
	 * 
	 * assertEquals(TAXTABLE_DE_2_ID, taxTab.getID()); assertEquals("DE_USt_red",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(7.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test03_2() throws Exception { taxTab =
	 * gcshFile.getPriceByName("DE_USt_red");
	 * 
	 * assertEquals(TAXTABLE_DE_2_ID, taxTab.getID()); assertEquals("DE_USt_red",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(7.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test04_1() throws Exception { taxTab =
	 * gcshFile.getPriceByID(TAXTABLE_FR_1_ID);
	 * 
	 * assertEquals(TAXTABLE_FR_1_ID, taxTab.getID()); assertEquals("FR_TVA_Std",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(20.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test04_2() throws Exception { taxTab =
	 * gcshFile.getPriceByName("FR_TVA_Std");
	 * 
	 * assertEquals(TAXTABLE_FR_1_ID, taxTab.getID()); assertEquals("FR_TVA_Std",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(20.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test05_1() throws Exception { taxTab =
	 * gcshFile.getPriceByID(TAXTABLE_FR_2_ID);
	 * 
	 * assertEquals(TAXTABLE_FR_2_ID, taxTab.getID()); assertEquals("FR_TVA_red",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(10.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 * 
	 * @Test public void test05_2() throws Exception { taxTab =
	 * gcshFile.getPriceByName("FR_TVA_red");
	 * 
	 * assertEquals(TAXTABLE_FR_2_ID, taxTab.getID()); assertEquals("FR_TVA_red",
	 * taxTab.getName()); assertEquals(null, taxTab.getParentID());
	 * 
	 * assertEquals(1, taxTab.getEntries().size()); assertEquals(10.0,
	 * ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAmount().doubleValue(),
	 * ConstTest.DIFF_TOLERANCE ); assertEquals(GCshPriceEntry.TYPE_PERCENT,
	 * ((GCshPriceEntry) taxTab.getEntries().toArray()[0]).getType() );
	 * assertEquals(TAX_ACCT_ID, ((GCshPriceEntry)
	 * taxTab.getEntries().toArray()[0]).getAccountID() ); }
	 */
}
