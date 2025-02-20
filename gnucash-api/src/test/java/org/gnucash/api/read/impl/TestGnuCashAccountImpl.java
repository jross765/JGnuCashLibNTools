package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashAccountImpl {
    public static final GCshID ROOT_ACCT_ID = new GCshID("14305dc80e034834b3f531696d81b493"); // Root Account

    public static final GCshID ACCT_1_ID = new GCshID("bbf77a599bd24a3dbfec3dd1d0bb9f5c"); // Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa
    public static final GCshID ACCT_2_ID = new GCshID("cc2c4709633943c39293bfd73de88c9b"); // Root Account:Aktiva:Depots:Depot RaiBa
    public static final GCshID ACCT_3_ID = new GCshID("5008258df86243ee86d37dee64327c27"); // Root Account:Fremdkapital
    public static final GCshID ACCT_4_ID = new GCshID("68a4c19f9a8c48909fc69d0dc18c37a6"); // Root Account:Fremdkapital:Lieferanten:Lieferfanto
    public static final GCshID ACCT_5_ID = new GCshID("7e223ee2260d4ba28e8e9e19ce291f43"); // Root Account:Aktiva:Forderungen:Unfug_Quatsch
    public static final GCshID ACCT_6_ID = new GCshID("ebc834e7f20e4be38f445d655142d6b1"); // Root Account:Anfangsbestand
    public static final GCshID ACCT_7_ID = new GCshID("d49554f33a0340bdb6611a1ab5575998"); // Root Account:Aktiva:Depots:Depot RaiBa:DE0007100000 Mercedes-Benz
    public static final GCshID ACCT_8_ID = new GCshID("b3741e92e3b9475b9d5a2dc8254a8111"); // Root Account:Aktiva:Depots:Depot RaiBa:DE0007164600 SAP

    // -----------------------------------------------------------------

    private GnuCashFile gcshFile = null;
    private GnuCashAccount acct = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
    	return new JUnit4TestAdapter(TestGnuCashAccountImpl.class);
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
    }

    // -----------------------------------------------------------------

    @Test
    public void test01_1() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_1_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_1_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.BANK, acct.getType());
    	assertEquals("Giro RaiBa", acct.getName());
    	assertEquals("Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa", acct.getQualifiedName());
    	assertEquals("Girokonto 1", acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

    	assertEquals(9175.31, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(9175.31, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	List<GnuCashTransaction> trxList = acct.getTransactions();
    	// Collections.sort(trxList, Comparator.reverseOrder()); // not necessary
    	assertEquals(10, trxList.size());
    	assertEquals("568864bfb0954897ab8578db4d27372f", trxList.get(0).getID().toString());
    	assertEquals("cc9fe6a245df45ba9b494660732a7755", trxList.get(1).getID().toString());
    	assertEquals("4307689faade47d8aab4db87c8ce3aaf", trxList.get(2).getID().toString());
    	assertEquals("29557cfdf4594eb68b1a1b710722f991", trxList.get(3).getID().toString());
    	assertEquals("67796d4f7c924c1da38f7813dbc3a99d", trxList.get(4).getID().toString());
    	assertEquals("18a45dfc8a6868c470438e27d6fe10b2", trxList.get(5).getID().toString());

    	List<GCshAccountLot> lotList = acct.getLots();
    	// Collections.sort(trxList, Comparator.reverseOrder()); // not necessary
    	assertEquals(null, lotList);
    	// assertEquals(1, lotList.size());
    	// assertEquals("xyz", lotList.get(0).getID().toString());
    }

    @Test
    public void test01_2() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_2_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_2_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.ASSET, acct.getType());
    	assertEquals("Depot RaiBa", acct.getName());
    	assertEquals("Root Account:Aktiva:Depots:Depot RaiBa", acct.getQualifiedName());
    	assertEquals("Aktiendepot 1", acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals("7ee6fe4de6db46fd957f3513c9c6f983", acct.getParentAccountID().toString());

    	// ::TODO
    	assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(2978.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	// ::TODO
    	assertEquals(0, acct.getTransactions().size());
    	//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID());
    	//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getID());

    	assertEquals(null, acct.getLots());
    }

    @Test
    public void test01_3() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_3_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_3_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.LIABILITY, acct.getType());
    	assertEquals("Fremdkapital", acct.getName());
    	assertEquals("Root Account:Fremdkapital", acct.getQualifiedName());
    	assertEquals("alle Verbindlichkeiten", acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals(ROOT_ACCT_ID, acct.getParentAccountID());

    	List<GnuCashAccount> acctList = acct.getChildren();
    	assertEquals(3, acctList.size());
    	assertEquals("e5523198bef94e2c89f0a42fa1e27e42", acctList.get(0).getID().toString());
    	assertEquals("a6d76c8d72764905adecd78d955d25c0", acctList.get(1).getID().toString());
    	assertEquals("1a5b06dada56466197edbd15e64fd425", acctList.get(2).getID().toString());

    	assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	// ::CHECK: Should'nt the value in the following assert be positive
    	// (that's how it is displayed in GnuCacsh, after all, at least with
    	// standard settings).
    	assertEquals(-289.92, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	assertEquals(0, acct.getTransactions().size());

    	assertEquals(null, acct.getLots());
    }

    @Test
    public void test01_4() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_4_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_4_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.PAYABLE, acct.getType());
    	assertEquals("Lieferfanto", acct.getName());
    	assertEquals("Root Account:Fremdkapital:Lieferanten:Lieferfanto", acct.getQualifiedName());
    	assertEquals(null, acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals("a6d76c8d72764905adecd78d955d25c0", acct.getParentAccountID().toString());

    	// ::TODO
    	assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	// ::TODO
    	assertEquals(2, acct.getTransactions().size());
    	assertEquals("aa64d862bb5e4d749eb41f198b28d73d", acct.getTransactions().get(0).getID().toString());
    	assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(1).getID().toString());

    	List<GCshAccountLot> lotList = acct.getLots();
    	assertEquals(1, lotList.size());
    	assertEquals("ae4de9f6ba2941af94595a6b161795c7", lotList.get(0).getID().toString());
    	assertEquals("Lieferantenrechnung 2740921", lotList.get(0).getTitle());
    	// assertEquals(null, lotList.get(0).getNotes());
    }

    @Test
    public void test01_5() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_5_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_5_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.RECEIVABLE, acct.getType());
    	assertEquals("Unfug_Quatsch", acct.getName());
    	assertEquals("Root Account:Aktiva:Forderungen:Unfug_Quatsch", acct.getQualifiedName());
    	assertEquals(null, acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals("74401ce4880c4f4487c4301027a71bde", acct.getParentAccountID().toString());

    	assertEquals(709.95, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(709.95, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	assertEquals(4, acct.getTransactions().size());
    	assertEquals("c97032ba41684b2bb5d1391c9d7547e9", acct.getTransactions().get(0).getID().toString());
    	assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getID().toString());
    	assertEquals("9e066e5f3081485ab08539e41bf85495", acct.getTransactions().get(2).getID().toString());
    	assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(3).getID().toString());

    	List<GCshAccountLot> lotList = acct.getLots();
    	// Collections.sort(trxList, Comparator.reverseOrder()); // not necessary
    	assertEquals(2, lotList.size());
    	// assertEquals(1, lotList.size());
    	// assertEquals("xyz", lotList.get(0).getID().toString());
    }

    @Test
    public void test01_6() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_6_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_6_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.EQUITY, acct.getType());
    	assertEquals("Anfangsbestand", acct.getName());
    	assertEquals("Root Account:Anfangsbestand", acct.getQualifiedName());
    	assertEquals("Anfangsbestand", acct.getDescription());
    	assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());

    	assertEquals(ROOT_ACCT_ID, acct.getParentAccountID());

    	assertEquals(0.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(0.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	assertEquals(0, acct.getTransactions().size());

    	assertEquals(null, acct.getLots());
    }

    @Test
    public void test01_7() throws Exception {
    	acct = gcshFile.getAccountByID(ACCT_7_ID);
    	assertNotEquals(null, acct);

    	assertEquals(ACCT_7_ID, acct.getID());
    	assertEquals(GnuCashAccount.Type.STOCK, acct.getType());
    	assertEquals("DE0007100000 Mercedes-Benz", acct.getName());
    	assertEquals("Root Account:Aktiva:Depots:Depot RaiBa:DE0007100000 Mercedes-Benz", acct.getQualifiedName());
    	assertEquals("Mercedes-Benz Group AG", acct.getDescription());
    	assertEquals("EURONEXT:MBG", acct.getCmdtyCurrID().toString());

    	assertEquals(ACCT_2_ID, acct.getParentAccountID());

    	assertEquals(200.00, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
    	assertEquals(200.00, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    	assertEquals(2, acct.getTransactions().size());
    	assertEquals("cc9fe6a245df45ba9b494660732a7755", acct.getTransactions().get(0).getID().toString());

    	assertEquals(null, acct.getLots());
    }
}
