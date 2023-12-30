package org.gnucash.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.SplitNotFoundException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.write.GnucashWritableObject;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashTransactionImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableTransactionImpl extends GnucashTransactionImpl 
                                            implements GnucashWritableTransaction 
{

    /**
     * Our logger for debug- and error-ourput.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableTransactionImpl.class);

    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    private final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(this);

    // -----------------------------------------------------------

    /**
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnucashWritableTransactionImpl(final GncTransaction jwsdpPeer, final GnucashFileImpl file) {
	super(jwsdpPeer, file, true);

	// repair a broken file
	if (jwsdpPeer.getTrnDatePosted() == null) {
	    LOGGER.warn("Repairing broken transaction " + jwsdpPeer.getTrnId() + " with no date-posted!");
	    // we use our own ObjectFactory because: Exception in thread "AWT-EventQueue-0"
	    // java.lang.IllegalAccessError: tried to access
	    // method org.gnucash.write.jwsdpimpl.GnucashFileImpl.getObjectFactory()
	    // Lbiz/wolschon/fileformats/gnucash/jwsdpimpl/generated/ObjectFactory; from
	    // class org.gnucash.write.jwsdpimpl
	    // .GnucashWritableTransactionImpl
	    // ObjectFactory factory = file.getObjectFactory();
	    ObjectFactory factory = new ObjectFactory();
	    GncTransaction.TrnDatePosted datePosted = factory.createGncTransactionTrnDatePosted();
	    datePosted.setTsDate(jwsdpPeer.getTrnDateEntered().getTsDate());
	    jwsdpPeer.setTrnDatePosted(datePosted);
	}

    }

    /**
     * Create a new Transaction and add it to the file.
     *
     * @param file the file we belong to
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableTransactionImpl(final GnucashWritableFileImpl file) throws IllegalArgumentException {
	super(createTransaction_int(file, GCshID.getNew()), file, true);
	file.addTransaction(this);
    }

    public GnucashWritableTransactionImpl(final GnucashTransaction trx) throws IllegalArgumentException {
	super(trx.getJwsdpPeer(), trx.getGnucashFile(), false);

	// ::TODO
//	System.err.println("NOT IMPLEMENTED YET");
//	for ( GnucashTransactionSplit splt : trx.getSplits() )  {
//	    addSplit(new GnucashTransactionSplitImpl(splt.getJwsdpPeer(), trx,
//		                                     false, false));
//	}
    }

    // -----------------------------------------------------------

    /**
     * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String,
     *      java.lang.String)
     */
    public void setUserDefinedAttribute(final String name, final String value) {
	helper.setUserDefinedAttribute(name, value);
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnucashWritableFileImpl getWritableFile() {
	return (GnucashWritableFileImpl) getGnucashFile();
    }

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     */
    protected static GncTransaction createTransaction_int(
            final GnucashWritableFileImpl file, 
            final GCshID trxID) {
        if ( ! trxID.isSet() ) {
            throw new IllegalArgumentException("GUID not set!");
        }
    
        ObjectFactory factory = file.getObjectFactory();
        GncTransaction jwsdpTrx = file.createGncTransactionType();
    
        {
            GncTransaction.TrnId id = factory.createGncTransactionTrnId();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(trxID.toString());
            jwsdpTrx.setTrnId(id);
        }
    
        {
            GncTransaction.TrnDateEntered dateEntered = factory.createGncTransactionTrnDateEntered();
            dateEntered.setTsDate(DATE_ENTERED_FORMAT.format(ZonedDateTime.now()));
            jwsdpTrx.setTrnDateEntered(dateEntered);
        }
    
        {
            GncTransaction.TrnDatePosted datePosted = factory.createGncTransactionTrnDatePosted();
            datePosted.setTsDate(DATE_ENTERED_FORMAT.format(ZonedDateTime.now()));
            jwsdpTrx.setTrnDatePosted(datePosted);
        }
    
        {
            GncTransaction.TrnCurrency currency = factory.createGncTransactionTrnCurrency();
            currency.setCmdtyId(file.getDefaultCurrencyID());
            currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            jwsdpTrx.setTrnCurrency(currency);
        }
    
        {
            GncTransaction.TrnSplits splits = factory.createGncTransactionTrnSplits();
            jwsdpTrx.setTrnSplits(splits);
        }
    
        jwsdpTrx.setVersion(Const.XML_FORMAT_VERSION);
        jwsdpTrx.setTrnDescription("-");
    
        LOGGER.debug("createTransaction_int: Created new transaction (core): " + jwsdpTrx.getTrnId().getValue());
    
        return jwsdpTrx;
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param element the jaxb-data
     * @return the new split-instance
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    protected GnucashTransactionSplitImpl createSplit(
	    final GncTransaction.TrnSplits.TrnSplit element,
	    final boolean addToAcct,
	    final boolean addToInvc) throws IllegalArgumentException {
	GnucashWritableTransactionSplitImpl splt = 
		new GnucashWritableTransactionSplitImpl(element, this,
			                                addToAcct, addToInvc);
	if (getPropertyChangeSupport() != null) {
	    getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}

	return splt;
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#createWritableSplit(GnucashAccount)
     */
    public GnucashWritableTransactionSplit createWritableSplit(final GnucashAccount account) throws IllegalArgumentException {
	GnucashWritableTransactionSplitImpl splt = new GnucashWritableTransactionSplitImpl(this, account);
	addSplit(splt);
	if (getPropertyChangeSupport() != null) {
	    getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}
	return splt;
    }

    /**
     * @param impl the split to remove from this transaction
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void remove(final GnucashWritableTransactionSplit impl) throws IllegalArgumentException {
	getJwsdpPeer().getTrnSplits().getTrnSplit().remove(((GnucashWritableTransactionSplitImpl) impl).getJwsdpPeer());
	getWritableFile().setModified(true);
	if (mySplits != null) {
	    mySplits.remove(impl);
	}
	GnucashWritableAccountImpl account = (GnucashWritableAccountImpl) impl.getAccount();
	if (account != null) {
	    account.removeTransactionSplit(impl);
	}

	// there is no count for splits up to now
	// getWritableFile().decrementCountDataFor()

	if (getPropertyChangeSupport() != null) {
	    getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}
    }

    /**
     * @throws SplitNotFoundException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableFirstSplit()
     */
    @Override
    public GnucashWritableTransactionSplit getFirstSplit() throws SplitNotFoundException, IllegalArgumentException {
	return (GnucashWritableTransactionSplit) super.getFirstSplit();
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableFirstSplit()
     */
    public GnucashWritableTransactionSplit getWritableFirstSplit() throws SplitNotFoundException, IllegalArgumentException {
	return (GnucashWritableTransactionSplit) super.getFirstSplit();
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableSecondSplit()
     */
    @Override
    public GnucashWritableTransactionSplit getSecondSplit() throws SplitNotFoundException, IllegalArgumentException {
	return (GnucashWritableTransactionSplit) super.getSecondSplit();
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableSecondSplit()
     */
    public GnucashWritableTransactionSplit getWritableSecondSplit()  throws SplitNotFoundException, IllegalArgumentException {
	return (GnucashWritableTransactionSplit) super.getSecondSplit();
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableSplitByID(java.lang.String)
     */
    public GnucashWritableTransactionSplit getWritableSplitByID(final GCshID id) throws IllegalArgumentException {
	return (GnucashWritableTransactionSplit) super.getSplitByID(id);
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#getWritableSplits()
     */
    public List<GnucashWritableTransactionSplit> getWritableSplits() throws IllegalArgumentException {
	List<GnucashWritableTransactionSplit> result = new ArrayList<GnucashWritableTransactionSplit>();
	
	for ( GnucashTransactionSplit split : super.getSplits() ) {
	    GnucashWritableTransactionSplit newSplit = new GnucashWritableTransactionSplitImpl(split);
	    result.add(newSplit);
	}

	return result;
    }

    /**
     * @param impl the split to add to mySplits
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected void addSplit(final GnucashWritableTransactionSplitImpl impl) throws IllegalArgumentException {
	super.addSplit(impl);
	// ((GnucashFileImpl) getGnucashFile()).getTransactionManager().addTransactionSplit(impl, false);
    }

    /**
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableTransaction#remove()
     */
    public void remove() throws IllegalArgumentException {
	getWritableFile().removeTransaction(this);
	Collection<GnucashWritableTransactionSplit> c = new ArrayList<GnucashWritableTransactionSplit>();
	c.addAll(getWritableSplits());
	for (GnucashWritableTransactionSplit element : c) {
	    element.remove();
	}

    }

    /**
     * @see GnucashWritableTransaction#setNumber(java.lang.String)
     */
    public void setNumber(final String number) {
        if ( number == null ) {
            throw new IllegalArgumentException("null number given!");
        }
    
        if ( number.trim().length() == 0 ) {
            throw new IllegalArgumentException("empty number given!");
        }
    
        String old = getJwsdpPeer().getTrnNum();
        getJwsdpPeer().setTrnNum(number);
        getWritableFile().setModified(true);
    
        if (old == null || !old.equals(number)) {
            if (getPropertyChangeSupport() != null) {
        	getPropertyChangeSupport().firePropertyChange("transactionNumber", old, number);
            }
        }
    }

    /**
     * @param id the new commodity/currency name-space/code
     * @see #setCurrencyNameSpace(String)
     * @see {@link GnucashTransaction#getCurrencyID()}
     */
    public void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) {
	this.getJwsdpPeer().getTrnCurrency().setCmdtySpace(cmdtyCurrID.getNameSpace());
	this.getJwsdpPeer().getTrnCurrency().setCmdtyId(cmdtyCurrID.getCode());
	getWritableFile().setModified(true);
    }

    /**
     * @see GnucashWritableTransaction#setDateEntered(LocalDateTime)
     */
    public void setDateEntered(final ZonedDateTime dateEntered) {
	if ( dateEntered == null ) {
	    throw new IllegalArgumentException("null date given!");
	}

	this.dateEntered = dateEntered;
	String dateEnteredStr = this.dateEntered.format(DATE_ENTERED_FORMAT);
	getJwsdpPeer().getTrnDateEntered().setTsDate(dateEnteredStr);
	getWritableFile().setModified(true);
    }


    @Override
    public void setDateEntered(LocalDateTime dateEntered) {
	setDateEntered(dateEntered.atZone(ZoneId.systemDefault()));
    }
    
    /**
     * @see GnucashWritableTransaction#setDatePosted(LocalDateTime)
     */
    public void setDatePosted(final LocalDate datePosted) {
	if ( datePosted == null ) {
	    throw new IllegalArgumentException("null date given!");
	}

	this.datePosted = ZonedDateTime.of(datePosted, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.datePosted.format(DATE_POSTED_FORMAT);
	getJwsdpPeer().getTrnDatePosted().setTsDate(datePostedStr);
	getWritableFile().setModified(true);
    }

    /**
     * @see GnucashWritableTransaction#setNotes(java.lang.String)
     */
    public void setDescription(final String descr) {
	if ( descr == null) {
	    throw new IllegalArgumentException("null description given!");
	}

	// Caution: empty string allowed here
//	if ( descr.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty description given!");
//	}

	String old = getJwsdpPeer().getTrnDescription();
	getJwsdpPeer().setTrnDescription(descr);
	getWritableFile().setModified(true);

	if (old == null || !old.equals(descr)) {
	    if (getPropertyChangeSupport() != null) {
		getPropertyChangeSupport().firePropertyChange("description", old, descr);
	    }
	}
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableTransactionImpl [");

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

}
