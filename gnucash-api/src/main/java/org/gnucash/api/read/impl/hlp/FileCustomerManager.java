package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCustomerManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashCustomer> custMap;

    // ---------------------------------------------------------------
    
    public FileCustomerManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	custMap = new HashMap<GCshID, GnucashCustomer>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncCustomer)) {
		continue;
	    }
	    GncV2.GncBook.GncGncCustomer jwsdpCust = (GncV2.GncBook.GncGncCustomer) bookElement;

	    try {
		GnucashCustomerImpl cust = createCustomer(jwsdpCust);
		custMap.put(cust.getId(), cust);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Customer-Entry with id=" + jwsdpCust.getCustId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in customer map: " + custMap.size());
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
	custMap.put(cust.getId(), cust);
    }

    public void removeCustomer(GnucashCustomer cust) {
	custMap.remove(cust.getId());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getCustomerByID(java.lang.String)
     */
    public GnucashCustomer getCustomerByID(final GCshID id) {
	if (custMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashCustomer retval = custMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getCustomerByID: No Customer with id '" + id + "'. We know " + custMap.size() + " customers.");
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

	if (custMap == null) {
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
	return custMap.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesCustomerMap() {
	return custMap.size();
    }

}
