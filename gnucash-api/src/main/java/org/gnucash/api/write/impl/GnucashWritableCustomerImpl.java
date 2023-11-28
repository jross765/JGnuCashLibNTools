package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashWritableCustomerImpl extends GnucashCustomerImpl 
                                         implements GnucashWritableCustomer 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerImpl.class);

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer alredy entered into th jwsdp-peer of the file
     */
    protected static GncV2.GncBook.GncGncCustomer createCustomer(final GnucashWritableFileImpl file,
            final String guid) {
    
        if (guid == null) {
            throw new IllegalArgumentException("null guid given!");
        }
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncV2.GncBook.GncGncCustomer cust = file.createGncGncCustomerType();
    
        cust.setCustTaxincluded("USEGLOBAL");
        cust.setVersion(Const.XML_FORMAT_VERSION);
        cust.setCustDiscount("0/1");
        cust.setCustCredit("0/1");
        cust.setCustUseTt(0);
        cust.setCustName("no name given");
    
        {
            GncV2.GncBook.GncGncCustomer.CustGuid id = factory.createGncV2GncBookGncGncCustomerCustGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(guid);
            cust.setCustGuid(id);
            cust.setCustId(id.getValue());
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
            cust.setCustAddr(addr);
        }
    
        {
            org.gnucash.api.generated.Address saddr = factory.createAddress();
            saddr.setAddrAddr1("");
            saddr.setAddrAddr2("");
            saddr.setAddrAddr3("");
            saddr.setAddrAddr4("");
            saddr.setAddrName("");
            saddr.setAddrEmail("");
            saddr.setAddrFax("");
            saddr.setAddrPhone("");
            saddr.setVersion(Const.XML_FORMAT_VERSION);
            cust.setCustShipaddr(saddr);
        }
    
        {
            GncV2.GncBook.GncGncCustomer.CustCurrency currency = factory.createGncV2GncBookGncGncCustomerCustCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            cust.setCustCurrency(currency);
        }
    
        cust.setCustActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(cust);
        file.setModified(true);
    
        return cust;
    }

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnucashWritableFile#createWritableCustomer()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    protected GnucashWritableCustomerImpl(final GncV2.GncBook.GncGncCustomer jwsdpPeer,
	    final GnucashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnucashWritableFile#createWritableCustomer()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnucashWritableCustomerImpl(final GnucashWritableFileImpl file) {
	super(createCustomer(file, file.createGUID()), file);
    }

    public GnucashWritableCustomerImpl(GnucashCustomerImpl cust) {
	super(cust.getJwsdpPeer(), cust.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Delete this customer and remove it from the file.
     *
     * @see GnucashWritableCustomer#remove()
     */
    @Override
    public void remove() {
	GncV2.GncBook.GncGncCustomer peer = getJwsdpPeer();
	(getGnucashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnucashFile()).removeCustomer(this);
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
     * @see GnucashWritableCustomer#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String number) {
	String oldNumber = getNumber();
	getJwsdpPeer().setCustId(number);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("customerNumber", oldNumber, number);
	}
    }

    /**
     * @see GnucashWritableCustomer#setName(java.lang.String)
     */
    @Override
    public void setName(final String name) {
	String oldName = getName();
	getJwsdpPeer().setCustName(name);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    /**
     * @see GnucashWritableCustomer#setDiscount(java.lang.String)
     */
    @Override
    public void setDiscount(final FixedPointNumber discount) {
	FixedPointNumber oldDiscount = getDiscount();
	getJwsdpPeer().setCustDiscount(discount.toGnucashString());
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldDiscount, discount);
	}
    }

    /**
     * @see GnucashWritableCustomer#setDiscount(java.lang.String)
     */
    @Override
    public void setCredit(final FixedPointNumber credit) {
	FixedPointNumber oldCredit = getDiscount();
	getJwsdpPeer().setCustCredit(credit.toGnucashString());
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldCredit, credit);
	}
    }

    /**
     * @see GnucashWritableCustomer#setAdress(org.gnucash.fileformats.gnucash.GnucashCustomer.ShippingAdress)
     */
    @Override
    public void setAddress(final GCshAddress adr) {
        /*
         * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
         * getJwsdpPeer().setCustAddr(adrImpl.getJwsdpPeer()); } else
         */
	
        {
    
            if (getJwsdpPeer().getCustAddr() == null) {
        	getJwsdpPeer().setCustAddr(getGnucashFile().getObjectFactory().createAddress());
            }
    
            getJwsdpPeer().getCustAddr().setAddrAddr1(adr.getAddressLine1());
            getJwsdpPeer().getCustAddr().setAddrAddr2(adr.getAddressLine2());
            getJwsdpPeer().getCustAddr().setAddrAddr3(adr.getAddressLine3());
            getJwsdpPeer().getCustAddr().setAddrAddr4(adr.getAddressLine4());
            getJwsdpPeer().getCustAddr().setAddrName(adr.getAddressName());
            getJwsdpPeer().getCustAddr().setAddrEmail(adr.getEmail());
            getJwsdpPeer().getCustAddr().setAddrFax(adr.getFax());
            getJwsdpPeer().getCustAddr().setAddrPhone(adr.getTel());
        }
    
        getGnucashFile().setModified(true);
    }

    /**
     * @see GnucashWritableCustomer#setShippingAddress(GCshWritableAddress)
     */
    @Override
    public void setShippingAddress(final GCshAddress adr) {
        /*
         * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
         * getJwsdpPeer().setCustShipaddr(adrImpl.getJwsdpPeer()); } else
         */
	
        {
    
            if (getJwsdpPeer().getCustShipaddr() == null) {
        	getJwsdpPeer().setCustShipaddr(getGnucashFile().getObjectFactory().createAddress());
            }
    
            getJwsdpPeer().getCustShipaddr().setAddrAddr1(adr.getAddressLine1());
            getJwsdpPeer().getCustShipaddr().setAddrAddr2(adr.getAddressLine2());
            getJwsdpPeer().getCustShipaddr().setAddrAddr3(adr.getAddressLine3());
            getJwsdpPeer().getCustShipaddr().setAddrAddr4(adr.getAddressLine4());
            getJwsdpPeer().getCustShipaddr().setAddrName(adr.getAddressName());
            getJwsdpPeer().getCustShipaddr().setAddrEmail(adr.getEmail());
            getJwsdpPeer().getCustShipaddr().setAddrFax(adr.getFax());
            getJwsdpPeer().getCustShipaddr().setAddrPhone(adr.getTel());
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
        getJwsdpPeer().setCustNotes(notes);
        getGnucashFile().setModified(true);
    
        PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
        }
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableCustomer#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustAddr());
    }

    /**
     * @see GnucashWritableCustomer#getWritableShippingAddress()
     */
    @Override
    public GCshWritableAddress getWritableShippingAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustShipaddr());
    }

    /**
     * @see GnucashCustomer#getAddress()
     */
    @Override
    public GCshWritableAddress getAddress() {
        return getWritableAddress();
    }

    /**
     * @see GnucashCustomer#getShippingAddress()
     */
    @Override
    public GCshWritableAddress getShippingAddress() {
	return getWritableShippingAddress();
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

}