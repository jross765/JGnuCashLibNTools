package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashCustomerImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableCustomerImpl extends GnuCashCustomerImpl 
                                         implements GnuCashWritableCustomer 
{

    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCustomerImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnuCashWritableFile#createWritableCustomer()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableCustomerImpl(
    		final GncGncCustomer jwsdpPeer,
    		final GnuCashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnuCashWritableFile#createWritableCustomer()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnuCashWritableCustomerImpl(final GnuCashWritableFileImpl file) {
	super(createCustomer_int(file, new GCshCustID( new GCshCustID( GCshID.getNew()) ) ), file);
    }

    public GnuCashWritableCustomerImpl(final GnuCashCustomerImpl cust) {
	super(cust.getJwsdpPeer(), cust.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param newID the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncGncCustomer createCustomer_int(
    		final GnuCashWritableFileImpl file,
            final GCshCustID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("argument <newID> is null");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("argument <newID> is null");
		}
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncGncCustomer jwsdpCust = file.createGncGncCustomerType();
    
        jwsdpCust.setCustTaxincluded("USEGLOBAL");
        jwsdpCust.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpCust.setCustDiscount("0/1");
        jwsdpCust.setCustCredit("0/1");
        jwsdpCust.setCustUseTt(0);
        jwsdpCust.setCustName("no name given");
    
        {
            GncGncCustomer.CustGuid id = factory.createGncGncCustomerCustGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(newID.toString());
            jwsdpCust.setCustGuid(id);
            jwsdpCust.setCustId(id.getValue());
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
            jwsdpCust.setCustAddr(addr);
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
            jwsdpCust.setCustShipaddr(saddr);
        }
    
        {
            GncGncCustomer.CustCurrency currency = factory.createGncGncCustomerCustCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            jwsdpCust.setCustCurrency(currency);
        }
    
        jwsdpCust.setCustActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(jwsdpCust);
        file.setModified(true);
    
        LOGGER.debug("createCustomer_int: Created new customer (core): " + jwsdpCust.getCustGuid().getValue());
        
        return jwsdpCust;
    }

    /**
     * Delete this customer and remove it from the file.
     *
     * @see GnuCashWritableCustomer#remove()
     */
    @Override
    public void remove() {
	GncGncCustomer peer = getJwsdpPeer();
	(getGnuCashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnuCashFile()).removeCustomer(this);
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
     * @see GnuCashWritableCustomer#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String number) {
	String oldNumber = getNumber();
	getJwsdpPeer().setCustId(number);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("customerNumber", oldNumber, number);
	}
    }

    /**
     * @see GnuCashWritableCustomer#setName(java.lang.String)
     */
    @Override
    public void setName(final String name) {
	if ( name == null ) {
	    throw new IllegalArgumentException("argument <name> is null");
	}

	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("argument <name> is empty");
	}

	String oldName = getName();
	getJwsdpPeer().setCustName(name);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    /**
     * @see #setCredit(FixedPointNumber)
     */
    @Override
    public void setDiscount(final FixedPointNumber dscnt) {
	if ( dscnt == null ) {
	    throw new IllegalArgumentException("argument <dscnt> is null");
	}

	FixedPointNumber oldDiscount = getDiscount();
	getJwsdpPeer().setCustDiscount(dscnt.toGnuCashString());
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldDiscount, dscnt);
	}
    }

    /**
     * @see #setDiscount(FixedPointNumber)
     */
    @Override
    public void setCredit(final FixedPointNumber cred) {
	if ( cred == null ) {
	    throw new IllegalArgumentException("argument <cred> is null");
	}

	FixedPointNumber oldCredit = getDiscount();
	getJwsdpPeer().setCustCredit(cred.toGnuCashString());
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldCredit, cred);
	}
    }

    /**
     * @param nts user-defined notes about the customer (may be null)
     * @see GnuCashWritableCustomer#setNotes(String)
     */
    @Override
    public void setNotes(final String nts) {
	if ( nts == null ) {
	    throw new IllegalArgumentException("argument <nts> is null");
	}

	// Caution: empty string are allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("argument <nts> is null");
//	}

        String oldNotes = getNotes();
        getJwsdpPeer().setCustNotes(nts);
        getGnuCashFile().setModified(true);
    
        PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
        if (propertyChangeSupport != null) {
            propertyChangeSupport.firePropertyChange("notes", oldNotes, nts);
        }
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashWritableCustomer#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustAddr(), getGnuCashFile());
    }

    /**
     * @see GnuCashCustomer#getAddress()
     */
    @Override
    public GCshWritableAddress getAddress() {
        return getWritableAddress();
    }

	@Override
	public GCshWritableAddress createWritableAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAddress(GCshWritableAddress impl) {
		// TODO Auto-generated method stub
		
	}

    /**
     * @see #setShippingAddress(GCshAddress)
     */
    @Override
    public void setAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("argument <adr> is null");
	}

        /*
         * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
         * getJwsdpPeer().setCustAddr(adrImpl.getJwsdpPeer()); } else
         */
	
        {
    
            if (getJwsdpPeer().getCustAddr() == null) {
        	getJwsdpPeer().setCustAddr(getGnuCashFile().getObjectFactory().createAddress());
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
    
        getGnuCashFile().setModified(true);
    }
    
    // ----------------------------

    /**
     * @see GnuCashWritableCustomer#getWritableShippingAddress()
     */
    @Override
    public GCshWritableAddress getWritableShippingAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustShipaddr(), getGnuCashFile());
    }

    /**
     * @see GnuCashCustomer#getShippingAddress()
     */
    @Override
    public GCshWritableAddress getShippingAddress() {
    	return getWritableShippingAddress();
    }

	@Override
	public GCshWritableAddress createWritableShippingAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeShippingAddress(GCshWritableAddress impl) {
		// TODO Auto-generated method stub
		
	}

    /**
     * @see #setAddress(GCshAddress)
     */
    @Override
    public void setShippingAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("argument <adr> is null");
	}

        /*
         * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
         * getJwsdpPeer().setCustShipaddr(adrImpl.getJwsdpPeer()); } else
         */
	
        {
    
            if (getJwsdpPeer().getCustShipaddr() == null) {
        	getJwsdpPeer().setCustShipaddr(getGnuCashFile().getObjectFactory().createAddress());
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
        getGnuCashFile().setModified(true);
    }

    // -----------------------------------------------------------------
    // The methods in this part are overridden methods from
    // GnuCashCustomerImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenInvoices() {
	try {
	    return getWritableGnuCashFile().getUnpaidWritableInvoicesForCustomer_direct(this).size();
	} catch (TaxTableNotFoundException e) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
    }

    // ----------------------------

    // ::TODO
//    @Override
//    public Collection<GnuCashGenerInvoice> getInvoices() {
//	Collection<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();
//
//	for ( GnuCashCustomerInvoice invc : getWritableGnuCashFile().getInvoicesForCustomer_direct(this) ) {
//	    retval.add(invc);
//	}
//	
//	for ( GnuCashJobInvoice invc : getWritableGnuCashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }
//
    @Override
    public List<GnuCashCustomerInvoice> getPaidInvoices_direct() {
    	List<GnuCashCustomerInvoice> result = new ArrayList<GnuCashCustomerInvoice>();
	
	try {
	    for ( GnuCashWritableCustomerInvoice wrtblInvc : getPaidWritableInvoices_direct() ) {
		GnuCashCustomerInvoiceImpl rdblInvc = GnuCashWritableCustomerInvoiceImpl.toReadable((GnuCashWritableCustomerInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getPaidInvoices_viaAllJobs() {
//	return getWritableGnuCashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    @Override
    public List<GnuCashCustomerInvoice> getUnpaidInvoices_direct() {
    	List<GnuCashCustomerInvoice> result = new ArrayList<GnuCashCustomerInvoice>();
	
	try {
	    for ( GnuCashWritableCustomerInvoice wrtblInvc : getUnpaidWritableInvoices_direct() ) {
		GnuCashCustomerInvoiceImpl rdblInvc = GnuCashWritableCustomerInvoiceImpl.toReadable((GnuCashWritableCustomerInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getUnpaidInvoices_viaAllJobs() {
//	return getWritableGnuCashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnuCashCustomerImpl.

    // ::TODO
//    @Override
//    public List<GnuCashGenerInvoice> getWritableInvoices() {
//	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();
//
//	for ( GnuCashCustomerInvoice invc : getWritableGnuCashFile().getInvoicesForCustomer_direct(this) ) {
//	    retval.add(invc);
//	}
//	
//	for ( GnuCashJobInvoice invc : getWritableGnuCashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }

    public List<GnuCashWritableCustomerInvoice> getPaidWritableInvoices_direct() throws TaxTableNotFoundException {
	return getWritableGnuCashFile().getPaidWritableInvoicesForCustomer_direct(this);
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getPaidWritableInvoices_viaAllJobs() {
//	return getWritableGnuCashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    public List<GnuCashWritableCustomerInvoice> getUnpaidWritableInvoices_direct() throws TaxTableNotFoundException {
	return getWritableGnuCashFile().getUnpaidWritableInvoicesForCustomer_direct(this);
    }

    // ::TODO
//    public List<GnuCashWritableJobInvoice>      getUnpaidWritableInvoices_viaAllJobs() {
//	return getWritableGnuCashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getCustSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setCustSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getCustSlots(),
									 	 getWritableGnuCashFile(),
									 	 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getCustSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getCustSlots(),
									 	 	getWritableGnuCashFile(),
									 	 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getCustSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getCustSlots(),
									 	 getWritableGnuCashFile(),
									 	 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getCustSlots());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableCustomerImpl [");
	
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
