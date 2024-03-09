package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashEmployeeImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableEmployeeImpl extends GnuCashEmployeeImpl 
                                         implements GnuCashWritableEmployee 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableEmployeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnuCashWritableFile#createWritableEmployee()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableEmployeeImpl(
    		final GncGncEmployee jwsdpPeer,
    		final GnuCashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnuCashWritableFile#createWritableEmployee()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnuCashWritableEmployeeImpl(final GnuCashWritableFileImpl file) {
	super(createEmployee_int(file, GCshID.getNew()), file);
    }

    public GnuCashWritableEmployeeImpl(final GnuCashEmployeeImpl empl) {
	super(empl.getJwsdpPeer(), empl.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into the jwsdp-peer of the file
     */
    protected static GncGncEmployee createEmployee_int(
    		final GnuCashWritableFileImpl file,
            final GCshID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncGncEmployee jwsdpEmpl = file.createGncGncEmployeeType();
    
        jwsdpEmpl.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpEmpl.setEmployeeUsername("no user name given");
    
        {
            GncGncEmployee.EmployeeGuid id = factory.createGncGncEmployeeEmployeeGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(newID.toString());
            jwsdpEmpl.setEmployeeGuid(id);
            jwsdpEmpl.setEmployeeId(id.getValue());
        }
    
        {
            org.gnucash.api.generated.Address addr = factory.createAddress();
            addr.setAddrAddr1("");
            addr.setAddrAddr2("");
            addr.setAddrName("no name given"); // not absolutely necessary, but recommendable,
                                               // since it's an important part of the preview mask
            addr.setAddrAddr3("");
            addr.setAddrAddr4("");
            addr.setAddrName("");
            addr.setAddrEmail("");
            addr.setAddrFax("");
            addr.setAddrPhone("");
            addr.setVersion(Const.XML_FORMAT_VERSION);
            jwsdpEmpl.setEmployeeAddr(addr);
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
        }
    
        // These two have to be set, else GnuCash runs into a parse error
        {
            jwsdpEmpl.setEmployeeWorkday("8"); // ::MAGIC
            jwsdpEmpl.setEmployeeRate("1");    // ::MAGIC
        }
    
        {
            GncGncEmployee.EmployeeCurrency currency = factory.createGncGncEmployeeEmployeeCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            jwsdpEmpl.setEmployeeCurrency(currency);
        }
    
        jwsdpEmpl.setEmployeeActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(jwsdpEmpl);
        file.setModified(true);
    
        LOGGER.debug("createEmployee_int: Created new employee (core): " + jwsdpEmpl.getEmployeeGuid().getValue());
        
        return jwsdpEmpl;
    }

    /**
     * Delete this employee and remove it from the file.
     *
     * @see GnuCashWritableEmployee#remove()
     */
    @Override
    public void remove() {
	GncGncEmployee peer = getJwsdpPeer();
	(getGnuCashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnuCashFile()).removeEmployee(this);
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
     * @see GnuCashWritableEmployee#setNumber(java.lang.String)
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
	getJwsdpPeer().setEmployeeId(number);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("employeeNumber", oldNumber, number);
	}
    }

    @Override
    public void setUserName(final String userName) {
	if ( userName == null ) {
	    throw new IllegalArgumentException("null user name given!");
	}

	if ( userName.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty user name given!");
	}

	String oldUserName = getUserName();
	getJwsdpPeer().setEmployeeUsername(userName);
	getGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("username", oldUserName, userName);
	}
    }

    @Override
    public void setAddress(final GCshAddress adr) {
	if ( adr == null ) {
	    throw new IllegalArgumentException("null address given!");
	}

        /*
         * if (adr instanceof AddressImpl) { AddressImpl adrImpl = (AddressImpl) adr;
         * getJwsdpPeer().setEmplAddr(adrImpl.getJwsdpPeer()); } else
         */
	
        {
    
            if (getJwsdpPeer().getEmployeeAddr() == null) {
        	getJwsdpPeer().setEmployeeAddr(getGnuCashFile().getObjectFactory().createAddress());
            }
    
            getJwsdpPeer().getEmployeeAddr().setAddrAddr1(adr.getAddressLine1());
            getJwsdpPeer().getEmployeeAddr().setAddrAddr2(adr.getAddressLine2());
            getJwsdpPeer().getEmployeeAddr().setAddrAddr3(adr.getAddressLine3());
            getJwsdpPeer().getEmployeeAddr().setAddrAddr4(adr.getAddressLine4());
            getJwsdpPeer().getEmployeeAddr().setAddrName(adr.getAddressName());
            getJwsdpPeer().getEmployeeAddr().setAddrEmail(adr.getEmail());
            getJwsdpPeer().getEmployeeAddr().setAddrFax(adr.getFax());
            getJwsdpPeer().getEmployeeAddr().setAddrPhone(adr.getTel());
        }
    
        getGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashWritableEmployee#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getEmployeeAddr(), getGnuCashFile());
    }

    /**
     * @see GnuCashEmployee#getAddress()
     */
    @Override
    public GCshWritableAddress getAddress() {
        return getWritableAddress();
    }

    // -----------------------------------------------------------------
    // The methods in this part are overridden methods from
    // GnuCashEmployeeImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenVouchers() {
	try {
	    return getWritableGnuCashFile().getUnpaidWritableVouchersForEmployee(this).size();
	} catch (TaxTableNotFoundException e) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
    }

    // ----------------------------

    @Override
    public List<GnuCashEmployeeVoucher> getPaidVouchers() throws InvalidCmdtyCurrTypeException {
    	List<GnuCashEmployeeVoucher> result = new ArrayList<GnuCashEmployeeVoucher>();
	
	try {
	    for ( GnuCashWritableEmployeeVoucher wrtblVch : getPaidWritableVouchers() ) {
		GnuCashEmployeeVoucherImpl rdblVch = GnuCashWritableEmployeeVoucherImpl.toReadable((GnuCashWritableEmployeeVoucherImpl) wrtblVch);
		result.add(rdblVch);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    @Override
    public List<GnuCashEmployeeVoucher> getUnpaidVouchers() throws InvalidCmdtyCurrTypeException {
    	List<GnuCashEmployeeVoucher> result = new ArrayList<GnuCashEmployeeVoucher>();
	
	try {
	    for ( GnuCashWritableEmployeeVoucher wrtblVch : getUnpaidWritableVouchers() ) {
		GnuCashEmployeeVoucherImpl rdblVch = GnuCashWritableEmployeeVoucherImpl.toReadable((GnuCashWritableEmployeeVoucherImpl) wrtblVch);
		result.add(rdblVch);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    
    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnuCashEmployeeImpl.

    public List<GnuCashWritableEmployeeVoucher> getPaidWritableVouchers() throws TaxTableNotFoundException {
	return getWritableGnuCashFile().getPaidWritableVouchersForEmployee(this);
    }

    public List<GnuCashWritableEmployeeVoucher> getUnpaidWritableVouchers() throws TaxTableNotFoundException {
	return getWritableGnuCashFile().getUnpaidWritableVouchersForEmployee(this);
    }
    
    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getEmployeeSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setEmployeeSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getEmployeeSlots(),
									 	 getWritableGnuCashFile(),
									 	 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getEmployeeSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getEmployeeSlots(),
									 	 	getWritableGnuCashFile(),
									 	 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getEmployeeSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getEmployeeSlots(),
									 	 getWritableGnuCashFile(),
									 	 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getEmployeeSlots());
	}

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableEmployeeImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", number='");
	buffer.append(getNumber() + "'");
	
	buffer.append(", username='");
	buffer.append(getUserName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
