package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.impl.GnuCashCommodityImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileCommodityManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCommodityManager.class);
    
    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected Map<String, GnuCashCommodity>  cmdtyMap; // Keys: Sic, String not CmdtyCurrID
    protected Map<String, String>            xCodeMap; // X-Code -> Qualif. ID
                                                       // Values: Sic, String not CmdtyCurrID like above

    // ---------------------------------------------------------------
    
    public FileCommodityManager(GnuCashFileImpl gcshFile) {
    	this.gcshFile = gcshFile;
    	init(gcshFile.getRootElement());
    }

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		initMap1(pRootElement);
		initMap2(pRootElement);
	}

	private void initMap1(final GncV2 pRootElement) {
		cmdtyMap = new HashMap<String, GnuCashCommodity>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncCommodity) ) {
				continue;
			}
			GncCommodity jwsdpCmdty = (GncCommodity) bookElement;

			// CAUTION: As opposed to the other entities, there is always
			// one additional object in the GnuCash file for commodities,
			// the "template" object. We will ignore it.
			if ( jwsdpCmdty.getCmdtyName() != null ) {
				if ( jwsdpCmdty.getCmdtyName().equals("template") ) {
					continue;
				}
			}

			try {
				GnuCashCommodityImpl cmdty = createCommodity(jwsdpCmdty);
				try {
					cmdtyMap.put(cmdty.getQualifID().toString(), cmdty);
				} catch (InvalidCmdtyCurrTypeException e) {
					LOGGER.error("initMap1: Could not add Commodity to map: " + cmdty.toString());
				}
			} catch (RuntimeException e) {
				LOGGER.error("initMap1: [RuntimeException] Problem in " + getClass().getName() + ".initMap1: "
						+ "ignoring illegal Commodity entry with id=" + jwsdpCmdty.getCmdtyId(), e);
			}
		} // for

		LOGGER.debug("initMap1: No. of entries in Commodity map (1): " + cmdtyMap.size());
	}

	private void initMap2(final GncV2 pRootElement) {
		xCodeMap = new HashMap<String, String>();

		for ( String qualifID : cmdtyMap.keySet() ) {
			GnuCashCommodity cmdty = cmdtyMap.get(qualifID);
			try {
				xCodeMap.put(cmdty.getXCode(), cmdty.getQualifID().toString());
			} catch (InvalidCmdtyCurrTypeException e) {
				LOGGER.error("initMap2: Could not add element to map: " + cmdty.getXCode());
			}
		}

		LOGGER.debug("initMap2: No. of entries in Commodity map (2): " + xCodeMap.size());
	}

	protected GnuCashCommodityImpl createCommodity(final GncCommodity jwsdpCmdty) {
		GnuCashCommodityImpl cmdty = new GnuCashCommodityImpl(jwsdpCmdty, gcshFile);
		LOGGER.debug("Generated new commodity: " + cmdty.getQualifID());
		return cmdty;
	}

	// ---------------------------------------------------------------

	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrID qualifID) {
		if ( qualifID == null ) {
			throw new IllegalArgumentException("null commodity ID given");
		}
		
		if ( ! qualifID.isSet() ) {
			throw new IllegalArgumentException("unset commodity ID given");
		}
		
		return getCommodityByQualifID(qualifID.toString());
	}

	public GnuCashCommodity getCommodityByQualifID(final String nameSpace, final String id) {
		if ( nameSpace == null ) {
			throw new IllegalArgumentException("null name space given");
		}

		if ( nameSpace.trim().equals("") ) {
			throw new IllegalArgumentException("empty name space given");
		}

		if ( id == null ) {
			throw new IllegalArgumentException("null ID string given");
		}

		if ( id.trim().equals("") ) {
			throw new IllegalArgumentException("empty ID string given");
		}

		return getCommodityByQualifID(nameSpace + GCshCmdtyCurrID.SEPARATOR + id);
	}

	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exch, String id) {
		if ( exch == GCshCmdtyCurrNameSpace.Exchange.UNSET ) {
			throw new IllegalArgumentException("unset exchange given ");
		}
		
		if ( id == null ) {
			throw new IllegalArgumentException("null ID string given");
		}

		if ( id.trim().equals("") ) {
			throw new IllegalArgumentException("empty ID string given");
		}

		return getCommodityByQualifID(exch.toString() + GCshCmdtyCurrID.SEPARATOR + id);
	}

	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
		if ( mic == GCshCmdtyCurrNameSpace.MIC.UNSET ) {
			throw new IllegalArgumentException("unset MIC given");
		}
		
		if ( id == null ) {
			throw new IllegalArgumentException("null ID string given");
		}

		if ( id.trim().equals("") ) {
			throw new IllegalArgumentException("empty ID string given");
		}

		return getCommodityByQualifID(mic.toString() + GCshCmdtyCurrID.SEPARATOR + id);
	}

	public GnuCashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
		if ( secIdType == GCshCmdtyCurrNameSpace.SecIdType.UNSET ) {
			throw new IllegalArgumentException("unset security ID type given");
		}
		
		if ( id == null ) {
			throw new IllegalArgumentException("null ID string given");
		}

		if ( id.trim().equals("") ) {
			throw new IllegalArgumentException("empty ID string given");
		}

		return getCommodityByQualifID(secIdType.toString() + GCshCmdtyCurrID.SEPARATOR + id);
	}

	public GnuCashCommodity getCommodityByQualifID(final String qualifID) {
		if ( qualifID == null ) {
			throw new IllegalArgumentException("null ID string given");
		}

		if ( qualifID.trim().equals("") ) {
			throw new IllegalArgumentException("empty ID string given");
		}

		if ( cmdtyMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashCommodity retval = cmdtyMap.get(qualifID.trim());
		if ( retval == null ) {
			LOGGER.warn("getCommodityByQualifID: No Commodity with qualified id '" + qualifID + "'. We know "
					+ cmdtyMap.size() + " commodities.");
		}

		return retval;
	}

	public GnuCashCommodity getCommodityByXCode(final String xCode) {
		if ( xCode == null ) {
			throw new IllegalArgumentException("null x-code given");
		}

		if ( xCode.trim().equals("") ) {
			throw new IllegalArgumentException("empty x-code given");
		}

		if ( cmdtyMap == null || xCodeMap == null ) {
			throw new IllegalStateException("no root-element(s) loaded");
		}

		if ( cmdtyMap.size() != xCodeMap.size() ) {
			// CAUTION: Don't throw an exception, at least not in all cases,
			// because this is not necessarily an error: Only if the GnuCash
			// file does not contain quotes for foreign currencies (i.e. currency-
			// commodities but only security-commodities is this an error.
			// throw new IllegalStateException("Sizes of root elements are not equal");
			LOGGER.debug("getCommodityByXCode: Sizes of root elements are not equal.");
		}

		String qualifIDStr = xCodeMap.get(xCode);
		if ( qualifIDStr == null ) {
			LOGGER.warn("getCommodityByXCode: No Commodity with X-Code '" + xCode + "'. We know " + xCodeMap.size()
					+ " commodities in map 2.");
		}

		GnuCashCommodity retval = cmdtyMap.get(qualifIDStr);
		if ( retval == null ) {
			LOGGER.warn("getCommodityByXCode: No Commodity with qualified ID '" + qualifIDStr + "'. We know "
					+ cmdtyMap.size() + " commodities in map 1.");
		}

		return retval;
	}

	public List<GnuCashCommodity> getCommoditiesByName(final String expr) {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}

		if ( cmdtyMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return getCommoditiesByName(expr, true);
	}

	public List<GnuCashCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}

		if ( cmdtyMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashCommodity> result = new ArrayList<GnuCashCommodity>();

		for ( GnuCashCommodity cmdty : getCommodities() ) {
			if ( cmdty.getName() != null ) // yes, that can actually happen!
			{
				if ( relaxed ) {
					if ( cmdty.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(cmdty);
					}
				} else {
					if ( cmdty.getName().equals(expr) ) {
						result.add(cmdty);
					}
				}
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	public GnuCashCommodity getCommodityByNameUniq(final String expr)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}

		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}

		if ( cmdtyMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashCommodity> cmdtyList = getCommoditiesByName(expr, false);
		if ( cmdtyList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( cmdtyList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return cmdtyList.get(0);
	}

	public Collection<GnuCashCommodity> getCommodities() {
		if ( cmdtyMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(cmdtyMap.values());
	}

	// ---------------------------------------------------------------

	public int getNofEntriesCommodityMap() {
		return cmdtyMap.size();
	}

}
