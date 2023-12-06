package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.impl.GnucashFileImpl;

public class GetTrxInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static GCshID trxID        = new GCshID("xyz");
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
	GnucashFileImpl gcshFile = new GnucashFileImpl(new File(gcshFileName));

	// You normally would get the transaction-ID by first choosing 
	// a specific account (cf. GetAcctInfo), getting its list of 
	// transactions and then choosing from them.
	GnucashTransaction trx = gcshFile.getTransactionByID(trxID);

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

    private void showSplits(GnucashTransaction trx) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	System.out.println("");
	System.out.println("Splits:");

	for (GnucashTransactionSplit splt : trx.getSplits()) {
	    System.out.println(" - " + splt.toString());
	}
    }
}
