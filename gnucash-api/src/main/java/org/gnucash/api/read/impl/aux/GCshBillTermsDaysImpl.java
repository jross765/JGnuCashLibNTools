package org.gnucash.api.read.impl.aux;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBillTermsDaysImpl implements GCshBillTermsDays {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshBillTermsDaysImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncV2.GncBook.GncGncBillTerm.BilltermDays jwsdpPeer;

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
    public GCshBillTermsDaysImpl(
	    final GncV2.GncBook.GncGncBillTerm.BilltermDays peer, 
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
    public GncV2.GncBook.GncGncBillTerm.BilltermDays getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnucashFile getGnucashFile() {
	return myFile;
    }

    // ---------------------------------------------------------------

    @Override
    public Integer getDueDays() {
	return jwsdpPeer.getBtDaysDueDays();
    }

    @Override
    public Integer getDiscountDays() {
	return jwsdpPeer.getBtDaysDiscDays();
    }

    @Override
    public FixedPointNumber getDiscount() {
	if ( jwsdpPeer.getBtDaysDiscount() == null )
	    return null;
	
	return new FixedPointNumber(jwsdpPeer.getBtDaysDiscount());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GCshBillTermsDaysImpl [");

	buffer.append(" due-days: ");
	buffer.append(getDueDays());

	buffer.append(" discount-days: ");
	buffer.append(getDiscountDays());

	buffer.append(" discount: ");
	buffer.append(getDiscount());

	buffer.append("]");

	return buffer.toString();
    }
    
}
