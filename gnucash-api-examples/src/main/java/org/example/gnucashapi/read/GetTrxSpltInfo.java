package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.impl.GnuCashFileImpl;

public class GetTrxSpltInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static GCshID spltID       = new GCshID("xyz");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetTrxSpltInfo tool = new GetTrxSpltInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));

	// You normally would get the transaction-split-ID by first choosing
	// a specific transaction (cf. GetTrxInfo), getting its list of splits
	// and then choosing from them.
	GnuCashTransactionSplit splt = gcshFile.getTransactionSplitByID(spltID);
	
	// ------------------------

	try {
	    System.out.println("ID:          " + splt.getID());
	} catch (Exception exc) {
	    System.out.println("ID:          " + "ERROR");
	}

	try {
	    System.out.println("Account ID:  " + splt.getAccountID());
	} catch (Exception exc) {
	    System.out.println("Account ID:  " + "ERROR");
	}

	try {
	    System.out.println("Lot:         " + splt.getLotID());
	} catch (Exception exc) {
	    System.out.println("Lot:         " + "ERROR");
	}

	try {
	    System.out.println("Action (code): " + splt.getAction());
	} catch (Exception exc) {
	    System.out.println("Action (code): " + "ERROR");
	}

	try {
	    System.out.println("Action (str): " + splt.getActionStr());
	} catch (Exception exc) {
	    System.out.println("Action (str): " + "ERROR");
	}

	try {
	    System.out.println("Value:       " + splt.getValueFormatted());
	} catch (Exception exc) {
	    System.out.println("Value:       " + "ERROR");
	}

	try {
	    System.out.println("Quantity:    " + splt.getQuantityFormatted());
	} catch (Exception exc) {
	    System.out.println("Quantity:    " + "ERROR");
	}

	try {
	    System.out.println("Description: '" + splt.getDescription() + "'");
	} catch (Exception exc) {
	    System.out.println("Description: " + "ERROR");
	}
    }
}
