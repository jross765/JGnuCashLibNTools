package org.gnucash.api.write.impl.spec;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Vendor bill entry that can be modified.
 * 
 * @see GnuCashVendorBillEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntryImpl
 * @see GnuCashWritableEmployeeVoucherEntryImpl
 * @see GnuCashWritableJobInvoiceEntryImpl
 */
public class GnuCashWritableVendorBillEntryImpl extends GnuCashWritableGenerInvoiceEntryImpl 
                                                implements GnuCashWritableVendorBillEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableVendorBillEntryImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnuCashGenerInvoiceEntryImpl#GnuCashInvoiceEntryImpl(GncGncEntry,
	 *      GnuCashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableVendorBillEntryImpl(final GncGncEntry jwsdpPeer, final GnuCashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the vendor with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param bll      the vendor bill to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnuCashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnuCashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableVendorBillEntryImpl(final GnuCashWritableVendorBillImpl bll, final GnuCashAccount account,
			final FixedPointNumber quantity, final FixedPointNumber price)
			throws TaxTableNotFoundException {
		super(bll, createVendBillEntry_int(bll, account, quantity, price));

		// Caution: Call addBillEntry one level above now
		// (GnuCashWritableVendorBillImpl.createVendBillEntry)
		// bll.addBillEntry(this);
		this.myInvoice = bll;
	}

	public GnuCashWritableVendorBillEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	public GnuCashWritableVendorBillEntryImpl(final GnuCashVendorBillEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	// -----------------------------------------------------------

	@Override
	public void setTaxable(boolean val)
			throws TaxTableNotFoundException {
		setVendBllTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		setVendBllTaxTable(taxTab);
	}

	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setCustInvcTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setCustInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

    // 	---------------------------------------------------------------
	
	@Override
	public void setPrice(String price)
			throws TaxTableNotFoundException {
		setVendBllPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price)
			throws TaxTableNotFoundException {
		setVendBllPrice(price);
	}

	/**
	 * Do not use
	 */
    @Override
    public void setCustInvcPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setEmplVchPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setJobInvcPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	// ---------------------------------------------------------------

	@Override
	public GCshID getBillID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashVendorBill getBill() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.VENDOR )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException(
						"No vendor bill with id '" + getBillID() + "' for bill entry with id '" + getID() + "'");
			}
		}

		return new GnuCashVendorBillImpl(myInvoice);
	}

	@Override
	public GCshID getAccountID() throws AccountNotFoundException {
		return super.getVendBllAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	@Override
	public FixedPointNumber getPrice() {
		return super.getVendBllPrice();
	}

	@Override
	public String getPriceFormatted() {
		return super.getVendBllPriceFormatted();
	}
	
	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableVendorBillEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", bill-id=");
		buffer.append(getBillID());

		buffer.append(", description='");
		buffer.append(getDescription() + "'");

		buffer.append(", date=");
		try {
			buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
		} catch (Exception e) {
			buffer.append(getDate().toLocalDate().toString());
		}

		buffer.append(", action='");
		try {
			buffer.append(getAction() + "'");
		} catch (Exception e) {
			buffer.append("ERROR" + "'");
		}

		buffer.append(", account-id=");
		try {
			buffer.append(getAccountID());
		} catch (Exception e) {
		    buffer.append("ERROR");
		}
		
		buffer.append(", price=");
		try {
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
