package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.SimpleAccount;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnuCashAccount that used a
 * jwsdp-generated backend.
 */
public class GnuCashAccountImpl extends SimpleAccount 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashAccountImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncAccount jwsdpPeer;
    
    // ---------------------------------------------------------------

    /**
     * Helper to implement the {@link GnuCashObject}-interface.
     */
//    protected GnuCashObjectImpl helper;

    // ---------------------------------------------------------------

    /**
     * The splits of this transaction. May not be fully initialized during loading
     * of the GnuCash file.
     *
     * @see #mySplitsNeedSorting
     */
    private final List<GnuCashTransactionSplit> mySplits = new ArrayList<GnuCashTransactionSplit>();

    /**
     * If {@link #mySplits} needs to be sorted because it was modified. Sorting is
     * done in a lazy way.
     */
    private boolean mySplitsNeedSorting = false;
    
    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashAccountImpl(final GncAccount peer, final GnuCashFile gcshFile) {
	super(gcshFile);

//	if (peer.getActSlots() == null) {
//	    peer.setActSlots(new ObjectFactory().createSlotsType());
//	}

	jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncAccount getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashAccount#getID()
     */
    public GCshID getID() {
	return new GCshID(jwsdpPeer.getActId().getValue());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashAccount#getParentAccountID()
     */
    public GCshID getParentAccountID() {
	GncAccount.ActParent parent = jwsdpPeer.getActParent();
	if (parent == null) {
	    return null;
	}

	return new GCshID(parent.getValue());
    }

    /**
     * @see GnuCashAccount#getChildren()
     */
    public List<GnuCashAccount> getChildren() {
    	return getGnuCashFile().getAccountsByParentID(getID());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashAccount#getName()
     */
    public String getName() {
    	return jwsdpPeer.getActName();
    }

    /**
     * @see GnuCashAccount#getDescription()
     */
    public String getDescription() {
    	return jwsdpPeer.getActDescription();
    }

    /**
     * @see GnuCashAccount#getCode()
     */
    public String getCode() {
    	return jwsdpPeer.getActCode();
    }

    private String getTypeStr() throws UnknownAccountTypeException {
    	return jwsdpPeer.getActType();
    }

    /**
     * @throws UnknownAccountTypeException 
     * @see GnuCashAccount#getType()
     */
    public Type getType() throws UnknownAccountTypeException {
	try {
	    Type result = Type.valueOf( getTypeStr() );
	    return result;
	} catch ( Exception exc ) {
	    throw new UnknownAccountTypeException();
	}
    }

    /**
	 * {@inheritDoc}
	 * @throws InvalidCmdtyCurrTypeException 
	 */
    @Override
	public GCshCmdtyCurrID getCmdtyCurrID() throws InvalidCmdtyCurrTypeException {
	if ( jwsdpPeer.getActCommodity() == null &&
	     jwsdpPeer.getActType().equals(Type.ROOT.toString()) ) {
	    return new GCshCurrID(); // default-currency because gnucash 2.2 has no currency on the root-account
	}
	
	GCshCmdtyCurrID result = new GCshCmdtyCurrID(jwsdpPeer.getActCommodity().getCmdtySpace(),
		                             jwsdpPeer.getActCommodity().getCmdtyId()); 
	
	return result;
	}

	/**
     * @see GnuCashAccount#getTransactionSplits()
     */
    @Override
    public List<GnuCashTransactionSplit> getTransactionSplits() {

    	if (mySplitsNeedSorting) {
    		Collections.sort(mySplits);
    		mySplitsNeedSorting = false;
    	}

    	return mySplits;
    }

    /**
     * @see GnuCashAccount#addTransactionSplit(GnuCashTransactionSplit)
     */
    public void addTransactionSplit(final GnuCashTransactionSplit splt) {
	GnuCashTransactionSplit old = getTransactionSplitByID(splt.getID());
	if ( old != null ) {
	    // There already is a split with that ID
	    if ( ! old.equals(splt) ) {
	    	System.err.println("addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " + 
	    			splt.getID() + "[" + splt.getClass().getName() + "] and " + 
	    			old.getID() + "[" + old.getClass().getName() + "]\n" + 
	    			"new=" + splt.toString() + "\n" + 
	    			"old=" + old.toString());
	    	LOGGER.error("addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " + 
	    			splt.getID() + "[" + splt.getClass().getName() + "] and " + 
	    			old.getID() + "[" + old.getClass().getName() + "]\n" + 
	    			"new=" + splt.toString() + "\n" + 
	    			"old=" + old.toString());
	    	IllegalStateException exc = new IllegalStateException("DEBUG");
	    	exc.printStackTrace();
	    	replaceTransactionSplit(old, splt);
	    }
	} else {
	    // There is no split with that ID yet
	    mySplits.add(splt);
	    mySplitsNeedSorting = true;
	}
    }

    /**
     * For internal use only.
     *
     * @param splt
     */
    private void replaceTransactionSplit(final GnuCashTransactionSplit splt,
	    final GnuCashTransactionSplit impl) {
    	if ( ! mySplits.remove(splt) ) {
    		throw new IllegalArgumentException("old object not found!");
    	}

    	mySplits.add(impl);
    }

    // -----------------------------------------------------------------

    @Override
    public String getUserDefinedAttribute(final String name) {
    	return HasUserDefinedAttributesImpl
    			.getUserDefinedAttributeCore(jwsdpPeer.getActSlots(), name);
    }

    @Override
    public List<String> getUserDefinedAttributeKeys() {
    	return HasUserDefinedAttributesImpl
    			.getUserDefinedAttributeKeysCore(jwsdpPeer.getActSlots());
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashAccountImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", code='");
	buffer.append(getCode() + "'");
	
	buffer.append(", type=");
	try {
	    buffer.append(getType());
	} catch (UnknownAccountTypeException e) {
	    buffer.append("ERROR");
	}
	
	buffer.append(", qualif-name='");
	buffer.append(getQualifiedName() + "'");
	
	buffer.append(", commodity/currency='");
	try {
	    buffer.append(getCmdtyCurrID() + "'");
	} catch (InvalidCmdtyCurrTypeException e) {
	    buffer.append("ERROR");
	}
	
	buffer.append("]");
	
	return buffer.toString();
    }
    
}
