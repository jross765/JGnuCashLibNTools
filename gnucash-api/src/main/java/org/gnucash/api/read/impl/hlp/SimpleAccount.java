package org.gnucash.api.read.impl.hlp;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/*
 * This is a base-class that helps implementing the GnuCashAccount
 * interface with its extensive number of convenience-methods.<br/>
 */
public abstract class SimpleAccount extends GnuCashObjectImpl 
									implements GnuCashAccount 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAccount.class);

	// ---------------------------------------------------------------

	private static NumberFormat currencyFormat = null;

	// ---------------------------------------------------------------

	public SimpleAccount(final GnuCashFile gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * The returned list is sorted by the natural order of the Transaction-Splits.
	 */
	@Override
	public List<GnuCashTransaction> getTransactions() {
		List<GnuCashTransaction> retval = new ArrayList<GnuCashTransaction>();

		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			retval.add(splt.getTransaction());
		}

		// retval.sort(Comparator.reverseOrder()); // not necessary 
		
		return retval;
	}

	@Override
	public List<GnuCashTransaction> getTransactions(final LocalDate fromDate, final LocalDate toDate) {
		List<GnuCashTransaction> retval = new ArrayList<GnuCashTransaction>();

		for ( GnuCashTransaction trx : getTransactions() ) {
			 if ( ( trx.getDatePosted().toLocalDate().isEqual( fromDate ) ||
				    trx.getDatePosted().toLocalDate().isAfter( fromDate ) ) &&
			      ( trx.getDatePosted().toLocalDate().isEqual( toDate ) ||
					trx.getDatePosted().toLocalDate().isBefore( toDate ) ) ) {
				 retval.add(trx);
			 }
		}

		// retval.sort(Comparator.reverseOrder()); // not necessary 
		
		return retval;
	}
	
	@Override
	public boolean isChildAccountRecursive(final GnuCashAccount account) {

		if ( this == account ) {
			return true;
		}

		for ( GnuCashAccount child : getChildren() ) {
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
	@Override
	public String getQualifiedName() {
		GnuCashAccount acc = getParentAccount();

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

	@Override
	public GnuCashAccount getParentAccount() {
		GCshID parentID = getParentAccountID();
		if ( parentID == null ) {
			return null;
		}

		return getGnuCashFile().getAccountByID(parentID);
	}

	@Override
	public boolean isRootAccount() {
		if ( getType() == Type.ROOT )
			return true;
		else
			return false;
	}

	@Override
	public FixedPointNumber getBalance() {
		return getBalance(LocalDate.now());
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date) {
		return getBalance(date, (List<GnuCashTransactionSplit>) null);
	}

	/**
	 * The currency will be the one of this account.
	 */
	@Override
	public FixedPointNumber getBalance(final LocalDate date, List<GnuCashTransactionSplit> after) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( GnuCashTransactionSplit splt : getTransactionSplits() ) {
			if ( date != null && 
				 after != null ) {
				if ( splt.getTransaction().getDatePosted().isAfter(ChronoZonedDateTime.from(date.atStartOfDay())) ) {
					after.add(splt);
					continue;
				}
			}
	
			// the currency of the quantity is the one of the account
			if ( splt.getAction() == GnuCashTransactionSplit.Action.SPLIT ) {
				balance.multiply(splt.getQuantity());
			} else {
				balance.add(splt.getQuantity());
			}
		}
	
		return balance;
	}

	@Override
	public FixedPointNumber getBalance(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID) {
		FixedPointNumber retval = getBalance(date);

		if ( retval == null ) {
			LOGGER.error("getBalance: Error creating balance!");
			return null;
		}

		// is conversion needed?
		if ( getCmdtyCurrID().equals(cmdtyCurrID) ) {
			return retval;
		}
	
		ComplexPriceTable priceTab = getGnuCashFile().getCurrencyTable();
	
		if ( priceTab == null ) {
			LOGGER.error("getBalance: Cannot transfer "
					+ "to given currency because we have no currency-table!");
			return null;
		}
	
		if ( ! priceTab.convertToBaseCurrency(retval, cmdtyCurrID) ) {
			Collection<String> currList = getGnuCashFile().getCurrencyTable()
					.getCurrencies(getCmdtyCurrID().getNameSpace());
			LOGGER.error("getBalance: Cannot transfer " + "from our currency '"
					+ getCmdtyCurrID().toString() + "' to the base-currency!" + " \n(we know "
					+ getGnuCashFile().getCurrencyTable().getNameSpaces().size() + " currency-namespaces and "
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
	public FixedPointNumber getBalance(final LocalDate date, final Currency curr) {

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

		ComplexPriceTable priceTab = getGnuCashFile().getCurrencyTable();

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
	public FixedPointNumber getBalance(final GnuCashTransactionSplit lastIncludesSplit) {
	
		FixedPointNumber balance = new FixedPointNumber();
	
		for ( GnuCashTransactionSplit split : getTransactionSplits() ) {
			balance.add(split.getQuantity());
	
			if ( split == lastIncludesSplit ) {
				break;
			}
	
		}
	
		return balance;
	}

	@Override
	public String getBalanceFormatted() {
		return getCurrencyFormat().format(getBalance());
	}

	@Override
	public String getBalanceFormatted(final Locale lcl) {
		NumberFormat cf = NumberFormat.getCurrencyInstance(lcl);
		cf.setCurrency(getCurrency());
		return cf.format(getBalance());
	}

	@Override
	public FixedPointNumber getBalanceRecursive() {
		return getBalanceRecursive(LocalDate.now());
	}

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date) {
		return getBalanceRecursive(date, getCmdtyCurrID());
	}

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final GCshCmdtyCurrID cmdtyCurrID) {
	
			// BEGIN OLD IMPL
//		    FixedPointNumber retval = getBalance(date, cmdtyCurrID);
//	
//		    if (retval == null) {
//			retval = new FixedPointNumber();
//		    }
//	
//		    for ( GnuCashAccount child : getChildren() ) {
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
//		}
	}

	@Override
	public FixedPointNumber getBalanceRecursive(final LocalDate date, final Currency curr) {

		FixedPointNumber retval = getBalance(date, curr);

		if ( retval == null ) {
			retval = new FixedPointNumber();
		}

		for ( GnuCashAccount child : getChildren() ) {
			retval.add(child.getBalanceRecursive(date, curr));
		}

		return retval;
	}

	@Override
	public String getBalanceRecursiveFormatted() {
		return getCurrencyFormat().format(getBalanceRecursive());
	}

	@Override
	public String getBalanceRecursiveFormatted(final LocalDate date) {
		return getCurrencyFormat().format(getBalanceRecursive(date));
	}

	@Override
	public GnuCashTransactionSplit getLastSplitBeforeRecursive(final LocalDate date) {

		GnuCashTransactionSplit lastSplit = null;

		for ( GnuCashTransactionSplit split : getTransactionSplits() ) {
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

		for ( GnuCashAccount account : getChildren() ) {
			GnuCashTransactionSplit split = account.getLastSplitBeforeRecursive(date);
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

		for ( GnuCashAccount child : getChildren() ) {
			if ( child.hasTransactionsRecursive() ) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return null if we are no currency but e.g. a fund
	 */
	public Currency getCurrency() {
		if ( getCmdtyCurrID().getType() != GCshCmdtyCurrID.Type.CURRENCY ) {
			return null;
		}

		String gcshCurrID = getCmdtyCurrID().getCode();
		return Currency.getInstance(gcshCurrID);
	}

	public NumberFormat getCurrencyFormat() {
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

	@Override
	public GnuCashTransactionSplit getTransactionSplitByID(final GCshID id) {
		if ( id == null ) {
			throw new IllegalArgumentException("null id given!");
		}

		if ( ! id.isSet() ) {
			throw new IllegalArgumentException("ID not set");
		}

		for ( GnuCashTransactionSplit split : getTransactionSplits() ) {
			if ( id.equals(split.getID()) ) {
				return split;
			}

		}

		return null;
	}

    // -----------------------------------------------------------------

	@Override
	public int compareTo(final GnuCashAccount otherAcct) {
		int i = compareToByQualifiedName(otherAcct);
		if ( i != 0 ) {
			return i;
		}

		i = compareToByID(otherAcct);
		if ( i != 0 ) {
			return i;
		}

		return ("" + hashCode()).compareTo("" + otherAcct.hashCode());
	}

	private int compareToByID(final GnuCashAccount otherAcct) {
		return getID().toString().compareTo(otherAcct.getID().toString());
	}

	private int compareToByCode(final GnuCashAccount otherAcct) {
		return getCode().toString().compareTo(otherAcct.getCode().toString());
	}

	private int compareToByName(final GnuCashAccount otherAcct) {
		return getName().compareTo(otherAcct.getName());
	}

	private int compareToByQualifiedName(final GnuCashAccount otherAcct) {
		return getQualifiedName().compareTo(otherAcct.getQualifiedName());
	}

    // -----------------------------------------------------------------

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

}
