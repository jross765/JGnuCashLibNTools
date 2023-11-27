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
import org.gnucash.read.NoEntryFoundException;
import org.gnucash.read.TooManyEntriesFoundException;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.impl.GnucashCustomerImpl;
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

public class FileCustomerManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    protected Map<GCshID, GnucashCustomer> customerID2customer;

    // ---------------------------------------------------------------
    
    public FileCustomerManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	customerID2customer = new HashMap<GCshID, GnucashCustomer>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncCustomer)) {
		continue;
	    }
	    GncV2.GncBook.GncGncCustomer jwsdpCust = (GncV2.GncBook.GncGncCustomer) bookElement;

	    try {
		GnucashCustomerImpl cust = createCustomer(jwsdpCust);
		customerID2customer.put(cust.getId(), cust);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Customer-Entry with id=" + jwsdpCust.getCustId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in customer map: " + customerID2customer.size());
    }

    /**
     * @param jwsdpCust the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashCustomer to wrap the given JAXB object.
     */
    protected GnucashCustomerImpl createCustomer(final GncV2.GncBook.GncGncCustomer jwsdpCust) {
	GnucashCustomerImpl cust = new GnucashCustomerImpl(jwsdpCust, gcshFile);
	return cust;
    }

    // ---------------------------------------------------------------

    public void addCustomer(GnucashCustomer cust) {
	customerID2customer.put(cust.getId(), cust);
    }

    public void removeCustomer(GnucashCustomer cust) {
	customerID2customer.remove(cust.getId());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getCustomerByID(java.lang.String)
     */
    public GnucashCustomer getCustomerByID(final GCshID id) {
	if (customerID2customer == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashCustomer retval = customerID2customer.get(id);
	if (retval == null) {
	    LOGGER.warn("getCustomerByID: No Customer with id '" + id + "'. We know " + customerID2customer.size() + " customers.");
	}
	return retval;
    }

    /**
     * @see GnucashFile#getCustomersByName(java.lang.String)
     */
    public Collection<GnucashCustomer> getCustomersByName(final String name) {
	return getCustomersByName(name, true);
    }

    /**
     * @see GnucashFile#getCustomersByName(java.lang.String)
     */
    public Collection<GnucashCustomer> getCustomersByName(final String expr, boolean relaxed) {

	if (customerID2customer == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashCustomer> result = new ArrayList<GnucashCustomer>();

	for ( GnucashCustomer cust : getCustomers() ) {
	    if ( relaxed ) {
		if ( cust.getName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(cust);
		}
	    } else {
		if ( cust.getName().equals(expr) ) {
		    result.add(cust);
		}
	    }
	}
	
	return result;
    }

    public GnucashCustomer getCustomerByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashCustomer> custList = getCustomersByName(name);
	if ( custList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( custList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return custList.iterator().next();
    }
    
    /**
     * @see GnucashFile#getCustomers()
     */
    public Collection<GnucashCustomer> getCustomers() {
	return customerID2customer.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesCustomerMap() {
	return customerID2customer.size();
    }

}
