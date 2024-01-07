package org.gnucash.api.write.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableJobInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Job {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Job.class);
    
    // ---------------------------------------------------------------
    
    public static Collection<GnucashWritableJobInvoice> getInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    GnucashWritableJobInvoiceImpl wrtblInvc = new GnucashWritableJobInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (WrongInvoiceTypeException e) {
		    LOGGER.error("getInvoices: Cannot instantiate GnucashWritableJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    public static Collection<GnucashWritableJobInvoice> getPaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    GnucashWritableJobInvoiceImpl wrtblInvc = new GnucashWritableJobInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidInvoices: Cannot instantiate GnucashWritableJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    public static Collection<GnucashWritableJobInvoice> getUnpaidInvoices(final FileInvoiceManager invcMgr, final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableJobInvoice> retval = new ArrayList<GnucashWritableJobInvoice>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getID()) ) {
		try {
		    GnucashWritableJobInvoiceImpl wrtblInvc = new GnucashWritableJobInvoiceImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblInvc);
		} catch (WrongInvoiceTypeException e) {
		    LOGGER.error("getUnpaidInvoices: Cannot instantiate GnucashWritableJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }
   
}
