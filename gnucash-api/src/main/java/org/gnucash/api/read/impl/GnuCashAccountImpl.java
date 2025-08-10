package org.gnucash.api.read.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.api.read.aux.GCshAcctReconInfo;
import org.gnucash.api.read.impl.aux.GCshAccountLotImpl;
import org.gnucash.api.read.impl.aux.GCshAcctReconInfoImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.SimpleAccount;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.UnknownAccountTypeException;

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
    
    // ----------------------------
    
    protected /* final */ List<GCshAccountLot> myLots = null; // sic, null, i.Ggs. zu oben

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashAccountImpl(
    		final GncAccount peer,
    		final GnuCashFile gcshFile) {
	super(gcshFile);

//	if (peer.getActSlots() == null) {
//	    peer.setActSlots(new ObjectFactory().createSlotsType());
//	}

//	if (peer.getActLots() == null) {
//    peer.setActLots(new ObjectFactory().createGncAccountActLots());
//  }

	if (peer == null) {
	    throw new IllegalArgumentException("argument <peer> is null");
	}

	if (gcshFile == null) {
	    throw new IllegalArgumentException("argument <gcshFile> is null");
	}

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
    public GCshAcctID getID() {
	return new GCshAcctID(jwsdpPeer.getActId().getValue());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashAccount#getParentAccountID()
     */
    public GCshAcctID getParentAccountID() {
	GncAccount.ActParent parent = jwsdpPeer.getActParent();
	if (parent == null) {
	    return null;
	}

	return new GCshAcctID(parent.getValue());
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

    private String getTypeStr() {
    	return jwsdpPeer.getActType();
    }

    /**
     * @see GnuCashAccount#getType()
     */
    public Type getType() {
	try {
	    Type result = Type.valueOf( getTypeStr() );
	    return result;
	} catch ( Exception exc ) {
	    throw new UnknownAccountTypeException();
	}
    }

    /**
	 * {@inheritDoc}
	 */
    @Override
	public GCshCmdtyCurrID getCmdtyCurrID() {
	if ( jwsdpPeer.getActCommodity() == null &&
	     jwsdpPeer.getActType().equals(Type.ROOT.toString()) ) {
	    return new GCshCurrID(); // default-currency because gnucash 2.2 has no currency on the root-account
	}
	
	GCshCmdtyCurrID result = new GCshCmdtyCurrID(jwsdpPeer.getActCommodity().getCmdtySpace(),
		                             jwsdpPeer.getActCommodity().getCmdtyId()); 
	
	return result;
	}
    
    // ----------------------------

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
	    			splt.getID() + " [" + splt.getClass().getName() + "] and " + 
	    			old.getID() + " [" + old.getClass().getName() + "]\n" + 
	    			"new = " + splt.toString() + "\n" + 
	    			"old = " + old.toString());
	    	LOGGER.error("addTransactionSplit: New Transaction Split object with same ID, needs to be replaced: " + 
	    			splt.getID() + " [" + splt.getClass().getName() + "] and " + 
	    			old.getID() + " [" + old.getClass().getName() + "]\n" + 
	    			"new=" + splt.toString() + "\n" + 
	    			"old=" + old.toString());
	    	// ::TODO
	    	IllegalStateException exc = new IllegalStateException("DEBUG");
	    	exc.printStackTrace();
	    	replaceTransactionSplit(old, (GnuCashTransactionSplitImpl) splt);
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
    public void replaceTransactionSplit(
    		final GnuCashTransactionSplit splt,
    		final GnuCashTransactionSplitImpl impl) {
    	if ( ! mySplits.remove(splt) ) {
    		throw new IllegalArgumentException("Could not remove split from local list");
    	}

    	mySplits.add(impl);
    }

    // ----------------------------

    @Override
    public List<GCshAccountLot> getLots() {
    	if (myLots == null) {
    	    initLots();
    	}
    	
    	return myLots;
    }

    private void initLots() {
    	if ( jwsdpPeer.getActLots() == null )
    		return;
    	
		List<GncAccount.ActLots.GncLot> jwsdpLots = jwsdpPeer.getActLots().getGncLot();

	myLots = new ArrayList<GCshAccountLot>();
	for ( GncAccount.ActLots.GncLot elt : jwsdpLots ) {
		myLots.add(createLot(elt));
	}
    }

    /**
     * Create a new split for a split found in the jaxb-data.
     *
     * @param jwsdpSplt the jaxb-data
     * @return the new split-instance
     */
    protected GCshAccountLotImpl createLot(
	    final GncAccount.ActLots.GncLot jwsdpLot) {
	return new GCshAccountLotImpl(jwsdpLot, this);
    }

    /**
     * @see GnuCashAccount#addLot(GCshAccountLot)
     */
    public void addLot(final GCshAccountLot lot) {
    	GCshAccountLot old = getLotByID(lot.getID());
	if ( old != null ) {
	    // There already is a lot with that ID
	    if ( ! old.equals(lot) ) {
	    	System.err.println("addLot: New Account Lot object with same ID, needs to be replaced: " + 
	    			lot.getID() + " [" + lot.getClass().getName() + "] and " + 
	    			old.getID() + " [" + old.getClass().getName() + "]\n" + 
	    			"new = " + lot.toString() + "\n" + 
	    			"old = " + old.toString());
	    	LOGGER.error("addLot: New Account Lot object with same ID, needs to be replaced: " + 
	    			lot.getID() + " [" + lot.getClass().getName() + "] and " + 
	    			old.getID() + " [" + old.getClass().getName() + "]\n" + 
	    			"new = " + lot.toString() + "\n" + 
	    			"old = " + old.toString());
	    	IllegalStateException exc = new IllegalStateException("DEBUG");
	    	exc.printStackTrace();
	    	replaceLot(old, lot);
	    }
	} else {
	    // There is no split with that ID yet
	    myLots.add(lot);
	}
    }

    /**
     * For internal use only.
     *
     * @param lot
     */
    public void replaceLot(
    		final GCshAccountLot lot,
    		final GCshAccountLot impl) {
    	if ( ! myLots.remove(lot) ) {
    		throw new IllegalArgumentException("Could not remove lot from local list");
    	}

    	myLots.add(impl);
    }

    // ---------------------------------------------------------------
    
    public GCshAcctReconInfo getReconcileInfo() {
    	if ( jwsdpPeer.getActSlots() == null )
    		return null;
    	
    	for ( Slot slt : jwsdpPeer.getActSlots().getSlot() ) {
    		if ( slt.getSlotKey().equals(Const.SLOT_KEY_ACCT_RECONCILE_INFO) ) {
    			GCshAcctReconInfo rcnInf = new GCshAcctReconInfoImpl(slt, this);
    			return rcnInf;
    		}
    	}
    	
    	return null;
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

    // https://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram-in-java
    @Override
    public void printTree(StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);
        buffer.append(toString());
        buffer.append('\n');
        
        for ( Iterator<GnuCashAccount> it = getChildren().iterator(); it.hasNext(); ) {
        	GnuCashAccount next = it.next();
            if (it.hasNext()) {
                next.printTree(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
            } else {
                next.printTree(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
            }
        }
    }
}
