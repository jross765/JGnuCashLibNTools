package org.gnucash.api.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.impl.ObjectCascadeException;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;

/**
 * Extension of GnucashFile that allows writing.
 * 
 * æsee {@link GnucashFile}
 */
public interface GnucashWritableFile extends GnucashFile, 
                                             GnucashWritableObject 
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
     * {@link GnucashFile#getGnucashFile()}
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

    GnucashWritableAccount getWritableAccountByID(final GCshID acctID);

    GnucashWritableAccount getWritableAccountByNameUniq(final String name, final boolean qualif)
	    throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type the type to look for
     * @return A changeable version of all accounts of that type.
     * @throws UnknownAccountTypeException
     */
    Collection<GnucashWritableAccount> getWritableAccountsByType(GnucashAccount.Type type)
	    throws UnknownAccountTypeException;

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     * @throws UnknownAccountTypeException
     */
    Collection<? extends GnucashWritableAccount> getWritableParentlessAccounts() throws UnknownAccountTypeException;

    /**
     *
     * @return a read-only collection of all accounts
     */
    Collection<? extends GnucashWritableAccount> getWritableAccounts();

    // ----------------------------

    /**
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnucashWritableAccount createWritableAccount();

    /**
     * @param acct the account to remove
     */
    void removeAccount(GnucashWritableAccount acct);

    // -----------------------------------------------------------

    GnucashWritableTransaction getWritableTransactionByID(final GCshID trxID);

    /**
     * @see GnucashFile#getTransactions()
     * @return writable versions of all transactions in the book.
     */
    Collection<? extends GnucashWritableTransaction> getWritableTransactions();

    // ----------------------------

    /**
     * @return a new transaction with no splits that is already added to this file
     * 
     */
    GnucashWritableTransaction createWritableTransaction();

    /**
     *
     * @param impl the transaction to remove.
     * 
     */
    void removeTransaction(GnucashWritableTransaction impl);

    // ---------------------------------------------------------------

    /**
     * @param spltID
     * @return
     */
    GnucashWritableTransactionSplit getWritableTransactionSplitByID(final GCshID spltID);

    /**
     * @return
     */
    Collection<GnucashWritableTransactionSplit> getWritableTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param invcID 
     * @see GnucashFile#getGenerInvoiceByID(GCshID)
     * @param id the id to look for
     * @return A changeable version of the invoice.
     */
    GnucashWritableGenerInvoice getWritableGenerInvoiceByID(final GCshID invcID);

    Collection<GnucashWritableGenerInvoice> getWritableGenerInvoices();

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
    GnucashWritableCustomerInvoice createWritableCustomerInvoice(
	    final String invoiceNumber, 
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct, 
	    final GnucashAccount receivableAcct, 
	    final LocalDate openedDate,
	    final LocalDate postDate, 
	    final LocalDate dueDate)
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
    GnucashWritableVendorBill createWritableVendorBill(
	    final String invoiceNumber, 
	    final GnucashVendor vend,
	    final GnucashAccount expensesAcct, 
	    final GnucashAccount payableAcct, 
	    final LocalDate openedDate,
	    final LocalDate postDate, 
	    final LocalDate dueDate)
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
    GnucashWritableEmployeeVoucher createWritableEmployeeVoucher(
	    final String invoiceNumber, 
	    final GnucashEmployee empl,
	    final GnucashAccount expensesAcct, 
	    final GnucashAccount payableAcct, 
	    final LocalDate openedDate,
	    final LocalDate postDate, 
	    final LocalDate dueDate)
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
    GnucashWritableJobInvoice createWritableJobInvoice(
	    final String invoiceNumber, 
	    final GnucashGenerJob job,
	    final GnucashAccount incExpAcct, 
	    final GnucashAccount recvblPayblAcct, 
	    final LocalDate openedDate,
	    final LocalDate postDate, 
	    final LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException,
	    InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException;

    void removeGenerInvoice(final GnucashWritableGenerInvoice impl);

    // ---------------------------------------------------------------

    /**
     * @param invcEntrID 
     * @see GnucashFile#getGenerInvoiceEntryByID(GCshID)
     * @param id the id to look for
     * @return A changeable version of the invoice entry.
     */
    GnucashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(final GCshID invcEntrID);

    Collection<GnucashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries();

    // ----------------------------
    // ::TODO

//    GnucashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry();
//
//    GnucashWritableVendorBillEntry createWritableVendorBillEntry();
//
//    GnucashWritableEmployeeVoucherEntry createWritableEmployeeVoucher();
//
//    GnucashWritableJobInvoiceEntry createWritableJobInvoice();
//
//    void removeGenerInvoiceEntry(final GnucashWritableGenerInvoiceEntry impl);

    // ---------------------------------------------------------------

    GnucashWritableCustomer getWritableCustomerByID(final GCshID custID);

    Collection<GnucashWritableCustomer> getWritableCustomers();

    // ----------------------------

    GnucashWritableCustomer createWritableCustomer();

    void removeCustomer(final GnucashWritableCustomer cust);

    // ---------------------------------------------------------------

    GnucashWritableVendor getWritableVendorByID(final GCshID vendID);

    Collection<GnucashWritableVendor> getWritableVendors();

    // ----------------------------

    GnucashWritableVendor createWritableVendor();

    void removeVendor(final GnucashWritableVendor vend);

    // ---------------------------------------------------------------

    GnucashWritableEmployee getWritableEmployeeByID(final GCshID emplID);

    Collection<GnucashWritableEmployee> getWritableEmployees();

    // ----------------------------

    GnucashWritableEmployee createWritableEmployee();

    void removeEmployee(final GnucashWritableEmployee empl);

    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getGenerJobByID(GCshID)
     * @param jobID the id of the job to fetch
     * @return A changeable version of the job or null of not found.
     */
    GnucashWritableGenerJob getWritableGenerJobByID(GCshID jobID);

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    GnucashWritableGenerJob getWritableGenerJobByNumber(final String jnr);

    /**
     * @return all jobs as writable versions.
     */
    Collection<GnucashWritableGenerJob> getWritableGenerJobs();

    // ----------------------------

    /**
     * @param cust 
     * @param number 
     * @param name 
     * @return a new customer job with no values that is already added to this file
     */
    GnucashWritableCustomerJob createWritableCustomerJob(
	    final GnucashCustomer cust, 
	    final String number,
	    final String name);

    /**
     * @param vend 
     * @param number 
     * @param name 
     * @return a new vendor job with no values that is already added to this file
     */
    GnucashWritableVendorJob createWritableVendorJob(
	    final GnucashVendor vend, 
	    final String number, 
	    final String name);

    void removeGenerJob(final GnucashWritableGenerJob job);

    void removeCustomerJob(final GnucashWritableCustomerJobImpl job);

    void removeVendorJob(final GnucashWritableVendorJobImpl job);

    // ---------------------------------------------------------------

    GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrID cmdtyID);

    GnucashWritableCommodity getWritableCommodityByQualifID(final String nameSpace, final String id);

    GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.Exchange exchange, String id);

    GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.MIC mic, String id);

    GnucashWritableCommodity getWritableCommodityByQualifID(final GCshCmdtyCurrNameSpace.SecIdType secIdType, String id);

    GnucashWritableCommodity getWritableCommodityByQualifID(final String qualifID);

    GnucashWritableCommodity getWritableCommodityByXCode(final String xCode);

    Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr);

    Collection<GnucashWritableCommodity> getWritableCommoditiesByName(final String expr, final boolean relaxed);
    
    GnucashWritableCommodity getWritableCommodityByNameUniq(final String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
    Collection<GnucashWritableCommodity> getWritableCommodities();

    // ----------------------------

    /**
     * @return a new commodity with no values that is already added to this file
     */
    GnucashWritableCommodity createWritableCommodity();

    /**
     * @param cmdty the commodity to remove
     * @throws InvalidCmdtyCurrTypeException
     * @throws ObjectCascadeException
     * @throws InvalidCmdtyCurrIDException
     */
    void removeCommodity(GnucashWritableCommodity cmdty)
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
    void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
	    final int pCmdtyNameFraction, final String pCmdtyName);

    // ---------------------------------------------------------------

    GnucashWritablePrice getWritablePriceByID(final GCshID prcID);

    Collection<GnucashWritablePrice> getWritablePrices();

    // ----------------------------

    /**
     * @return a new price object with no values that is already added to this file
     */
    GnucashWritablePrice createWritablePrice();

    /**
     * @param prc the price to remove
     */
    void removePrice(GnucashWritablePrice prc);

    // -----------------------------------------------------------

    GCshWritableTaxTable getWritableTaxTableByID(final GCshID taxTabID);

    GCshWritableTaxTable getWritableTaxTableByName(final String name);

    /**
     * @see GnucashFile#getTaxTables()
     * @return writable versions of all tax tables in the book.
     */
    Collection<GCshWritableTaxTable> getWritableTaxTables();

    // -----------------------------------------------------------

    GCshWritableBillTerms getWritableBillTermsByID(final GCshID bllTrmID);

    GCshWritableBillTerms getWritableBillTermsByName(final String name);

    /**
     * @see GnucashFile#getBillTerms()
     * @return writable versions of all bill terms in the book.
     */
    Collection<GCshWritableBillTerms> getWritableBillTerms();

}
