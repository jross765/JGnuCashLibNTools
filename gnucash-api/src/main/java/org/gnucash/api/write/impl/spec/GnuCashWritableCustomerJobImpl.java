package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customer job that can be modified.
 * 
 * @see GnuCashCustomerJob
 * 
 * @see GnuCashWritableVendorJobImpl
 */
public class GnuCashWritableCustomerJobImpl extends GnuCashWritableGenerJobImpl 
                                            implements GnuCashWritableCustomerJob 
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableCustomerJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer the XML(jaxb)-object we are fronting.
	 * @param gcshFile      the file we belong to
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableCustomerJobImpl(final GncGncJob jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param owner the customer the job is from
	 * @param file  the file to add the customer job to
	 * @param number 
	 * @param name 
	 */
	public GnuCashWritableCustomerJobImpl(
			final GnuCashWritableFileImpl file, 
			final GnuCashCustomer owner,
			final String number, 
			final String name) {
		super(createCustomerJob_int(file, new GCshGenerJobID( GCshID.getNew() ), owner, number, name), file);
	}

	public GnuCashWritableCustomerJobImpl(GnuCashWritableGenerJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GCshOwner.Type.CUSTOMER )
			throw new WrongJobTypeException();
	}

	public GnuCashWritableCustomerJobImpl(GnuCashCustomerJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashWritableCustomerJob#remove()
	 */
	public void remove() {
		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot remove a job that has invoices");
		}
		GnuCashWritableFileImpl writableFile = (GnuCashWritableFileImpl) getGnuCashFile();
		writableFile.getRootElement().getGncBook().getBookElements().remove(getJwsdpPeer());
		writableFile.removeGenerJob(this);
	}

	// ---------------------------------------------------------------

	/**
	 * @return the ID of the customer who/that owns the Job 
	 */
	public GCshCustID getCustomerID() {
		return new GCshCustID( getOwnerID() );
	}

	/**
	 * @return the customer who/that owns the Job 
	 */
	public GnuCashCustomer getCustomer() {
		return getGnuCashFile().getCustomerByID(getCustomerID());
	}

	// ---------------------------------------------------------------

//    /**
//     * @see GnuCashWritableCustomerJob#setCustomerType(java.lang.String)
//     */
//    public void setCustomerType(final String customerType) {
//	if (customerType == null) {
//	    throw new IllegalArgumentException("argument <customerType> is null");
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
	 * @see GnuCashWritableCustomerJob#setCustomer(GnuCashCustomer)
	 */
	public void setCustomer(final GnuCashCustomer cust) {
		if ( cust == null ) {
			throw new IllegalArgumentException("argument <cust> is null");
		}

		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot change customer of a job that has invoices");
		}

		GnuCashCustomer oldCust = getCustomer();
		if ( oldCust == cust ||
			 oldCust.getID().equals(getID()) ) {
			return; // nothing has changed
		}

		attemptChange();
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(cust.getID().toString());
		getWritableGnuCashFile().setModified(true);
		
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("customer", oldCust, cust);
		}
	}

	// ---------------------------------------------------------------
	// The methods in this part are overridden methods from
	// GnuCashGenerJobImpl.
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
			return getWritableGnuCashFile().getUnpaidWritableInvoicesForJob(this).size();
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
//	for ( GnuCashCustomerInvoice invc : getWritableGnuCashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }
//

	@Override
	public List<GnuCashJobInvoice> getPaidInvoices() {
		List<GnuCashJobInvoice> result = new ArrayList<GnuCashJobInvoice>();

		try {
			for ( GnuCashWritableJobInvoice wrtblInvc : getPaidWritableInvoices() ) {
				GnuCashJobInvoiceImpl rdblInvc = GnuCashWritableJobInvoiceImpl
						.toReadable((GnuCashWritableJobInvoiceImpl) wrtblInvc);
				result.add(rdblInvc);
			}
		} catch (TaxTableNotFoundException exc) {
			throw new IllegalStateException("Encountered tax table exception");
		}

		return result;
	}

	@Override
	public List<GnuCashJobInvoice> getUnpaidInvoices() {
		List<GnuCashJobInvoice> result = new ArrayList<GnuCashJobInvoice>();

		try {
			for ( GnuCashWritableJobInvoice wrtblInvc : getUnpaidWritableInvoices() ) {
				GnuCashJobInvoiceImpl rdblInvc = GnuCashWritableJobInvoiceImpl
						.toReadable((GnuCashWritableJobInvoiceImpl) wrtblInvc);
				result.add(rdblInvc);
			}
		} catch (TaxTableNotFoundException exc) {
			throw new IllegalStateException("Encountered tax table exception");
		}

		return result;
	}

	// -----------------------------------------------------------------
	// The methods in this part are the "writable"-variants of
	// the according ones in the super class GnuCashCustomerImpl.

	// ::TODO
//    @Override
//    public List<GnuCashGenerInvoice> getWritableInvoices() {
//	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();
//
//	for ( GnuCashCustomerInvoice invc : getWritableGnuCashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }

	public List<GnuCashWritableJobInvoice> getPaidWritableInvoices()
			throws TaxTableNotFoundException {
		return getWritableGnuCashFile().getPaidWritableInvoicesForJob(this);
	}

	public List<GnuCashWritableJobInvoice> getUnpaidWritableInvoices()
			throws TaxTableNotFoundException {
		return getWritableGnuCashFile().getUnpaidWritableInvoicesForJob(this);
	}

}
