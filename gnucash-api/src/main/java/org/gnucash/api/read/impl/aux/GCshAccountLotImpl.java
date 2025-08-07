package org.gnucash.api.read.impl.aux;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshAccountLotImpl extends GnuCashObjectImpl 
								implements GCshAccountLot 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshAccountLotImpl.class);

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
    public GCshAccountLotImpl(
	    final GncAccount.ActLots.GncLot peer,
	    final GnuCashAccountImpl acct) {
	super(acct.getGnuCashFile());

	jwsdpPeer = peer;
	myAccount = acct;
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
    public GCshLotID getID() {
	return new GCshLotID( jwsdpPeer.getLotId().getValue() );
    }

    @Override
    public String getTitle() {
    	if ( jwsdpPeer.getLotSlots() == null ) {
    		throw new IllegalStateException("No slots in account-lot");
    	}
    		
		return getUserDefinedAttribute("title");
    }

	@Override
	public String getNotes() {
    	if ( jwsdpPeer.getLotSlots() == null ) {
    		throw new IllegalStateException("No slots in account-lot");
    	}
    		
		return getUserDefinedAttribute("notes");
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

	// ----------------------------

	@Override
	public List<GnuCashTransactionSplit> getTransactionSplits() {
		return myAccount.getGnuCashFile().getTransactionSplitsByAccountLotID(getID());
	}

	@Override
	public List<GnuCashTransactionSplit> getSplitsBefore(final LocalDate date) {
		ArrayList<GnuCashTransactionSplit> result = new ArrayList<GnuCashTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			if ( splt.getTransaction().getDatePosted().toLocalDate().isBefore(date) ||
				 splt.getTransaction().getDatePosted().toLocalDate().isEqual(date) ) {
				result.add(splt);
			}
		}

		return result;
	}

	@Override
	public List<GnuCashTransactionSplit> getSplitsAfterBefore(final LocalDate fromDate, final LocalDate toDate) {
		ArrayList<GnuCashTransactionSplit> result = new ArrayList<GnuCashTransactionSplit>();
		
		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			if ( ( splt.getTransaction().getDatePosted().toLocalDate().isAfter(fromDate) ||
				   splt.getTransaction().getDatePosted().toLocalDate().isEqual(fromDate) ) 
				 &&
				 ( splt.getTransaction().getDatePosted().toLocalDate().isBefore(toDate) ||
				   splt.getTransaction().getDatePosted().toLocalDate().isEqual(toDate) ) ) {
				result.add(splt);
			}
		}

		return result;
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

	// -----------------------------------------------------------
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeCore(jwsdpPeer.getLotSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
				.getUserDefinedAttributeKeysCore(jwsdpPeer.getLotSlots());
	}

	// ---------------------------------------------------------------

    @Override
    public String toString() {
    	StringBuffer buffer = new StringBuffer();
    	buffer.append("GCshAccountLotImpl [");

    	buffer.append("id=");
    	buffer.append(getID());

    	buffer.append(", account-id=");
    	buffer.append(getAccount().getID());

//    		buffer.append(", account=");
//    		GnuCashAccount account = getAccount();
//    		buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");

    	buffer.append(", title='");
    	buffer.append(getTitle() + "'");

    	buffer.append(", notes='");
    	buffer.append(getNotes() + "'");

//    	buffer.append(", account-description='");
//    	buffer.append(getAccount().getDescription() + "'");

    	buffer.append("]");
    	return buffer.toString();
    }

}
