package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashVendorImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableVendorImpl extends GnuCashVendorImpl 
                                       implements GnuCashWritableVendor 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableVendorImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnuCashWritableFile#createWritableVendor()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
	public GnuCashWritableVendorImpl(
			final GncGncVendor jwsdpPeer,
			final GnuCashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnuCashWritableFile#createWritableVendor()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnuCashWritableVendorImpl(final GnuCashWritableFileImpl file) {
	super(createVendor_int(file, GCshID.getNew()), file);
    }

    public GnuCashWritableVendorImpl(final GnuCashVendorImpl vend) {
	super(vend.getJwsdpPeer(), vend.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncGncVendor createVendor_int(
    		final GnuCashWritableFileImpl file, 
    		final GCshID vendID) {
	if ( ! vendID.isSet() ) {
	    throw new IllegalArgumentException("GUID not set!");
	}
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncGncVendor jwsdpVend = file.createGncGncVendorType();
    
        jwsdpVend.setVendorTaxincluded("USEGLOBAL");
        jwsdpVend.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpVend.setVendorUseTt(0);
        jwsdpVend.setVendorName("no name given");
    
        {
            GncGncVendor.VendorGuid id = factory.createGncGncVendorVendorGuid();
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
            GncGncVendor.VendorCurrency currency = factory.createGncGncVendorVendorCurrency();
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

    /**
     * Delete this Vendor and remove it from the file.
     *
     * @see GnuCashWritableVendor#remove()
     */
    @Override
    public void remove() {
	GncGncVendor peer = getJwsdpPeer();
	(getGnuCashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnuCashFile()).removeVendor(this);
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
     * @see GnuCashWritableVendor#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String number) {
	if ( number == null ) {
	    throw new IllegalArgumentException("null number given!");
	}

	if ( number.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty number given!");
	}

	String oldNumber = getNumber();
	getJwsdpPeer().setVendorId(number);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("VendorNumber", oldNumber, number);
	}
    }

    /**
     * @see GnuCashWritableVendor#setName(java.lang.String)
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
	getJwsdpPeer().setVendorName(name);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
    }

    @Override
    public void setAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

	/*
	 * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
	 * getJwsdpPeer().setVendAddr(adrImpl.getJwsdpPeer()); } else
	 */
	
	{

	    if (getJwsdpPeer().getVendorAddr() == null) {
		getJwsdpPeer().setVendorAddr(getGnuCashFile().getObjectFactory().createAddress());
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

	getGnuCashFile().setModified(true);
    }

    /**
     * @param notes user-defined notes about the customer (may be null)
     * @see GnuCashWritableCustomer#setNotes(String)
     */
    @Override
    public void setNotes(final String notes) {
	if ( notes == null ) {
	    throw new IllegalArgumentException("null notesgiven!");
	}

	// Caution: empty string allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty notesgiven!");
//	}

	String oldNotes = getNotes();
	getJwsdpPeer().setVendorNotes(notes);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("notes", oldNotes, notes);
	}
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashWritableVendor#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
	return new GCshWritableAddressImpl(getJwsdpPeer().getVendorAddr(), getGnuCashFile());
    }

    /**
     * @see GnuCashVendor#getAddress()
     */
    @Override
    public GCshWritableAddress getAddress() {
	return getWritableAddress();
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
    public int getNofOpenBills() {
	try {
	    return getWritableGnuCashFile().getUnpaidWritableBillsForVendor_direct(this).size();
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
    public List<GnuCashVendorBill> getPaidBills_direct() throws InvalidCmdtyCurrTypeException {
    	List<GnuCashVendorBill> result = new ArrayList<GnuCashVendorBill>();
	
	try {
	    for ( GnuCashWritableVendorBill wrtblInvc : getPaidWritableBills_direct() ) {
		GnuCashVendorBillImpl rdblInvc = GnuCashWritableVendorBillImpl.toReadable((GnuCashWritableVendorBillImpl) wrtblInvc);
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
    public List<GnuCashVendorBill> getUnpaidBills_direct() throws InvalidCmdtyCurrTypeException {
    	List<GnuCashVendorBill> result = new ArrayList<GnuCashVendorBill>();
	
	try {
	    for ( GnuCashWritableVendorBill wrtblInvc : getUnpaidWritableBills_direct() ) {
		GnuCashVendorBillImpl rdblInvc = GnuCashWritableVendorBillImpl.toReadable((GnuCashWritableVendorBillImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getUnpaidBills_viaAllJobs() {
//	return getWritableGnuCashFile().getUnpaidWritableBillsForVendor_viaAllJobs(this);
//    }

    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnuCashCustomerImpl.

    // ::TODO
//    @Override
//    public List<GnuCashGenerInvoice> getWritableBills() {
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

    public List<GnuCashWritableVendorBill> getPaidWritableBills_direct() throws InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnuCashFile().getPaidWritableBillsForVendor_direct(this);
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getPaidWritableBills_viaAllJobs() {
//	return getWritableGnuCashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    public List<GnuCashWritableVendorBill> getUnpaidWritableBills_direct() throws InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnuCashFile().getUnpaidWritableBillsForVendor_direct(this);
    }

    // ::TODO
//    public Collection<GnuCashWritableJobInvoice>      getUnpaidWritableBills_viaAllJobs() {
//	return getWritableGnuCashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getVendorSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setVendorSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getVendorSlots(),
										 getWritableGnuCashFile(),
										 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getVendorSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getVendorSlots(),
										 	getWritableGnuCashFile(),
										 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getVendorSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getVendorSlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getVendorSlots());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableVendorImpl [");
	
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
