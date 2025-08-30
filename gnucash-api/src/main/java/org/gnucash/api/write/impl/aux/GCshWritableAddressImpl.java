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
     * @see GCshWritableAddress#setAddressName(java.lang.String)
     */
    public void setAddressName(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrName(val);
    	getWritableGnuCashFile().setModified(true);
    }

    /**
     * @see #setAddressLine2(String)
     * @see #setAddressLine3(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine1(final String val) {
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
     * @see #setAddressLine1(String)
     * @see #setAddressLine3(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine2(final String val) {
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
     * @see #setAddressLine1(String)
     * @see #setAddressLine2(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine3(final String val) {
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
     * @see #setAddressLine1(String)
     * @see #setAddressLine2(String)
     * @see #setAddressLine3(String)
     */
    public void setAddressLine4(final String val) {
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

    public void setTel(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrPhone(val);
    	getWritableGnuCashFile().setModified(true);
    }

    public void setFax(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrFax(val);
    	getWritableGnuCashFile().setModified(true);
    }

    public void setEmail(final String val) {
    	if ( val == null ) {
    		throw new IllegalArgumentException("argument <val> is null");
    	}

//    	// sic: empty is allowed
//    	if ( val.trim().length() == 0 ) {
//    		throw new IllegalArgumentException("argument <val> is empty");
//    	}
    	
    	getJwsdpPeer().setAddrEmail(val);
    	getWritableGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("GCshWritableAddressImpl [\n");

		buffer.append(getAddressName() + "\n");
		buffer.append("\n");
		buffer.append(getAddressLine1() + "\n");
		buffer.append(getAddressLine2() + "\n");
		buffer.append(getAddressLine3() + "\n");
		buffer.append(getAddressLine4() + "\n");
		buffer.append("\n");
		buffer.append("Tel.:   " + getTel() + "\n");
		buffer.append("Fax:    " + getFax() + "\n");
		buffer.append("eMail:  " + getEmail() + "\n");

		buffer.append("]");

		return buffer.toString();
    }
    
}
