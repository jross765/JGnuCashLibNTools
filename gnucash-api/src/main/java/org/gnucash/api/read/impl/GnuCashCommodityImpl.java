package org.gnucash.api.read.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashCommodityImpl extends GnuCashObjectImpl 
								  implements GnuCashCommodity 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCommodityImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncCommodity jwsdpPeer;

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashCommodityImpl(final GncCommodity peer, final GnuCashFile gcshFile) {
    	super(gcshFile);
    	
    	this.jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncCommodity getJwsdpPeer() {
	return jwsdpPeer;
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
     */
    @Override
    public GCshCmdtyCurrID getQualifID() {
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

    // ---------------------------------------------------------------

	@Override
	public List<GnuCashAccount> getStockAccounts() {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();
		
		for ( GnuCashAccount acct : getGnuCashFile().getAccountsByType(GnuCashAccount.Type.STOCK) ) {
			GCshCmdtyCurrID cmdtyCurrID = acct.getCmdtyCurrID();
			if ( this.getQualifID().equals(cmdtyCurrID) ) {
				result.add(acct);
			}
		}
		
		return result;
	}

    // -----------------------------------------------------------------

    // ::TODO sort the entries by date
    @Override
    public List<GnuCashPrice> getQuotes() {
    	List<GnuCashPrice> result = new ArrayList<GnuCashPrice>();
	
	Collection<GnuCashPrice> prices = getGnuCashFile().getPrices();
	for ( GnuCashPrice price : prices ) {
	    if ( price.getFromCmdtyCurrQualifID().toString().equals(getQualifID().toString()) ) {
		result.add(price);
	    }
	}
	
	return result;
    }

    @Override
    public GnuCashPrice getYoungestQuote() {
	
	GnuCashPrice result = null;

	LocalDate youngestDate = LocalDate.of(1970, 1, 1); // ::MAGIC
	for ( GnuCashPrice price : getQuotes() ) {
	    if ( price.getDate().isAfter(youngestDate) ) {
		result = price;
		youngestDate = price.getDate();
	    }
	}

	return result;
    }

    // -----------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getCmdtySlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getCmdtySlots());
	}

    // -----------------------------------------------------------------

	@Override
	public int compareTo(final GnuCashCommodity otherCmdty) {
		int i = compareToByName(otherCmdty);
		if ( i != 0 ) {
			return i;
		}

		i = compareToByQualifID(otherCmdty);
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherCmdty.hashCode());
	}
	
//	private int compareToByID(final GnuCashCommodity otherCmdty) {
//		return getID().toString().compareTo(otherCmdty.getID().toString());
//	}

	private int compareToByQualifID(final GnuCashCommodity otherCmdty) {
		return getQualifID().toString().compareTo(otherCmdty.getQualifID().toString());
	}

	private int compareToByName(final GnuCashCommodity otherCmdty) {
		return getName().compareTo(otherCmdty.getName());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
	String result = "GnuCashCommodityImpl [";

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
