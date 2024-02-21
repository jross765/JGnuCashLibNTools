package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashAccount.Type;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileAccountManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileAccountManager.class);

	// ---------------------------------------------------------------

	protected GnucashFileImpl gcshFile;

	private Map<GCshID, GnucashAccount> acctMap;

	// ---------------------------------------------------------------

	public FileAccountManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		acctMap = new HashMap<GCshID, GnucashAccount>();

		for ( Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext(); ) {
			Object bookElement = iter.next();
			if ( !(bookElement instanceof GncAccount) ) {
				continue;
			}
			GncAccount jwsdpAcct = (GncAccount) bookElement;

			try {
				GnucashAccount acct = createAccount(jwsdpAcct);
				acctMap.put(acct.getID(), acct);
			} catch (RuntimeException e) {
				LOGGER.error("init: [RuntimeException] Problem in " + getClass().getName() + ".init: "
						+ "ignoring illegal Account-Entry with id=" + jwsdpAcct.getActId().getValue(), e);
			}
		} // for

		LOGGER.debug("init: No. of entries in account map: " + acctMap.size());
	}

	protected GnucashAccountImpl createAccount(final GncAccount jwsdpAcct) {
		GnucashAccountImpl acct = new GnucashAccountImpl(jwsdpAcct, gcshFile);
		LOGGER.debug("Generated new account: " + acct.getID());
		return acct;
	}

	// ---------------------------------------------------------------

	public void addAccount(GnucashAccount acct) {
		acctMap.put(acct.getID(), acct);
		LOGGER.debug("addAccount: Added account to cache: " + acct.getID());
	}

	public void removeAccount(GnucashAccount acct) {
		acctMap.remove(acct.getID());
		LOGGER.debug("removeAccount: Removed account from cache: " + acct.getID());
	}

	// ---------------------------------------------------------------

	/**
	 * @see GnucashFile#getAccountByID(java.lang.String)
	 */
	public GnucashAccount getAccountByID(final GCshID acctID) {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashAccount retval = acctMap.get(acctID);
		if ( retval == null ) {
			LOGGER.error(
					"getAccountByID: No Account with ID '" + acctID + "'. " + "We know " + acctMap.size() + " accounts.");
		}
		return retval;
	}

	public Collection<GnucashAccount> getAccountsByParentID(final GCshID acctID) {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		SortedSet<GnucashAccount> retval = new TreeSet<GnucashAccount>();

		for ( GnucashAccount acct : acctMap.values() ) {
			GCshID prntID = acct.getParentAccountID();
			if ( prntID == null ) {
				if ( acctID == null ) {
					retval.add((GnucashAccount) acct);
				} else if ( ! acctID.isSet() ) {
					retval.add((GnucashAccount) acct);
				}
			} else {
				if ( prntID.equals(acctID) ) {
					retval.add((GnucashAccount) acct);
				}
			}
		}

		return retval;
	}

	public Collection<GnucashAccount> getAccountsByName(final String name) {
		return getAccountsByName(name, true, true);
	}

	public Collection<GnucashAccount> getAccountsByName(final String expr, boolean qualif, boolean relaxed) {

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		Collection<GnucashAccount> result = new ArrayList<GnucashAccount>();

		for ( GnucashAccount acct : acctMap.values() ) {
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

		return result;
	}

	public GnucashAccount getAccountByNameUniq(final String name, final boolean qualif)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		Collection<GnucashAccount> acctList = getAccountsByName(name, qualif, false);
		if ( acctList.size() == 0 )
			throw new NoEntryFoundException();
		else if ( acctList.size() > 1 )
			throw new TooManyEntriesFoundException();
		else
			return acctList.iterator().next();
	}

	/*
	 * warning: this function has to traverse all accounts. If it much faster to try
	 * getAccountByID first and only call this method if the returned account does
	 * not have the right name.
	 */
	public GnucashAccount getAccountByNameEx(final String nameRegEx)
			throws NoEntryFoundException, TooManyEntriesFoundException {

		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashAccount foundAccount = getAccountByNameUniq(nameRegEx, true);
		if ( foundAccount != null ) {
			return foundAccount;
		}
		Pattern pattern = Pattern.compile(nameRegEx);

		for ( GnucashAccount acct : acctMap.values() ) {
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
	public GnucashAccount getAccountByIDorName(final GCshID id, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		GnucashAccount retval = getAccountByID(id);
		if ( retval == null ) {
			retval = getAccountByNameUniq(name, true);
		}

		return retval;
	}

	/**
	 * First try to fetch the account by id, then fall back to traversing all
	 * accounts to get if by it's name.
	 */
	public GnucashAccount getAccountByIDorNameEx(final GCshID id, final String name)
			throws NoEntryFoundException, TooManyEntriesFoundException {
		GnucashAccount retval = getAccountByID(id);
		if ( retval == null ) {
			retval = getAccountByNameEx(name);
		}

		return retval;
	}

	public Collection<GnucashAccount> getAccountsByType(Type type)
			throws UnknownAccountTypeException {
		Collection<GnucashAccount> result = new ArrayList<GnucashAccount>();

		for ( GnucashAccount acct : getAccounts() ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		return result;
	}

	public Collection<GnucashAccount> getAccountsByTypeAndName(Type type, String expr, boolean qualif, boolean relaxed)
			throws UnknownAccountTypeException {
		Collection<GnucashAccount> result = new ArrayList<GnucashAccount>();

		for ( GnucashAccount acct : getAccountsByName(expr, qualif, relaxed) ) {
			if ( acct.getType() == type ) {
				result.add(acct);
			}
		}

		return result;
	}

	// ---------------------------------------------------------------

	public Collection<GnucashAccount> getAccounts() {
		if ( acctMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(new TreeSet<>(acctMap.values()));
	}

	public GnucashAccount getRootAccount() throws UnknownAccountTypeException {
		for ( GnucashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() == null && 
				 acct.getType() == GnucashAccount.Type.ROOT ) {
				return acct;
			}
		}

		return null; // Compiler happy
	}

	public Collection<? extends GnucashAccount> getParentlessAccounts() throws UnknownAccountTypeException {
		try {
			Collection<GnucashAccount> retval = new TreeSet<GnucashAccount>();

			for ( GnucashAccount acct : getAccounts() ) {
				if ( acct.getParentAccountID() == null ) {
					retval.add(acct);
				}

			}

			return retval;
		} catch (RuntimeException e) {
			LOGGER.error("getParentlessAccounts: Problem getting all root-account", e);
			throw e;
		} catch (Throwable e) {
			LOGGER.error("getParentlessAccounts: SERIOUS Problem getting all root-account", e);
			return new ArrayList<GnucashAccount>();
		}
	}

	public Collection<GCshID> getTopAccountIDs() throws UnknownAccountTypeException {
		Collection<GCshID> result = new ArrayList<GCshID>();

		GCshID rootAcctID = getRootAccount().getID();
		for ( GnucashAccount acct : getAccounts() ) {
			if ( acct.getParentAccountID() != null ) {
				if ( acct.getParentAccountID().equals(rootAcctID) &&
					 ( acct.getType() == GnucashAccount.Type.ASSET ||
					   acct.getType() == GnucashAccount.Type.LIABILITY ||
					   acct.getType() == GnucashAccount.Type.INCOME ||
					   acct.getType() == GnucashAccount.Type.EXPENSE ||
					   acct.getType() == GnucashAccount.Type.EQUITY) ) {
					result.add(acct.getID());
				}
			}
		}

		return result;
	}

	public Collection<GnucashAccount> getTopAccounts() throws UnknownAccountTypeException {
		Collection<GnucashAccount> result = new ArrayList<GnucashAccount>();

		for ( GCshID acctID : getTopAccountIDs() ) {
			GnucashAccount acct = getAccountByID(acctID);
			result.add(acct);
		}

		return result;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesAccountMap() {
		return acctMap.size();
	}

}
