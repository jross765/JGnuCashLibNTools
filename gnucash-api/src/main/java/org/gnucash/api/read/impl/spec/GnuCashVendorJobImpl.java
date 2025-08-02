package org.gnucash.api.read.impl.spec;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @see GnuCashCustomerJobImpl
 */
public class GnuCashVendorJobImpl extends GnuCashGenerJobImpl
                                  implements GnuCashVendorJob
{
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashVendorJobImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param gcshFile the file to register under
	 */
	@SuppressWarnings("exports")
	public GnuCashVendorJobImpl(final GncGncJob peer, final GnuCashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnuCashVendorJobImpl(final GnuCashGenerJob job) {
		super(job.getJwsdpPeer(), job.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( job.getOwnerType() != GnuCashGenerJob.TYPE_VENDOR )
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
	public GCshVendID getVendorID() {
		return new GCshVendID( getOwnerID() );
	}

	/**
	 * {@inheritDoc}
	 */
	public GnuCashVendor getVendor() {
		return getGnuCashFile().getVendorByID(getVendorID());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashVendorJobImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", number=");
		buffer.append(getNumber());

		buffer.append(", name='");
		buffer.append(getName() + "'");

		buffer.append(", vendor-id=");
		buffer.append(getVendorID());

		buffer.append(", is-active=");
		buffer.append(isActive());

		buffer.append("]");
		return buffer.toString();
	}

}
