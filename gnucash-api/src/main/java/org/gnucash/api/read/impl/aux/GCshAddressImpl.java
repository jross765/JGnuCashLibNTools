package org.gnucash.api.read.impl.aux;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshAddress;

public class GCshAddressImpl implements GCshAddress {

    /**
     * The JWSDP-object we are wrapping.
     */
    private final org.gnucash.api.generated.Address jwsdpPeer;

    /**
     * the file we belong to.
     */
    protected final GnuCashFile myFile;
    
    // -----------------------------------------------------------

    /**
     * @param newPeer the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GCshAddressImpl(
	    final org.gnucash.api.generated.Address newPeer,
	    final GnuCashFile gcshFile) {
	super();

	this.jwsdpPeer = newPeer;
	this.myFile = gcshFile;
    }

    // -----------------------------------------------------------

    /**
     * @return The JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public org.gnucash.api.generated.Address getJwsdpPeer() {
	return jwsdpPeer;
    }

    public GnuCashFile getGnuCashFile() {
	return myFile;
    }

    // -----------------------------------------------------------

    /**
     * @see GnuCashVendor.GCshAddress#getAddressName()
     */
    public String getAddressName() {
	if (jwsdpPeer.getAddrName() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrName();
    }

    /**
     * @see GnuCashVendor.GCshAddress#getAddressLine1()
     */
    public String getAddressLine1() {
	if (jwsdpPeer.getAddrAddr1() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrAddr1();
    }

    /**
     * @see GnuCashVendor.GCshAddress#getAddressLine2()
     */
    public String getAddressLine2() {
	if (jwsdpPeer.getAddrAddr2() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrAddr2();
    }

    /**
     * @return third and last line below the name
     */
    public String getAddressLine3() {
	if (jwsdpPeer.getAddrAddr3() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrAddr3();
    }

    /**
     * @return fourth and last line below the name
     */
    public String getAddressLine4() {
	if (jwsdpPeer.getAddrAddr4() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrAddr4();
    }

    /**
     * @return telephone
     */
    public String getTel() {
	if (jwsdpPeer.getAddrPhone() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrPhone();
    }

    /**
     * @return Fax
     */
    public String getFax() {
	if (jwsdpPeer.getAddrFax() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrFax();
    }

    /**
     * @return Email
     */
    public String getEmail() {
	if (jwsdpPeer.getAddrEmail() == null) {
	    return "";
	}
	return jwsdpPeer.getAddrEmail();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();

	buffer.append("GCshAddressImpl [\n");

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
