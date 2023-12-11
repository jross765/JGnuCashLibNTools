package org.gnucash.api.read.impl.hlp;

// File statistics methods
// (primarily, but not exclusively, for test purposes)
public interface FileStats {

    public int ERROR = -1; // ::MAGIC
    
    // ---------------------------------------------------------------

    int getNofEntriesAccounts();

    int getNofEntriesTransactions();

    int getNofEntriesTransactionSplits();

    // ----------------------------
    
    int getNofEntriesGenerInvoices();

    int getNofEntriesGenerInvoiceEntries();

    // ----------------------------
    
    int getNofEntriesCustomers();

    int getNofEntriesVendors();

    int getNofEntriesEmployees();

    int getNofEntriesGenerJobs();

    // ----------------------------
    
    int getNofEntriesCommodities();
    
    int getNofEntriesPrices();

    // ----------------------------
    
    int getNofEntriesTaxTables();

    int getNofEntriesBillTerms();

}
