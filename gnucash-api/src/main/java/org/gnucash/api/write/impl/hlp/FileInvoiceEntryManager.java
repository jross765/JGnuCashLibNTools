package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager extends org.gnucash.api.read.impl.hlp.FileInvoiceEntryManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);

	// ---------------------------------------------------------------

	public FileInvoiceEntryManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashGenerInvoiceEntryImpl createGenerInvoiceEntry(final GncGncEntry jwsdpInvcEntr) {
		GnuCashWritableGenerInvoiceEntryImpl entr = new GnuCashWritableGenerInvoiceEntryImpl(jwsdpInvcEntr, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createGenerInvoiceEntry: Generated new writable generic invoice entry: " + entr.getID());
		return entr;
	}

	// ---------------------------------------------------------------

	public void addGenerInvcEntry(GnuCashGenerInvoiceEntry entr) {
		if ( entr == null ) {
			throw new IllegalArgumentException("argument <entr> is null");
		}
		
		invcEntrMap.put(entr.getID(), entr);
		LOGGER.debug("Added (generic) invoice entry to cache: " + entr.getID());
	}

	public void removeGenerInvcEntry(GnuCashGenerInvoiceEntry entr) {
		if ( entr == null ) {
			throw new IllegalArgumentException("argument <entr> is null");
		}
		
		invcEntrMap.remove(entr.getID());
		LOGGER.debug("Removed (generic) invoice entry from cache: " + entr.getID());
	}

}
