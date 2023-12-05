package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashGenerInvoiceEntry> invcEntrMap;
    
    // ---------------------------------------------------------------
    
    public FileInvoiceEntryManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	invcEntrMap = new HashMap<GCshID, GnucashGenerInvoiceEntry>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncEntry)) {
		continue;
	    }
	    GncV2.GncBook.GncGncEntry jwsdpInvcEntr = (GncV2.GncBook.GncGncEntry) bookElement;

	    try {
		GnucashGenerInvoiceEntry invcEntr = createGenerInvoiceEntry(jwsdpInvcEntr);
		invcEntrMap.put(invcEntr.getId(), invcEntr);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal (generic) Invoice-Entry-Entry with id="
			+ jwsdpInvcEntr.getEntryGuid().getValue(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in (generic) invoice-entry map: " + invcEntrMap.size());
    }

    /**
     * @param jwsdpInvcEntr the JWSDP-peer (parsed xml-element) to fill our object
     *                      with
     * @return the new GnucashInvoiceEntry to wrap the given jaxb-object.
     */
    protected GnucashGenerInvoiceEntry createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	GnucashGenerInvoiceEntry entr = new GnucashGenerInvoiceEntryImpl(jwsdpInvcEntr, gcshFile, true);
	return entr;
    }

    // ---------------------------------------------------------------

    public void addInvcEntry(GnucashGenerInvoiceEntry entr) {
	invcEntrMap.put(entr.getId(), entr);
    }

    public void removeInvcEntry(GnucashGenerInvoiceEntry entr) {
	invcEntrMap.remove(entr.getId());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerInvoiceByID(java.lang.String)
     */
    public GnucashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshID id) {
	if (invcEntrMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashGenerInvoiceEntry retval = invcEntrMap.get(id);
	if (retval == null) {
	    LOGGER.error("getGenerInvoiceEntryByID: No (generic) Invoice-Entry with id '" + id + "'. " + 
	                 "We know " + invcEntrMap.size() + " accounts.");
	}

	return retval;
    }

    /**
     * @see GnucashFile#getGenerInvoices()
     */
    public Collection<GnucashGenerInvoiceEntry> getGenerInvoiceEntries() {

	Collection<GnucashGenerInvoiceEntry> c = invcEntrMap.values();

	ArrayList<GnucashGenerInvoiceEntry> retval = new ArrayList<GnucashGenerInvoiceEntry>(c);
	Collections.sort(retval);

	return retval;
    }

    // ---------------------------------------------------------------

    public int getNofEntriesGenerInvoiceEntriesMap() {
	return invcEntrMap.size();
    }

}
