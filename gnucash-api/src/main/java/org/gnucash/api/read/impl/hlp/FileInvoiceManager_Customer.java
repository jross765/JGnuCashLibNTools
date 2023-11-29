package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.LinkedList;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);
    
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
    public static Collection<GnucashCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getInvoices_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashJobInvoice> getInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getInvoices() ) {
		retval.add(jobInvc);
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
    public static Collection<GnucashCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidInvoices_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
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
    public static Collection<GnucashJobInvoice> getPaidInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getPaidInvoices() ) {
		retval.add(jobInvc);
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
    public static Collection<GnucashCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidInvoices_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
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
    public static Collection<GnucashJobInvoice> getUnpaidInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
		retval.add(jobInvc);
	    }
	}

	return retval;
    }

}
