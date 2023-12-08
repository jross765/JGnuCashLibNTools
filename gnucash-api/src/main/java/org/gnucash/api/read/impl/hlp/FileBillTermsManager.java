package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBillTermsManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileBillTermsManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GCshBillTerms> bllTrmMap = null;

    // ---------------------------------------------------------------
    
    public FileBillTermsManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	bllTrmMap = new HashMap<GCshID, GCshBillTerms>();

        List<Object> bookElements = pRootElement.getGncBook().getBookElements();
        for (Object bookElement : bookElements) {
            if (!(bookElement instanceof GncV2.GncBook.GncGncBillTerm)) {
                continue;
            }
            GncV2.GncBook.GncGncBillTerm jwsdpPeer = (GncV2.GncBook.GncGncBillTerm) bookElement;
            GCshBillTermsImpl billTerms = new GCshBillTermsImpl(jwsdpPeer);
            bllTrmMap.put(billTerms.getID(), billTerms);
        }

	LOGGER.debug("init: No. of entries in bill terms map: " + bllTrmMap.size());
    }

    /**
     * @param jwsdpCust the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GCshTaxTable to wrap the given JAXB object.
     */
    protected GCshTaxTableImpl createTaxTable(final GncV2.GncBook.GncGncTaxTable jwsdpTaxTab) {
	GCshTaxTableImpl taxTab = new GCshTaxTableImpl(jwsdpTaxTab, gcshFile);
	return taxTab;
    }

    // ---------------------------------------------------------------

    public void addBillTerms(GCshBillTerms bllTrm) {
	bllTrmMap.put(bllTrm.getID(), bllTrm);
	LOGGER.debug("Added bill terms to cache: " + bllTrm.getID());
    }

    public void removeBillTerms(GCshBillTerms bllTrm) {
	bllTrmMap.remove(bllTrm.getID());
	LOGGER.debug("Removed bill terms from cache: " + bllTrm.getID());
    }

    // ---------------------------------------------------------------

    /**
     * @param id ID of a bill terms item
     * @return the identified bill terms item or null
     */
    public GCshBillTerms getBillTermsByID(final GCshID id) {
        if (bllTrmMap == null) {
            throw new IllegalStateException("no root-element loaded");
        }
        
        return bllTrmMap.get(id);
    }

    /**
     * @param name Name of a bill terms item
     * @return the identified bill-terms item or null
     */
    public GCshBillTerms getBillTermsByName(final String name) {
	if (bllTrmMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	for (GCshBillTerms billTerms : bllTrmMap.values()) {
	    if (billTerms.getName().equals(name)) {
		return billTerms;
	    }
	}

	return null;
    }

    /**
     * @return all TaxTables defined in the book
     * @link GnucashTaxTable
     */
    public Collection<GCshBillTerms> getBillTerms() {
        if (bllTrmMap == null) {
          throw new IllegalStateException("no root-element loaded");
        }

        return bllTrmMap.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesBillTermsMap() {
	return bllTrmMap.size();
    }

}
