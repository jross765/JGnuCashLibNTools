package org.gnucash.read.impl;

// Statistics methods (for test purposes)
public interface GnucashFileStats {

    int getNofEntriesAccountMap();

    int getNofEntriesTransactionMap();

    int getNofEntriesTransactionSplitMap();

    int getNofEntriesGenerInvoiceMap();

    int getNofEntriesGenerInvoiceEntriesMap();

    int getNofEntriesCustomerMap();

    int getNofEntriesVendorMap();

    int getNofEntriesEmployeeMap();

    int getNofEntriesGenerJobMap();

    int getNofEntriesCommodityMap();
    
    // ----------------------------
    
    int getNofEntriesTaxTableMap();

    int getNofEntriesBillTermsMap();

}
