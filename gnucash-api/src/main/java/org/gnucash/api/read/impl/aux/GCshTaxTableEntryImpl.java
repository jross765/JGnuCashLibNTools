package org.gnucash.api.read.impl.aux;

import java.util.Objects;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshTaxTableEntryImpl implements GCshTaxTableEntry {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshTaxTableEntryImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * the jwsdp-object we are wrapping.
     */
    private GncV2.GncBook.GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry jwsdpPeer;

    /**
     * the file we belong to.
     */
    private final GnucashFile myFile;
    
    // ----------------------------
    
    protected GCshID myAccountID;
    protected GnucashAccount myAccount;

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
    @Override
    public Type getType() {
	return Type.valueOf( getJwsdpPeer().getTteType() );
    }

    /**
     * @return Returns the accountID.
     * @link #myAccountID
     */
    @Override
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
    @Override
    public GnucashAccount getAccount() {
	if (myAccount == null) {
	    myAccount = myFile.getAccountByID(getAccountID());
	}

	return myAccount;
    }

    /**
     * @return the amount the tax is
     * @link #getType()
     */
    @Override
    public FixedPointNumber getAmount() {
	return new FixedPointNumber(getJwsdpPeer().getTteAmount());
    }

    // ---------------------------------------------------------------
    
    @Override
    public int hashCode() {
	return Objects.hash(jwsdpPeer, myFile);
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!(obj instanceof GCshTaxTableEntryImpl)) {
	    return false;
	}
	GCshTaxTableEntryImpl other = (GCshTaxTableEntryImpl) obj;
	return Objects.equals(jwsdpPeer, other.jwsdpPeer) && Objects.equals(myFile, other.myFile);
    }

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	String result = "GCshTaxTableEntryImpl [";
	
	result += "type=" + getType(); 
	result += ", account-id=" + getAccountID(); 
	result += ", amount=" + getAmount(); 
		                          
	result += "]";
	
	return result;
    }

}
