package org.gnucash.api.write.impl.aux;

import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshAddressImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableAddressImpl extends GCshAddressImpl 
                                     implements GCshWritableAddress 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableAddressImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableAddressImpl(
	    final org.gnucash.api.generated.Address jwsdpPeer,
	    final GnuCashWritableFile gcshFile) {
    	super(jwsdpPeer, gcshFile);
    }

    public GCshWritableAddressImpl(final GCshAddressImpl addr) {
    	super(addr.getJwsdpPeer(), addr.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see GCshWritableAddress#setName(java.lang.String)
     */
    public void setName(final String name) {
    	if ( name == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrName(name);
    	getWritableGnuCashFile().setModified(true);
    }

    /**
     * @see #setLine2(String)
     * @see #setLine3(String)
     * @see #setLine4(String)
     */
    public void setLine1(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrAddr1(val);
    	getWritableGnuCashFile().setModified(true);
    }

    /**
     * @see #setLine1(String)
     * @see #setLine3(String)
     * @see #setLine4(String)
     */
    public void setLine2(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrAddr2(val);
    	getWritableGnuCashFile().setModified(true);
    }

    /**
     * @see #setLine1(String)
     * @see #setLine2(String)
     * @see #setLine4(String)
     */
    public void setLine3(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrAddr3(val);
    	getWritableGnuCashFile().setModified(true);
    }

    /**
     * @see #setLine1(String)
     * @see #setLine2(String)
     * @see #setLine3(String)
     */
    public void setLine4(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrAddr4(val);
    	getWritableGnuCashFile().setModified(true);
    }

    public void setTel(final String tel) {
    	if ( tel == null ) {
    		throw new IllegalArgumentException("argument <tel> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <tel> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrPhone(tel);
    	getWritableGnuCashFile().setModified(true);
    }

    public void setFax(final String fax) {
    	if ( fax == null ) {
    		throw new IllegalArgumentException("argument <fax> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <fax> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrFax(fax);
    	getWritableGnuCashFile().setModified(true);
    }

    public void setEmail(final String eml) {
    	if ( eml == null ) {
    		throw new IllegalArgumentException("argument <eml> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <eml> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrEmail(eml);
    	getWritableGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------
    // Old names
    
    @Override
    @Deprecated
    public void setAddressName(String name) {
    	setName(name);
    }

    @Override
    @Deprecated
    public void setAddressLine1(String val) {
    	setLine1(val);
    }

    @Override
    @Deprecated
    public void setAddressLine2(String val) {
    	setLine2(val);
    }

    @Override
    @Deprecated
    public void setAddressLine3(String val) {
    	setLine3(val);
    }

    @Override
    @Deprecated
    public void setAddressLine4(String val) {
    	setLine4(val);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("GCshWritableAddressImpl [\n");

		buffer.append(getName() + "\n");
		buffer.append("\n");
		buffer.append(getLine1() + "\n");
		buffer.append(getLine2() + "\n");
		buffer.append(getLine3() + "\n");
		buffer.append(getLine4() + "\n");
		buffer.append("\n");
		buffer.append("Tel.:   " + getTel() + "\n");
		buffer.append("Fax:    " + getFax() + "\n");
		buffer.append("eMail:  " + getEmail() + "\n");

		buffer.append("]");

		return buffer.toString();
    }
    
}
