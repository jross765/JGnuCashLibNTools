package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorBillImpl;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.GnucashWritableVendor;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashVendorImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableVendorImpl extends GnucashVendorImpl 
                                       implements GnucashWritableVendor 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableVendorImpl.class);

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
    public GnucashWritableVendorImpl(final GncGncVendor jwsdpPeer,
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
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncGncVendor createVendor_int(
	    final GnucashWritableFileImpl file, 
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
     * @see GnucashWritableVendor#remove()
     */
    @Override
    public void remove() {
	GncGncVendor peer = getJwsdpPeer();
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
	if ( number == null ) {
	    throw new IllegalArgumentException("null number given!");
	}

	if ( number.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty number given!");
	}

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
	if ( name == null ) {
	    throw new IllegalArgumentException("null name given!");
	}

	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty name given!");
	}

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
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

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
	if ( notes == null ) {
	    throw new IllegalArgumentException("null notesgiven!");
	}

	// Caution: empty string allowed here
//	if ( notes.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty notesgiven!");
//	}

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
	return new GCshWritableAddressImpl(getJwsdpPeer().getVendorAddr(), getGnucashFile());
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
    public int getNofOpenBills() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	try {
	    return getWritableGnucashFile().getUnpaidWritableBillsForVendor_direct(this).size();
	} catch (TaxTableNotFoundException e) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
    }

    // ----------------------------

    // ::TODO
//    @Override
//    public Collection<GnucashGenerInvoice> getInvoices() throws WrongInvoiceTypeException, IllegalArgumentException {
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
    public Collection<GnucashVendorBill> getPaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
	Collection<GnucashVendorBill> result = new ArrayList<GnucashVendorBill>();
	
	try {
	    for ( GnucashWritableVendorBill wrtblInvc : getPaidWritableBills_direct() ) {
		GnucashVendorBillImpl rdblInvc = GnucashWritableVendorBillImpl.toReadable((GnucashWritableVendorBillImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getPaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
//	return getWritableGnucashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    @Override
    public Collection<GnucashVendorBill> getUnpaidBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
	Collection<GnucashVendorBill> result = new ArrayList<GnucashVendorBill>();
	
	try {
	    for ( GnucashWritableVendorBill wrtblInvc : getUnpaidWritableBills_direct() ) {
		GnucashVendorBillImpl rdblInvc = GnucashWritableVendorBillImpl.toReadable((GnucashWritableVendorBillImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getUnpaidBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
//	return getWritableGnucashFile().getUnpaidWritableBillsForVendor_viaAllJobs(this);
//    }

    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnucashCustomerImpl.

    // ::TODO
//    @Override
//    public Collection<GnucashGenerInvoice> getWritableBills() throws WrongInvoiceTypeException, IllegalArgumentException {
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

    public Collection<GnucashWritableVendorBill> getPaidWritableBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getPaidWritableBillsForVendor_direct(this);
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getPaidWritableBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
//	return getWritableGnucashFile().getPaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

    public Collection<GnucashWritableVendorBill> getUnpaidWritableBills_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getUnpaidWritableBillsForVendor_direct(this);
    }

    // ::TODO
//    public Collection<GnucashWritableJobInvoice>      getUnpaidWritableBills_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
//	return getWritableGnucashFile().getUnpaidWritableInvoicesForCustomer_viaAllJobs(this);
//    }

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
