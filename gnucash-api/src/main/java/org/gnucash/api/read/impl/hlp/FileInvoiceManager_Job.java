package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Job {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Job.class);
    
    // ---------------------------------------------------------------
    
    /**
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, IllegalArgumentException {
	Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    LOGGER.error("getInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    LOGGER.error("getPaidInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashJobInvoice> retval = new ArrayList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    LOGGER.error("getUnpaidInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }
   
}
