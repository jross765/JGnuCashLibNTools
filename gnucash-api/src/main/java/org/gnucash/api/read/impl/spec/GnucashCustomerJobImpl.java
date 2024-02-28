package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.impl.GnucashGenerJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see GnucashVendorJobImpl
 */
public class GnucashCustomerJobImpl extends GnucashGenerJobImpl
                                    implements GnucashCustomerJob
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCustomerJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param gcshFile the file to register under
	 */
	@SuppressWarnings("exports")
	public GnucashCustomerJobImpl(final GncGncJob peer, final GnucashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnucashCustomerJobImpl(final GnucashGenerJob job) throws WrongJobTypeException {
		super(job.getJwsdpPeer(), job.getGnucashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GnucashGenerJob.TYPE_CUSTOMER )
			throw new WrongJobTypeException();

		// ::TODO
//	for ( GnucashGenerInvoice invc : job.getInvoices() )
//	{
//	    addInvoice(new GnucashJobInvoiceImpl(invc));
//	}
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GCshID getCustomerID() {
		return getOwnerID();
	}

	/**
	 * {@inheritDoc}
	 */
	public GnucashCustomer getCustomer() {
		return getGnucashFile().getCustomerByID(getCustomerID());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnucashCustomerJobImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", number=");
		buffer.append(getNumber());

		buffer.append(", name='");
		buffer.append(getName() + "'");

		buffer.append(", customer-id=");
		buffer.append(getCustomerID());

		buffer.append(", is-active=");
		buffer.append(isActive());

		buffer.append("]");
		return buffer.toString();
	}

}
