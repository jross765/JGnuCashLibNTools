package org.gnucash.apiext.secacct;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.base.tuples.AcctIDAmountPair;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Collection of simplified, high-level access functions to a GnuCash file for
 * managing securities accounts (brokerage accounts).
 * <br>
 * These methods are sort of "macros" for the low-level access functions
 * in the "API" module.
 */
public class SecuritiesAccountTransactionManager {
    
    public enum Type {
	BUY_STOCK,
	DIVIDEND
    }
    
    // ---------------------------------------------------------------
    
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuritiesAccountTransactionManager.class);

    // ---------------------------------------------------------------
    
    public static GnuCashWritableTransaction genBuyStockTrx(
	    final GnuCashWritableFileImpl gcshFile,
	    final GCshID stockAcctID,
	    final GCshID taxFeeAcctID,
	    final GCshID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final FixedPointNumber taxesFees,
	    final LocalDate postDate,
	    final String descr) {
    	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
    	
    	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
    	expensesAcctAmtList.add(newPair);
    	
    	return genBuyStockTrx(gcshFile, 
    			      stockAcctID, expensesAcctAmtList, offsetAcctID, 
    			      nofStocks, stockPrc, 
    			      postDate, descr);	
    }
    
    public static GnuCashWritableTransaction genBuyStockTrx(
	    final GnuCashWritableFileImpl gcshFile,
	    final GCshID stockAcctID,
	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
	    final GCshID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final LocalDate postDate,
	    final String descr) {
	
	if ( gcshFile == null ) {
	    throw new IllegalArgumentException("null GnuCash file given");
	}
		
	if ( stockAcctID == null  ||
	     offsetAcctID == null ) {
	    throw new IllegalArgumentException("null account ID given");
	}
	
	if ( ! ( stockAcctID.isSet()  ) ||
	     ! ( offsetAcctID.isSet() ) ) {
	    throw new IllegalArgumentException("unset account ID given");
	}
		
	if ( expensesAcctAmtList == null ) {
	    throw new IllegalArgumentException("null expenses account list given");
	}
			
	if ( expensesAcctAmtList.isEmpty() ) {
	    throw new IllegalArgumentException("empty expenses account list given");
	}
			
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    if ( ! elt.isNotNull() ) {
		throw new IllegalArgumentException("null expenses account list element given");
	    }
	    if ( ! elt.isSet() ) {
		throw new IllegalArgumentException("unset expenses account list element given");
	    }
	}

	if ( nofStocks == null  ||
		 stockPrc == null ) {
	    throw new IllegalArgumentException("null amount given");
	}
	
	if ( nofStocks.doubleValue() <= 0.0 ) {
	    throw new IllegalArgumentException("number of stocks <= 0.0 given");
	}
				
	if ( stockPrc.doubleValue() <= 0.0 ) {
	    throw new IllegalArgumentException("stock price <= 0.0 given");
	}
				
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    if ( elt.amount().doubleValue() <= 0.0 ) {
		throw new IllegalArgumentException("expense <= 0.0 given");
	    }
	}

	LOGGER.debug("genBuyStockTrx: Account 1 name (stock):      '" + gcshFile.getAccountByID(stockAcctID).getQualifiedName() + "'");
	int counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    LOGGER.debug("genBuyStockTrx: Account 2." + counter + " name (expenses): '" + gcshFile.getAccountByID(elt.accountID()).getQualifiedName() + "'");
	    counter++;
	}
	LOGGER.debug("genBuyStockTrx: Account 3 name (offsetting): '" + gcshFile.getAccountByID(offsetAcctID).getQualifiedName() + "'");

	// ---
	// Check account types
	GnuCashAccount stockAcct  = gcshFile.getAccountByID(stockAcctID);
	if ( stockAcct.getType() != GnuCashAccount.Type.STOCK ) {
	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + GnuCashAccount.Type.STOCK);
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != GnuCashAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
	    }
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != GnuCashAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
	    }
	}

	GnuCashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
	if ( offsetAcct.getType() != GnuCashAccount.Type.BANK ) {
	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + GnuCashAccount.Type.BANK);
	}

	// ---

	FixedPointNumber amtNet   = nofStocks.copy().multiply(stockPrc);
	LOGGER.debug("genBuyStockTrx: Net amount: " + amtNet);
	
	FixedPointNumber amtGross = amtNet.copy();
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    amtGross.add(elt.amount());
	}
	LOGGER.debug("genBuyStockTrx: Gross amount: " + amtGross);

	// ---

	GnuCashWritableTransaction trx = gcshFile.createWritableTransaction();
	trx.setDescription(descr);

	// ---
	
	GnuCashWritableTransactionSplit splt1 = trx.createWritableSplit(offsetAcct);
	splt1.setValue(new FixedPointNumber(amtGross.copy().negate()));
	splt1.setQuantity(new FixedPointNumber(amtGross.copy().negate()));
	LOGGER.debug("genBuyStockTrx: Split 1 to write: " + splt1.toString());

	// ---
	
	GnuCashWritableTransactionSplit splt2 = trx.createWritableSplit(stockAcct);
	splt2.setValue(new FixedPointNumber(amtNet));
	splt2.setQuantity(new FixedPointNumber(nofStocks));
	splt2.setAction(GnuCashTransactionSplit.Action.BUY);
	LOGGER.debug("genBuyStockTrx: Split 2 to write: " + splt2.toString());

	// ---

	counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    GnuCashWritableTransactionSplit splt3 = trx.createWritableSplit(expensesAcct);
	    splt3.setValue(new FixedPointNumber(elt.amount()));
	    splt3.setQuantity(new FixedPointNumber(elt.amount()));
	    LOGGER.debug("genBuyStockTrx: Split 3." + counter + " to write: " + splt3.toString());
	    counter++;
	}

	// ---

	trx.setDatePosted(postDate);
	trx.setDateEntered(LocalDateTime.now());

	// ---

	LOGGER.info("genBuyStockTrx: Generated new Transaction: " + trx.getID());
	return trx;
    }
    
    // ---------------------------------------------------------------
    
    public static GnuCashWritableTransaction genDivivendTrx(
    	    final GnuCashWritableFileImpl gcshFile,
    	    final GCshID stockAcctID,
    	    final GCshID incomeAcctID,
    	    final GCshID taxFeeAcctID,
    	    final GCshID offsetAcctID,
    	    final FixedPointNumber divGross,
    	    final FixedPointNumber taxesFees,
    	    final LocalDate postDate,
    	    final String descr) {
    	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
    	
    	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
    	expensesAcctAmtList.add(newPair);
    	
    	return genDivivendTrx(gcshFile, 
    			      stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
    			      divGross, 
    			      postDate, descr);
    }
    
    public static GnuCashWritableTransaction genDivivendTrx(
    	    final GnuCashWritableFileImpl gcshFile,
    	    final GCshID stockAcctID,
    	    final GCshID incomeAcctID,
    	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
    	    final GCshID offsetAcctID,
    	    final FixedPointNumber divGross,
    	    final LocalDate postDate,
    	    final String descr) {
    	
    	if ( gcshFile == null ) {
    	    throw new IllegalArgumentException("null GnuCash file given");
    	}
    		
    	if ( stockAcctID == null  ||
    	     incomeAcctID == null ||
    	     offsetAcctID == null ) {
    	    throw new IllegalArgumentException("null account ID given");
    	}
    	
    	if ( ! ( stockAcctID.isSet()  ) ||
    	     ! ( incomeAcctID.isSet() ) ||
    	     ! ( offsetAcctID.isSet() ) ) {
    	    throw new IllegalArgumentException("unset account ID given");
    	}
    		
    	if ( expensesAcctAmtList == null ) {
    	    throw new IllegalArgumentException("null expenses account list given");
    	}

    	// CAUTION: Yes, this actually happen in real life, e.g. with specifics 
    	// of German tax law (Freibetrag, Kapitalausschuettung).
//    	if ( expensesAcctAmtList.isEmpty() ) {
//    	    throw new IllegalArgumentException("empty expenses account list given");
//    	}
    			
    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    	    if ( ! elt.isNotNull() ) {
    		throw new IllegalArgumentException("null expenses account list element given");
    	    }
    	    if ( ! elt.isSet() ) {
    		throw new IllegalArgumentException("unset expenses account list element given");
    	    }
    	}

    	if ( divGross == null ) {
    	    throw new IllegalArgumentException("null gross dividend given");
    	}

    	// CAUTION: The following two: In fact, this can happen
    	// (negative booking after cancellation / Stornobuchung)
//    	if ( divGross.doubleValue() <= 0.0 ) {
//    	    throw new IllegalArgumentException("gross dividend <= 0.0 given");
//    	}
//    				
//    	if ( taxes.doubleValue() <= 0.0 ) {
//    	    throw new IllegalArgumentException("taxes <= 0.0 given");
//    	}
    				
    	LOGGER.debug("genDivivendTrx: Account 1 name (stock):      '" + gcshFile.getAccountByID(stockAcctID).getQualifiedName() + "'");
    	LOGGER.debug("genDivivendTrx: Account 2 name (income):     '" + gcshFile.getAccountByID(incomeAcctID).getQualifiedName() + "'");
    	int counter = 1;
    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    	    LOGGER.debug("genDivivendTrx: Account 3." + counter + " name (expenses): '" + gcshFile.getAccountByID(elt.accountID()).getQualifiedName() + "'");
    	    counter++;
    	}
    	LOGGER.debug("genDivivendTrx: Account 4 name (offsetting): '" + gcshFile.getAccountByID(offsetAcctID).getQualifiedName() + "'");

    	// ---
    	// Check account types
    	GnuCashAccount stockAcct  = gcshFile.getAccountByID(stockAcctID);
    	if ( stockAcct.getType() != GnuCashAccount.Type.STOCK ) {
    	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + GnuCashAccount.Type.STOCK);
    	}

    	GnuCashAccount incomeAcct = gcshFile.getAccountByID(incomeAcctID);
    	if ( incomeAcct.getType() != GnuCashAccount.Type.INCOME ) {
    	    throw new IllegalArgumentException("Account with ID " + incomeAcct + " is not of type " + GnuCashAccount.Type.INCOME);
    	}

    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    		GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
    	    if ( expensesAcct.getType() != GnuCashAccount.Type.EXPENSE ) {
    		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnuCashAccount.Type.EXPENSE);
    	    }
    	}
    	
    	GnuCashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
    	if ( offsetAcct.getType() != GnuCashAccount.Type.BANK ) {
    	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + GnuCashAccount.Type.BANK);
    	}

    	// ---

    	FixedPointNumber expensesSum = new FixedPointNumber();
    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    	    expensesSum.add(elt.amount());
    	}
    	LOGGER.debug("genDivivendTrx: Sum of all expenses: " + expensesSum);

    	FixedPointNumber divNet = divGross.copy().subtract(expensesSum);
    	LOGGER.debug("genDivivendTrx: Net dividend: " + divNet);

    	// ---

    	GnuCashWritableTransaction trx = gcshFile.createWritableTransaction();
    	trx.setDescription(descr);

    	// ---
    	
    	GnuCashWritableTransactionSplit splt1 = trx.createWritableSplit(stockAcct);
    	splt1.setValue(new FixedPointNumber());
    	splt1.setQuantity(new FixedPointNumber());
    	splt1.setAction(GnuCashTransactionSplit.Action.DIVIDEND);
    	LOGGER.debug("genDivivendTrx: Split 1 to write: " + splt1.toString());

    	// ---

    	GnuCashWritableTransactionSplit splt2 = trx.createWritableSplit(offsetAcct);
    	splt2.setValue(new FixedPointNumber(divNet));
    	splt2.setQuantity(new FixedPointNumber(divNet));
    	LOGGER.debug("genDivivendTrx: Split 2 to write: " + splt2.toString());

    	// ---

    	GnuCashWritableTransactionSplit splt3 = trx.createWritableSplit(incomeAcct);
    	splt3.setValue(new FixedPointNumber(divGross.copy().negate()));
    	splt3.setQuantity(new FixedPointNumber(divGross.copy().negate()));
    	LOGGER.debug("genDivivendTrx: Split 3 to write: " + splt3.toString());

    	// ---

    	counter = 1;
    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    	    GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
    	    GnuCashWritableTransactionSplit splt4 = trx.createWritableSplit(expensesAcct);
    	    splt4.setValue(new FixedPointNumber(elt.amount()));
    	    splt4.setQuantity(new FixedPointNumber(elt.amount()));
    	    LOGGER.debug("genDivivendTrx: Split 4." + counter + " to write: " + splt4.toString());
    	    counter++;
    	}

    	// ---

    	trx.setDatePosted(postDate);
    	trx.setDateEntered(LocalDateTime.now());

    	// ---

    	LOGGER.info("genDivivendTrx: Generated new Transaction: " + trx.getID());
    	return trx;
    }
        
}
