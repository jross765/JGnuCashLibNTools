package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashEmployeeVoucherImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorBillEntryImpl;
import org.gnucash.api.read.impl.spec.GnucashVendorBillImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.GnucashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

public class GetInvcInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static GCshID invcID       = new GCshID("xyz");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetInvcInfo tool = new GetInvcInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashFileImpl gcshFile = new GnucashFileImpl(new File(gcshFileName));

	// You normally would get the invoice-ID by first getting
	// the list of invoices/bills for a customer/vendor/job 
	// (cf. GetCustInfo, GetVendInfo, GetJobInfo), getting its
	// list of invoices and then choosing from them.
	GnucashGenerInvoice invc = gcshFile.getGenerInvoiceByID(invcID);

	// ------------------------

	try {
	    System.out.println("ID:                " + invc.getID());
	} catch (Exception exc) {
	    System.out.println("ID:                " + "ERROR");
	}

	try {
	    System.out.println("toString (gener.): " + invc.toString());
	} catch (Exception exc) {
	    System.out.println("toString (gener.): " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
		GnucashCustomerInvoiceImpl spec = new GnucashCustomerInvoiceImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
		GnucashVendorBillImpl spec = new GnucashVendorBillImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
		GnucashEmployeeVoucherImpl spec = new GnucashEmployeeVoucherImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB ) {
		GnucashJobInvoiceImpl spec = new GnucashJobInvoiceImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    }
	} catch (Exception exc) {
	    System.out.println("toString (spec):   " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Owner (dir.):      " + invc.getOwnerID(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println("Owner (dir.):      " + "ERROR");
	}

	try {
	    System.out.println("Owner type:        " + invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println("Owner type:        " + "ERROR");
	}

	try {
	    if (invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT).equals(GnucashGenerInvoice.TYPE_JOB))
		System.out.println("Owner (via job):   " + invc.getOwnerID(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	    else
		System.out.println("Owner (via job):   " + "n/a");
	} catch (Exception exc) {
	    System.out.println("Owner (via job):   " + "ERROR");
	}

	try {
	    if (invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT).equals(GnucashGenerInvoice.TYPE_JOB))
		System.out.println(
			"Owning job's owner type: " + invc.getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB));
	    else
		System.out.println("Owning job's owner type: " + "n/a");
	} catch (Exception exc) {
	    System.out.println("Owning job's owner type:   " + "ERROR");
	}

	try {
	    System.out.println("Number:            '" + invc.getNumber() + "'");
	} catch (Exception exc) {
	    System.out.println("Number:            " + "ERROR");
	}

	try {
	    System.out.println("Description:       '" + invc.getDescription() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:       " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Date opened:       " + invc.getDateOpened());
	} catch (Exception exc) {
	    System.out.println("Date opened:       " + "ERROR");
	}

	try {
	    System.out.println("Date posted:       " + invc.getDatePosted());
	} catch (Exception exc) {
	    System.out.println("Date posted:       " + "ERROR");
	}

	System.out.println("");
	try {
	    if ( invc.getType() ==  GnucashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount w/o tax:       " + invc.getInvcAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount w/o tax:       " + invc.getBillAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount w/o tax:       " + invc.getVoucherAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB )
		System.out.println("Amount w/o tax:       " + invc.getJobAmountWithoutTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount w/o tax:       " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount w/ tax:        " + invc.getInvcAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount w/ tax:        " + invc.getBillAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount w/ tax:        " + invc.getVoucherAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB )
		System.out.println("Amount w/ tax:        " + invc.getJobAmountWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount w/ tax:        " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount paid w/ tax:   " + invc.getInvcAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount paid:          " + invc.getBillAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount paid:          " + invc.getVoucherAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB )
		System.out.println("Amount paid:          " + invc.getJobAmountPaidWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount paid w/ tax:   " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount Unpaid w/ tax: " + invc.getInvcAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount Unpaid:        " + invc.getBillAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount Unpaid:        " + invc.getVoucherAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB )
		System.out.println("Amount Unpaid:        " + invc.getJobAmountUnpaidWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount Unpaid w/ tax: " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnucashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Fully paid:           " + invc.isInvcFullyPaid());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_VENDOR )
		System.out.println("Fully paid:           " + invc.isBillFullyPaid());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Fully paid:           " + invc.isVoucherFullyPaid());
	    else if ( invc.getType() == GnucashGenerInvoice.TYPE_JOB )
		System.out.println("Fully paid:           " + invc.isJobFullyPaid());
	} catch (Exception exc) {
	    System.out.println("Fully paid:           " + "ERROR");
	}

	// ---

	showEntries(invc);
	showTransactions(invc);
    }

    // -----------------------------------------------------------------

    private void showEntries(GnucashGenerInvoice invc) {
	System.out.println("");
	System.out.println("Entries:");

	for (GnucashGenerInvoiceEntry entry : invc.getGenerEntries()) {
	    showOneEntry(entry);
	}
    }

    private void showOneEntry(GnucashGenerInvoiceEntry entry) {
	try {
	    if ( entry.getType() == GnucashGenerInvoice.TYPE_CUSTOMER ) {
		try {
		    GnucashCustomerInvoiceEntry entrySpec = new GnucashCustomerInvoiceEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnucashGenerInvoice.TYPE_VENDOR ) {
		try {
		    GnucashVendorBillEntry entrySpec = new GnucashVendorBillEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE ) {
		try {
		    GnucashEmployeeVoucherEntry entrySpec = new GnucashEmployeeVoucherEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnucashGenerInvoice.TYPE_JOB ) {
		try {
		    GnucashJobInvoiceEntry entrySpec = new GnucashJobInvoiceEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    }
	} catch (WrongInvoiceTypeException e) {
	    System.out.println(" - " + "ERROR");
	}
    }

    private void showTransactions(GnucashGenerInvoice invc) {
	System.out.println("");
	System.out.println("Transactions:");

	try {
	    System.out.println("Posting transaction: " + invc.getPostTransaction());
	} catch (Exception exc) {
	    System.out.println("Posting transaction: " + "ERROR");
	}

	System.out.println("Paying transactions:");
	for (GnucashTransaction trx : invc.getPayingTransactions()) {
	    try {
		System.out.println(" - " + trx.toString());
	    } catch (Exception exc) {
		System.out.println(" - " + "ERROR");
	    }
	}
    }
}
