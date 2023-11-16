package org.gnucash.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2.GncBook.GncGncInvoice;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerInvoiceEntry;
import org.gnucash.read.GnucashTransaction;
import org.gnucash.read.GnucashTransactionSplit;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.read.spec.GnucashCustomerInvoice;
import org.gnucash.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.read.spec.SpecInvoiceCommon;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashCustomerInvoiceImpl extends GnucashGenerInvoiceImpl
                                        implements GnucashCustomerInvoice,
                                                   SpecInvoiceCommon
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCustomerInvoiceImpl.class);

  @SuppressWarnings("exports")
  public GnucashCustomerInvoiceImpl(final GncGncInvoice peer, final GnucashFile gncFile)
  {
    super(peer, gncFile);
  }

  public GnucashCustomerInvoiceImpl(final GnucashGenerInvoice invc) throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    super(invc.getJwsdpPeer(), invc.getFile());

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER  &&
	 invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.JOB )
      throw new WrongInvoiceTypeException();
    
    for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() )
    {
      addEntry(new GnucashCustomerInvoiceEntryImpl(entry));
    }

    for ( GnucashTransaction trx : invc.getPayingTransactions() )
    {
      for ( GnucashTransactionSplit splt : trx.getSplits() ) 
      {
        String lot = splt.getLotID();
        if ( lot != null ) {
            for ( GnucashGenerInvoice invc1 : splt.getTransaction().getGnucashFile().getGenerInvoices() ) {
                String lotID = invc1.getLotID();
                if ( lotID != null &&
                     lotID.equals(lot) ) {
                    // Check if it's a payment transaction. 
                    // If so, add it to the invoice's list of payment transactions.
                    if ( splt.getAction().equals(GnucashTransactionSplit.Action.PAYMENT.getLocaleString()) ) {
                        addPayingTransaction(splt);
                    }
                } // if lotID
            } // for invc
        } // if lot
      } // for splt
    } // for trx
  }
  
  // -----------------------------------------------------------------

  @Override
  public String getCustomerId() {
    return getOwnerId();
  }

  @Override
  public GnucashCustomer getCustomer() throws WrongInvoiceTypeException
  {
    return getCustomer_direct();
  }

  public GnucashCustomer getCustomer_direct() throws WrongInvoiceTypeException {
    if ( ! getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnucashGenerInvoice.TYPE_CUSTOMER.getCode()) )
      throw new WrongInvoiceTypeException();
    
    GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
    return file.getCustomerByID(ownerID);
  }

  // ---------------------------------------------------------------

  @Override
  public GnucashCustomerInvoiceEntry getEntryById(GCshID id) throws WrongInvoiceTypeException
  {
    return new GnucashCustomerInvoiceEntryImpl(getGenerEntryById(id));
  }

  @Override
  public Collection<GnucashCustomerInvoiceEntry> getEntries() throws WrongInvoiceTypeException
  {
    Collection<GnucashCustomerInvoiceEntry> castEntries = new HashSet<GnucashCustomerInvoiceEntry>();
    
    for ( GnucashGenerInvoiceEntry entry : getGenerEntries() )
    {
      if ( entry.getType() == GCshOwner.Type.CUSTOMER )
      {
        castEntries.add(new GnucashCustomerInvoiceEntryImpl(entry));
      }
    }
    
    return castEntries;
  }

  @Override
  public void addEntry(final GnucashCustomerInvoiceEntry entry)
  {
    addGenerEntry(entry);
  }

  // -----------------------------------------------------------------

  @Override
  public FixedPointNumber getAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getInvcAmountUnpaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getInvcAmountPaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException
  {
    return getInvcAmountPaidWithoutTaxes();
  }

  @Override
  public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException
  {
    return getInvcAmountWithTaxes();
  }
  
  @Override
  public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException
  {
    return getInvcAmountWithoutTaxes();
  }

  @Override
  public String getAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getInvcAmountUnpaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getInvcAmountPaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getInvcAmountPaidWithoutTaxesFormatted();
  }

  @Override
  public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getInvcAmountWithTaxesFormatted();
  }

  @Override
  public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getInvcAmountWithoutTaxesFormatted();
  }
  
  // ------------------------------
  
  @Override
  public boolean isFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return isInvcFullyPaid();
  }
  
  @Override
  public boolean isNotFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return isNotInvcFullyPaid();
  }
  
  // ------------------------------

  @Override
  public FixedPointNumber getBillAmountUnpaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getBillAmountPaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getBillAmountPaidWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getBillAmountWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }
  
  @Override
  public FixedPointNumber getBillAmountWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillAmountWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // ------------------------------

  @Override
  public FixedPointNumber getVoucherAmountUnpaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getVoucherAmountPaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getVoucherAmountPaidWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getVoucherAmountWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }
  
  @Override
  public FixedPointNumber getVoucherAmountWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherAmountWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // ------------------------------

  @Override
  public FixedPointNumber getJobAmountUnpaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getJobAmountPaidWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getJobAmountPaidWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getJobAmountWithTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }
  
  @Override
  public FixedPointNumber getJobAmountWithoutTaxes() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobAmountWithTaxesFormatted() throws WrongInvoiceTypeException 
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // ------------------------------

  @Override
  public boolean isBillFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public boolean isNotBillFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // ------------------------------

  @Override
  public boolean isVoucherFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public boolean isNotVoucherFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // ------------------------------

  @Override
  public boolean isJobFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public boolean isNotJobFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  // -----------------------------------------------------------------

  @Override
  public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[GnucashCustomerInvoiceImpl:");
      buffer.append(" id: ");
      buffer.append(getId());
      buffer.append(" customer-id: ");
      buffer.append(getCustomerId());
      buffer.append(" invoice-number: '");
      buffer.append(getNumber() + "'");
      buffer.append(" description: '");
      buffer.append(getDescription() + "'");
      buffer.append(" #entries: ");
      try {
        buffer.append(getEntries().size());
      }
      catch (WrongInvoiceTypeException e) {
        buffer.append("ERROR");
      }
      buffer.append(" date-opened: ");
      try {
        buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
      }
      catch (Exception e) {
        buffer.append(getDateOpened().toLocalDate().toString());
      }
      buffer.append("]");
      return buffer.toString();
  }

}
