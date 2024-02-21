package org.gnucash.api.read.impl.aux;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.generated.GncGncBillTerm.BilltermChild;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.BillTermsTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshBillTermsImpl implements GCshBillTerms {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshBillTermsImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncBillTerm jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnucashFile myFile;
    
    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GCshBillTermsImpl(
	    final GncGncBillTerm peer, 
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
    public GncGncBillTerm getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnucashFile getGnucashFile() {
	return myFile;
    }

    // -----------------------------------------------------------

    public GCshID getID() {
	return new GCshID( jwsdpPeer.getBilltermGuid().getValue() );
    }

    public int getRefcount() {
	return jwsdpPeer.getBilltermRefcount();
    }

    public String getName() {
	return jwsdpPeer.getBilltermName();
    }

    public String getDescription() {
	return jwsdpPeer.getBilltermDesc();
    }

    public boolean isInvisible() {
	if (jwsdpPeer.getBilltermInvisible() == 1)
	    return true;
	else
	    return false;
    }
    
    // ------------------------

    public Type getType() throws BillTermsTypeException {
	if ( getDays() != null )
	    return Type.DAYS;
	else if ( getProximo() != null )
	    return Type.PROXIMO;
	else
	    throw new BillTermsTypeException("Cannot determine bill terms type");
    }

    public GCshBillTermsDays getDays() {
	if ( jwsdpPeer.getBilltermDays() == null )
	    return null;
	
	GCshBillTermsDays days = new GCshBillTermsDaysImpl(jwsdpPeer.getBilltermDays(), myFile);
	return days;
    }

    public GCshBillTermsProximo getProximo() {
	if ( jwsdpPeer.getBilltermProximo() == null )
	    return null;
	
	GCshBillTermsProximo prox = new GCshBillTermsProximoImpl(jwsdpPeer.getBilltermProximo(), myFile);
	return prox;
    }

    // ------------------------

    public GCshID getParentID() {
	if ( jwsdpPeer.getBilltermParent() == null )
	    return null;

	return new GCshID( jwsdpPeer.getBilltermParent().getValue() );
    }

    public List<String> getChildren() {

	if ( jwsdpPeer.getBilltermChild() == null )
	    return null;
	
	List<String> result = new ArrayList<String>();

	for (BilltermChild child : jwsdpPeer.getBilltermChild()) {
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
	} catch ( Exception exc ) {
	    buffer.append("ERROR");
	}

	buffer.append("]");

	return buffer.toString();
    }
    
}
