package org.example.gnucashapi.write.gen.simple;

import java.io.File;

import org.gnucash.api.read.impl.GnuCashEmployeeImpl;
import org.gnucash.api.write.GnuCashWritableEmployee;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;

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
		GnuCashWritableFileImpl gcshFile = new GnuCashWritableFileImpl(new File(gcshInFileName));

		GnuCashWritableEmployee empl = gcshFile.createWritableEmployee(userName);
		empl.setNumber(GnuCashEmployeeImpl.getNewNumber(empl));
		empl.getAddress().setName(name);

		System.out.println("Employee to write: " + empl.toString());
		gcshFile.writeFile(new File(gcshOutFileName));
		System.out.println("OK");
    }
}
