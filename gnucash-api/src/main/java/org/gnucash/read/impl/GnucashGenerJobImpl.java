package org.gnucash.read.impl;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.Locale;

import org.gnucash.Const;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.GncV2.GncBook.GncGncJob.JobOwner;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.SpecInvoiceCommon;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashGenerJobImpl implements GnucashGenerJob {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GnucashGenerJobImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncV2.GncBook.GncGncJob jwsdpPeer;

    /**
     * The file we belong to.
     */
    protected final GnucashFile file;

    /**
     * The currencyFormat to use for default-formating.<br/>
     * Please access only using {@link #getCurrencyFormat()}.
     *
     * @see #getCurrencyFormat()
     */
    private NumberFormat currencyFormat = null;

    // -----------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @see #jwsdpPeer
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnucashGenerJobImpl(final GncV2.GncBook.GncGncJob peer, final GnucashFile gncFile) {
	super();

	jwsdpPeer = peer;
	file = gncFile;
    }

    /**
     *
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncJob getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    public GnucashFile getFile() {
	return file;
    }

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    public GCshID getId() {
	assert jwsdpPeer.getJobGuid().getType().equals(Const.XML_DATA_TYPE_GUID);

	String guid = jwsdpPeer.getJobGuid().getValue();
	if (guid == null) {
	    throw new IllegalStateException("job has a null guid-value! guid-type=" + jwsdpPeer.getJobGuid().getType());
	}

	return new GCshID(guid);
    }

    /**
     * @return true if the job is still active
     */
    public boolean isActive() {
	return getJwsdpPeer().getJobActive() == 1;
    }

    /**
     * {@inheritDoc}
     */
    public String getNumber() {
	return jwsdpPeer.getJobId();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return jwsdpPeer.getJobName();
    }

    /**
     * @return the currency-format to use if no locale is given.
     */
    protected NumberFormat getCurrencyFormat() {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	}

	return currencyFormat;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshOwner.Type getOwnerType() {
	return GCshOwner.Type.valueOff( jwsdpPeer.getJobOwner().getOwnerType() );
    }

    @Deprecated
    public String getOwnerTypeStr() {
	return jwsdpPeer.getJobOwner().getOwnerType();
    }

    /**
     * {@inheritDoc}
     */
    public String getOwnerId() {
	assert jwsdpPeer.getJobOwner().getOwnerId().getType().equals(Const.XML_DATA_TYPE_GUID);
	return jwsdpPeer.getJobOwner().getOwnerId().getValue();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getFile().getUnpaidInvoicesForJob(this).size();
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public FixedPointNumber getIncomeGenerated() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashJobInvoice invcSpec : getPaidInvoices()) {
//		if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_JOB) ) {
//		    GnucashJobInvoice invcSpec = new GnucashJobInvoiceImpl(invcGen);
		GnucashGenerJob job = invcSpec.getGenerJob();
		if (job.getId().equals(this.getId())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountWithoutTaxes());
		}
//		} // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getIncomeGenerated: Serious error");
	}

	return retval;
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public String getIncomeGeneratedFormatted() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getCurrencyFormat().format(getIncomeGenerated());
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public String getIncomeGeneratedFormatted(Locale lcl) throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return NumberFormat.getCurrencyInstance(lcl).format(getIncomeGenerated());
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public FixedPointNumber getOutstandingValue() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashJobInvoice invcSpec : getUnpaidInvoices()) {
//            if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_JOB) ) {
//              GnucashJobInvoice invcSpec = new GnucashJobInvoiceImpl(invcGen); 
		GnucashGenerJob job = invcSpec.getGenerJob();
		if (job.getId().equals(this.getId())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getOutstandingValue: Serious error");
	}

	return retval;
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public String getOutstandingValueFormatted() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getCurrencyFormat().format(getOutstandingValue());
    }

    /**
     * {@inheritDoc}
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public String getOutstandingValueFormatted(Locale lcl) throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue());
    }

    // -----------------------------------------------------------------

    @Override
    public Collection<GnucashJobInvoice> getInvoices() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return file.getInvoicesForJob(this);
    }

    @Override
    public Collection<GnucashJobInvoice> getPaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return file.getPaidInvoicesForJob(this);
    }

    @Override
    public Collection<GnucashJobInvoice> getUnpaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return file.getUnpaidInvoicesForJob(this);
    }

    // -----------------------------------------------------------------

    public static int getHighestNumber(GnucashCustomer cust) {
	return cust.getGnucashFile().getHighestJobNumber();
    }

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    @Override
    public JobOwner getOwnerPeerObj() {
	return jwsdpPeer.getJobOwner();
    }

    // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[GnucashGenerJobImpl:");
	buffer.append(" id: ");
	buffer.append(getId());
	
	buffer.append(" number: ");
	buffer.append(getNumber());
	
	buffer.append(" name: '");
	buffer.append(getName() + "'");
	
	buffer.append(" owner-type: ");
	buffer.append(getOwnerType());
	
	buffer.append(" cust/vend-id: ");
	buffer.append(getOwnerId());
	
	buffer.append(" is-active: ");
	buffer.append(isActive());
	
	buffer.append("]");
	return buffer.toString();
    }

}
