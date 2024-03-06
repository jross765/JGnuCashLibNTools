package org.example.gnucashapi.write.gen.simple;

import java.io.File;

import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.write.GnuCashWritableCustomer;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

public class GenCust {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    private static String name            = "Customatrix jr.";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenCust tool = new GenCust();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));

	GnuCashWritableCustomer cust = gcshFile.createWritableCustomer(name);
	cust.setNumber(GnuCashCustomerImpl.getNewNumber(cust));
	
	System.out.println("Customer to write: " + cust.toString());
	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
