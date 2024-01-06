package org.gnucash.api.read.impl.hlp;

import java.util.Iterator;
import java.util.List;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncTransaction.TrnSplits.TrnSplit;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Raw implements FileStats {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Raw.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile = null;
    
    // ---------------------------------------------------------------
    
    public FileStats_Raw(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
    }

    // ---------------------------------------------------------------

    @Override
    public int getNofEntriesAccounts() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncAccount ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesTransactions() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncTransaction ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesTransactionSplits() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncTransaction ) {
		GncTransaction trx = (GncTransaction) bookElement;
		for ( TrnSplit splt : trx.getTrnSplits().getTrnSplit() ) {
		    result++;
		}
	    }
	}
	
	return result;
    }

    // ----------------------------
    
    @Override
    public int getNofEntriesGenerInvoices() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncInvoice ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesGenerInvoiceEntries() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncEntry ) {
		result++;
	    }
	}
	
	return result;
    }

    // ----------------------------
    
    @Override
    public int getNofEntriesCustomers() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncCustomer ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesVendors() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncVendor ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesEmployees() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncEmployee ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesGenerJobs() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncJob ) {
		result++;
	    }
	}
	
	return result;
    }

    // ----------------------------

    @Override
    public int getNofEntriesCommodities() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncCommodity ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesPrices() {
        return getPriceDB().getPrice().size();
    }

    // ----------------------------

    @Override
    public int getNofEntriesTaxTables() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncTaxTable ) {
		result++;
	    }
	}
	
	return result;
    }

    @Override
    public int getNofEntriesBillTerms() {
	int result = 0;
	
	for (Iterator<Object> iter = gcshFile.getRootElement().getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if ( bookElement instanceof GncGncBillTerm ) {
		result++;
	    }
	}
	
	return result;
    }

    // ---------------------------------------------------------------

    private GncPricedb getPriceDB() {
	List<Object> bookElements = gcshFile.getRootElement().getGncBook().getBookElements();
	for ( Object bookElement : bookElements ) {
	    if ( bookElement instanceof GncPricedb ) {
		return (GncPricedb) bookElement;
	    } 
	}
	
	return null; // Compiler happy
    }

}
