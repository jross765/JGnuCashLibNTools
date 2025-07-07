package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileCustomerManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileCustomerManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	protected Map<GCshID, GnuCashCustomer> custMap;

	// ---------------------------------------------------------------

	public FileCustomerManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		custMap = new HashMap<GCshID, GnuCashCustomer>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncCustomer) ) {
				continue;
			}
			GncGncCustomer jwsdpCust = (GncGncCustomer) bookElement;

			try {
				GnuCashCustomerImpl cust = createCustomer(jwsdpCust);
				custMap.put(cust.getID(), cust);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Customer-Entry with id=" + jwsdpCust.getCustId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in customer map: " + custMap.size());
	}

	protected GnuCashCustomerImpl createCustomer(final GncGncCustomer jwsdpCust) {
		GnuCashCustomerImpl cust = new GnuCashCustomerImpl(jwsdpCust, gcshFile);
		LOGGER.debug("Generated new customer: " + cust.getID());
		return cust;
	}

	// ---------------------------------------------------------------

	public GnuCashCustomer getCustomerByID(final GCshID custID) {
		if ( custID == null ) {
			throw new IllegalArgumentException("argument <custID> is null");
		}
		
		if ( ! custID.isSet() ) {
			throw new IllegalArgumentException("argument <custID> is not set");
		}
		
		if ( custMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashCustomer retval = custMap.get(custID);
		if ( retval == null ) {
			LOGGER.warn("getCustomerByID: No Customer with id '" + custID + "'. We know " + custMap.size() + " customers.");
		}
		
		return retval;
	}

	public List<GnuCashCustomer> getCustomersByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		return getCustomersByName(name, true);
	}

	public List<GnuCashCustomer> getCustomersByName(final String expr, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("argument <expr> is null");
		}
		
		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("argument <expr> is empty");
		}
		
		if ( custMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashCustomer> result = new ArrayList<GnuCashCustomer>();

		for ( GnuCashCustomer cust : getCustomers() ) {
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

	public GnuCashCustomer getCustomerByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( name == null ) {
			throw new IllegalArgumentException("argument <name> is null");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("argument <name> is empty");
		}
		
		List<GnuCashCustomer> custList = getCustomersByName(name);
		if ( custList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( custList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return custList.get(0);
	}

	// ::CHECK
	// https://stackoverflow.com/questions/52620446/collectors-tounmodifiablelist-vs-collections-unmodifiablelist-in-java-10?rq=3
	public Collection<GnuCashCustomer> getCustomers() {
		if ( custMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(custMap.values());
		// return custMap.values().stream().collect( Collectors.toUnmodifiableList() );
	}

	// ---------------------------------------------------------------

	public int getNofEntriesCustomerMap() {
		return custMap.size();
	}

}
