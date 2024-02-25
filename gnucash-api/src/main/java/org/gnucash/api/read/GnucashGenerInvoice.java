package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.List;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Cust;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Empl;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Job;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Vend;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshID;

/**
 * This class represents a generic invoice.
 * <br>
 * Please note: In GnuCash lingo, an "invoice" does not precisely meet the 
 * normal definition of the business term 
 * (<a href="https://en.wikipedia.org/wiki/Invoice">Wikipedia</a>).
 * Rather, it is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer invoice  ({@link GnucashCustomerInvoice})</li>
 *   <li>a vendor bill       ({@link GnucashVendorBill})</li>
 *   <li>an employee voucher ({@link GnucashEmployeeVoucher})</li>
 *   <li>a job invoice       ({@link GnucashJobInvoice})</li>
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
 * @see GnucashCustomerInvoice
 * @see GnucashEmployeeVoucher
 * @see GnucashVendorBill
 * @see GnucashJobInvoice
 */
public interface GnucashGenerInvoice extends Comparable<GnucashGenerInvoice>,
                                             GnucashGenerInvoice_Cust,
                                             GnucashGenerInvoice_Vend,
                                             GnucashGenerInvoice_Empl,
                                             GnucashGenerInvoice_Job,
                                             HasAttachment
{

    // For the following types cf.:
    // -
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gncInvoice.h

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

    /**
     * The gnucash-file is the top-level class to contain everything.
     * 
     * @return the file we are associated with
     */
    GnucashFile getFile();

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
     * @throws WrongInvoiceTypeException
     */
    GCshID getOwnerID(ReadVariant readvar) throws WrongInvoiceTypeException;

    GCshOwner.Type getOwnerType(ReadVariant readvar) throws WrongInvoiceTypeException;

    // ---------------------------------------------------------------

    /**
     * @return the id of the {@link GnucashAccount} the payment is made to.
     */
    GCshID getPostAccountID();

    /**
     * @return
     */
    GCshID getPostTransactionID();

    // ---------------------------------------------------------------

    /**
     * @return
     */
    GnucashAccount getPostAccount();

    /**
     * @return the transaction that transferes the money from the customer to the
     *         account for money you are to get and the one you owe the taxes.
     */
    GnucashTransaction getPostTransaction();

    /**
     *
     * @return the transactions the customer Paid this invoice vis.
     */
    List<? extends GnucashTransaction> getPayingTransactions();

    /**
     *
     * @param trans a transaction the customer Paid a part of this invoice vis.
     */
    void addPayingTransaction(GnucashTransactionSplit trans);

    /**
     *
     * @param trans a transaction that is the transaction due to handing out this
     *              invoice
     */
    void addTransaction(GnucashTransaction trans);

    // ---------------------------------------------------------------

    /**
     * Look for an entry by it's id.
     * 
     * @param entrID the id to look for
     * @return the Entry found or null
     */
    GnucashGenerInvoiceEntry getGenerEntryByID(GCshID entrID);

    /**
     *
     * @return the content of the invoice
     * @see ${@link GnucashGenerInvoiceEntry}
     */
    List<GnucashGenerInvoiceEntry> getGenerEntries();

    /**
     * This method is used internally during the loading of a file.
     * 
     * @param entry the entry to ad during loading.
     */
    void addGenerEntry(GnucashGenerInvoiceEntry entry);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    InvoiceOwner getOwnerPeerObj();

}
