package org.gnucash.api.read;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.gnucash.api.currency.ComplexPriceTable;
import org.gnucash.api.read.GnuCashAccount.Type;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Interface of a top-level class that gives access to a GnuCash file
 * with all its accounts, transactions, etc.
 */
public interface GnuCashFile extends GnuCashObject,
                                     HasUserDefinedAttributes 
{

    /**
     *
     * @return the file on disk we are managing
     */
    File getFile();

    // ---------------------------------------------------------------

    /**
     * The Currency-Table gets initialized with the latest prices found in the
     * GnuCash file.
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
     * @param acctID the unique ID of the account to look for
     * @return the account or null if it's not found
     */
    GnuCashAccount getAccountByID(GCshID acctID);

    /**
     *
     * @param prntAcctID if null, gives all account that have no parent
     * @return all accounts with that parent in no particular order
     */
    List<GnuCashAccount> getAccountsByParentID(GCshID prntAcctID);

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
    Collection<GnuCashAccount> getAccountsByName(String expr);

    /**
     * @param expr
     * @param qualif
     * @param relaxed
     * @return
     */
    Collection<GnuCashAccount> getAccountsByName(String expr, boolean qualif, boolean relaxed);

    /**
     * @param expr
     * @param qualif
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashAccount getAccountByNameUniq(String expr, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

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
    GnuCashAccount getAccountByNameEx(String name) throws NoEntryFoundException, TooManyEntriesFoundException;

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
    GnuCashAccount getAccountByIDorName(GCshID id, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

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
    GnuCashAccount getAccountByIDorNameEx(GCshID acctID, String name) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type
     * @param acctName
     * @param qualif
     * @param relaxed
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GnuCashAccount> getAccountsByType(Type type) throws UnknownAccountTypeException;
    
    /**
     * @param type
     * @param acctName
     * @param qualif
     * @param relaxed
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GnuCashAccount> getAccountsByTypeAndName(Type type, String acctName, 
		                                        boolean qualif, boolean relaxed) throws UnknownAccountTypeException;
    /**
     * @return all accounts
     */
    Collection<GnuCashAccount> getAccounts();

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    GnuCashAccount getRootAccount() throws UnknownAccountTypeException;

    /**
     * @return a read-only collection of all accounts that have no parent (the
     *         result is sorted)
     * @throws UnknownAccountTypeException 
     */
    Collection<? extends GnuCashAccount> getParentlessAccounts() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GCshID> getTopAccountIDs() throws UnknownAccountTypeException;

    /**
     * @return
     * @throws UnknownAccountTypeException
     */
    Collection<GnuCashAccount> getTopAccounts() throws UnknownAccountTypeException;

    // ---------------------------------------------------------------

    /**
     * @param trxID the unique ID of the transaction to look for
     * @return the transaction or null if it's not found
     */
    GnuCashTransaction getTransactionByID(GCshID trxID);

    /**
     * @return a (possibly read-only) collection of all transactions Do not modify
     *         the returned collection!
     */
    Collection<? extends GnuCashTransaction> getTransactions();

    // ---------------------------------------------------------------

    /**
     * @param spltID the unique ID of the transaction split to look for
     * @return the transaction split or null if it's not found
     */
    GnuCashTransactionSplit getTransactionSplitByID(GCshID spltID);

    /**
     * @return
     */
    Collection<GnuCashTransactionSplit> getTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param invcID the unique ID of the (generic) invoice to look for
     * @return the invoice or null if it's not found
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    GnuCashGenerInvoice getGenerInvoiceByID(GCshID invcID);

    /**
     * 
     * @param type
     * @return
     */
    List<GnuCashGenerInvoice> getGenerInvoicesByType(GCshOwner.Type type);

    /**
     * @return a (possibly read-only) collection of all invoices Do not modify the
     *         returned collection!
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getGenerInvoices();

    // ----------------------------

    /**
     * @return a (possibly read-only) collection of all invoices that are fully Paid
     *         Do not modify the returned collection!
     * @throws UnknownAccountTypeException 
     *  
     * @see #getUnpaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getPaidGenerInvoices() throws UnknownAccountTypeException;

    /**
     * @return a (possibly read-only) collection of all invoices that are not fully
     *         Paid Do not modify the returned collection!
     * @throws UnknownAccountTypeException 
     *  
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoices()
     * @see #getGenerInvoiceByID(GCshID)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashGenerInvoice> getUnpaidGenerInvoices() throws UnknownAccountTypeException;

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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getInvoicesForCustomer_direct(GnuCashCustomer cust)
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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust)
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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getPaidInvoicesForCustomer_direct(GnuCashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getPaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashCustomerInvoice> getUnpaidInvoicesForCustomer_direct(GnuCashCustomer cust) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    List<GnuCashJobInvoice>      getUnpaidInvoicesForCustomer_viaAllJobs(GnuCashCustomer cust)throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getBillsForVendor_direct(GnuCashVendor vend) throws WrongInvoiceTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getBillsForVendor_viaAllJobs(GnuCashVendor vend) throws WrongInvoiceTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getPaidBillsForVendor_direct(GnuCashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getPaidBillsForVendor_viaAllJobs(GnuCashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashVendorBill>      getUnpaidBillsForVendor_direct(GnuCashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getUnpaidBillsForVendor_viaAllJobs(GnuCashVendor vend) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidVouchersForEmployee_viaJob(GnuCashVendor)
     */
    List<GnuCashEmployeeVoucher> getVouchersForEmployee(GnuCashEmployee empl) throws WrongInvoiceTypeException;

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
     * @see #getUnpaidVouchersForEmployee_viaJob(GnuCashVendor)
     */
    List<GnuCashEmployeeVoucher> getPaidVouchersForEmployee(GnuCashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidVouchersForEmployee_viaJob(GnuCashVendor)
     */
    List<GnuCashEmployeeVoucher> getUnpaidVouchersForEmployee(GnuCashEmployee empl) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getInvoicesForJob(GnuCashGenerJob job) throws WrongInvoiceTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */

    List<GnuCashJobInvoice>      getPaidInvoicesForJob(GnuCashGenerJob job) throws WrongInvoiceTypeException, UnknownAccountTypeException;

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
     * @see #getUnpaidBillsForVendor_viaJob(GnuCashVendor)
     */
    List<GnuCashJobInvoice>      getUnpaidInvoicesForJob(GnuCashGenerJob job) throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ---------------------------------------------------------------

    /**
     * @param id the unique ID of the (generic) invoice entry to look for
     * @return the invoice entry or null if it's not found
     * @see #getUnpaidGenerInvoices()
     * @see #getPaidGenerInvoices()
     * @see #getGenerInvoiceByID(String)
     * @see #getUnpaidInvoicesForCustomer_viaJob(GnuCashCustomer)
     */
    GnuCashGenerInvoiceEntry getGenerInvoiceEntryByID(GCshID id);

    /**
     * @return
     */
    Collection<GnuCashGenerInvoiceEntry> getGenerInvoiceEntries();
    
    // ---------------------------------------------------------------

    /**
     * @param jobID the unique ID of the job to look for
     * @return the job or null if it's not found
     */
    GnuCashGenerJob getGenerJobByID(GCshID jobID);

    /**
     * @param expr
     * @return
     */
    Collection<GnuCashGenerJob> getGenerJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashGenerJob> getGenerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashGenerJob getGenerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashGenerJob> getGenerJobs();

    // ----------------------------

    /**
     * @param custID the unique ID of the customer job to look for
     * @return the job or null if it's not found
     */
    GnuCashCustomerJob getCustomerJobByID(GCshID custID);

    /**
     * @param expr search expression
     * @return
     */
    Collection<GnuCashCustomerJob> getCustomerJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashCustomerJob> getCustomerJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCustomerJob getCustomerJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customer jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashCustomerJob> getCustomerJobs();

    // ----------------------------

    /**
     * @param vendID the unique ID of the vendor job to look for
     * @return the job or null if it's not found
     */
    GnuCashVendorJob getVendorJobByID(GCshID vendID);

    /**
     * @param expr search expression
     * @return
     */
    Collection<GnuCashVendorJob> getVendorJobsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashVendorJob> getVendorJobsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashVendorJob getVendorJobByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendor jobs Do not modify the
     *         returned collection!
     */
    Collection<GnuCashVendorJob> getVendorJobs();

    // ---------------------------------------------------------------

    /**
     * @param custID the unique ID of the customer to look for
     * @return the customer or null if it's not found
     */
    GnuCashCustomer getCustomerByID(GCshID custID);

    /**
     * warning: this function has to traverse all customers. If it much faster to
     * try getCustomerByID first and only call this method if the returned account
     * does not have the right name.
     * @param expr  search expression
     * @return null if not found
     * @see #getCustomerByID(GCshID)
     */
    Collection<GnuCashCustomer> getCustomersByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashCustomer> getCustomersByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCustomer getCustomerByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all customers Do not modify the
     *         returned collection!
     */
    Collection<GnuCashCustomer> getCustomers();

    // ---------------------------------------------------------------

    /**
     * @param vendID the unique ID of the vendor to look for
     * @return the vendor or null if it's not found
     */
    GnuCashVendor getVendorByID(GCshID vendID);

    /**
     * warning: this function has to traverse all vendors. If it much faster to try
     * getVendorByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr  search expression
     * @return null if not found
     * @see #getVendorByID(GCshID)
     */
    Collection<GnuCashVendor> getVendorsByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashVendor> getVendorsByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashVendor getVendorByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all vendors Do not modify the
     *         returned collection!
     */
    Collection<GnuCashVendor> getVendors();

    // ---------------------------------------------------------------

    /**
     * @param emplID the unique ID of the employee to look for
     * @return the employee or null if it's not found
     */
    GnuCashEmployee getEmployeeByID(GCshID emplID);

    /**
     * warning: this function has to traverse all employees. If it much faster to
     * try getEmployeeByID first and only call this method if the returned account
     * does not have the right name.
     * @param expr  search expression
     * @return null if not found
     * @see #getEmployeeByID(GCshID)
     */
    Collection<GnuCashEmployee> getEmployeesByUserName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    Collection<GnuCashEmployee> getEmployeesByUserName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashEmployee getEmployeeByUserNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;


    /**
     * @return a (possibly read-only) collection of all employees Do not modify the
     *         returned collection!
     */
    Collection<GnuCashEmployee> getEmployees();

    // ---------------------------------------------------------------

    /**
     * @param cmdtyCurrID 
     * @param id the unique ID of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyCurrID cmdtyCurrID);

    /**
     * @param nameSpace
     * @param id
     * @return
     */
    GnuCashCommodity getCommodityByQualifID(String nameSpace, String id);

    /**
     * @param exchange
     * @param id
     * @return
     */
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.Exchange exchange, String id);

    /**
     * @param mic
     * @param id
     * @return
     */
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.MIC mic, String id);

    /**
     * @param secIdType
     * @param id
     * @return
     */
    GnuCashCommodity getCommodityByQualifID(GCshCmdtyCurrNameSpace.SecIdType secIdType, String id);

    /**
     * @param qualifID the unique ID of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    GnuCashCommodity getCommodityByQualifID(String qualifID);

    /**
     * @param xCode the unique X-code of the currency/security/commodity to look for
     * @return the currency/security/commodity or null if it's not found
     */
    GnuCashCommodity getCommodityByXCode(String xCode);

    /**
     * warning: this function has to traverse all currencies/securities/commodities. If it much faster to try
     * getCommodityByID first and only call this method if the returned account does
     * not have the right name.
     * @param expr search expression
     * @return null if not found
     * @see #getCommodityByID(GCshID)
     */
    List<GnuCashCommodity> getCommoditiesByName(String expr);

    /**
     * @param expr search expression
     * @param relaxed
     * @return
     */
    List<GnuCashCommodity> getCommoditiesByName(String expr, boolean relaxed);

    /**
     * @param expr search expression
     * @return
     * @throws NoEntryFoundException
     * @throws TooManyEntriesFoundException
     */
    GnuCashCommodity getCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @return a (possibly read-only) collection of all currencies/securities/commodities Do not modify the
     *         returned collection!
     */
    Collection<GnuCashCommodity> getCommodities();

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
     * @link GnuCashTaxTable
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
     * @link GnuCashTaxTable
     */
    Collection<GCshBillTerms> getBillTerms();

    // ---------------------------------------------------------------

    /**
     * @param prcID id of a price
     * @return the identified price or null
     */
    GnuCashPrice getPriceByID(GCshID prcID);

    /**
     * @return all prices defined in the book
     * @link GCshPrice
     */
    Collection<GnuCashPrice> getPrices();

    /**
     * @param cmdtyCurrID 
     * @param pCmdtySpace the name space for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @return the latest price-quote in the GnuCash file in EURO
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    @Deprecated
    FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

}
