package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager extends org.gnucash.api.read.impl.hlp.FileInvoiceManager 
{

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager.class);
    
    // ---------------------------------------------------------------
    
    public FileInvoiceManager(GnucashWritableFileImpl gcshFile) {
	super(gcshFile);
    }

    // ---------------------------------------------------------------
    
    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FileInvoiceManager#createGenerInvoice(GncV2.GncBook.GncGncInvoice)
     */
    @Override
    protected GnucashGenerInvoiceImpl createGenerInvoice(final GncV2.GncBook.GncGncInvoice jwsdpInvc) {
	GnucashWritableGenerInvoiceImpl invc = new GnucashWritableGenerInvoiceImpl(jwsdpInvc, gcshFile);
	LOGGER.debug("Generated new writable generic invoice: " + invc.getID());
	return invc;
    }

}
