package org.gnucash.api.read.impl.spec;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see GnuCashVendorJobImpl
 */
public class GnuCashCustomerJobImpl extends GnuCashGenerJobImpl
                                    implements GnuCashCustomerJob
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCustomerJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param gcshFile the file to register under
	 */
	@SuppressWarnings("exports")
	public GnuCashCustomerJobImpl(final GncGncJob peer, final GnuCashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnuCashCustomerJobImpl(final GnuCashGenerJob job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GnuCashGenerJob.TYPE_CUSTOMER )
			throw new WrongJobTypeException();

		// ::TODO
//	for ( GnuCashGenerInvoice invc : job.getInvoices() )
//	{
//	    addInvoice(new GnuCashJobInvoiceImpl(invc));
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
	public GnuCashCustomer getCustomer() {
		return getGnuCashFile().getCustomerByID(getCustomerID());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashCustomerJobImpl [");

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
