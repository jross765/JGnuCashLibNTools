package org.gnucash.write.impl;

import java.beans.PropertyChangeSupport;

import org.gnucash.Const;
import org.gnucash.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.ObjectFactory;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.aux.GCshAddress;
import org.gnucash.read.impl.GnucashEmployeeImpl;
import org.gnucash.write.GnucashWritableEmployee;
import org.gnucash.write.GnucashWritableFile;
import org.gnucash.write.GnucashWritableObject;
import org.gnucash.write.aux.GCshWritableAddress;
import org.gnucash.write.impl.aux.GCshWritableAddressImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashWritableEmployeeImpl extends GnucashEmployeeImpl 
                                         implements GnucashWritableEmployee 
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeImpl.class);

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer alredy entered into th jwsdp-peer of the file
     */
    protected static GncV2.GncBook.GncGncEmployee createEmployee(final GnucashWritableFileImpl file,
            final String guid) {
    
        if (guid == null) {
            throw new IllegalArgumentException("null guid given!");
        }
    
        ObjectFactory factory = file.getObjectFactory();
    
        GncV2.GncBook.GncGncEmployee empl = file.createGncGncEmployeeType();
    
        empl.setVersion(Const.XML_FORMAT_VERSION);
        empl.setEmployeeUsername("no user name given");
    
        {
            GncV2.GncBook.GncGncEmployee.EmployeeGuid id = factory.createGncV2GncBookGncGncEmployeeEmployeeGuid();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(guid);
            empl.setEmployeeGuid(id);
            empl.setEmployeeId(id.getValue());
        }
    
        {
            org.gnucash.generated.Address addr = factory.createAddress();
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
            empl.setEmployeeAddr(addr);
        }
    
        {
            org.gnucash.generated.Address saddr = factory.createAddress();
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
            empl.setEmployeeWorkday("8"); // ::MAGIC
            empl.setEmployeeRate("1");    // ::MAGIC
        }
    
        {
            GncV2.GncBook.GncGncEmployee.EmployeeCurrency currency = factory.createGncV2GncBookGncGncEmployeeEmployeeCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            empl.setEmployeeCurrency(currency);
        }
    
        empl.setEmployeeActive(1);
    
        file.getRootElement().getGncBook().getBookElements().add(empl);
        file.setModified(true);
    
        return empl;
    }

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
    protected GnucashWritableEmployeeImpl(final GncV2.GncBook.GncGncEmployee jwsdpPeer,
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
	super(createEmployee(file, file.createGUID()), file);
    }

    // ---------------------------------------------------------------

    /**
     * Delete this employee and remove it from the file.
     *
     * @see GnucashWritableEmployee#remove()
     */
    @Override
    public void remove() {
	GncV2.GncBook.GncGncEmployee peer = getJwsdpPeer();
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
	String oldNumber = getNumber();
	getJwsdpPeer().setEmployeeId(number);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("employeeNumber", oldNumber, number);
	}
    }

    /**
     * @see GnucashWritableEmployee#setName(java.lang.String)
     */
    @Override
    public void setUserName(final String userName) {
	String oldUserName = getUserName();
	getJwsdpPeer().setEmployeeUsername(userName);
	getGnucashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("username", oldUserName, userName);
	}
    }

    /**
     * @see GnucashWritableEmployee#setAdress(org.gnucash.fileformats.gnucash.GnucashEmployee.ShippingAdress)
     */
    @Override
    public void setAddress(final GCshAddress adr) {
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
        return new GCshWritableAddressImpl(getJwsdpPeer().getEmployeeAddr());
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

}
