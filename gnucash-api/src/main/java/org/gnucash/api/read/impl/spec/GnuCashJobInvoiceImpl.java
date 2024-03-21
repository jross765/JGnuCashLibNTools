package org.gnucash.api.read.impl.spec;

import java.util.Collection;
import java.util.HashSet;

import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashCustomerImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.GnuCashGenerJobImpl;
import org.gnucash.api.read.impl.GnuCashVendorImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.read.spec.WrongJobTypeException;
import org.gnucash.api.read.spec.hlp.SpecInvoiceCommon;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @see GnuCashCustomerInvoiceImpl
 * @see GnuCashEmployeeVoucherImpl
 * @see GnuCashVendorBillImpl
 * @see GnuCashGenerJobImpl
 * @see GnuCashCustomerImpl
 * @see GnuCashVendorImpl
 */
public class GnuCashJobInvoiceImpl extends GnuCashGenerInvoiceImpl
                                   implements GnuCashJobInvoice,
                                              SpecInvoiceCommon
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashJobInvoiceImpl.class);

	// ---------------------------------------------------------------

	@SuppressWarnings("exports")
	public GnuCashJobInvoiceImpl(final GncGncInvoice peer, final GnuCashFile gcshFile) {
		super(peer, gcshFile);
	}

	public GnuCashJobInvoiceImpl(final GnuCashGenerInvoice invc) {
		super(invc.getJwsdpPeer(), invc.getGnuCashFile());

		// No, we cannot check that first, because the super() method
		// always has to be called first.
		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.CUSTOMER
				&& invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT) != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		for ( GnuCashGenerInvoiceEntry entry : invc.getGenerEntries() ) {
			addEntry(new GnuCashJobInvoiceEntryImpl(entry));
		}

		for ( GnuCashTransaction trx : invc.getPayingTransactions() ) {
			for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
				GCshID lot = splt.getLotID();
				if ( lot != null ) {
					for ( GnuCashGenerInvoice invc1 : splt.getTransaction().getGnuCashFile().getGenerInvoices() ) {
						GCshID lotID = invc1.getLotID();
						if ( lotID != null && lotID.equals(lot) ) {
							// Check if it's a payment transaction.
							// If so, add it to the invoice's list of payment transactions.
							if ( splt.getAction() == GnuCashTransactionSplit.Action.PAYMENT ) {
								addPayingTransaction(splt);
							}
						} // if lotID
					} // for invc
				} // if lot
			} // for splt
		} // for trx
	}

	// ---------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCshID getJobID() {
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
	public GCshID getOwnerID(ReadVariant readVar) {
		if ( readVar == ReadVariant.DIRECT )
			return getOwnerId_direct();
		else if ( readVar == ReadVariant.VIA_JOB )
			return getOwnerId_viaJob();

		return null; // Compiler happy
	}

	@Override
	protected GCshID getOwnerId_viaJob() {
		return getGenerJob().getOwnerID();
	}

	// -----------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCshID getCustomerID() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_CUSTOMER )
			throw new WrongInvoiceTypeException();

		return getOwnerId_viaJob();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GCshID getVendorID() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_VENDOR )
			throw new WrongInvoiceTypeException();

		return getOwnerId_viaJob();
	}

	// ----------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashGenerJob getGenerJob() {
		return getGnuCashFile().getGenerJobByID(getJobID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashCustomerJob getCustJob() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_CUSTOMER )
			throw new WrongJobTypeException();

		return new GnuCashCustomerJobImpl(getGenerJob());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashVendorJob getVendJob() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_VENDOR )
			throw new WrongJobTypeException();

		return new GnuCashVendorJobImpl(getGenerJob());
	}

	// ------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashCustomer getCustomer() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_CUSTOMER )
			throw new WrongInvoiceTypeException();

		return getGnuCashFile().getCustomerByID(getCustomerID());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GnuCashVendor getVendor() {
		if ( getGenerJob().getOwnerType() != GnuCashGenerJob.TYPE_VENDOR )
			throw new WrongInvoiceTypeException();

		return getGnuCashFile().getVendorByID(getVendorID());
	}

	// ---------------------------------------------------------------

	@Override
	public GnuCashJobInvoiceEntry getEntryByID(GCshID id) {
		return new GnuCashJobInvoiceEntryImpl(getGenerEntryByID(id));
	}

	@Override
	public Collection<GnuCashJobInvoiceEntry> getEntries() {
		Collection<GnuCashJobInvoiceEntry> castEntries = new HashSet<GnuCashJobInvoiceEntry>();

		for ( GnuCashGenerInvoiceEntry entry : getGenerEntries() ) {
			if ( entry.getType() == GCshOwner.Type.JOB ) {
				castEntries.add(new GnuCashJobInvoiceEntryImpl(entry));
			}
		}

		return castEntries;
	}

	@Override
	public void addEntry(final GnuCashJobInvoiceEntry entry) {
		addGenerEntry(entry);
	}

	// -----------------------------------------------------------------

	@Override
	public FixedPointNumber getAmountUnpaidWithTaxes() {
		return getJobInvcAmountUnpaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithTaxes() {
		return getJobInvcAmountPaidWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountPaidWithoutTaxes() {
		return getJobInvcAmountPaidWithoutTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithTaxes() {
		return getJobInvcAmountWithTaxes();
	}

	@Override
	public FixedPointNumber getAmountWithoutTaxes() {
		return getJobInvcAmountWithoutTaxes();
	}

	@Override
	public String getAmountUnpaidWithTaxesFormatted() {
		return getJobInvcAmountUnpaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithTaxesFormatted() {
		return getJobInvcAmountPaidWithTaxesFormatted();
	}

	@Override
	public String getAmountPaidWithoutTaxesFormatted() {
		// return getJobAmountPaidWithoutTaxesFormatted();
		return null;
	}

	@Override
	public String getAmountWithTaxesFormatted() {
		return getJobInvcAmountWithTaxesFormatted();
	}

	@Override
	public String getAmountWithoutTaxesFormatted() {
		return getJobInvcAmountWithoutTaxesFormatted();
	}

	// ------------------------------

	@Override
	public boolean isFullyPaid() {
		return isJobInvcFullyPaid();
	}

	@Override
	public boolean isNotFullyPaid() {
		return isNotInvcJobFullyPaid();
	}

	// ------------------------------

//  @Override
//  public FixedPointNumber getInvcAmountUnpaidWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountPaidWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountPaidWithoutTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getInvcAmountWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  @Override
//  public FixedPointNumber getInvcAmountWithoutTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountUnpaidWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountPaidWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountPaidWithoutTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getInvcAmountWithoutTaxesFormatted()
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public FixedPointNumber getBillAmountUnpaidWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountPaidWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountPaidWithoutTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public FixedPointNumber getBillAmountWithTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  @Override
//  public FixedPointNumber getBillAmountWithoutTaxes() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountUnpaidWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountPaidWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountPaidWithoutTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountWithTaxesFormatted() 
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public String getBillAmountWithoutTaxesFormatted()
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public boolean isInvcFullyPaid()
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public boolean isNotInvcFullyPaid()
//  {
//    throw new WrongInvoiceTypeException();
//  }
//  
//  // ------------------------------
//
//  @Override
//  public boolean isBillFullyPaid()
//  {
//    throw new WrongInvoiceTypeException();
//  }
//
//  @Override
//  public boolean isNotBillFullyPaid()
//  {
//    throw new WrongInvoiceTypeException();
//  }

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashJobInvoiceImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", job-id=");
		buffer.append(getJobID());

		buffer.append(", invoice-number='");
		buffer.append(getNumber() + "'");

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", #entries:=");
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
