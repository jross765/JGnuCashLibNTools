package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.simple.GCshTrxID;

public class GetTrxInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static GCshTrxID trxID     = new GCshTrxID("xyz");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetTrxInfo tool = new GetTrxInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));

	// You normally would get the transaction-ID by first choosing 
	// a specific account (cf. GetAcctInfo), getting its list of 
	// transactions and then choosing from them.
	GnuCashTransaction trx = gcshFile.getTransactionByID(trxID);

	// ------------------------

	try {
	    System.out.println("ID:              " + trx.getID());
	} catch (Exception exc) {
	    System.out.println("ID:              " + "ERROR");
	}

	try {
	    System.out.println("Balance:         " + trx.getBalanceFormatted());
	} catch (Exception exc) {
	    System.out.println("Balance:         " + "ERROR");
	}

	try {
	    System.out.println("Cmdty/Curr:      '" + trx.getCmdtyCurrID() + "'");
	} catch (Exception exc) {
	    System.out.println("Cmdty/Curr:      " + "ERROR");
	}

	try {
	    System.out.println("Description:     '" + trx.getDescription() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:     " + "ERROR");
	}

	// ---

	showSplits(trx);
    }

    // -----------------------------------------------------------------

    private void showSplits(GnuCashTransaction trx) {
	System.out.println("");
	System.out.println("Splits:");

	for (GnuCashTransactionSplit splt : trx.getSplits()) {
	    System.out.println(" - " + splt.toString());
	}
    }
}
