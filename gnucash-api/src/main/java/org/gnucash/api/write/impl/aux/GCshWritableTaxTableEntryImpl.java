package org.gnucash.api.write.impl.aux;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.impl.aux.GCshTaxTableEntryImpl;
import org.gnucash.api.write.aux.GCshWritableTaxTableEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshTaxTableEntryImpl to allow read-write access instead of
 * read-only access.
 */
public class GCshWritableTaxTableEntryImpl extends GCshTaxTableEntryImpl 
                                           implements GCshWritableTaxTableEntry 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritableTaxTableEntryImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritableTaxTableEntryImpl(
	    final GncV2.GncBook.GncGncTaxTable.TaxtableEntries.GncGncTaxTableEntry jwsdpPeer,
	    final GnucashFile gncFile) {
	super(jwsdpPeer, gncFile);
    }

    // ---------------------------------------------------------------

    @Override
    public void setType(final Type type) {
	setTypeStr(type.toString());
    }

    @Override
    public void setTypeStr(final String typeStr) {
	if ( typeStr == null ) {
	    throw new IllegalArgumentException("null type given!");
	}
	
	if ( typeStr.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty type given!");
	}

	getJwsdpPeer().setTteType(typeStr);
    }

    /**
     * @param account The account to set.
     * @link #myAccount
     */
    @Override
    public void setAccountID(final GCshID acctId) {
	if ( acctId == null ) {
	    throw new IllegalArgumentException("null account-ID given!");
	}
	
	if ( ! acctId.isSet() ) {
	    throw new IllegalArgumentException("unset account-ID given!");
	}

	myAccountID = acctId;
	
	getJwsdpPeer().getTteAcct().setType(Const.XML_DATA_TYPE_GUID);
	getJwsdpPeer().getTteAcct().setValue(acctId.toString());
    }

    /**
     * @param acct The account to set.
     * @link #myAccount
     */
    @Override
    public void setAccount(final GnucashAccount acct) {
	if ( acct == null ) {
	    throw new IllegalArgumentException("null account given!");
	}

	myAccount = acct;

	setAccountID(acct.getID());
    }

    @Override
    public void setAmount(final FixedPointNumber amt) {
	getJwsdpPeer().setTteAmount(amt.toGnucashString());
    }

}
