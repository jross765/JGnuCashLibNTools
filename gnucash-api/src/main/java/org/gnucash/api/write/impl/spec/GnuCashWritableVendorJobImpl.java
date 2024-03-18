package org.gnucash.api.write.impl.spec;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorJobImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vendor job that can be modified.
 * 
 * @see GnuCashVendorJob
 * 
 * @see GnuCashWritableCustomerJobImpl
 */
public class GnuCashWritableVendorJobImpl extends GnuCashWritableGenerJobImpl 
                                          implements GnuCashWritableVendorJob 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableVendorJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param jwsdpPeer the XML(jaxb)-object we are fronting.
	 * @param gcshFile      the file we belong to
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableVendorJobImpl(final GncGncJob jwsdpPeer, final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

	/**
	 * @param owner the vendor the job is from
	 * @param file  the file to add the vendor job to
	 * @param number 
	 * @param name 
	 */
	public GnuCashWritableVendorJobImpl(
			final GnuCashWritableFileImpl file, 
			final GnuCashVendor owner,
			final String number, 
			final String name) {
		super(createVendorJob_int(file, GCshID.getNew(), owner, number, name), file);
	}

	public GnuCashWritableVendorJobImpl(GnuCashWritableGenerJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GCshOwner.Type.VENDOR )
			throw new WrongJobTypeException();
	}

	public GnuCashWritableVendorJobImpl(GnuCashVendorJobImpl job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashWritableVendorJob#remove()
	 */
	public void remove() {
		if ( !getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot remove a job that has invoices!");
		}
		GnuCashWritableFileImpl writableFile = (GnuCashWritableFileImpl) getGnuCashFile();
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
	public GnuCashVendor getVendor() {
		return getGnuCashFile().getVendorByID(getVendorID());
	}

	// ---------------------------------------------------------------

//    /**
//     * The GnuCash file is the top-level class to contain everything.
//     *
//     * @return the file we are associated with
//     */
//    @Override
//    public GnuCashWritableFileImpl getWritableGnuCashFile() {
//	return (GnuCashWritableFileImpl) super.getGnuCashFile();
//    }
//
//    /**
//     * The GnuCash file is the top-level class to contain everything.
//     *
//     * @return the file we are associated with
//     */
//    @Override
//    public GnuCashWritableFileImpl getGnuCashFile() {
//	return (GnuCashWritableFileImpl) super.getGnuCashFile();
//    }

    // ---------------------------------------------------------------

//    /**
//     * @see GnuCashWritableVendorJob#setVendorType(java.lang.String)
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
	 * @see GnuCashWritableVendorJob#setVendor(GnuCashVendor)
	 */
	public void setVendor(final GnuCashVendor vend) {
		if ( ! getInvoices().isEmpty() ) {
			throw new IllegalStateException("cannot change vendor of a job that has invoices!");
		}

		if ( vend == null ) {
			throw new IllegalArgumentException("null 'vendor' given!");
		}

		GnuCashVendor oldVend = getVendor();
		if ( oldVend == vend ) {
			return; // nothing has changed
		}
		getJwsdpPeer().getJobOwner().getOwnerId().setValue(vend.getID().toString());
		getWritableGnuCashFile().setModified(true);
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("vendor", oldVend, vend);
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
