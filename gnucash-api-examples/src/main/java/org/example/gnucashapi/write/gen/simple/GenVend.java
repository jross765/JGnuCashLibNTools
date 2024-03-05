package org.example.gnucashapi.write.gen.simple;

import java.io.File;

import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.write.GnuCashWritableVendor;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

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
	GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));

	GnuCashWritableVendor vend = gcshFile.createWritableVendor();
	vend.setNumber(GnuCashVendorImpl.getNewNumber(vend));
	vend.setName(name);

	System.out.println("Vendor to write: " + vend.toString());
	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
