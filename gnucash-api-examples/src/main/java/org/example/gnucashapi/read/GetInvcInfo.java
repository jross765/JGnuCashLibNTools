package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
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
	GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));

	// You normally would get the invoice-ID by first getting
	// the list of invoices/bills for a customer/vendor/job 
	// (cf. GetCustInfo, GetVendInfo, GetJobInfo), getting its
	// list of invoices and then choosing from them.
	GnuCashGenerInvoice invc = gcshFile.getGenerInvoiceByID(invcID);

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
	    if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
		GnuCashCustomerInvoiceImpl spec = new GnuCashCustomerInvoiceImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
		GnuCashVendorBillImpl spec = new GnuCashVendorBillImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
		GnuCashEmployeeVoucherImpl spec = new GnuCashEmployeeVoucherImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
		GnuCashJobInvoiceImpl spec = new GnuCashJobInvoiceImpl(invc);
		System.out.println("toString (spec):   " + spec.toString());
	    }
	} catch (Exception exc) {
	    System.out.println("toString (spec):   " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Owner (dir.):      " + invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println("Owner (dir.):      " + "ERROR");
	}

	try {
	    System.out.println("Owner type:        " + invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT));
	} catch (Exception exc) {
	    System.out.println("Owner type:        " + "ERROR");
	}

	try {
	    if (invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(GnuCashGenerInvoice.TYPE_JOB))
		System.out.println("Owner (via job):   " + invc.getOwnerID(GnuCashGenerInvoice.ReadVariant.VIA_JOB));
	    else
		System.out.println("Owner (via job):   " + "n/a");
	} catch (Exception exc) {
	    System.out.println("Owner (via job):   " + "ERROR");
	}

	try {
	    if (invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT).equals(GnuCashGenerInvoice.TYPE_JOB))
		System.out.println(
			"Owning job's owner type: " + invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB));
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
	    if ( invc.getType() ==  GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount w/o tax:       " + invc.getCustInvcAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount w/o tax:       " + invc.getVendBllAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount w/o tax:       " + invc.getEmplVchAmountWithoutTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Amount w/o tax:       " + invc.getJobInvcAmountWithoutTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount w/o tax:       " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount w/ tax:        " + invc.getCustInvcAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount w/ tax:        " + invc.getVendBllAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount w/ tax:        " + invc.getEmplVchAmountWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Amount w/ tax:        " + invc.getJobInvcAmountWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount w/ tax:        " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount paid w/ tax:   " + invc.getCustInvcAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount paid:          " + invc.getVendBllAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount paid:          " + invc.getEmplVchAmountPaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Amount paid:          " + invc.getJobInvcAmountPaidWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount paid w/ tax:   " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Amount Unpaid w/ tax: " + invc.getCustInvcAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Amount Unpaid:        " + invc.getVendBllAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Amount Unpaid:        " + invc.getEmplVchAmountUnpaidWithTaxesFormatted());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Amount Unpaid:        " + invc.getJobInvcAmountUnpaidWithTaxesFormatted());
	} catch (Exception exc) {
	    System.out.println("Amount Unpaid w/ tax: " + "ERROR");
	}

	try {
	    if ( invc.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Fully paid:           " + invc.isCustInvcFullyPaid());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Fully paid:           " + invc.isVendBllFullyPaid());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE )
		System.out.println("Fully paid:           " + invc.isEmplVchFullyPaid());
	    else if ( invc.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Fully paid:           " + invc.isJobInvcFullyPaid());
	} catch (Exception exc) {
	    System.out.println("Fully paid:           " + "ERROR");
	}

	// ---

	showEntries(invc);
	showTransactions(invc);
    }

    // -----------------------------------------------------------------

    private void showEntries(GnuCashGenerInvoice invc) {
	System.out.println("");
	System.out.println("Entries:");

	for (GnuCashGenerInvoiceEntry entry : invc.getGenerEntries()) {
	    showOneEntry(entry);
	}
    }

    private void showOneEntry(GnuCashGenerInvoiceEntry entry) {
	try {
	    if ( entry.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
		try {
		    GnuCashCustomerInvoiceEntry entrySpec = new GnuCashCustomerInvoiceEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
		try {
		    GnuCashVendorBillEntry entrySpec = new GnuCashVendorBillEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnuCashGenerInvoice.TYPE_EMPLOYEE ) {
		try {
		    GnuCashEmployeeVoucherEntry entrySpec = new GnuCashEmployeeVoucherEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    } else if ( entry.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
		try {
		    GnuCashJobInvoiceEntry entrySpec = new GnuCashJobInvoiceEntryImpl(entry);
		    System.out.println(" - " + entrySpec.toString());
		} catch (Exception exc) {
		    System.out.println(" - " + entry.toString());
		}
	    }
	} catch (WrongInvoiceTypeException e) {
	    System.out.println(" - " + "ERROR");
	}
    }

    private void showTransactions(GnuCashGenerInvoice invc) {
	System.out.println("");
	System.out.println("Transactions:");

	try {
	    System.out.println("Posting transaction: " + invc.getPostTransaction());
	} catch (Exception exc) {
	    System.out.println("Posting transaction: " + "ERROR");
	}

	System.out.println("Paying transactions:");
	for (GnuCashTransaction trx : invc.getPayingTransactions()) {
	    try {
		System.out.println(" - " + trx.toString());
	    } catch (Exception exc) {
		System.out.println(" - " + "ERROR");
	    }
	}
    }
}
