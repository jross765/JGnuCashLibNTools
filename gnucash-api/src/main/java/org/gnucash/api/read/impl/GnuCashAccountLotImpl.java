package org.gnucash.api.read.impl;

import java.time.LocalDate;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashAccountLot;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashTransactionSplit.Action;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnuCashAccountLotImpl extends GnuCashObjectImpl 
								  implements GnuCashAccountLot 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashAccountLotImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncAccount.ActLots.GncLot jwsdpPeer;

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
    public GnuCashAccountLotImpl(
	    final GncAccount.ActLots.GncLot peer,
	    final GnuCashAccountImpl acct,
	    final boolean addLotToAcct) {
	super(acct.getGnuCashFile());

	jwsdpPeer = peer;
	myAccount = acct;

	if ( addLotToAcct ) {
	    if (acct == null) {
	    	LOGGER.error("No such Account id='" + getAccountID() + "' for Account-Lot with id '" + getID()
	    		+ "' title'" + getTitle() + "' in account with id '" + getAccount().getID() + "'");
	    } else {
	    	acct.addLot(this);
	    }
	}
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncAccount.ActLots.GncLot getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    @Override
    public GCshID getID() {
	return new GCshID( jwsdpPeer.getLotId().getValue() );
    }

    @Override
    public String getTitle() {
    	if ( jwsdpPeer.getLotSlots() == null ) {
    		throw new IllegalStateException("No slots in account-lot");
    	}
    		
		for ( Slot slot : jwsdpPeer.getLotSlots().getSlot() ) {
			if ( slot.getSlotKey().equals("title") ) { // ::MAGIC
				SlotValue val = slot.getSlotValue();
				if ( val.getType().equals(Const.XML_DATA_TYPE_STRING) ) {
					return val.getContent().toString();
				} else {
					return null;
				}
			}
		}
		
		return null;
    }

	@Override
	public String getNotes() {
    	if ( jwsdpPeer.getLotSlots() == null ) {
    		throw new IllegalStateException("No slots in account-lot");
    	}
    		
		for ( Slot slot : jwsdpPeer.getLotSlots().getSlot() ) {
			if ( slot.getSlotKey().equals("notes") ) { // ::MAGIC
				SlotValue val = slot.getSlotValue();
				if ( val.getType().equals(Const.XML_DATA_TYPE_STRING) ) {
					return val.getContent().toString();
				} else {
					return null;
				}
			}
		}
		
		return null;
	}
	
	// ----------------------------

	@Override
	public GCshID getAccountID() {
    	return myAccount.getID();
	}

	@Override
	public GnuCashAccount getAccount() {
    	return myAccount;
	}

	// ----------------------------

	@Override
	public List<GnuCashTransactionSplit> getTransactionSplits() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GnuCashTransactionSplit getTransactionSplitByID(GCshID id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GnuCashTransactionSplit getLastSplitBefore(LocalDate date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addTransactionSplit(GnuCashTransactionSplit split) {
		// TODO Auto-generated method stub
		
	}

	// ----------------------------

	@Override
	public boolean hasTransactions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<GnuCashTransaction> getTransactions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GnuCashTransaction> getTransactions(LocalDate fromDate, LocalDate toDate) {
		// TODO Auto-generated method stub
		return null;
	}

	// ---------------------------------------------------------------

    @Override
    public String toString() {
	
	String result = "GnuCashAccountLotImpl [";

    result += "id='" + getID().toString() + "'";
	result += ", title='" + getTitle() + "'"; 
	result += ", notes='" + getNotes() + "']";
	
	return result;
    }

}
