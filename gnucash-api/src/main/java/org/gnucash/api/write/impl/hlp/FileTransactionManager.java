package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashTransactionImpl;
import org.gnucash.api.read.impl.GnuCashTransactionSplitImpl;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionImpl;
import org.gnucash.api.write.impl.GnuCashWritableTransactionSplitImpl;
import org.gnucash.base.basetypes.simple.GCshSpltID;
import org.gnucash.base.basetypes.simple.GCshTrxID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransactionManager extends org.gnucash.api.read.impl.hlp.FileTransactionManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileTransactionManager.class);

	// ---------------------------------------------------------------

	public FileTransactionManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GnuCashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
		GnuCashWritableTransactionImpl trx = new GnuCashWritableTransactionImpl(jwsdpTrx, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createTransaction: Generated new writable transaction: " + trx.getID());
		return trx;
	}

    @Override
	protected GnuCashTransactionSplitImpl createTransactionSplit(
			final GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt,
			final GnuCashTransaction trx, // actually, should be GnuCash*Writable*Transaction, 
                                          // but then the compiler is not happy...
			final boolean addSpltToAcct, 
			final boolean addSpltToInvc) {
    	if ( ! ( trx instanceof GnuCashWritableTransaction ) ) {
    		throw new IllegalArgumentException("transaction must be a writable one");
    	}
    	
    	GnuCashWritableTransactionSplitImpl splt = new GnuCashWritableTransactionSplitImpl(jwsdpTrxSplt, 
    																					   (GnuCashWritableTransaction) trx, 
    																					   addSpltToAcct, addSpltToInvc);
    	LOGGER.debug("createTransactionSplit: Generated new writable transaction split: " + splt.getID());
    	return splt;
    }

	// ---------------------------------------------------------------

	public void addTransaction(GnuCashTransaction trx) {
		addTransaction(trx, true);
	}

	public void addTransaction(GnuCashTransaction trx, boolean withSplt) {
		if ( trx == null ) {
			throw new IllegalArgumentException("argument <trx> is null");
		}
		
		trxMap.put(trx.getID(), trx);

		if ( withSplt ) {
			if ( trx.getSplits() != null ) {
				for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
					addTransactionSplit(splt, false);
				}
			}
		}

		LOGGER.debug("addTransaction: Added transaction to cache: " + trx.getID());
	}

	public void removeTransaction(GnuCashTransaction trx) {
		removeTransaction(trx, true);
	}

	public void removeTransaction(GnuCashTransaction trx, boolean withSplt) {
		if ( trx == null ) {
			throw new IllegalArgumentException("argument <trx> is null");
		}
		
		if ( withSplt ) {
			for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
				removeTransactionSplit(splt, false);
			}
		}

		trxMap.remove(trx.getID());

		LOGGER.debug("removeTransaction: Removed transaction from cache: " + trx.getID());
	}

	// ----------------------------
	
	public void removeTransaction_raw(final GCshTrxID trxID) {
		GncV2 pRootElement = gcshFile.getRootElement();

		for ( int i = 0; i < pRootElement.getGncBook().getBookElements().size(); i++ ) {
			Object bookElement = pRootElement.getGncBook().getBookElements().get(i);
			
			if ( !(bookElement instanceof GncTransaction) ) {
				continue;
			}

			GncTransaction jwsdpTrx = (GncTransaction) bookElement;
			if ( jwsdpTrx.getTrnId().getValue().equals(trxID.toString())) {
				pRootElement.getGncBook().getBookElements().remove(i);
				i--;
			}
		}
	}

	// ---------------------------------------------------------------

	public void addTransactionSplit(GnuCashTransactionSplit splt) {
		addTransactionSplit(splt, true);
	}

	public void addTransactionSplit(GnuCashTransactionSplit splt, boolean withTrx) {
		if ( splt == null ) {
			throw new IllegalArgumentException("argument <splt> is null");
		}
		
		trxSpltMap.put(splt.getID(), splt);

		if ( withTrx ) {
			addTransaction(splt.getTransaction(), false);
		}
	}

	public void removeTransactionSplit(GnuCashTransactionSplit splt) {
		removeTransactionSplit(splt, false);
	}

	public void removeTransactionSplit(GnuCashTransactionSplit splt, boolean withTrx) {
		if ( splt == null ) {
			throw new IllegalArgumentException("argument <splt> is null");
		}
		
		if ( withTrx ) {
			removeTransaction(splt.getTransaction(), false);
		}

		trxSpltMap.remove(splt.getID());
	}

	// ----------------------------
	
	public void removeTransactionSplit_raw(final GCshTrxID trxID, final GCshSpltID spltID) {
		GncTransaction trxRaw = getTransaction_raw(trxID);
		
		for ( int i = 0; i < trxRaw.getTrnSplits().getTrnSplit().size(); i++ ) {
			Object bookElement = trxRaw.getTrnSplits().getTrnSplit().get(i);
			
			if ( !(bookElement instanceof GncTransaction.TrnSplits.TrnSplit) ) {
				continue;
			}

			GncTransaction.TrnSplits.TrnSplit jwsdpTrxSplt = (GncTransaction.TrnSplits.TrnSplit) bookElement;
			if ( jwsdpTrxSplt.getSplitId().getValue().equals(spltID.toString())) {
				trxRaw.getTrnSplits().getTrnSplit().remove(i);
				i--;
			}
		}
	}

}
