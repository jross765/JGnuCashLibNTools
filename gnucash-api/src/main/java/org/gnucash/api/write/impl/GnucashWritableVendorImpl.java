package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.GnucashWritableVendor;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashWritableVendorImpl extends GnucashVendorImpl 
                                       implements GnucashWritableVendor 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableVendorImpl.class);

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer alredy entered into th jwsdp-peer of the file
     */
    protected static GncV2.GncBook.GncGncVendor createVendor_int(
	    final GnucashWritableFileImpl file, 
	    final GCshID vendID) {
	if ( ! vendID.isSet() ) {
	    throw new IllegalArgumentException("GUID not set!");
	}
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncV2.GncBook.GncGncVendor jwsdpVend = file.createGncGncVendorType();
    
        jwsdpVend.setVendorTaxincluded("USEGLOBAL");
        jwsdpVend.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpVend.setVendorUseTt(0);
        jwsdpVend.setVendorName("no name given");
    
        {
            GncV2.GncBook.GncGncVendor.VendorGuid id = factory.createGncV2GncBookGncGncVendorVendorGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(vendID.toString());
            jwsdpVend.setVendorGuid(id);
            jwsdpVend.setVendorId(id.getValue());
        }
    
        {
            org.gnucash.api.generated.Address addr = factory.createAddress();
            addr.setAddrAddr1("");
            addr.setAddrAddr2("");
            addr.setAddrName("");
            addr.setAddrAddr3("");
            addr.setAddrAddr4("");
            addr.setAddrName("");
            addr.setAddrEmail("");
            addr.setAddrFax("");
            addr.setAddrPhone("");
            addr.setVersion(Const.XML_FORMAT_VERSION);
            jwsdpVend.setVendorAddr(addr);
        }
    
        {
            GncV2.GncBook.GncGncVendor.VendorCurrency currency = factory.createGncV2GncBookGncGncVendorVendorCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            jwsdpVend.setVendorCurrency(currency);
        }
    
        jwsdpVend.setVendorActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(jwsdpVend);
        file.setModified(true);
    
        LOGGER.debug("createVendor_int: Created new vendor (core): " + jwsdpVend.getVendorGuid().getValue());
        
        return jwsdpVend;
    }

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnucashWritableFile#createWritableVendor()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    public GnucashWritableVendorImpl(final GncV2.GncBook.GncGncVendor jwsdpPeer,
	    final GnucashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnucashWritableFile#createWritableVendor()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnucashWritableVendorImpl(final GnucashWritableFileImpl file) {
	super(createVendor_int(file, GCshID.getNew()), file);
    }

    public GnucashWritableVendorImpl(final GnucashVendorImpl vend) {
	super(vend.getJwsdpPeer(), vend.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Delete this Vendor and remove it from the file.
     *
     * @see GnucashWritableVendor#remove()
     */
    @Override
    public void remove() {
	GncV2.GncBook.GncGncVendor peer = getJwsdpPeer();
	(getGnucashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnucashFile()).removeVendor(this);
    }

    // ---------------------------------------------------------------

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getWritableGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableVendor#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String number) {
	String oldNumber = getNumber();
	getJwsdpPeer().setVendorId(number);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("VendorNumber", oldNumber, number);
	}
    }

    /**
     * @see GnucashWritableVendor#setName(java.lang.String)
     */
    @Override
    public void setName(final String name) {
	String oldName = getName();
	getJwsdpPeer().setVendorName(name);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    /**
     * @see GnucashWritableVendor#setAdress(org.gnucash.fileformats.gnucash.GnucashVendor.ShippingAdress)
     */
    @Override
    public void setAddress(final GCshAddress adr) {
	/*
	 * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
	 * getJwsdpPeer().setVendAddr(adrImpl.getJwsdpPeer()); } else
	 */
	
	{

	    if (getJwsdpPeer().getVendorAddr() == null) {
		getJwsdpPeer().setVendorAddr(getGnucashFile().getObjectFactory().createAddress());
	    }

	    getJwsdpPeer().getVendorAddr().setAddrAddr1(adr.getAddressLine1());
	    getJwsdpPeer().getVendorAddr().setAddrAddr2(adr.getAddressLine2());
	    getJwsdpPeer().getVendorAddr().setAddrAddr3(adr.getAddressLine3());
	    getJwsdpPeer().getVendorAddr().setAddrAddr4(adr.getAddressLine4());
	    getJwsdpPeer().getVendorAddr().setAddrName(adr.getAddressName());
	    getJwsdpPeer().getVendorAddr().setAddrEmail(adr.getEmail());
	    getJwsdpPeer().getVendorAddr().setAddrFax(adr.getFax());
	    getJwsdpPeer().getVendorAddr().setAddrPhone(adr.getTel());
	}

	getGnucashFile().setModified(true);
    }

    /**
     * @param notes user-defined notes about the customer (may be null)
     * @see GnucashWritableCustomer#setNotes(String)
     */
    @Override
    public void setNotes(final String notes) {
	String oldNotes = getNotes();
	getJwsdpPeer().setVendorNotes(notes);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
	}
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableVendor#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
	return new GCshWritableAddressImpl(getJwsdpPeer().getVendorAddr());
    }

    /**
     * @see GnucashVendor#getAddress()
     */
    @Override
    public GCshWritableAddress getAddress() {
	return getWritableAddress();
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public void setUserDefinedAttribute(final String name, final String value) {
	helper.setUserDefinedAttribute(name, value);
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableVendorImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", number='");
	buffer.append(getNumber() + "'");
	
	buffer.append(", name='");
	buffer.append(getName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
