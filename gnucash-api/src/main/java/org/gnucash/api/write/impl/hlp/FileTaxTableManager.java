package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.aux.GCshWritableTaxTableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTaxTableManager extends org.gnucash.api.read.impl.hlp.FileTaxTableManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTaxTableManager.class);

	// ---------------------------------------------------------------

	public FileTaxTableManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GCshTaxTableImpl createTaxTable(final GncGncTaxTable jwsdpTaxTab) {
		GCshWritableTaxTableImpl wrtblTaxTab = new GCshWritableTaxTableImpl(jwsdpTaxTab, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createTaxTab: Generated new writable bill terms: " + wrtblTaxTab.getID());
		return wrtblTaxTab;
	}

	// ---------------------------------------------------------------

	public void addTaxTable(GCshTaxTable taxTab) {
		if ( taxTab == null ) {
			throw new IllegalArgumentException("null tax table given");
		}
		
		taxTabMap.put(taxTab.getID(), taxTab);
		LOGGER.debug("Added tax table to cache: " + taxTab.getID());
	}

	public void removeTaxTable(GCshTaxTable taxTab) {
		if ( taxTab == null ) {
			throw new IllegalArgumentException("null tax table given");
		}
		
		taxTabMap.remove(taxTab.getID());
		LOGGER.debug("Removed tax table from cache: " + taxTab.getID());
	}

}
