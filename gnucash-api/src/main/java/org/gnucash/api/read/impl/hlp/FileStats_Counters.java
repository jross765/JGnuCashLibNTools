package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileStats_Counters implements FileStats {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileStats_Counters.class);

	// ---------------------------------------------------------------

	private GnuCashFileImpl gcshFile = null;

	// ---------------------------------------------------------------

	public FileStats_Counters(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
	}

	// ---------------------------------------------------------------

	@Override
	public int getNofEntriesAccounts() {
		try {
			return gcshFile.getCountDataFor("account");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesAccountLots() {
		return ERROR; // n/a
	}

	@Override
	public int getNofEntriesTransactions() {
		try {
			return gcshFile.getCountDataFor("transaction");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesTransactionSplits() {
		return ERROR; // n/a
	}

	// ----------------------------

	@Override
	public int getNofEntriesGenerInvoices() {
		try {
			return gcshFile.getCountDataFor("gnc:GncInvoice");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesGenerInvoiceEntries() {
		try {
			return gcshFile.getCountDataFor("gnc:GncEntry");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesCustomers() {
		try {
			return gcshFile.getCountDataFor("gnc:GncCustomer");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesVendors() {
		try {
			return gcshFile.getCountDataFor("gnc:GncVendor");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesEmployees() {
		try {
			return gcshFile.getCountDataFor("gnc:GncEmployee");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesGenerJobs() {
		try {
			return gcshFile.getCountDataFor("gnc:GncJob");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesCommodities() {
		try {
			return gcshFile.getCountDataFor("commodity");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesPrices() {
		try {
			return gcshFile.getCountDataFor("price");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	// ----------------------------

	@Override
	public int getNofEntriesTaxTables() {
		try {
			return gcshFile.getCountDataFor("gnc:GncTaxTable");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

	@Override
	public int getNofEntriesBillTerms() {
		try {
			return gcshFile.getCountDataFor("gnc:GncBillTerm");
		} catch ( IllegalArgumentException exc ) {
			return ERROR;
		}
	}

}
