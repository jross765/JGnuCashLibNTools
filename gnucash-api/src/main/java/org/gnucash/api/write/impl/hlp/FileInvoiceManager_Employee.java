package org.gnucash.api.write.impl.hlp;

import java.util.Collection;
import java.util.LinkedList;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileInvoiceManager_Employee {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileInvoiceManager_Employee.class);
    
    // ---------------------------------------------------------------
   
    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws TaxTableNotFoundException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashWritableEmployeeVoucher> getVouchers(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableEmployeeVoucher> retval = new LinkedList<GnucashWritableEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : invcMgr.getGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
		try {
		    GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblVch);
		} catch (Exception e) {
		    LOGGER.error("getVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
		}
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashWritableEmployeeVoucher> getPaidVouchers(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableEmployeeVoucher> retval = new LinkedList<GnucashWritableEmployeeVoucher>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getPaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
		try {
		    GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblVch);
		} catch (Exception e) {
		    LOGGER.error("getPaidVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
		}
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public static Collection<GnucashWritableEmployeeVoucher> getUnpaidVouchers(final FileInvoiceManager invcMgr, final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	Collection<GnucashWritableEmployeeVoucher> retval = new LinkedList<GnucashWritableEmployeeVoucher>();

	for ( GnucashWritableGenerInvoice invc : invcMgr.getUnpaidWritableGenerInvoices() ) {
	    if ( invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getID()) ) {
		try {
		    GnucashWritableEmployeeVoucherImpl wrtblVch = new GnucashWritableEmployeeVoucherImpl((GnucashWritableGenerInvoiceImpl) invc);
		    retval.add(wrtblVch);
		} catch (Exception e) {
		    LOGGER.error("getUnpaidVouchers: Cannot instantiate GnucashWritableEmployeeVoucherImpl");
		}
	    }
	}

	return retval;
    }

}
