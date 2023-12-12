package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashCommodityImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCommodityManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileCommodityManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<String, GnucashCommodity>  cmdtyMap; // Keys: Sic, String not CmdtyCurrID
    private Map<String, String>            xCodeMap; // X-Code -> Qualif. ID
                                                     // Values: Sic, String not CmdtyCurrID like above

    // ---------------------------------------------------------------
    
    public FileCommodityManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
	initMap1(pRootElement);
	initMap2(pRootElement);
    }
    
    private void initMap1(final GncV2 pRootElement) {
	cmdtyMap = new HashMap<String, GnucashCommodity>();

	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncV2.GncBook.GncCommodity)) {
		continue;
	    }
	    GncV2.GncBook.GncCommodity jwsdpCmdty = (GncV2.GncBook.GncCommodity) bookElement;

	    // CAUTION: As opposed to the other entities, there is always 
	    // one additional object in the GnuCash file for commodities, 
	    // the "template" object. We will ignore it.
	    if ( jwsdpCmdty.getCmdtyName() != null ) {
		if ( jwsdpCmdty.getCmdtyName().equals("template") ) {
		    continue;
		}
	    }
	    
	    try {
		GnucashCommodityImpl cmdty = createCommodity(jwsdpCmdty);
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
	    GnucashCommodity cmdty = cmdtyMap.get(qualifID);
	    try {
		xCodeMap.put(cmdty.getXCode(), cmdty.getQualifID().toString());
	    } catch (InvalidCmdtyCurrTypeException e) {
		LOGGER.error("initMap2: Could not add element to map: " + cmdty.getXCode());
	    }
	} 

	LOGGER.debug("initMap2: No. of entries in Commodity map (2): " + xCodeMap.size());
    }

    /**
     * @param jwsdpCmdty the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashCommodity to wrap the given JAXB object.
     */
    protected GnucashCommodityImpl createCommodity(final GncV2.GncBook.GncCommodity jwsdpCmdty) {
	GnucashCommodityImpl cmdty = new GnucashCommodityImpl(jwsdpCmdty, gcshFile);
	return cmdty;
    }

    // ---------------------------------------------------------------

    public void addCommodity(GnucashCommodity cmdty) {
	cmdtyMap.put(cmdty.getQualifID().toString(), cmdty);

	if ( cmdty.getXCode() != null )
	    xCodeMap.put(cmdty.getXCode(), cmdty.getQualifID().toString());
	
	LOGGER.debug("Added commodity to cache: " + cmdty.getQualifID());
    }

    public void removeCommodity(GnucashCommodity cmdty) {
	cmdtyMap.remove(cmdty.getQualifID().toString());
	
	for ( String xCode : xCodeMap.keySet() ) {
	    if ( xCodeMap.get(xCode).equals(cmdty.getQualifID().toString()) )
		xCodeMap.remove(xCode);
	}
	
	LOGGER.debug("Removed commodity from cache: " + cmdty.getQualifID());
    }

    // ---------------------------------------------------------------

    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrID qualifID) {
	return getCommodityByQualifID(qualifID.toString());
    }

    public GnucashCommodity getCommodityByQualifID(final String nameSpace, final String id) {
	return getCommodityByQualifID(nameSpace + GCshCmdtyCurrID.SEPARATOR + id);
    }

    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id) {
	return getCommodityByQualifID(exchange.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id) {
	return getCommodityByQualifID(mic.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    public GnucashCommodity getCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id) {
	return getCommodityByQualifID(secIdType.toString() + GCshCmdtyCurrID.SEPARATOR + id);
    }

    public GnucashCommodity getCommodityByQualifID(final String qualifID) {
	if (qualifID == null) {
	    throw new IllegalStateException("null string given");
	}

	if (qualifID.trim().equals("")) {
	    throw new IllegalStateException("Search string is empty");
	}

	if (cmdtyMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashCommodity retval = cmdtyMap.get(qualifID.trim());
	if (retval == null) {
	    LOGGER.warn("getCommodityByQualifID: No Commodity with qualified id '" + qualifID + "'. We know " + cmdtyMap.size()
		    + " commodities.");
	}
	
	return retval;
    }

    public GnucashCommodity getCommodityByXCode(final String xCode) {
	if ( cmdtyMap == null ||
             xCodeMap == null ) {
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
	if (qualifIDStr == null) {
	    LOGGER.warn("getCommodityByXCode: No Commodity with X-Code '" + xCode + "'. We know " + xCodeMap.size() + " commodities in map 2.");
	}
	
	GnucashCommodity retval = cmdtyMap.get(qualifIDStr);
	if (retval == null) {
	    LOGGER.warn("getCommodityByXCode: No Commodity with qualified ID '" + qualifIDStr + "'. We know " + cmdtyMap.size() + " commodities in map 1.");
	}
	
	return retval;
    }

    public Collection<GnucashCommodity> getCommoditiesByName(final String expr) {
	return getCommoditiesByName(expr, true);
    }
    
    public Collection<GnucashCommodity> getCommoditiesByName(final String expr, final boolean relaxed) {
	if (cmdtyMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	Collection<GnucashCommodity> result = new ArrayList<GnucashCommodity>();

	for ( GnucashCommodity cmdty : getCommodities() ) {
	    if ( cmdty.getName() != null ) // yes, that can actually happen! 
	    {
		if ( relaxed ) {
		    if ( cmdty.getName().toLowerCase().
			    contains(expr.trim().toLowerCase()) ) {
			result.add(cmdty);
		    }
		} else {
		    if ( cmdty.getName().equals(expr) ) {
			result.add(cmdty);
		    }
		}
	    }
	}
	
	return result;
    }

    public GnucashCommodity getCommodityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException {
	Collection<GnucashCommodity> cmdtyList = getCommoditiesByName(expr, false);
	if ( cmdtyList.size() == 0 )
	    throw new NoEntryFoundException();
	else if ( cmdtyList.size() > 1 )
	    throw new TooManyEntriesFoundException();
	else
	    return cmdtyList.iterator().next();
    }

    public Collection<GnucashCommodity> getCommodities() {
	if (cmdtyMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	
	return cmdtyMap.values();
    }

    // ---------------------------------------------------------------

    public int getNofEntriesCommodityMap() {
	return cmdtyMap.size();
    }

}
