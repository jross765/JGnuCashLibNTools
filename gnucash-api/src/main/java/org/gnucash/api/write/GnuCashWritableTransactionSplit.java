package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.aux.GCshLotID;

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
	 * Remove this split from the system.
	 *  
	 */
	void remove();

	/**
	 * Does not convert the quantity to another
	 * currency if the new account has another
	 * one then the old!
	 * @param acctID the new account to give this money to/take it from.
	 * 
	 * @see #getAccountID()
	 * @see #setAccount(GnuCashAccount)
	 */
	void setAccountID(GCshAcctID acctID);

	/**
	 * Does not convert the quantity to another
	 * currency if the new account has another
	 * one then the old!
	 * @param account the new account to give this
	 *        money to/take it from.
	 *        
	 * @see #getAccount()
	 * @see #setAccountID(GCshAcctID)
	 */
	void setAccount(GnuCashAccount account);

	/**
	 * For invoice payment transactions: One of the splits
	 * contains a reference to the account lot which in turn
	 * references the invoice.
	 * 
	 * Similarly For transactions on stock accounts: When buying and
	 * selling securities, it is important to know which securities
	 * exactly have been sold (typically FIFO logic) in order to be able
	 * to correctly prepare a tax report. This is achieved by account lots
	 * which in turn contain the relevant transaction splits. Technically
	 * speaking, the according splits point to the account lot. With this
	 * function, you can have a transaction split point to a specific 
	 * stock account's lot.
	 * 
	 * @param lotID the ID of the account lot this transaction split
     * shall reference to..
     * 
     * @see #getLotID()
     * @see #unsetLotID()
	 */
	void setLotID(GCshLotID lotID);

	/**
	 * @see #setLotID(GCshLotID)
	 */
	void unsetLotID();

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setQuantity(FixedPointNumber)}.
	 * @param n the new quantity (in the currency of the account)
	 * 
	 * @see #getQuantity()
	 * @see #setQuantity(String)
	 */
	void setQuantity(FixedPointNumber n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setQuantity(FixedPointNumber)}.
	 * @param n the new quantity (in the currency of the account)
	 * 
	 * @see #getQuantity()
	 * @see #setQuantity(FixedPointNumber)
	 */
	void setQuantity(String n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * @param n the new value (in the currency of the transaction)
	 * 
	 * @see #getValue()
	 * @see #setValue(FixedPointNumber)
	 */
	void setValue(FixedPointNumber n);

	/**
	 * If the currencies of transaction and account match, this also does
	 * ${@link #setValue(FixedPointNumber)}.
	 * @param n the new value (in the currency of the transaction)
	 * 
	 * @see #getValue()
	 * @see #setValue(FixedPointNumber)
	 */
	void setValue(String n);

	/**
	 * Set the description-text.
	 * @param desc the new description
	 * 
	 * @see #getDescription()
	 */
	void setDescription(String desc);

	/**
	 * Set the type of association this split has with
	 * an invoice's lot.
	 * @param action null, or one of the ACTION_xyz values defined
	 * @throws IllegalTransactionSplitActionException
	 * 
	 * @see #getAction()
	 * @see #setActionStr(String)
	 */
	void setAction(Action action);

	/**
	 * 
	 * @param action
	 * 
	 * @throws IllegalTransactionSplitActionException
	 * 
	 * @see #getActionStr()
	 * @see #setAction(org.gnucash.api.read.GnuCashTransactionSplit.Action)
	 */
	void setActionStr(String action) throws IllegalTransactionSplitActionException;

}

