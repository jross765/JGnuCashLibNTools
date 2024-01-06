package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Cust;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Empl;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Job;
import org.gnucash.api.read.hlp.GnucashGenerInvoice_Vend;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * This class represents an invoice that is sent to a customer
 * so (s)he knows what to pay you. <br>
 * <br>
 * Implementations of this interface are comparable and sorts primarily on the date the Invoice was
 * created and secondarily on the date it should be paid.
 *
 * @see GnucashGenerJob
 * @see GnucashCustomer
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
    public static final GCshOwner.Type TYPE_VENDOR = GCshOwner.Type.VENDOR;
    public static final GCshOwner.Type TYPE_EMPLOYEE = GCshOwner.Type.EMPLOYEE; // Not used yet, for future releases
    public static final GCshOwner.Type TYPE_JOB = GCshOwner.Type.JOB;

    // ------------------------------

    public enum ReadVariant {
	DIRECT, // The entity that directly owns the
		// invoice, be it a customer invoice,
		// a vendor bill or a job invoice (thus,
		// the customer's / vendor's / job's ID.
	VIA_JOB, // If it's a job invoice, then this option means
		 // that we want the ID of the customer / vendor
		 // who is the owner of the job (depending of the
		 // job's type).
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

//    /**
//    *
//    * @return Invoice' owner structure 
//    */
//    InvoiceOwner getOwner();

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
    Collection<? extends GnucashTransaction> getPayingTransactions();

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
    Collection<GnucashGenerInvoiceEntry> getGenerEntries();

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
