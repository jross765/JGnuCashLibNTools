package org.gnucash.read.impl.spec;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.impl.GnucashGenerJobImpl;
import org.gnucash.read.spec.GnucashEmployeeJob;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GnucashEmployeeJobImpl extends GnucashGenerJobImpl
                                    implements GnucashEmployeeJob
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashEmployeeJobImpl.class);

    /**
     * @param peer the JWSDP-object we are facading.
     * @see #jwsdpPeer
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnucashEmployeeJobImpl(
            final GncV2.GncBook.GncGncJob peer,
            final GnucashFile gncFile) {
        super(peer, gncFile);
    }

    public GnucashEmployeeJobImpl(final GnucashGenerJob job) throws WrongInvoiceTypeException {
	super(job.getJwsdpPeer(), job.getFile());

	// No, we cannot check that first, because the super() method
	// always has to be called first.
	if ( job.getOwnerType() != GnucashGenerJob.TYPE_EMPLOYEE )
	    throw new WrongInvoiceTypeException();

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
    public GCshID getEmployeeId() {
        return getOwnerId();
    }

    /**
     * {@inheritDoc}
     */
    public GnucashEmployee getEmployee() {
        return file.getEmployeeByID(getEmployeeId());
    }

    // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[GnucashEmployeeJobImpl:");
	buffer.append(" id: ");
	buffer.append(getId());
	
	buffer.append(" number: ");
	buffer.append(getNumber());
	
	buffer.append(" name: '");
	buffer.append(getName() + "'");
	
	buffer.append(" employee-id: ");
	buffer.append(getEmployeeId());
	
	buffer.append(" is-active: ");
	buffer.append(isActive());
	
	buffer.append("]");
	return buffer.toString();
    }

}
