package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager extends org.gnucash.api.read.impl.hlp.FileInvoiceEntryManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);

	// ---------------------------------------------------------------

	public FileInvoiceEntryManager(GnucashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnucashGenerInvoiceEntryImpl createGenerInvoiceEntry(final GncGncEntry jwsdpInvcEntr) {
		GnucashWritableGenerInvoiceEntryImpl entr = new GnucashWritableGenerInvoiceEntryImpl(jwsdpInvcEntr, (GnucashWritableFileImpl) gcshFile);
		LOGGER.debug("createGenerInvoiceEntry: Generated new writable generic invoice entry: " + entr.getID());
		return entr;
	}

}
