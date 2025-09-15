package org.gnucash.api.read;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashTransactionSplit_Action {

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashTransactionSplit_Action.class);
	}

	@Before
	public void initialize() throws Exception {
		// ::EMPTY
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		assertEquals("CREDIT", GnuCashTransactionSplit.Action.CREDIT.toString());
	}
	
	@Test
	public void test02() throws Exception {
		assertEquals("Credit", GnuCashTransactionSplit.Action.CREDIT.getLocaleString(Locale.ENGLISH));
		assertEquals("Crédit", GnuCashTransactionSplit.Action.CREDIT.getLocaleString(Locale.FRENCH));
		assertEquals("Haben",  GnuCashTransactionSplit.Action.CREDIT.getLocaleString(Locale.GERMAN));
	}
	
	// ----------------------------
	
	@Test
	public void test03() throws Exception {
		assertEquals(GnuCashTransactionSplit.Action.CREDIT,  GnuCashTransactionSplit.Action.valueOf("CREDIT"));
		assertEquals(GnuCashTransactionSplit.Action.PAYMENT, GnuCashTransactionSplit.Action.valueOf("PAYMENT"));
	}
	
	@Test
	public void test04() throws Exception {
		assertEquals(GnuCashTransactionSplit.Action.CREDIT, GnuCashTransactionSplit.Action.valueOff("TRX_SPLT_ACTION_CREDIT"));
		assertEquals(GnuCashTransactionSplit.Action.PAYMENT, GnuCashTransactionSplit.Action.valueOff("TRX_SPLT_ACTION_PAYMENT"));
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOff(null));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOff(""));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOff("  "));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
	}
	
	@Test
	public void test05() throws Exception {
		assertEquals(GnuCashTransactionSplit.Action.CREDIT, GnuCashTransactionSplit.Action.valueOfff("Credit", Locale.ENGLISH));
		assertEquals(GnuCashTransactionSplit.Action.CREDIT, GnuCashTransactionSplit.Action.valueOfff("Crédit", Locale.FRENCH));
		assertEquals(GnuCashTransactionSplit.Action.CREDIT, GnuCashTransactionSplit.Action.valueOfff("Haben",  Locale.GERMAN));
		
		assertEquals(GnuCashTransactionSplit.Action.PAYMENT, GnuCashTransactionSplit.Action.valueOfff("Pay",     Locale.ENGLISH));
		assertEquals(GnuCashTransactionSplit.Action.PAYMENT, GnuCashTransactionSplit.Action.valueOfff("Payer",   Locale.FRENCH));
		assertEquals(GnuCashTransactionSplit.Action.PAYMENT, GnuCashTransactionSplit.Action.valueOfff("Zahlung", Locale.GERMAN));
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOfff(null));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOfff(""));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashTransactionSplit.Action.valueOfff("   "));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}		
	}
}
