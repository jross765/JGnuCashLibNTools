package org.gnucash.api.write.impl.aux;

import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.aux.GCshAccountLotImpl;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.aux.GCshWritableAccountLot;
import org.gnucash.api.write.impl.GnuCashWritableAccountImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnuCashTransactionSplitImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableAccountLotImpl extends GCshAccountLotImpl 
                                        implements GCshWritableAccountLot
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableAccountLotImpl.class);

    // ---------------------------------------------------------------

    // Our helper to implement the GnuCashWritableObject-interface.
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * @param jwsdpPeer   the JWSDP-object we are facading.
     * @param acct the account we belong to
     */
    @SuppressWarnings("exports")
    public GCshWritableAccountLotImpl(
    		final GncAccount.ActLots.GncLot jwsdpPeer,
    		final GnuCashWritableAccountImpl acct) {
    	super(jwsdpPeer, acct);
    }

    /**
     * create a new split and and add it to the given transaction.
     *
     * @param acct  transaction the transaction we will belong to
     */
    public GCshWritableAccountLotImpl(
    		final GnuCashWritableAccountImpl acct) {
	super(createAccountLot_int(acct, GCshID.getNew()), 
		  acct);

	acct.addLot(this);
    }

    public GCshWritableAccountLotImpl(final GCshAccountLotImpl lot) {
    	super(lot.getJwsdpPeer(), (GnuCashAccountImpl) lot.getAccount());
    }

    // ---------------------------------------------------------------

    /**
	 * Creates a new Transaction and add's it to the given GnuCash file Don't modify
	 * the ID of the new transaction!
	 */
	protected static GncAccount.ActLots.GncLot createAccountLot_int(
	    final GnuCashWritableAccountImpl acct, 
	    final GCshID newID) {
	if ( acct == null ) {
	    throw new IllegalArgumentException("argument <acct> is null");
	}
	
	if ( newID == null ) {
		throw new IllegalArgumentException("argument <newID> is null");
	}
	
	if ( ! newID.isSet() ) {
		throw new IllegalArgumentException("argument <newID> is not set");
	}
	
	// This is needed because account.addLot() later
	// must have an already built List of lots --
	// if not, it will create the list from the JAXB-Data.
	// Thus 2 instances of this GCshWritableAccountLotImpl
	// will exist: One created in getLots() from this JAXB-Data
	// the other is this object.
	acct.getLots();
	
	GnuCashWritableFileImpl gnucashFileImpl = acct.getWritableGnuCashFile();
	ObjectFactory factory = gnucashFileImpl.getObjectFactory();
	
	GncAccount.ActLots.GncLot jwsdpLot = gnucashFileImpl.createGncAccountLotType();
	
	{
	    GncAccount.ActLots.GncLot.LotId id = factory.createGncAccountActLotsGncLotLotId();
	    id.setType(Const.XML_DATA_TYPE_GUID);
	    id.setValue(newID.toString());
	    jwsdpLot.setLotId(id);
	}
	
	LOGGER.debug("createAccountLot_int: Created new account lot (core): " + jwsdpLot.getLotId().getValue());
	
	return jwsdpLot;
	}

    // ---------------------------------------------------------------

    /**
     * @see GCshAccountLotImpl#getAccount()
     */
    @Override
    public GnuCashWritableAccount getAccount() {
	return (GnuCashWritableAccount) super.getAccount();
    }

    /**
     * remove this lot from its account.
     */
    public void remove() {
	getAccount().remove(this);
    }

	// ---------------------------------------------------------------

	@Override
	public void setTitle(String title) {
		setUserDefinedAttribute("title", title);
	}

	@Override
	public void setNotes(String notes) {
		setUserDefinedAttribute("notes", notes);
	}

	// ---------------------------------------------------------------

	@Override
	public void clearTransactionSplits() {
		for ( GnuCashWritableTransactionSplit splt : getAccount().getWritableTransactionSplits() ) {
			if ( splt.getLotID() != null ) {
				if ( splt.getAccountID().equals(getAccountID()) && /* this should be the case anyway */
					 splt.getLotID().equals(getID()) ) {           /* this is the actual condition */
					splt.unsetLotID();
				}
			}
		}
	}

	@Override
	public void addTransactionSplit(GnuCashWritableTransactionSplit splt) {
		if ( ! splt.getAccountID().equals(getAccountID()) ) {
			throw new IllegalArgumentException("split " + splt.getID() + " does not belong to account " + getAccountID());
		}
		
		splt.setLotID(getID());
	}
	
	@Override
	public void setTransactionSplits(List<GnuCashWritableTransactionSplit> splitList) {
		clearTransactionSplits();
		for ( GnuCashWritableTransactionSplit splt : splitList ) {
			addTransactionSplit(splt);
		}
	}

    // --------------------- support for propertyChangeListeners ---------------

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

    // ---------------------------------------------------------------

	public void setUserDefinedAttribute(final String name, final String value) {
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getLotSlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getLotSlots());
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GCshWritableAccountLotImpl [");

	buffer.append("id=");
	buffer.append(getID());

	buffer.append(", account-id=");
	buffer.append(getAccount().getID());

//		buffer.append(", account=");
//		GnuCashAccount account = getAccount();
//		buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");

	buffer.append(", title='");
	buffer.append(getTitle() + "'");

	buffer.append(", notes='");
	buffer.append(getNotes() + "'");

//	buffer.append(", account-description='");
//	buffer.append(getAccount().getDescription() + "'");

	buffer.append("]");
	return buffer.toString();
    }

}
