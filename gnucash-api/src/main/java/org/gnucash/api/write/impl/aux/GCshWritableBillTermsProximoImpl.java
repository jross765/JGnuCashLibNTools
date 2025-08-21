package org.gnucash.api.write.impl.aux;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.read.impl.aux.GCshBillTermsProximoImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBillTermsProximo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GCshBillTermsProximoImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBillTermsProximoImpl extends GCshBillTermsProximoImpl 
                                              implements GCshWritableBillTermsProximo 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBillTermsProximoImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBillTermsProximoImpl(
	    final GncGncBillTerm.BilltermProximo jwsdpPeer, 
	    final GnuCashWritableFile gcshFile) {
    	super(jwsdpPeer, gcshFile);
    }

    public GCshWritableBillTermsProximoImpl(final GCshBillTermsProximoImpl bllTrm) {
    	super(bllTrm.getJwsdpPeer(), bllTrm.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    @Override
    public void setDueDay(final Integer dueDay) {
		if ( dueDay == null ) {
			throw new IllegalArgumentException("argument <dueDay> is null");
		}

		if ( dueDay <= 0 ) {
			throw new IllegalArgumentException("argument <prntID> is <= 0");
		}

		jwsdpPeer.setBtProxDueDay(dueDay);
    }

    @Override
    public void setDiscountDay(final Integer dscntDay) {
		if ( dscntDay == null ) {
			throw new IllegalArgumentException("argument <dscntDay> is null");
		}

		if ( dscntDay <= 0 ) {
			throw new IllegalArgumentException("argument <dscntDay> is <= 0");
		}

		jwsdpPeer.setBtProxDiscDay(dscntDay);
    }

    @Override
    public void setDiscount(final FixedPointNumber dscnt) {
		if ( dscnt == null ) {
			throw new IllegalArgumentException("argument <dscnt> is null");
		}

		if ( dscnt.getBigDecimal().doubleValue() <= 0 ) {
			throw new IllegalArgumentException("argument <dscnt> is <= 0");
		}

		jwsdpPeer.setBtProxDiscount(dscnt.toGnuCashString());
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GCshWritableBillTermsProximoImpl [");

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
