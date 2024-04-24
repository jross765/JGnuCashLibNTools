package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileEmployeeManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileEmployeeManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    private Map<GCshID, GnuCashEmployee> emplMap;

    // ---------------------------------------------------------------
    
	public FileEmployeeManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

    // ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		emplMap = new HashMap<GCshID, GnuCashEmployee>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncEmployee) ) {
				continue;
			}
			GncGncEmployee jwsdpEmpl = (GncGncEmployee) bookElement;

			try {
				GnuCashEmployeeImpl empl = createEmployee(jwsdpEmpl);
				emplMap.put(empl.getID(), empl);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Employee-Entry with id=" + jwsdpEmpl.getEmployeeId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in vendor map: " + emplMap.size());
	}

	protected GnuCashEmployeeImpl createEmployee(final GncGncEmployee jwsdpEmpl) {
		GnuCashEmployeeImpl empl = new GnuCashEmployeeImpl(jwsdpEmpl, gcshFile);
		LOGGER.debug("Generated new employee: " + empl.getID());
		return empl;
	}

	// ---------------------------------------------------------------

	public void addEmployee(GnuCashEmployee empl) {
		emplMap.put(empl.getID(), empl);
		LOGGER.debug("Added employee to cache: " + empl.getID());
	}

	public void removeEmployee(GnuCashEmployee empl) {
		emplMap.remove(empl.getID());
		LOGGER.debug("Removed employee from cache: " + empl.getID());
	}

	// ---------------------------------------------------------------

	public GnuCashEmployee getEmployeeByID(final GCshID id) {
		if ( emplMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashEmployee retval = emplMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getEmployeeByID: No Employee with id '" + id + "'. We know " + emplMap.size() + " employees.");
		}
		
		return retval;
	}

	public List<GnuCashEmployee> getEmployeesByUserName(final String userName) {
		return getEmployeesByUserName(userName, true);
	}

	public List<GnuCashEmployee> getEmployeesByUserName(final String expr, boolean relaxed) {

		if ( emplMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashEmployee> result = new ArrayList<GnuCashEmployee>();

		for ( GnuCashEmployee empl : getEmployees() ) {
			if ( relaxed ) {
				if ( empl.getUserName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
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

	public GnuCashEmployee getEmployeeByUserNameUniq(final String userName)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnuCashEmployee> emplList = getEmployeesByUserName(userName);
		if ( emplList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( emplList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return emplList.get(0);
	}

	public Collection<GnuCashEmployee> getEmployees() {
		if ( emplMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(emplMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesCustomerMap() {
		return emplMap.size();
	}

}
