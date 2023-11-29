package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.LinkedList;

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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidInvoices: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }
   
}
