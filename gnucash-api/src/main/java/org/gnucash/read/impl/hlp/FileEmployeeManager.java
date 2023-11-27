package org.gnucash.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.NoEntryFoundException;
import org.gnucash.read.TooManyEntriesFoundException;
import org.gnucash.read.impl.GnucashEmployeeImpl;
import org.gnucash.read.impl.GnucashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileEmployeeManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileEmployeeManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    protected Map<GCshID, GnucashEmployee> emplMap;

    // ---------------------------------------------------------------
    
    public FileEmployeeManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	emplMap = new HashMap<GCshID, GnucashEmployee>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncGncEmployee)) {
		continue;
	    }
	    GncV2.GncBook.GncGncEmployee jwsdpEmpl = (GncV2.GncBook.GncGncEmployee) bookElement;

	    try {
		GnucashEmployeeImpl empl = createEmployee(jwsdpEmpl);
		emplMap.put(empl.getId(), empl);
	    } catch (RuntimeException e) {
		LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
			+ "ignoring illegal Employee-Entry with id=" + jwsdpEmpl.getEmployeeId(), e);
	    }
	} // for

	LOGGER.debug("init: No. of entries in vendor map: " + emplMap.size());
    }

    /**
     * @param jwsdpVend the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashVendor to wrap the given JAXB object.
     */
    protected GnucashEmployeeImpl createEmployee(final GncV2.GncBook.GncGncEmployee jwsdpEmpl) {
	GnucashEmployeeImpl empl = new GnucashEmployeeImpl(jwsdpEmpl, gcshFile);
	return empl;
    }

    // ---------------------------------------------------------------

    public void addEmployee(GnucashEmployee empl) {
	emplMap.put(empl.getId(), empl);
    }

    public void removeEmployee(GnucashEmployee empl) {
	emplMap.remove(empl.getId());
    }

    // ---------------------------------------------------------------

    public GnucashEmployee getEmployeeByID(final GCshID id) {
	if (emplMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashEmployee retval = emplMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getEmployeeByID: No Employee with id '" + id + "'. We know " + emplMap.size() + " employees.");
	}
	return retval;
    }

    public Collection<GnucashEmployee> getEmployeesByUserName(final String userName) {
	return getEmployeesByUserName(userName, true);
    }

    public Collection<GnucashEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {

	if (emplMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	Collection<GnucashEmployee> result = new ArrayList<GnucashEmployee>();

	for ( GnucashEmployee empl : getEmployees() ) {
	    if ( relaxed ) {
		if ( empl.getUserName().trim().toLowerCase().
			contains(expr.trim().toLowerCase()) ) {
		    result.add(empl);
		}
	    } else {
		if ( empl.getUserName().equals(expr) ) {
		    result.add(empl);
		}
	    }
	}
	
	return result;
    }

    public GnucashEmployee getEmployeeByUserNameUniq(final String userName) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashEmployee> emplList = getEmployeesByUserName(userName);
	if ( emplList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( emplList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return emplList.iterator().next();
    }
    
    public Collection<GnucashEmployee> getEmployees() {
	return emplMap.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesCustomerMap() {
	return emplMap.size();
    }

}
