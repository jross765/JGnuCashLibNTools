package org.gnucash.api.read.impl.aux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.aux.GCshAcctReconInfo;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshAcctReconInfoImpl extends GnuCashObjectImpl 
								   implements GCshAcctReconInfo 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshAcctReconInfoImpl.class);

    // ----------------------------
    
    private static final int ERROR_VAL = -999;
    
    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final Slot jwsdpPeer;

    /**
     * the account this lot belongs to.
     */
    private final GnuCashAccountImpl myAccount;

    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param acct  the acc ount this lot belongs to
     */
    @SuppressWarnings("exports")
    public GCshAcctReconInfoImpl(
	    final Slot peer,
	    final GnuCashAccountImpl acct) {
		super(acct.getGnuCashFile());

		jwsdpPeer = peer;
		myAccount = acct;
    }

    // ---------------------------------------------------------------

	/**
     * @return the JWSDP-object we are wrapping.
     */
    @Override
	@SuppressWarnings("exports")
    public Slot getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    @Override
    public boolean areChildrenIncluded() {
    	if ( ! jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
    		throw new IllegalStateException("JWSDP peer's value is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
    	}
    	
    	List<Object> subSlotList = jwsdpPeer.getSlotValue().getContent();
    	boolean found = false;
    	for ( Object obj : subSlotList ) {
    		try {
    		Slot subSlot = (Slot) obj;
    		// System.err.println("slot-key: " + subSlot.getSlotKey());
    		if ( subSlot.getSlotKey().equals(Const.SLOT_KEY_ACCT_INCLUDE_CHILDREN) ) {
    			found = true;
    			if ( ! subSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_INTEGER) ) {
    				throw new IllegalStateException("Slot type expected to be " + Const.XML_DATA_TYPE_INTEGER);
    			}
    			String inclStr = subSlot.getSlotValue().getContent().toString();
    			inclStr = inclStr.substring(1, inclStr.length() - 1); // cut of brackets
        		Integer inclInt = Integer.parseInt(inclStr);
    			return ( inclInt.intValue() == 1 );
    		}
    		} catch ( Exception exc ) {
    			// System.err.println("areChildrenIncluded: Cannot cast or whatever!");
    			int dummy = 0;
    		}
    	}
    	
    	if ( ! found ) {
    		LOGGER.error("Could not find sub-slot of type '" + Const.SLOT_KEY_ACCT_INCLUDE_CHILDREN + "'");
    	}
    	
    	return false;
    }

    @Override
    public LocalDateTime getLastDateTime() {
    	if ( ! jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
    		throw new IllegalStateException("JWSDP peer's value is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
    	}
    	
    	List<Object> subSlotList = jwsdpPeer.getSlotValue().getContent();
    	boolean found = false;
    	for ( Object obj : subSlotList ) {
    		try {
    		Slot subSlot = (Slot) obj;
    		// System.err.println("slot-key: " + subSlot.getSlotKey());
    		if ( subSlot.getSlotKey().equals(Const.SLOT_KEY_ACCT_LAST_DATE) ) {
    			found = true;
    			if ( ! subSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_INTEGER) ) { // sic, integer
    				throw new IllegalStateException("Slot type expected to be " + Const.XML_DATA_TYPE_INTEGER);
    			}
    			String dateTimeStr = subSlot.getSlotValue().getContent().toString();
    			dateTimeStr = dateTimeStr.substring(1, dateTimeStr.length() - 1); // cut of brackets
        		Long dateTimeLong = Long.parseLong(dateTimeStr);
    			Instant dateTimeInst = Instant.ofEpochSecond(dateTimeLong.longValue());
    			return LocalDateTime.ofInstant(dateTimeInst, ZoneId.systemDefault());
    		}
    		} catch ( Exception exc ) {
    			// System.err.println("getLastDate: Cannot cast or whatever!");
    			int dummy = 0;
    		}
    	}
    	
    	if ( ! found ) {
    		LOGGER.error("Could not find sub-slot of type '" + Const.SLOT_KEY_ACCT_LAST_DATE + "'");
    	}
    	
    	return null;
    }

    @Override
    public LastInterval getLastInterval() {
    	if ( ! jwsdpPeer.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
    		throw new IllegalStateException("JWSDP peer's value is not of type '" + Const.XML_DATA_TYPE_FRAME + "'");
    	}
    	
    	List<Object> subSlotList = jwsdpPeer.getSlotValue().getContent();
    	boolean found = false;
    	for ( Object obj : subSlotList ) {
    		try {
    		Slot subSlot = (Slot) obj;
    		if ( subSlot.getSlotKey().equals(Const.SLOT_KEY_ACCT_LAST_INTERVAL) ) {
    			found = true;
    			if ( ! subSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
    				throw new IllegalStateException("Slot type expected to be " + Const.XML_DATA_TYPE_INTEGER);
    			}
    	    	List<Object> subSubSlotList = subSlot.getSlotValue().getContent();
    			LastInterval newIntvl = new LastInterval();
    	    	// -----
    	    	boolean found2 = false;
    	    	for ( Object obj2 : subSubSlotList ) {
    	    		try {
    	    		Slot subSubSlot = (Slot) obj2;
    	    		if ( subSubSlot.getSlotKey().equals(Const.SLOT_KEY_ACCT_LAST_INTERVAL_DAYS) ) {
    	    			found2 = true;
    	    			newIntvl.days = getLastInterval_helper(subSubSlot);
    	    		}
    	    		} catch ( Exception exc2 ) {
    	    			// System.err.println("Cahnnoht! (1)");
    	    			int dummy = 0;
    	    		}
    	    	}
    	    	if ( ! found2 ) {
    	    		LOGGER.error("Could not find sub-sub-slot of type '" + Const.SLOT_KEY_ACCT_LAST_INTERVAL_DAYS + "'");
	    			newIntvl.days = ERROR_VAL;
    	    	}
    	    	// -----
    	    	found2 = false;
    	    	for ( Object obj2 : subSubSlotList ) {
    	    		try {
    	    		Slot subSubSlot = (Slot) obj2;
    	    		if ( subSubSlot.getSlotKey().equals(Const.SLOT_KEY_ACCT_LAST_INTERVAL_MONTHS) ) {
    	    			found2 = true;
    	    			newIntvl.months = getLastInterval_helper(subSubSlot);
    	    		}
    	    		} catch ( Exception exc2 ) {
    	    			// System.err.println("Cahnnoht! (2)");
    	    			int dummy = 0;
    	    		}
    	    	}
    	    	if ( ! found2 ) {
    	    		LOGGER.error("Could not find sub-sub-slot of type '" + Const.SLOT_KEY_ACCT_LAST_INTERVAL_MONTHS + "'");
	    			newIntvl.months = ERROR_VAL;
    	    	}
    	    	// -----
    	    	return newIntvl;
    		}
    		} catch ( Exception exc ) {
    			// System.err.println("getLastInterval: Cannot cast or whatever!");
    			int dummy = 0;
    		}
    	}
    	
    	if ( ! found ) {
    		LOGGER.error("Could not find sub-slot of type '" + Const.SLOT_KEY_ACCT_LAST_INTERVAL + "'");
    	}
    	
    	return null;
    }
    
    private int getLastInterval_helper(Slot subSubSlot) {
    	if ( ! subSubSlot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_INTEGER) ) {
    		throw new IllegalStateException("Slot type expected to be " + Const.XML_DATA_TYPE_INTEGER);
    	}
    	
    	String valStr = subSubSlot.getSlotValue().getContent().toString();
    	valStr = valStr.substring(1, valStr.length() - 1); // cut of brackets
    	// System.err.println("xyz '" + valStr + "'");
    	Integer valInt = Integer.parseInt(valStr);
    	return valInt.intValue();
    }
    
    // ----------------------------

	@Override
	public GCshAcctID getAccountID() {
    	return myAccount.getID();
	}

	@Override
	public GnuCashAccount getAccount() {
    	return myAccount;
	}

	// ---------------------------------------------------------------

    @Override
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("GCshAcctReconInfoImpl [");

    	buffer.append("account-id=");
    	buffer.append(getAccount().getID());

    	buffer.append(", include-children=");
    	buffer.append(areChildrenIncluded());

    	buffer.append(", last-date='");
    	buffer.append(getLastDateTime() + "'");

    	buffer.append(", last-interval=");
    	buffer.append(getLastInterval().toString());

    	buffer.append("]");
    	return buffer.toString();
    }

}
