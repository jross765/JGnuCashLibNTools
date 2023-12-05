package org.example.gnucashapi.write;

import java.io.File;

import org.gnucash.api.read.impl.GnucashEmployeeImpl;
import org.gnucash.api.write.GnucashWritableEmployee;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;

public class GenEmpl {
    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName  = "example_in.gnucash";
    private static String gcshOutFileName = "example_out.gnucash";
    private static String userName        = "emplomatic";
    private static String name            = "Emplomatic 2000";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenEmpl tool = new GenEmpl();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashWritableFileImpl gcshFile = new GnucashWritableFileImpl(new File(gcshInFileName));

	GnucashWritableEmployee empl = gcshFile.createWritableEmployee();
	empl.setNumber(GnucashEmployeeImpl.getNewNumber(empl));
	empl.setUserName(userName);
	empl.getAddress().setAddressName(name);

	System.out.println("Employee to write: " + empl.toString());
	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }
}
