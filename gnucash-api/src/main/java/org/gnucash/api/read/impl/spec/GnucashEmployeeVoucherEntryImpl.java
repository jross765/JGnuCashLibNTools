package org.gnucash.api.read.impl.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashEmployeeVoucherEntryImpl extends GnucashGenerInvoiceEntryImpl
                                             implements GnucashEmployeeVoucherEntry 
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashEmployeeVoucherEntryImpl.class);

  @SuppressWarnings("exports")
  public GnucashEmployeeVoucherEntryImpl(
          final GnucashEmployeeVoucher invoice,
          final GncV2.GncBook.GncGncEntry peer) 
  {
    super(invoice, peer, true);
  }

  @SuppressWarnings("exports")
  public GnucashEmployeeVoucherEntryImpl(
          final GnucashGenerInvoice invoice,
          final GncV2.GncBook.GncGncEntry peer) throws WrongInvoiceTypeException 
  {
    super(invoice, peer, true);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invoice.getType() != GCshOwner.Type.EMPLOYEE )
      throw new WrongInvoiceTypeException();
  }

  @SuppressWarnings("exports")
  public GnucashEmployeeVoucherEntryImpl(final GncV2.GncBook.GncGncEntry peer, final GnucashFileImpl gncFile) 
  {
    super(peer, gncFile, true);
  }

  public GnucashEmployeeVoucherEntryImpl(final GnucashGenerInvoiceEntry entry) throws WrongInvoiceTypeException
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( entry.getType() != GnucashGenerInvoice.TYPE_EMPLOYEE )
      throw new WrongInvoiceTypeException();
  }

  public GnucashEmployeeVoucherEntryImpl(final GnucashEmployeeVoucherEntry entry)
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
  }

  // ---------------------------------------------------------------

  public GCshID getVoucherID()
  {
    return getGenerInvoiceID();
  }
  
  @Override
  public GnucashEmployeeVoucher getVoucher() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    if ( myInvoice == null )
    {
      myInvoice = getGenerInvoice();
      if ( myInvoice.getType() != GCshOwner.Type.EMPLOYEE )
        throw new WrongInvoiceTypeException();
        
      if ( myInvoice == null )
      {
        throw new IllegalStateException(
            "No employee voucher with id '" + getVoucherID()
            + "' for voucher entry with id '" + getID() + "'");
      }
    }
    
    return new GnucashEmployeeVoucherImpl(myInvoice);
  }

  // ---------------------------------------------------------------

  @Override
  public FixedPointNumber getPrice() throws WrongInvoiceTypeException {
    return getVoucherPrice();
  }

  @Override
  public String getPriceFormatted() throws WrongInvoiceTypeException {
      return getVoucherPriceFormatted();
  }
  
  // ---------------------------------------------------------------

  @Override
  public FixedPointNumber getInvcPrice() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getInvcPriceFormatted() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  // ---------------------------------------------------------------

  @Override
  public FixedPointNumber getBillPrice() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getBillPriceFormatted() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  // ------------------------------

  @Override
  public FixedPointNumber getJobPrice() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getJobPriceFormatted() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  // ---------------------------------------------------------------

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("GnucashEmployeeVoucherEntryImpl [");
    
    buffer.append("id=");
    buffer.append(getID());
    
    buffer.append(", voucher-id=");
    buffer.append(getVoucherID());

    buffer.append(", description='");
    buffer.append(getDescription() + "'");
    
    buffer.append(", date=");
    try {
	buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
    }
    catch (Exception e) {
	buffer.append(getDate().toLocalDate().toString());
    }
    
    buffer.append(", action='");
    try {
	buffer.append(getAction() + "'");
    } catch (Exception e) {
	buffer.append("ERROR" + "'");
    }
    
    buffer.append(", price=");
    try
    {
      buffer.append(getPrice());
    } catch (WrongInvoiceTypeException e) {
      buffer.append("ERROR");
    }
    
    buffer.append(", quantity=");
    buffer.append(getQuantity());
    
    buffer.append("]");
    return buffer.toString();
  }

}
