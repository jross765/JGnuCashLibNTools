package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerJobImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customer job that can be modified.
 * 
 * @see GnucashCustomerJob
 * 
 * @see GnucashWritableVendorJobImpl
 */
public class GnucashWritableCustomerJobImpl extends GnucashWritableGenerJobImpl 
                                            implements GnucashWritableCustomerJob 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer the XML(jaxb)-object we are fronting.
	 * @param gcshFile      the file we belong to
	 */
	@SuppressWarnings("exports")
	public GnucashWritableCustomerJobImpl(final GncGncJob jwsdpPeer, final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param owner the customer the job is from
	 * @param file  the file to add the customer job to
	 * @param number 
	 * @param name 
	 */
	public GnucashWritableCustomerJobImpl(
			final GnucashWritableFileImpl file, 
			final GnucashCustomer owner,
			final String number, 
			final String name) {
		super(createCustomerJob_int(file, GCshID.getNew(), owner, number, name), file);
	}

	public GnucashWritableCustomerJobImpl(GnucashWritableGenerJobImpl job) throws WrongJobTypeException {
		super(job.getJwsdpPeer(), job.getGnucashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GCshOwner.Type.CUSTOMER )
			throw new WrongJobTypeException();
	}

	public GnucashWritableCustomerJobImpl(GnucashCustomerJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnucashFile());
	}

	// ---------------------------------------------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 * @see GnucashWritableCustomerJob#remove()
	 */
	public void remove() throws WrongInvoiceTypeException {
		if ( !getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot remove a job that has invoices!");
		}
		GnucashWritableFileImpl writableFile = (GnucashWritableFileImpl) getGnucashFile();
		writableFile.getRootElement().getGncBook().getBookElements().remove(getJwsdpPeer());
		writableFile.removeGenerJob(this);
	}

	// ---------------------------------------------------------------

	/**
	 * @return 
	 */
	public GCshID getCustomerID() {
		return getOwnerID();
	}

	/**
	 * @return 
	 */
	public GnucashCustomer getCustomer() {
		return getGnucashFile().getCustomerByID(getCustomerID());
	}

	// ---------------------------------------------------------------

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
	 * @throws WrongJobTypeException
	 * @see GnucashWritableCustomerJob#setCustomer(GnucashCustomer)
	 */
	public void setCustomer(final GnucashCustomer cust) throws WrongJobTypeException {
		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot change customer of a job that has invoices!");
		}

		if ( cust == null ) {
			throw new IllegalArgumentException("null 'customer' given!");
		}

		GnucashCustomer oldCust = getCustomer();
		if ( oldCust == cust ) {
			return; // nothing has changed
		}
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(cust.getID().toString());
		getWritableGnucashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("customer", oldCust, cust);
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
	public int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException {
		try {
			return getWritableGnucashFile().getUnpaidWritableInvoicesForJob(this).size();
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
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }
//

	@Override
	public List<GnucashJobInvoice> getPaidInvoices()
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnucashJobInvoice> result = new ArrayList<GnucashJobInvoice>();

		try {
			for ( GnucashWritableJobInvoice wrtblInvc : getPaidWritableInvoices() ) {
				GnucashJobInvoiceImpl rdblInvc = GnucashWritableJobInvoiceImpl
						.toReadable((GnucashWritableJobInvoiceImpl) wrtblInvc);
				result.add(rdblInvc);
			}
		} catch (TaxTableNotFoundException exc) {
			throw new IllegalStateException("Encountered tax table exception");
		}

		return result;
	}

	@Override
	public List<GnucashJobInvoice> getUnpaidInvoices()
			throws WrongInvoiceTypeException, UnknownAccountTypeException {
		List<GnucashJobInvoice> result = new ArrayList<GnucashJobInvoice>();

		try {
			for ( GnucashWritableJobInvoice wrtblInvc : getUnpaidWritableInvoices() ) {
				GnucashJobInvoiceImpl rdblInvc = GnucashWritableJobInvoiceImpl
						.toReadable((GnucashWritableJobInvoiceImpl) wrtblInvc);
				result.add(rdblInvc);
			}
		} catch (TaxTableNotFoundException exc) {
			throw new IllegalStateException("Encountered tax table exception");
		}

		return result;
	}

	// -----------------------------------------------------------------
	// The methods in this part are the "writable"-variants of
	// the according ones in the super class GnucashCustomerImpl.

	// ::TODO
//    @Override
//    public List<GnucashGenerInvoice> getWritableInvoices() throws WrongInvoiceTypeException {
//	List<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();
//
//	for ( GnucashCustomerInvoice invc : getWritableGnucashFile().getInvoicesForJob(this) ) {
//	    retval.add(invc);
//	}
//	
//	return retval;
//    }

	public List<GnucashWritableJobInvoice> getPaidWritableInvoices()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return getWritableGnucashFile().getPaidWritableInvoicesForJob(this);
	}

	public List<GnucashWritableJobInvoice> getUnpaidWritableInvoices()
			throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException,
			InvalidCmdtyCurrTypeException, TaxTableNotFoundException {
		return getWritableGnucashFile().getUnpaidWritableInvoicesForJob(this);
	}

}
