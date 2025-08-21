package org.gnucash.api.read.impl.aux;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncBillTerm.BilltermChild;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.BillTermsTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBillTermsImpl implements GCshBillTerms {

    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshBillTermsImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncBillTerm jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GCshBillTermsImpl(
	    final GncGncBillTerm peer, 
	    final GnuCashFile gcshFile) {
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
    public GncGncBillTerm getJwsdpPeer() {
    	return jwsdpPeer;
    }

    public GnuCashFile getGnuCashFile() {
    	return myFile;
    }

    // -----------------------------------------------------------

    @Override
	public GCshBllTrmID getID() {
    	return new GCshBllTrmID( jwsdpPeer.getBilltermGuid().getValue() );
    }

    @Override
	public int getRefcount() {
    	return jwsdpPeer.getBilltermRefcount();
    }

    @Override
	public String getName() {
    	return jwsdpPeer.getBilltermName();
    }

    @Override
	public String getDescription() {
    	return jwsdpPeer.getBilltermDesc();
    }

    @Override
	public boolean isInvisible() {
		if ( jwsdpPeer.getBilltermInvisible() == 1 ) {
			return true;
		} else {
			return false;
		}
    }
    
    // ------------------------

    @Override
	public Type getType() {
		if ( getDays() != null ) {
			return Type.DAYS;
		} else if ( getProximo() != null ) {
			return Type.PROXIMO;
		} else {
			throw new BillTermsTypeException("Cannot determine bill terms type");
		}
    }

    @Override
	public GCshBillTermsDays getDays() {
		if ( jwsdpPeer.getBilltermDays() == null ) {
			return null;
		}

		GCshBillTermsDays days = new GCshBillTermsDaysImpl(jwsdpPeer.getBilltermDays(), myFile);
		return days;
    }

    @Override
	public GCshBillTermsProximo getProximo() {
		if ( jwsdpPeer.getBilltermProximo() == null ) {
			return null;
		}

		GCshBillTermsProximo prox = new GCshBillTermsProximoImpl(jwsdpPeer.getBilltermProximo(), myFile);
		return prox;
    }

    // ------------------------

    @Override
	public GCshBllTrmID getParentID() {
		if ( jwsdpPeer.getBilltermParent() == null ) {
			return null;
		}

		return new GCshBllTrmID(jwsdpPeer.getBilltermParent().getValue());
    }

    @Override
	public List<String> getChildren() {
		if ( jwsdpPeer.getBilltermChild() == null ) {
			return null;
		}

		List<String> result = new ArrayList<String>();

		for ( BilltermChild child : jwsdpPeer.getBilltermChild() ) {
			result.add(new String(child.getValue()));
		}

		return result;
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GCshBillTermsImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", type=");
		try {
			buffer.append(getType());
		} catch (BillTermsTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", name='");
		buffer.append(getName() + "'");

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", type=");
		try {
			if ( getType() == Type.DAYS ) {
				buffer.append(" " + getDays());
			} else if ( getType() == Type.PROXIMO ) {
				buffer.append(" " + getProximo());
			}
		} catch (Exception exc) {
			buffer.append("ERROR");
		}

		buffer.append("]");

		return buffer.toString();
    }
    
}
