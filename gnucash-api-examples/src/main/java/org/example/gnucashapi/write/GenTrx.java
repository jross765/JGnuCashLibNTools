package org.example.gnucashapi.write;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;

public class GenTrx {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName     = "example_in.gnucash";
    private static String gcshOutFileName    = "example_out.gnucash";
    
    // ---
    
    private static GCshID           fromAcct1ID = new GCshID("e617cfd1317b4e318caf5dfba51b172e"); // Root Account:Aktiva:Kassen:Kasse Ada
    private static GCshID           toAcct1ID   = new GCshID("0c405f3669a14606be3c1a62ac5455a9"); // Root Account:Aufwendungen:Bildung:Zeitungen
    private static GnucashTransactionSplit.Action act1 = null;                                    // Do not set here
    private static FixedPointNumber amt1        = new FixedPointNumber("1234/100");
    private static FixedPointNumber qty1        = amt1;
    private static LocalDate        datPst1     = LocalDate.of(2024, 2, 15);
    private static String           descr1      = "Bahnhof Zeitungskiosk";
    
    // ---
    
    // Following two account IDs: sic, not KMMComplAcctID
    private static GCshID           fromAcct2ID  = new GCshID("bbf77a599bd24a3dbfec3dd1d0bb9f5c"); // Root Account:Aktiva:Sichteinlagen:KK:Giro RaiBa
    private static GCshID           toAcct21ID   = new GCshID("b3741e92e3b9475b9d5a2dc8254a8111"); // Root Account:Aktiva:Depots:Depot RaiBa:DE0007164600 SAP
    private static GCshID           toAcct22ID   = new GCshID("d3f947fdfbf54240b0cfb09fea4963ca"); // Root Account:Aufwendungen:Sonstiges:Bankgebühren
    private static GnucashTransactionSplit.Action act2 = GnucashTransactionSplit.Action.BUY;
    private static FixedPointNumber qty22        = new FixedPointNumber("15");
    // private static FixedPointNumber prc1         = new FixedPointNumber("1/1");       // optional
    private static FixedPointNumber prc2         = new FixedPointNumber("15574/100"); // half-mandatory
    // private static FixedPointNumber prc3         = prc1;                              // optional
    private static FixedPointNumber amt22        = qty22.copy().multiply(prc2);       // net
    private static FixedPointNumber amt23        = new FixedPointNumber("95/10");     // fees & commissions
    private static FixedPointNumber amt21        = amt22.copy().add(amt23);           // gross
    private static FixedPointNumber qty21        = amt21;
    private static FixedPointNumber qty23        = amt23;
    private static LocalDate        datPst2      = LocalDate.of(2024, 1, 15);
    private static String           descr2       = "Aktienkauf";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenTrx tool = new GenTrx();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashWritableFileImpl gcshFile = new GnucashWritableFileImpl(new File(gcshInFileName));

	System.out.println("---------------------------");
	System.out.println("Generate transaction no. 1:");
	System.out.println("---------------------------");
	genTrx1(gcshFile);

	System.out.println("");
	System.out.println("---------------------------");
	System.out.println("Generate transaction no. 2:");
	System.out.println("---------------------------");
	genTrx2(gcshFile);
	
	System.out.println("");
	System.out.println("---------------------------");
	System.out.println("Write file:");
	System.out.println("---------------------------");
	gcshFile.writeFile(new File(gcshOutFileName));
	
	System.out.println("OK");
    }

    private void genTrx1(GnucashWritableFileImpl gcshFile) {
	System.err.println("Account 1 name (from): '" + gcshFile.getAccountByID(fromAcct1ID).getQualifiedName() + "'");
	System.err.println("Account 2 name (to):   '" + gcshFile.getAccountByID(toAcct1ID).getQualifiedName() + "'");

	GnucashWritableTransaction trx = gcshFile.createWritableTransaction();
	trx.setDescription(descr1);

	GnucashWritableTransactionSplit splt1 = trx.createWritableSplit(gcshFile.getAccountByID(fromAcct1ID));
	splt1.setValue(new FixedPointNumber(amt1.negate()));
	splt1.setQuantity(new FixedPointNumber(qty1.negate()));

	GnucashWritableTransactionSplit splt2 = trx.createWritableSplit(gcshFile.getAccountByID(toAcct1ID));
	splt2.setValue(new FixedPointNumber(amt1));
	splt2.setQuantity(new FixedPointNumber(qty1));

	trx.setDatePosted(datPst1);
	trx.setDateEntered(LocalDateTime.now());

	System.out.println("Transaction to write: " + trx.toString());
    }
    
    private void genTrx2(GnucashWritableFileImpl gcshFile) {
	System.err.println("Account 1 name (from): '" + gcshFile.getAccountByID(fromAcct2ID).getQualifiedName() + "'");
	System.err.println("Account 2 name (to):   '" + gcshFile.getAccountByID(toAcct21ID).getQualifiedName() + "'");
	System.err.println("Account 3 name (to):   '" + gcshFile.getAccountByID(toAcct22ID).getQualifiedName() + "'");

	// ---

	GnucashWritableTransaction trx = gcshFile.createWritableTransaction();
	trx.setDescription(descr2);

	// ---

	GnucashWritableTransactionSplit splt1 = trx.createWritableSplit(gcshFile.getAccountByID(fromAcct2ID));
	splt1.setValue(new FixedPointNumber(amt21.negate()));
	splt1.setQuantity(new FixedPointNumber(qty21.negate()));
	splt1.setDescription("Abrechnung");
	System.out.println("Split 1 to write: " + splt1.toString());

	// ---

	GnucashWritableTransactionSplit splt2 = trx.createWritableSplit(gcshFile.getAccountByID(toAcct21ID));
	splt2.setValue(new FixedPointNumber(amt22));
	splt2.setQuantity(new FixedPointNumber(qty22));
	splt2.setAction(act2);
	splt2.setDescription("Kauf SAP");
	System.out.println("Split 2 to write: " + splt2.toString());

	// ---

	GnucashWritableTransactionSplit splt3 = trx.createWritableSplit(gcshFile.getAccountByID(toAcct22ID));
	splt3.setValue(new FixedPointNumber(amt23));
	splt3.setQuantity(new FixedPointNumber(qty23));
	splt3.setDescription("Bankgebühren");
	System.out.println("Split 3 to write: " + splt3.toString());

	// ---

	trx.setDatePosted(datPst2);
	trx.setDateEntered(LocalDateTime.now());

	// ---

	System.out.println("Transaction to write: " + trx.toString());
    }
    
}
