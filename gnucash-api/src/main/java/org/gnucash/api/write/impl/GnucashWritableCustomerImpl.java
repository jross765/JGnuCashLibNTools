package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.hlp.GnucashWritableObjectImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashCustomerImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableCustomerImpl extends GnucashCustomerImpl 
                                         implements GnucashWritableCustomer 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerImpl.class);

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
    @SuppressWarnings("exports")
    public GnucashWritableCustomerImpl(
    		final GncGncCustomer jwsdpPeer,
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
	super(createCustomer_int(file, GCshID.getNew()), file);
    }

    public GnucashWritableCustomerImpl(final GnucashCustomerImpl cust) {
	super(cust.getJwsdpPeer(), cust.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param newID the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncGncCustomer createCustomer_int(
	    final GnucashWritableFileImpl file,
            final GCshID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
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
     * @see GnucashWritableCustomer#remove()
     */
    @Override
    public void remove() {
	GncGncCustomer peer = getJwsdpPeer();
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
	if ( name == null ) {
	    throw new IllegalArgumentException("null name given!");
	}

	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty name given!");
	}

	String oldName = getName();
	getJwsdpPeer().setCustName(name);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    /**
     * @see #setCredit(FixedPointNumber)
     */
    @Override
    public void setDiscount(final FixedPointNumber discount) {
	if ( discount == null ) {
	    throw new IllegalArgumentException("null discount given!");
	}

	FixedPointNumber oldDiscount = getDiscount();
	getJwsdpPeer().setCustDiscount(discount.toGnucashString());
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldDiscount, discount);
	}
    }

    /**
     * @see #setDiscount(FixedPointNumber)
     */
    @Override
    public void setCredit(final FixedPointNumber credit) {
	if ( credit == null ) {
	    throw new IllegalArgumentException("null credit given!");
	}

	FixedPointNumber oldCredit = getDiscount();
	getJwsdpPeer().setCustCredit(credit.toGnucashString());
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("discount", oldCredit, credit);
	}
    }

    /**
     * @see #setShippingAddress(GCshAddress)
     */
    @Override
    public void setAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

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
     * @see #setAddress(GCshAddress)
     */
    @Override
    public void setShippingAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

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
	if ( notes == null ) {
	    throw new IllegalArgumentException("null notes given!");
	}

	// Caution: empty string are allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty notes given!");
//	}

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
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustAddr(), getGnucashFile());
    }

    /**
     * @see GnucashWritableCustomer#getWritableShippingAddress()
     */
    @Override
    public GCshWritableAddress getWritableShippingAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getCustShipaddr(), getGnucashFile());
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

	public void clean() {
		helper.cleanSlots();
	}

    // -----------------------------------------------------------------
    // The methods in this part are overridden methods from
    // GnucashCustomerImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	try {
	    return getWritableGnucashFile().getUnpaidWritableInvoicesForCustomer_direct(this).size();
	} catch (TaxTableNotFoundException e) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
    }

    // ----------------------------

    // ::TODO
//    @Override
//    public Collection<GnucashGenerInvoice> getInvoices() throws WrongInvoiceTypeException {
//	Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
//
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForCustomer_direct(this) ) {
//	    retval.add(invc);
//	}
//	
//	for ( GnucashJobInvoice invc : getWritableGnucashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }
//
    @Override
    public List<GnucashCustomerInvoice> getPaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
    	List<GnucashCustomerInvoice> result = new ArrayList<GnucashCustomerInvoice>();
	
	try {
	    for ( GnucashWritableCustomerInvoice wrtblInvc : getPaidWritableInvoices_direct() ) {
		GnucashCustomerInvoiceImpl rdblInvc = GnucashWritableCustomerInvoiceImpl.toReadable((GnucashWritableCustomerInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getPaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
//	return getWritableGnucashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    @Override
    public List<GnucashCustomerInvoice> getUnpaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
    	List<GnucashCustomerInvoice> result = new ArrayList<GnucashCustomerInvoice>();
	
	try {
	    for ( GnucashWritableCustomerInvoice wrtblInvc : getUnpaidWritableInvoices_direct() ) {
		GnucashCustomerInvoiceImpl rdblInvc = GnucashWritableCustomerInvoiceImpl.toReadable((GnucashWritableCustomerInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getUnpaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
//	return getWritableGnucashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnucashCustomerImpl.

    // ::TODO
//    @Override
//    public List<GnucashGenerInvoice> getWritableInvoices() throws WrongInvoiceTypeException {
//	List<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
//
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForCustomer_direct(this) ) {
//	    retval.add(invc);
//	}
//	
//	for ( GnucashJobInvoice invc : getWritableGnucashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }

    public List<GnucashWritableCustomerInvoice> getPaidWritableInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getPaidWritableInvoicesForCustomer_direct(this);
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getPaidWritableInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
//	return getWritableGnucashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    public List<GnucashWritableCustomerInvoice> getUnpaidWritableInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getUnpaidWritableInvoicesForCustomer_direct(this);
    }

    // ::TODO
//    public List<GnucashWritableJobInvoice>      getUnpaidWritableInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
//	return getWritableGnucashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableCustomerImpl [");
	
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
