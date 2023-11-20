package org.example.gnucash.read;

import java.io.File;
import java.util.Collection;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.NoEntryFoundException;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.impl.GnucashFileImpl;
import org.gnucash.read.spec.GnucashEmployeeJob;
import org.gnucash.read.spec.GnucashEmployeeVoucher;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.WrongInvoiceTypeException;

public class GetEmplInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static Helper.Mode mode    = Helper.Mode.ID;
    private static GCshID emplID       = new GCshID("xyz");
    private static String emplName     = "abc";
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetEmplInfo tool = new GetEmplInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashFileImpl gcshFile = new GnucashFileImpl(new File(gcshFileName));

	GnucashEmployee empl = gcshFile.getEmployeeByID(emplID);
	if ( mode == Helper.Mode.ID ) {
	    empl = gcshFile.getEmployeeByID(emplID);
	    if (empl == null) {
		System.err.println("Found no account with that ID");
		throw new NoEntryFoundException();
	    }
	} else if ( mode == Helper.Mode.NAME ) {
	    Collection<GnucashEmployee> emplList = null;
	    emplList = gcshFile.getEmployeesByUserName(emplName, true);
	    if (emplList.size() == 0) {
		System.err.println("Found no account with that name.");
		throw new NoEntryFoundException();
	    } else if (emplList.size() > 1) {
		System.err.println("Found several accounts with that name.");
		System.err.println("Taking first one.");
	    }
	    empl = emplList.iterator().next(); // first element
	}
	
	// ------------------------

	try {
	    System.out.println("ID:                " + empl.getId());
	} catch (Exception exc) {
	    System.out.println("ID:                " + "ERROR");
	}

	try {
	    System.out.println("Number:            '" + empl.getNumber() + "'");
	} catch (Exception exc) {
	    System.out.println("Number:            " + "ERROR");
	}

	try {
	    System.out.println("User name:         '" + empl.getUserName() + "'");
	} catch (Exception exc) {
	    System.out.println("User name:         " + "ERROR");
	}

	try {
	    System.out.println("Address:           '" + empl.getAddress() + "'");
	} catch (Exception exc) {
	    System.out.println("Address:           " + "ERROR");
	}

	System.out.println("");
	System.out.println("Expenses generated:");
	try {
	    System.out
		    .println(" - direct:  " + empl.getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println(" - direct:  " + "ERROR");
	}

	try {
	    System.out.println(
		    " - via all jobs:  " + empl.getExpensesGeneratedFormatted(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	} catch (Exception exc) {
	    System.out.println(" - via all jobs:  " + "ERROR");
	}

	System.out.println("Outstanding value:");
	try {
	    System.out
		    .println(" - direct: " + empl.getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println(" - direct: " + "ERROR");
	}

	try {
	    System.out.println(
		    " - via all jobs: " + empl.getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	} catch (Exception exc) {
	    System.out.println(" - via all jobs: " + "ERROR");
	}

	// ---

	showJobs(empl);
	showVouchers(empl);
    }

    // -----------------------------------------------------------------

    private void showJobs(GnucashEmployee empl) throws WrongInvoiceTypeException {
	System.out.println("");
	System.out.println("Jobs:");
	for (GnucashEmployeeJob job : empl.getJobs()) {
	    System.out.println(" - " + job.toString());
	}
    }

    private void showVouchers(GnucashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	System.out.println("");
	System.out.println("Vouchers:");

	System.out.println("Number of open vouchers: " + empl.getNofOpenVouchers());

	System.out.println("");
	System.out.println("Paid vouchers (direct):");
	for (GnucashEmployeeVoucher vch : empl.getPaidVouchers_direct()) {
	    System.out.println(" - " + vch.toString());
	}

	System.out.println("");
	System.out.println("Paid vouchers (via all jobs):");
	for (GnucashJobInvoice invc : empl.getPaidVouchers_viaAllJobs()) {
	    System.out.println(" - " + invc.toString());
	}

	System.out.println("");
	System.out.println("Unpaid vouchers (direct):");
	for (GnucashEmployeeVoucher vch : empl.getUnpaidVouchers_direct()) {
	    System.out.println(" - " + vch.toString());
	}

	System.out.println("");
	System.out.println("Unpaid vouchers (via all jobs):");
	for (GnucashJobInvoice invc : empl.getUnpaidVouchers_viaAllJobs()) {
	    System.out.println(" - " + invc.toString());
	}
    }
}
