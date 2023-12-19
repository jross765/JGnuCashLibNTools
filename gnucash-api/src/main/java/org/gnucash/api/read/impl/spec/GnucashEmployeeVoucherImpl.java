package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncV2.GncBook.GncGncInvoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashEmployeeVoucherImpl extends GnucashGenerInvoiceImpl
                                        implements GnucashEmployeeVoucher,
                                                   SpecInvoiceCommon
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashEmployeeVoucherImpl.class);

  @SuppressWarnings("exports")
  public GnucashEmployeeVoucherImpl(final GncGncInvoice peer, final GnucashFile gncFile)
  {
    super(peer, gncFile);
  }

  public GnucashEmployeeVoucherImpl(final GnucashGenerInvoice invc) throws WrongInvoiceTypeException, IllegalArgumentException
  {
    super(invc.getJwsdpPeer(), invc.getFile());

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_EMPLOYEE  &&
         invc.getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT) != GnucashGenerInvoice.TYPE_JOB )
      throw new WrongInvoiceTypeException();
    
    for ( GnucashGenerInvoiceEntry entry : invc.getGenerEntries() )
    {
      addEntry(new GnucashEmployeeVoucherEntryImpl(entry));
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
                    if ( splt.getAction() == GnucashTransactionSplit.Action.PAYMENT ) {
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
  public GCshID getEmployeeID() {
      return getOwnerID();
  }

  @Override
  public GnucashEmployee getEmployee() throws WrongInvoiceTypeException {
    return getEmployee_direct();
  }

  public GnucashEmployee getEmployee_direct() throws WrongInvoiceTypeException {
    if ( ! getJwsdpPeer().getInvoiceOwner().getOwnerType().equals(GnucashGenerInvoice.TYPE_EMPLOYEE.getCode()) )
      throw new WrongInvoiceTypeException();
    
    GCshID ownerID = new GCshID(getJwsdpPeer().getInvoiceOwner().getOwnerId().getValue());
    return file.getEmployeeByID(ownerID);
  }

  // ---------------------------------------------------------------

  @Override
  public GnucashEmployeeVoucherEntry getEntryByID(GCshID id) throws WrongInvoiceTypeException
  {
    return new GnucashEmployeeVoucherEntryImpl(getGenerEntryByID(id));
  }

  @Override
  public Collection<GnucashEmployeeVoucherEntry> getEntries() throws WrongInvoiceTypeException
  {
    Collection<GnucashEmployeeVoucherEntry> castEntries = new HashSet<GnucashEmployeeVoucherEntry>();
    
    for ( GnucashGenerInvoiceEntry entry : getGenerEntries() )
    {
      if ( entry.getType() == GnucashGenerInvoice.TYPE_EMPLOYEE )
      {
        castEntries.add(new GnucashEmployeeVoucherEntryImpl(entry));
      }
    }
    
    return castEntries;
  }

  @Override
  public void addEntry(final GnucashEmployeeVoucherEntry entry)
  {
    addGenerEntry(entry);
  }

  // -----------------------------------------------------------------

  @Override
  public FixedPointNumber getAmountUnpaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getVoucherAmountUnpaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithTaxes() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getVoucherAmountPaidWithTaxes();
  }

  @Override
  public FixedPointNumber getAmountPaidWithoutTaxes() throws WrongInvoiceTypeException
  {
    return getVoucherAmountPaidWithoutTaxes();
  }

  @Override
  public FixedPointNumber getAmountWithTaxes() throws WrongInvoiceTypeException
  {
    return getVoucherAmountWithTaxes();
  }
  
  @Override
  public FixedPointNumber getAmountWithoutTaxes() throws WrongInvoiceTypeException
  {
    return getVoucherAmountWithoutTaxes();
  }

  @Override
  public String getAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getVoucherAmountUnpaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return getVoucherAmountPaidWithTaxesFormatted();
  }

  @Override
  public String getAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getVoucherAmountPaidWithoutTaxesFormatted();
  }

  @Override
  public String getAmountWithTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getVoucherAmountWithTaxesFormatted();
  }

  @Override
  public String getAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    return getVoucherAmountWithoutTaxesFormatted();
  }
  
  // ------------------------------
  
  @Override
  public boolean isFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return isVoucherFullyPaid();
  }
  
  @Override
  public boolean isNotFullyPaid() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException
  {
    return isNotVoucherFullyPaid();
  }
  
  // ------------------------------

  @Override
  public FixedPointNumber getInvcAmountUnpaidWithTaxes() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getInvcAmountPaidWithTaxes() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getInvcAmountPaidWithoutTaxes() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public FixedPointNumber getInvcAmountWithTaxes() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }
  
  @Override
  public FixedPointNumber getInvcAmountWithoutTaxes() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcAmountUnpaidWithTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcAmountPaidWithTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcAmountPaidWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcAmountWithTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcAmountWithoutTaxesFormatted() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
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
  public boolean isInvcFullyPaid() throws WrongInvoiceTypeException
  {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public boolean isNotInvcFullyPaid() throws WrongInvoiceTypeException
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
      buffer.append("GnucashEmployeeVoucherImpl [");
      
      buffer.append("id=");
      buffer.append(getID());
      
      buffer.append(", employee-id=");
      buffer.append(getEmployeeID());
      
      buffer.append(", voucher-number='");
      buffer.append(getNumber() + "'");
      
      buffer.append(", description='");
      buffer.append(getDescription() + "'");
      
      buffer.append(", #entries=");
      try {
        buffer.append(getEntries().size());
      } catch (WrongInvoiceTypeException e) {
        buffer.append("ERROR");
      }
      
      buffer.append(", date-opened=");
      try {
        buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
      } catch (Exception e) {
        buffer.append(getDateOpened().toLocalDate().toString());
      }
      
      buffer.append("]");
      return buffer.toString();
  }

}
