package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashTransactionSplit;
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
 * Implementation of GnucashAccount that used a
 * jwsdp-generated backend.
 */
public class GnucashAccountImpl extends SimpleAccount 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashAccountImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncAccount jwsdpPeer;
    
    // ---------------------------------------------------------------

    /**
     * Helper to implement the {@link GnucashObject}-interface.
     */
//    protected GnucashObjectImpl helper;

    // ---------------------------------------------------------------

    /**
     * The splits of this transaction. May not be fully initialized during loading
     * of the gnucash-file.
     *
     * @see #mySplitsNeedSorting
     */
    private final List<GnucashTransactionSplit> mySplits = new ArrayList<GnucashTransactionSplit>();

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
    public GnucashAccountImpl(final GncAccount peer, final GnucashFile gcshFile) {
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
     * @see GnucashAccount#getID()
     */
    public GCshID getID() {
	return new GCshID(jwsdpPeer.getActId().getValue());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashAccount#getParentAccountID()
     */
    public GCshID getParentAccountID() {
	GncAccount.ActParent parent = jwsdpPeer.getActParent();
	if (parent == null) {
	    return null;
	}

	return new GCshID(parent.getValue());
    }

    /**
     * @see GnucashAccount#getChildren()
     */
    public Collection<GnucashAccount> getChildren() {
    	return getGnucashFile().getAccountsByParentID(getID());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashAccount#getName()
     */
    public String getName() {
    	return jwsdpPeer.getActName();
    }

    /**
     * @see GnucashAccount#getDescription()
     */
    public String getDescription() {
    	return jwsdpPeer.getActDescription();
    }

    /**
     * @see GnucashAccount#getCode()
     */
    public String getCode() {
    	return jwsdpPeer.getActCode();
    }

    private String getTypeStr() throws UnknownAccountTypeException {
    	return jwsdpPeer.getActType();
    }

    /**
     * @throws UnknownAccountTypeException 
     * @see GnucashAccount#getType()
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
     * @see GnucashAccount#getTransactionSplits()
     */
    @Override
    public List<GnucashTransactionSplit> getTransactionSplits() {

    	if (mySplitsNeedSorting) {
    		Collections.sort(mySplits);
    		mySplitsNeedSorting = false;
    	}

    	return mySplits;
    }

    /**
     * @see GnucashAccount#addTransactionSplit(GnucashTransactionSplit)
     */
    public void addTransactionSplit(final GnucashTransactionSplit splt) {
	GnucashTransactionSplit old = getTransactionSplitByID(splt.getID());
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
    private void replaceTransactionSplit(final GnucashTransactionSplit splt,
	    final GnucashTransactionSplit impl) {
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
	buffer.append("GnucashAccountImpl [");
	
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
