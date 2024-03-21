package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncJob.JobOwner;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashGenerJobImpl extends GnuCashObjectImpl 
                                 implements GnuCashGenerJob 
{

	protected static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerJobImpl.class);
	
	// ---------------------------------------------------------------

	/**
	 * the JWSDP-object we are facading.
	 */
	protected final GncGncJob jwsdpPeer;

	/**
	 * The currencyFormat to use for default-formating.<br/>
	 * Please access only using {@link #getCurrencyFormat()}.
	 *
	 * @see #getCurrencyFormat()
	 */
	private NumberFormat currencyFormat = null;

	// ---------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param gcshFile the file to register under
	 */
	@SuppressWarnings("exports")
	public GnuCashGenerJobImpl(final GncGncJob peer, final GnuCashFile gcshFile) {
		super(gcshFile);

		jwsdpPeer = peer;
	}

	// ---------------------------------------------------------------
	/**
	 *
	 * @return The JWSDP-Object we are wrapping.
	 */
	@SuppressWarnings("exports")
	public GncGncJob getJwsdpPeer() {
		return jwsdpPeer;
	}

	/**
	 * @return the unique-id to identify this object with across name- and
	 *         hirarchy-changes
	 */
	public GCshID getID() {
		assert jwsdpPeer.getJobGuid().getType().equals(Const.XML_DATA_TYPE_GUID);

		String guid = jwsdpPeer.getJobGuid().getValue();
		if ( guid == null ) {
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
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		return currencyFormat;
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GCshOwner.Type getOwnerType() {
		return GCshOwner.Type.valueOff(jwsdpPeer.getJobOwner().getOwnerType());
	}

	@Deprecated
	public String getOwnerTypeStr() {
		return jwsdpPeer.getJobOwner().getOwnerType();
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getOwnerID() {
		assert jwsdpPeer.getJobOwner().getOwnerId().getType().equals(Const.XML_DATA_TYPE_GUID);
		return new GCshID(jwsdpPeer.getJobOwner().getOwnerId().getValue());
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNofOpenInvoices() {
		return getGnuCashFile().getUnpaidInvoicesForJob(this).size();
	}

	/**
	 * {@inheritDoc}
	 */
	public FixedPointNumber getIncomeGenerated() {
		FixedPointNumber retval = new FixedPointNumber();

			for ( GnuCashJobInvoice invcSpec : getPaidInvoices() ) {
//		if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//		    GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen);
				GnuCashGenerJob job = invcSpec.getGenerJob();
				if ( job.getID().equals(this.getID()) ) {
					retval.add(invcSpec.getAmountWithoutTaxes());
				}
//		} // if invc type
			} // for

		return retval;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getIncomeGeneratedFormatted() {
		return getCurrencyFormat().format(getIncomeGenerated());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getIncomeGeneratedFormatted(Locale lcl) {
		return NumberFormat.getCurrencyInstance(lcl).format(getIncomeGenerated());
	}

	/**
	 * {@inheritDoc}
	 */
	public FixedPointNumber getOutstandingValue() {
		FixedPointNumber retval = new FixedPointNumber();

			for ( GnuCashJobInvoice invcSpec : getUnpaidInvoices() ) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_JOB) ) {
//              GnuCashJobInvoice invcSpec = new GnuCashJobInvoiceImpl(invcGen); 
				GnuCashGenerJob job = invcSpec.getGenerJob();
				if ( job.getID().equals(this.getID()) ) {
					retval.add(invcSpec.getAmountUnpaidWithTaxes());
				}
//            } // if invc type
			} // for

		return retval;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOutstandingValueFormatted() {
		return getCurrencyFormat().format(getOutstandingValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getOutstandingValueFormatted(Locale lcl) {
		return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue());
	}

	// -----------------------------------------------------------------

	@Override
	public List<GnuCashJobInvoice> getInvoices() {
		return getGnuCashFile().getInvoicesForJob(this);
	}

	@Override
	public List<GnuCashJobInvoice> getPaidInvoices() {
		return getGnuCashFile().getPaidInvoicesForJob(this);
	}

	@Override
	public List<GnuCashJobInvoice> getUnpaidInvoices() {
		return getGnuCashFile().getUnpaidInvoicesForJob(this);
	}

	// ---------------------------------------------------------------

	public static int getHighestNumber(GnuCashGenerJob job) {
		return ((GnuCashFileImpl) job.getGnuCashFile()).getHighestJobNumber();
	}

	public static String getNewNumber(GnuCashGenerJob job) {
		return ((GnuCashFileImpl) job.getGnuCashFile()).getNewJobNumber();
	}

	// -----------------------------------------------------------------

	@SuppressWarnings("exports")
	@Override
	public JobOwner getOwnerPeerObj() {
		return jwsdpPeer.getJobOwner();
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashGenerJobImpl [");
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
