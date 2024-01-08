package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncTaxTable;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTaxTableManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTaxTableManager.class);

	// ---------------------------------------------------------------

	private GnucashFileImpl gcshFile;

	private Map<GCshID, GCshTaxTable> taxTabMap;

	// ---------------------------------------------------------------

	public FileTaxTableManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		taxTabMap = new HashMap<GCshID, GCshTaxTable>();

		List<Object> bookElements = pRootElement.getGncBook().getBookElements();
		for ( Object bookElement : bookElements ) {
			if ( !(bookElement instanceof GncGncTaxTable) ) {
				continue;
			}

			GncGncTaxTable jwsdpPeer = (GncGncTaxTable) bookElement;
			GCshTaxTableImpl taxTab = createTaxTable(jwsdpPeer);
			taxTabMap.put(taxTab.getID(), taxTab);
		}

		LOGGER.debug("init: No. of entries in tax table map: " + taxTabMap.size());
	}

	/**
	 * @param jwsdpCust the JWSDP-peer (parsed xml-element) to fill our object with
	 * @return the new GCshTaxTable to wrap the given JAXB object.
	 */
	protected GCshTaxTableImpl createTaxTable(final GncGncTaxTable jwsdpTaxTab) {
		GCshTaxTableImpl taxTab = new GCshTaxTableImpl(jwsdpTaxTab, gcshFile);
		LOGGER.debug("Generated new tax table: " + taxTab.getID());
		return taxTab;
	}

	// ---------------------------------------------------------------

	public void addTaxTable(GCshTaxTable taxTab) {
		taxTabMap.put(taxTab.getID(), taxTab);
		LOGGER.debug("Added tax table to cache: " + taxTab.getID());
	}

	public void removeTaxTable(GCshTaxTable taxTab) {
		taxTabMap.remove(taxTab.getID());
		LOGGER.debug("Removed tax table from cache: " + taxTab.getID());
	}

	// ---------------------------------------------------------------

	public GCshTaxTable getTaxTableByID(final GCshID id) {
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return taxTabMap.get(id);
	}

	public GCshTaxTable getTaxTableByName(final String name) {
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		for ( GCshTaxTable taxTab : taxTabMap.values() ) {
			if ( taxTab.getName().equals(name) ) {
				return taxTab;
			}
		}

		return null;
	}

	public Collection<GCshTaxTable> getTaxTables() {
		if ( taxTabMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		// return Collections.unmodifiableCollection(new TreeSet<>(taxTabMap.values()));
		return taxTabMap.values();
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTaxTableMap() {
		return taxTabMap.size();
	}

}
