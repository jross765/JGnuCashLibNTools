package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashCustomerJobImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableCustomerJobImpl extends GnucashCustomerJobImpl 
                                            implements GnucashWritableCustomerJob 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerJobImpl.class);

    /**
     * @param jwsdpPeer the XML(jaxb)-object we are fronting.
     * @param file      the file we belong to
     */
    @SuppressWarnings("exports")
    public GnucashWritableCustomerJobImpl(
	    final GncGncJob jwsdpPeer, 
	    final GnucashFile file) {
	super(jwsdpPeer, file);
    }

    /**
     * @param owner the customer the job is from
     * @param file  the file to add the customer job to
     */
    public GnucashWritableCustomerJobImpl(
	    final GnucashWritableFileImpl file, 
	    final GnucashCustomer owner,
	    final String number, 
	    final String name) {
	super(createCustomerJob_int(file, GCshID.getNew(), owner, number, name), file);
    }

    public GnucashWritableCustomerJobImpl(GnucashCustomerJobImpl job) {
	super(job.getJwsdpPeer(), job.getFile());
    }

    // -----------------------------------------------------------------

    /**
     * @throws WrongInvoiceTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableCustomerJob#remove()
     */
    public void remove() throws WrongInvoiceTypeException, IllegalArgumentException {
	if (!getInvoices().isEmpty()) {
	    throw new IllegalStateException("cannot remove a job that has invoices!");
	}
	GnucashWritableFileImpl writableFile = (GnucashWritableFileImpl) getFile();
	writableFile.getRootElement().getGncBook().getBookElements().remove(getJwsdpPeer());
	writableFile.removeGenerJob(this);
    }

    /**
     * @param cust the customer the job is from
     * @param file     the file to add the customer job to
     * @param jobID     the internal id to use. May be null to generate an ID.
     * @return the jaxb-job
     */
    private static GncGncJob createCustomerJob_int(
	    final GnucashWritableFileImpl file, 
	    final GCshID jobID,
	    final GnucashCustomer cust,
	    final String number,
	    final String name) {

	if (file == null) {
	    throw new IllegalArgumentException("null file given");
	}

	if ( ! jobID.isSet() ) {
	    throw new IllegalArgumentException("GUID not set!");
	}
	
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	ObjectFactory factory = file.getObjectFactory();

	GncGncJob jwsdpJob = file.createGncGncJobType();

	jwsdpJob.setJobActive(1);
	jwsdpJob.setJobId(number);
	jwsdpJob.setJobName(name);
	jwsdpJob.setVersion(Const.XML_FORMAT_VERSION);

	{
	    GncGncJob.JobGuid id = factory.createGncGncJobJobGuid();
	    id.setType(Const.XML_DATA_TYPE_GUID);
	    id.setValue(jobID.toString());
	    jwsdpJob.setJobGuid(id);
	}

	{
	    GncGncJob.JobOwner owner = factory.createGncGncJobJobOwner();
	    owner.setOwnerType(GCshOwner.Type.CUSTOMER.getCode());

	    OwnerId ownerid = factory.createOwnerId();
	    ownerid.setType(Const.XML_DATA_TYPE_GUID);
	    ownerid.setValue(cust.getID().toString());

	    owner.setOwnerId(ownerid);
	    owner.setVersion(Const.XML_FORMAT_VERSION);
	    jwsdpJob.setJobOwner(owner);
	}

	file.getRootElement().getGncBook().getBookElements().add(jwsdpJob);
	file.setModified(true);

        LOGGER.debug("createCustomerJob_int: Created new customer job (core): " + jwsdpJob.getJobGuid().getValue());
        
        return jwsdpJob;
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    protected GnucashWritableFileImpl getWritableFile() {
	return (GnucashWritableFileImpl) getFile();
    }

//    /**
//     * @see GnucashWritableCustomerJob#setCustomerType(java.lang.String)
//     */
//    public void setCustomerType(final String customerType) {
//	if (customerType == null) {
//	    throw new IllegalArgumentException("null 'customerType' given!");
//	}
//
//	Object old = getJwsdpPeer().getJobOwner().getOwnerType();
//	if (old == customerType) {
//	    return; // nothing has changed
//	}
//	getJwsdpPeer().getJobOwner().setOwnerType(customerType);
//	getWritableFile().setModified(true);
//	// <<insert code to react further to this change here
//	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
//	if (propertyChangeFirer != null) {
//	    propertyChangeFirer.firePropertyChange("customerType", old, customerType);
//	}
//    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableCustomerJob#setCustomer(GnucashCustomer)
     */
    public void setCustomer(final GnucashCustomer cust) throws WrongInvoiceTypeException, IllegalArgumentException {
	if (!getInvoices().isEmpty()) {
	    throw new IllegalStateException("cannot change customer of a job that has invoices!");
	}

	if (cust == null) {
	    throw new IllegalArgumentException("null 'customer' given!");
	}

	GnucashCustomer oldCust = getCustomer();
	if (oldCust == cust) {
	    return; // nothing has changed
	}
	getJwsdpPeer().getJobOwner().getOwnerId().setValue(cust.getID().toString());
	getWritableFile().setModified(true);
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("customer", oldCust, cust);
	}
    }

    /**
     * @see GnucashWritableCustomerJob#setNumber(java.lang.String)
     */
    @Override
    public void setNumber(final String jobNumber) {
	if (jobNumber == null) {
	    throw new IllegalArgumentException("null job-number given!");
	}
	
	if (jobNumber.trim().length() == 0) {
	    throw new IllegalArgumentException("empty job-number given!");
	}

	GnucashGenerJob otherJob = getWritableFile().getWritableGenerJobByNumber(jobNumber);
	if ( otherJob != null ) { 
	    if ( ! otherJob.getID().equals(getID()) ) {
		throw new IllegalArgumentException(
		    "another job (id='" + otherJob.getID() + "' already exists with given job number '" + jobNumber + "')");
	    }
	}

	String oldJobNumber = getJwsdpPeer().getJobId();
	if (oldJobNumber.equals(jobNumber)) {
	    return; // nothing has changed
	}
	
	getJwsdpPeer().setJobId(jobNumber);
	getWritableFile().setModified(true);
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("id", oldJobNumber, jobNumber);
	}

    }

    /**
     * @see GnucashWritableCustomerJob#setName(java.lang.String)
     */
    public void setName(final String jobName) {
	if (jobName == null) {
	    throw new IllegalArgumentException("null job-name given!");
	}

	if (jobName.trim().length() == 0) {
	    throw new IllegalArgumentException("empty job-name given!");
	}

	String oldJobName = getJwsdpPeer().getJobName();
	if (oldJobName.equals(jobName)) {
	    return; // nothing has changed
	}
	
	getJwsdpPeer().setJobName(jobName);
	getWritableFile().setModified(true);
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("name", oldJobName, jobName);
	}
    }

    /**
     * @param jobActive true is the job is to be (re)activated, false to deactivate
     */
    public void setActive(final boolean jobActive) {

	boolean oldJobActive = getJwsdpPeer().getJobActive() != 0;
	if (oldJobActive == jobActive) {
	    return; // nothing has changed
	}
	
	if (jobActive) {
	    getJwsdpPeer().setJobActive(1);
	} else {
	    getJwsdpPeer().setJobActive(0);
	}
	getWritableFile().setModified(true);
	// <<insert code to react further to this change here
	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
	if (propertyChangeFirer != null) {
	    propertyChangeFirer.firePropertyChange("active", oldJobActive, jobActive);
	}
    }

// ------------------------ support for propertyChangeListeners ------------------

    /**
     * support for firing PropertyChangeEvents. (gets initialized only if we really
     * have listeners)
     */
    private volatile PropertyChangeSupport myPropertyChange = null;

    /**
     * Returned value may be null if we never had listeners.
     *
     * @return Our support for firing PropertyChangeEvents
     */
    protected PropertyChangeSupport getPropertyChangeSupport() {
	return myPropertyChange;
    }

    /**
     * Add a PropertyChangeListener to the listener list. The listener is registered
     * for all properties.
     *
     * @param listener The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
    public final void addPropertyChangeListener(final PropertyChangeListener listener) {
	if (myPropertyChange == null) {
	    myPropertyChange = new PropertyChangeSupport(this);
	}
	myPropertyChange.addPropertyChangeListener(listener);
    }

    /**
     * Add a PropertyChangeListener for a specific property. The listener will be
     * invoked only when a call on firePropertyChange names that specific property.
     *
     * @param propertyName The name of the property to listen on.
     * @param listener     The PropertyChangeListener to be added
     */
    @SuppressWarnings("exports")
    public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
	if (myPropertyChange == null) {
	    myPropertyChange = new PropertyChangeSupport(this);
	}
	myPropertyChange.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName The name of the property that was listened on.
     * @param listener     The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
    public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
	if (myPropertyChange != null) {
	    myPropertyChange.removePropertyChangeListener(propertyName, listener);
	}
    }

    /**
     * Remove a PropertyChangeListener from the listener list. This removes a
     * PropertyChangeListener that was registered for all properties.
     *
     * @param listener The PropertyChangeListener to be removed
     */
    @SuppressWarnings("exports")
    public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
	if (myPropertyChange != null) {
	    myPropertyChange.removePropertyChangeListener(listener);
	}
    }

    // ---------------------------------------------------------------
    // The methods in this part are overridden methods from
    // GnucashGenerJobImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	try {
	    return getWritableFile().getUnpaidWritableInvoicesForJob(this).size();
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
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }
//

    @Override
    public Collection<GnucashJobInvoice> getPaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashJobInvoice> result = new ArrayList<GnucashJobInvoice>();
	
	try {
	    for ( GnucashWritableJobInvoice wrtblInvc : getPaidWritableInvoices() ) {
		GnucashJobInvoiceImpl rdblInvc = GnucashWritableJobInvoiceImpl.toReadable((GnucashWritableJobInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	Collection<GnucashJobInvoice> result = new ArrayList<GnucashJobInvoice>();
	
	try {
	    for ( GnucashWritableJobInvoice wrtblInvc : getUnpaidWritableInvoices() ) {
		GnucashJobInvoiceImpl rdblInvc = GnucashWritableJobInvoiceImpl.toReadable((GnucashWritableJobInvoiceImpl) wrtblInvc);
		result.add(rdblInvc);
	    }
	} catch ( TaxTableNotFoundException exc ) {
	    throw new IllegalStateException("Encountered tax table exception");
	}
	
	return result;
    }

    // -----------------------------------------------------------------
    // The methods in this part are the "writable"-variants of 
    // the according ones in the super class GnucashCustomerImpl.

    // ::TODO
//    @Override
//    public Collection<GnucashGenerInvoice> getWritableInvoices() throws WrongInvoiceTypeException, IllegalArgumentException {
//	Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
//
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }

    public Collection<GnucashWritableJobInvoice> getPaidWritableInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableFile().getPaidWritableInvoicesForJob(this);
    }

    public Collection<GnucashWritableJobInvoice> getUnpaidWritableInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException, InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
	return getWritableFile().getUnpaidWritableInvoicesForJob(this);
    }

}
