package org.gnucash.api.read;

import java.util.List;
import java.util.Locale;

import org.gnucash.api.generated.GncGncJob;
import org.gnucash.api.generated.GncGncJob.JobOwner;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnucashObject;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;


/**
 * This class represents a generic job.
 * <br>
 * Please note: In GnuCash lingo, a "job" is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer job ({@link GnucashCustomerJob})</li>
 *   <li>a vendor job   ({@link GnucashVendorJob})</li>
 * </ul>
 * This is the reason why here, we call the invoice "generic" in order to avoid
 * misunderstandings. 
 * <br>
 * It normally should be avoided to use it directly; instead, use one of its 
 * specialized variants.
 *  
 * @see GnucashCustomerJob
 * @see GnucashVendorJob
 */
public interface GnucashGenerJob extends GnucashObject {

    public static final GCshOwner.Type TYPE_CUSTOMER = GCshOwner.Type.CUSTOMER;
    public static final GCshOwner.Type TYPE_VENDOR   = GCshOwner.Type.VENDOR;

    // -----------------------------------------------------------------

    @SuppressWarnings("exports")
    GncGncJob getJwsdpPeer();

    // -----------------------------------------------------------------

    /**
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

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
    GCshID getOwnerID();
    
    // ---------------------------------------------------------------

    /**
     * Date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     * 
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     */
    int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    /**
     * @return the sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getIncomeGenerated() throws UnknownAccountTypeException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated() Formatted according to the current locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted() throws UnknownAccountTypeException;

    /**
     * @param lcl 
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated() Formatted according to the given locale's
     *      currency-format
     */
    String getIncomeGeneratedFormatted(Locale lcl) throws UnknownAccountTypeException;

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    FixedPointNumber getOutstandingValue() throws UnknownAccountTypeException;

    /**
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getOutstandingValue() Formatted according to the current locale's
     *      currency-format
     */
    String getOutstandingValueFormatted() throws UnknownAccountTypeException;

    /**
     *
     * @return 
     * @throws UnknownAccountTypeException 
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    String getOutstandingValueFormatted(Locale lcl) throws UnknownAccountTypeException;

    // ---------------------------------------------------------------

    List<GnucashJobInvoice> getInvoices() throws WrongInvoiceTypeException;

    List<GnucashJobInvoice> getPaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    List<GnucashJobInvoice> getUnpaidInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException;

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    JobOwner getOwnerPeerObj();

}
