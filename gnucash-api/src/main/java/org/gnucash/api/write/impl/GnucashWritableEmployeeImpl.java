package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableEmployee;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.impl.aux.GCshWritableAddressImpl;
import org.gnucash.api.write.impl.hlp.GnucashWritableObjectImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashEmployeeImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableEmployeeImpl extends GnucashEmployeeImpl 
                                         implements GnucashWritableEmployee 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnucashWritableFile#createWritableEmployee()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnucashWritableEmployeeImpl(
    		final GncGncEmployee jwsdpPeer,
    		final GnucashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnucashWritableFile#createWritableEmployee()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnucashWritableEmployeeImpl(final GnucashWritableFileImpl file) {
	super(createEmployee_int(file, GCshID.getNew()), file);
    }

    public GnucashWritableEmployeeImpl(final GnucashEmployeeImpl empl) {
	super(empl.getJwsdpPeer(), empl.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into the jwsdp-peer of the file
     */
    protected static GncGncEmployee createEmployee_int(
	    final GnucashWritableFileImpl file,
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
     * @see GnucashWritableEmployee#remove()
     */
    @Override
    public void remove() {
	GncGncEmployee peer = getJwsdpPeer();
	(getGnucashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnucashFile()).removeEmployee(this);
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
     * @see GnucashWritableEmployee#setNumber(java.lang.String)
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
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
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
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
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
        	getJwsdpPeer().setEmployeeAddr(getGnucashFile().getObjectFactory().createAddress());
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
    
        getGnucashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableEmployee#getWritableAddress()
     */
    @Override
    public GCshWritableAddress getWritableAddress() {
        return new GCshWritableAddressImpl(getJwsdpPeer().getEmployeeAddr(), getGnucashFile());
    }

    /**
     * @see GnucashEmployee#getAddress()
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
    // GnucashEmployeeImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	try {
	    return getWritableGnucashFile().getUnpaidWritableVouchersForEmployee(this).size();
	} catch (TaxTableNotFoundException e) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
    }

    // ----------------------------

    @Override
    public List<GnucashEmployeeVoucher> getPaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
    	List<GnucashEmployeeVoucher> result = new ArrayList<GnucashEmployeeVoucher>();
	
	try {
	    for ( GnucashWritableEmployeeVoucher wrtblVch : getPaidWritableVouchers() ) {
		GnucashEmployeeVoucherImpl rdblVch = GnucashWritableEmployeeVoucherImpl.toReadable((GnucashWritableEmployeeVoucherImpl) wrtblVch);
		result.add(rdblVch);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    @Override
    public List<GnucashEmployeeVoucher> getUnpaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException {
    	List<GnucashEmployeeVoucher> result = new ArrayList<GnucashEmployeeVoucher>();
	
	try {
	    for ( GnucashWritableEmployeeVoucher wrtblVch : getUnpaidWritableVouchers() ) {
		GnucashEmployeeVoucherImpl rdblVch = GnucashWritableEmployeeVoucherImpl.toReadable((GnucashWritableEmployeeVoucherImpl) wrtblVch);
		result.add(rdblVch);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    
    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnucashEmployeeImpl.

    public List<GnucashWritableEmployeeVoucher> getPaidWritableVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getPaidWritableVouchersForEmployee(this);
    }

    public List<GnucashWritableEmployeeVoucher> getUnpaidWritableVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableGnucashFile().getUnpaidWritableVouchersForEmployee(this);
    }
    
    // -----------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableEmployeeImpl [");
	
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
