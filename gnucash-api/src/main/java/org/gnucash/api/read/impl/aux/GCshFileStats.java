package org.gnucash.api.read.impl.aux;

import java.io.IOException;

import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.hlp.FileStats;
import org.gnucash.api.read.impl.hlp.FileStats_Cache;
import org.gnucash.api.read.impl.hlp.FileStats_Counters;
import org.gnucash.api.read.impl.hlp.FileStats_Raw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshFileStats {
    
    public enum Type {
	RAW,
	COUNTER,
	CACHE
    }
    
    // ---------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshFileStats.class);
    
    // ---------------------------------------------------------------

    private FileStats_Raw      raw; 
    private FileStats_Counters cnt; 
    private FileStats_Cache    che; 

    // ---------------------------------------------------------------
    
    public GCshFileStats(GnuCashFileImpl gcshFile) throws IOException {
	raw = new FileStats_Raw(gcshFile);
	cnt = new FileStats_Counters(gcshFile);
	che = new FileStats_Cache(gcshFile);
    }

    // ---------------------------------------------------------------

    public int getNofEntriesAccounts(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesAccounts();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesAccounts();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesAccounts();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesTransactions(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesTransactions();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesTransactions();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesTransactions();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesTransactionSplits(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesTransactionSplits();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesTransactionSplits();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesTransactionSplits();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    // ----------------------------
    
    public int getNofEntriesGenerInvoices(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesGenerInvoices();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesGenerInvoices();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesGenerInvoices();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesGenerInvoiceEntries(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesGenerInvoiceEntries();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesGenerInvoiceEntries();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesGenerInvoiceEntries();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    // ----------------------------
    
    public int getNofEntriesCustomers(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesCustomers();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesCustomers();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesCustomers();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesVendors(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesVendors();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesVendors();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesVendors();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesEmployees(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesEmployees();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesEmployees();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesEmployees();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesGenerJobs(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesGenerJobs();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesGenerJobs();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesGenerJobs();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    // ----------------------------
    
    public int getNofEntriesCommodities(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesCommodities();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesCommodities();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesCommodities();
	}
	
	return FileStats.ERROR; // Compiler happy
    }
    
    public int getNofEntriesPrices(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesPrices();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesPrices();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesPrices();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    // ----------------------------
    
    public int getNofEntriesTaxTables(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesTaxTables();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesTaxTables();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesTaxTables();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

    public int getNofEntriesBillTerms(Type type) {
	if ( type == Type.RAW ) {
	    return raw.getNofEntriesBillTerms();
	} else if ( type == Type.COUNTER ) {
	    return cnt.getNofEntriesBillTerms();
	} else if ( type == Type.CACHE ) {
	    return che.getNofEntriesBillTerms();
	}
	
	return FileStats.ERROR; // Compiler happy
    }

	// ---------------------------------------------------------------
	
	public boolean equals(GCshFileStats other) {
		if ( other.getNofEntriesAccounts(Type.RAW)     != getNofEntriesAccounts(Type.RAW) ||
			 other.getNofEntriesAccounts(Type.COUNTER) != getNofEntriesAccounts(Type.COUNTER) ||
			 other.getNofEntriesAccounts(Type.CACHE)   != getNofEntriesAccounts(Type.CACHE)) {
			return false;
		}
		
		if ( other.getNofEntriesTransactions(Type.RAW)     != getNofEntriesTransactions(Type.RAW) ||
			 other.getNofEntriesTransactions(Type.COUNTER) != getNofEntriesTransactions(Type.COUNTER) ||
			 other.getNofEntriesTransactions(Type.CACHE)   != getNofEntriesTransactions(Type.CACHE)) {
			return false;
		}
			
		if ( other.getNofEntriesTransactionSplits(Type.RAW)     != getNofEntriesTransactionSplits(Type.RAW) ||
			 other.getNofEntriesTransactionSplits(Type.COUNTER) != getNofEntriesTransactionSplits(Type.COUNTER) ||
			 other.getNofEntriesTransactionSplits(Type.CACHE)   != getNofEntriesTransactionSplits(Type.CACHE)) {
			return false;
		}
				
		if ( other.getNofEntriesGenerInvoices(Type.RAW)     != getNofEntriesGenerInvoices(Type.RAW) ||
			 other.getNofEntriesGenerInvoices(Type.COUNTER) != getNofEntriesGenerInvoices(Type.COUNTER) ||
			 other.getNofEntriesGenerInvoices(Type.CACHE)   != getNofEntriesGenerInvoices(Type.CACHE)) {
			return false;
		}
					
		if ( other.getNofEntriesGenerInvoiceEntries(Type.RAW)     != getNofEntriesGenerInvoiceEntries(Type.RAW) ||
			 other.getNofEntriesGenerInvoiceEntries(Type.COUNTER) != getNofEntriesGenerInvoiceEntries(Type.COUNTER) ||
			 other.getNofEntriesGenerInvoiceEntries(Type.CACHE)   != getNofEntriesGenerInvoiceEntries(Type.CACHE)) {
			return false;
		}
						
		if ( other.getNofEntriesCustomers(Type.RAW)     != getNofEntriesCustomers(Type.RAW) ||
			 other.getNofEntriesCustomers(Type.COUNTER) != getNofEntriesCustomers(Type.COUNTER) ||
			 other.getNofEntriesCustomers(Type.CACHE)   != getNofEntriesCustomers(Type.CACHE)) {
			return false;
		}
					
		if ( other.getNofEntriesVendors(Type.RAW)     != getNofEntriesVendors(Type.RAW) ||
			 other.getNofEntriesVendors(Type.COUNTER) != getNofEntriesVendors(Type.COUNTER) ||
			 other.getNofEntriesVendors(Type.CACHE)   != getNofEntriesVendors(Type.CACHE)) {
			return false;
		}
						
		if ( other.getNofEntriesEmployees(Type.RAW)     != getNofEntriesEmployees(Type.RAW) ||
			 other.getNofEntriesEmployees(Type.COUNTER) != getNofEntriesEmployees(Type.COUNTER) ||
			 other.getNofEntriesEmployees(Type.CACHE)   != getNofEntriesEmployees(Type.CACHE)) {
			return false;
		}
							
		if ( other.getNofEntriesGenerJobs(Type.RAW)     != getNofEntriesGenerJobs(Type.RAW) ||
			 other.getNofEntriesGenerJobs(Type.COUNTER) != getNofEntriesGenerJobs(Type.COUNTER) ||
			 other.getNofEntriesGenerJobs(Type.CACHE)   != getNofEntriesGenerJobs(Type.CACHE)) {
			return false;
		}
								
		if ( other.getNofEntriesCommodities(Type.RAW)     != getNofEntriesCommodities(Type.RAW) ||
			 other.getNofEntriesCommodities(Type.COUNTER) != getNofEntriesCommodities(Type.COUNTER) ||
			 other.getNofEntriesCommodities(Type.CACHE)   != getNofEntriesCommodities(Type.CACHE)) {
			return false;
		}
					
		if ( other.getNofEntriesPrices(Type.RAW)     != getNofEntriesPrices(Type.RAW) ||
			 other.getNofEntriesPrices(Type.COUNTER) != getNofEntriesPrices(Type.COUNTER) ||
			 other.getNofEntriesPrices(Type.CACHE)   != getNofEntriesPrices(Type.CACHE)) {
			return false;
		}
									
		if ( other.getNofEntriesTaxTables(Type.RAW)     != getNofEntriesTaxTables(Type.RAW) ||
			 other.getNofEntriesTaxTables(Type.COUNTER) != getNofEntriesTaxTables(Type.COUNTER) ||
			 other.getNofEntriesTaxTables(Type.CACHE)   != getNofEntriesTaxTables(Type.CACHE)) {
			return false;
		}
						
		if ( other.getNofEntriesBillTerms(Type.RAW)     != getNofEntriesBillTerms(Type.RAW) ||
			 other.getNofEntriesBillTerms(Type.COUNTER) != getNofEntriesBillTerms(Type.COUNTER) ||
			 other.getNofEntriesBillTerms(Type.CACHE)   != getNofEntriesBillTerms(Type.CACHE)) {
			return false;
		}
							
		return true;
	}

}
