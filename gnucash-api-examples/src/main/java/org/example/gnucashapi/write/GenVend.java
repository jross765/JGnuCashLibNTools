package org.example.gnucashapi.write;

import java.io.File;

import org.gnucash.api.read.impl.GnucashVendorImpl;
import org.gnucash.api.write.GnucashWritableVendor;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;

public class GenVend {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    private static String name            = "Vendorix the Great";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenVend tool = new GenVend();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashWritableFileImpl gcshFile = new GnucashWritableFileImpl(new File(gcshInFileName));

	GnucashWritableVendor vend = gcshFile.createWritableVendor();
	vend.setNumber(GnucashVendorImpl.getNewNumber(vend));
	vend.setName(name);

	System.out.println("Vendor to write: " + vend.toString());
	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
