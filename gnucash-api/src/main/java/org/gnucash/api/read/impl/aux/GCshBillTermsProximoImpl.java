package org.gnucash.api.read.impl.aux;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBillTermsProximoImpl implements GCshBillTermsProximo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshBillTermsProximoImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncV2.GncBook.GncGncBillTerm.BilltermProximo jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnucashFile myFile;
    
    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @see #jwsdpPeer
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public GCshBillTermsProximoImpl(
	    final GncV2.GncBook.GncGncBillTerm.BilltermProximo peer, 
	    final GnucashFile gcshFile) {
	super();

	this.jwsdpPeer = peer;
	this.myFile = gcshFile;
    }

    // ---------------------------------------------------------------

    /**
     *
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncBillTerm.BilltermProximo getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnucashFile getGnucashFile() {
	return myFile;
    }

    // ---------------------------------------------------------------

    @Override
    public Integer getDueDay() {
	return jwsdpPeer.getBtProxDueDay();
    }

    @Override
    public Integer getDiscountDay() {
	return jwsdpPeer.getBtProxDiscDay();
    }

    @Override
    public FixedPointNumber getDiscount() {
	if ( jwsdpPeer.getBtProxDiscount() == null )
	    return null;
	
	return new FixedPointNumber(jwsdpPeer.getBtProxDiscount());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GCshBillTermsProximoImpl [");

	buffer.append("due-day=");
	buffer.append(getDueDay());

	buffer.append(", discount-day=");
	buffer.append(getDiscountDay());

	buffer.append(", discount=");
	buffer.append(getDiscount());

	buffer.append("]");

	return buffer.toString();
    }
    
}
