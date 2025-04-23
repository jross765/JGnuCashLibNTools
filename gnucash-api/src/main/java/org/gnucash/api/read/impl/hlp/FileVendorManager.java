package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileVendorManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileVendorManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected Map<GCshID, GnuCashVendor> vendMap;

    // ---------------------------------------------------------------
    
	public FileVendorManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		vendMap = new HashMap<GCshID, GnuCashVendor>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncGncVendor) ) {
				continue;
			}
			GncGncVendor jwsdpVend = (GncGncVendor) bookElement;

			try {
				GnuCashVendorImpl vend = createVendor(jwsdpVend);
				vendMap.put(vend.getID(), vend);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Vendor-Entry with id=" + jwsdpVend.getVendorId(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in vendor map: " + vendMap.size());
	}

	protected GnuCashVendorImpl createVendor(final GncGncVendor jwsdpVend) {
		GnuCashVendorImpl vend = new GnuCashVendorImpl(jwsdpVend, gcshFile);
		LOGGER.debug("Generated new vendor: " + vend.getID());
		return vend;
	}

	// ---------------------------------------------------------------

	public GnuCashVendor getVendorByID(GCshID vendID) {
		if ( vendID == null ) {
			throw new IllegalArgumentException("null vendor ID given");
		}
		
		if ( ! vendID.isSet() ) {
			throw new IllegalArgumentException("unset vendor ID given");
		}
		
		if ( vendMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashVendor retval = vendMap.get(vendID);
		if ( retval == null ) {
			LOGGER.warn("getVendorByID: No Vendor with id '" + vendID + "'. We know " + vendMap.size() + " vendors.");
		}
		
		return retval;
	}

	public List<GnuCashVendor> getVendorsByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		return getVendorsByName(name, true);
	}

	public List<GnuCashVendor> getVendorsByName(final String expr, final boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}
		
		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}
		
		if ( vendMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashVendor> result = new ArrayList<GnuCashVendor>();

		for ( GnuCashVendor vend : getVendors() ) {
			if ( relaxed ) {
				if ( vend.getName().trim().toLowerCase().contains(expr.trim().toLowerCase()) ) {
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

	public GnuCashVendor getVendorByNameUniq(final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		List<GnuCashVendor> vendList = getVendorsByName(name);
		if ( vendList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( vendList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return vendList.get(0);
	}

	public Collection<GnuCashVendor> getVendors() {
		if ( vendMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(vendMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesVendorMap() {
		return vendMap.size();
	}

}
