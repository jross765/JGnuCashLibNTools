package org.gnucash.apiext.depot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.base.tuples.AcctIDAmountPair;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DepotTransactionManager {
    
    public enum Type {
	BUY_STOCK,
	DIVIDEND
    }
    
    // ---------------------------------------------------------------
    
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(DepotTransactionManager.class);

    // ---------------------------------------------------------------
    
    @SuppressWarnings("exports")
    public static GnucashWritableTransaction genBuyStockTrx(
	    final GnucashWritableFileImpl gcshFile,
	    final GCshID stockAcctID,
	    final GCshID taxFeeAcctID,
	    final GCshID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final FixedPointNumber taxesFees,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
    	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
    	
    	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
    	expensesAcctAmtList.add(newPair);
    	
    	return genBuyStockTrx(gcshFile, 
    			      stockAcctID, expensesAcctAmtList, offsetAcctID, 
    			      nofStocks, stockPrc, 
    			      postDate, descr);	
    }
    
    @SuppressWarnings("exports")
    public static GnucashWritableTransaction genBuyStockTrx(
	    final GnucashWritableFileImpl gcshFile,
	    final GCshID stockAcctID,
	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
	    final GCshID offsetAcctID,
	    final FixedPointNumber nofStocks,
	    final FixedPointNumber stockPrc,
	    final LocalDate postDate,
	    final String descr) throws UnknownAccountTypeException {
	
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
	GnucashAccount stockAcct  = gcshFile.getAccountByID(stockAcctID);
	if ( stockAcct.getType() != GnucashAccount.Type.STOCK ) {
	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + GnucashAccount.Type.STOCK);
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
		GnucashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != GnucashAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnucashAccount.Type.EXPENSE);
	    }
	}

	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
		GnucashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    if ( expensesAcct.getType() != GnucashAccount.Type.EXPENSE ) {
		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnucashAccount.Type.EXPENSE);
	    }
	}

	GnucashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
	if ( offsetAcct.getType() != GnucashAccount.Type.BANK ) {
	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + GnucashAccount.Type.BANK);
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

	GnucashWritableTransaction trx = gcshFile.createWritableTransaction();
	trx.setDescription(descr);

	// ---
	
	String spltDescr = "Generated by DepotTransactionManager, " + LocalDateTime.now();

	GnucashWritableTransactionSplit splt1 = trx.createWritableSplit(offsetAcct);
	splt1.setValue(new FixedPointNumber(amtGross.negate()));
	splt1.setQuantity(new FixedPointNumber(amtGross.negate()));
	splt1.setDescription(spltDescr);
	LOGGER.debug("genBuyStockTrx: Split 1 to write: " + splt1.toString());

	// ---
	
	GnucashWritableTransactionSplit splt2 = trx.createWritableSplit(stockAcct);
	splt2.setValue(new FixedPointNumber(amtNet));
	splt2.setQuantity(new FixedPointNumber(nofStocks));
	splt2.setAction(GnucashTransactionSplit.Action.BUY);
	splt2.setDescription(spltDescr);
	LOGGER.debug("genBuyStockTrx: Split 2 to write: " + splt2.toString());

	// ---

	counter = 1;
	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
	    GnucashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
	    GnucashWritableTransactionSplit splt3 = trx.createWritableSplit(expensesAcct);
	    splt3.setValue(new FixedPointNumber(elt.amount()));
	    splt3.setQuantity(new FixedPointNumber(elt.amount()));
	    splt3.setDescription(spltDescr);
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
    
    @SuppressWarnings("exports")
    public static GnucashWritableTransaction genDivivendTrx(
    	    final GnucashWritableFileImpl gcshFile,
    	    final GCshID stockAcctID,
    	    final GCshID incomeAcctID,
    	    final GCshID taxFeeAcctID,
    	    final GCshID offsetAcctID,
    	    final FixedPointNumber divGross,
    	    final FixedPointNumber taxesFees,
    	    final LocalDate postDate,
    	    final String descr) throws UnknownAccountTypeException {
    	Collection<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>();
    	
    	AcctIDAmountPair newPair = new AcctIDAmountPair(taxFeeAcctID, taxesFees);
    	expensesAcctAmtList.add(newPair);
    	
    	return genDivivendTrx(gcshFile, 
    			      stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID, 
    			      divGross, 
    			      postDate, descr);
    }
    
    @SuppressWarnings("exports")
    public static GnucashWritableTransaction genDivivendTrx(
    	    final GnucashWritableFileImpl gcshFile,
    	    final GCshID stockAcctID,
    	    final GCshID incomeAcctID,
    	    final Collection<AcctIDAmountPair> expensesAcctAmtList,
    	    final GCshID offsetAcctID,
    	    final FixedPointNumber divGross,
    	    final LocalDate postDate,
    	    final String descr) throws UnknownAccountTypeException {
    	
    	if ( gcshFile == null ) {
    	    throw new IllegalArgumentException("null KMyMoney file given");
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
    	GnucashAccount stockAcct  = gcshFile.getAccountByID(stockAcctID);
    	if ( stockAcct.getType() != GnucashAccount.Type.STOCK ) {
    	    throw new IllegalArgumentException("Account with ID " + stockAcctID + " is not of type " + GnucashAccount.Type.STOCK);
    	}

    	GnucashAccount incomeAcct = gcshFile.getAccountByID(incomeAcctID);
    	if ( incomeAcct.getType() != GnucashAccount.Type.INCOME ) {
    	    throw new IllegalArgumentException("Account with ID " + incomeAcct + " is not of type " + GnucashAccount.Type.INCOME);
    	}

    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    		GnucashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
    	    if ( expensesAcct.getType() != GnucashAccount.Type.EXPENSE ) {
    		throw new IllegalArgumentException("Account with ID " + elt.accountID() + " is not of type " + GnucashAccount.Type.EXPENSE);
    	    }
    	}
    	
    	GnucashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
    	if ( offsetAcct.getType() != GnucashAccount.Type.BANK ) {
    	    throw new IllegalArgumentException("Account with ID " + offsetAcctID + " is not of type " + GnucashAccount.Type.BANK);
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

    	GnucashWritableTransaction trx = gcshFile.createWritableTransaction();
    	trx.setDescription(descr);

    	// ---
    	
    	String spltDescr = "Generated by DepotTransactionManager, " + LocalDateTime.now();

    	GnucashWritableTransactionSplit splt1 = trx.createWritableSplit(stockAcct);
    	splt1.setValue(new FixedPointNumber());
    	splt1.setQuantity(new FixedPointNumber());
    	splt1.setAction(GnucashTransactionSplit.Action.DIVIDEND);
    	splt1.setDescription(spltDescr);
    	LOGGER.debug("genDivivendTrx: Split 1 to write: " + splt1.toString());

    	// ---

    	GnucashWritableTransactionSplit splt2 = trx.createWritableSplit(offsetAcct);
    	splt2.setValue(new FixedPointNumber(divNet));
    	splt2.setQuantity(new FixedPointNumber(divNet));
    	splt2.setDescription(spltDescr);
    	LOGGER.debug("genDivivendTrx: Split 2 to write: " + splt2.toString());

    	// ---

    	GnucashWritableTransactionSplit splt3 = trx.createWritableSplit(incomeAcct);
    	splt3.setValue(new FixedPointNumber(divGross.negate()));
    	splt3.setQuantity(new FixedPointNumber(divGross.negate()));
    	splt3.setDescription(spltDescr);
    	LOGGER.debug("genDivivendTrx: Split 3 to write: " + splt3.toString());

    	// ---

    	counter = 1;
    	for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
    	    GnucashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
    	    GnucashWritableTransactionSplit splt4 = trx.createWritableSplit(expensesAcct);
    	    splt4.setValue(new FixedPointNumber(elt.amount()));
    	    splt4.setQuantity(new FixedPointNumber(elt.amount()));
    	    splt4.setDescription(descr);
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
