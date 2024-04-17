package org.example.gnucashapi.read;

import java.io.File;
import java.util.Collection;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;

import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;

public class GetCmdtyInfo {

    enum Mode {
	ISIN, 
	EXCHANGE_TICKER, 
	NAME
    }

    // -----------------------------------------------------------------

    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName     = "example_in.gnucash";
    private static Mode   mode             = Mode.ISIN;
    private static GCshCmdtyCurrNameSpace.Exchange exchange = GCshCmdtyCurrNameSpace.Exchange.EURONEXT;
    private static String ticker           = "MBG";
    private static String isin             = "DE0007100000";
    private static String searchName       = "merced";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetCmdtyInfo tool = new GetCmdtyInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));

	GnuCashCommodity cmdty = null;
	if ( mode == Mode.ISIN ) {
	    cmdty = gcshFile.getCommodityByXCode(isin);
	    if (cmdty == null) {
		System.err.println("Could not find commodities with this ISIN.");
		throw new NoEntryFoundException();
	    }
	} else if ( mode == Mode.EXCHANGE_TICKER ) {
	    cmdty = gcshFile.getCommodityByQualifID(exchange, ticker);
	    if (cmdty == null) {
		System.err.println("Could not find commodities with this exchange/ticker.");
		throw new NoEntryFoundException();
	    }
	} else if ( mode == Mode.NAME ) {
	    Collection<GnuCashCommodity> cmdtyList = gcshFile.getCommoditiesByName(searchName);
	    if (cmdtyList.size() == 0) {
		System.err.println("Could not find commodities matching this name.");
		throw new NoEntryFoundException();
	    }
	    if (cmdtyList.size() > 1) {
		System.err.println("Found several commodities with that name.");
		System.err.println("Taking first one.");
	    }
	    cmdty = cmdtyList.iterator().next(); // first element
	}
	
	// ------------------------

	try {
	    System.out.println("Qualified ID:      '" + cmdty.getQualifID() + "'");
	} catch (Exception exc) {
	    System.out.println("Qualified ID:      " + "ERROR");
	}

	try {
	    System.out.println("X-Code:            " + cmdty.getXCode());
	} catch (Exception exc) {
	    System.out.println("X-Code :           " + "ERROR");
	}

	try {
	    System.out.println("Name:              '" + cmdty.getName() + "'");
	} catch (Exception exc) {
	    System.out.println("Name:              " + "ERROR");
	}

	try {
	    System.out.println("Fraction:          " + cmdty.getFraction());
	} catch (Exception exc) {
	    System.out.println("Fraction:          " + "ERROR");
	}

	// ---

	showQuotes(cmdty);
    }

    // -----------------------------------------------------------------

    private void showQuotes(GnuCashCommodity cmdty) {
	System.out.println("");
	System.out.println("Quotes:");

	System.out.println("");
	System.out.println("Number of quotes: " + cmdty.getQuotes().size());

	System.out.println("");
	for (GnuCashPrice prc : cmdty.getQuotes()) {
	    System.out.println(" - " + prc.toString());
	}

	System.out.println("");
	System.out.println("Youngest Quote:");
	System.out.println(cmdty.getYoungestQuote());
    }
}
