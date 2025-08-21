package org.gnucash.api.write.impl.aux;

import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
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
     * @see GCshWritableAddress#setAddressName(java.lang.String)
     */
    public void setAddressName(final String a) {
    	getJwsdpPeer().setAddrName(a);
    	// TODO: setModified()
    }

    /**
     * @see #setAddressLine2(String)
     * @see #setAddressLine3(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine1(final String a) {
    	getJwsdpPeer().setAddrAddr1(a);
    	// TODO: setModified()
    }

    /**
     * @see #setAddressLine1(String)
     * @see #setAddressLine3(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine2(final String a) {
    	getJwsdpPeer().setAddrAddr2(a);
    	// TODO: setModified()
    }

    /**
     * @see #setAddressLine1(String)
     * @see #setAddressLine2(String)
     * @see #setAddressLine4(String)
     */
    public void setAddressLine3(final String a) {
    	getJwsdpPeer().setAddrAddr3(a);
    	// TODO: setModified()
    }

    /**
     * @see #setAddressLine1(String)
     * @see #setAddressLine2(String)
     * @see #setAddressLine3(String)
     */
    public void setAddressLine4(final String a) {
    	getJwsdpPeer().setAddrAddr4(a);
    	// TODO: setModified()
    }

    public void setTel(final String a) {
    	getJwsdpPeer().setAddrPhone(a);
    	// TODO: setModified()
    }

    public void setFax(final String a) {
    	getJwsdpPeer().setAddrFax(a);
    	// TODO: setModified()
    }

    public void setEmail(final String a) {
    	getJwsdpPeer().setAddrEmail(a);
    	// TODO: setModified()
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

		buffer.append("]\n");

		return buffer.toString();
    }
    
}
