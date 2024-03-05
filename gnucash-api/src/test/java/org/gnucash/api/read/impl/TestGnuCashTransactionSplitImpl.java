package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Locale;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashTransactionSplitImpl {
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
