package org.gnucash.api.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.SplitNotFoundException;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnuCashTransaction that uses JWSDP.
 */
public class GnuCashTransactionImpl extends GnuCashObjectImpl 
                                    implements GnuCashTransaction 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashTransactionImpl.class);

    protected static final DateTimeFormatter DATE_ENTERED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_POSTED_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncTransaction jwsdpPeer;

    /**
     * The Currency-Format to use if no locale is given.
     */
    protected NumberFormat currencyFormat;

    // ---------------------------------------------------------------

    /**
     * @see GnuCashTransaction#getDateEntered()
     */
    protected ZonedDateTime dateEntered;

    /**
     * @see GnuCashTransaction#getDatePosted()
     */
    protected ZonedDateTime datePosted;

    // ---------------------------------------------------------------

    /**
     * Create a new Transaction, facading a JWSDP-transaction.
     *
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     * @param addTrxToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashTransactionImpl(
	    final GncTransaction peer, 
	    final GnuCashFile gcshFile,
	    final boolean addTrxToInvc) {
	super(gcshFile);

//	if (peer.getTrnSlots() == null) {
//	    peer.setTrnSlots(jwsdpPeer.getTrnSlots());
//	}

	if (peer == null) {
	    throw new IllegalArgumentException("null jwsdpPeer given");
	}

	if (gcshFile == null) {
	    throw new IllegalArgumentException("null file given");
	}

	jwsdpPeer = peer;

	if ( addTrxToInvc ) {
	    for ( GnuCashGenerInvoice invc : getInvoices() ) {
		invc.addTransaction(this);
	    }
	}
    }

    // Copy-constructor
    public GnuCashTransactionImpl(final GnuCashTransaction trx) {
	super(trx.getGnuCashFile());

//	if (trx.getJwsdpPeer().getTrnSlots() == null) {
//	    trx.getJwsdpPeer().setTrnSlots(jwsdpPeer.getTrnSlots());
//	}

	if (trx.getJwsdpPeer() == null) {
	    throw new IllegalArgumentException("Transaction not correctly initialized: null jwsdpPeer given");
	}

	if (trx.getGnuCashFile() == null) {
	    throw new IllegalArgumentException("Transaction not correctly initialized: null file given");
	}

	jwsdpPeer = trx.getJwsdpPeer();

	for ( GnuCashGenerInvoice invc : getInvoices() ) {
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

    // ---------------------------------------------------------------

    /**
     *  
     * @see GnuCashTransaction#isBalanced()
     */
    public boolean isBalanced() {

	return getBalance().equals(new FixedPointNumber());

    }

    /**
     * @throws InvalidCmdtyCurrTypeException 
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
     *  
     * @see GnuCashTransaction#getBalance()
     */
    public FixedPointNumber getBalance() {

	FixedPointNumber fp = new FixedPointNumber();

	for (GnuCashTransactionSplit split : getSplits()) {
	    fp.add(split.getValue());
	}

	return fp;
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     *
     * @see GnuCashTransaction#getBalanceFormatted()
     */
    public String getBalanceFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
    	return getCurrencyFormat().format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     *
     * @see GnuCashTransaction#getBalanceFormatted(java.util.Locale)
     */
    public String getBalanceFormatted(final Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {

	NumberFormat cf = NumberFormat.getInstance(lcl);
	if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    cf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	} else {
	    cf.setCurrency(null);
	}

	return cf.format(getBalance());
    }

    /**
     * The result is in the currency of the transaction.
     *  
     *
     * @see GnuCashTransaction#getNegatedBalance()
     */
    public FixedPointNumber getNegatedBalance() throws IllegalArgumentException {
	return getBalance().multiply(new FixedPointNumber("-100/100"));
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     *
     * @see GnuCashTransaction#getNegatedBalanceFormatted()
     */
    public String getNegatedBalanceFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return getCurrencyFormat().format(getNegatedBalance());
    }

    /**
     * The result is in the currency of the transaction.
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     *  
     *
     * @see GnuCashTransaction#getNegatedBalanceFormatted(java.util.Locale)
     */
    public String getNegatedBalanceFormatted(final Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	NumberFormat nf = NumberFormat.getInstance(lcl);
	if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    nf.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	} else {
	    nf.setCurrency(null);
	}

	return nf.format(getNegatedBalance());
    }

    /**
     * @see GnuCashTransaction#getID()
     */
    public GCshID getID() {
	return new GCshID( jwsdpPeer.getTrnId().getValue() );
    }

    /**
     * @return the invoices this transaction belongs to (not payments but the
     *         transaction belonging to handing out the invoice)
     */
    public List<GnuCashGenerInvoice> getInvoices() {
    	List<GCshID> invoiceIDs = getInvoiceIDs();
	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();

	for (GCshID invoiceID : invoiceIDs) {

	    GnuCashGenerInvoice invoice = getGnuCashFile().getGenerInvoiceByID(invoiceID);
	    if (invoice == null) {
		LOGGER.error("No invoice with id='" + invoiceID + "' for transaction '" + getID() + 
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
    public List<GCshID> getInvoiceIDs() {

	List<GCshID> retval = new ArrayList<GCshID>();

	SlotsType slots = jwsdpPeer.getTrnSlots();
	if (slots == null) {
	    return retval;
	}

	for (Slot slot : (List<Slot>) slots.getSlot()) {
	    if (!slot.getSlotKey().equals(Const.SLOT_KEY_INVC_TYPE)) {
		continue;
	    }

	    SlotValue slotVal = slot.getSlotValue();

	    ObjectFactory objectFactory = new ObjectFactory();
	    Slot subSlot = objectFactory.createSlot();
	    subSlot.setSlotKey(slot.getSlotKey());
	    SlotValue subSlotVal = objectFactory.createSlotValue();
	    subSlotVal.setType(Const.XML_DATA_TYPE_STRING);
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
     * @see GnuCashTransaction#getDescription()
     */
    public String getDescription() {
	return jwsdpPeer.getTrnDescription();
    }

    // ----------------------------

    /**
     * @see #getSplits()
     */
    protected List<GnuCashTransactionSplit> mySplits = null;

    /**
     * @param impl the split to add to mySplits
     * @throws ClassNotFoundException 
     */
    protected void addSplit(final GnuCashTransactionSplitImpl impl) {
	if (!jwsdpPeer.getTrnSplits().getTrnSplit().contains(impl.getJwsdpPeer())) {
	    jwsdpPeer.getTrnSplits().getTrnSplit().add(impl.getJwsdpPeer());
	}

	List<GnuCashTransactionSplit> splits = getSplits();
	if (!splits.contains(impl)) {
	    splits.add(impl);
	}

    }

    /**
     *  
     * @see GnuCashTransaction#getSplitsCount()
     */
    public int getSplitsCount() {
	return getSplits().size();
    }

    public GnuCashTransactionSplit getSplitByID(final GCshID id) {
	for (GnuCashTransactionSplit split : getSplits()) {
	    if (split.getID().equals(id)) {
		return split;
	    }

	}
	return null;
    }

    /**
     * @throws SplitNotFoundException 
     *  
     * @see GnuCashTransaction#getFirstSplit()
     */
    public GnuCashTransactionSplit getFirstSplit() throws SplitNotFoundException {
	if ( getSplits().size() == 0 )
	    throw new SplitNotFoundException();
	
	return getSplits().get(0);
    }

    /**
     * @throws SplitNotFoundException 
     *  
     * @see GnuCashTransaction#getSecondSplit()
     */
    public GnuCashTransactionSplit getSecondSplit() throws SplitNotFoundException {
	if ( getSplits().size() <= 1 )
	    throw new SplitNotFoundException();
	
	return getSplits().get(1);
    }

    /**
     *  
     * @see GnuCashTransaction#getSplits()
     */
    public List<GnuCashTransactionSplit> getSplits() {
	return getSplits(false, false);
    }

    public List<GnuCashTransactionSplit> getSplits(final boolean addToAcct, final boolean addToInvc) {
	if (mySplits == null) {
	    initSplits(addToAcct, addToInvc);
	}
	return mySplits;
    }

    private void initSplits(final boolean addToAcct, final boolean addToInvc) {
	List<GncTransaction.TrnSplits.TrnSplit> jwsdpSplits = jwsdpPeer.getTrnSplits().getTrnSplit();

	mySplits = new ArrayList<GnuCashTransactionSplit>();
	for (GncTransaction.TrnSplits.TrnSplit element : jwsdpSplits) {
	    mySplits.add(createSplit(element, 
		                         addToAcct, addToInvc));
	}
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     * @throws ClassNotFoundException 
     */
    protected GnuCashTransactionSplitImpl createSplit(
	    final GncTransaction.TrnSplits.TrnSplit jwsdpSplt,
	    final boolean addToAcct, 
	    final boolean addToInvc) {
	return new GnuCashTransactionSplitImpl(jwsdpSplt, this, 
		                                   addToAcct, addToInvc);
    }

    /**
     * @see GnuCashTransaction#getDateEntered()
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
	    if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    	currencyFormat.setCurrency(new GCshCurrID(getCmdtyCurrID()).getCurrency());
	    } else {
	    	currencyFormat = NumberFormat.getInstance();
	    }

	}
	return currencyFormat;
    }

    /**
     * @see GnuCashTransaction#getDatePostedFormatted()
     */
    public String getDatePostedFormatted() {
	return DateFormat.getDateInstance().format(getDatePosted());
    }

    /**
     * @see GnuCashTransaction#getDatePosted()
     */
    public ZonedDateTime getDatePosted() {
	if (datePosted == null) {
	    String s = jwsdpPeer.getTrnDatePosted().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		datePosted = ZonedDateTime.parse(s, DATE_POSTED_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException(
			"unparsable date '" + s + "' in transaction with id='" + getID() + "'!");
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
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeCore(jwsdpPeer.getTrnSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeKeysCore(jwsdpPeer.getTrnSlots());
	}

	// -----------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashTransactionImpl [");

	buffer.append("id=");
	buffer.append(getID());

	// ::TODO: That only works in simple cases --
	// need a more generic approach
	buffer.append(", amount=");
	try {
	    buffer.append(getFirstSplit().getValueFormatted());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(", description='");
	buffer.append(getDescription() + "'");

	buffer.append(", #splits=");
	try {
	    buffer.append(getSplitsCount());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(", date-posted=");
	try {
	    buffer.append(getDatePosted().format(DATE_POSTED_FORMAT));
	} catch (Exception e) {
	    buffer.append(getDatePosted().toString());
	}

	buffer.append(", date-entered=");
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
    public int compareTo(final GnuCashTransaction otherTrx) {
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

    public String getNumber() {
	return getJwsdpPeer().getTrnNum();
    }

}
