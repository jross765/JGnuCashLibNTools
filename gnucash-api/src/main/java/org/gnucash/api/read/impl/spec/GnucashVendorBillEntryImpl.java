package org.gnucash.api.read.impl.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashVendorBill;
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashVendorBillEntryImpl extends GnucashGenerInvoiceEntryImpl
                                        implements GnucashVendorBillEntry 
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashVendorBillEntryImpl.class);

  @SuppressWarnings("exports")
  public GnucashVendorBillEntryImpl(
          final GnucashVendorBill invoice,
          final GncV2.GncBook.GncGncEntry peer) 
  {
    super(invoice, peer, true);
  }

  @SuppressWarnings("exports")
  public GnucashVendorBillEntryImpl(
          final GnucashGenerInvoice invoice,
          final GncV2.GncBook.GncGncEntry peer) throws WrongInvoiceTypeException 
  {
    super(invoice, peer, true);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invoice.getType() != GCshOwner.Type.VENDOR )
      throw new WrongInvoiceTypeException();
  }

  @SuppressWarnings("exports")
  public GnucashVendorBillEntryImpl(final GncV2.GncBook.GncGncEntry peer, final GnucashFileImpl gncFile) 
  {
    super(peer, gncFile);
  }

  public GnucashVendorBillEntryImpl(final GnucashGenerInvoiceEntry entry) throws WrongInvoiceTypeException
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( entry.getType() != GnucashGenerInvoice.TYPE_VENDOR )
      throw new WrongInvoiceTypeException();
  }

  public GnucashVendorBillEntryImpl(final GnucashVendorBillEntry entry)
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
  }

  // ---------------------------------------------------------------

  public GCshID getBillID()
  {
    return getGenerInvoiceID();
  }
  
  @Override
  public GnucashVendorBill getBill() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    if ( myInvoice == null )
    {
      myInvoice = getGenerInvoice();
      if ( myInvoice.getType() != GCshOwner.Type.VENDOR )
        throw new WrongInvoiceTypeException();
        
      if ( myInvoice == null )
      {
        throw new IllegalStateException(
            "No vendor bill with id '" + getBillID()
            + "' for bill entry with id '" + getId() + "'");
      }
    }
    
    return new GnucashVendorBillImpl(myInvoice);
  }

  // ---------------------------------------------------------------

  @Override
  public FixedPointNumber getPrice() throws WrongInvoiceTypeException {
    return getBillPrice();
  }

  @Override
  public String getPriceFormatted() throws WrongInvoiceTypeException {
      return getBillPriceFormatted();
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

  // ------------------------------

  @Override
  public FixedPointNumber getVoucherPrice() throws WrongInvoiceTypeException {
    throw new WrongInvoiceTypeException();
  }

  @Override
  public String getVoucherPriceFormatted() throws WrongInvoiceTypeException {
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
    buffer.append("[GnucashVendorBillEntryImpl:");
    buffer.append(" id: ");
    buffer.append(getId());
    buffer.append(" bill-id: ");
    buffer.append(getBillID());
    //      buffer.append(" bill: ");
    //      GnucashVendorBill bill = getBill();
    //      buffer.append(invoice==null?"null":bill.getName());
    buffer.append(" description: '");
    buffer.append(getDescription() + "'");
    buffer.append(" date: ");
    try {
	buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
    }
    catch (Exception e) {
	buffer.append(getDate().toLocalDate().toString());
    }
    buffer.append(" action: '");
    try {
	buffer.append(getAction() + "'");
    } catch (Exception e) {
	buffer.append("ERROR" + "'");
    }
    buffer.append(" price: ");
    try
    {
      buffer.append(getPrice());
    }
    catch (WrongInvoiceTypeException e)
    {
      buffer.append("ERROR");
    }
    buffer.append(" quantity: ");
    buffer.append(getQuantity());
    buffer.append("]");
    return buffer.toString();
  }

}