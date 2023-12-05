package org.example.gnucashapi.write;

import java.io.File;

import org.gnucash.api.read.impl.GnucashCustomerImpl;
import org.gnucash.api.write.GnucashWritableCustomer;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;

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
	GnucashWritableFileImpl gcshFile = new GnucashWritableFileImpl(new File(gcshInFileName));

	GnucashWritableCustomer cust = gcshFile.createWritableCustomer();
	cust.setNumber(GnucashCustomerImpl.getNewNumber(cust));
	cust.setName(name);
	
	System.out.println("Customer to write: " + cust.toString());
	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
