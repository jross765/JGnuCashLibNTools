package org.gnucash.api.read.impl.aux;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshTaxTableEntryImpl implements GCshTaxTableEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshTaxTableEntryImpl.class);

    /**
     * the jwsdp-object we are wrapping.
     */
    private GncV2.GncBook.GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry jwsdpPeer;

    /**
     * the file we belong to.
     */
    private final GnucashFile myFile;
    
    // ---------------------------------------------------------------
    
    /**
     * initialised lazy.
     */
    private GCshID myAccountID;
    /**
     * initialised lazy.
     */
    private GnucashAccount myAccount;

    // ---------------------------------------------------------------

    /**
     * @param element the jwsdp-object we are wrapping
     * @param file    the file we belong to
     */
    @SuppressWarnings("exports")
    public GCshTaxTableEntryImpl(
	    final GncV2.GncBook.GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry element,
	    final GnucashFile file) {
	super();
	
	this.jwsdpPeer = element;
	this.myFile = file;
    }

    // ---------------------------------------------------------------

    /**
     * @return the jwsdp-object we are wrapping
     */
    protected GncV2.GncBook.GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * usually ${@link GCshTaxTableEntry#TYPE_PERCENT}.
     * 
     * @link #getAmount()
     */
    public Type getType() {
	return Type.valueOf( getJwsdpPeer().getTteType() );
    }

    /**
     * @return the amount the tax is
     * @link #getType()
     */
    public FixedPointNumber getAmount() {
	return new FixedPointNumber(getJwsdpPeer().getTteAmount());
    }

    /**
     * @return Returns the accountID.
     * @link #myAccountID
     */
    public GCshID getAccountID() {
	if (myAccountID == null) {
	    myAccountID = new GCshID(getJwsdpPeer().getTteAcct().getValue());
	}

	return myAccountID;
    }

    /**
     * @return Returns the account.
     * @link #myAccount
     */
    public GnucashAccount getAccount() {
	if (myAccount == null) {
	    myAccount = myFile.getAccountByID(getAccountID());
	}

	return myAccount;
    }

    /**
     * @param account The account to set.
     * @link #myAccount
     */
    public void setAccountID(final GCshID acctId) {
	if (acctId == null) {
	    throw new IllegalArgumentException("null 'accountId' given!");
	}

	myAccountID = acctId;
	
	getJwsdpPeer().getTteAcct().setType(Const.XML_DATA_TYPE_GUID);
	getJwsdpPeer().getTteAcct().setValue(acctId.toString());
    }

    /**
     * @param acct The account to set.
     * @link #myAccount
     */
    public void setAccount(final GnucashAccount acct) {
	if (acct == null) {
	    throw new IllegalArgumentException("null 'account' given!");
	}

	myAccount = acct;

	setAccountID(acct.getId());
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	return "GCshTaxTableEntryImpl [myAccountID=" + getAccountID() + 
		                      ", myAccount=" + getAccount() + 
		                         ", amount=" + getAmount() + 
		                           ", type=" + getType() + "]";
    }

}