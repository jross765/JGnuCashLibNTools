package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected Map<GCshGenerInvcEntrID, GnuCashGenerInvoiceEntry> invcEntrMap;
    
    // ---------------------------------------------------------------
    
	public FileInvoiceEntryManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		invcEntrMap = new HashMap<GCshGenerInvcEntrID, GnuCashGenerInvoiceEntry>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncEntry) ) {
				continue;
			}
			GncGncEntry jwsdpInvcEntr = (GncGncEntry) bookElement;

			try {
				GnuCashGenerInvoiceEntry invcEntr = createGenerInvoiceEntry(jwsdpInvcEntr);
				invcEntrMap.put(invcEntr.getID(), invcEntr);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal (generic) Invoice-Entry-Entry with id="
						+ jwsdpInvcEntr.getEntryGuid().getValue(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in (generic) invoice-entry map: " + invcEntrMap.size());
	}

	protected GnuCashGenerInvoiceEntryImpl createGenerInvoiceEntry(final GncGncEntry jwsdpInvcEntr) {
		GnuCashGenerInvoiceEntryImpl entr = new GnuCashGenerInvoiceEntryImpl(jwsdpInvcEntr, gcshFile, true);
		LOGGER.debug("Generated new generic invoice entry: " + entr.getID());
		return entr;
	}

	// ---------------------------------------------------------------

	public GnuCashGenerInvoiceEntry getGenerInvoiceEntryByID(final GCshGenerInvcEntrID entrID) {
		if ( entrID == null ) {
			throw new IllegalArgumentException("argument <entrID> is null");
		}
		
		if ( ! entrID.isSet() ) {
			throw new IllegalArgumentException("argument <entrID> is not set");
		}
		
		if ( invcEntrMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		GnuCashGenerInvoiceEntry retval = invcEntrMap.get(entrID);
		if ( retval == null ) {
			LOGGER.error("getGenerInvoiceEntryByID: No (generic) Invoice-Entry with id '" + entrID + "'. " + "We know "
					+ invcEntrMap.size() + " accounts.");
		}

		return retval;
	}

	public List<GnuCashGenerInvoiceEntry> getGenerInvoiceEntries() {

		Collection<GnuCashGenerInvoiceEntry> c = invcEntrMap.values();

		List<GnuCashGenerInvoiceEntry> retval = new ArrayList<GnuCashGenerInvoiceEntry>(c);
		Collections.sort(retval);

		return Collections.unmodifiableList(retval);
	}

	// ---------------------------------------------------------------

	public int getNofEntriesGenerInvoiceEntriesMap() {
		return invcEntrMap.size();
	}

}
