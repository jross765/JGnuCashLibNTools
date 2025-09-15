package org.gnucash.api.read;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashGenerInvoiceEntry_Action {

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashGenerInvoiceEntry_Action.class);
	}

	@Before
	public void initialize() throws Exception {
		// ::EMPTY
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		assertEquals("HOURS", GnuCashGenerInvoiceEntry.Action.HOURS.toString());
	}
	
	@Test
	public void test02() throws Exception {
		assertEquals("Hours",   GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.ENGLISH));
		assertEquals("Heures",  GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.FRENCH));
		assertEquals("Stunden", GnuCashGenerInvoiceEntry.Action.HOURS.getLocaleString(Locale.GERMAN));
	}

	// ----------------------------

	@Test
	public void test03() throws Exception {
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS,    GnuCashGenerInvoiceEntry.Action.valueOf("HOURS"));
		assertEquals(GnuCashGenerInvoiceEntry.Action.JOB,      GnuCashGenerInvoiceEntry.Action.valueOf("JOB"));
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, GnuCashGenerInvoiceEntry.Action.valueOf("MATERIAL"));
	}
	
	@Test
	public void test04() throws Exception {
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS,    GnuCashGenerInvoiceEntry.Action.valueOff("INVC_ENTR_ACTION_HOURS"));
		assertEquals(GnuCashGenerInvoiceEntry.Action.JOB,      GnuCashGenerInvoiceEntry.Action.valueOff("INVC_ENTR_ACTION_JOB"));
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, GnuCashGenerInvoiceEntry.Action.valueOff("INVC_ENTR_ACTION_MATERIAL"));
		
		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOff(null));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOff(""));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOff("  "));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
	}
	
	@Test
	public void test05() throws Exception {
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, GnuCashGenerInvoiceEntry.Action.valueOfff("Hours",   Locale.ENGLISH));
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, GnuCashGenerInvoiceEntry.Action.valueOfff("Heures",  Locale.FRENCH));
		assertEquals(GnuCashGenerInvoiceEntry.Action.HOURS, GnuCashGenerInvoiceEntry.Action.valueOfff("Stunden", Locale.GERMAN));

		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, GnuCashGenerInvoiceEntry.Action.valueOfff("Material",           Locale.ENGLISH));
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, GnuCashGenerInvoiceEntry.Action.valueOfff("Matières premières", Locale.FRENCH));
		assertEquals(GnuCashGenerInvoiceEntry.Action.MATERIAL, GnuCashGenerInvoiceEntry.Action.valueOfff("Material",           Locale.GERMAN));

		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOfff(null));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOfff(""));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(null, GnuCashGenerInvoiceEntry.Action.valueOfff("   "));
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
	}
}
