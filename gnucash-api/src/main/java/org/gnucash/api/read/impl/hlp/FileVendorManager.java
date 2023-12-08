package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileVendorManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileVendorManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashVendor> vendMap;

    // ---------------------------------------------------------------
    
    public FileVendorManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	vendMap = new HashMap<GCshID, GnucashVendor>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncVendor)) {
		continue;
	    }
	    GncV2.GncBook.GncGncVendor jwsdpVend = (GncV2.GncBook.GncGncVendor) bookElement;

	    try {
		GnucashVendorImpl vend = createVendor(jwsdpVend);
		vendMap.put(vend.getID(), vend);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Vendor-Entry with id=" + jwsdpVend.getVendorId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in vendor map: " + vendMap.size());
    }

    /**
     * @param jwsdpVend the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashVendor to wrap the given JAXB object.
     */
    protected GnucashVendorImpl createVendor(final GncV2.GncBook.GncGncVendor jwsdpVend) {
	GnucashVendorImpl vend = new GnucashVendorImpl(jwsdpVend, gcshFile);
	return vend;
    }

    // ---------------------------------------------------------------

    public void addVendor(GnucashVendor vend) {
	vendMap.put(vend.getID(), vend);
	LOGGER.debug("Added vendor to cache: " + vend.getID());
    }

    public void removeVendor(GnucashVendor vend) {
	vendMap.remove(vend.getID());
	LOGGER.debug("Removed vendor to cache: " + vend.getID());
    }

    // ---------------------------------------------------------------

    public GnucashVendor getVendorByID(GCshID id) {
	if (vendMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashVendor retval = vendMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getVendorByID: No Vendor with id '" + id + "'. We know " + vendMap.size() + " vendors.");
	}
	return retval;
    }

    public Collection<GnucashVendor> getVendorsByName(final String name) {
	return getVendorsByName(name, true);
    }

    public Collection<GnucashVendor> getVendorsByName(final String expr, final boolean relaxed) {
	if (vendMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashVendor> result = new ArrayList<GnucashVendor>();
	
	for ( GnucashVendor vend : getVendors() ) {
	    if ( relaxed ) {
		if ( vend.getName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(vend);
		}
	    } else {
		if ( vend.getName().equals(expr) ) {
		    result.add(vend);
		}
	    }
	}
	
	return result;
    }

    public GnucashVendor getVendorByNameUniq(final String name) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashVendor> vendList = getVendorsByName(name);
	if ( vendList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( vendList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return vendList.iterator().next();
    }
    
    public Collection<GnucashVendor> getVendors() {
	if (vendMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	return vendMap.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesVendorMap() {
	return vendMap.size();
    }

}
