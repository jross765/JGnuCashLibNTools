package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashGenerInvoiceEntry> invoiceEntryID2invoiceEntry;
    
    // ---------------------------------------------------------------
    
    public FileInvoiceEntryManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	invoiceEntryID2invoiceEntry = new HashMap<GCshID, GnucashGenerInvoiceEntry>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncEntry)) {
		continue;
	    }
	    GncV2.GncBook.GncGncEntry jwsdpInvcEntr = (GncV2.GncBook.GncGncEntry) bookElement;

	    try {
		GnucashGenerInvoiceEntry invcEntr = createGenerInvoiceEntry(jwsdpInvcEntr);
		invoiceEntryID2invoiceEntry.put(invcEntr.getId(), invcEntr);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal (generic) Invoice-Entry-Entry with id="
			+ jwsdpInvcEntr.getEntryGuid().getValue(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in (generic) invoice-entry map: " + invoiceEntryID2invoiceEntry.size());
    }

    /**
     * @param jwsdpInvcEntr the JWSDP-peer (parsed xml-element) to fill our object
     *                      with
     * @return the new GnucashInvoiceEntry to wrap the given jaxb-object.
     */
    protected GnucashGenerInvoiceEntry createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	GnucashGenerInvoiceEntry entr = new GnucashGenerInvoiceEntryImpl(jwsdpInvcEntr, gcshFile);
	return entr;
    }

    // ---------------------------------------------------------------

    public void addInvcEntry(GnucashGenerInvoiceEntry entr) {
	invoiceEntryID2invoiceEntry.put(entr.getId(), entr);
    }

    public void removeInvcEntry(GnucashGenerInvoiceEntry entr) {
	invoiceEntryID2invoiceEntry.remove(entr.getId());
    }

    /**
     * @see GnucashFile#getGenerInvoiceByID(java.lang.String)
     */
    public GnucashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshID id) {
	if (invoiceEntryID2invoiceEntry == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashGenerInvoiceEntry retval = invoiceEntryID2invoiceEntry.get(id);
	if (retval == null) {
	    LOGGER.error("No (generic) Invoice-Entry with id '" + id + "'. " + 
	                 "We know " + invoiceEntryID2invoiceEntry.size() + " accounts.");
	}

	return retval;
    }

    /**
     * @see GnucashFile#getGenerInvoices()
     */
    public Collection<GnucashGenerInvoiceEntry> getGenerInvoiceEntries() {

	Collection<GnucashGenerInvoiceEntry> c = invoiceEntryID2invoiceEntry.values();

	ArrayList<GnucashGenerInvoiceEntry> retval = new ArrayList<GnucashGenerInvoiceEntry>(c);
	Collections.sort(retval);

	return retval;
    }

    // ---------------------------------------------------------------

    public int getNofEntriesGenerInvoiceEntriesMap() {
	return invoiceEntryID2invoiceEntry.size();
    }

}
