package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.GnucashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnucashVendorJob;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncV2.GncBook.GncGncInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashJobInvoiceImpl extends GnucashGenerInvoiceImpl
                                   implements GnucashJobInvoice,
                                              SpecInvoiceCommon
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashJobInvoiceImpl.class);

  @SuppressWarnings("exports")
  public GnucashJobInvoiceImpl(final GncGncInvoice peer, final GnucashFile gncFile)
  {
    super(peer, gncFile);
  }

  public GnucashJobInvoiceImpl(final GnucashGenerInvoice invc) throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    super(invc.getJwsdpPeer(), invc.getFile());

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER  &&
	 invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.JOB )
      throw new WrongInvoiceTypeException();
    
    for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() )
    {
      addEntry(new GnucashJobInvoiceEntryImpl(entry));
    }

    for ( GnucashTransaction trx : invc.getPayingTransactions() )
    {
      for ( GnucashTransactionSplit splt : trx.getSplits() ) 
      {
        GCshID lot = splt.getLotID();
        if ( lot != null ) {
            for ( GnucashGenerInvoice invc1 : splt.getTransaction().getGnucashFile().getGenerInvoices() ) {
                GCshID lotID = invc1.getLotID();
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

  /**
   * {@inheritDoc}
   */
  @Override
  public GCshID getJobId() {
    return getOwnerId_direct();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GCshOwner.Type getJobType() {
      return getGenerJob().getOwnerType();
  }

  // -----------------------------------------------------------------

  @Override
  public GCshID getOwnerId(ReadVariant readVar) {
      if (readVar == ReadVariant.DIRECT)
	  return getOwnerId_direct();
      else if (readVar == ReadVariant.VIA_JOB)
	  return getOwnerId_viaJob();

      return null; // Compiler happy
  }

  @Override
  protected GCshID getOwnerId_viaJob() {
      return getGenerJob().getOwnerId();
  }

  // -----------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public GCshID getCustomerId() throws WrongInvoiceTypeException {
    if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_CUSTOMER )
	throw new WrongInvoiceTypeException();
    
    return getOwnerId_viaJob();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GCshID getVendorId() throws WrongInvoiceTypeException {
    if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_VENDOR )
	throw new WrongInvoiceTypeException();
    
    return getOwnerId_viaJob();
  }

  // ----------------------------
  
  /**
   * {@inheritDoc}
   */
  @Override
  public GnucashGenerJob getGenerJob() 
  {
      return file.getGenerJobByID(getJobId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GnucashCustomerJob getCustJob() throws WrongInvoiceTypeException
  {
      if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_CUSTOMER )
	  throw new WrongInvoiceTypeException();
      
      return new GnucashCustomerJobImpl(getGenerJob());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GnucashVendorJob getVendJob() throws WrongInvoiceTypeException
  {
      if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_VENDOR )
	  throw new WrongInvoiceTypeException();

      return new GnucashVendorJobImpl(getGenerJob());
  }

  // ------------------------------

  /**
   * {@inheritDoc}
   */
  @Override
  public GnucashCustomer getCustomer() throws WrongInvoiceTypeException {
      if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_CUSTOMER )
		throw new WrongInvoiceTypeException();
      
      return getFile().getCustomerByID(getCustomerId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GnucashVendor getVendor() throws WrongInvoiceTypeException {
      if ( getGenerJob().getOwnerType() != GnucashGenerJob.TYPE_VENDOR )
		throw new WrongInvoiceTypeException();

      return getFile().getVendorByID(getVendorId());
  }

  // ---------------------------------------------------------------

  @Override
  public GnucashJobInvoiceEntry getEntryById(GCshID id) throws WrongInvoiceTypeException
  {
    return new GnucashJobInvoiceEntryImpl(getGenerEntryById(id));
  }

  @Override
  public Collection<GnucashJobInvoiceEntry> getEntries() throws WrongInvoiceTypeException
  {
    Collection<GnucashJobInvoiceEntry> castEntries = new HashSet<GnucashJobInvoiceEntry>();
    
    for ( GnucashGenerInvoiceEntry entry : getGenerEntries() )
    {
      if ( entry.getType() == GCshOwner.Type.JOB )
      {
        castEntries.add(new GnucashJobInvoiceEntryImpl(entry));
      }
    }
    
    return castEntries;
  }

  @Override
  public void addEntry(final GnucashJobInvoiceEntry entry)
  {
    addGenerEntry(entry);
  }

  // -----------------------------------------------------------------

  @Override
  public FixedPointNumber getAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountUnpaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountPaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountPaidWithoutTaxes();
  }

  @Override
  public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountWithTaxes();
  }
  
  @Override
  public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountWithoutTaxes();
  }

  @Override
  public String getAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountUnpaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountPaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    // return getJobAmountPaidWithoutTaxesFormatted();
      return null;
  }

  @Override
  public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountWithTaxesFormatted();
  }

  @Override
  public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return getJobAmountWithoutTaxesFormatted();
  }
  
  // ------------------------------
  
  @Override
  public boolean isFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return isJobFullyPaid();
  }
  
  @Override
  public boolean isNotFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    return isNotJobFullyPaid();
  }
  
  // ------------------------------

//  @Override
//  public FixedPointNumber getInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  @Override
//  public FixedPointNumber getInvcAmountWithoutTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public FixedPointNumber getBillAmountUnpaidWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountPaidWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountPaidWithoutTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountWithTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  @Override
//  public FixedPointNumber getBillAmountWithoutTaxes() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountWithTaxesFormatted() throws WrongInvoiceTypeException 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public boolean isInvcFullyPaid() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public boolean isNotInvcFullyPaid() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public boolean isBillFullyPaid() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public boolean isNotBillFullyPaid() throws WrongInvoiceTypeException
//  {
//    throw new WrongInvoiceTypeException();
//  }
  
  // -----------------------------------------------------------------

  @Override
  public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[GnucashJobInvoiceImpl:");
      buffer.append(" id: ");
      buffer.append(getId());
      buffer.append(" job-id: ");
      buffer.append(getJobId());
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