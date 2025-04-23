package org.gnucash.apiext.trxmgr;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionMergerVar2 extends TransactionMergerBase
								   implements IFTransactionMerger 
{
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionMergerVar2.class);
    
    // ---------------------------------------------------------------
    
	// CAUTION: 
	// In this merger variant, the survivor trx's bank split will be deleted
	// and vice versa.
	// ==> The zdTrxBankSpltID (ZD Splt), part of the dier trx, will die, 
	//     but a copy of it will survive as part of the survivor trx, 
	//     called ZS Splt/after.
	// Analogously, the dierBankTrxSplt (ZS Splt/before), part of the survivor trx, 
	// will die / be replaced by the above-mentioned copy ZS Splt/after.
	// 
	// Visualization:
	// -------------
	//
	// Dier Trx                            Survivor Trx
	// +-- XD Splt                         +-- XS Splt
	// +-- YD Splt                         +-- YS Splt
	// +-- ZD Splt (to bank acct)          +-- ZS Splt/before (to bank account)
	//     ^                               |   ^
	//     +-- will be copied to           |   +-- Will be replaced by ZS Splt/after
	//         survivor trx -------------> +-- ZS Splt/after (to bank account)
	//                                         ^
	//                                         +-- Copy of ZD Splt
    //
	// Let that sink in for a moment before you review the code in this class.

	private GCshID zdTrxBankSpltID = null;
	private GCshID zsBankTrxSpltBeforeID = null;

	private GnuCashWritableTransaction survivorTrx = null;
	
    // ---------------------------------------------------------------
	
	public TransactionMergerVar2(GnuCashWritableFile gcshFile) {
		super(gcshFile);
		setVar(Var.VAR_2);
	}
    
    // ---------------------------------------------------------------
	
	public GCshID getZDBankTrxSpltID() {
		return zdTrxBankSpltID;
	}
    
	public void setZDBankTrxSpltID(GCshID spltID) {
		this.zdTrxBankSpltID = spltID;
	}
	
	// ---
    
	public GCshID getZSBankTrxSpltBeforeID() {
		return zsBankTrxSpltBeforeID;
	}
    
	public void setZSBankTrxSpltBeforeID(GCshID spltID) {
		this.zsBankTrxSpltBeforeID = spltID;
	}
    
	// ---
    
	public GnuCashWritableTransaction getSurvivorTransaction() {
		return survivorTrx;
	}
    
	public void setSurvivorTransaction(GnuCashWritableTransaction trx) {
		this.survivorTrx = trx;
	}
    
    // ---------------------------------------------------------------
    
	public void merge(GCshID survivorID, GCshID dierID) throws MergePlausiCheckException {
		GnuCashTransaction survivor = gcshFile.getTransactionByID(survivorID);
		GnuCashWritableTransaction dier = gcshFile.getWritableTransactionByID(dierID);
		merge(survivor, dier);
	}

	public void merge(GnuCashTransaction survivor, GnuCashWritableTransaction dier) throws MergePlausiCheckException {
		if ( zdTrxBankSpltID == null ) {
			throw new IllegalStateException("ZD bank Trx Split ID is null");
		}
		
		if ( zsBankTrxSpltBeforeID == null ) {
			throw new IllegalStateException("ZS bank Trx Split (before) ID is null");
		}
		
		if ( survivorTrx == null ) {
			throw new IllegalStateException("New bank Trx is null");
		}
		
		if ( ! zdTrxBankSpltID.isSet() ) {
			throw new IllegalStateException("ZD bank Trx Split ID is not set");
		}
		
		if ( ! zsBankTrxSpltBeforeID.isSet() ) {
			throw new IllegalStateException("ZSw bank Trx Split (before) ID is not set");
		}
		
		if ( ! survivorTrx.getID().isSet() ) {
			throw new IllegalStateException("New bank Trx's ID is not set");
		}
		
		if ( ! zdTrxBankSpltID.equals(zsBankTrxSpltBeforeID) ) {
			throw new IllegalStateException("IDs of ZD bank Trx Split and ZS bank Trx Split (before) are identical");
		}
		
		// ---
		
		// 1) Perform plausi checks
		if ( ! plausiCheck(survivor, dier) ) {
			LOGGER.error("merge: survivor-dier-pair did not pass plausi check: " + survivor.getID() + "/" + dier.getID());
			throw new MergePlausiCheckException();
		}

		GnuCashWritableTransactionSplit zsBankTrxSpltAfter = copyBankTrxSplt();
		LOGGER.info("merge: Transaction Split " + zdTrxBankSpltID + " copied to new Splt " + zsBankTrxSpltAfter.getID());
		
		GnuCashWritableTransactionSplit zsBankTrxSpltBefore = gcshFile.getWritableTransactionSplitByID(zsBankTrxSpltBeforeID);
		survivorTrx.remove(zsBankTrxSpltBefore);
		LOGGER.info("merge: Removed Transaction Split " + zsBankTrxSpltBeforeID);
		
		GCshID dierID = dier.getID();
		gcshFile.removeTransaction(dier);
		LOGGER.info("merge: Transaction " + dierID + " (dier) removed");
	}

    // ---------------------------------------------------------------
	
	private GnuCashWritableTransactionSplit copyBankTrxSplt() {
		GnuCashTransactionSplit zdTrxBankSplt = gcshFile.getTransactionSplitByID(zdTrxBankSpltID);
		GnuCashWritableTransactionSplit copy = survivorTrx.createWritableSplit(zdTrxBankSplt.getAccount());
		
		copy.setAction(zdTrxBankSplt.getAction());
		copy.setValue(zdTrxBankSplt.getValue());
		copy.setQuantity(zdTrxBankSplt.getQuantity());
		copy.setDescription(zdTrxBankSplt.getDescription());
		
		// User-defined attributes
		// ::TODO
//		for ( String attrKey : zdTrxBankSplt.getUserDefinedAttributeKeys() ) {
//			newBankTrxSplt.addUserDefinedAttribute( zdTrxBankSplt.getUserDefinedAttribute(attrKey) );
//		}
		
		return copy;
	}
}
