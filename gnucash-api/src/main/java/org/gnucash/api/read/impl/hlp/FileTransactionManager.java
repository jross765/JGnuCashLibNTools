package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	protected GnucashFileImpl gcshFile;

	private Map<GCshID, GnucashTransaction>      trxMap;
	private Map<GCshID, GnucashTransactionSplit> trxSpltMap;

	// ---------------------------------------------------------------

	public FileTransactionManager(GnucashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		init1(pRootElement);
		init2(pRootElement);
	}

	private void init1(final GncV2 pRootElement) {
		trxMap = new HashMap<GCshID, GnucashTransaction>();

		for ( GnucashTransactionImpl trx : getTransactions_readAfresh() ) {
			trxMap.put(trx.getID(), trx);
		}

		LOGGER.debug("init1: No. of entries in transaction map: " + trxMap.size());
	}

	private void init2(final GncV2 pRootElement) {
		trxSpltMap = new HashMap<GCshID, GnucashTransactionSplit>();

		for ( GnucashTransaction trx : trxMap.values() ) {
			try {
				List<GnucashTransactionSplit> spltList = null;
				if ( gcshFile instanceof GnucashWritableFileImpl ) {
					spltList = ((GnucashTransactionImpl) trx).getSplits(false, true);
				} else {
					spltList = ((GnucashTransactionImpl) trx).getSplits(true, true);
				}
				for ( GnucashTransactionSplit splt : spltList ) {
					trxSpltMap.put(splt.getID(), splt);
				}
			} catch (RuntimeException e) {
				LOGGER.error("init2: [RuntimeException] Problem in " + getClass().getName() + ".init2: "
						+ "ignoring illegal Transaction entry with id=" + trx.getID(), e);
//		System.err.println("init2: ignoring illegal Transaction entry with id: " + trx.getID());
//		System.err.println("  " + e.getMessage());
			}
		} // for trx

		LOGGER.debug("init2: No. of entries in transaction split map: " + trxSpltMap.size());
	}

	// ----------------------------

	protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
		GnucashTransactionImpl trx = new GnucashTransactionImpl(jwsdpTrx, gcshFile.getGnucashFile(), true);
		LOGGER.debug("createTransaction: Generated new transaction: " + trx.getID());
		return trx;
	}

	protected GnucashTransactionSplitImpl createTransactionSplit(
			final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
			final GnucashTransaction trx, 
			final boolean addSpltToAcct, 
			final boolean addSpltToInvc) {
		GnucashTransactionSplitImpl splt = new GnucashTransactionSplitImpl(jwsdpTrxSplt, trx, 
				                                                           addSpltToAcct, addSpltToInvc);
		LOGGER.debug("createTransactionSplit: Generated new transaction split: " + splt.getID());
		return splt;
	}

	// ---------------------------------------------------------------

	public void addTransaction(GnucashTransaction trx) {
		addTransaction(trx, true);
	}

	public void addTransaction(GnucashTransaction trx, boolean withSplt) {
		trxMap.put(trx.getID(), trx);

		if ( withSplt ) {
			for ( GnucashTransactionSplit splt : trx.getSplits() ) {
				addTransactionSplit(splt, false);
			}
		}

		LOGGER.debug("addTransaction: Added transaction to cache: " + trx.getID());
	}

	public void removeTransaction(GnucashTransaction trx) {
		removeTransaction(trx, true);
	}

	public void removeTransaction(GnucashTransaction trx, boolean withSplt) {
		if ( withSplt ) {
			for ( GnucashTransactionSplit splt : trx.getSplits() ) {
				removeTransactionSplit(splt, false);
			}
		}

		trxMap.remove(trx.getID());

		LOGGER.debug("removeTransaction: Removed transaction from cache: " + trx.getID());
	}

	// ---------------------------------------------------------------

	public void addTransactionSplit(GnucashTransactionSplit splt) {
		addTransactionSplit(splt, true);
	}

	public void addTransactionSplit(GnucashTransactionSplit splt, boolean withTrx) {
		trxSpltMap.put(splt.getID(), splt);

		if ( withTrx ) {
			addTransaction(splt.getTransaction(), false);
		}
	}

	public void removeTransactionSplit(GnucashTransactionSplit splt) {
		removeTransactionSplit(splt, true);
	}

	public void removeTransactionSplit(GnucashTransactionSplit splt, boolean withTrx) {
		if ( withTrx ) {
			removeTransaction(splt.getTransaction(), false);
		}

		trxSpltMap.remove(splt.getID());
	}

	// ---------------------------------------------------------------

	public GnucashTransaction getTransactionByID(final GCshID trxID) {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashTransaction retval = trxMap.get(trxID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionByID: No Transaction with id '" + trxID + "'. We know " + trxMap.size() + " transactions.");
		}

		return retval;
	}

	public Collection<? extends GnucashTransaction> getTransactions() {
		if ( trxMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		return Collections.unmodifiableCollection(trxMap.values());
	}

	// ---------------------------------------------------------------

	public GnucashTransactionSplit getTransactionSplitByID(final GCshID spltID) {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnucashTransactionSplit retval = trxSpltMap.get(spltID);
		if ( retval == null ) {
			LOGGER.warn("getTransactionSplitByID: No Transaction-Split with id '" + spltID + "'. We know " + trxSpltMap.size() + " transaction splits.");
		}

		return retval;
	}

	public List<GnucashTransactionImpl> getTransactions_readAfresh() {
		List<GnucashTransactionImpl> result = new ArrayList<GnucashTransactionImpl>();

		for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
			try {
				GnucashTransactionImpl trx = createTransaction(jwsdpTrx);
				result.add(trx);
			} catch (RuntimeException e) {
				LOGGER.error("getTransactions_readAfresh: [RuntimeException] Problem in " + getClass().getName()
						+ ".getTransactions_readAfresh: " + "ignoring illegal Transaction entry with id="
						+ jwsdpTrx.getTrnId().getValue(), e);
//		System.err.println("getTransactions_readAfresh: ignoring illegal Transaction entry with id: " + jwsdpTrx.getTrnID().getValue());
//		System.err.println("  " + e.getMessage());
			}
		}

		return result;
	}

	private List<GncTransaction> getTransactions_raw() {
		GncV2 pRootElement = gcshFile.getRootElement();

		List<GncTransaction> result = new ArrayList<GncTransaction>();

		for ( Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext(); ) {
			Object bookElement = iter.next();
			if ( !(bookElement instanceof GncTransaction) ) {
				continue;
			}

			GncTransaction jwsdpTrx = (GncTransaction) bookElement;
			result.add(jwsdpTrx);
		}

		return result;
	}

	// ----------------------------

	public List<GnucashTransactionSplit> getTransactionSplits() {
		if ( trxSpltMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}
		
		List<GnucashTransactionSplit> result = new ArrayList<GnucashTransactionSplit>();
		for ( GnucashTransactionSplit elt : trxSpltMap.values() ) {
			result.add(elt);
		}
		
		return Collections.unmodifiableList(result);
	}

	public List<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh() {
		List<GnucashTransactionSplitImpl> result = new ArrayList<GnucashTransactionSplitImpl>();

		for ( GnucashTransaction trx : getTransactions_readAfresh() ) {
			for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
				try {
					GnucashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx,
																			  false, false);
					result.add(splt);
				} catch (RuntimeException e) {
					LOGGER.error("getTransactionSplits_readAfresh(1): [RuntimeException] Problem in "
							+ "ignoring illegal Transaction Split entry with id="
							+ jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(1): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
				}
			} // for jwsdpTrxSplt
		} // for trx

		return result;
	}

	public List<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh(final GCshID trxID) {
		List<GnucashTransactionSplitImpl> result = new ArrayList<GnucashTransactionSplitImpl>();

		for ( GnucashTransaction trx : getTransactions_readAfresh() ) {
			if ( trx.getID().equals(trxID) ) {
				for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getID()) ) {
					try {
						GnucashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx, 
																				  true, true);
						result.add(splt);
					} catch (RuntimeException e) {
						LOGGER.error("getTransactionSplits_readAfresh(2): [RuntimeException] Problem in "
								+ "ignoring illegal Transaction Split entry with id="
								+ jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(2): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitID().getValue());
//			System.err.println("  " + e.getMessage());
					}
				} // for jwsdpTrxSplt
			} // if
		} // for trx

		return result;
	}

	private List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GncTransaction jwsdpTrx) {
		List<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();

		for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : jwsdpTrx.getTrnSplits().getTrnSplit() ) {
			result.add(jwsdpTrxSplt);
		}

		return result;
	}

	private List<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GCshID trxID) {
		List<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();

		for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
			if ( jwsdpTrx.getTrnId().getValue().equals(trxID.toString()) ) {
				for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : jwsdpTrx.getTrnSplits().getTrnSplit() ) {
					result.add(jwsdpTrxSplt);
				}
			}
		}

		return result;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesTransactionMap() {
		return trxMap.size();
	}

	public int getNofEntriesTransactionSplitMap() {
		return trxSpltMap.size();
	}

}
