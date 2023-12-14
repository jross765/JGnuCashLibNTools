package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceEntryManager extends org.gnucash.api.read.impl.hlp.FileInvoiceEntryManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceEntryManager.class);
    
    // ---------------------------------------------------------------
    
    public FileInvoiceEntryManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @param jwsdpInvcEntr the xml-object to represent in the entry.
     * @return a new invoice-entry, already registered with this file.
     * @see FileInvoiceEntryManager#createGenerInvoiceEntry(GncV2.GncBook.GncGncEntry)
     */
    @Override
    protected GnucashGenerInvoiceEntryImpl createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	GnucashWritableGenerInvoiceEntryImpl entr = new GnucashWritableGenerInvoiceEntryImpl(jwsdpInvcEntr, (GnucashWritableFileImpl) gcshFile);
	LOGGER.debug("Generated new writable generic invoice entry: " + entr.getID());
	return entr;
    }

}
