package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnuCashInvoice that uses JWSDP.
 */
public class GnuCashGenerInvoiceImpl extends GnuCashObjectImpl
									 implements GnuCashGenerInvoice 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerInvoiceImpl.class);

	protected static final DateTimeFormatter DATE_OPENED_FORMAT       = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
	protected static final DateTimeFormatter DATE_OPENED_FORMAT_BOOK  = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
	protected static final DateTimeFormatter DATE_OPENED_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// ::TODO Outdated
	// Cf.
	// https://stackoverflow.com/questions/10649782/java-cannot-format-given-object-as-a-date
	protected static final DateFormat DATE_OPENED_FORMAT_1 = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);
	protected static final DateFormat DATE_POSTED_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

	// -----------------------------------------------------------------

	/**
	 * the JWSDP-object we are facading.
	 */
	protected final GncGncInvoice jwsdpPeer;

	// ------------------------------

	/**
	 * @see GnuCashGenerInvoice#getDateOpened()
	 */
	protected ZonedDateTime dateOpened;

	/**
	 * @see GnuCashGenerInvoice#getDatePosted()
	 */
	protected ZonedDateTime datePosted;

	/**
	 * The entries of this invoice.
	 */
	protected List<GnuCashGenerInvoiceEntry> entries = new ArrayList<GnuCashGenerInvoiceEntry>();

	/**
	 * The transactions that are paying for this invoice.
	 */
	private final List<GnuCashTransaction> payingTransactions = new ArrayList<GnuCashTransaction>();

	// ------------------------------

	/**
	 * @see #getDateOpenedFormatted()
	 * @see #getDatePostedFormatted()
	 */
	private DateFormat dateFormat = null;

	/**
	 * The currencyFormat to use for default-formating.<br/>
	 * Please access only using {@link #getCurrencyFormat()}.
	 * 
	 * @see #getCurrencyFormat()
	 */
	private NumberFormat currencyFormat = null;

	// -----------------------------------------------------------------

	/**
	 * @param peer the JWSDP-object we are facading.
	 * @param gcshFile the file to register under
	 */
	@SuppressWarnings("exports")
	public GnuCashGenerInvoiceImpl(final GncGncInvoice peer, final GnuCashFile gcshFile) {
		super(gcshFile);

//		if ( peer.getInvoiceSlots() == null ) {
//			peer.setInvoiceSlots(new ObjectFactory().createSlotsType());
//		}

		this.jwsdpPeer = peer;
	}

	// Copy-constructor
	public GnuCashGenerInvoiceImpl(final GnuCashGenerInvoice invc) {
		super(invc.getGnuCashFile());

//		if ( invc.getJwsdpPeer().getInvoiceSlots() == null ) {
//			invc.getJwsdpPeer().setInvoiceSlots(new ObjectFactory().createSlotsType());
//		}

		this.jwsdpPeer = invc.getJwsdpPeer();

		for ( GnuCashGenerInvoiceEntry entr : invc.getGenerEntries() ) {
			addGenerEntry(entr);
		}
	}

//	// -----------------------------------------------------------------
//
//	public GnuCashObjectImpl getGnuCashObject() {
//		return helper;
//	}
//
	// -----------------------------------------------------------

	@Override
	public String getURL() {
		return getUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI);
	}

	// -----------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(final String name) {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeKeysCore(jwsdpPeer.getInvoiceSlots());
	}

	// -----------------------------------------------------------------

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isCustInvcFullyPaid() throws WrongInvoiceTypeException {
		return !isNotCustInvcFullyPaid();
	}

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isNotCustInvcFullyPaid() throws WrongInvoiceTypeException {
		return getCustInvcAmountWithTaxes().isGreaterThan(getCustInvcAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
	}

	// ------------------------------

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isVendBllFullyPaid() throws WrongInvoiceTypeException {
		return !isNotVendBllFullyPaid();
	}

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isNotVendBllFullyPaid() throws WrongInvoiceTypeException {
		return getVendBllAmountWithTaxes().isGreaterThan(getVendBllAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
	}

	// ------------------------------

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isEmplVchFullyPaid() throws WrongInvoiceTypeException {
		return !isNotEmplVchFullyPaid();
	}

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isNotEmplVchFullyPaid() throws WrongInvoiceTypeException {
		return getEmplVchAmountWithTaxes().isGreaterThan(getEmplVchAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
	}

	// ------------------------------

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isJobInvcFullyPaid() throws WrongInvoiceTypeException {
		return !isNotInvcJobFullyPaid();
	}

	/**
	 * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
	 * @throws WrongInvoiceTypeException
	 * 
	 * @see GnuCashGenerInvoice#isNotCustInvcFullyPaid()
	 */
	public boolean isNotInvcJobFullyPaid() throws WrongInvoiceTypeException {
		return getJobInvcAmountWithTaxes().isGreaterThan(getJobInvcAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public void addPayingTransaction(final GnuCashTransactionSplit trans) {
		payingTransactions.add(trans.getTransaction());
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTransaction(final GnuCashTransaction trans) {
		//

	}

	/**
	 * {@inheritDoc}
	 */
	public List<GnuCashTransaction> getPayingTransactions() {
		return payingTransactions;
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getPostAccountID() {
		try {
			return new GCshID(jwsdpPeer.getInvoicePostacc().getValue());
		} catch (NullPointerException exc) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getPostTransactionID() {
		try {
			return new GCshID(jwsdpPeer.getInvoicePosttxn().getValue());
		} catch (NullPointerException exc) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public GnuCashAccount getPostAccount() {
		if ( getPostAccountID() == null ) {
			return null;
		}
		return getGnuCashFile().getAccountByID(getPostAccountID());
	}

	/**
	 * @return the transaction that transferes the money from the customer to the
	 *         account for money you are to get and the one you owe the taxes.
	 */
	public GnuCashTransaction getPostTransaction() {
		if ( getPostTransactionID() == null ) {
			return null;
		}
		return getGnuCashFile().getTransactionByID(getPostTransactionID());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getCustInvcAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		return ((FixedPointNumber) getCustInvcAmountWithTaxes().clone()).subtract(getCustInvcAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getCustInvcAmountPaidWithTaxes()
			throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber takenFromReceivableAccount = new FixedPointNumber();
		for ( GnuCashTransaction trx : getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.RECEIVABLE ) {
					if ( !split.getValue().isPositive() ) {
						takenFromReceivableAccount.subtract(split.getValue());
					}
				}
			} // split
		} // trx

		return takenFromReceivableAccount;
	}

	@Override
	public FixedPointNumber getCustInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getCustInvcSumExclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		// TODO: we should sum them without taxes grouped by tax% and
		// multiply the sums with the tax% to be calculating
		// correctly

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getCustInvcSumInclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getCustInvcAmountWithoutTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getCustInvcSumExclTaxes());
			}
		}

		return retval;
	}

	// ------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public String getCustInvcAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getCustInvcAmountUnpaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public String getCustInvcAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getCustInvcAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getCustInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getCustInvcAmountPaidWithoutTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getCustInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getCustInvcAmountWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getCustInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getCustInvcAmountWithoutTaxes());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getVendBllAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException {

		// System.err.println("debug: GnuCashInvoiceImpl.getAmountUnpaid(): "
		// + "getBillAmountUnpaid()="+getBillAmountWithoutTaxes()+"
		// getBillAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

		return ((FixedPointNumber) getVendBllAmountWithTaxes().clone()).subtract(getVendBllAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getVendBllAmountPaidWithTaxes()
			throws WrongInvoiceTypeException {

		FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
		for ( GnuCashTransaction trx : getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.PAYABLE ) {
					if ( split.getValue().isPositive() ) {
						takenFromPayableAccount.add(split.getValue());
					}
				}
			} // split
		} // trx

		// System.err.println("getBillAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

		return takenFromPayableAccount;
	}

	@Override
	public FixedPointNumber getVendBllAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getVendBllSumExclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithTaxes() throws WrongInvoiceTypeException {

		FixedPointNumber retval = new FixedPointNumber();

		// TODO: we should sum them without taxes grouped by tax% and
		// multiply the sums with the tax% to be calculating
		// correctly

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getVendBllSumInclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getVendBllAmountWithoutTaxes() throws WrongInvoiceTypeException {

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getVendBllSumExclTaxes());
			}
		}

		return retval;
	}

	// ------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getVendBllAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getVendBllAmountUnpaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getVendBllAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getVendBllAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getVendBllAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getVendBllAmountPaidWithoutTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getVendBllAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getVendBllAmountWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getVendBllAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getVendBllAmountWithoutTaxes());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getEmplVchAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException {

		// System.err.println("debug: GnuCashInvoiceImpl.getAmountUnpaid(): "
		// + "getVoucherAmountUnpaid()="+getVoucherAmountWithoutTaxes()+"
		// getVoucherAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

		return ((FixedPointNumber) getEmplVchAmountWithTaxes().clone()).subtract(getEmplVchAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getEmplVchAmountPaidWithTaxes()
			throws WrongInvoiceTypeException {

		FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
		for ( GnuCashTransaction trx : getPayingTransactions() ) {
			for ( GnuCashTransactionSplit split : trx.getSplits() ) {
				if ( split.getAccount().getType() == GnuCashAccount.Type.PAYABLE ) {
					if ( split.getValue().isPositive() ) {
						takenFromPayableAccount.add(split.getValue());
					}
				}
			} // split
		} // trx

		// System.err.println("getVoucherAmountPaidWithTaxes="+takenFromPayableAccount.doubleValue());

		return takenFromPayableAccount;
	}

	@Override
	public FixedPointNumber getEmplVchAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getEmplVchSumExclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getEmplVchAmountWithTaxes() throws WrongInvoiceTypeException {

		FixedPointNumber retval = new FixedPointNumber();

		// TODO: we should sum them without taxes grouped by tax% and
		// multiply the sums with the tax% to be calculating
		// correctly

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getEmplVchSumInclTaxes());
			}
		}

		return retval;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getEmplVchAmountWithoutTaxes() throws WrongInvoiceTypeException {

		FixedPointNumber retval = new FixedPointNumber();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				retval.add(entry.getEmplVchSumExclTaxes());
			}
		}

		return retval;
	}

	// ------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getEmplVchAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getEmplVchAmountUnpaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getEmplVchAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getEmplVchAmountPaidWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getEmplVchAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getEmplVchAmountPaidWithoutTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getEmplVchAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getEmplVchAmountWithTaxes());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getEmplVchAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getEmplVchAmountWithoutTaxes());
	}

	// ---------------------------------------------------------------

	/**
	 * @return what the customer must still pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getJobInvcAmountUnpaidWithTaxes()
			throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_CUSTOMER) )
			return getCustInvcAmountUnpaidWithTaxes();
		else if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_VENDOR) )
			return getVendBllAmountUnpaidWithTaxes();

		return null; // Compiler happy
	}

	/**
	 * @return what the customer has already pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithTaxes()
			throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_CUSTOMER) )
			return getCustInvcAmountPaidWithTaxes();
		else if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_VENDOR) )
			return getVendBllAmountPaidWithTaxes();

		return null; // Compiler happy
	}

	/**
	 * @return what the customer has already pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getJobInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_CUSTOMER) )
			return getCustInvcAmountPaidWithoutTaxes();
		else if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_VENDOR) )
			return getVendBllAmountPaidWithoutTaxes();

		return null; // Compiler happy
	}

	/**
	 * @return what the customer needs to pay in total (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithTaxes() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_CUSTOMER) )
			return getCustInvcAmountWithTaxes();
		else if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_VENDOR) )
			return getVendBllAmountWithTaxes();

		return null; // Compiler happy
	}

	/**
	 * @return what the customer needs to pay in total (excl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public FixedPointNumber getJobInvcAmountWithoutTaxes() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_CUSTOMER) )
			return getCustInvcAmountWithoutTaxes();
		else if ( jobInvc.getJobType().equals(GnuCashGenerJob.TYPE_VENDOR) )
			return getVendBllAmountWithoutTaxes();

		return null; // Compiler happy
	}

// ----------------------------

	/**
	 * Formating uses the default-locale's currency-format.
	 * 
	 * @return what the customer must still pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getJobInvcAmountUnpaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getJobInvcAmountUnpaidWithTaxes());
	}

	/**
	 * @return what the customer has already pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public String getJobInvcAmountPaidWithTaxesFormatted()
			throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getJobInvcAmountPaidWithTaxes());
	}

	/**
	 * @return what the customer has already pay (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public String getJobInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getJobInvcAmountPaidWithoutTaxes());
	}

	/**
	 * Formating uses the default-locale's currency-format.
	 * 
	 * @return what the customer needs to pay in total (incl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public String getJobInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getJobInvcAmountWithTaxes());
	}

	/**
	 * @return what the customer needs to pay in total (excl. taxes)
	 * @throws WrongInvoiceTypeException
	 * 
	 */
	@Override
	public String getJobInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException {
		return this.getCurrencyFormat().format(this.getJobInvcAmountWithoutTaxes());
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public GCshTaxedSumImpl[] getCustInvcTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_CUSTOMER && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

		invoiceentries: for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				FixedPointNumber taxPerc = entry.getCustInvcApplicableTaxPercent();

				for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
					GCshTaxedSumImpl taxedSum = taxedSum2;
					if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
						taxedSum.setTaxsum(taxedSum.getTaxsum()
								.add(entry.getCustInvcSumInclTaxes().subtract(entry.getCustInvcSumExclTaxes())));
						continue invoiceentries;
					}
				}

				GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
						entry.getCustInvcSumInclTaxes().subtract(entry.getCustInvcSumExclTaxes()));
				taxedSums.add(taxedSum);
			} // type
		} // for

		return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);

	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public GCshTaxedSumImpl[] getVendBllTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_VENDOR && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

		invoiceentries: for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				FixedPointNumber taxPerc = entry.getVendBllApplicableTaxPercent();

				for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
					GCshTaxedSumImpl taxedSum = taxedSum2;
					if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
						taxedSum.setTaxsum(taxedSum.getTaxsum()
								.add(entry.getVendBllSumInclTaxes().subtract(entry.getVendBllSumExclTaxes())));
						continue invoiceentries;
					}
				}

				GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
						entry.getVendBllSumInclTaxes().subtract(entry.getVendBllSumExclTaxes()));
				taxedSums.add(taxedSum);
			} // type
		} // for

		return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws WrongInvoiceTypeException
	 */
	@Override
	public GCshTaxedSumImpl[] getEmplVchTaxes() throws WrongInvoiceTypeException {

		if ( getType() != TYPE_EMPLOYEE && getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

		invoiceentries: for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == getType() ) {
				FixedPointNumber taxPerc = entry.getEmplVchApplicableTaxPercent();

				for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
					GCshTaxedSumImpl taxedSum = taxedSum2;
					if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
						taxedSum.setTaxsum(taxedSum.getTaxsum()
								.add(entry.getEmplVchSumInclTaxes().subtract(entry.getEmplVchSumExclTaxes())));
						continue invoiceentries;
					}
				}

				GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
						entry.getEmplVchSumInclTaxes().subtract(entry.getVendBllSumExclTaxes()));
				taxedSums.add(taxedSum);
			} // type
		} // for

		return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);
	}

	/**
	 *
	 * @return For a vendor bill: How much sales-taxes are to pay.
	 * @throws WrongInvoiceTypeException
	 * @throws ClassNotFoundException
	 * @see GCshTaxedSumImpl
	 */
	GCshTaxedSumImpl[] getJobTaxes() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
		if ( jobInvc.getJobType() == GCshOwner.Type.CUSTOMER )
			return getCustInvcTaxes();
		else if ( jobInvc.getJobType() == GCshOwner.Type.VENDOR )
			return getVendBllTaxes();
		else if ( jobInvc.getJobType() == GCshOwner.Type.EMPLOYEE )
			return getEmplVchTaxes();

		return null; // Compiler happy
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	public GCshID getID() {
		return new GCshID(getJwsdpPeer().getInvoiceGuid().getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshOwner.Type getType() {
		return GCshOwner.Type.valueOff(getJwsdpPeer().getInvoiceOwner().getOwnerType());
	}

	@Deprecated
	public String getTypeStr() {
		return getJwsdpPeer().getInvoiceOwner().getOwnerType();
	}

	/**
	 * {@inheritDoc}
	 */
	public GCshID getLotID() {
		if ( getJwsdpPeer().getInvoicePostlot() == null ) {
			return null; // unposted invoices have no postlot
		}

		return new GCshID(getJwsdpPeer().getInvoicePostlot().getValue());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return getJwsdpPeer().getInvoiceNotes();
	}

	// ----------------------------

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("exports")
	public GncGncInvoice getJwsdpPeer() {
		return jwsdpPeer;
	}

	// ----------------------------

	/**
	 * {@inheritDoc}
	 */
	public GnuCashGenerInvoiceEntry getGenerEntryByID(final GCshID id) {
		for ( GnuCashGenerInvoiceEntry element : getGenerEntries() ) {
			if ( element.getID().equals(id) ) {
				return element;
			}

		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<GnuCashGenerInvoiceEntry> getGenerEntries() {
		return entries;
	}

	/**
	 * {@inheritDoc}
	 */
	public void addGenerEntry(final GnuCashGenerInvoiceEntry entry) {
		if ( !entries.contains(entry) ) {
			entries.add(new GnuCashGenerInvoiceEntryImpl(entry));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ZonedDateTime getDateOpened() {
		if ( dateOpened == null ) {
			String dateStr = getJwsdpPeer().getInvoiceOpened().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				dateOpened = ZonedDateTime.parse(dateStr, DATE_OPENED_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException("unparsable date '" + dateStr + "' in invoice!");
				ex.initCause(e);
				throw ex;
			}

		}

		return dateOpened;
	}

	/**
	 * @see #getDateOpenedFormatted()
	 * @see #getDatePostedFormatted()
	 * @return the Dateformat to use.
	 */
	protected DateFormat getDateFormat() {
		if ( dateFormat == null ) {
			dateFormat = DateFormat.getDateInstance();
		}

		return dateFormat;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDateOpenedFormatted() {
		return getDateFormat().format(getDateOpened());
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDatePostedFormatted() {
		return getDateFormat().format(getDatePosted());
	}

	/**
	 * {@inheritDoc}
	 */
	public ZonedDateTime getDatePosted() {
		if ( datePosted == null ) {
			String dateStr = getJwsdpPeer().getInvoiceOpened().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				datePosted = ZonedDateTime.parse(dateStr, DATE_OPENED_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException(
						"unparsable date '" + dateStr + "' in invoice entry!");
				ex.initCause(e);
				throw ex;
			}

		}

		return datePosted;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getNumber() {
		return getJwsdpPeer().getInvoiceId();
	}

	// -----------------------------------------------------------

	public GCshID getOwnerID() {
		return getOwnerId_direct();
	}

	public GCshID getOwnerID(ReadVariant readVar) throws WrongInvoiceTypeException {
		if ( readVar == ReadVariant.DIRECT )
			return getOwnerId_direct();
		else if ( readVar == ReadVariant.VIA_JOB )
			return getOwnerId_viaJob();

		return null; // Compiler happy
	}

	protected GCshID getOwnerId_direct() {
		assert getJwsdpPeer().getInvoiceOwner().getOwnerId().getType().equals(Const.XML_DATA_TYPE_GUID);
		return new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
	}

	protected GCshID getOwnerId_viaJob() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashGenerJob job = getGnuCashFile().getGenerJobByID(getOwnerID());
		return job.getOwnerID();
	}

	// ----------------------------

	@Override
	public GCshOwner.Type getOwnerType(ReadVariant readVar) throws WrongInvoiceTypeException {
		if ( readVar == ReadVariant.DIRECT )
			return getOwnerType_direct();
		else if ( readVar == ReadVariant.VIA_JOB )
			return getOwnerType_viaJob();

		return null; // Compiler happy
	}

	public GCshOwner.Type getOwnerType_direct() {
		return GCshOwner.Type.valueOff(getJwsdpPeer().getInvoiceOwner().getOwnerType());
	}

	@Deprecated
	public String getOwnerType_directStr() {
		return getJwsdpPeer().getInvoiceOwner().getOwnerType();
	}

	protected GCshOwner.Type getOwnerType_viaJob() throws WrongInvoiceTypeException {
		if ( getType() != TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashGenerJob job = getGnuCashFile().getGenerJobByID(getOwnerID());
		return job.getOwnerType();
	}

	// -----------------------------------------------------------

	/**
	 * sorts primarily on the date the transaction happened and secondarily on the
	 * date it was entered.
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * @param otherInvc invoice to compare with
	 * @return -1 0 or 1
	 */
	public int compareTo(final GnuCashGenerInvoice otherInvc) {
		try {
			int compare = otherInvc.getDatePosted().compareTo(getDatePosted());
			if ( compare != 0 ) {
				return compare;
			}

			return otherInvc.getDateOpened().compareTo(getDateOpened());
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashGenerInvoiceImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", owner-id=");
		buffer.append(getOwnerID());

		buffer.append(", owner-type (dir.)=");
		try {
			buffer.append(getOwnerType(ReadVariant.DIRECT));
		} catch (WrongInvoiceTypeException e) {
			// TODO Auto-generated catch block
			buffer.append("ERROR");
		}

		buffer.append(", number='");
		buffer.append(getNumber() + "'");

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", #entries=");
		buffer.append(entries.size());

		buffer.append(", date-opened=");
		try {
			buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDateOpened().toLocalDate().toString());
		}

		buffer.append("]");
		return buffer.toString();
	}

	// ---------------------------------------------------------------

	/**
	 *
	 * @return the currency-format to use if no locale is given.
	 */
	protected NumberFormat getCurrencyFormat() {
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		return currencyFormat;
	}

	@SuppressWarnings("exports")
	@Override
	public InvoiceOwner getOwnerPeerObj() {
		return jwsdpPeer.getInvoiceOwner();
	}

}
