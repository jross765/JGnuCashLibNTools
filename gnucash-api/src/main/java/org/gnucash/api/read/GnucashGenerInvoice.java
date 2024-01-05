package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.aux.GCshTaxedSumImpl;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncGncInvoice.InvoiceOwner;

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
                                             HasAttachment
{
  
  // For the following types cf.:
  //  - https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gncInvoice.h
  
  public static final GCshOwner.Type TYPE_CUSTOMER = GCshOwner.Type.CUSTOMER;
  public static final GCshOwner.Type TYPE_VENDOR   = GCshOwner.Type.VENDOR;
  public static final GCshOwner.Type TYPE_EMPLOYEE = GCshOwner.Type.EMPLOYEE; // Not used yet, for future releases
  public static final GCshOwner.Type TYPE_JOB      = GCshOwner.Type.JOB;
  
  // ------------------------------

  public enum ReadVariant {
    DIRECT,  // The entity that directly owns the
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

  // ----------------------------

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
   * @return what the customer must still pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the customer needs to pay in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getInvcAmountWithTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the customer needs to pay in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getInvcAmountWithoutTaxes() throws WrongInvoiceTypeException;

  // ----------------------------

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the customer must still pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the customer needs to pay in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * @return what the customer needs to pay in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  // ---------------------------------------------------------------

  /**
   * @return what the vendor is yet to receive (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getBillAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the vendor has already received (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getBillAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the vendor has already received (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getBillAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the vendor receives in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getBillAmountWithTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the vendor receives in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getBillAmountWithoutTaxes() throws WrongInvoiceTypeException;

  // ---------------------------------------------------------------

  /**
   * @return what the employee is yet to receive (incl. taxes)
   * @throws WrongInvoiceTypeException
   * @throws UnknownAccountTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getVoucherAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the employee has already received (incl. taxes)
   * @throws WrongInvoiceTypeException
   * @throws UnknownAccountTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getVoucherAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the employee has already received (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getVoucherAmountPaidWithoutTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the employee receives in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getVoucherAmountWithTaxes() throws WrongInvoiceTypeException;

  /**
   * @return what the employee receives in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  FixedPointNumber getVoucherAmountWithoutTaxes() throws WrongInvoiceTypeException;

  // ----------------------------

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the vendor is still to receive (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getBillAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the vendor already has received (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getBillAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the vendor already has received (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getBillAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the vendor will receive in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getBillAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * @return what the vendor will receive in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getBillAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  // ----------------------------

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the employee is still to receive (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getVoucherAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the employee already has received (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getVoucherAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the employee already has received (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getVoucherAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the employee will receive in in total (incl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getVoucherAmountWithTaxesFormatted() throws WrongInvoiceTypeException;

  /**
   * @return what the employee will receive in total (excl. taxes)
   * @throws WrongInvoiceTypeException
   */
  String getVoucherAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException;

  // ---------------------------------------------------------------

  /**
   * @return what the customer must still pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getJobAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getJobAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getJobAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

  /**
   * @return what the customer needs to pay in total (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getJobAmountWithTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

  /**
   * @return what the customer needs to pay in total (excl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  FixedPointNumber getJobAmountWithoutTaxes() throws WrongInvoiceTypeException, IllegalArgumentException;

// ----------------------------

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the customer must still pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getJobAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws UnknownAccountTypeException 
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getJobAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  /**
   * @return what the customer has already pay (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getJobAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

  /**
   * Formating uses the default-locale's currency-format.
   * 
   * @return what the customer needs to pay in total (incl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getJobAmountWithTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

  /**
   * @return what the customer needs to pay in total (excl. taxes)
   * @throws WrongInvoiceTypeException
 * @throws 
 * @throws IllegalArgumentException 
 * @throws ClassNotFoundException 
 * @throws SecurityException 
 * @throws NoSuchFieldException 
   */
  String getJobAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, IllegalArgumentException;

  // ---------------------------------------------------------------

  /**
   *
   * @return For a customer invoice: How much sales-taxes are to pay.
   * @throws WrongInvoiceTypeException
   * @see GCshTaxedSumImpl
   */
  GCshTaxedSumImpl[] getInvcTaxes() throws WrongInvoiceTypeException;

  /**
   *
   * @return For a vendor bill: How much sales-taxes are to pay.
   * @throws WrongInvoiceTypeException
   * @see GCshTaxedSumImpl
   */
  GCshTaxedSumImpl[] getBillTaxes() throws WrongInvoiceTypeException;

  /**
   *
   * @return For a vendor bill: How much sales-taxes are to pay.
   * @throws WrongInvoiceTypeException
   * @see GCshTaxedSumImpl
   */
  GCshTaxedSumImpl[] getVoucherTaxes() throws WrongInvoiceTypeException;

  // ---------------------------------------------------------------

  /**
   * @return the id of the {@link GnucashAccount} the payment is made to.
   */
  GCshID getPostAccountID();
  
  GCshID getPostTransactionID();

  // ---------------------------------------------------------------

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
   * @param id the id to look for
   * @return the Entry found or null
   */
  GnucashGenerInvoiceEntry getGenerEntryByID(GCshID id);

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

  boolean isInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  boolean isNotInvcFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  // ----------------------------

  boolean isBillFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  boolean isNotBillFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  // ----------------------------

  boolean isVoucherFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  boolean isNotVoucherFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  // ----------------------------

  boolean isJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  boolean isNotJobFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException;

  // ---------------------------------------------------------------

  @SuppressWarnings("exports")
  InvoiceOwner getOwnerPeerObj();

}
