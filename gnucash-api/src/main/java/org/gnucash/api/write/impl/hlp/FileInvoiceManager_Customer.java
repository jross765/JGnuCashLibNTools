package org.gnucash.api.write.impl.hlp;

import java.util.Collection;
import java.util.LinkedList;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Customer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Customer.class);
    
    // ---------------------------------------------------------------
    
    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws TaxTableNotFoundException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableCustomerInvoice> getInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableCustomerInvoice> retval = new LinkedList<GnucashWritableCustomerInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
		try {
		    GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (Exception e) {
		    LOGGER.error("getInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableJobInvoice> getInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, IllegalArgumentException {
	Collection<GnucashWritableJobInvoice> retval = new LinkedList<GnucashWritableJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getInvoices() ) {
		retval.add((GnucashWritableJobInvoice) jobInvc);
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws TaxTableNotFoundException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableCustomerInvoice> getPaidInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableCustomerInvoice> retval = new LinkedList<GnucashWritableCustomerInvoice>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
		try {
		    GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (Exception e) {
		    LOGGER.error("getPaidInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableJobInvoice> getPaidInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashWritableJobInvoice> retval = new LinkedList<GnucashWritableJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getPaidInvoices() ) {
		retval.add((GnucashWritableJobInvoice) jobInvc);
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws TaxTableNotFoundException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableCustomerInvoice> getUnpaidInvoices_direct(final FileInvoiceManager invcMgr, final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableCustomerInvoice> retval = new LinkedList<GnucashWritableCustomerInvoice>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getID()) ) {
		try {
		    GnucashWritableCustomerInvoiceImpl wrtblInvc = new GnucashWritableCustomerInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (Exception e) {
		    LOGGER.error("getUnpaidInvoices_direct: Cannot instantiate GnucashWritableCustomerInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public static Collection<GnucashWritableJobInvoice> getUnpaidInvoices_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashWritableJobInvoice> retval = new LinkedList<GnucashWritableJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
		retval.add((GnucashWritableJobInvoice) jobInvc);
	    }
	}

	return retval;
    }

}
