package org.gnucash.read;

import java.util.Collection;
import java.util.Locale;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.GncV2.GncBook.GncGncJob.JobOwner;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.WrongInvoiceTypeException;


/**
 * A job needs to be done. Once it or a part of it<br>
 * is done an invoice can be created and later be Paid by the customer<br>
 * of this job.
 * @see GnucashGenerInvoice
 * @see GnucashCustomer
 */
public interface GnucashGenerJob {

    public static final GCshOwner.Type TYPE_CUSTOMER = GCshOwner.Type.CUSTOMER;
    public static final GCshOwner.Type TYPE_VENDOR   = GCshOwner.Type.VENDOR;
    public static final GCshOwner.Type TYPE_EMPLOYEE = GCshOwner.Type.EMPLOYEE;

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    GncV2.GncBook.GncGncJob getJwsdpPeer();

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashFile getFile();

    // -----------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getId();

    /**
     * @return true if the job is still active
     */
    boolean isActive();

    /**
     *
     * @return the user-defines number of this job (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @return the name the user gave to this job.
     */
    String getName();

    // ----------------------------

    /**
     * Not used.
     * 
     * @return CUSTOMETYPE_CUSTOMER
     */
    GCshOwner.Type getOwnerType();

    /**
     *
     * @return the id of the customer this job is from.
     * @see #getCustomer()
     */
    GCshID getOwnerId();
    
    // ---------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getIncomeGenerated() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted(Locale lcl) throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     */
    FixedPointNumber getOutstandingValue() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted() throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    /**
     *
     * @throws UnknownAccountTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws WrongInvoiceTypeException
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl) throws UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ---------------------------------------------------------------

    Collection<GnucashJobInvoice> getInvoices() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    Collection<GnucashJobInvoice> getPaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    Collection<GnucashJobInvoice> getUnpaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException;

    // ---------------------------------------------------------------

    public static int getHighestNumber(GnucashCustomer cust) {
	return cust.getGnucashFile().getHighestJobNumber();
    }

    public static String getNewNumber(GnucashCustomer cust) {
	return cust.getGnucashFile().getNewJobNumber();
    }

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    JobOwner getOwnerPeerObj();

}
