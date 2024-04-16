package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.MappingException;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashTransactionSplit that uses JWSDSP.
 */
public class GnuCashTransactionSplitImpl extends GnuCashObjectImpl 
                                         implements GnuCashTransactionSplit 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashTransactionSplitImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncTransaction.TrnSplits.TrnSplit jwsdpPeer;

    /**
     * the transaction this split belongs to.
     */
    private final GnuCashTransaction myTransaction;

    // ---------------------------------------------------------------

    /**
     * @param peer the JWSDP-object we are facading.
     * @param trx  the transaction this split belongs to
     */
    @SuppressWarnings("exports")
    public GnuCashTransactionSplitImpl(
	    final GncTransaction.TrnSplits.TrnSplit peer,
	    final GnuCashTransaction trx,
	    final boolean addSpltToAcct,
	    final boolean addSpltToInvc) {
	super(trx.getGnuCashFile());

	jwsdpPeer = peer;
	myTransaction = trx;

	if ( addSpltToAcct ) {
	    GnuCashAccount acct = getAccount();
	    if (acct == null) {
	    	LOGGER.error("No such Account id='" + getAccountID() + "' for Transactions-Split with id '" + getID()
	    		+ "' description '" + getDescription() + "' in transaction with id '" + getTransaction().getID()
	    		+ "' description '" + getTransaction().getDescription() + "'");
	    } else {
	    	acct.addTransactionSplit(this);
	    }
	}

	if ( addSpltToInvc ) {
	    GCshID lot = getLotID();
	    if ( lot != null ) {
		for ( GnuCashGenerInvoice invc : getTransaction().getGnuCashFile().getGenerInvoices() ) {
		    GCshID lotID = invc.getLotID();
		    if ( lotID != null && 
			 lotID.equals(lot) ) {
			// Check if it's a payment transaction.
			// If so, add it to the invoice's list of payment transactions.
			if ( getAction() == Action.PAYMENT ) {
			    invc.addPayingTransaction(this);
			}
		    }
		} // for invc
	    } // lot
	} // addSpltToInvc
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GncTransaction.TrnSplits.TrnSplit getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashTransactionSplit#getID()
     */
    public GCshID getID() {
	return new GCshID( jwsdpPeer.getSplitId().getValue() );
    }

    /**
     * @return the lot-id that identifies this transaction to belong to an invoice
     *         with that lot-id.
     */
    public GCshID getLotID() {
	if (getJwsdpPeer().getSplitLot() == null) {
	    return null;
	}

	return new GCshID( getJwsdpPeer().getSplitLot().getValue() );

    }

    /**
     *  
     * @see GnuCashTransactionSplit#getActionStr()
     */
    @Override
    public Action getAction() {
    	try {
    		return Action.valueOff( getActionStr() );
    	} catch (Exception e) {
    		throw new MappingException("Could not map string '" + getActionStr() + "' to Action enum");
    	}
    }

    /**
     * @see GnuCashTransactionSplit#getAction()
     */
    public String getActionStr() {
    	if (getJwsdpPeer().getSplitAction() == null) {
    		return "";
    	}

    	return getJwsdpPeer().getSplitAction();
    }

    /**
     * @see GnuCashTransactionSplit#getAccountID()
     */
    public GCshID getAccountID() {
    	assert jwsdpPeer.getSplitAccount().getType().equals(Const.XML_DATA_TYPE_GUID);
    	String acctID = jwsdpPeer.getSplitAccount().getValue();
    	assert acctID != null;
    	return new GCshID(acctID);
    }

    /**
     * @see GnuCashTransactionSplit#getAccount()
     */
    public GnuCashAccount getAccount() {
    	return myTransaction.getGnuCashFile().getAccountByID(getAccountID());
    }

    /**
     * @see GnuCashTransactionSplit#getAccountID()
     */
    public GCshID getTransactionID() {
    	return myTransaction.getID();
    }

    /**
     * @see GnuCashTransactionSplit#getTransaction()
     */
    public GnuCashTransaction getTransaction() {
    	return myTransaction;
    }

    /**
     * @see GnuCashTransactionSplit#getValue()
     */
    public FixedPointNumber getValue() {
    	return new FixedPointNumber(jwsdpPeer.getSplitValue());
    }

    /**
     * @return The currencyFormat for the quantity to use when no locale is given.
     */
    protected NumberFormat getQuantityCurrencyFormat() {
    	return ((GnuCashAccountImpl) getAccount()).getCurrencyFormat();
    }

    /**
     * @return the currency-format of the transaction
     */
    public NumberFormat getValueCurrencyFormat() {
    	return ((GnuCashTransactionImpl) getTransaction()).getCurrencyFormat();
    }

    /**
     * @see GnuCashTransactionSplit#getValueFormatted()
     */
    public String getValueFormatted() {
    	NumberFormat nf = getValueCurrencyFormat();
    	if ( getTransaction().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
    		// redundant, but symmetry:
    		nf.setCurrency(new GCshCurrID(getTransaction().getCmdtyCurrID()).getCurrency());
    		return nf.format(getValue());
    	} else {
    		return nf.format(getValue()) + " " + getTransaction().getCmdtyCurrID().toString(); 
    	}
    }

    /**
     * @see GnuCashTransactionSplit#getValueFormatted(java.util.Locale)
     */
    public String getValueFormatted(final Locale lcl) {

	NumberFormat nf = NumberFormat.getInstance(lcl);
	if ( getTransaction().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    // redundant, but symmetry:
	    nf.setCurrency(new GCshCurrID(getTransaction().getCmdtyCurrID()).getCurrency());
	    return nf.format(getValue());
	}
	else {
	    return nf.format(getValue()) + " " + getTransaction().getCmdtyCurrID().toString(); 
	}
    }

    /**
     * @see GnuCashTransactionSplit#getValueFormattedForHTML()
     */
    public String getValueFormattedForHTML() {
	return getValueFormatted().replaceFirst("€", "&euro;");
    }

    /**
     * @see GnuCashTransactionSplit#getValueFormattedForHTML(java.util.Locale)
     */
    public String getValueFormattedForHTML(final Locale lcl) {
	return getValueFormatted(lcl).replaceFirst("€", "&euro;");
    }

    /**
     * @see GnuCashTransactionSplit#getAccountBalance()
     */
    public FixedPointNumber getAccountBalance() {
	return getAccount().getBalance(this);
    }

    /**
     * @see GnuCashTransactionSplit#getAccountBalanceFormatted()
     */
    public String getAccountBalanceFormatted() {
	return ((GnuCashAccountImpl) getAccount()).getCurrencyFormat().format(getAccountBalance());
    }

    /**
     * @see GnuCashTransactionSplit#getAccountBalanceFormatted(java.util.Locale)
     */
    public String getAccountBalanceFormatted(final Locale lcl) {
	return getAccount().getBalanceFormatted(lcl);
    }

    /**
     * @see GnuCashTransactionSplit#getQuantity()
     */
    public FixedPointNumber getQuantity() {
	return new FixedPointNumber(jwsdpPeer.getSplitQuantity());
    }

    /**
     * The value is in the currency of the account!
     */
    public String getQuantityFormatted() {
	NumberFormat nf = getQuantityCurrencyFormat();
	if ( getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    nf.setCurrency(new GCshCurrID(getAccount().getCmdtyCurrID()).getCurrency());
	    return nf.format(getQuantity());
	}
	else {
	    return nf.format(getQuantity()) + " " + getAccount().getCmdtyCurrID().toString(); 
	}
    }

    /**
     * The value is in the currency of the account!
     *
     * @param lcl the locale to format to
     * @return the formatted number
     */
    public String getQuantityFormatted(final Locale lcl) {
	NumberFormat nf = NumberFormat.getCurrencyInstance(lcl);
	if ( getAccount().getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
	    nf.setCurrency(new GCshCurrID(getAccount().getCmdtyCurrID()).getCurrency());
	    return nf.format(getQuantity());
	}
	else {
	    return nf.format(getQuantity()) + " " + getAccount().getCmdtyCurrID().toString(); 
	}
    }

    /**
     * The value is in the currency of the account!
     */
    public String getQuantityFormattedForHTML() {
	return getQuantityFormatted().replaceFirst("€", "&euro;");
    }

    /**
     * The value is in the currency of the account!
     */
    public String getQuantityFormattedForHTML(final Locale lcl) {
	return getQuantityFormatted(lcl).replaceFirst("€", "&euro;");
    }

    /**
     * {@inheritDoc}
     *
     * @see GnuCashTransactionSplit#getDescription()
     */
    public String getDescription() {
	if (jwsdpPeer.getSplitMemo() == null) {
	    return "";
	}
	return jwsdpPeer.getSplitMemo();
    }

    // ---------------------------------------------------------------
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getSplitSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getSplitSlots());
	}

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashTransactionSplitImpl [");

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

//	buffer.append(", account=");
//	GnuCashAccount account = getAccount();
//	buffer.append(account == null ? "null" : "'" + account.getQualifiedName() + "'");

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

    public int compareTo(final GnuCashTransactionSplit otherSplt) {
	try {
	    GnuCashTransaction otherTrans = otherSplt.getTransaction();
	    int c = otherTrans.compareTo(getTransaction());
	    if (c != 0) {
		return c;
	    }

	    c = otherSplt.getID().toString().compareTo(getID().toString());
	    if (c != 0) {
		return c;
	    }

	    if (otherSplt != this) {
		LOGGER.error("compareTo: Duplicate transaction-split-id!! " + 
			otherSplt.getID() + "[" + otherSplt.getClass().getName() + "] and " + 
			getID() + "[" + getClass().getName() + "]\n" + 
			"split0=" + otherSplt.toString() + "\n" + 
			"split1=" + toString());
		IllegalStateException exc = new IllegalStateException("DEBUG");
		exc.printStackTrace();
	    }

	    return 0;

	} catch (Exception e) {
	    e.printStackTrace();
	    return 0;
	}
    }

}
