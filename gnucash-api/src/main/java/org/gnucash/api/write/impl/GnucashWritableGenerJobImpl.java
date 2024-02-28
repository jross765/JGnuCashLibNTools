package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.WrongOwnerJITypeException;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.impl.GnucashGenerJobImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.GnucashWritableGenerJob;
import org.gnucash.api.write.impl.hlp.GnucashWritableObjectImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashGenerInvoiceImpl to allow read-write access instead of
 * read-only access.
 */
public abstract class GnucashWritableGenerJobImpl extends GnucashGenerJobImpl 
												  implements GnucashWritableGenerJob 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableGenerJobImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    protected final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(getWritableGnucashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Create an editable invoice facading an existing JWSDP-peer.
     *
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param gcshFile      the file to register under
     * @see GnucashGenerInvoiceImpl#GnucashInvoiceImpl(GncGncInvoice,
     *      GnucashFile)
     */
    @SuppressWarnings("exports")
	public GnucashWritableGenerJobImpl(
			final GncGncJob jwsdpPeer, 
			final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

    public GnucashWritableGenerJobImpl(final GnucashGenerJobImpl job) {
    	super(job.getJwsdpPeer(), job.getGnucashFile());
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
	 * @param cust  the customer the job is from
	 * @param file  the file to add the customer job to
	 * @param jobID the internal id to use. May be null to generate an ID.
	 * @return the jaxb-job
	 */
	protected static GncGncJob createCustomerJob_int(
			final GnucashWritableFileImpl file, 
			final GCshID jobID,
			final GnucashCustomer cust, 
			final String number, 
			final String name) {

		if ( file == null ) {
			throw new IllegalArgumentException("null file given");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("GUID not set!");
		}

		if ( cust == null ) {
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
	 * @param vend  the vendor the job is from
	 * @param file  the file to add the vendor job to
	 * @param jobID the internal id to use. May be null to generate an ID.
	 * @return the jaxb-job
	 */
	protected static GncGncJob createVendorJob_int(
			final GnucashWritableFileImpl file, 
			final GCshID jobID,
			final GnucashVendor vend, 
			final String number, 
			final String name) {

		if ( file == null ) {
			throw new IllegalArgumentException("null file given");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("GUID not set!");
		}

		if ( vend == null ) {
			throw new IllegalArgumentException("null vendor given");
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
			owner.setOwnerType(GCshOwner.Type.VENDOR.getCode());

			OwnerId ownerid = factory.createOwnerId();
			ownerid.setType(Const.XML_DATA_TYPE_GUID);
			ownerid.setValue(vend.getID().toString());

			owner.setOwnerId(ownerid);
			owner.setVersion(Const.XML_FORMAT_VERSION);
			jwsdpJob.setJobOwner(owner);
		}

		file.getRootElement().getGncBook().getBookElements().add(jwsdpJob);
		file.setModified(true);

		LOGGER.debug("createVendorJob_int: Created new vendor job (core): " + jwsdpJob.getJobGuid().getValue());

		return jwsdpJob;
	}


    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableGenerInvoice#isModifiable()
     */
    public boolean isModifiable() {
	return true; // ::TODO / ::CHECK
    }

    /**
     * Throw an IllegalStateException if we are not modifiable.
     *
     * @see #isModifiable()
     */
    protected void attemptChange() {
	if (!isModifiable()) {
	    throw new IllegalStateException(
		    "this invoice is NOT changeable because there already have been made payments for it!");
	}
    }

    // -----------------------------------------------------------

    // ::TODO
//	void setOwnerID(String ownerID) {
//	    GCshOwner owner = new GCshOwner(GCshOwner.JIType.INVOICE, ownerID);
//	    getJwsdpPeer().setInvoiceOwner(new GCShOwner(xxx));
//	}

	public void setOwner(GCshOwner owner) throws WrongOwnerJITypeException {
		if ( owner.getJIType() != GCshOwner.JIType.JOB )
			throw new WrongOwnerJITypeException();

		attemptChange();
		getJwsdpPeer().setJobOwner(owner.getJobOwner());
		getGnucashFile().setModified(true);
	}

    // ------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 */
	public void setCustomer(final GnucashCustomer cust) throws WrongJobTypeException {
		if ( getOwnerType() != GCshOwner.Type.CUSTOMER )
			throw new WrongJobTypeException();

		attemptChange();
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(cust.getID().toString());
		getGnucashFile().setModified(true);
	}

	/**
	 * @throws WrongJobTypeException 
	 */
	public void setVendor(final GnucashVendor vend) throws WrongJobTypeException {
		if ( getOwnerType() != GCshOwner.Type.VENDOR )
			throw new WrongJobTypeException();

		attemptChange();
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(vend.getID().toString());
		getGnucashFile().setModified(true);
	}

    // ---------------------------------------------------------------

	@Override
	public void setNumber(final String jobNumber) {
		if ( jobNumber == null ) {
			throw new IllegalArgumentException("null job-number given!");
		}

		if ( jobNumber.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty job-number given!");
		}

		GnucashGenerJob otherJob = getWritableGnucashFile().getWritableGenerJobByNumber(jobNumber);
		if ( otherJob != null ) {
			if ( !otherJob.getID().equals(getID()) ) {
				throw new IllegalArgumentException("another job (id='" + otherJob.getID()
						+ "' already exists with given job number '" + jobNumber + "')");
			}
		}

		String oldJobNumber = getJwsdpPeer().getJobId();
		if ( oldJobNumber.equals(jobNumber) ) {
			return; // nothing has changed
		}

		getJwsdpPeer().setJobId(jobNumber);
		getWritableGnucashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("id", oldJobNumber, jobNumber);
		}

	}

	@Override
	public void setName(final String jobName) {
		if ( jobName == null ) {
			throw new IllegalArgumentException("null job-name given!");
		}

		if ( jobName.trim().length() == 0 ) {
			throw new IllegalArgumentException("empty job-name given!");
		}

		String oldJobName = getJwsdpPeer().getJobName();
		if ( oldJobName.equals(jobName) ) {
			return; // nothing has changed
		}

		getJwsdpPeer().setJobName(jobName);
		getWritableGnucashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("name", oldJobName, jobName);
		}
	}

	/**
	 * @param jobActive true is the job is to be (re)activated, false to deactivate
	 */
	@Override
	public void setActive(final boolean jobActive) {

		boolean oldJobActive = getJwsdpPeer().getJobActive() != 0;
		if ( oldJobActive == jobActive ) {
			return; // nothing has changed
		}

		if ( jobActive ) {
			getJwsdpPeer().setJobActive(1);
		} else {
			getJwsdpPeer().setJobActive(0);
		}
		getWritableGnucashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("active", oldJobActive, jobActive);
		}
	}

    // ---------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnucashWritableGenerJobImpl [");
		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", number=");
		buffer.append(getNumber());

		buffer.append(", name='");
		buffer.append(getName() + "'");

		buffer.append(", owner-type=");
		buffer.append(getOwnerType());

		buffer.append(", cust/vend-id=");
		buffer.append(getOwnerID());

		buffer.append(", is-active=");
		buffer.append(isActive());

		buffer.append("]");
		return buffer.toString();
	}

}
