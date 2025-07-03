package org.example.gnucashapi.write.gen.complex;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apiext.secacct.SecuritiesAccountTransactionManager;
import org.gnucash.apiext.secacct.SecuritiesAccountTransactionManager.Type;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.tuples.AcctIDAmountPair;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GenDepotTrx {
	// BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";

	private static SecuritiesAccountTransactionManager.Type type = Type.DIVIDEND;

	private static GCshID stockAcctID  = new GCshID( "b3741e92e3b9475b9d5a2dc8254a8111" );
	private static GCshID incomeAcctID = new GCshID( "d7c384bfc136464490965f3f254313b1" ); // only for dividend, not for buy/sell
	private static List<AcctIDAmountPair> expensesAcctAmtList = new ArrayList<AcctIDAmountPair>(); // only for dividend, not for buy/sell
	private static GCshID offsetAcctID = new GCshID( "bbf77a599bd24a3dbfec3dd1d0bb9f5c" );
	
	private static FixedPointNumber nofStocks = new FixedPointNumber(15); // only for buy/sell, not for dividend
	private static FixedPointNumber stockPrc  = new FixedPointNumber("23080/100"); // only for buy/sell, not for dividend
	private static FixedPointNumber divGross  = new FixedPointNumber("11223/100"); // only for dividend, not for buy/sell

	private static LocalDate datPst = LocalDate.of(2024, 3, 1);
	private static String descr = "Dividend payment";
	// END Example data

    // -----------------------------------------------------------------

	public static void main(String[] args) {
		try {
			GenDepotTrx tool = new GenDepotTrx();
			tool.kernel();
		} catch (Exception exc) {
			System.err.println("Execution exception. Aborting.");
			exc.printStackTrace();
			System.exit(1);
		}
	}

	protected void kernel() throws Exception {
		GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));

		GnuCashAccount stockAcct = gcshFile.getAccountByID(stockAcctID);
		if ( stockAcct == null )
			System.err.println("Error: Cannot get account with ID '" + stockAcctID + "'");

		GnuCashAccount incomeAcct = null;
		if ( incomeAcctID != null ) {
			incomeAcct = gcshFile.getAccountByID(incomeAcctID);
			if ( incomeAcct == null )
				System.err.println("Error: Cannot get account with ID '" + incomeAcctID + "'");
		}

		for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
			GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
			if ( expensesAcct == null )
				System.err.println("Error: Cannot get account with ID '" + elt.accountID() + "'");
		}

		GnuCashAccount offsetAcct = gcshFile.getAccountByID(offsetAcctID);
		if ( offsetAcct == null )
			System.err.println("Error: Cannot get account with ID '" + offsetAcctID + "'");

		System.err.println("Account 1 name (stock):      '" + stockAcct.getQualifiedName() + "'");
		if ( incomeAcctID != null )
			System.err.println("Account 2 name (income):     '" + incomeAcct.getQualifiedName() + "'");

		int counter = 1;
		for ( AcctIDAmountPair elt : expensesAcctAmtList ) {
			GnuCashAccount expensesAcct = gcshFile.getAccountByID(elt.accountID());
			System.err.println("Account 3." + counter + " name (expenses): '" + expensesAcct.getQualifiedName() + "'");
			counter++;
		}

		System.err.println("Account 4 name (offsetting): '" + offsetAcct.getQualifiedName() + "'");

		// ---

		GnuCashWritableTransaction trx = null;
		initExpAccts();
		if ( type == SecuritiesAccountTransactionManager.Type.BUY_STOCK ) {
			trx = SecuritiesAccountTransactionManager
					.genBuyStockTrx(gcshFile, 
									stockAcctID, expensesAcctAmtList, offsetAcctID,
									nofStocks, stockPrc, 
									datPst, descr);
		} else if ( type == SecuritiesAccountTransactionManager.Type.DIVIDEND ) {
			trx = SecuritiesAccountTransactionManager
					.genDividDistribTrx(gcshFile,
										stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID,
										GnuCashTransactionSplit.Action.DIVIDEND, divGross, datPst,
										descr);
		} else if ( type == SecuritiesAccountTransactionManager.Type.DISTRIBUTION ) {
			trx = SecuritiesAccountTransactionManager
					.genDividDistribTrx(gcshFile,
										stockAcctID, incomeAcctID, expensesAcctAmtList, offsetAcctID,
										GnuCashTransactionSplit.Action.DIST, divGross, datPst,
										descr);
		}

		// ---

		System.out.println("Transaction to write: " + trx.toString());
		gcshFile.writeFile(new File(gcshOutFileName));

		System.out.println("OK");
	}
	
	// Example for a dividend payment in Germany (domestic share).
	// If we had a foreign share (e.g. US), we would have to add a 
	// third entry to the list: "Auslaend. Quellensteuer" (that 
	// account is not in the test file yet).
	private void initExpAccts() {
		GCshID expAcct1 = new GCshID( "2a195872e24048a0a6228107ca8b6a52" ); // Kapitalertragsteuer
		FixedPointNumber amt1 = divGross.copy().multiply(new FixedPointNumber("25/100"));
		AcctIDAmountPair acctAmtPr1 = new AcctIDAmountPair(expAcct1, amt1);
		expensesAcctAmtList.add(acctAmtPr1);
		
		GCshID expAcct2 = new GCshID( "41e998de2af144c7a9db5049fb677f8a" ); // Soli
		FixedPointNumber amt2 = amt1.copy().multiply(new FixedPointNumber("55/100"));
		AcctIDAmountPair acctAmtPr2 = new AcctIDAmountPair(expAcct2, amt2);
		expensesAcctAmtList.add(acctAmtPr2);
	}
}
