package org.example.gnucashapi.read;

import java.io.File;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillEntryImpl;

public class GetInvcEntryInfo {
    // BEGIN Example data -- adapt to your needs
    private static String gcshFileName = "example_in.gnucash";
    private static GCshID invcEntrID   = new GCshID("xyz");
    // END Example data

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GetInvcEntryInfo tool = new GetInvcEntryInfo();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnuCashFileImpl gcshFile = new GnuCashFileImpl(new File(gcshFileName));

	// You normally would get the invoice-entry-ID by first choosing
	// a specific invoice (cf. GetInvcInfo), getting its list of entries
	// and then choosing from them.
	GnuCashGenerInvoiceEntry entr = gcshFile.getGenerInvoiceEntryByID(invcEntrID);

	// ------------------------

	try {
	    System.out.println("ID:                " + entr.getID());
	} catch (Exception exc) {
	    System.out.println("ID:                " + "ERROR");
	}

	try {
	    System.out.println("toString (gener.): " + entr.toString());
	} catch (Exception exc) {
	    System.out.println("toString (gener.): " + "ERROR");
	}

	try {
	    if ( entr.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER ) {
		GnuCashCustomerInvoiceEntryImpl spec = new GnuCashCustomerInvoiceEntryImpl(entr);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( entr.getType() == GnuCashGenerInvoice.TYPE_VENDOR ) {
		GnuCashVendorBillEntryImpl spec = new GnuCashVendorBillEntryImpl(entr);
		System.out.println("toString (spec):   " + spec.toString());
	    } else if ( entr.getType() == GnuCashGenerInvoice.TYPE_JOB ) {
		GnuCashJobInvoiceEntryImpl spec = new GnuCashJobInvoiceEntryImpl(entr);
		System.out.println("toString (spec):   " + spec.toString());
	    }
	} catch (Exception exc) {
	    System.out.println("toString (spec):   " + "ERROR");
	}

	System.out.println("");
	try {
	    System.out.println("Type:              " + entr.getType());
	} catch (Exception exc) {
	    System.out.println("Type:              " + "ERROR");
	}

	try {
	    System.out.println("Gener. Invoice ID: " + entr.getGenerInvoiceID());
	} catch (Exception exc) {
	    System.out.println("Gener. Invoice ID: " + "ERROR");
	}

	try {
	    System.out.println("Action:            " + entr.getAction());
	} catch (Exception exc) {
	    System.out.println("Action:            " + "ERROR");
	}

	try {
	    System.out.println("Description:       '" + entr.getDescription() + "'");
	} catch (Exception exc) {
	    System.out.println("Description:       " + "ERROR");
	}

	System.err.println("");
	System.err.println("Taxes:");
	try {
	    if ( entr.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Taxable:           " + entr.isCustInvcTaxable());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Taxable:           " + entr.isVendBllTaxable());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Taxable:           " + entr.isJobInvcTaxable());
	} catch (Exception exc) {
	    System.out.println("Taxable:           " + "ERROR");
	}

	try {
	    if ( entr.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Tax perc.:         " + entr.getCustInvcApplicableTaxPercentFormatted());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Tax perc.:         " + entr.getVendBllApplicableTaxPercentFormatted());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Tax perc.:         " + entr.getJobInvcApplicableTaxPercentFormatted());
	} catch (Exception exc) {
	    System.out.println("Tax perc.:         " + "ERROR");
	}

	try {
	    System.out.println("Tax-table:");
	    if ( entr.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println(entr.getCustInvcTaxTable().toString());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println(entr.getVendBllTaxTable().toString());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println(entr.getJobInvcTaxTable().toString());
	} catch (Exception exc) {
	    System.out.println("ERROR");
	}

	System.out.println("");
	try {
	    if ( entr.getType() == GnuCashGenerInvoice.TYPE_CUSTOMER )
		System.out.println("Price:             " + entr.getCustInvcPriceFormatted());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_VENDOR )
		System.out.println("Price:             " + entr.getVendBllPriceFormatted());
	    else if ( entr.getType() == GnuCashGenerInvoice.TYPE_JOB )
		System.out.println("Price:             " + entr.getJobInvcPriceFormatted());
	} catch (Exception exc) {
	    System.out.println("Price:             " + "ERROR");
	}

	try {
	    System.out.println("Quantity:          " + entr.getQuantityFormatted());
	} catch (Exception exc) {
	    System.out.println("Quantity:          " + "ERROR");
	}
    }
}
