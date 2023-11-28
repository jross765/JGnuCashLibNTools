package org.gnucash.api.read.impl.spec;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashCustomerInvoiceEntryImpl extends GnucashGenerInvoiceEntryImpl
                                             implements GnucashCustomerInvoiceEntry 
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCustomerInvoiceEntryImpl.class);

  @SuppressWarnings("exports")
  public GnucashCustomerInvoiceEntryImpl(
          final GnucashCustomerInvoice invoice,
          final GncV2.GncBook.GncGncEntry peer) 
  {
    super(invoice, peer, true);
  }

  @SuppressWarnings("exports")
  public GnucashCustomerInvoiceEntryImpl(
          final GnucashGenerInvoice invoice,
          final GncV2.GncBook.GncGncEntry peer) throws WrongInvoiceTypeException 
  {
    super(invoice, peer, true);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( invoice.getType() != GCshOwner.Type.CUSTOMER )
      throw new WrongInvoiceTypeException();
  }

  @SuppressWarnings("exports")
  public GnucashCustomerInvoiceEntryImpl(final GncV2.GncBook.GncGncEntry peer, final GnucashFileImpl gncFile) 
  {
    super(peer, gncFile);
  }

  public GnucashCustomerInvoiceEntryImpl(final GnucashGenerInvoiceEntry entry) throws WrongInvoiceTypeException
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);

    // No, we cannot check that first, because the super() method
    // always has to be called first.
    if ( entry.getType() != GCshOwner.Type.CUSTOMER )
      throw new WrongInvoiceTypeException();
  }

  public GnucashCustomerInvoiceEntryImpl(final GnucashCustomerInvoiceEntry entry)
  {
    super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false);
  }

  // ---------------------------------------------------------------

  public GCshID getInvoiceID()
  {
    return getGenerInvoiceID();
  }
  
  @Override
  public GnucashCustomerInvoice getInvoice() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException
  {
    if ( myInvoice == null )
    {
      myInvoice = getGenerInvoice();
      if ( myInvoice.getType() != GCshOwner.Type.CUSTOMER )
        throw new WrongInvoiceTypeException();
        
      if ( myInvoice == null )
      {
        throw new IllegalStateException(
            "No customer invoice with id '" + getInvoiceID()
            + "' for invoice entry with id '" + getId() + "'");
      }
    }
    
    return new GnucashCustomerInvoiceImpl(myInvoice);
  }

  // ---------------------------------------------------------------

  @Override
  public FixedPointNumber getPrice() throws WrongInvoiceTypeException {
    return getInvcPrice();
  }

  @Override
  public String getPriceFormatted() throws WrongInvoiceTypeException {
      return getInvcPriceFormatted();
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
    buffer.append("[GnucashCustomerInvoiceEntryImpl:");
    buffer.append(" id: ");
    buffer.append(getId());
    buffer.append(" invoice-id: ");
    buffer.append(getInvoiceID());
    //      buffer.append(" invoice: ");
    //      GnucashCustomerInvoice invc = getInvoice();
    //      buffer.append(invoice==null?"null":invoice.getName());
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
