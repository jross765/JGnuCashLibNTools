package org.example.gnucashapi.write.gen.simple;

import java.io.File;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.write.GnuCashWritableAccount;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace.SecIdType;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_SecIdType;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;

public class GenAcct
{
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    
    private static String                name1        = "FlixIt";
    private static GnuCashAccount.Type   type1        = GnuCashAccount.Type.EXPENSE;
    private static GCshCurrID            currID1      = new GCshCurrID("EUR");
    private static GCshID                parentID1    = new GCshID("aa8e4dac1bd141468c1eca045598a52b"); // Fernsehen
    
    private static String                name2        = "Depot Sparkasse";
    private static GnuCashAccount.Type   type2        = GnuCashAccount.Type.STOCK;
    private static GCshCmdtyID_SecIdType cmdtyID2     = new GCshCmdtyID_SecIdType(SecIdType.ISIN, "DE9876543210");
    private static GCshID                parentID2    = new GCshID("7ee6fe4de6db46fd957f3513c9c6f983"); // Depots
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
    	try {
    		GenAcct tool = new GenAcct();
    		tool.kernel();
    	} catch (Exception exc) {
    		System.err.println("Execution exception. Aborting.");
    		exc.printStackTrace();
    		System.exit(1);
    	}
    }

    protected void kernel() throws Exception
    {
    	GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));

    	GnuCashWritableAccount acct1 = gcshFile.createWritableAccount(type1, currID1, parentID1, name1);
    	System.out.println("Account to write: " + acct1.toString());
    	gcshFile.writeFile(new File(gcshOutFileName));
    	System.out.println("OK");

    	System.out.println("");
    	GnuCashWritableAccount acct2 = gcshFile.createWritableAccount(type2, cmdtyID2, parentID2, name2);
    	System.out.println("Account to write: " + acct2.toString());
    	gcshFile.writeFile(new File(gcshOutFileName));
    	System.out.println("OK");
    }
  
}
