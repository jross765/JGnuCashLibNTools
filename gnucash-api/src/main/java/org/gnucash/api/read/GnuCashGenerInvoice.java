package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.List;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnuCashGenerInvoice_Cust;
import org.gnucash.api.read.hlp.GnuCashGenerInvoice_Empl;
import org.gnucash.api.read.hlp.GnuCashGenerInvoice_Job;
import org.gnucash.api.read.hlp.GnuCashGenerInvoice_Vend;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasAttachment;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * This class represents a generic invoice.
 * It usually (altough not right after creation) contains
 * one or several "entries" (line items, {@link GnuCashGenerInvoiceEntry}).
 * <br>
 * Please note: In GnuCash lingo, an "invoice" does not precisely meet the 
 * normal definition of the business term 
 * (<a href="https://en.wikipedia.org/wiki/Invoice">Wikipedia</a>).
 * Rather, it is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer invoice  ({@link GnuCashCustomerInvoice})</li>
 *   <li>a vendor bill       ({@link GnuCashVendorBill})</li>
 *   <li>an employee voucher ({@link GnuCashEmployeeVoucher})</li>
 *   <li>a job invoice       ({@link GnuCashJobInvoice})</li>
 * </ul>
 * This is the reason why here, we call the invoice "generic" in order to avoid
 * misunderstandings. 
 * <br>
 * Please note that it normally should be avoided to use it directly; 
 * instead, use one of its specialized variants. 
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the Invoice was
 * created and secondarily on the date it should be paid.
 * <br>
 * Cf. <a href="https://gnucash.org/docs/v5/C/gnucash-manual/busnss-ar-invoices1.html">GnuCash manual</a>
 *
 * @see GnuCashCustomerInvoice
 * @see GnuCashEmployeeVoucher
 * @see GnuCashVendorBill
 * @see GnuCashJobInvoice
 */
public interface GnuCashGenerInvoice extends Comparable<GnuCashGenerInvoice>,
                                             GnuCashGenerInvoice_Cust,
                                             GnuCashGenerInvoice_Vend,
                                             GnuCashGenerInvoice_Empl,
                                             GnuCashGenerInvoice_Job,
                                             GnuCashObject,
                                             HasUserDefinedAttributes,
                                             HasAttachment
{

    // For the following types cf.:
    // -
    // https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/gncInvoice.h

    public static final GCshOwner.Type TYPE_CUSTOMER = GCshOwner.Type.CUSTOMER;
    public static final GCshOwner.Type TYPE_VENDOR   = GCshOwner.Type.VENDOR;
    public static final GCshOwner.Type TYPE_EMPLOYEE = GCshOwner.Type.EMPLOYEE;
    public static final GCshOwner.Type TYPE_JOB      = GCshOwner.Type.JOB;

    // ------------------------------

    public enum ReadVariant {
        /**
         * The entity that directly owns the
         * invoice, be it a customer invoice,
         * a vendor bill or a job invoice (thus,
         * the customer's / vendor's / job's ID.
         */
		DIRECT,
		
		/**
		 * If it's a job invoice, then this option means
		 * that we want the ID of the customer / vendor
		 * who is the owner of the job (depending of the
		 * job's type).
		 */
		VIA_JOB 
    }

    // -----------------------------------------------------------------

    /**
     *
     * @return the unique-id to identify this object with across name- and
     *         hirarchy-changes
     */
    GCshID getID();

    GCshOwner.Type getType();

    /**
     * @return the user-defined description for this object (may contain multiple
     *         lines and non-ascii-characters)
     */
    String getDescription();

    // ----------------------------

    @SuppressWarnings("exports")
    GncGncInvoice getJwsdpPeer();

    // ---------------------------------------------------------------

    /**
     * @return the date when this transaction was added to or modified in the books.
     */
    ZonedDateTime getDateOpened();

    /**
     * @return the date when this transaction was added to or modified in the books.
     */
    String getDateOpenedFormatted();

    /**
     * @return the date when this transaction happened.
     */
    ZonedDateTime getDatePosted();

    /**
     * @return the date when this transaction happened.
     */
    String getDatePostedFormatted();

    /**
     * @return the lot-id that identifies transactions to belong to an invoice with
     *         that lot-id.
     */
    GCshID getLotID();

    /**
     *
     * @return the user-defines number of this invoice (may contain non-digits)
     */
    String getNumber();

    /**
     *
     * @param readvar
     * @return Invoice' owner ID
     */
    GCshID getOwnerID(ReadVariant readvar);

    GCshOwner.Type getOwnerType(ReadVariant readvar);

    // ---------------------------------------------------------------

    /**
     * @return the id of the {@link GnuCashAccount} the payment is made to.
     */
    GCshID getPostAccountID();

    /**
     * @return ID of the (generic) invoice's posting transaction (if it exists)
     *  
     * @see #getPostAccount()
     * @see #getPostTransactionID()
     * @see #getPostTransaction()
     */
    GCshID getPostTransactionID();

    // ---------------------------------------------------------------

    /**
     * @return ID of the (generic) invoice's posting account (the one
     * targeted by the posting transaction, if it exists).
     * 
     * @see #getPostTransactionID()
     * @see #getPostTransaction()
     */
    GnuCashAccount getPostAccount();

    /**
     * @return the transaction that transferes the money from the customer to the
     *         account for money you are to get and the one you owe the taxes.
     *         
     * @see #getPostAccount()
     * @see #getPostTransactionID()
     * @see #getPostTransaction()
     */
    GnuCashTransaction getPostTransaction();

    /**
     *
     * @return the transactions the customer Paid this invoice vis.
     */
    List<? extends GnuCashTransaction> getPayingTransactions();

    /**
     *
     * @param trans a transaction the customer Paid a part of this invoice vis.
     */
    void addPayingTransaction(GnuCashTransactionSplit trans);

    /**
     *
     * @param trans a transaction that is the transaction due to handing out this
     *              invoice
     */
    void addTransaction(GnuCashTransaction trans);

    // ---------------------------------------------------------------

    /**
     * Look for an entry by it's id.
     * 
     * @param entrID the id to look for
     * @return the Entry found or null
     */
    GnuCashGenerInvoiceEntry getGenerEntryByID(GCshID entrID);

    /**
     *
     * @return the content of the invoice
     */
    List<GnuCashGenerInvoiceEntry> getGenerEntries();

    /**
     * This method is used internally during the loading of a file.
     * 
     * @param entry the entry to ad during loading.
     */
    void addGenerEntry(GnuCashGenerInvoiceEntry entry);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    InvoiceOwner getOwnerPeerObj();

}
