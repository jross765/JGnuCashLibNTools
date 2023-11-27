package org.gnucash.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.GnucashVendor;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.impl.GnucashFileImpl;
import org.gnucash.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.read.impl.spec.GnucashCustomerInvoiceImpl;
import org.gnucash.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.read.impl.spec.GnucashVendorBillImpl;
import org.gnucash.read.spec.GnucashCustomerInvoice;
import org.gnucash.read.spec.GnucashCustomerJob;
import org.gnucash.read.spec.GnucashEmployeeVoucher;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.GnucashVendorBill;
import org.gnucash.read.spec.GnucashVendorJob;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileGenerInvoiceManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileGenerInvoiceManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashGenerInvoice> invcMap;

    // ---------------------------------------------------------------
    
    public FileGenerInvoiceManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	invcMap = new HashMap<GCshID, GnucashGenerInvoice>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncInvoice)) {
		continue;
	    }
	    GncV2.GncBook.GncGncInvoice jwsdpInvc = (GncV2.GncBook.GncGncInvoice) bookElement;

	    try {
		GnucashGenerInvoice invc = createGenerInvoice(jwsdpInvc);
		invcMap.put(invc.getId(), invc);
	    } catch (RuntimeException e) {
		LOGGER.error("initGenerInvoiceMap: [RuntimeException] Problem in " + getClass().getName() + ".initInvoiceMap: "
			+ "ignoring illegal (generic) Invoice-Entry with id=" + jwsdpInvc.getInvoiceId(), e);
	    }
	} // for

	LOGGER.debug("initGenerInvoiceMap: No. of entries in (generic) invoice map: " + invcMap.size());
    }

    /**
     * @param jwsdpInvc the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashInvoice to wrap the given jaxb-object.
     */
    protected GnucashGenerInvoice createGenerInvoice(final GncV2.GncBook.GncGncInvoice jwsdpInvc) {
	GnucashGenerInvoice invc = new GnucashGenerInvoiceImpl(jwsdpInvc, gcshFile);
	return invc;
    }

    // ---------------------------------------------------------------

    public void addGenerInvoice(GnucashGenerInvoice invc) {
	invcMap.put(invc.getId(), invc);
    }

    public void removeGenerInvoice(GnucashGenerInvoice invc) {
	invcMap.remove(invc.getId());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerInvoiceByID(java.lang.String)
     */
    public GnucashGenerInvoice getGenerInvoiceByID(final GCshID id) {
	if (invcMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashGenerInvoice retval = invcMap.get(id);
	if (retval == null) {
	    LOGGER.error("getGenerInvoiceByID: No (generic) Invoice with id '" + id + "'. " + 
	                 "We know " + invcMap.size() + " accounts.");
	}

	return retval;
    }

    /**
     * @see GnucashFile#getGenerInvoices()
     */
    public Collection<GnucashGenerInvoice> getGenerInvoices() {

	Collection<GnucashGenerInvoice> c = invcMap.values();

	ArrayList<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>(c);
	Collections.sort(retval);

	return retval;
    }
    
    // ----------------------------

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see GnucashFile#getPaidGenerInvoices()
     */
    public Collection<GnucashGenerInvoice> getPaidGenerInvoices() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashGenerInvoice> retval = new LinkedList<GnucashGenerInvoice>();
	for (GnucashGenerInvoice invc : getGenerInvoices()) {
	    if ( invc.getType() == GCshOwner.Type.CUSTOMER ) {
		try {
		    if (invc.isInvcFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getPaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.VENDOR ) {
		try {
		    if (invc.isBillFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getPaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.EMPLOYEE ) {
		try {
		    if (invc.isVoucherFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getPaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.JOB ) {
		try {
		    if (invc.isJobFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getPaidGenerInvoices: Serious error");
		}
	    }
	}

	return retval;
    }

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see GnucashFile#getUnpaidGenerInvoices()
     */
    public Collection<GnucashGenerInvoice> getUnpaidGenerInvoices() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashGenerInvoice> retval = new LinkedList<GnucashGenerInvoice>();
	for (GnucashGenerInvoice invc : getGenerInvoices()) {
	    if ( invc.getType() == GCshOwner.Type.CUSTOMER ) {
		try {
		    if (invc.isNotInvcFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getUnpaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.VENDOR ) {
		try {
		    if (invc.isNotBillFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getUnpaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.EMPLOYEE ) {
		try {
		    if (invc.isNotVoucherFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getUnpaidGenerInvoices: Serious error");
		}
	    } else if ( invc.getType() == GCshOwner.Type.JOB ) {
		try {
		    if (invc.isNotJobFullyPaid()) {
			retval.add(invc);
		    }
		} catch (WrongInvoiceTypeException e) {
		    // This should not happen
		    LOGGER.error("getUnpaidGenerInvoices: Serious error");
		}
	    }
	}

	return retval;
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public Collection<GnucashCustomerInvoice> getInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getInvoicesForCustomer_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
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
    public Collection<GnucashJobInvoice> getInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
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
    public Collection<GnucashCustomerInvoice> getPaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidInvoicesForCustomer_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
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
    public Collection<GnucashJobInvoice> getPaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
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
    public Collection<GnucashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashCustomerInvoice> retval = new LinkedList<GnucashCustomerInvoice>();

	for ( GnucashGenerInvoice invc : getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(cust.getId())) {
		try {
		    retval.add(new GnucashCustomerInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidInvoicesForCustomer_direct: Cannot instantiate GnucashCustomerInvoiceImpl");
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
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForCustomer_viaAllJobs(final GnucashCustomer cust)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashCustomerJob job : cust.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
		retval.add(jobInvc);
	    }
	}

	return retval;
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public Collection<GnucashVendorBill> getBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashVendorBill> retval = new LinkedList<GnucashVendorBill>();

	for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getId())) {
		try {
		    retval.add(new GnucashVendorBillImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getBillsForVendor_direct: Cannot instantiate GnucashVendorBillImpl");
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
    public Collection<GnucashJobInvoice> getBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashVendorJob job : vend.getJobs() ) {
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public Collection<GnucashVendorBill> getPaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashVendorBill> retval = new LinkedList<GnucashVendorBill>();

	for ( GnucashGenerInvoice invc : getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getId())) {
		try {
		    retval.add(new GnucashVendorBillImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidBillsForVendor_direct: Cannot instantiate GnucashVendorBillImpl");
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
    public Collection<GnucashJobInvoice> getPaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashVendorJob job : vend.getJobs() ) {
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
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public Collection<GnucashVendorBill> getUnpaidBillsForVendor_direct(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashVendorBill> retval = new LinkedList<GnucashVendorBill>();

	for ( GnucashGenerInvoice invc : getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(vend.getId())) {
		try {
		    retval.add(new GnucashVendorBillImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidBillsForVendor_direct: Cannot instantiate GnucashVendorBillImpl");
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
    public Collection<GnucashJobInvoice> getUnpaidBillsForVendor_viaAllJobs(final GnucashVendor vend)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashVendorJob job : vend.getJobs() ) {
	    for ( GnucashJobInvoice jobInvc : job.getUnpaidInvoices() ) {
		retval.add(jobInvc);
	    }
	}

	return retval;
    }
    
    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    public Collection<GnucashEmployeeVoucher> getVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getVouchersForEmployee_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
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
    public Collection<GnucashEmployeeVoucher> getPaidVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidVouchersForEmployee_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
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
    public Collection<GnucashEmployeeVoucher> getUnpaidVouchersForEmployee_direct(final GnucashEmployee empl)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashEmployeeVoucher> retval = new LinkedList<GnucashEmployeeVoucher>();

	for ( GnucashGenerInvoice invc : getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(empl.getId())) {
		try {
		    retval.add(new GnucashEmployeeVoucherImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidVouchersForEmployee_direct: Cannot instantiate GnucashEmployeeVoucherImpl");
		}
	    }
	}

	return retval;
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashFile#getUnpaidInvoicesForCustomer_direct(GnucashCustomer)
     */
    public Collection<GnucashJobInvoice> getInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : getGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getInvoicesForJob: Cannot instantiate GnucashJobInvoiceImpl");
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
    public Collection<GnucashJobInvoice> getPaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : getPaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getPaidInvoicesForJob: Cannot instantiate GnucashJobInvoiceImpl");
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
    public Collection<GnucashJobInvoice> getUnpaidInvoicesForJob(final GnucashGenerJob job)
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashJobInvoice> retval = new LinkedList<GnucashJobInvoice>();

	for ( GnucashGenerInvoice invc : getUnpaidGenerInvoices() ) {
	    if (invc.getOwnerId(GnucashGenerInvoice.ReadVariant.DIRECT).equals(job.getId())) {
		try {
		    retval.add(new GnucashJobInvoiceImpl(invc));
		} catch (WrongInvoiceTypeException e) {
		    // This really should not happen, one can almost
		    // throw a fatal log here.
		    LOGGER.error("getUnpaidInvoicesForJob: Cannot instantiate GnucashJobInvoiceImpl");
		}
	    }
	}

	return retval;
    }

    // ---------------------------------------------------------------

    public int getNofEntriesGenerInvoiceMap() {
	return invcMap.size();
    }

}
