package org.gnucash.api.write.impl.aux;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.read.aux.BillTermsTypeException;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.api.read.impl.aux.GCshBillTermsDaysImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.api.read.impl.aux.GCshBillTermsProximoImpl;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableBillTermsDays;
import org.gnucash.api.write.aux.GCshWritableBillTermsProximo;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshBillTermsImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableBillTermsImpl extends GCshBillTermsImpl 
                                       implements GCshWritableBillTerms 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableBillTermsImpl.class);

    // ---------------------------------------------------------------
    
    private Type type;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableBillTermsImpl(
	    final GncGncBillTerm jwsdpPeer, 
	    final GnucashWritableFile gcshFile) {
	super(jwsdpPeer, gcshFile);
	
	try {
	    this.type = super.getType();
	} catch ( BillTermsTypeException exc ) {
	    this.type = Type.UNSET;
	}
    }

    public GCshWritableBillTermsImpl(final GCshBillTermsImpl bllTrm) {
	super(bllTrm.getJwsdpPeer(), bllTrm.getGnucashFile());

	try {
	    this.type = super.getType();
	} catch ( BillTermsTypeException exc ) {
	    this.type = Type.UNSET;
	}
    }

    // ---------------------------------------------------------------
    
    @Override
    public Type getType() throws BillTermsTypeException {
	return type;
    }

    @Override
    public GCshWritableBillTermsDays getWritableDays() {
	GCshBillTermsDays bllTrmDays = getDays();
	return new GCshWritableBillTermsDaysImpl((GCshBillTermsDaysImpl) bllTrmDays);
    }

    @Override
    public GCshWritableBillTermsProximo getWritableProximo() {
	GCshBillTermsProximo bllTrmProx = getProximo();
	return new GCshWritableBillTermsProximoImpl((GCshBillTermsProximoImpl) bllTrmProx);
    }
    
    // ---------------------------------------------------------------

    @Override
    public void setRefcount(int refCnt) {
	jwsdpPeer.setBilltermRefcount(refCnt);
    }

    @Override
    public void setName(String name) {
	jwsdpPeer.setBilltermName(name);
    }

    @Override
    public void setDescription(final String descr) {
	jwsdpPeer.setBilltermDesc(descr);
    }

    @Override
    public void setInvisible(final boolean val) {
	int intVal = 0;
	if ( val )
	    intVal = 1;
	
	jwsdpPeer.setBilltermInvisible(intVal);
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(final Type newType) throws BillTermsTypeException {
	
	if ( newType == Type.UNSET ) {
	    throw new BillTermsTypeException("Bill type may not be set to 'UNSET'");
	}
	
	if ( type != Type.UNSET &&
	     type != newType ) {
	    throw new BillTermsTypeException("Bill terms type may not be changed");
	}
	
	this.type = newType;
    }

    @Override
    public void setDays(final GCshWritableBillTermsDays bllTrmsDays) {
	if ( type != Type.DAYS ) {
	    throw new IllegalStateException("Cannot set bill terms (days) for type '" + type.toString() + "'");
	}
	
	if ( bllTrmsDays == null ) {
	    throw new IllegalArgumentException("null bill terms (days) given!");
	}
	
	if ( ! ( bllTrmsDays instanceof GCshWritableBillTermsDaysImpl ) ) {
	    throw new IllegalArgumentException("wrong implementation of bill terms (days) given!");
	}
	
	jwsdpPeer.setBilltermDays(((GCshWritableBillTermsDaysImpl) bllTrmsDays).getJwsdpPeer());
    }

    @Override
    public void setProximo(final GCshWritableBillTermsProximo bllTrmsProx) {
	if ( type != Type.PROXIMO ) {
	    throw new IllegalStateException("Cannot set bill terms (proxmo) for type '" + type.toString() + "'");
	}
	
	if ( bllTrmsProx == null ) {
	    throw new IllegalArgumentException("null bill terms (prox) given!");
	}
	
	if ( ! ( bllTrmsProx instanceof GCshWritableBillTermsProximoImpl ) ) {
	    throw new IllegalArgumentException("wrong implementation of bill terms (proxy) given!");
	}
	
	jwsdpPeer.setBilltermProximo(((GCshWritableBillTermsProximoImpl) bllTrmsProx).getJwsdpPeer());
    }

    // ---------------------------------------------------------------

    @Override
    public void setParentID(final GCshID prntID) {
	if ( prntID == null ) {
	    throw new IllegalArgumentException("null parent ID given!");
	}
	
	if ( ! prntID.isSet() ) {
	    throw new IllegalArgumentException("unset parent ID given!");
	}

	GncGncBillTerm.BilltermParent intVal = ((GnucashWritableFileImpl) myFile).createGncGncBillTermParentType();
	intVal.setType(Const.XML_DATA_TYPE_GUID);
	intVal.setValue(prntID.toString());
	
	jwsdpPeer.setBilltermParent(intVal);
    }
    
    @Override
    public void addChild(final String chld) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public void removeChild(String chld) {
	// TODO Auto-generated method stub
	
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GCshWritableBillTermsImpl [");

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
