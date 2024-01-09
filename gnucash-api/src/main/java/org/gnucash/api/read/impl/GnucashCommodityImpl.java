package org.gnucash.api.read.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashCommodityImpl implements GnucashCommodity 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCommodityImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private final GncCommodity jwsdpPeer;

    /**
     * The file we belong to.
     */
    protected final GnucashFile gcshFile;
    
    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnucashCommodityImpl(final GncCommodity peer, final GnucashFile gcshFile) {
	this.jwsdpPeer = peer;
	this.gcshFile = gcshFile;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncCommodity getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnucashFile getGnucashFile() {
	return gcshFile;
    }

    // ---------------------------------------------------------------

    protected String getNameSpace() {
	if ( jwsdpPeer.getCmdtySpace() == null )
	    return null;
	
	return jwsdpPeer.getCmdtySpace();
    }

    private String getID() {
	if ( jwsdpPeer.getCmdtyId() == null )
	    return null;
	
	return jwsdpPeer.getCmdtyId();
    }

    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrTypeException 
     */
    @Override
    public GCshCmdtyCurrID getQualifID() throws InvalidCmdtyCurrTypeException {
	if ( getNameSpace() == null ||
	     getID() == null )
	    return null;
	
	GCshCmdtyCurrID result = new GCshCmdtyCurrID(getNameSpace(), getID());
	
	return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
	if ( jwsdpPeer.getCmdtyName() == null )
	    return null;
	
	return jwsdpPeer.getCmdtyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getXCode() {
	if ( jwsdpPeer.getCmdtyXcode() == null )
	    return null;
	
	return jwsdpPeer.getCmdtyXcode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getFraction() {
	if ( jwsdpPeer.getCmdtyFraction() == null )
	    return null;
	
	return jwsdpPeer.getCmdtyFraction();
    }

    // -----------------------------------------------------------------

    // ::TODO sort the entries by date
    @Override
    public Collection<GnucashPrice> getQuotes() throws InvalidCmdtyCurrTypeException {
	Collection<GnucashPrice> result = new ArrayList<GnucashPrice>();
	
	Collection<GnucashPrice> prices = getGnucashFile().getPrices();
	for ( GnucashPrice price : prices ) {
	    if ( price.getFromCmdtyCurrQualifID().toString().equals(getQualifID().toString()) ) {
		result.add(price);
	    }
	}
	
	return result;
    }

    @Override
    public GnucashPrice getYoungestQuote() throws InvalidCmdtyCurrTypeException {
	
	GnucashPrice result = null;

	LocalDate youngestDate = LocalDate.of(1970, 1, 1); // ::MAGIC
	for ( GnucashPrice price : getQuotes() ) {
	    if ( price.getDate().isAfter(youngestDate) ) {
		result = price;
		youngestDate = price.getDate();
	    }
	}

	return result;
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
	String result = "GnucashCommodityImpl [";

	try {
	    result += "qualif-id='" + getQualifID().toString() + "'";
	} catch (InvalidCmdtyCurrTypeException e) {
	    result += "qualif-id=" + "ERROR";
	}
	
	result += ", namespace='" + getNameSpace() + "'"; 
	result += ", name='" + getName() + "'"; 
	result += ", x-code='" + getXCode() + "'"; 
	result += ", fraction=" + getFraction() + "]";
	
	return result;
    }

}
