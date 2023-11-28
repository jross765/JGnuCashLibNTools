package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.SplitNotFoundException;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashTransaction that uses JWSDP.
 */
public class GnucashTransactionImpl extends GnucashObjectImpl 
                                    implements GnucashTransaction 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashTransactionImpl.class);

    protected static final DateTimeFormatter DATE_ENTERED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_POSTED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    private GncTransaction jwsdpPeer;

    /**
     * The file we belong to.
     */
    private final GnucashFile file;

    /**
     * The Currency-Format to use if no locale is given.
     */
    protected NumberFormat currencyFormat;

    // ---------------------------------------------------------------

    /**
     * @see GnucashTransaction#getDateEntered()
     */
    protected ZonedDateTime dateEntered;

    /**
     * @see GnucashTransaction#getDatePosted()
     */
    protected ZonedDateTime datePosted;

    // ---------------------------------------------------------------

    /**
     * Create a new Transaction, facading a JWSDP-transaction.
     *
     * @param peer    the JWSDP-object we are facading.
     * @param gncFile the file to register under
     * @see #jwsdpPeer
     */
    @SuppressWarnings("exports")
    public GnucashTransactionImpl(
	    final GncTransaction peer, 
	    final GnucashFile gncFile,
	    final boolean addTrxToInvc) {
	super((peer.getTrnSlots() == null) ? new ObjectFactory().createSlotsType() : peer.getTrnSlots(), gncFile);

	if (peer.getTrnSlots() == null) {
	    peer.setTrnSlots(getSlots());
	}

	if (peer == null) {
	    throw new IllegalArgumentException("null jwsdpPeer given");
	}

	if (gncFile == null) {
	    throw new IllegalArgumentException("null file given");
	}

	jwsdpPeer = peer;
	file = gncFile;

	if ( addTrxToInvc ) {
	    for (GnucashGenerInvoice invc : getInvoices()) {
		invc.addTransaction(this);
	    }
	}
    }

    // Copy-constructor
    public GnucashTransactionImpl(final GnucashTransaction trx) {
	super((trx.getJwsdpPeer().getTrnSlots() == null) ? new ObjectFactory().createSlotsType()
		: trx.getJwsdpPeer().getTrnSlots(), trx.getGnucashFile());

	if (trx.getJwsdpPeer().getTrnSlots() == null) {
	    trx.getJwsdpPeer().setTrnSlots(getSlots());
	}

	if (trx.getJwsdpPeer() == null) {
	    throw new IllegalArgumentException("Transaction not correctly initialized: null jwsdpPeer given");
	}

	if (trx.getGnucashFile() == null) {
	    throw new IllegalArgumentException("Transaction not correctly initialized: null file given");
	}

	jwsdpPeer = trx.getJwsdpPeer();
	file = trx.getGnucashFile();

	for (GnucashGenerInvoice invc : getInvoices()) {
	    invc.addTransaction(this);
	}

    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GncTransaction getJwsdpPeer() {
	return jwsdpPeer;
    }

    /**
     * @see GnucashTransaction#getGnucashFile()
     */
    @Override
    public GnucashFile getGnucashFile() {
	return file;
    }

    // ---------------------------------------------------------------

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#isBalanced()
     */
    public boolean isBalanced() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	return getBalance().equals(new FixedPointNumber());

    }

    /**
     * @throws InvalidCmdtyCurrTypeException 
     * @see GnucashAccount#getCurrencyID()
     */
    public GCshCmdtyCurrID getCmdtyCurrID() throws InvalidCmdtyCurrTypeException {

	GCshCmdtyCurrID result = new GCshCmdtyCurrID(jwsdpPeer.getTrnCurrency().getCmdtySpace(), 
		                             jwsdpPeer.getTrnCurrency().getCmdtyId());
	return result;
    }

    /**
     * The result is in the currency of the transaction.
     *
     * @return the balance of the sum of all splits
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getBalance()
     */
    public FixedPointNumber getBalance() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	FixedPointNumber fp = new FixedPointNumber();

	for (GnucashTransactionSplit split : getSplits()) {
	    fp.add(split.getValue());
	}

	return fp;
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransaction#getBalanceFormatted()
     */
    public String getBalanceFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getCurrencyFormat().format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransaction#getBalanceFormatted(java.util.Locale)
     */
    public String getBalanceFormatted(final Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	NumberFormat cf = NumberFormat.getInstance(lcl);
	if (getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    cf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	} else {
	    cf.setCurrency(null);
	}

	return cf.format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransaction#getNegatedBalance()
     */
    public FixedPointNumber getNegatedBalance() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getBalance().multiply(new FixedPointNumber("-100/100"));
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransaction#getNegatedBalanceFormatted()
     */
    public String getNegatedBalanceFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getCurrencyFormat().format(getNegatedBalance());
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     *
     * @see GnucashTransaction#getNegatedBalanceFormatted(java.util.Locale)
     */
    public String getNegatedBalanceFormatted(final Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	NumberFormat nf = NumberFormat.getInstance(lcl);
	if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    nf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	} else {
	    nf.setCurrency(null);
	}

	return nf.format(getNegatedBalance());
    }

    /**
     * @see GnucashTransaction#getId()
     */
    public GCshID getId() {
	return new GCshID( jwsdpPeer.getTrnId().getValue() );
    }

    /**
     * @return the invoices this transaction belongs to (not payments but the
     *         transaction belonging to handing out the invoice)
     */
    public Collection<GnucashGenerInvoice> getInvoices() {
	Collection<GCshID> invoiceIDs = getInvoiceIDs();
	List<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>(invoiceIDs.size());

	for (GCshID invoiceID : invoiceIDs) {

	    GnucashGenerInvoice invoice = file.getGenerInvoiceByID(invoiceID);
	    if (invoice == null) {
		LOGGER.error("No invoice with id='" + invoiceID + "' for transaction '" + getId() + 
			     "' description '" + getDescription() + "'");
	    } else {
		retval.add(invoice);
	    }

	}

	return retval;
    }

    /**
     * @return the invoices this transaction belongs to (not payments but the
     *         transaction belonging to handing out the invoice)
     */
    public Collection<GCshID> getInvoiceIDs() {

	List<GCshID> retval = new LinkedList<GCshID>();

	SlotsType slots = jwsdpPeer.getTrnSlots();
	if (slots == null) {
	    return retval;
	}

	for (Slot slot : (List<Slot>) slots.getSlot()) {
	    if (!slot.getSlotKey().equals("gncInvoice")) {
		continue;
	    }

	    SlotValue slotVal = slot.getSlotValue();

	    ObjectFactory objectFactory = new ObjectFactory();
	    Slot subSlot = objectFactory.createSlot();
	    subSlot.setSlotKey(slot.getSlotKey());
	    SlotValue subSlotVal = objectFactory.createSlotValue();
	    subSlotVal.setType("string");
	    subSlotVal.getContent().add(slotVal.getContent().get(0));
	    subSlot.setSlotValue(subSlotVal);
	    if (!subSlot.getSlotKey().equals("invoice-guid")) {
		continue;
	    }

	    if (!subSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GUID)) {
		continue;
	    }

	    retval.add(new GCshID( (String) subSlot.getSlotValue().getContent().get(0) ));

	}

	return retval;
    }

    /**
     * @see GnucashTransaction#getDescription()
     */
    public String getDescription() {
	return jwsdpPeer.getTrnDescription();
    }

    // ----------------------------

    /**
     * @see #getSplits()
     */
    protected List<GnucashTransactionSplit> mySplits = null;

    /**
     * @param impl the split to add to mySplits
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected void addSplit(final GnucashTransactionSplitImpl impl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (!jwsdpPeer.getTrnSplits().getTrnSplit().contains(impl.getJwsdpPeer())) {
	    jwsdpPeer.getTrnSplits().getTrnSplit().add(impl.getJwsdpPeer());
	}

	Collection<GnucashTransactionSplit> splits = getSplits();
	if (!splits.contains(impl)) {
	    splits.add(impl);
	}

    }

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSplitsCount()
     */
    public int getSplitsCount() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getSplits().size();
    }

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSplitByID(java.lang.String)
     */
    public GnucashTransactionSplit getSplitByID(final GCshID id) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	for (GnucashTransactionSplit split : getSplits()) {
	    if (split.getId().equals(id)) {
		return split;
	    }

	}
	return null;
    }

    /**
     * @throws SplitNotFoundException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getFirstSplit()
     */
    public GnucashTransactionSplit getFirstSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getSplits().size() == 0 )
	    throw new SplitNotFoundException();
	
	Iterator<GnucashTransactionSplit> iter = getSplits().iterator();
	return iter.next();
    }

    /**
     * @throws SplitNotFoundException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSecondSplit()
     */
    public GnucashTransactionSplit getSecondSplit() throws SplitNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getSplits().size() <= 1 )
	    throw new SplitNotFoundException();
	
	Iterator<GnucashTransactionSplit> iter = getSplits().iterator();
	iter.next();
	return iter.next();
    }

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashTransaction#getSplits()
     */
    public List<GnucashTransactionSplit> getSplits() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return getSplits(false, false);
    }

    public List<GnucashTransactionSplit> getSplits(final boolean addToAcct, final boolean addToInvc) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (mySplits == null) {
	    initSplits(addToAcct, addToInvc);
	}
	return mySplits;
    }

    private void initSplits(final boolean addToAcct, final boolean addToInvc) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	List<GncTransaction.TrnSplits.TrnSplit> jwsdpSplits = jwsdpPeer.getTrnSplits().getTrnSplit();

	mySplits = new ArrayList<GnucashTransactionSplit>(jwsdpSplits.size());
	for (GncTransaction.TrnSplits.TrnSplit element : jwsdpSplits) {
	    mySplits.add(createSplit(element, 
		                     addToAcct, addToInvc));
	}
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param element the jaxb-data
     * @return the new split-instance
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected GnucashTransactionSplitImpl createSplit(
	    final GncTransaction.TrnSplits.TrnSplit element,
	    final boolean addToAcct, 
	    final boolean addToInvc) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return new GnucashTransactionSplitImpl(element, this, 
		                               addToAcct, addToInvc);
    }

    /**
     * @see GnucashTransaction#getDateEntered()
     */
    public ZonedDateTime getDateEntered() {
	if (dateEntered == null) {
	    String s = jwsdpPeer.getTrnDateEntered().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		dateEntered = ZonedDateTime.parse(s, DATE_ENTERED_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException("unparsable date '" + s + "' in transaction!");
		ex.initCause(e);
		throw ex;
	    }
	}

	return dateEntered;
    }

    /**
     * The Currency-Format to use if no locale is given.
     *
     * @return default currency-format with the transaction's currency set
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    protected NumberFormat getCurrencyFormat() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	    if (getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY) {
		currencyFormat.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	    } else {
		currencyFormat = NumberFormat.getInstance();
	    }

	}
	return currencyFormat;
    }

    /**
     * @see GnucashTransaction#getDatePostedFormatted()
     */
    public String getDatePostedFormatted() {
	return DateFormat.getDateInstance().format(getDatePosted());
    }

    /**
     * @see GnucashTransaction#getDatePosted()
     */
    public ZonedDateTime getDatePosted() {
	if (datePosted == null) {
	    String s = jwsdpPeer.getTrnDatePosted().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		datePosted = ZonedDateTime.parse(s, DATE_POSTED_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException(
			"unparsable date '" + s + "' in transaction with id='" + getId() + "'!");
		ex.initCause(e);
		throw ex;
	    }
	}

	return datePosted;
    }

	// -----------------------------------------------------------
    
    @Override
    public String getURL() {
	return getUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI);
    }

	// -----------------------------------------------------------
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[GnucashTransactionImpl:");

	buffer.append(" id: ");
	buffer.append(getId());

	// ::TODO: That only works in simple cases --
	// need a more generic approach
	buffer.append(" amount: ");
	try {
	    buffer.append(getFirstSplit().getValueFormatted());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(" description: '");
	buffer.append(getDescription() + "'");

	buffer.append(" #splits: ");
	try {
	    buffer.append(getSplitsCount());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(" date-posted: ");
	try {
	    buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDatePosted().toString());
	}

	buffer.append(" date-entered: ");
	try {
	    buffer.append(getDateEntered().format(DATE_ENTERED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDateEntered().toString());
	}

	buffer.append("]");

	return buffer.toString();
    }

    /**
     * sorts primarily on the date the transaction happened and secondarily on the
     * date it was entered.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(final GnucashTransaction otherTrx) {
	try {
	    int compare = otherTrx.getDatePosted().compareTo(getDatePosted());
	    if (compare != 0) {
		return compare;
	    }

	    return otherTrx.getDateEntered().compareTo(getDateEntered());
	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    public String getTransactionNumber() {
	return getJwsdpPeer().getTrnNum();
    }
}
