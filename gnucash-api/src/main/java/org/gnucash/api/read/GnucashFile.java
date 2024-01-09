package org.gnucash.api.read;

import java.io.File;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount.Type;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnucashObject;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * Interface of a top-level class<br/>
 * that gives access to a gnucash-file <br/>
 * with all it's transactions and accounts,... <br/>
 * <br/>
 */
public interface GnucashFile extends GnucashObject {

    /**
     *
     * @return the file on disk we are managing
     */
    File getFile();

    // ---------------------------------------------------------------

    /**
     * The Currency-Table gets initialized with the latest prices found in the
     * gnucash-file.
     * 
     * @return Returns the currencyTable.
     */
    ComplexPriceTable getCurrencyTable();

    /**
     * Use a heuristic to determine the defaultcurrency-id. If we cannot find one,
     * we default to EUR.<br/>
     * Comodity-stace is fixed as "CURRENCY" .
     * 
     * @return the default-currencyID to use.
     */
    String getDefaultCurrencyID();

    // ---------------------------------------------------------------

    /**
     * @param acctID the unique id of the account to look for
     * @return the account or null if it's not found
     */
    GnucashAccount getAccountByID(GCshID acctID);

    /**
     *
     * @param prntAcctID if null, gives all account that have no parent
     * @return all accounts with that parent in no particular order
     */
    Collection<GnucashAccount> getAccountsByParentID(GCshID prntAcctID);

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr 
     *
     * @param name the UNQUaLIFIED name to look for
     * @return null if not found
     * @see #getAccountByID(GCshID)
     */
    Collection<GnucashAccount> getAccountsByName(String expr);

    /**
     * @param expr
     * @param qualif
     * @param relaxed
     * @return
     */
    Collection<GnucashAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

    /**
     * @param expr
     * @param qualif
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashAccount getAccountByNameUniq(String expr, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * warning: this function has to traverse all accounts. If it much faster to try
     * getAccountByID first and only call this method if the returned account does
     * not have the right name.
     *
     * @param name the regular expression of the name to look for
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    GnucashAccount getAccountByNameEx(String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param id   the id to look for
     * @param name the name to look for if nothing is found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    GnucashAccount getAccountByIDorName(GCshID id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * First try to fetch the account by id, then fall back to traversing all
     * accounts to get if by it's name.
     *
     * @param acctID   the id to look for
     * @param name the regular expression of the name to look for if nothing is
     *             found for the id
     * @return null if not found
     * @throws TooManyEntriesFoundException 
     * @throws NoEntryFoundException 
     * @see #getAccountByID(GCshID)
     * @see #getAccountsByName(String)
     */
    GnucashAccount getAccountByIDorNameEx(GCshID acctID, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type
     * @param acctName
     * @param qualif
     * @param relaxed
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashAccount> getAccountsByTypeAndName(Type type, String acctName, 
		                                        boolean qualif, boolean relaxed) throws UnknownAccountTypeException;
    /**
     * @return all accounts
     */
    Collection<GnucashAccount> getAccounts();

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    GnucashAccount getRootAccount() throws UnknownAccountTypeException;

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     * @throws UnknownAccountTypeException 
     */
    Collection<? extends GnucashAccount> getParentlessAccounts() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GCshID> getTopAccountIDs() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashAccount> getTopAccounts() throws UnknownAccountTypeException;

    // ---------------------------------------------------------------

    /**
     * @param trxID the unique id of the transaction to look for
     * @return the transaction or null if it's not found
     */
    GnucashTransaction getTransactionByID(GCshID trxID);

    /**
     * @return a (possibly read-only) collection of all transactions Do not modify
     *         the returned collection!
     */
    Collection<? extends GnucashTransaction> getTransactions();

    // ---------------------------------------------------------------

    /**
     * @param spltID the unique id of the transaction split to look for
     * @return the transaction split or null if it's not found
     */
    GnucashTransactionSplit getTransactionSplitByID(GCshID spltID);

    /**
     * @return
     */
    Collection<GnucashTransactionSplit> getTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param invcID the unique id of the (generic) invoice to look for
     * @return the invoice or null if it's not found
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    GnucashGenerInvoice getGenerInvoiceByID(GCshID invcID);

    /**
     * @return a (possibly read-only) collection of all invoices Do not modify the
     *         returned collection!
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashGenerInvoice> getGenerInvoices();

    // ----------------------------

    /**
     * @return a (possibly read-only) collection of all invoices that are fully Paid
     *         Do not modify the returned collection!
     * @throws UnknownAccountTypeException 
     *  
     * @see #getUnpaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashGenerInvoice> getPaidGenerInvoices() throws UnknownAccountTypeException;

    /**
     * @return a (possibly read-only) collection of all invoices that are not fully
     *         Paid Do not modify the returned collection!
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashGenerInvoice> getUnpaidGenerInvoices() throws UnknownAccountTypeException;

    // ----------------------------

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashCustomerInvoice> getInvoicesForCustomer_direct(GnucashCustomer cust)
	    throws WrongInvoiceTypeException;

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashJobInvoice>      getInvoicesForCustomer_viaAllJobs(GnucashCustomer cust)
	    throws WrongInvoiceTypeException;

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashCustomerInvoice> getPaidInvoicesForCustomer_direct(GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given customer. Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashJobInvoice>      getPaidInvoicesForCustomer_viaAllJobs(GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given customer Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(GnucashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param cust the customer to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given customer Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    Collection<GnucashJobInvoice>      getUnpaidInvoicesForCustomer_viaAllJobs(GnucashCustomer cust)throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ----------------------------

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashVendorBill>      getBillsForVendor_direct(GnucashVendor vend) throws WrongInvoiceTypeException;

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashJobInvoice>      getBillsForVendor_viaAllJobs(GnucashVendor vend) throws WrongInvoiceTypeException;

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(String)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashVendorBill>      getPaidBillsForVendor_direct(GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have fully been
     *         paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashJobInvoice>      getPaidBillsForVendor_viaAllJobs(GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have not fully
     *         been paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashVendorBill>      getUnpaidBillsForVendor_direct(GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param vend the vendor to look for (not null)
     * @return a (possibly read-only) collection of all bills that have not fully
     *         been paid and are from the given vendor Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashJobInvoice>      getUnpaidBillsForVendor_viaAllJobs(GnucashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ----------------------------

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidVouchersForEmployee_viaJob(GnucashVendor)
     */
    Collection<GnucashEmployeeVoucher> getVouchersForEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException;

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have fully been
     *         paid and are from the given employee Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidVouchersForEmployee_viaJob(GnucashVendor)
     */
    Collection<GnucashEmployeeVoucher> getPaidVouchersForEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param empl the employee to look for (not null)
     * @return a (possibly read-only) collection of all vouchers that have not fully
     *         been paid and are from the given employee Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidVouchersForEmployee_viaJob(GnucashVendor)
     */
    Collection<GnucashEmployeeVoucher> getUnpaidVouchersForEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ----------------------------

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashJobInvoice>      getInvoicesForJob(GnucashGenerJob job) throws WrongInvoiceTypeException;

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */

    Collection<GnucashJobInvoice>      getPaidInvoicesForJob(GnucashGenerJob job) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @param job the job to look for (not null)
     * @return a (possibly read-only) collection of all invoices that have not fully
     *         been paid and are from the given job Do not modify the returned
     *         collection!
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidBillsForVendor_viaJob(GnucashVendor)
     */
    Collection<GnucashJobInvoice>      getUnpaidInvoicesForJob(GnucashGenerJob job) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ---------------------------------------------------------------

    /**
     * @param id the unique id of the (generic) invoice entry to look for
     * @return the invoice entry or null if it's not found
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(String)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnucashCustomer)
     */
    GnucashGenerInvoiceEntry getGenerInvoiceEntryByID(GCshID id);

    /**
     * @return
     */
    Collection<GnucashGenerInvoiceEntry> getGenerInvoiceEntries();
    
    // ---------------------------------------------------------------

    /**
     * @param jobID the unique id of the job to look for
     * @return the job or null if it's not found
     */
    GnucashGenerJob getGenerJobByID(GCshID jobID);

    /**
     * @param expr
     * @return
     */
    Collection<GnucashGenerJob> getGenerJobsByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashGenerJob> getGenerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashGenerJob getGenerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all jobs Do not modify the
     *         returned collection!
     */
    Collection<GnucashGenerJob> getGenerJobs();

    // ----------------------------

    /**
     * @param custID the unique id of the customer job to look for
     * @return the job or null if it's not found
     */
    GnucashCustomerJob getCustomerJobByID(GCshID custID);

    /**
     * @param expr
     * @return
     */
    Collection<GnucashCustomerJob> getCustomerJobsByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashCustomerJob> getCustomerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashCustomerJob getCustomerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customer jobs Do not modify the
     *         returned collection!
     */
    Collection<GnucashCustomerJob> getCustomerJobs();

    // ----------------------------

    /**
     * @param vendID the unique id of the vendor job to look for
     * @return the job or null if it's not found
     */
    GnucashVendorJob getVendorJobByID(GCshID vendID);

    /**
     * @param expr
     * @return
     */
    Collection<GnucashVendorJob> getVendorJobsByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashVendorJob> getVendorJobsByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashVendorJob getVendorJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendor jobs Do not modify the
     *         returned collection!
     */
    Collection<GnucashVendorJob> getVendorJobs();

    // ---------------------------------------------------------------

    /**
     * @param custID the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    GnucashCustomer getCustomerByID(GCshID custID);

    /**
     * warning: this function has to traverse all customers. If it much faster to
     * try getCustomerByID first and only call this method if the returned account
     * does not have the right name.
     * @param expr 
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getCustomerByID(GCshID)
     */
    Collection<GnucashCustomer> getCustomersByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashCustomer> getCustomersByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashCustomer getCustomerByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<GnucashCustomer> getCustomers();

    // ---------------------------------------------------------------

    /**
     * @param vendID the unique id of the vendor to look for
     * @return the vendor or null if it's not found
     */
    GnucashVendor getVendorByID(GCshID vendID);

    /**
     * warning: this function has to traverse all vendors. If it much faster to try
     * getVendorByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr 
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getVendorByID(GCshID)
     */
    Collection<GnucashVendor> getVendorsByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashVendor> getVendorsByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashVendor getVendorByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendors Do not modify the
     *         returned collection!
     */
    Collection<GnucashVendor> getVendors();

    // ---------------------------------------------------------------

    /**
     * @param emplID the unique id of the employee to look for
     * @return the employee or null if it's not found
     */
    GnucashEmployee getEmployeeByID(GCshID emplID);

    /**
     * warning: this function has to traverse all employees. If it much faster to
     * try getEmployeeByID first and only call this method if the returned account
     * does not have the right name.
     * @param expr 
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getEmployeeByID(GCshID)
     */
    Collection<GnucashEmployee> getEmployeesByUserName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashEmployee> getEmployeesByUserName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashEmployee getEmployeeByUserNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;


    /**
     * @return a (possibly read-only) collection of all employees Do not modify the
     *         returned collection!
     */
    Collection<GnucashEmployee> getEmployees();

    // ---------------------------------------------------------------

    /**
     * @param cmdtyCurrID 
     * @param id the unique id of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnucashCommodity getCommodityByQualifID(GCshCmdtyCurrID cmdtyCurrID);

    /**
     * @param nameSpace
     * @param id
     * @return
     */
    GnucashCommodity getCommodityByQualifID(String nameSpace, String id);

    /**
     * @param exchange
     * @param id
     * @return
     */
    GnucashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.Exchange exchange, String id);

    /**
     * @param mic
     * @param id
     * @return
     */
    GnucashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.MIC mic, String id);

    /**
     * @param secIdType
     * @param id
     * @return
     */
    GnucashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.SecIdType secIdType, String id);

    /**
     * @param qualifID 
     * @param id the unique id of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    GnucashCommodity getCommodityByQualifID(String qualifID);

    /**
     * @param xCode 
     * @param id the unique id of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnucashCommodity getCommodityByXCode(String xCode);

    /**
     * warning: this function has to traverse all currencies/securities/commodities. If it much faster to try
     * getCommodityByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr 
     *
     * @param name the name to look for
     * @return null if not found
     * @see #getCommodityByID(GCshID)
     */
    Collection<GnucashCommodity> getCommoditiesByName(String expr);

    /**
     * @param expr
     * @param relaxed
     * @return
     */
    Collection<GnucashCommodity> getCommoditiesByName(String expr, boolean relaxed);

    /**
     * @param expr
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnucashCommodity getCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all currencies/securities/commodities Do not modify the
     *         returned collection!
     */
    Collection<GnucashCommodity> getCommodities();

    // ---------------------------------------------------------------

    /**
     * @param taxTabID id of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByID(GCshID taxTabID);

    /**
     * @param name 
     * @param id name of a tax table
     * @return the identified tax table or null
     */
    GCshTaxTable getTaxTableByName(String name);

    /**
     * @return all TaxTables defined in the book
     * @link GnucashTaxTable
     */
    Collection<GCshTaxTable> getTaxTables();

    // ---------------------------------------------------------------

    /**
     * @param bllTrmID id of a tax table
     * @return the identified tax table or null
     */
    GCshBillTerms getBillTermsByID(GCshID bllTrmID);

    /**
     * @param name 
     * @param id name of a tax table
     * @return the identified tax table or null
     */
    GCshBillTerms getBillTermsByName(String name);

    /**
     * @return all TaxTables defined in the book
     * @link GnucashTaxTable
     */
    Collection<GCshBillTerms> getBillTerms();

    // ---------------------------------------------------------------

    /**
     * @param prcID id of a price
     * @return the identified price or null
     */
    GnucashPrice getPriceByID(GCshID prcID);

    /**
     * @return all prices defined in the book
     * @link GCshPrice
     */
    Collection<GnucashPrice> getPrices();

    /**
     * @param cmdtyCurrID 
     * @param pCmdtySpace the name space for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the gnucash-file in EURO
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    @Deprecated
    FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

}
