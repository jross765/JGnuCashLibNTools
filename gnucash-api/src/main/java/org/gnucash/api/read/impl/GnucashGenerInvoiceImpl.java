package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashInvoice that uses JWSDP.
 */
public class GnucashGenerInvoiceImpl implements GnucashGenerInvoice {
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashGenerInvoiceImpl.class);

    protected static final DateTimeFormatter DATE_OPENED_FORMAT       = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_OPENED_FORMAT_BOOK  = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_OPENED_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ::TODO Outdated
    // Cf.
    // https://stackoverflow.com/questions/10649782/java-cannot-format-given-object-as-a-date
    protected static final DateFormat DATE_OPENED_FORMAT_1 = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);
    protected static final DateFormat DATE_POSTED_FORMAT   = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    // -----------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected GncGncInvoice jwsdpPeer;

    /**
     * The file we belong to.
     */
    protected final GnucashFile file;

    /**
     * Helper to implement the {@link GnucashObject}-interface.
     */
    protected GnucashObjectImpl helper;

    // ------------------------------

    /**
     * @see GnucashGenerInvoice#getDateOpened()
     */
    protected ZonedDateTime dateOpened;

    /**
     * @see GnucashGenerInvoice#getDatePosted()
     */
    protected ZonedDateTime datePosted;

    /**
     * The entries of this invoice.
     */
    protected Collection<GnucashGenerInvoiceEntry> entries = new HashSet<GnucashGenerInvoiceEntry>();

    /**
     * The transactions that are paying for this invoice.
     */
    private final Collection<GnucashTransaction> payingTransactions = new ArrayList<GnucashTransaction>();

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
     * @see #jwsdpPeer
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnucashGenerInvoiceImpl(final GncGncInvoice peer, final GnucashFile gncFile) {
	super();

	if (peer.getInvoiceSlots() == null) {
	    peer.setInvoiceSlots(new ObjectFactory().createSlotsType());
	}

	jwsdpPeer = peer;
	file = gncFile;

	helper = new GnucashObjectImpl(peer.getInvoiceSlots(), gncFile);
    }

    // Copy-constructor
    public GnucashGenerInvoiceImpl(final GnucashGenerInvoice invc) {
	super();

	if (invc.getJwsdpPeer().getInvoiceSlots() == null) {
	    invc.getJwsdpPeer().setInvoiceSlots(new ObjectFactory().createSlotsType());
	}

	this.jwsdpPeer = invc.getJwsdpPeer();
	this.file = invc.getFile();

	helper = new GnucashObjectImpl(invc.getJwsdpPeer().getInvoiceSlots(), invc.getFile());

	for (GnucashGenerInvoiceEntry entr : invc.getGenerEntries()) {
	    addGenerEntry(entr);
	}
    }

    // -----------------------------------------------------------------
    
    public GnucashObjectImpl getGnucashObject() {
    	return helper;
    }

    /**
     * Examples: The user-defined-attribute "hidden"="true"/"false" was introduced
     * in gnucash2.0 to hide accounts.
     *
     * @param name the name of the user-defined attribute
     * @return the value or null if not set
     */
    public String getUserDefinedAttribute(final String name) {
	return helper.getUserDefinedAttribute(name);
    }

    /**
     * @return all keys that can be used with
     *         ${@link #getUserDefinedAttribute(String)}}.
     */
    public Collection<String> getUserDefinedAttributeKeys() {
	return helper.getUserDefinedAttributeKeys();
    }

    // -----------------------------------------------------------------

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isCustInvcFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return ! isNotCustInvcFullyPaid();
    }

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isNotCustInvcFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getCustInvcAmountWithTaxes().isGreaterThan(getCustInvcAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
    }

    // ------------------------------

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isVendBllFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return ! isNotVendBllFullyPaid();
    }

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isNotVendBllFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getVendBllAmountWithTaxes().isGreaterThan(getVendBllAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
    }

    // ------------------------------

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isEmplVchFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return ! isNotEmplVchFullyPaid();
    }

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isNotEmplVchFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getEmplVchAmountWithTaxes().isGreaterThan(getEmplVchAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
    }

    // ------------------------------

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isJobInvcFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return ! isNotInvcJobFullyPaid();
    }

    /**
     * @return getAmountWithoutTaxes().isGreaterThan(getAmountPaidWithoutTaxes())
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GnucashGenerInvoice#isNotCustInvcFullyPaid()
     */
    public boolean isNotInvcJobFullyPaid()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getJobInvcAmountWithTaxes().isGreaterThan(getJobInvcAmountPaidWithTaxes(), Const.DIFF_TOLERANCE);
    }

    // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void addPayingTransaction(final GnucashTransactionSplit trans) {
	payingTransactions.add(trans.getTransaction());
    }

    /**
     * {@inheritDoc}
     */
    public void addTransaction(final GnucashTransaction trans) {
	//

    }

    /**
     * {@inheritDoc}
     */
    public Collection<GnucashTransaction> getPayingTransactions() {
	return payingTransactions;
    }

    /**
     * {@inheritDoc}
     */
    public GCshID getPostAccountID() {
	try {
	    return new GCshID( jwsdpPeer.getInvoicePostacc().getValue() );
	} catch (NullPointerException exc) {
	    return null;
	}
    }

    /**
     * {@inheritDoc}
     */
    public GCshID getPostTransactionID() {
	try {
	    return new GCshID( jwsdpPeer.getInvoicePosttxn().getValue() );
	} catch (NullPointerException exc) {
	    return null;
	}
    }

    /**
     * {@inheritDoc}
     */
    public GnucashAccount getPostAccount() {
	if (getPostAccountID() == null) {
	    return null;
	}
	return file.getAccountByID(getPostAccountID());
    }

    /**
     * @return the transaction that transferes the money from the customer to the
     *         account for money you are to get and the one you owe the taxes.
     */
    public GnucashTransaction getPostTransaction() {
	if (getPostTransactionID() == null) {
	    return null;
	}
	return file.getTransactionByID(getPostTransactionID());
    }

    // -----------------------------------------------------------------

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getCustInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return ((FixedPointNumber) getCustInvcAmountWithTaxes().clone()).subtract(getCustInvcAmountPaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getCustInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	FixedPointNumber takenFromReceivableAccount = new FixedPointNumber();
	for ( GnucashTransaction trx : getPayingTransactions() ) {
	    for ( GnucashTransactionSplit split : trx.getSplits() ) {
		if ( split.getAccount().getType() == GnucashAccount.Type.RECEIVABLE ) {
		    if (!split.getValue().isPositive()) {
			takenFromReceivableAccount.subtract(split.getValue());
		    }
		}
	    } // split
	} // trx

	return takenFromReceivableAccount;
    }

    @Override
    public FixedPointNumber getCustInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException {

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	FixedPointNumber retval = new FixedPointNumber();

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	FixedPointNumber retval = new FixedPointNumber();

	// TODO: we should sum them without taxes grouped by tax% and
	// multiply the sums with the tax% to be calculating
	// correctly

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	FixedPointNumber retval = new FixedPointNumber();

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getCustInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getCustInvcAmountUnpaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getCustInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getVendBllAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	// System.err.println("debug: GnucashInvoiceImpl.getAmountUnpaid(): "
	// + "getBillAmountUnpaid()="+getBillAmountWithoutTaxes()+"
	// getBillAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

	return ((FixedPointNumber) getVendBllAmountWithTaxes().clone()).subtract(getVendBllAmountPaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getVendBllAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
	for ( GnucashTransaction trx : getPayingTransactions() ) {
	    for ( GnucashTransactionSplit split : trx.getSplits() ) {
		if ( split.getAccount().getType() == GnucashAccount.Type.PAYABLE ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getVendBllAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getVendBllAmountUnpaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getVendBllAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getEmplVchAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	// System.err.println("debug: GnucashInvoiceImpl.getAmountUnpaid(): "
	// + "getVoucherAmountUnpaid()="+getVoucherAmountWithoutTaxes()+"
	// getVoucherAmountPaidWithTaxes()="+getAmountPaidWithTaxes() );

	return ((FixedPointNumber) getEmplVchAmountWithTaxes().clone()).subtract(getEmplVchAmountPaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getEmplVchAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {

	FixedPointNumber takenFromPayableAccount = new FixedPointNumber();
	for ( GnucashTransaction trx : getPayingTransactions() ) {
	    for ( GnucashTransactionSplit split : trx.getSplits() ) {
		if ( split.getAccount().getType() == GnucashAccount.Type.PAYABLE ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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

	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getEmplVchAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getEmplVchAmountUnpaidWithTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getEmplVchAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
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
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getJobInvcAmountUnpaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
	    return getCustInvcAmountUnpaidWithTaxes();
	else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
	    return getVendBllAmountUnpaidWithTaxes();

	return null; // Compiler happy
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getJobInvcAmountPaidWithTaxes()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
	    return getCustInvcAmountPaidWithTaxes();
	else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
	    return getVendBllAmountPaidWithTaxes();

	return null; // Compiler happy
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getJobInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
	    return getCustInvcAmountPaidWithoutTaxes();
	else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
	    return getVendBllAmountPaidWithoutTaxes();

	return null; // Compiler happy
    }

    /**
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getJobInvcAmountWithTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
	    return getCustInvcAmountWithTaxes();
	else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
	    return getVendBllAmountWithTaxes();

	return null; // Compiler happy
    }

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public FixedPointNumber getJobInvcAmountWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_CUSTOMER) )
	    return getCustInvcAmountWithoutTaxes();
	else if ( jobInvc.getJobType().equals(GnucashGenerJob.TYPE_VENDOR) )
	    return getVendBllAmountWithoutTaxes();

	return null; // Compiler happy
    }

// ----------------------------

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer must still pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getJobInvcAmountUnpaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobInvcAmountUnpaidWithTaxes());
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getJobInvcAmountPaidWithTaxesFormatted()
	    throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobInvcAmountPaidWithTaxes());
    }

    /**
     * @return what the customer has already pay (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getJobInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobInvcAmountPaidWithoutTaxes());
    }

    /**
     * Formating uses the default-locale's currency-format.
     * 
     * @return what the customer needs to pay in total (incl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getJobInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
	return this.getCurrencyFormat().format(this.getJobInvcAmountWithTaxes());
    }

    /**
     * @return what the customer needs to pay in total (excl. taxes)
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    @Override
    public String getJobInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException {
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

	if ( getType() != TYPE_CUSTOMER && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

	invoiceentries: 
	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
	    if ( entry.getType() == getType() ) {
		FixedPointNumber taxPerc = entry.getCustInvcApplicableTaxPercent();

		for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
		    GCshTaxedSumImpl taxedSum = taxedSum2;
		    if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
			taxedSum.setTaxsum(
				taxedSum.getTaxsum()
				        .add( entry.getCustInvcSumInclTaxes()
					           .subtract(entry.getCustInvcSumExclTaxes()) ) );
			continue invoiceentries;
		    }
		}

		GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
							entry.getCustInvcSumInclTaxes()
								.subtract(entry.getCustInvcSumExclTaxes()));
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

	if ( getType() != TYPE_VENDOR && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

	invoiceentries: 
	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
	    if ( entry.getType() == getType() ) {
		FixedPointNumber taxPerc = entry.getVendBllApplicableTaxPercent();

		for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
		    GCshTaxedSumImpl taxedSum = taxedSum2;
		    if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
			taxedSum.setTaxsum( taxedSum.getTaxsum()
				                    .add( entry.getVendBllSumInclTaxes()
				                	       .subtract(entry.getVendBllSumExclTaxes())) );
			continue invoiceentries;
		    }
		}

		GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
							entry.getVendBllSumInclTaxes()
								.subtract(entry.getVendBllSumExclTaxes()));
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

	if ( getType() != TYPE_EMPLOYEE && 
	     getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	List<GCshTaxedSumImpl> taxedSums = new ArrayList<GCshTaxedSumImpl>();

	invoiceentries: 
	for ( GnucashGenerInvoiceEntry entry : getGenerEntries() ) {
	    if ( entry.getType() == getType() ) {
		FixedPointNumber taxPerc = entry.getEmplVchApplicableTaxPercent();

		for ( GCshTaxedSumImpl taxedSum2 : taxedSums ) {
		    GCshTaxedSumImpl taxedSum = taxedSum2;
		    if ( taxedSum.getTaxpercent().equals(taxPerc) ) {
			taxedSum.setTaxsum( taxedSum.getTaxsum()
					            .add( entry.getEmplVchSumInclTaxes()
					        	       .subtract(entry.getEmplVchSumExclTaxes())) );
			continue invoiceentries;
		    }
		}

		GCshTaxedSumImpl taxedSum = new GCshTaxedSumImpl(taxPerc,
							entry.getEmplVchSumInclTaxes()
								.subtract(entry.getVendBllSumExclTaxes()));
		taxedSums.add(taxedSum);
	    } // type
	} // for

	return taxedSums.toArray(new GCshTaxedSumImpl[taxedSums.size()]);
    }

    /**
     *
     * @return For a vendor bill: How much sales-taxes are to pay.
     * @throws WrongInvoiceTypeException
     * @throws
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @see GCshTaxedSumImpl
     */
    GCshTaxedSumImpl[] getJobTaxes() throws WrongInvoiceTypeException, IllegalArgumentException {
	if ( getType() != TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
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
	if (getJwsdpPeer().getInvoicePostlot() == null) {
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

    /**
     * {@inheritDoc}
     */
    public GnucashFile getFile() {
	return file;
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    public GnucashGenerInvoiceEntry getGenerEntryByID(final GCshID id) {
	for ( GnucashGenerInvoiceEntry element : getGenerEntries() ) {
	    if ( element.getID().equals(id) ) {
		return element;
	    }

	}
	
	return null;
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GnucashGenerInvoiceEntry> getGenerEntries() {
	return entries;
    }

    /**
     * {@inheritDoc}
     */
    public void addGenerEntry(final GnucashGenerInvoiceEntry entry) {
	if ( ! entries.contains(entry) ) {
	    entries.add(new GnucashGenerInvoiceEntryImpl(entry));
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

	GnucashGenerJob job = file.getGenerJobByID(getOwnerID());
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

	GnucashGenerJob job = file.getGenerJobByID(getOwnerID());
	return job.getOwnerType();
    }

    // -----------------------------------------------------------

    @Override
    public String getURL() {
	return getUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI);
    }

    // -----------------------------------------------------------

    /**
     * sorts primarily on the date the transaction happened and secondarily on the
     * date it was entered.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     * @param o invoice to compare with
     * @return -1 0 or 1
     */
    public int compareTo(final GnucashGenerInvoice otherInvc) {
	try {
	    int compare = otherInvc.getDatePosted().compareTo(getDatePosted());
	    if (compare != 0) {
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
	buffer.append("GnucashGenerInvoiceImpl [");

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
