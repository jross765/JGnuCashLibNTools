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
     * @param gcshFile 
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
     * @see GnuCashVendor#getAddress()
     */
    @Override
	public String getName() {
		if ( jwsdpPeer.getAddrName() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrName();
    }

    /**
     * @see GnuCashVendor#getAddress
     */
    @Override
	public String getLine1() {
		if ( jwsdpPeer.getAddrAddr1() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrAddr1();
    }

    /**
     * @see GnuCashVendor#getAddress
     */
    @Override
	public String getLine2() {
		if ( jwsdpPeer.getAddrAddr2() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrAddr2();
    }

    /**
     * @return third and last line below the name
     */
    @Override
	public String getLine3() {
		if ( jwsdpPeer.getAddrAddr3() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrAddr3();
    }

    /**
     * @return fourth and last line below the name
     */
    @Override
	public String getLine4() {
		if ( jwsdpPeer.getAddrAddr4() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrAddr4();
    }

    /**
     * @return telephone
     */
    @Override
	public String getTel() {
		if ( jwsdpPeer.getAddrPhone() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrPhone();
    }

    /**
     * @return Fax
     */
    @Override
	public String getFax() {
		if ( jwsdpPeer.getAddrFax() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrFax();
    }

    /**
     * @return Email
     */
    @Override
	public String getEmail() {
		if ( jwsdpPeer.getAddrEmail() == null ) {
			return "";
		}
		return jwsdpPeer.getAddrEmail();
    }

    // ---------------------------------------------------------------
    // Old names
    
    @Override
    @Deprecated
    public String getAddressName() {
    	return getName();
    }

    @Override
    @Deprecated
    public String getAddressLine1() {
    	return getLine1();
    }

    @Override
    @Deprecated
    public String getAddressLine2() {
    	return getLine2();
    }

    @Override
    @Deprecated
    public String getAddressLine3() {
    	return getLine3();
    }

    @Override
    @Deprecated
    public String getAddressLine4() {
    	return getLine4();
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("GCshAddressImpl [\n");

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
