package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.generated.GncCountData;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Counters implements FileStats {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Counters.class);

	// ---------------------------------------------------------------

	private GnucashFileImpl gcshFile = null;

	// ---------------------------------------------------------------

	public FileStats_Counters(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
	}

	// ---------------------------------------------------------------

	@Override
	public int getNofEntriesAccounts() {
		GncCountData obj = findCountDataByType("account");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesTransactions() {
		GncCountData obj = findCountDataByType("transaction");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return ERROR; // n/a
	}

	// ----------------------------

	@Override
	public int getNofEntriesGenerInvoices() {
		GncCountData obj = findCountDataByType("gnc:GncInvoice");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesGenerInvoiceEntries() {
		GncCountData obj = findCountDataByType("gnc:GncEntry");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesCustomers() {
		GncCountData obj = findCountDataByType("gnc:GncCustomer");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesVendors() {
		GncCountData obj = findCountDataByType("gnc:GncVendor");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesEmployees() {
		GncCountData obj = findCountDataByType("gnc:GncEmployee");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesGenerJobs() {
		GncCountData obj = findCountDataByType("gnc:GncJob");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesCommodities() {
		GncCountData obj = findCountDataByType("commodity");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesPrices() {
		GncCountData obj = findCountDataByType("price");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesTaxTables() {
		GncCountData obj = findCountDataByType("gnc:GncTaxTable");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	@Override
	public int getNofEntriesBillTerms() {
		GncCountData obj = findCountDataByType("gnc:GncBillTerm");
		if ( obj == null ) {
			return ERROR;
		} else {
			return obj.getValue();
		}
	}

	// ---------------------------------------------------------------

	private GncCountData findCountDataByType(final String type) {
		for ( GncCountData count : gcshFile.getRootElement().getGncBook().getGncCountData() ) {
			if ( count.getCdType().equals(type) ) {
				return count;
			}
		}

		return null;
	}

}
