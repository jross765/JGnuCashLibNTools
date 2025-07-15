package org.gnucash.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.TransactionSplitNotFoundException;

/**
 * Extension of GnuCashTransactionImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableTransactionImpl extends GnuCashTransactionImpl 
                                            implements GnuCashWritableTransaction 
{

    /**
     * Our logger for debug- and error-ourput.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableTransactionImpl.class);

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // -----------------------------------------------------------

    /**
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableTransactionImpl(final GncTransaction jwsdpPeer, final GnuCashFileImpl file) {
	super(jwsdpPeer, file, true);

	// repair a broken file
	if (jwsdpPeer.getTrnDatePosted() == null) {
	    LOGGER.warn("Repairing broken transaction " + jwsdpPeer.getTrnId() + " with no date-posted");
	    // we use our own ObjectFactory because: Exception in thread "AWT-EventQueue-0"
	    // java.lang.IllegalAccessError: tried to access
	    // method org.gnucash.write.jwsdpimpl.GnuCashFileImpl.getObjectFactory()
	    // Lbiz/wolschon/fileformats/gnucash/jwsdpimpl/generated/ObjectFactory; from
	    // class org.gnucash.write.jwsdpimpl
	    // .GnuCashWritableTransactionImpl
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
     */
    public GnuCashWritableTransactionImpl(final GnuCashWritableFileImpl file) {
	super(createTransaction_int(file, GCshID.getNew()), file, true);
	file.addTransaction(this);
    }

    public GnuCashWritableTransactionImpl(final GnuCashTransaction trx) {
	super(trx.getJwsdpPeer(), trx.getGnuCashFile(), false);

	// ::TODO
//	System.err.println("NOT IMPLEMENTED YET");
//	for ( GnuCashTransactionSplit splt : trx.getSplits() )  {
//	    addSplit(new GnuCashTransactionSplitImpl(splt.getJwsdpPeer(), trx,
//		                                     false, false));
//	}
    }

    // ---------------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    // -----------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnuCashWritableFileImpl getWritableFile() {
	return (GnuCashWritableFileImpl) getGnuCashFile();
    }

    /**
     * Creates a new Transaction and add's it to the given GnuCash file Don't modify
     * the ID of the new transaction!
     */
    protected static GncTransaction createTransaction_int(
            final GnuCashWritableFileImpl file, 
            final GCshID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("argument <newID> is null");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("argument <newID> is not set");
		}
    
        ObjectFactory factory = file.getObjectFactory();
        
        GncTransaction jwsdpTrx = file.createGncTransactionType();
    
        {
            GncTransaction.TrnId id = factory.createGncTransactionTrnId();
            id.setType(Const.XML_DATA_TYPE_GUID);
            id.setValue(newID.toString());
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
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     */
    @Override
    protected GnuCashTransactionSplitImpl createSplit(
    		final GncTransaction.TrnSplits.TrnSplit jwsdpSplt,
    		final boolean addToAcct,
    		final boolean addToInvc) {
	GnuCashWritableTransactionSplitImpl gcshTrxSplt = 
		new GnuCashWritableTransactionSplitImpl(jwsdpSplt, this,
			                                    addToAcct, addToInvc);
	if (helper.getPropertyChangeSupport() != null) {
	    helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}

	return gcshTrxSplt;
    }

    /**
     * @see GnuCashWritableTransaction#createWritableSplit(GnuCashAccount)
     */
    public GnuCashWritableTransactionSplit createWritableSplit(final GnuCashAccount acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("argument <acct> is null");
		}

	GnuCashWritableTransactionSplitImpl splt = new GnuCashWritableTransactionSplitImpl(this, acct);
	addSplit(splt);
	if (helper.getPropertyChangeSupport() != null) {
	    helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}
	return splt;
    }

    /**
     * @param impl the split to remove from this transaction
     */
    public void remove(final GnuCashWritableTransactionSplit impl) {
	getJwsdpPeer().getTrnSplits().getTrnSplit()
		.remove(((GnuCashWritableTransactionSplitImpl) impl).getJwsdpPeer());
	getWritableFile().setModified(true);
	
	if ( mySplits == null ) { 
		// important!
		@SuppressWarnings("unused")
		List<GnuCashTransactionSplit> dummy = getSplits();
	} else {
		// That does not work with writable splits:
	    // mySplits.remove(impl);
		// Instead:
		for ( int i = 0; i < mySplits.size(); i++ ) {
			if ( mySplits.get(i).getID().equals(impl.getID())) {
				mySplits.remove(i);
				i--;
			}
		}
	}
	
	GnuCashWritableAccountImpl account = (GnuCashWritableAccountImpl) impl.getAccount();
	if (account != null) {
	    account.removeTransactionSplit(impl);
	}

	getWritableFile().removeTransactionSplit(impl);
	// there is no count for splits up to now
	// getWritableFile().decrementCountDataFor()
	if (helper.getPropertyChangeSupport() != null) {
	    helper.getPropertyChangeSupport().firePropertyChange("splits", null, getWritableSplits());
	}
    }

    /**
     * @see GnuCashWritableTransaction#getWritableFirstSplit()
     */
    @Override
    public GnuCashWritableTransactionSplit getFirstSplit() throws TransactionSplitNotFoundException {
	return (GnuCashWritableTransactionSplit) super.getFirstSplit();
    }

    /**
     * @see GnuCashWritableTransaction#getWritableFirstSplit()
     */
    public GnuCashWritableTransactionSplit getWritableFirstSplit() throws TransactionSplitNotFoundException {
	return (GnuCashWritableTransactionSplit) super.getFirstSplit();
    }

    /**
     * @see GnuCashWritableTransaction#getWritableSecondSplit()
     */
    @Override
    public GnuCashWritableTransactionSplit getSecondSplit() throws TransactionSplitNotFoundException {
	return (GnuCashWritableTransactionSplit) super.getSecondSplit();
    }

    /**
     * @see GnuCashWritableTransaction#getWritableSecondSplit()
     */
    public GnuCashWritableTransactionSplit getWritableSecondSplit()  throws TransactionSplitNotFoundException {
	return (GnuCashWritableTransactionSplit) super.getSecondSplit();
    }

    /**
     * @see #getSplitByID(GCshID)
     */
    public GnuCashWritableTransactionSplit getWritableSplitByID(final GCshID spltID) {
	return (GnuCashWritableTransactionSplit) super.getSplitByID(spltID);
    }

    /**
     * @see #getSplits()
     */
    public List<GnuCashWritableTransactionSplit> getWritableSplits() {
	List<GnuCashWritableTransactionSplit> result = new ArrayList<GnuCashWritableTransactionSplit>();
	
	for ( GnuCashTransactionSplit split : super.getSplits() ) {
	    GnuCashWritableTransactionSplit newSplit = new GnuCashWritableTransactionSplitImpl(split, false, false);
	    result.add(newSplit);
	}

	return result;
    }

    /**
     * @param impl the split to add to mySplits
     */
    protected void addSplit(final GnuCashWritableTransactionSplitImpl impl) {
	super.addSplit(impl);
	// ((GnuCashFileImpl) getGnuCashFile()).getTransactionManager().addTransactionSplit(impl, false);
    }

    /**
     * @see GnuCashWritableTransaction#remove()
     */
    public void remove() {
	getWritableFile().removeTransaction(this);
//	Collection<GnuCashWritableTransactionSplit> c = new ArrayList<GnuCashWritableTransactionSplit>();
//	c.addAll(getWritableSplits());
//	for (GnuCashWritableTransactionSplit element : c) {
//	    element.remove();
//	}
    }

    /**
     * @see GnuCashWritableTransaction#setNumber(java.lang.String)
     */
    public void setNumber(final String numStr) {
        if ( numStr == null ) {
            throw new IllegalArgumentException("argument <numStr> is null");
        }
    
        if ( numStr.trim().length() == 0 ) {
            throw new IllegalArgumentException("argument <numStr> is empty");
        }
    
        String old = getJwsdpPeer().getTrnNum();
        getJwsdpPeer().setTrnNum(numStr);
        getWritableFile().setModified(true);
    
        if (old == null || !old.equals(numStr)) {
            if (helper.getPropertyChangeSupport() != null) {
            	helper.getPropertyChangeSupport().firePropertyChange("transactionNumber", old, numStr);
            }
        }
    }

    /**
     * @param cmdtyCurrID the new commodity/currency name-space/code
     * 
     */
    public void setCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) {
	this.getJwsdpPeer().getTrnCurrency().setCmdtySpace(cmdtyCurrID.getNameSpace());
	this.getJwsdpPeer().getTrnCurrency().setCmdtyId(cmdtyCurrID.getCode());
	getWritableFile().setModified(true);
    }

    /**
     * @param dateEntered 
     * @see GnuCashWritableTransaction#setDateEntered(LocalDateTime)
     */
    public void setDateEntered(final ZonedDateTime dateEntered) {
	if ( dateEntered == null ) {
	    throw new IllegalArgumentException("argument <dateEntered> is null");
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
    
    public void setDatePosted(final LocalDate datePosted) {
	if ( datePosted == null ) {
	    throw new IllegalArgumentException("argument <datePosted> is null");
	}

	this.datePosted = ZonedDateTime.of(datePosted, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.datePosted.format(DATE_POSTED_FORMAT);
	getJwsdpPeer().getTrnDatePosted().setTsDate(datePostedStr);
	getWritableFile().setModified(true);
    }

    public void setDescription(final String descr) {
	if ( descr == null) {
	    throw new IllegalArgumentException("argument <descr> is null");
	}

	// Caution: empty string allowed here
//	if ( descr.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("argument <descr> is empty");
//	}

	String old = getJwsdpPeer().getTrnDescription();
	getJwsdpPeer().setTrnDescription(descr);
	getWritableFile().setModified(true);

	if (old == null || !old.equals(descr)) {
	    if (helper.getPropertyChangeSupport() != null) {
	    	helper.getPropertyChangeSupport().firePropertyChange("description", old, descr);
	    }
	}
    }

    // ---------------------------------------------------------------

    @Override
    public void setURL(final String url) {
    	try {
    		setUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI, url);
    	} catch (SlotListDoesNotContainKeyException exc ) {
    		addUserDefinedAttribute(Const.XML_DATA_TYPE_STRING, Const.SLOT_KEY_ASSOC_URI, url);
    	}
    }

    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getTrnSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setTrnSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getTrnSlots(),
										 getWritableFile(), 
										 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getTrnSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getTrnSlots(),
										 	getWritableFile(), 
										 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getTrnSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getTrnSlots(),
										 getWritableFile(), 
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getTrnSlots());
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableTransactionImpl [");

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
