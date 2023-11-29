package org.gnucash.api.read.impl.hlp;

import java.util.Collection;
import java.util.LinkedList;

import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileInvoiceManager_Employee {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);
    
    // ---------------------------------------------------------------
   
    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashEmployeeVoucher> getVouchers_direct(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getVouchers_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashEmployeeVoucher> getPaidVouchers_direct(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : invcMgr.getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidVouchers_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashEmployeeVoucher> getUnpaidVouchers_direct(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : invcMgr.getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidVouchers_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
		}
	    }
	}

	return retval;
    }

}
