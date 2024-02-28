package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorJobImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerJobImpl;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vendor job that can be modified.
 * 
 * @see GnucashVendorJob
 * 
 * @see GnucashWritableCustomerJobImpl
 */
public class GnucashWritableVendorJobImpl extends GnucashWritableGenerJobImpl 
                                          implements GnucashWritableVendorJob 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableVendorJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer the XML(jaxb)-object we are fronting.
	 * @param gcshFile      the file we belong to
	 */
	@SuppressWarnings("exports")
	public GnucashWritableVendorJobImpl(final GncGncJob jwsdpPeer, final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param owner the vendor the job is from
	 * @param file  the file to add the vendor job to
	 * @param number 
	 * @param name 
	 */
	public GnucashWritableVendorJobImpl(
			final GnucashWritableFileImpl file, 
			final GnucashVendor owner,
			final String number, 
			final String name) {
		super(createVendorJob_int(file, GCshID.getNew(), owner, number, name), file);
	}

	public GnucashWritableVendorJobImpl(GnucashWritableGenerJobImpl job) throws WrongJobTypeException {
		super(job.getJwsdpPeer(), job.getGnucashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GCshOwner.Type.VENDOR )
			throw new WrongJobTypeException();
	}

	public GnucashWritableVendorJobImpl(GnucashVendorJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnucashFile());
	}

	// ---------------------------------------------------------------

	/**
	 * @throws WrongInvoiceTypeException
	 * @see GnucashWritableVendorJob#remove()
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
	public GCshID getVendorID() {
		return getOwnerID();
	}

	/**
	 * @return
	 */
	public GnucashVendor getVendor() {
		return getGnucashFile().getVendorByID(getVendorID());
	}

	// ---------------------------------------------------------------

//    /**
//     * The gnucash-file is the top-level class to contain everything.
//     *
//     * @return the file we are associated with
//     */
//    @Override
//    public GnucashWritableFileImpl getWritableGnucashFile() {
//	return (GnucashWritableFileImpl) super.getGnucashFile();
//    }
//
//    /**
//     * The gnucash-file is the top-level class to contain everything.
//     *
//     * @return the file we are associated with
//     */
//    @Override
//    public GnucashWritableFileImpl getGnucashFile() {
//	return (GnucashWritableFileImpl) super.getGnucashFile();
//    }

    // ---------------------------------------------------------------

//    /**
//     * @see GnucashWritableVendorJob#setVendorType(java.lang.String)
//     */
//    public void setVendorType(final String vendorType) {
//	if (vendorType == null) {
//	    throw new IllegalArgumentException("null 'vendorType' given!");
//	}
//
//	Object old = getJwsdpPeer().getJobOwner().getOwnerType();
//	if (old == vendorType) {
//	    return; // nothing has changed
//	}
//	getJwsdpPeer().getJobOwner().setOwnerType(vendorType);
//	getWritableFile().setModified(true);
//	// <<insert code to react further to this change here
//	PropertyChangeSupport propertyChangeFirer = getPropertyChangeSupport();
//	if (propertyChangeFirer != null) {
//	    propertyChangeFirer.firePropertyChange("vendorType", old, vendorType);
//	}
//    }

	/**
	 * @throws WrongJobTypeException
	 * @see GnucashWritableVendorJob#setVendor(GnucashVendor)
	 */
	public void setVendor(final GnucashVendor vend) throws WrongJobTypeException {
		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot change vendor of a job that has invoices!");
		}

		if ( vend == null ) {
			throw new IllegalArgumentException("null 'vendor' given!");
		}

		GnucashVendor oldVend = getVendor();
		if ( oldVend == vend ) {
			return; // nothing has changed
		}
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(vend.getID().toString());
		getWritableGnucashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("vendor", oldVend, vend);
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
