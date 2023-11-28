package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashTransactionImpl;
import org.gnucash.api.read.impl.GnucashTransactionSplitImpl;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);
    
    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private Map<GCshID, GnucashTransaction>      trxMap;
    private Map<GCshID, GnucashTransactionSplit> trxSpltMap;

    // ---------------------------------------------------------------
    
    public FileTransactionManager(GnucashFileImpl gcshFile) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	init1(pRootElement);
	init2(pRootElement);
    }

    private void init1(final GncV2 pRootElement) {
	trxMap = new HashMap<GCshID, GnucashTransaction>();

	for ( GnucashTransactionImpl trx : getTransactions_readAfresh() ) {
	    trxMap.put(trx.getId(), trx);
	}

	LOGGER.debug("init1: No. of entries in transaction map: " + trxMap.size());
    }
    
    private void init2(final GncV2 pRootElement) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	trxSpltMap = new HashMap<GCshID, GnucashTransactionSplit>();

	for ( GnucashTransaction trx : trxMap.values() ) {
	    try {
		for ( GnucashTransactionSplit splt : ((GnucashTransactionImpl) trx).getSplits(true, true) ) {
		    trxSpltMap.put(splt.getId(), splt);
		}
	    } catch (RuntimeException e) {
		LOGGER.error("init2: [RuntimeException] Problem in " + getClass().getName() + ".init2: "
			+ "ignoring illegal Transaction entry with id=" + trx.getId(), e);
//		System.err.println("init2: ignoring illegal Transaction entry with id: " + trx.getId());
//		System.err.println("  " + e.getMessage());
	    }
	} // for trx

	LOGGER.debug("init2: No. of entries in transaction split map: " + trxSpltMap.size());
    }
    
    // ----------------------------

    /**
     * @param jwsdpTrx the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashTransaction to wrap the given jaxb-object.
     */
    protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
	GnucashTransactionImpl trx = new GnucashTransactionImpl(jwsdpTrx, gcshFile.getGnucashFile(), true);
	return trx;
    }

    /**
     * @param jwsdpTrx the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GnucashTransaction to wrap the given jaxb-object.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected GnucashTransactionSplitImpl createTransactionSplit(
	    final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
	    final GnucashTransaction trx,
	    final boolean addSpltToAcct,
	    final boolean addSpltToInvc) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	GnucashTransactionSplitImpl splt = new GnucashTransactionSplitImpl(jwsdpTrxSplt, trx, 
		                                                           addSpltToAcct, addSpltToInvc);
	return splt;
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getTransactionByID(java.lang.String)
     */
    public GnucashTransaction getTransactionByID(final GCshID id) {
	if (trxMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	GnucashTransaction retval = trxMap.get(id);
	if (retval == null) {
	    LOGGER.warn("getTransactionByID: No Transaction with id '" + id + "'. We know " + trxMap.size() + " transactions.");
	}
	return retval;
    }

    /**
     * @see GnucashFile#getTransactions()
     */
    public Collection<? extends GnucashTransaction> getTransactions() {
	if (trxMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(trxMap.values());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getTransactionByID(java.lang.String)
     */
    public GnucashTransactionSplit getTransactionSplitByID(final GCshID id) {
        if (trxSpltMap == null) {
            throw new IllegalStateException("no root-element loaded");
        }
    
        GnucashTransactionSplit retval = trxSpltMap.get(id);
        if (retval == null) {
            LOGGER.warn("getTransactionSplitByID: No Transaction-Split with id '" + id + "'. We know "
        	    + trxSpltMap.size() + " transaction splits.");
        }
        return retval;
    }

    public Collection<GnucashTransactionImpl> getTransactions_readAfresh() {
	Collection<GnucashTransactionImpl> result = new ArrayList<GnucashTransactionImpl>();
	
	for ( GncTransaction jwsdpTrx : getTransactions_raw() ) {
	    try {
		GnucashTransactionImpl trx = createTransaction(jwsdpTrx);
		result.add(trx);
	    } catch (RuntimeException e) {
		LOGGER.error("getTransactions_readAfresh: [RuntimeException] Problem in " + getClass().getName() + ".getTransactions_readAfresh: "
			+ "ignoring illegal Transaction entry with id=" + jwsdpTrx.getTrnId().getValue(), e);
//		System.err.println("getTransactions_readAfresh: gnoring illegal Transaction entry with id: " + jwsdpTrx.getTrnId().getValue());
//		System.err.println("  " + e.getMessage());
	    }
	}
	
	return result;
    }

    private Collection<GncTransaction> getTransactions_raw() {
	GncV2 pRootElement = gcshFile.getRootElement();
	
	Collection<GncTransaction> result = new ArrayList<GncTransaction>();
	
	for (Iterator<Object> iter = pRootElement.getGncBook().getBookElements().iterator(); iter.hasNext();) {
	    Object bookElement = iter.next();
	    if (!(bookElement instanceof GncTransaction)) {
		continue;
	    }
	    
	    GncTransaction jwsdpTrx = (GncTransaction) bookElement;
	    result.add(jwsdpTrx);
	}
	
	return result;
    }
    
    // ----------------------------

    public Collection<GnucashTransactionSplit> getTransactionSplits() {
	if (trxSpltMap == null) {
	    throw new IllegalStateException("no root-element loaded");
	}
	return Collections.unmodifiableCollection(trxSpltMap.values());
    }

    public Collection<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	Collection<GnucashTransactionSplitImpl> result = new ArrayList<GnucashTransactionSplitImpl>();
	
	for ( GnucashTransaction trx : getTransactions_readAfresh() ) {
		for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getId()) ) {
		    try {
			GnucashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx, 
					                                          false, false);
			result.add(splt);
		    } catch (RuntimeException e) {
			LOGGER.error("getTransactionSplits_readAfresh(1): [RuntimeException] Problem in " + getClass().getName() + ".getTransactionSplits_readAfresh: "
				    + "ignoring illegal Transaction Split entry with id=" + jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(1): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitId().getValue());
//			System.err.println("  " + e.getMessage());
		    }
		} // for jwsdpTrxSplt
	} // for trx
	
	return result;
    }

    public Collection<GnucashTransactionSplitImpl> getTransactionSplits_readAfresh(final GCshID trxID) throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	Collection<GnucashTransactionSplitImpl> result = new ArrayList<GnucashTransactionSplitImpl>();
	
	for ( GnucashTransaction trx : getTransactions_readAfresh() ) {
	    if ( trx.getId().equals(trxID) ) {
		for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : getTransactionSplits_raw(trx.getId()) ) {
		    try {
			GnucashTransactionSplitImpl splt = createTransactionSplit(jwsdpTrxSplt, trx, 
					                                          true, true);
			result.add(splt);
		    } catch (RuntimeException e) {
			LOGGER.error("getTransactionSplits_readAfresh(2): [RuntimeException] Problem in " + getClass().getName() + ".getTransactionSplits_readAfresh: "
				    + "ignoring illegal Transaction Split entry with id=" + jwsdpTrxSplt.getSplitId().getValue(), e);
//			System.err.println("getTransactionSplits_readAfresh(2): ignoring illegal Transaction Split entry with id: " + jwsdpTrxSplt.getSplitId().getValue());
//			System.err.println("  " + e.getMessage());
		    }
		} // for jwsdpTrxSplt
	    } // if
	} // for trx
	
	return result;
    }

    private Collection<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GncTransaction jwsdpTrx) {
	Collection<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();
	
	for ( GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt : jwsdpTrx.getTrnSplits().getTrnSplit() ) {
	    result.add(jwsdpTrxSplt);
	}
	
	return result;
    }

    private Collection<GncTransaction.TrnSplits.TrnSplit> getTransactionSplits_raw(final GCshID trxID) {
	Collection<GncTransaction.TrnSplits.TrnSplit> result = new ArrayList<GncTransaction.TrnSplits.TrnSplit>();
	
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

    public void addTransaction(GnucashTransaction trx) {
	trxMap.put(trx.getId(), trx);
    }

    public void removeTransaction(GnucashTransaction trx) {
	trxMap.remove(trx.getId());
    }

    // ---------------------------------------------------------------

    public void addTransactionSplit(GnucashTransactionSplit splt) {
	trxSpltMap.put(splt.getId(), splt);
    }

    public void removeTransactionSplit(GnucashTransactionSplit splt) {
	trxSpltMap.remove(splt.getId());
    }

    // ---------------------------------------------------------------

    public int getNofEntriesTransactionMap() {
	return trxMap.size();
    }

    public int getNofEntriesTransactionSplitMap() {
	return trxSpltMap.size();
    }

}
