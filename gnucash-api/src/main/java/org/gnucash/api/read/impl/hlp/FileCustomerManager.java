package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCustomerManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);

	// ---------------------------------------------------------------

	protected GnucashFileImpl gcshFile;

	private Map<GCshID, GnucashCustomer> custMap;

	// ---------------------------------------------------------------

	public FileCustomerManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		custMap = new HashMap<GCshID, GnucashCustomer>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncCustomer) ) {
				continue;
			}
			GncGncCustomer jwsdpCust = (GncGncCustomer) bookElement;

			try {
				GnucashCustomerImpl cust = createCustomer(jwsdpCust);
				custMap.put(cust.getID(), cust);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Customer-Entry with id=" + jwsdpCust.getCustId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in customer map: " + custMap.size());
	}

	protected GnucashCustomerImpl createCustomer(final GncGncCustomer jwsdpCust) {
		GnucashCustomerImpl cust = new GnucashCustomerImpl(jwsdpCust, gcshFile);
		LOGGER.debug("Generated new customer: " + cust.getID());
		return cust;
	}

	// ---------------------------------------------------------------

	public void addCustomer(GnucashCustomer cust) {
		custMap.put(cust.getID(), cust);
		LOGGER.debug("Added customer to cache: " + cust.getID());
	}

	public void removeCustomer(GnucashCustomer cust) {
		custMap.remove(cust.getID());
		LOGGER.debug("Removed customer from cache: " + cust.getID());
	}

	// ---------------------------------------------------------------

	public GnucashCustomer getCustomerByID(final GCshID id) {
		if ( custMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashCustomer retval = custMap.get(id);
		if ( retval == null ) {
			LOGGER.warn("getCustomerByID: No Customer with id '" + id + "'. We know " + custMap.size() + " customers.");
		}
		return retval;
	}

	public List<GnucashCustomer> getCustomersByName(final String name) {
		return getCustomersByName(name, true);
	}

	public List<GnucashCustomer> getCustomersByName(final String expr, boolean relaxed) {

		if ( custMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnucashCustomer> result = new ArrayList<GnucashCustomer>();

		for ( GnucashCustomer cust : getCustomers() ) {
			if ( relaxed ) {
				if ( cust.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
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

	public GnucashCustomer getCustomerByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		List<GnucashCustomer> custList = getCustomersByName(name);
		if ( custList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( custList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return custList.get(0);
	}

	// ::CHECK
	// https://stackoverflow.com/questions/52620446/collectors-tounmodifiablelist-vs-collections-unmodifiablelist-in-java-10?rq=3
	public Collection<GnucashCustomer> getCustomers() {
		return Collections.unmodifiableCollection(custMap.values());
		// return custMap.values().stream().collect( Collectors.toUnmodifiableList() );
	}

	// ---------------------------------------------------------------

	public int getNofEntriesCustomerMap() {
		return custMap.size();
	}

}
