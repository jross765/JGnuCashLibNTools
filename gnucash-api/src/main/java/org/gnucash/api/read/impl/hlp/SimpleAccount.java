package org.gnucash.api.read.impl.hlp;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This is a base-class that helps implementing the GnucashAccount
 * interface with its extensive number of convenience-methods.<br/>
 */
public abstract class SimpleAccount implements GnucashAccount {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccount.class);

	// ---------------------------------------------------------------

	private final GnucashFile myFile;

	// ----------------------------

	private static NumberFormat currencyFormat = null;

	private volatile PropertyChangeSupport myPtyChg = null;

	// ---------------------------------------------------------------

	public SimpleAccount(final GnucashFile myFile) {
		super();
		this.myFile = myFile;
	}

	// ---------------------------------------------------------------

	/*
	 * The returned list is sorted by the natural order of the Transaction-Splits.
	 */
	public List<GnucashTransaction> getTransactions() {
		List<? extends GnucashTransactionSplit> splits = getTransactionSplits();
		List<GnucashTransaction> retval = new ArrayList<GnucashTransaction>(splits.size());

		for ( Object element : splits ) {
			GnucashTransactionSplit split = (GnucashTransactionSplit) element;
			retval.add(split.getTransaction());
		}

		return retval;
	}

	public GnucashFile getGnucashFile() {
		return myFile;
	}

	public boolean isChildAccountRecursive(final GnucashAccount account) {

		if ( this == account ) {
			return true;
		}

		for ( GnucashAccount child : getChildren() ) {
			if ( this == child ) {
				return true;
			}
			if ( child.isChildAccountRecursive(account) ) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return getQualifiedName();
	}

	/**
	 * Get name including the name of the parent accounts.
	 */
	public String getQualifiedName() {
		GnucashAccount acc = getParentAccount();

		if ( acc == null || 
			 acc.getID() == getID() ) {
			if ( getParentAccountID() == null || 
				 getParentAccountID().equals("") ) {
				return getName();
			}

			return "UNKNOWN" + SEPARATOR + getName();
		}

		return acc.getQualifiedName() + SEPARATOR + getName();
	}

	public GnucashAccount getParentAccount() {
		GCshID parentID = getParentAccountID();
		if ( parentID == null ) {
			return null;
		}

		return getGnucashFile().getAccountByID(parentID);
	}

	public Collection<GnucashAccount> getSubAccounts() {
		return getChildren();
	}

	@Override
	public FixedPointNumber getBalance() {
		return getBalance(LocalDate.now());
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date) {
		return getBalance(date, (Collection<GnucashTransactionSplit>) null);
	}

	/**
	 * The currency will be the one of this account.
	 */
	@Override
	public FixedPointNumber getBalance(final LocalDate date, Collection<GnucashTransactionSplit> after) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( GnucashTransactionSplit splt : getTransactionSplits() ) {
			if ( date != null && 
				 after != null ) {
				if ( splt.getTransaction().getDatePosted().isAfter(ChronoZonedDateTime.from(date.atStartOfDay())) ) {
					after.add(splt);
					continue;
				}
			}
	
			// the currency of the quantity is the one of the account
			balance.add(splt.getQuantity());
		}
	
		return balance;
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID)
			throws InvalidCmdtyCurrTypeException {
		FixedPointNumber retval = getBalance(date);

		if ( retval == null ) {
			LOGGER.error("getBalance: Error creating balance!");
			return null;
		}

		// is conversion needed?
		if ( getCmdtyCurrID().equals(cmdtyCurrID) ) {
			return retval;
		}
	
		ComplexPriceTable priceTab = getGnucashFile().getCurrencyTable();
	
		if ( priceTab == null ) {
			LOGGER.error("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}
	
		if ( ! priceTab.convertToBaseCurrency(retval, cmdtyCurrID) ) {
			Collection<String> currList = getGnucashFile().getCurrencyTable()
					.getCurrencies(getCmdtyCurrID().getNameSpace());
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ getCmdtyCurrID().toString() + "' to the base-currency!" + " \n(we know "
					+ getGnucashFile().getCurrencyTable().getNameSpaces().size() + " currency-namespaces and "
					+ (currList == null ? "no" : "" + currList.size()) + " currencies in our namespace)");
			return null;
		}
	
		if ( ! priceTab.convertFromBaseCurrency(retval, cmdtyCurrID) ) {
			LOGGER.error("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ cmdtyCurrID.toString() + "'!");
			return null;
		}
	
		return retval;
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final Currency curr)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {

		FixedPointNumber retval = getBalance(date);

		if ( retval == null ) {
			LOGGER.warn("getBalance: Error creating balance!");
			return null;
		}

		if ( curr == null ||
			 retval.equals(new FixedPointNumber()) ) {
			return retval;
		}

		// is conversion needed?
		if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			if ( getCmdtyCurrID().getCode().equals(curr.getCurrencyCode()) ) {
				return retval;
			}
		}

		ComplexPriceTable priceTab = getGnucashFile().getCurrencyTable();

		if ( priceTab == null ) {
			LOGGER.warn("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}

		if ( ! priceTab.convertToBaseCurrency(retval, getCmdtyCurrID()) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from our currency '"
					+ getCmdtyCurrID().toString() + "' to the base-currency!");
			return null;
		}

		if ( ! priceTab.convertFromBaseCurrency(retval, new GCshCurrID(curr)) ) {
			LOGGER.warn("getBalance: Cannot transfer " + "from base-currenty to given currency '"
					+ curr + "'!");
			return null;
		}

		return retval;
	}

	@Override
	public FixedPointNumber getBalance(final GnucashTransactionSplit lastIncludesSplit) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( GnucashTransactionSplit split : getTransactionSplits() ) {
			balance.add(split.getQuantity());
	
			if ( split == lastIncludesSplit ) {
				break;
			}
	
		}
	
		return balance;
	}

	public String getBalanceFormatted() throws InvalidCmdtyCurrTypeException {
		return getCurrencyFormat().format(getBalance());
	}

	public String getBalanceFormatted(final Locale lcl) throws InvalidCmdtyCurrTypeException {
	
		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(getCurrency());
		return cf.format(getBalance());
	}

	public FixedPointNumber getBalanceRecursive() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		return getBalanceRecursive(LocalDate.now());
	}

	public FixedPointNumber getBalanceRecursive(final LocalDate date)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		return getBalanceRecursive(date, getCmdtyCurrID());
	}

	public FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID)
				throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	
			// BEGIN OLD IMPL
//		    FixedPointNumber retval = getBalance(date, cmdtyCurrID);
//	
//		    if (retval == null) {
//			retval = new FixedPointNumber();
//		    }
//	
//		    for ( GnucashAccount child : getChildren() ) {
//			retval.add(child.getBalanceRecursive(date, cmdtyCurrID));
//		    }
//	
//		    return retval;
			// END OLD IMPL
	
			if ( cmdtyCurrID.getType() == GCshCmdtyCurrID.Type.CURRENCY )
				return getBalanceRecursive(date, new GCshCurrID(cmdtyCurrID.getCode()).getCurrency());
			else
				return getBalance(date, cmdtyCurrID); // CAUTION: This assumes that under a stock account,
													  // there are no children (which sounds sensible,
													  // but there might be special cases)
		}

	public FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {

		FixedPointNumber retval = getBalance(date, curr);

		if ( retval == null ) {
			retval = new FixedPointNumber();
		}

		for ( GnucashAccount child : getChildren() ) {
			retval.add(child.getBalanceRecursive(date, curr));
		}

		return retval;
	}

	@Override
	public String getBalanceRecursiveFormatted()
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive());
	}

	@Override
	public String getBalanceRecursiveFormatted(final LocalDate date)
			throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
		return getCurrencyFormat().format(getBalanceRecursive(date));
	}

	@Override
	public GnucashTransactionSplit getLastSplitBeforeRecursive(final LocalDate date) {

		GnucashTransactionSplit lastSplit = null;

		for ( GnucashTransactionSplit split : getTransactionSplits() ) {
			if ( date == null || 
				 split.getTransaction().getDatePosted()
				 	.isBefore(ChronoZonedDateTime.from(date.atStartOfDay())) ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		for ( Iterator<GnucashAccount> iter = getSubAccounts().iterator(); iter.hasNext(); ) {
			GnucashAccount account = (GnucashAccount) iter.next();
			GnucashTransactionSplit split = account.getLastSplitBeforeRecursive(date);
			if ( split != null && 
				 split.getTransaction() != null ) {
				if ( lastSplit == null ||
					 split.getTransaction().getDatePosted()
						.isAfter(lastSplit.getTransaction().getDatePosted()) ) {
					lastSplit = split;
				}
			}
		}

		return lastSplit;
	}

	@Override
	public boolean hasTransactions() {
		return this.getTransactionSplits().size() > 0;
	}

	@Override
	public boolean hasTransactionsRecursive() {
		if ( this.hasTransactions() ) {
			return true;
		}

		for ( GnucashAccount child : getChildren() ) {
			if ( child.hasTransactionsRecursive() ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return null if we are no currency but e.g. a fund
	 * @throws InvalidCmdtyCurrTypeException
	 */
	public Currency getCurrency() throws InvalidCmdtyCurrTypeException {
		if ( getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			return null;
		}

		String gcshCurrID = getCmdtyCurrID().getCode();
		return Currency.getInstance(gcshCurrID);
	}

	public NumberFormat getCurrencyFormat() throws InvalidCmdtyCurrTypeException {
		if ( currencyFormat == null ) {
			currencyFormat = NumberFormat.getCurrencyInstance();
		}

		// the currency may have changed
		if ( getCmdtyCurrID().getType() == GCshCmdtyCurrID.Type.CURRENCY ) {
			Currency currency = getCurrency();
			currencyFormat.setCurrency(currency);
		} else {
			currencyFormat = NumberFormat.getNumberInstance();
		}

		return currencyFormat;
	}

	public GnucashTransactionSplit getTransactionSplitByID(final GCshID id) {
		if ( id == null ) {
			throw new IllegalArgumentException("null id given!");
		} 

		if ( ! id.isSet() ) {
			throw new IllegalArgumentException("ID not set");
		}

		for ( GnucashTransactionSplit split : getTransactionSplits() ) {
			if ( id.equals(split.getID()) ) {
				return split;
			}

		}

		return null;
	}

	/*
	 * This is an extension to ${@link #compareNamesTo(Object)} that makes sure that
	 * NEVER 2 accounts with different IDs compare to 0. Compares our name to
	 * o.toString() .<br/> 
	 * If both starts with some digits the resulting ${@link
	 * java.lang.Integer} are compared.<br/> 
	 * If one starts with a number and the other does not, the one starting with a
	 * number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
	 * ${@link java.lang.String} is done.
	 */
	@Override
	public int compareTo(final GnucashAccount otherAcc) {

		int i = compareNamesTo(otherAcc);
		if ( i != 0 ) {
			return i;
		}

		GnucashAccount other = otherAcc;
		i = other.getID().toString().compareTo(getID().toString());
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherAcc.hashCode());

	}

	/*
	 * Compares our name to o.toString() .<br/>
	 * If both starts with some digits the resulting ${@link java.lang.Integer} are
	 * compared.<br/>
	 * If one starts with a number and the other does not, the one starting with a
	 * number is "bigger"<br/>
	 * else and if both integers are equals a normals comparison of the
	 */
	public int compareNamesTo(final Object o) throws ClassCastException {

		// usually compare the qualified name
		String other = o.toString();
		String me = getQualifiedName();

		// if we have the same parent,
		// compare the unqualified name.
		// This enshures that the exception
		// for numbers is used within our parent-
		// account too and not just in the top-
		// level accounts
		if ( o instanceof GnucashAccount && 
				((GnucashAccount) o).getParentAccountID() != null && 
				getParentAccountID() != null && 
				((GnucashAccount) o).getParentAccountID().toString()
						.equalsIgnoreCase(getParentAccountID().toString()) ) {
			other = ((GnucashAccount) o).getName();
			me = getName();
		}

		// compare

		Long i0 = startsWithNumber(other);
		Long i1 = startsWithNumber(me);
		if ( i0 == null && i1 != null ) {
			return 1;
		} else if ( i1 == null && i0 != null ) {
			return -1;
		} else if ( i0 == null ) {
			return me.compareTo(other);
		} else if ( i1 == null ) {
			return me.compareTo(other);
		} else if ( i1.equals(i0) ) {
			return me.compareTo(other);
		}

		return i1.compareTo(i0);
	}

	/*
	 * Helper used in ${@link #compareTo(Object)} to compare names starting with a
	 * number.
	 */
	private Long startsWithNumber(final String s) {
		int digitCount = 0;
		for ( int i = 0; i < s.length() && Character.isDigit(s.charAt(i)); i++ ) {
			digitCount++;
		}
		if ( digitCount == 0 ) {
			return null;
		}
		return Long.valueOf(s.substring(0, digitCount));
	}

	// ------------------------ support for propertyChangeListeners

	protected PropertyChangeSupport getPropertyChangeSupport() {
		return myPtyChg;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(listener);
	}

	/**
	 * Add a PropertyChangeListener for a specific property. The listener will be
	 * invoked only when a call on firePropertyChange names that specific property.
	 * 
	 * @param propertyName 
	 * @param listener 
	 */
	public final void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPtyChg == null ) {
			myPtyChg = new PropertyChangeSupport(this);
		}
		myPtyChg.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove a PropertyChangeListener for a specific property.
	 * 
	 * @param propertyName 
	 * @param listener 
	 */
	public final void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(propertyName, listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed
	 */
	public synchronized void removePropertyChangeListener(final PropertyChangeListener listener) {
		if ( myPtyChg != null ) {
			myPtyChg.removePropertyChangeListener(listener);
		}
	}

}
