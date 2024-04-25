package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Transaction-split that can be modified.<br/>
 * For propertyChange we support the properties "value", "quantity"
 * "description",  "splitAction" and "accountID".
 * 
 * @see GnuCashTransactionSplit
 */
public interface GnuCashWritableTransactionSplit extends GnuCashTransactionSplit, 
                                                         GnuCashWritableObject
{

	/**
	 * @return the transaction this is a split of.
	 */
	GnuCashWritableTransaction getTransaction();

	/**
	 * Remove this split from the sytem.
	 *  
	 */
	void remove();

	/**
	 * Does not convert the quantity to another
	 * currency if the new account has another
	 * one then the old!
	 * @param accountId the new account to give this
	 *        money to/take it from.
	 */
	void setAccountID(GCshID accountId);

	/**
	 * Does not convert the quantity to another
	 * currency if the new account has another
	 * one then the old!
	 * @param account the new account to give this
	 *        money to/take it from.
	 */
	void setAccount(GnuCashAccount account);

	/**
	 * For invoice payment transactions: One of the splits
	 * contains a reference to the account lot which in turn
	 * references the invoice.
	 * 
	 * @param accountId the new account to give this
	 *        money to/take it from.
	 */
	void setLotID(String accountId);


	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setQuantity(FixedPointNumber)}.
	 * @param n the new quantity (in the currency of the account)
	 */
	void setQuantity(String n);

	/**
	 * Same as ${@link #setQuantity(String)}.
	 * @param n the new quantity (in the currency of the account)
	 */
	void setQuantityFormattedForHTML(String n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setQuantity(FixedPointNumber)}.
	 * @param n the new quantity (in the currency of the account)
	 */
	void setQuantity(FixedPointNumber n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValue(String n);

	/**
	 * Same as ${@link #setValue(String)}.
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValueFormattedForHTML(String n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * @param n the new value (in the currency of the transaction)
	 */
	void setValue(FixedPointNumber n);

	/**
	 * Set the description-text.
	 * @param desc the new description
	 */
	void setDescription(String desc);

	/**
	 * Set the type of association this split has with
	 * an invoice's lot.
	 * @param action null, or one of the ACTION_xyz values defined
	 * @throws IllegalTransactionSplitActionException 
	 */
	void setAction(Action action);

	void setActionStr(String action) throws IllegalTransactionSplitActionException;

}

