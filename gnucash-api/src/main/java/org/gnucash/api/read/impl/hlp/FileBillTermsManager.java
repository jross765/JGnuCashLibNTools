package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBillTermsManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileBillTermsManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshID, GCshBillTerms> bllTrmMap = null;

	// ---------------------------------------------------------------

	public FileBillTermsManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		bllTrmMap = new HashMap<GCshID, GCshBillTerms>();

		List<Object> bookElements = pRootElement.getGncBook().getBookElements();
		for ( Object bookElement : bookElements ) {
			if ( !(bookElement instanceof GncGncBillTerm) ) {
				continue;
			}
			GncGncBillTerm jwsdpPeer = (GncGncBillTerm) bookElement;
			GCshBillTermsImpl billTerms = new GCshBillTermsImpl(jwsdpPeer, gcshFile);
			bllTrmMap.put(billTerms.getID(), billTerms);
		}

		LOGGER.debug("init: No. of entries in bill terms map: " + bllTrmMap.size());
	}

	protected GCshBillTermsImpl createBillTerms(final GncGncBillTerm jwsdpBllTrm) {
		GCshBillTermsImpl bllTrm = new GCshBillTermsImpl(jwsdpBllTrm, gcshFile);
		LOGGER.debug("Generated new bill terms: " + bllTrm.getID());
		return bllTrm;
	}

	// ---------------------------------------------------------------

	public GCshBillTerms getBillTermsByID(final GCshID bllTrmID) {
		if ( bllTrmID == null ) {
			throw new IllegalArgumentException("argument <bllTrmID> is null");
		}
		
		if ( ! bllTrmID.isSet() ) {
			throw new IllegalArgumentException("argument <bllTrmID> is not set");
		}
		
		if ( bllTrmMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GCshBillTerms retval = bllTrmMap.get(bllTrmID);
		if ( retval == null ) {
			LOGGER.error("getBillTermsByID: No bill term with ID '" + bllTrmID + "'. " + "We know " + bllTrmMap.size() + " bill terms.");
		}
		
		return retval;
	}

	public GCshBillTerms getBillTermsByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		if ( bllTrmMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		for ( GCshBillTerms billTerms : bllTrmMap.values() ) {
			if ( billTerms.getName().equals(name) ) {
				return billTerms;
			}
		}

		return null;
	}

	public Collection<GCshBillTerms> getBillTerms() {
		if ( bllTrmMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(bllTrmMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesBillTermsMap() {
		return bllTrmMap.size();
	}

}
