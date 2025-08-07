package org.gnucash.api.write.impl;

import java.text.ParseException;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashTransactionSplitImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableTransactionSplitImpl extends GnuCashTransactionSplitImpl 
                                                 implements GnuCashWritableTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableTransactionSplitImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * @param jwsdpPeer   the JWSDP-object we are facading.
     * @param trx the transaction we belong to
     * @param addSpltToAcct 
     * @param addSpltToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashWritableTransactionSplitImpl(
    		final GncTransaction.TrnSplits.TrnSplit jwsdpPeer,
    		final GnuCashWritableTransaction trx, 
    		final boolean addSpltToAcct, 
    		final boolean addSpltToInvc) {
    	super(jwsdpPeer, trx, 
    		  addSpltToAcct, addSpltToInvc);
    }

    /**
     * create a new split and and add it to the given transaction.
     *
     * @param trx  transaction the transaction we will belong to
     * @param acct the account we take money (or other things) from or give it to
     */
    public GnuCashWritableTransactionSplitImpl(
    		final GnuCashWritableTransactionImpl trx, 
    		final GnuCashAccount acct) {
	super(createTransactionSplit_int(trx, acct,
									 new GCshSpltID( GCshID.getNew()) ), 
		  trx, 
		  true, true);

	// ::TODO ::CHECK
	// this is a workaround.
	// if super does account.addSplit(this) it adds an instance on
	// GnuCashTransactionSplitImpl that is "!=
	// (GnuCashWritableTransactionSplitImpl)this";
	// thus we would get warnings about duplicate split-ids and can no longer
	// compare splits by instance.
	// if(account!=null)
	// ((GnuCashAccountImpl)account).replaceTransactionSplit(account.getTransactionSplitByID(getID()),
	// GnuCashWritableTransactionSplitImpl.this);

	trx.addSplit(this);
    }

    public GnuCashWritableTransactionSplitImpl(final GnuCashTransactionSplit split) {
    	super(split.getJwsdpPeer(), split.getTransaction(), 
    		  true, true);
    }

    public GnuCashWritableTransactionSplitImpl(
    		final GnuCashTransactionSplit split,
    		final boolean addSpltToAcct,
    		final boolean addSpltToInvc) {
    	super(split.getJwsdpPeer(), split.getTransaction(), 
    		  addSpltToAcct, addSpltToInvc);
    }

    // ---------------------------------------------------------------

    /**
	 * Creates a new Transaction and add's it to the given GnuCash file Don't modify
	 * the ID of the new transaction!
	 */
	protected static GncTransaction.TrnSplits.TrnSplit createTransactionSplit_int(
	    final GnuCashWritableTransactionImpl trx, 
	    final GnuCashAccount acct, 
	    final GCshSpltID newID) {
	if ( trx == null ) {
	    throw new IllegalArgumentException("argument <trx> is null");
	}
	
	if ( acct == null ) {
	    throw new IllegalArgumentException("argument <acct> is null");
	}
	
	if ( newID == null ) {
		throw new IllegalArgumentException("argument <newID> is null");
	}
	
	if ( ! newID.isSet() ) {
		throw new IllegalArgumentException("argument <newID> is null");
	}
	
	// This is needed because transaction.addSplit() later
	// must have an already built List of splits --
	// if not, it will create the list from the JAXB-Data.
	// Thus, 2 instances of this GnuCashWritableTransactionSplitImpl
	// will exist: One created in getSplits() from this JAXB-Data
	// the other is this object.
	trx.getSplits();
	
	GnuCashWritableFileImpl gnucashFileImpl = trx.getWritableFile();
	ObjectFactory factory = gnucashFileImpl.getObjectFactory();
	
	GncTransaction.TrnSplits.TrnSplit jwsdpSplt = gnucashFileImpl.createGncTransactionSplitType();
	
	{
	    GncTransaction.TrnSplits.TrnSplit.SplitId id = factory.createGncTransactionTrnSplitsTrnSplitSplitId();
	    id.setType(Const.XML_DATA_TYPE_GUID);
	    id.setValue(newID.toString());
	    jwsdpSplt.setSplitId(id);
	}
	
	jwsdpSplt.setSplitReconciledState(GnuCashTransactionSplit.ReconStatus.NREC.getCode());
	
	jwsdpSplt.setSplitQuantity("0/100");
	jwsdpSplt.setSplitValue("0/100");
	{
	    GncTransaction.TrnSplits.TrnSplit.SplitAccount splitaccount = factory.createGncTransactionTrnSplitsTrnSplitSplitAccount();
	    splitaccount.setType(Const.XML_DATA_TYPE_GUID);
	    splitaccount.setValue(acct.getID().toString());
	    jwsdpSplt.setSplitAccount(splitaccount);
	}
	
	LOGGER.debug("createTransactionSplit_int: Created new transaction split (core): "
		+ jwsdpSplt.getSplitId().getValue());
	
	return jwsdpSplt;
	}

    // ---------------------------------------------------------------

    /**
     * @see GnuCashTransactionSplitImpl#getTransaction()
     */
    @Override
    public GnuCashWritableTransaction getTransaction() {
	return (GnuCashWritableTransaction) super.getTransaction();
    }

    /**
     * remove this split from its transaction.
     */
    public void remove() {
	getTransaction().remove(this);
    }

    /**
     * @see GnuCashWritableTransactionSplit#setAccount(GnuCashAccount)
     */
    public void setAccountID(final GCshAcctID acctID) {
	setAccount(getTransaction().getGnuCashFile().getAccountByID(acctID));
    }

    /**
     * @see GnuCashWritableTransactionSplit#setAccount(GnuCashAccount)
     */
    public void setAccount(final GnuCashAccount acct) {
	if (acct == null) {
	    throw new NullPointerException("argument <acct> is null");
	}
	String old = (getJwsdpPeer().getSplitAccount() == null ? null : getJwsdpPeer().getSplitAccount().getValue());
	getJwsdpPeer().getSplitAccount().setType(Const.XML_DATA_TYPE_GUID);
	getJwsdpPeer().getSplitAccount().setValue(acct.getID().toString());
	((GnuCashWritableFile) getGnuCashFile()).setModified(true);

	if (old == null || !old.equals(acct.getID())) {
	    if (helper.getPropertyChangeSupport() != null) {
	    	helper.getPropertyChangeSupport().firePropertyChange("accountID", old, acct.getID());
	    }
	}

    }

    /**
     * @return true if the currency of transaction and account match
     */
    private boolean isCurrencyMatching() {
	GnuCashAccount acct = getAccount();
	if (acct == null) {
	    return false;
	}
	GnuCashWritableTransaction transaction = getTransaction();
	if (transaction == null) {
	    return false;
	}
	GCshCmdtyCurrID acctCmdtyCurrID = acct.getCmdtyCurrID();
	if (acctCmdtyCurrID == null) {
	    return false;
	}

	// Important: Don't forget to cast the IDs to their most basic type
	return ((GCshCmdtyCurrID) acctCmdtyCurrID).equals((GCshCmdtyCurrID) transaction.getCmdtyCurrID());
    }

    /**
     * @see GnuCashWritableTransactionSplit#setQuantity(FixedPointNumber)
     */
    @Override
    public void setQuantity(final FixedPointNumber quant) {
	if (quant == null) {
	    throw new NullPointerException("argument <quant> is null");
	}

	String old = getJwsdpPeer().getSplitQuantity();
	getJwsdpPeer().setSplitQuantity(quant.toGnuCashString());
	((GnuCashWritableFile) getGnuCashFile()).setModified(true);
	if ( isCurrencyMatching() ) {
	    String oldQuant = getJwsdpPeer().getSplitQuantity();
	    getJwsdpPeer().setSplitQuantity(quant.toGnuCashString());
	    if (old == null || !old.equals(quant.toGnuCashString())) {
		if (helper.getPropertyChangeSupport() != null) {
		    helper.getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldQuant), quant);
		}
	    }
	}

	if ( old == null || !old.equals(quant.toGnuCashString()) ) {
	    if (helper.getPropertyChangeSupport() != null) {
	    	helper.getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(old), quant);
	    }
	}
    }

    /**
	 * @see GnuCashWritableTransactionSplit#setQuantity(FixedPointNumber)
	 */
    @Override
	public void setQuantity(final String quantStr) {
		if ( quantStr == null ) {
			throw new IllegalArgumentException("argument <quantStr> is null");
		}
		
		if ( quantStr.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <quantStr> is empty");
		}
	
		try {
			this.setQuantity(new FixedPointNumber(quantStr.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		} catch (NumberFormatException e) {
			try {
				Number parsed = this.getQuantityCurrencyFormat().parse(quantStr);
				this.setQuantity(new FixedPointNumber(parsed.toString()));
			} catch (NumberFormatException e1) {
				throw e;
			} catch (ParseException e1) {
				throw e;
			}
		}
	}

	/**
	 * @see GnuCashWritableTransactionSplit#setValue(FixedPointNumber)
	 */
	@Override
	public void setValue(final FixedPointNumber val) {
		if (val == null) {
			throw new IllegalArgumentException("argument <val> is null");
		}
		
		String old = getJwsdpPeer().getSplitValue();
		jwsdpPeer.setSplitValue(val.toGnuCashString());
		((GnuCashWritableFile) getGnuCashFile()).setModified(true);
	
		if ( isCurrencyMatching() ) {
			String oldValue = getJwsdpPeer().getSplitQuantity();
			getJwsdpPeer().setSplitQuantity(val.toGnuCashString());
			if ( old == null || !old.equals(val.toGnuCashString()) ) {
				if ( helper.getPropertyChangeSupport() != null ) {
					helper.getPropertyChangeSupport().firePropertyChange("quantity", new FixedPointNumber(oldValue), val);
				}
			}
		}
	
		if ( old == null || !old.equals(val.toGnuCashString()) ) {
			if ( helper.getPropertyChangeSupport() != null ) {
				helper.getPropertyChangeSupport().firePropertyChange("value", new FixedPointNumber(old), val);
			}
		}
	}

	/**
     * @see GnuCashWritableTransactionSplit#setValue(FixedPointNumber)
     */
	@Override
    public void setValue(final String valStr) {
		if ( valStr == null ) {
			throw new IllegalArgumentException("argument <valStr> is null");
		}
		
		if ( valStr.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <valStr> is empty");
		}

		try {
			this.setValue(new FixedPointNumber(valStr.toLowerCase().replaceAll("&euro;", "").replaceAll("&pound;", "")));
		} catch (NumberFormatException e) {
			try {
				Number parsed = this.getValueCurrencyFormat().parse(valStr);
				this.setValue(new FixedPointNumber(parsed.toString()));
			} catch (NumberFormatException e1) {
				throw e;
			} catch (ParseException e1) {
				throw e;
			} catch (InvalidCmdtyCurrIDException e1) {
				throw e;
			}
		}
    }

    /**
     * Set the description-text.
     *
     * @param descr the new description
     */
    public void setDescription(final String descr) {
	if (descr == null) {
	    throw new IllegalArgumentException("argument <descr> is null");
	}

	// Caution: empty string allowed here
//		if ( descr.trim().length() == 0 ) {
//		    throw new IllegalArgumentException("argument <descr> is empty");
//		}

	String old = getJwsdpPeer().getSplitMemo();
	getJwsdpPeer().setSplitMemo(descr);
	((GnuCashWritableFile) getGnuCashFile()).setModified(true);

	if (old == null || !old.equals(descr)) {
	    if (helper.getPropertyChangeSupport() != null) {
	    	helper.getPropertyChangeSupport().firePropertyChange("description", old, descr);
	    }
	}
    }

    /**
     * Set the type of association this split has with an invoice's lot.
     *
     * @param act null, or one of the defined ACTION_xyz values
     * @throws IllegalTransactionSplitActionException
     */
    public void setAction(final Action act) {
	setActionStr(act.getLocaleString());
    }

    public void setActionStr(final String actStr) throws IllegalTransactionSplitActionException {
	if (actStr == null) {
	    throw new IllegalArgumentException("argument <actStr> is null");
	}

	if (actStr.trim().length() == 0) {
	    throw new IllegalArgumentException("argument <actStr> is empty");
	}

	String old = getJwsdpPeer().getSplitAction();
	getJwsdpPeer().setSplitAction(actStr);
	((GnuCashWritableFile) getGnuCashFile()).setModified(true);

	if (old == null || !old.equals(actStr)) {
	    if (helper.getPropertyChangeSupport() != null) {
	    	helper.getPropertyChangeSupport().firePropertyChange("splitAction", old, actStr);
	    }
	}
    }

    public void setLotID(final GCshLotID lotID) {
	if ( lotID == null ) {
	    throw new IllegalArgumentException("argument <lotID> is null");
	}

	if ( ! lotID.isSet() ) {
	    throw new IllegalArgumentException("argument <lotID> is not set");
	}

	GnuCashWritableTransactionImpl trx = (GnuCashWritableTransactionImpl) getTransaction();
	GnuCashWritableFileImpl writingFile = trx.getWritableFile();
	ObjectFactory factory = writingFile.getObjectFactory();

	if (getJwsdpPeer().getSplitLot() == null) {
	    GncTransaction.TrnSplits.TrnSplit.SplitLot lot = factory.createGncTransactionTrnSplitsTrnSplitSplitLot();
	    getJwsdpPeer().setSplitLot(lot);
	}
	
	try {
		getJwsdpPeer().getSplitLot().setValue(lotID.get());
		getJwsdpPeer().getSplitLot().setType(Const.XML_DATA_TYPE_GUID);
	} catch (GCshIDNotSetException exc) {
	    throw new IllegalArgumentException("UUID not set"); // Compiler happy
	}

	// if we have a lot, and if we are a paying transaction, then check the slots
	// ::TODO ::CHECK
	// 09.10.2023: This code, in the current setting, generates wrong
	// output (a closing split slot tag without an opening one, and
	// we don't (always?) need a split slot anyway.
//		SlotsType slots = getJwsdpPeer().getSplitSlots();
//		if (slots == null) {
//			slots = factory.createSlotsType();
//			getJwsdpPeer().setSplitSlots(slots);
//		}
//		if (slots.getSlot() == null) {
//			Slot slot = factory.createSlot();
//			slot.setSlotKey("trans-txn-type");
//			SlotValue value = factory.createSlotValue();
//			value.setType(Const.XML_DATA_TYPE_STRING);
//			value.getContent().add(GnuCashTransaction.TYPE_PAYMENT);
//			slot.setSlotValue(value);
//			slots.getSlot().add(slot);
//		}

    }

    public void unsetLotID() {
	if ( getLotID() == null ) {
	    throw new IllegalStateException("no lot ID in this transaction split");
	}

	getJwsdpPeer().setSplitLot(null);
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

	/**
     * @param name 
	 * @param value 
     */
	public void setUserDefinedAttribute(final String name, final String value) {
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(getJwsdpPeer().getSplitSlots());
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableTransactionSplitImpl [");

	buffer.append("id=");
	buffer.append(getID());

	buffer.append(", action=");
	try {
	    buffer.append(getAction());
	} catch (Exception e) {
	    buffer.append("ERROR");
	}

	buffer.append(", transaction-id=");
	buffer.append(getTransaction().getID());

	buffer.append(", accountID=");
	buffer.append(getAccountID());

//		buffer.append(", account=");
//		GnuCashAccount account = getAccount();
//		buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");

	buffer.append(", description='");
	buffer.append(getDescription() + "'");

	buffer.append(", transaction-description='");
	buffer.append(getTransaction().getDescription() + "'");

	buffer.append(", value=");
	buffer.append(getValue());

	buffer.append(", quantity=");
	buffer.append(getQuantity());

	buffer.append("]");
	return buffer.toString();
    }

}
