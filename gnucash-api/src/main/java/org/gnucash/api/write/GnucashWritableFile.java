package org.gnucash.api.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
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
import org.gnucash.api.write.impl.ObjectCascadeException;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerJob;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.gnucash.api.write.spec.GnucashWritableVendorJob;
import org.gnucash.api.generated.GncV2;

/**
 * Extension of GnucashFile that allows writing. <br/>
 * All the instances for accounts,... it returns can be assumed
 * to implement the respetive *Writable-interfaces.
 *
 * @see GnucashFile
 * @see org.gnucash.api.write.impl.GnucashWritableFileImpl
 */
public interface GnucashWritableFile extends GnucashFile, 
                                             GnucashWritableObject 
{

    /**
     * @return true if this file has been modified.
     */
    boolean isModified();

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

    /**
     * @param pB true if this file has been modified.
     * @see {@link #isModified()}
     */
    void setModified(boolean pB);

    /**
     * Write the data to the given file. That file becomes the new file returned by
     * {@link GnucashFile#getGnucashFile()}
     * 
     * @param file the file to write to
     * @throws IOException kn io-poblems
     */
    void writeFile(File file) throws IOException;

    /**
     * @return the underlying JAXB-element
     */
    @SuppressWarnings("exports")
    GncV2 getRootElement();

    // ---------------------------------------------------------------

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    GnucashWritableCustomer getCustomerByID(GCshID id);

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    GnucashWritableVendor getVendorByID(GCshID id);

    /**
     * @param id the unique id of the customer to look for
     * @return the customer or null if it's not found
     */
    GnucashWritableEmployee getEmployeeByID(GCshID id);
    
    // ---------------------------------------------------------------

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     * @throws UnknownAccountTypeException 
     */
    Collection<? extends GnucashWritableAccount> getWritableRootAccounts() throws UnknownAccountTypeException;

    /**
     *
     * @return a read-only collection of all accounts
     */
    Collection<? extends GnucashWritableAccount> getWritableAccounts();

    /**
     * @see GnucashFile#getTransactionByID(GCshID)
     * @return A changeable version of the transaction.
     */
    GnucashWritableTransaction getTransactionByID(GCshID id);

    public Collection<GnucashWritableGenerInvoice> getWritableGenerInvoices();
    
    /**
     * @see GnucashFile#getGenerInvoiceByID(GCshID)
     * @param id the id to look for
     * @return A changeable version of the invoice.
     */
    GnucashWritableGenerInvoice getGenerInvoiceByID(GCshID id);

    /**
     * @see GnucashFile#getAccountsByName(String)
     * @param name the name to look for
     * @return A changeable version of the account.
     */
    GnucashWritableAccount getAccountByNameUniq(String name, boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException;

    /**
     * @param type the type to look for
     * @return A changeable version of all accounts of that type.
     * @throws UnknownAccountTypeException 
     */
    Collection<GnucashWritableAccount> getAccountsByType(GnucashAccount.Type type) throws UnknownAccountTypeException;

    /**
     * @see GnucashFile#getAccountByID(GCshID)
     * @param id the id of the account to fetch
     * @return A changeable version of the account or null of not found.
     */
    GnucashWritableAccount getAccountByID(GCshID id);

    /**
     * @see GnucashFile#getGenerJobByID(GCshID)
     * @param jobID the id of the job to fetch
     * @return A changeable version of the job or null of not found.
     */
    GnucashWritableGenerJob getGenerJobByID(GCshID jobID);

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    GnucashWritableGenerJob getGenerJobByNumber(final String jnr);

    /**
     * @return all jobs as writable versions.
     */
    Collection<GnucashWritableGenerJob> getWritableGenerJobs();

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
    public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
	    final int pCmdtyNameFraction, final String pCmdtyName);

    /**
     * @see GnucashFile#getTransactions()
     * @return writable versions of all transactions in the book.
     */
    public Collection<? extends GnucashWritableTransaction> getWritableTransactions();

    // public GnucashWritableTransaction getWritableTransactionByID(final String trxId) throws TransactionNotFoundException;
    
    /**
     * @return a new transaction with no splits that is already added to this file
     */
    GnucashWritableTransaction createWritableTransaction();

    /**
     *
     * @param impl the transaction to remove.
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    void removeTransaction(GnucashWritableTransaction impl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ---------------------------------------------------------------

    /**
     * @return a new customer with no values that is already added to this file
     */
    GnucashWritableCustomer createWritableCustomer();

    /**
     * @return a new customer with no values that is already added to this file
     */
    GnucashWritableVendor createWritableVendor();
    
    /**
     * @return a new employeer with no values that is already added to this file
     */
    GnucashWritableEmployee createWritableEmployee();
    
    // ---------------------------------------------------------------

    /**
     * @return a new customer job with no values that is already added to this file
     */
    GnucashWritableCustomerJob createWritableCustomerJob(
	    final GnucashCustomer cust, 
	    final String number, 
	    final String name);

    /**
     * @return a new vendor job with no values that is already added to this file
     */
    GnucashWritableVendorJob createWritableVendorJob(
	    final GnucashVendor vend, 
	    final String number, 
	    final String name);

    /**
     * @return a new vendor job with no values that is already added to this file
     */
//    GnucashWritableVendorJob createWritableEmployeeJob(
//	    final GnucashEmployee empl, 
//	    final String number, 
//	    final String userName);

    // ---------------------------------------------------------------

    /**
     * @return a new account that is already added to this file as a top-level
     *         account
     */
    GnucashWritableAccount createWritableAccount();

    // -----------------------------------------------------------

    /**
     * FOR USE BY EXTENSIONS ONLY
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    GnucashWritableCustomerInvoice createWritableCustomerInvoice(
	    final String invoiceNumber, 
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct,
	    final GnucashAccount receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    GnucashWritableVendorBill createWritableVendorBill(
	    final String invoiceNumber, 
	    final GnucashVendor vend,
	    final GnucashAccount expensesAcct,
	    final GnucashAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
//    GnucashWritableEmployeeVoucher createWritableEmployeeVoucher(
//	    final String invoiceNumber, 
//	    final GnucashEmployee empl,
//	    final GnucashAccount expensesAcct,
//	    final GnucashAccount payableAcct,
//	    final LocalDate openedDate,
//	    final LocalDate postDate,
//	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * FOR USE BY EXTENSIONS ONLY
     * 
     * @return a new invoice with no entries that is already added to this file
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    GnucashWritableJobInvoice createWritableJobInvoice(
	    final String invoiceNumber, 
	    final GnucashGenerJob job,
	    final GnucashAccount incExpAcct,
	    final GnucashAccount recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, NumberFormatException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // -----------------------------------------------------------

    /**
     * @param impl the account to remove
     */
    void removeAccount(GnucashWritableAccount impl);

    // ---------------------------------------------------------------

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
    void removeCommodity(GnucashWritableCommodity cmdty) throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException;

}
