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
import org.gnucash.api.read.impl.spec.GnuCashEmployeeVoucherImpl;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Employee voucher entry that can be modified.
 * 
 * @see GnuCashEmployeeVoucherEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntryImpl
 * @see GnuCashWritableVendorBillEntryImpl
 * @see GnuCashWritableJobInvoiceEntryImpl
 */
public class GnuCashWritableEmployeeVoucherEntryImpl extends GnuCashWritableGenerInvoiceEntryImpl 
                                                     implements GnuCashWritableEmployeeVoucherEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableEmployeeVoucherEntryImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnuCashGenerInvoiceEntryImpl#GnuCashInvoiceEntryImpl(GncGncEntry,
	 *      GnuCashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableEmployeeVoucherEntryImpl(final GncGncEntry jwsdpPeer, final GnuCashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the employee with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param vch      the employee voucher to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnuCashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnuCashGenerInvoiceEntry#getEmplVchPrice()}}
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableEmployeeVoucherEntryImpl(
			final GnuCashWritableEmployeeVoucherImpl vch,
			final GnuCashAccount account, 
			final FixedPointNumber quantity, 
			final FixedPointNumber price)
			throws TaxTableNotFoundException {
		super(vch, createEmplVchEntry_int(vch, account, quantity, price));

		// Caution: Call addVoucherEntry one level above now
		// (GnuCashWritableEmployeeVoucherImpl.createEmplVoucherEntry)
		// vch.addVoucherEntry(this);
		this.myInvoice = vch;
	}

	public GnuCashWritableEmployeeVoucherEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	public GnuCashWritableEmployeeVoucherEntryImpl(final GnuCashEmployeeVoucherEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	// ---------------------------------------------------------------

	@Override
	public void setTaxable(boolean val)
			throws TaxTableNotFoundException {
		setEmplVchTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
			throws TaxTableNotFoundException {
		setEmplVchTaxTable(taxTab);
	}

	// ----------------------------

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
    public void setVendBllTaxable(final boolean val) throws TaxTableNotFoundException {
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
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setVendBllTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
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
		setEmplVchPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price)
			throws TaxTableNotFoundException {
		setEmplVchPrice(price);
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
    public void setVendBllPrice(final String n)
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
	public GCshID getVoucherID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashEmployeeVoucher getVoucher() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.EMPLOYEE )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException("No employee voucher with id '" + getVoucherID()
						+ "' for voucher entry with id '" + getID() + "'");
			}
		}

		return new GnuCashEmployeeVoucherImpl(myInvoice);
	}

	@Override
	public GCshID getAccountID() throws AccountNotFoundException {
		return super.getEmplVchAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	@Override
	public FixedPointNumber getPrice() {
		return super.getEmplVchPrice();
	}

	@Override
	public String getPriceFormatted() {
		return super.getEmplVchPriceFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public boolean isTaxable() {
		return super.isEmplVchTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return super.getEmplVchTaxTable();
	}

	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return super.getEmplVchApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return super.getEmplVchApplicableTaxPercentFormatted();
	}

	@Override
	public FixedPointNumber getSum() {
		return super.getEmplVchSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return super.getEmplVchSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return super.getEmplVchSumExclTaxes();
	}

	@Override
	public String getSumFormatted() {
		return super.getEmplVchSumExclTaxesFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return super.getEmplVchSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return super.getEmplVchSumExclTaxesFormatted();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableEmployeeVoucherEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", voucher-id=");
		buffer.append(getVoucherID());

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
