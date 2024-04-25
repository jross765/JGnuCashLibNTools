package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashAccount.Type;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;

public class FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	protected GnuCashFileImpl gcshFile;

	private Map<GCshID, GnuCashAccount> acctMap;

	// ---------------------------------------------------------------

	public FileAccountManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		acctMap = new HashMap<GCshID, GnuCashAccount>();

		for ( Object bookElement : pRootElement.getGncBook().getBookElements() ) {
			if ( !(bookElement instanceof GncAccount) ) {
				continue;
			}
			GncAccount jwsdpAcct = (GncAccount) bookElement;

			try {
				GnuCashAccount acct = createAccount(jwsdpAcct);
				acctMap.put(acct.getID(), acct);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Account-Entry with id=" + jwsdpAcct.getActId().getValue(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in account map: " + acctMap.size());
	}

	protected GnuCashAccountImpl createAccount(final GncAccount jwsdpAcct) {
		GnuCashAccountImpl acct = new GnuCashAccountImpl(jwsdpAcct, gcshFile);
		LOGGER.debug("Generated new account: " + acct.getID());
		return acct;
	}

	// ---------------------------------------------------------------

	public void addAccount(GnuCashAccount acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("null account given");
		}
		
		acctMap.put(acct.getID(), acct);
		LOGGER.debug("addAccount: Added account to cache: " + acct.getID());
	}

	public void removeAccount(GnuCashAccount acct) {
		if ( acct == null ) {
			throw new IllegalArgumentException("null account given");
		}
		
		acctMap.remove(acct.getID());
		LOGGER.debug("removeAccount: Removed account from cache: " + acct.getID());
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnuCashFile#getAccountByID(java.lang.String)
	 */
	public GnuCashAccount getAccountByID(final GCshID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("unset account ID given");
		}
		
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashAccount retval = acctMap.get(acctID);
		if ( retval == null ) {
			LOGGER.error("getAccountByID: No Account with ID '" + acctID + "'. " + "We know " + acctMap.size() + " accounts.");
		}
		
		return retval;
	}

	public List<GnuCashAccount> getAccountsByParentID(final GCshID acctID) {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("unset account ID given");
		}
		
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashAccount> retval = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : acctMap.values() ) {
			GCshID prntID = acct.getParentAccountID();
			if ( prntID == null ) {
				if ( acctID == null ) {
					retval.add((GnuCashAccount) acct);
				} else if ( ! acctID.isSet() ) {
					retval.add((GnuCashAccount) acct);
				}
			} else {
				if ( prntID.equals(acctID) ) {
					retval.add((GnuCashAccount) acct);
				}
			}
		}

		retval.sort(Comparator.naturalOrder()); 

		return retval;
	}

	public List<GnuCashAccount> getAccountsByName(final String name) {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		return getAccountsByName(name, true, true);
	}

	public List<GnuCashAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}
		
		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}
		
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : acctMap.values() ) {
			if ( relaxed ) {
				if ( qualif ) {
					if ( acct.getQualifiedName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().toLowerCase().contains(expr.trim().toLowerCase()) ) {
						result.add(acct);
					}
				}
			} else {
				if ( qualif ) {
					if ( acct.getQualifiedName().equals(expr) ) {
						result.add(acct);
					}
				} else {
					if ( acct.getName().equals(expr) ) {
						result.add(acct);
					}
				}
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	public GnuCashAccount getAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		List<GnuCashAccount> acctList = getAccountsByName(name, qualif, false);
		if ( acctList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( acctList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return acctList.get(0);
	}

	/*
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountByID first and only call this method if the returned account does
	 * not have the right name.
	 */
	public GnuCashAccount getAccountByNameEx(final String nameRegEx)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( nameRegEx == null ) {
			throw new IllegalArgumentException("null name/expression given");
		}
		
		if ( nameRegEx.trim().equals("") ) {
			throw new IllegalArgumentException("empty name/expression given");
		}
		
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashAccount foundAccount = getAccountByNameUniq(nameRegEx, true);
		if ( foundAccount != null ) {
			return foundAccount;
		}
		Pattern pattern = Pattern.compile(nameRegEx);

		for ( GnuCashAccount acct : acctMap.values() ) {
			Matcher matcher = pattern.matcher(acct.getName());
			if ( matcher.matches() ) {
				return acct;
			}
		}

		return null;
	}

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public GnuCashAccount getAccountByIDorName(final GCshID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("unset account ID given");
		}
		
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		GnuCashAccount retval = getAccountByID(acctID);
		if ( retval == null ) {
			retval = getAccountByNameUniq(name, true);
		}

		return retval;
	}

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public GnuCashAccount getAccountByIDorNameEx(final GCshID acctID, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		if ( acctID == null ) {
			throw new IllegalArgumentException("null account ID given");
		}
		
		if ( ! acctID.isSet() ) {
			throw new IllegalArgumentException("unset account ID given");
		}
		
		if ( name == null ) {
			throw new IllegalArgumentException("null name given");
		}
		
		if ( name.trim().equals("") ) {
			throw new IllegalArgumentException("empty name given");
		}
		
		GnuCashAccount retval = getAccountByID(acctID);
		if ( retval == null ) {
			retval = getAccountByNameEx(name);
		}

		return retval;
	}

	public List<GnuCashAccount> getAccountsByType(Type type) {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	public List<GnuCashAccount> getAccountsByTypeAndName(Type type, String expr, boolean qualif, boolean relaxed) {
		if ( expr == null ) {
			throw new IllegalArgumentException("null expression given");
		}
		
		if ( expr.trim().equals("") ) {
			throw new IllegalArgumentException("empty expression given");
		}
		
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GnuCashAccount acct : getAccountsByName(expr, qualif, relaxed) ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	// ---------------------------------------------------------------

	public Collection<GnuCashAccount> getAccounts() {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(acctMap.values());
	}

	public GnuCashAccount getRootAccount()  {
		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() == null && 
				 acct.getType() == GnuCashAccount.Type.ROOT ) {
				return acct;
			}
		}

		return null; // Compiler happy
	}

	public List<? extends GnuCashAccount> getParentlessAccounts() {
		try {
			List<GnuCashAccount> retval = new ArrayList<GnuCashAccount>();

			for ( GnuCashAccount acct : getAccounts() ) {
				if ( acct.getParentAccountID() == null ) {
					retval.add(acct);
				}

			}

			retval.sort(Comparator.naturalOrder()); 

			return retval;
		} catch (RuntimeException e) {
			LOGGER.error("getParentlessAccounts: Problem getting all root-account", e);
			throw e;
		} catch (Throwable e) {
			LOGGER.error("getParentlessAccounts: SERIOUS Problem getting all root-account", e);
			return new ArrayList<GnuCashAccount>();
		}
	}

	public List<GCshID> getTopAccountIDs() {
		List<GCshID> result = new ArrayList<GCshID>();

		GCshID rootAcctID = getRootAccount().getID();
		for ( GnuCashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() != null ) {
				if ( acct.getParentAccountID().equals(rootAcctID) &&
					 ( acct.getType() == GnuCashAccount.Type.ASSET ||
					   acct.getType() == GnuCashAccount.Type.LIABILITY ||
					   acct.getType() == GnuCashAccount.Type.INCOME ||
					   acct.getType() == GnuCashAccount.Type.EXPENSE ||
					   acct.getType() == GnuCashAccount.Type.EQUITY) ) {
					result.add(acct.getID());
				}
			}
		}

		return result;
	}

	public List<GnuCashAccount> getTopAccounts() {
		List<GnuCashAccount> result = new ArrayList<GnuCashAccount>();

		for ( GCshID acctID : getTopAccountIDs() ) {
			GnuCashAccount acct = getAccountByID(acctID);
			result.add(acct);
		}

		result.sort(Comparator.naturalOrder()); 

		return result;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesAccountMap() {
		return acctMap.size();
	}

}
