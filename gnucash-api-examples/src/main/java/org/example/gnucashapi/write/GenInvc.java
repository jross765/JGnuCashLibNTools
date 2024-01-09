package org.example.gnucashapi.write;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.AccountNotFoundException;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.OwnerNotFoundException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.WrongAccountTypeException;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.gnucash.api.write.spec.GnucashWritableVendorBillEntry;

public class GenInvc {
    
    enum InvoiceType {
	CUSTOMER, 
	VENDOR,
	EMPLOYEE,
	JOB
    }

    // -----------------------------------------------------------------

    // BEGIN Example data -- adapt to your needs
    private static String gcshInFileName    = "example_in.gnucash";
    private static String gcshOutFileName   = "example_out.gnucash";
    private static InvoiceType type         = InvoiceType.CUSTOMER;
    private static GCshID custID            = new GCshID("1d2081e8a10e4d5e9312d9fff17d470d");
    private static GCshID vendID            = new GCshID("bc1c7a6d0a6c4b4ea7dd9f8eb48f79f7");
    private static GCshID emplID            = new GCshID("7f70b352dcf44a5d8085767a53a9bc37");
    private static GCshID job1ID            = new GCshID("e91b99cd6fbb48a985cbf1e8041f378c"); // customer job
    private static GCshID job2ID            = new GCshID("028cfb5993ef4d6b83206bc844e2fe56"); // vendor job
    private static GCshID incAcctID         = new GCshID("fed745c4da5c49ebb0fde0f47222b35b"); // Root Account:Erträge:Sonstiges
    private static GCshID expAcctID         = new GCshID("7d4c7bf08901493ab346cc24595fdb97"); // Root Account:Aufwendungen:Sonstiges
    private static GCshID recvblAcctID      = new GCshID("ee7561449e61448fb8fefdc27a35d559"); // Root Account:Aktiva:Forderungen:sonstige
    private static GCshID paybleAcctID      = new GCshID("55711b4e6f564709bf880f292448237a"); // Root Account:Fremdkapital:Lieferanten:sonstige
    private static String number            = "1234";
    private static LocalDate dateOpen       = LocalDate.now();
    private static LocalDate datePost       = LocalDate.now();
    private static LocalDate dateDue        = LocalDate.now();
    private static FixedPointNumber amount  = new FixedPointNumber("1250/100");
    // END Example data

    // -----------------------------------------------------------------

    private static GnucashAccount incAcct    = null;
    private static GnucashAccount expAcct    = null;
    private static GnucashAccount recvblAcct = null;
    private static GnucashAccount payblAcct  = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) {
	try {
	    GenInvc tool = new GenInvc();
	    tool.kernel();
	} catch (Exception exc) {
	    System.err.println("Execution exception. Aborting.");
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    protected void kernel() throws Exception {
	GnucashWritableFileImpl gcshFile = new GnucashWritableFileImpl(new File(gcshInFileName));

	try {
	    incAcct = gcshFile.getAccountByID(incAcctID);
	    System.err.println("Income account:     " + "Code: " + incAcct.getCode() + ", " + "Type: "
		    + incAcct.getType() + ", " + "Name: '" + incAcct.getQualifiedName() + "'");

	    if ( ! incAcct.getType().equals(GnucashAccount.Type.INCOME) ) {
		System.err.println("Error: Account is not an income account");
		throw new WrongAccountTypeException();
	    }
	} catch (Exception exc) {
	    System.err.println("Error: Could not instantiate account with ID '" + incAcctID + "'");
	    throw new AccountNotFoundException();
	}

	try {
	    expAcct = gcshFile.getAccountByID(expAcctID);
	    System.err.println("Expenses account:     " + "Code: " + expAcct.getCode() + ", " + "Type: "
		    + expAcct.getType() + ", " + "Name: '" + expAcct.getQualifiedName() + "'");

	    if ( ! expAcct.getType().equals(GnucashAccount.Type.EXPENSE) ) {
		System.err.println("Error: Account is not an expenses account");
		throw new WrongAccountTypeException();
	    }
	} catch (Exception exc) {
	    System.err.println("Error: Could not instantiate account with ID '" + expAcctID + "'");
	    throw new AccountNotFoundException();
	}

	try {
	    recvblAcct = gcshFile.getAccountByID(recvblAcctID);
	    System.err.println("Accounts-receivable account: " + "Code: " + recvblAcct.getCode() + ", " + "Type: "
		    + recvblAcct.getType() + ", " + "Name: '" + recvblAcct.getQualifiedName() + "'");

	    if ( ! recvblAcct.getType().equals(GnucashAccount.Type.RECEIVABLE) ) {
		System.err.println("Error: Account is not an accounts-receivable account");
		throw new WrongAccountTypeException();
	    }
	} catch (Exception exc) {
	    System.err.println("Error: Could not instantiate account with ID '" + recvblAcctID + "'");
	    throw new AccountNotFoundException();
	}

	try {
	    payblAcct = gcshFile.getAccountByID(paybleAcctID);
	    System.err.println("Accounts-payable account: " + "Code: " + payblAcct.getCode() + ", " + "Type: "
		    + payblAcct.getType() + ", " + "Name: '" + payblAcct.getQualifiedName() + "'");

	    if ( ! payblAcct.getType().equals(GnucashAccount.Type.PAYABLE) ) {
		System.err.println("Error: Account is not an accounts-payable account");
		throw new WrongAccountTypeException();
	    }
	} catch (Exception exc) {
	    System.err.println("Error: Could not instantiate account with ID '" + paybleAcctID + "'");
	    throw new AccountNotFoundException();
	}

	GnucashGenerInvoice invc1 = null;
	GnucashGenerInvoice invc2 = null;
	
	if ( type == InvoiceType.CUSTOMER ) {
	    invc1 = doCustomer(gcshFile);
	    System.out.println("Invoice to write: " + invc1.toString());
	}
	else if ( type == InvoiceType.VENDOR ) {
	    invc1 = doVendor(gcshFile);
	    System.out.println("Invoice to write: " + invc1.toString());
	}
	else if ( type == InvoiceType.EMPLOYEE ) {
	    invc1 = doEmployee(gcshFile);
	    System.out.println("Invoice to write: " + invc1.toString());
	}
	else if ( type == InvoiceType.JOB ) {
	    invc1 = doJob_cust(gcshFile);
	    System.out.println("Invoice to write (1): " + invc1.toString());

	    invc2 = doJob_vend(gcshFile);
	    System.out.println("Invoice to write (2): " + invc2.toString());
	}

	gcshFile.writeFile(new File(gcshOutFileName));
	System.out.println("OK");
    }

    // -----------------------------------------------------------------

    private GnucashWritableCustomerInvoice doCustomer(GnucashWritableFileImpl gcshFile) throws Exception {
	GnucashCustomer cust = null;
	try {
	    cust = gcshFile.getWritableCustomerByID(custID);
	    System.err.println("Customer: " + cust.getNumber() + " (" + cust.getName() + ")");
	} catch (Exception exc) {
	    System.err.println("Error: No customer with ID '" + custID + "' found");
	    throw new OwnerNotFoundException();
	}

	GnucashWritableCustomerInvoice invc = gcshFile.createWritableCustomerInvoice(number, cust, 
								incAcct, recvblAcct, 
								dateOpen, datePost, dateDue);
	invc.setDescription("Generated by GenInvc " + LocalDateTime.now().toString());

	GnucashWritableCustomerInvoiceEntry entry1 = invc.createEntry(incAcct, 
		                                                      new FixedPointNumber(amount),
		                                                      new FixedPointNumber(1));
	entry1.setAction(GnucashGenerInvoiceEntry.Action.JOB);
	entry1.setDescription("Entry no. 1");
	entry1.setDate(dateOpen.minus(1, ChronoUnit.DAYS));

	GnucashWritableCustomerInvoiceEntry entry2 = invc.createEntry(incAcct, 
		                                                      new FixedPointNumber(amount),
		                                                      new FixedPointNumber(1), 
		                                                      "DE_USt_Std");
	entry2.setAction(GnucashGenerInvoiceEntry.Action.HOURS);
	entry2.setDescription("Entry no. 2");
	entry2.setDate(dateOpen);

	GnucashWritableCustomerInvoiceEntry entry3 = invc.createEntry(incAcct, 
		                                                      new FixedPointNumber(amount),
		                                                      new FixedPointNumber(1), 
		                                                      gcshFile.getTaxTableByName("FR_TVA_Std"));
	entry3.setAction(GnucashGenerInvoiceEntry.Action.MATERIAL);
	entry3.setDescription("Entry no. 3");
	entry3.setDate(dateOpen.plus(1, ChronoUnit.DAYS));

	// CAUTION: Posting an invoice directly after its generation 
	// will not work in the current version.  
	// Cf. the "Known Issues" section in the project README
	// invc.post(incAcct, recvblAcct, datePost, dateDue);

	return invc;
    }

    private GnucashWritableVendorBill doVendor(GnucashWritableFileImpl gcshFile) throws Exception {
	GnucashVendor vend = null;
	try {
	    vend = gcshFile.getVendorByID(vendID);
	    System.err.println("Vendor: " + vend.getNumber() + " (" + vend.getName() + ")");
	} catch (Exception exc) {
	    System.err.println("Error: No vendor with ID '" + vendID + "' found");
	    throw new OwnerNotFoundException();
	}

	GnucashWritableVendorBill bll = gcshFile.createWritableVendorBill(number, vend, 
							expAcct, payblAcct,
							dateOpen, datePost, dateDue);
	bll.setDescription("Generated by GenInvc " + LocalDateTime.now().toString());

	GnucashWritableVendorBillEntry entry1 = bll.createEntry(expAcct, 
		                                                new FixedPointNumber(amount),
		                                                new FixedPointNumber(1));
	entry1.setAction(GnucashGenerInvoiceEntry.Action.JOB);
	entry1.setDescription("Entry no. 1");
	entry1.setDate(dateOpen.minus(1, ChronoUnit.DAYS));

	GnucashWritableVendorBillEntry entry2 = bll.createEntry(expAcct, 
		                                                new FixedPointNumber(amount),
		                                                new FixedPointNumber(1), 
		                                                "DE_USt_Std");
	entry2.setAction(GnucashGenerInvoiceEntry.Action.HOURS);
	entry2.setDescription("Entry no. 2");
	entry2.setDate(dateOpen);

	GnucashWritableVendorBillEntry entry3 = bll.createEntry(expAcct, 
                                                                new FixedPointNumber(amount),
                                                                new FixedPointNumber(1), 
                                                                gcshFile.getTaxTableByName("FR_TVA_Std"));
	entry3.setAction(GnucashGenerInvoiceEntry.Action.MATERIAL);
	entry3.setDescription("Entry no. 3");
	entry3.setDate(dateOpen.plus(1, ChronoUnit.DAYS));

	// CAUTION: Posting an bill directly after its generation 
	// will not work in the current version.  
	// Cf. the "Known Issues" section in the project README
	// bll.post(expAcct, payblAcct, datePost, dateDue);

	return bll;
    }

    private GnucashWritableEmployeeVoucher doEmployee(GnucashWritableFileImpl gcshFile) throws Exception {
	GnucashEmployee empl = null;
	try {
	    empl = gcshFile.getEmployeeByID(emplID);
	    System.err.println("Employee: " + empl.getNumber() + " (" + empl.getUserName() + ")");
	} catch (Exception exc) {
	    System.err.println("Error: No employee with ID '" + emplID + "' found");
	    throw new OwnerNotFoundException();
	}

	GnucashWritableEmployeeVoucher vch = gcshFile.createWritableEmployeeVoucher(number, empl, 
							expAcct, payblAcct,
							dateOpen, datePost, dateDue);
	vch.setDescription("Generated by GenInvc " + LocalDateTime.now().toString());

	GnucashWritableEmployeeVoucherEntry entry1 = vch.createEntry(expAcct, 
		                                                     new FixedPointNumber(amount),
		                                                     new FixedPointNumber(1));
	entry1.setAction(GnucashGenerInvoiceEntry.Action.JOB);
	entry1.setDescription("Entry no. 1");
	entry1.setDate(dateOpen.minus(1, ChronoUnit.DAYS));

	GnucashWritableEmployeeVoucherEntry entry2 = vch.createEntry(expAcct, 
								     new FixedPointNumber(amount),
								     new FixedPointNumber(1),
								     "DE_USt_Std");
	entry2.setAction(GnucashGenerInvoiceEntry.Action.HOURS);
	entry2.setDescription("Entry no. 2");
	entry2.setDate(dateOpen);

	GnucashWritableEmployeeVoucherEntry entry3 = vch.createEntry(expAcct,
							    	     new FixedPointNumber(amount),
							    	     new FixedPointNumber(1), 
							    	     gcshFile.getTaxTableByName("FR_TVA_Std"));
	entry3.setAction(GnucashGenerInvoiceEntry.Action.MATERIAL);
	entry3.setDescription("Entry no. 3");
	entry3.setDate(dateOpen.plus(1, ChronoUnit.DAYS));

	// CAUTION: Posting a voucher directly after its generation 
	// will not work in the current version.  
	// Cf. the "Known Issues" section in the project README
	// vch.post(expAcct, payblAcct, datePost, dateDue);

	return vch;
    }

    private GnucashWritableJobInvoice doJob_cust(GnucashWritableFileImpl gcshFile) throws Exception {
	GnucashGenerJob job = null;
	try {
	    job = gcshFile.getGenerJobByID(job1ID);
	    System.err.println("(Gener.) job: " + job.getNumber() + " (" + job.getName() + ")");
	} catch (Exception exc) {
	    System.err.println("Error: No (gener.) job with ID '" + job1ID + "' found");
	    throw new OwnerNotFoundException();
	}

	GnucashWritableJobInvoice invc = gcshFile.createWritableJobInvoice(number, job, 
							incAcct, recvblAcct,
							dateOpen, datePost, dateDue);
	invc.setDescription("Generated by GenInvc " + LocalDateTime.now().toString());

	GnucashWritableJobInvoiceEntry entry1 = invc.createEntry(incAcct, 
                                                                 new FixedPointNumber(amount),
                                                                 new FixedPointNumber(1));
	entry1.setAction(GnucashGenerInvoiceEntry.Action.JOB);
	entry1.setDescription("Entry no. 1");
	entry1.setDate(dateOpen.minus(1, ChronoUnit.DAYS));

	GnucashWritableJobInvoiceEntry entry2 = invc.createEntry(incAcct, 
		                                                 new FixedPointNumber(amount),
		                                                 new FixedPointNumber(1), 
		                                                 "DE_USt_Std");
	entry2.setAction(GnucashGenerInvoiceEntry.Action.HOURS);
	entry2.setDescription("Entry no. 2");
	entry2.setDate(dateOpen);

	GnucashWritableJobInvoiceEntry entry3 = invc.createEntry(incAcct, 
		                                                 new FixedPointNumber(amount),
		                                                 new FixedPointNumber(1), 
		                                                 gcshFile.getTaxTableByName("FR_TVA_Std"));
	entry3.setAction(GnucashGenerInvoiceEntry.Action.MATERIAL);
	entry3.setDescription("Entry no. 3");
	entry3.setDate(dateOpen.plus(1, ChronoUnit.DAYS));

	// CAUTION: Posting an invoice directly after its generation 
	// will not work in the current version.  
	// Cf. the "Known Issues" section in the project README
	// invc.post(incAcct, recvblAcct, datePost, dateDue);

	return invc;
    }

    private GnucashWritableJobInvoice doJob_vend(GnucashWritableFileImpl gcshFile) throws Exception {
	GnucashGenerJob job = null;
	try {
	    job = gcshFile.getGenerJobByID(job2ID);
	    System.err.println("(Gener.) job: " + job.getNumber() + " (" + job.getName() + ")");
	} catch (Exception exc) {
	    System.err.println("Error: No (gener.) job with ID '" + job2ID + "' found");
	    throw new OwnerNotFoundException();
	}

	GnucashWritableJobInvoice invc = gcshFile.createWritableJobInvoice(number, job, 
							incAcct, payblAcct,
							dateOpen, datePost, dateDue);
	invc.setDescription("Generated by GenInvc " + LocalDateTime.now().toString());

	GnucashWritableJobInvoiceEntry entry1 = invc.createEntry(expAcct, 
		                                                 new FixedPointNumber(amount),
		                                                 new FixedPointNumber(1));
	entry1.setAction(GnucashGenerInvoiceEntry.Action.JOB);
	entry1.setDescription("Entry no. 1");
	entry1.setDate(dateOpen.minus(1, ChronoUnit.DAYS));

	GnucashWritableJobInvoiceEntry entry2 = invc.createEntry(expAcct, 
		                                                 new FixedPointNumber(amount),
		                                                 new FixedPointNumber(1), 
		                                                 "DE_USt_Std");
	entry2.setAction(GnucashGenerInvoiceEntry.Action.HOURS);
	entry2.setDescription("Entry no. 2");
	entry2.setDate(dateOpen);

	GnucashWritableJobInvoiceEntry entry3 = invc.createEntry(expAcct, 
		                                                 new FixedPointNumber(amount),
		                                                 new FixedPointNumber(1), 
		                                                 gcshFile.getTaxTableByName("FR_TVA_Std"));
	entry3.setAction(GnucashGenerInvoiceEntry.Action.MATERIAL);
	entry3.setDescription("Entry no. 3");
	entry3.setDate(dateOpen.plus(1, ChronoUnit.DAYS));

	invc.post(expAcct, payblAcct, datePost, dateDue);

	return invc;
    }
}
