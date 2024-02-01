package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashFileImpl {
    private GnucashFileImpl gcshFile = null;
    private GCshFileStats gcshFileStats = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestGnucashFileImpl.class);
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
	    gcshFile = new GnucashFileImpl(gcshFileStream);
	} catch (Exception exc) {
	    System.err.println("Cannot parse GnuCash file");
	    exc.printStackTrace();
	}

	gcshFileStats = new GCshFileStats(gcshFile);
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
	assertEquals(ConstTest.Stats.NOF_ACCT, gcshFileStats.getNofEntriesAccounts(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test02() throws Exception {
	assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_TRX, gcshFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test03() throws Exception {
	assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.RAW));
	// This one is an exception:
	// assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
	// gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_TRX_SPLT,
		gcshFileStats.getNofEntriesTransactionSplits(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test04() throws Exception {
	assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_INVC, gcshFileStats.getNofEntriesGenerInvoices(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test05() throws Exception {
	assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
		gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
		gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_INVC_ENTR,
		gcshFileStats.getNofEntriesGenerInvoiceEntries(GCshFileStats.Type.CACHE));
    }

    // ------------------------------

    @Test
    public void test06() throws Exception {
	assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_CUST, gcshFileStats.getNofEntriesCustomers(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test07() throws Exception {
	assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_VEND, gcshFileStats.getNofEntriesVendors(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test08() throws Exception {
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_EMPL, gcshFileStats.getNofEntriesEmployees(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test09() throws Exception {
	assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_JOB, gcshFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));
    }

    // ------------------------------

    @Test
    public void test10() throws Exception {
	// CAUTION: This one is an exception:
	// There is one additional commoditiy object on the "raw" level:
	// the "template".
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL + 1, gcshFileStats.getNofEntriesCommodities(GCshFileStats.Type.RAW));
	// ::CHECK ???
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL - 1, gcshFileStats.getNofEntriesCommodities(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, gcshFileStats.getNofEntriesCommodities(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test11() throws Exception {
	assertEquals(ConstTest.Stats.NOF_PRC, gcshFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_PRC, gcshFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_PRC, gcshFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));
    }

    // ------------------------------

    @Test
    public void test12() throws Exception {
	assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshFileStats.getNofEntriesTaxTables(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshFileStats.getNofEntriesTaxTables(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_TAXTAB, gcshFileStats.getNofEntriesTaxTables(GCshFileStats.Type.CACHE));
    }

    @Test
    public void test13() throws Exception {
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));
    }

    // -----------------------------------------------------------------

    @Test
    public void test20() {
	assertEquals("EUR", gcshFile.getDefaultCurrencyID());
    }

    @Test
    public void test21() {
	// [counter_formats, counters, features, options, remove-color-not-set-slots]
	assertEquals(5, gcshFile.getUserDefinedAttributeKeys().size());
	assertEquals("counter_formats", gcshFile.getUserDefinedAttributeKeys().toArray()[0]);
	assertEquals("counters", gcshFile.getUserDefinedAttributeKeys().toArray()[1]);
	assertEquals("features", gcshFile.getUserDefinedAttributeKeys().toArray()[2]);
	assertEquals("options", gcshFile.getUserDefinedAttributeKeys().toArray()[3]);
	assertEquals("remove-color-not-set-slots", gcshFile.getUserDefinedAttributeKeys().toArray()[4]);
    }

    @Test
    public void test22() {
	assertEquals("3", gcshFile.getUserDefinedAttribute("counters.gncCustomer"));
	assertEquals("f", gcshFile.getUserDefinedAttribute("options.Accounts.Use Trading Accounts"));
	assertEquals(null, gcshFile.getUserDefinedAttribute("options.Business.Company Fax Number"));
	assertEquals("83b1859fd415421cb24f8c72eb755fcc",
		gcshFile.getUserDefinedAttribute("options.Business.Default Customer TaxTable"));
	assertEquals("true", gcshFile.getUserDefinedAttribute("remove-color-not-set-slots"));
    }

}
