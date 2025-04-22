package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.aux.TestGCshAccountLotImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashTransactionSplitImpl {
	public static final GCshID ACCT_1_ID = TestGnuCashAccountImpl.ACCT_1_ID;
	public static final GCshID ACCT_8_ID = TestGnuCashAccountImpl.ACCT_8_ID;

	public static final GCshID TRX_4_ID = new GCshID("cc9fe6a245df45ba9b494660732a7755");
	public static final GCshID TRX_5_ID = new GCshID("4307689faade47d8aab4db87c8ce3aaf");

	public static final GCshID TRXSPLT_1_ID = new GCshID("b6a88c1d918e465892488c561e02831a");
	public static final GCshID TRXSPLT_2_ID = new GCshID("c3ae14400ec843f9bf63f5ef69a31528");

	public static final GCshID ACCTLOT_1_ID = TestGCshAccountLotImpl.ACCTLOT_1_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashTransactionSplit splt = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashTransactionSplitImpl.class);
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
	public void test01() throws Exception {
		splt = gcshFile.getTransactionSplitByID(TRXSPLT_1_ID);

		assertEquals(TRXSPLT_1_ID, splt.getID());
		assertEquals(TRX_4_ID, splt.getTransactionID());
		assertEquals(ACCT_1_ID, splt.getAccountID());
		assertEquals(null, splt.getAction());
		assertEquals(-2253.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("-2.253,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("-2.253,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(-2253.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("-2.253,00 €", splt.getQuantityFormatted());
		assertEquals("-2.253,00 &euro;", splt.getQuantityFormattedForHTML());
		assertEquals("", splt.getDescription());
		assertEquals(null, splt.getLotID());
		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	@Test
	public void test02() throws Exception {
		splt = gcshFile.getTransactionSplitByID(TRXSPLT_2_ID);

		assertEquals(TRXSPLT_2_ID, splt.getID());
		assertEquals(TRX_5_ID, splt.getTransactionID());
		assertEquals(ACCT_8_ID, splt.getAccountID());
		assertEquals(GnuCashTransactionSplit.Action.BUY, splt.getAction());
		assertEquals(1875.00, splt.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("1.875,00 €", splt.getValueFormatted()); // ::TODO: locale-specific!
		assertEquals("1.875,00 &euro;", splt.getValueFormattedForHTML());
		assertEquals(15.00, splt.getQuantity().doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals("15 EURONEXT:SAP", splt.getQuantityFormatted());
		assertEquals("15 EURONEXT:SAP", splt.getQuantityFormattedForHTML());
		assertEquals("", splt.getDescription());
		assertEquals(ACCTLOT_1_ID, splt.getLotID());
		assertEquals(null, splt.getUserDefinedAttributeKeys());
	}

	@Test
	public void test03() throws Exception {
		// Works only in German locale:
		// assertEquals("Rechnung",
		// GnuCashTransactionSplit.Action.INVOICE.getLocaleString());

		assertEquals("Bill", GnuCashTransactionSplit.Action.BILL.getLocaleString(Locale.ENGLISH));
		assertEquals("Lieferantenrechnung", GnuCashTransactionSplit.Action.BILL.getLocaleString(Locale.GERMAN));
		assertEquals("Facture fournisseur", GnuCashTransactionSplit.Action.BILL.getLocaleString(Locale.FRENCH));
	}

	// redundant:
	//  @Test
	//  public void test04() throws Exception
	//  {
	//    assertEquals(ConstTest.NOF_TRX_SPLT, ((FileStats) gcshFile).getNofEntriesTransactionSplits());
	//  }

}
