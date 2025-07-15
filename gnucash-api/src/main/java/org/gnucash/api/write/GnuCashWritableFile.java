package org.gnucash.api.write;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.aux.GCshWritableTaxTable;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerJobImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorJobImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableCustomerJob;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorJob;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.beanbase.NoEntryFoundException;
import xyz.schnorxoborx.base.beanbase.TooManyEntriesFoundException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashFile that allows writing.
 * 
 * @see {@link GnuCashFile}
 */
public interface GnuCashWritableFile extends GnuCashFile, 
                                             GnuCashWritableObject,
                                             HasWritableUserDefinedAttributes
{
	public enum CompressMode {
		COMPRESS,
		DO_NOT_COMPRESS,
		GUESS_FROM_FILENAME
	}
	
	// ---------------------------------------------------------------

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

    void writeFile(File file, CompressMode compMode) throws IOException;

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
     * @return A modifiable version of all accounts of the given type.
     */
    Collection<GnuCashWritableAccount> getWritableAccountsByType(GnuCashAccount.Type type);

    /**
     *
     * @return a read-only collection of all accounts that have no parent
     */
    Collection<? extends GnuCashWritableAccount> getWritableParentlessAccounts();

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
    @Deprecated
    GnuCashWritableAccount createWritableAccount();

    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type,
			  									 GCshCmdtyCurrID cmdtyCurrID,
			  									 GCshID parentID,
			  									 String name);

    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type, 
    											 GCshCmdtyID cmdtyID,
    											 GCshID parentID,
    											 String name);

    GnuCashWritableAccount createWritableAccount(GnuCashAccount.Type type, 
    											 GCshCurrID currID,
    											 GCshID parentID,
    											 String name);
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
     * 
     * #see {@link #getTransactionSplitByID(GCshID)}
     */
    GnuCashWritableTransactionSplit getWritableTransactionSplitByID(GCshID spltID);

    /**
     * @return
     * 
     * @see #getTransactionSplits()
     */
    Collection<GnuCashWritableTransactionSplit> getWritableTransactionSplits();

    // ---------------------------------------------------------------

    /**
     * @param invcID 
     * @param id the id to look for
     * @return A modifiable version of the invoice.
     *
     * @see #getGenerInvoiceByID(GCshID)
     */
    GnuCashWritableGenerInvoice getWritableGenerInvoiceByID(GCshID invcID);

    /**
     * 
     * @return
     * 
     * @see #getGenerInvoices()
     */
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
*  
     * @throws WrongOwnerTypeException
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
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

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
*  
     * @throws WrongOwnerTypeException 
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
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

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
*  
     * @throws WrongOwnerTypeException 
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
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

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
*  
     * @throws WrongOwnerTypeException 
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
	    throws WrongOwnerTypeException,
	    IllegalTransactionSplitActionException;

    void removeGenerInvoice(GnuCashWritableGenerInvoice invc, boolean withEntries);

    void removeCustomerInvoice(GnuCashWritableCustomerInvoice invc, boolean withEntries);

    void removeVendorBill(GnuCashWritableVendorBill bll, boolean withEntries);

    void removeEmployeeVoucher(GnuCashWritableEmployeeVoucher vch, boolean withEntries);

    void removeJobInvoice(GnuCashWritableJobInvoice invc, boolean withEntries);

    // ---------------------------------------------------------------

    /**
     * @param invcEntrID 
     * @see GnuCashFile#getGenerInvoiceEntryByID(GCshID)
     * @param id the id to look for
     * @return A modifiable version of the invoice entry.
     */
    GnuCashWritableGenerInvoiceEntry getWritableGenerInvoiceEntryByID(GCshID invcEntrID);

    Collection<GnuCashWritableGenerInvoiceEntry> getWritableGenerInvoiceEntries();

    // ----------------------------

    GnuCashWritableCustomerInvoiceEntry createWritableCustomerInvoiceEntry(
			GnuCashWritableCustomerInvoiceImpl invc,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableVendorBillEntry createWritableVendorBillEntry(
			GnuCashWritableVendorBillImpl bll, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableEmployeeVoucherEntry createWritableEmployeeVoucher(
			GnuCashWritableEmployeeVoucherImpl vch,
			GnuCashAccount account, 
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    GnuCashWritableJobInvoiceEntry createWritableJobInvoice(
			GnuCashWritableJobInvoiceImpl invc, 
			GnuCashAccount account,
			FixedPointNumber quantity, 
			FixedPointNumber price) throws TaxTableNotFoundException;

    void removeGenerInvoiceEntry(GnuCashWritableGenerInvoiceEntry entr);

    void removeCustomerInvoiceEntry(GnuCashWritableCustomerInvoiceEntry entr);

    void removeVendorBillEntry(GnuCashWritableVendorBillEntry entr);

    void removeEmployeeVoucherEntry(GnuCashWritableEmployeeVoucherEntry entr);

    void removeJobInvoiceEntry(GnuCashWritableJobInvoiceEntry entr);

    // ---------------------------------------------------------------

    GnuCashWritableCustomer getWritableCustomerByID(GCshID custID);

    Collection<GnuCashWritableCustomer> getWritableCustomers();

    // ----------------------------

    GnuCashWritableCustomer createWritableCustomer(String name);

    void removeCustomer(GnuCashWritableCustomer cust);

    // ---------------------------------------------------------------

    GnuCashWritableVendor getWritableVendorByID(GCshID vendID);

    Collection<GnuCashWritableVendor> getWritableVendors();

    // ----------------------------

    GnuCashWritableVendor createWritableVendor(String name);

    void removeVendor(GnuCashWritableVendor vend);

    // ---------------------------------------------------------------

    GnuCashWritableEmployee getWritableEmployeeByID(GCshID emplID);

    Collection<GnuCashWritableEmployee> getWritableEmployees();

    // ----------------------------

    GnuCashWritableEmployee createWritableEmployee(String userName);

    void removeEmployee(GnuCashWritableEmployee empl);

    // ---------------------------------------------------------------

    /**
     * @see GnuCashFile#getGenerJobByID(GCshID)
     * @param jobID the id of the job to fetch
     * @return A modifiable version of the job or null of not found.
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

    List<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr);

    List<GnuCashWritableCommodity> getWritableCommoditiesByName(String expr, boolean relaxed);
    
    GnuCashWritableCommodity getWritableCommodityByNameUniq(String expr) throws NoEntryFoundException, TooManyEntriesFoundException;
    
    Collection<GnuCashWritableCommodity> getWritableCommodities();

    // ----------------------------

    /**
     * @param qualifID Technical commodity ID, fully qualified (e.g., containing the 'name space').
     *                 <strong>Caution:</strong> GnuCash, for whatever reason, has no internal
     *                 technical UUID for commodities (as opposed to all other entities). This is
     *                 why, in this lib, we use the 'qualified' commodity ID as sort of pseudo-technical-
     *                 ID, which is very similar to the code, but declared 'technical' nonetheless.
	 * @param code  Commodity code (<strong>not</strong> the technical ID (which effectively
	 *              is very similar, but declared technical nontheless),
	 *              but the business ID, such as ISIN, CUSIP, etc. 
	 *              A ticker will also work, but it is <strong>not</strong> recommended,
	 *              as tickers typically are not unique, and there is a separate field
	 *              for it. 
	 * @param name  Security name
     * @return a new commodity with no values that is already added to this file
     */
    GnuCashWritableCommodity createWritableCommodity(GCshCmdtyID qualifID, String code, String name);

    /**
     * @param cmdty the commodity to remove
     * @throws ObjectCascadeException
     */
    void removeCommodity(GnuCashWritableCommodity cmdty) throws ObjectCascadeException;

    // ----------------------------

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     * 
     * @param pCmdtySpace        the name space (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    void addCurrency(String pCmdtySpace, String pCmdtyId, FixedPointNumber conversionFactor,
	    int pCmdtyNameFraction, String pCmdtyName);

    // ---------------------------------------------------------------

    GnuCashWritablePrice getWritablePriceByID(GCshID prcID);

    GnuCashWritablePrice getWritablePriceByCmdtyIDDate(GCshCmdtyID cmdtyID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrIDDate(GCshCurrID currID, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCurrDate(Currency curr, LocalDate date);
	
    GnuCashWritablePrice getWritablePriceByCmdtyCurrIDDate(GCshCmdtyCurrID cmdtyCurrID, LocalDate date);
    
    Collection<GnuCashWritablePrice> getWritablePrices();

    // ----------------------------

    /**
     * @param fromCmdtyCurrID 
     * @param toCurrID 
     * @param date 
     * @return a new price object with no values that is already added to this file
     */
    GnuCashWritablePrice createWritablePrice(GCshCmdtyCurrID fromCmdtyCurrID, GCshCurrID toCurrID,
											 LocalDate date);

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
