package org.gnucash.api.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.impl.ObjectCascadeException;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;

/**
 * Extension of GnuCashFile that allows writing.
 * 
 * æsee {@link GnuCashFile}
 */
public interface GnuCashWritableFile extends GnuCashFile, 
                                             GnuCashWritableObject,
                                             HasWritableUserDefinedAttributes
{
    /**
     * @param pB true if this file has been modified.
     * @see {@link #isModified()}
     */
    void setModified(boolean pB);

    /**
     * @return true if this file has been modified.
     */
    boolean isModified();

    /**
     * Write the data to the given file. That file becomes the new file returned by
     * {@link GnuCashFile#getGnuCashFile()}
     * 
     * @param file the file to write to
     * @throws IOException kn io-poblems
     */
    void writeFile(File file) throws IOException;

    /**
     * The value is guaranteed not to be bigger then the maximum of the current
     * system-time and the modification-time in the file at the time of the last
     * (full) read or sucessfull write.<br/ It is thus suitable to detect if the
     * file has been modified outside of this library
     * 
     * @return the time in ms (compatible with File.lastModified) of the last
     *         write-operation
     */
    long getLastWriteTime();

    // ---------------------------------------------------------------

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    GncV2 getRootElement();

    // ---------------------------------------------------------------

    GnuCashWritableAccount getWritableAccountByID(GCshID acctID);

    GnuCashWritableAccount getWritableAccountByNameUniq(String name, boolean qualif)
	    throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type the type to look for
     * @return A changeable version of all accounts of that type.
     * @throws UnknownAccountTypeException
     */
    Collection<GnuCashWritableAccount> getWritableAccountsByType(GnuCashAccount.Type type)
	    throws UnknownAccountTypeException;

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     * @throws UnknownAccountTypeException
     */
    Collection<? extends GnuCashWritableAccount> getWritableParentlessAccounts() throws UnknownAccountTypeException;

    /**
     *
     * @return a read-only collection of all accounts
     */
    Collection<? extends GnuCashWritableAccount> getWritableAccounts();

    // ----------------------------

    /**
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnuCashWritableAccount createWritableAccount();

    /**
     * @param acct the account to remove
     */
    void removeAccount(GnuCashWritableAccount acct);

    // -----------------------------------------------------------

    GnuCashWritableTransaction getWritableTransactionByID(GCshID trxID);

    /**
     * @see GnuCashFile#getTransactions()
     * @return writable versions of all transactions in the book.
     */
    Collection<? extends GnuCashWritableTransaction> getWritableTransactions();

    // ----------------------------

    /**
     * @return a new transaction with no splits that is already added to this file
     * 
     */
    GnuCashWritableTransaction createWritableTransaction();

    /**
     *
     * @param impl the transaction to remove.
     * 
     */
    void removeTransaction(GnuCashWritableTransaction impl);

    // ---------------------------------------------------------------

    /**
     * @param spltID
     * @return
     */
    GnuCashWritableTransactionSplit getWritableTransactionSplitByID(GCshID spltID);

    /**
     * @return
     */
    Collection<GnuCashWritableTransactionSplit> getWritableTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param invcID 
     * @see GnuCashFile#getGenerInvoiceByID(GCshID)
     * @param id the id to look for
     * @return A changeable version of the invoice.
     */
    GnuCashWritableGenerInvoice getWritableGenerInvoiceByID(GCshID invcID);

    Collection<GnuCashWritableGenerInvoice> getWritableGenerInvoices();

    // ----------------------------

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param cust 
     * @param incomeAcct 
     * @param receivableAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongInvoiceTypeException 
     * @throws WrongOwnerTypeException
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableCustomerInvoice createWritableCustomerInvoice(
	    String invoiceNumber, 
	    GnuCashCustomer cust,
	    GnuCashAccount incomeAcct, 
	    GnuCashAccount receivableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException,
	    InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param vend 
     * @param expensesAcct 
     * @param payableAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongInvoiceTypeException 
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableVendorBill createWritableVendorBill(
	    String invoiceNumber, 
	    GnuCashVendor vend,
	    GnuCashAccount expensesAcct, 
	    GnuCashAccount payableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException,
	    InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param empl 
     * @param expensesAcct 
     * @param payableAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongInvoiceTypeException 
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableEmployeeVoucher createWritableEmployeeVoucher(
	    String invoiceNumber, 
	    GnuCashEmployee empl,
	    GnuCashAccount expensesAcct, 
	    GnuCashAccount payableAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException,
	    InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * @param invoiceNumber 
     * @param job 
     * @param incExpAcct 
     * @param recvblPayblAcct 
     * @param openedDate 
     * @param postDate 
     * @param dueDate 
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongInvoiceTypeException 
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException
     * @throws NumberFormatException
     * 
     * @throws IllegalTransactionSplitActionException
     */
    GnuCashWritableJobInvoice createWritableJobInvoice(
	    String invoiceNumber, 
	    GnuCashGenerJob job,
	    GnuCashAccount incExpAcct, 
	    GnuCashAccount recvblPayblAcct, 
	    LocalDate openedDate,
	    LocalDate postDate, 
	    LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException,
	    InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

    void removeGenerInvoice(GnuCashWritableGenerInvoice impl);

    // ---------------------------------------------------------------

    /**
     * @param invcEntrID 
     * @see GnuCashFile#getGenerInvoiceEntryByID(GCshID)
     * @param id the id to look for
     * @return A changeable version of the invoice entry.
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(GCshID invcEntrID);

    Collection<GnuCashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries();

    // ----------------------------
    // ::TODO

//    GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry();
//
//    GnuCashWritableVendorBillEntry createWritableVendorBillEntry();
//
//    GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucher();
//
//    GnuCashWritableJobInvoiceEntry createWritableJobInvoice();
//
//    void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry impl);

    // ---------------------------------------------------------------

    GnuCashWritableCustomer getWritableCustomerByID(GCshID custID);

    Collection<GnuCashWritableCustomer> getWritableCustomers();

    // ----------------------------

    GnuCashWritableCustomer createWritableCustomer();

    void removeCustomer(GnuCashWritableCustomer cust);

    // ---------------------------------------------------------------

    GnuCashWritableVendor getWritableVendorByID(GCshID vendID);

    Collection<GnuCashWritableVendor> getWritableVendors();

    // ----------------------------

    GnuCashWritableVendor createWritableVendor();

    void removeVendor(GnuCashWritableVendor vend);

    // ---------------------------------------------------------------

    GnuCashWritableEmployee getWritableEmployeeByID(GCshID emplID);

    Collection<GnuCashWritableEmployee> getWritableEmployees();

    // ----------------------------

    GnuCashWritableEmployee createWritableEmployee();

    void removeEmployee(GnuCashWritableEmployee empl);

    // ---------------------------------------------------------------

    /**
     * @see GnuCashFile#getGenerJobByID(GCshID)
     * @param jobID the id of the job to fetch
     * @return A changeable version of the job or null of not found.
     */
    GnuCashWritableGenerJob getWritableGenerJobByID(GCshID jobID);

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    GnuCashWritableGenerJob getWritableGenerJobByNumber(String jnr);

    /**
     * @return all jobs as writable versions.
     */
    Collection<GnuCashWritableGenerJob> getWritableGenerJobs();

    // ----------------------------

    /**
     * @param cust 
     * @param number 
     * @param name 
     * @return a new customer job with no values that is already added to this file
     */
    GnuCashWritableCustomerJob createWritableCustomerJob(
	    GnuCashCustomer cust, 
	    String number,
	    String name);

    /**
     * @param vend 
     * @param number 
     * @param name 
     * @return a new vendor job with no values that is already added to this file
     */
    GnuCashWritableVendorJob createWritableVendorJob(
	    GnuCashVendor vend, 
	    String number, 
	    String name);

    void removeGenerJob(GnuCashWritableGenerJob job);

    void removeCustomerJob(GnuCashWritableCustomerJobImpl job);

    void removeVendorJob(GnuCashWritableVendorJobImpl job);

    // ---------------------------------------------------------------

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyCurrID cmdtyID);

    GnuCashWritableCommodity getWritableCommodityByQualifID(String nameSpace, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyCurrNameSpace.Exchange exchange, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyCurrNameSpace.MIC mic, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(GCshCmdtyCurrNameSpace.SecIdType secIdType, String id);

    GnuCashWritableCommodity getWritableCommodityByQualifID(String qualifID);

    GnuCashWritableCommodity getWritableCommodityByXCode(String xCode);

    Collection<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr);

    Collection<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr, boolean relaxed);
    
    GnuCashWritableCommodity getWritableCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
    Collection<GnuCashWritableCommodity> getWritableCommodities();

    // ----------------------------

    /**
     * @return a new commodity with no values that is already added to this file
     */
    GnuCashWritableCommodity createWritableCommodity();

    /**
     * @param cmdty the commodity to remove
     * @throws InvalidCmdtyCurrTypeException
     * @throws ObjectCascadeException
     * @throws InvalidCmdtyCurrIDException
     */
    void removeCommodity(GnuCashWritableCommodity cmdty)
	    throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException;

    // ----------------------------

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     * 
     * @param pCmdtySpace        the namespace (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    void addCurrency(String pCmdtySpace, String pCmdtyId, FixedPointNumber conversionFactor,
	    int pCmdtyNameFraction, String pCmdtyName);

    // ---------------------------------------------------------------

    GnuCashWritablePrice getWritablePriceByID(GCshID prcID);

    Collection<GnuCashWritablePrice> getWritablePrices();

    // ----------------------------

    /**
     * @return a new price object with no values that is already added to this file
     */
    GnuCashWritablePrice createWritablePrice();

    /**
     * @param prc the price to remove
     */
    void removePrice(GnuCashWritablePrice prc);

    // -----------------------------------------------------------

    GCshWritableTaxTable getWritableTaxTableByID(GCshID taxTabID);

    GCshWritableTaxTable getWritableTaxTableByName(String name);

    /**
     * @see GnuCashFile#getTaxTables()
     * @return writable versions of all tax tables in the book.
     */
    Collection<GCshWritableTaxTable> getWritableTaxTables();

    // -----------------------------------------------------------

    GCshWritableBillTerms getWritableBillTermsByID(GCshID bllTrmID);

    GCshWritableBillTerms getWritableBillTermsByName(String name);

    /**
     * @see GnuCashFile#getBillTerms()
     * @return writable versions of all bill terms in the book.
     */
    Collection<GCshWritableBillTerms> getWritableBillTerms();

}
