package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.impl.aux.GCshOwnerImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerJob;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashGenerInvoiceImpl to allow read-write access instead of
 * read-only access.
 */
public abstract class GnuCashWritableGenerJobImpl extends GnuCashGenerJobImpl 
												  implements GnuCashWritableGenerJob 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableGenerJobImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    protected final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Create an editable invoice facading an existing JWSDP-peer.
     *
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param gcshFile      the file to register under
     */
    @SuppressWarnings("exports")
	public GnuCashWritableGenerJobImpl(
			final GncGncJob jwsdpPeer, 
			final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

    public GnuCashWritableGenerJobImpl(final GnuCashGenerJobImpl job) {
    	super(job.getJwsdpPeer(), job.getGnuCashFile());
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
	 * @param cust  the customer the job is from
	 * @param file  the file to add the customer job to
	 * @param jobID the internal id to use. May be null to generate an ID.
	 * @return the jaxb-job
	 */
	protected static GncGncJob createCustomerJob_int(
			final GnuCashWritableFileImpl file, 
			final GCshGenerJobID jobID,
			final GnuCashCustomer cust, 
			final String number, 
			final String name) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( jobID == null ) {
			throw new IllegalArgumentException("argument <kjobID> is null");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("argument <kjobID> is not set");
		}

		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
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
			final GnuCashWritableFileImpl file, 
			final GCshGenerJobID jobID,
			final GnuCashVendor vend, 
			final String number, 
			final String name) {
		if ( file == null ) {
			throw new IllegalArgumentException("argument <file> is null");
		}

		if ( jobID == null ) {
			throw new IllegalArgumentException("argument <jobID> is null");
		}

		if ( !jobID.isSet() ) {
			throw new IllegalArgumentException("argument <jobID> is not set");
		}

		if ( vend == null ) {
			throw new IllegalArgumentException("argument <vend> is null");
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
     * @return 
     * @see GnuCashWritableGenerInvoice#isModifiable()
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
		if ( !isModifiable() ) {
			throw new IllegalStateException(
					"this invoice is NOT modifiable because there already have been made payments for it");
		}
    }

    // -----------------------------------------------------------

    @Override
	public void setOwnerID(GCshID ownID) {
    	if ( ownID == null ) {
    	    throw new IllegalArgumentException("argument <ownID> is null");
    	}
    	
    	if ( ! ownID.isSet() ) {
    	    throw new IllegalArgumentException("argument <ownID> is not set");
    	}
    	
		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot change owner of a job that has invoices");
		}

		GCshOwner oldOwner = new GCshOwnerImpl(getOwnerID(), GCshOwner.JIType.JOB, getGnuCashFile());
		GCshOwner newOwner = new GCshOwnerImpl(ownID, GCshOwner.JIType.JOB, getGnuCashFile());
    	if ( oldOwner.getJobType() != newOwner.getJobType() )
    		throw new IllegalStateException("Job owner type may not change");

		GCshID oldOwnID = getOwnerID();
		if ( oldOwnID.equals(ownID) ) {
			return; // nothing has changed
		}
		
    	try {
    		attemptChange();
			getJwsdpPeer().getJobOwner().getOwnerId().setValue(ownID.get());
	    	getGnuCashFile().setModified(true);
		} catch (GCshIDNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("ownerID", oldOwnID, ownID);
		}
	}

    @Override
	public void setOwner(GCshOwner own) {
    	if ( own == null ) {
    	    throw new IllegalArgumentException("argument <own> is null");
    	}
    	
    	if ( own.getJIType() != GCshOwner.JIType.JOB )
    		throw new IllegalArgumentException("argument <jobID> has wrong GCshOwner JI-type");

    	if ( ! own.getID().isSet() ) {
    	    throw new IllegalArgumentException("unset owner ID given");
    	}
    	
    	if ( getType() != own.getJobType() )
    		throw new IllegalStateException("Invoice owner type may not change");
    	
        setOwnerID(own.getID());
	}

    // ---------------------------------------------------------------

	@Override
	public void setNumber(final String jobNum) {
		if ( jobNum == null ) {
			throw new IllegalArgumentException("argument <jobNum> is null");
		}

		if ( jobNum.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <jobNum> is empty");
		}

		GnuCashGenerJob otherJob = getWritableGnuCashFile().getWritableGenerJobByNumber(jobNum);
		if ( otherJob != null ) {
			if ( !otherJob.getID().equals(getID()) ) {
				throw new IllegalArgumentException("another job (id='" + otherJob.getID()
						+ "' already exists with given job number '" + jobNum + "')");
			}
		}

		String oldJobNumber = getJwsdpPeer().getJobId();
		if ( oldJobNumber.equals(jobNum) ) {
			return; // nothing has changed
		}

		getJwsdpPeer().setJobId(jobNum);
		getWritableGnuCashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("id", oldJobNumber, jobNum);
		}

	}

	@Override
	public void setName(final String jobName) {
		if ( jobName == null ) {
			throw new IllegalArgumentException("argument <jobName> is null");
		}

		if ( jobName.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <jobName> is empty");
		}

		String oldJobName = getJwsdpPeer().getJobName();
		if ( oldJobName.equals(jobName) ) {
			return; // nothing has changed
		}

		getJwsdpPeer().setJobName(jobName);
		getWritableGnuCashFile().setModified(true);
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
		getWritableGnuCashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("active", oldJobActive, jobActive);
		}
	}

    // -----------------------------------------------------------------
    // The methods in this part are overridden methods from
    // GnuCashCustomerImpl.
    // They are actually necessary -- if we used the according methods 
    // in the super class, the results would be incorrect.
    // Admittedly, this is probably not the most elegant solution, but it works.
    // (In fact, I have been bug-hunting long hours before fixing it
    // by these overrides, and to this day, I have not fully understood
    // all the intricacies involved, to be honest. Moving on to other
    // to-dos...).
    // Cf. comments in FileInvoiceManager (write-version).

    @Override
    public int getNofOpenInvoices() {
		try {
			return getWritableGnuCashFile().getUnpaidWritableInvoicesForJob(this).size();
		} catch (TaxTableNotFoundException e) {
			throw new IllegalStateException("Encountered tax table exception");
		}
    }

    // ----------------------------

	@Override
	public List<GnuCashJobInvoice> getInvoices() {
		ArrayList<GnuCashJobInvoice> retval = new ArrayList<GnuCashJobInvoice>();

		for ( GnuCashJobInvoice invc : getWritableGnuCashFile().getInvoicesForJob(this) ) {
			retval.add(new GnuCashWritableJobInvoiceImpl((GnuCashJobInvoiceImpl) invc));
		}

		return retval;
    }

    // ---------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableGenerJobImpl [");
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
