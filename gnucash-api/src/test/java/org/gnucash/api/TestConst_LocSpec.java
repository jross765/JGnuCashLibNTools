package org.gnucash.api;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestConst_LocSpec {
	
	// -----------------------------------------------------------------
	
	Locale oldLcl = null;
	
	// -----------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestConst_LocSpec.class);
	}

	@Before
	public void initialize() throws Exception {
		oldLcl = Locale.getDefault(); // important
	}

	@After
	public void resetLocale() throws Exception {
		Locale.setDefault(oldLcl);
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_en() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
		
		assertEquals("Job", Const_LocSpec.getValue("INVC_ENTR_ACTION_JOB"));
		assertEquals("Generated from an invoice. " +
					 "Try unposting the invoice.", Const_LocSpec.getValue("INVC_READ_ONLY_SLOT_TEXT"));
	}

	@Test
	public void test01_fr() throws Exception {
		Locale.setDefault(Locale.FRENCH);
		
		assertEquals("Projet", Const_LocSpec.getValue("INVC_ENTR_ACTION_JOB"));
		assertEquals("Généré depuis une facture. " + 
					 "Essayez de suspendre la facture.", Const_LocSpec.getValue("INVC_READ_ONLY_SLOT_TEXT"));
	}

	@Test
	public void test01_de() throws Exception {
		Locale.setDefault(Locale.GERMAN);
		
		assertEquals("Auftrag", Const_LocSpec.getValue("INVC_ENTR_ACTION_JOB"));
		assertEquals("Aus einer Rechnung erzeugt. " + 
					 "Für Änderungen müssen Sie die Buchung der Rechnung löschen.", Const_LocSpec.getValue("INVC_READ_ONLY_SLOT_TEXT"));
	}

}
