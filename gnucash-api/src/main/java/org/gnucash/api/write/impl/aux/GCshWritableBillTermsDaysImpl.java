package org.gnucash.api.write.impl.aux;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.impl.aux.GCshBillTermsDaysImpl;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBillTermsDays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshBillTermsDaysImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBillTermsDaysImpl extends GCshBillTermsDaysImpl 
                                           implements GCshWritableBillTermsDays 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBillTermsDaysImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBillTermsDaysImpl(
	    final GncV2.GncBook.GncGncBillTerm.BilltermDays jwsdpPeer, 
	    final GnucashWritableFile gncFile) {
	super(jwsdpPeer, gncFile);
    }

    public GCshWritableBillTermsDaysImpl(final GCshBillTermsDaysImpl bllTrm) {
	super(bllTrm.getJwsdpPeer(), bllTrm.getGnucashFile());
    }

    // ---------------------------------------------------------------

    @Override
    public void setDueDays(final Integer dueDays) {
	if ( dueDays == null ) {
	    throw new IllegalArgumentException("null due days given!");
	}
	
	if ( dueDays <= 0 ) {
	    throw new IllegalArgumentException("due days <= 0 given!");
	}

	jwsdpPeer.setBtDaysDueDays(dueDays);
    }

    @Override
    public void setDiscountDays(final Integer dscntDays) {
	if ( dscntDays == null ) {
	    throw new IllegalArgumentException("null discount days given!");
	}
	
	if ( dscntDays <= 0 ) {
	    throw new IllegalArgumentException("discount days <= 0 given!");
	}

	jwsdpPeer.setBtDaysDiscDays(dscntDays);
    }

    @Override
    public void setDiscount(final FixedPointNumber dscnt) {
	if ( dscnt == null ) {
	    throw new IllegalArgumentException("null discount given!");
	}
	
	if ( dscnt.getBigDecimal().doubleValue() <= 0 ) {
	    throw new IllegalArgumentException("discount <= 0 given!");
	}

	jwsdpPeer.setBtDaysDiscount(dscnt.toGnucashString());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GCshWritableBillTermsDaysImpl [");

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
