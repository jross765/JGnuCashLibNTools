package org.gnucash.api.write.impl.spec;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.GnuCashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job invoice entry that can be modified.
 * 
 * @see GnuCashJobInvoiceEntry
 * 
 * @see GnuCashWritableCustomerInvoiceEntryImpl
 * @see GnuCashWritableEmployeeVoucherEntryImpl
 * @see GnuCashWritableVendorBillEntryImpl
 */
public class GnuCashWritableJobInvoiceEntryImpl extends GnuCashWritableGenerInvoiceEntryImpl 
                                                implements GnuCashWritableJobInvoiceEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableJobInvoiceEntryImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnuCashGenerInvoiceEntryImpl#GnuCashInvoiceEntryImpl(GncGncEntry,
	 *      GnuCashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnuCashWritableJobInvoiceEntryImpl(final GncGncEntry jwsdpPeer, final GnuCashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the job with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param invc     the job invoice to add this split to
	 * @param account  the income/expenses-account the money comes from
	 * @param quantity see ${@link GnuCashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnuCashGenerInvoiceEntry#getJobInvcPrice()}}
	 * @throws TaxTableNotFoundException
	 */
	public GnuCashWritableJobInvoiceEntryImpl(final GnuCashWritableJobInvoiceImpl invc, final GnuCashAccount account,
			final FixedPointNumber quantity, final FixedPointNumber price)
			throws TaxTableNotFoundException {
		super(invc, createJobInvoiceEntry_int(invc, account, quantity, price));

		// Caution: Call addJobEntry one level above now
		// (GnuCashWritableJobInvoiceImpl.createJobInvcEntry)
		// invc.addJobEntry(this);
		this.myInvoice = invc;
	}

	public GnuCashWritableJobInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

	public GnuCashWritableJobInvoiceEntryImpl(final GnuCashJobInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnuCashWritableFileImpl) entry.getGenerInvoice().getGnuCashFile());
	}

    // ---------------------------------------------------------------

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * The GnuCash file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

	// -----------------------------------------------------------

	@Override
	public void setTaxable(boolean val) throws TaxTableNotFoundException,
			UnknownInvoiceTypeException {
		setJobInvcTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab) throws TaxTableNotFoundException,
			UnknownInvoiceTypeException {
		setJobInvcTaxTable(taxTab);
	}

	// ----------------------------

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//	@Override
//    public void setJobInvcTaxable(final boolean val) throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxable(final boolean val) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
	}
	
	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//	@Override
//    public void setVendBllTaxable(final boolean val) throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//	}
	
	// ----------------------------

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//	@Override
//    public void setJobInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//    }

	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//	@Override
//    public void setVendBllTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//    }

    // 	---------------------------------------------------------------
	
	@Override
	public void setPrice(String price) throws TaxTableNotFoundException,
			UnknownInvoiceTypeException {
		setJobInvcPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws TaxTableNotFoundException,
			UnknownInvoiceTypeException {
		setJobInvcPrice(price);
	}

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//    @Override
//    public void setJobInvcPrice(final String n)
//	    throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//    }

	/**
	 * Do not use
	 */
    @Override
    public void setEmplVchPrice(final String n)
	    throws TaxTableNotFoundException {
		throw new WrongInvoiceTypeException();
    }

	// CAUTION: THIS ONE MUST NOT BE UNCOMMENTED!
//	/**
//	 * Do not use
//	 */
//    @Override
//    public void setVendBllPrice(final String n)
//	    throws TaxTableNotFoundException {
//		throw new WrongInvoiceTypeException();
//    }

	// ---------------------------------------------------------------

	@Override
	public GCshID getInvoiceID() {
		return getGenerInvoiceID();
	}

	@Override
	public GnuCashJobInvoice getInvoice() {
		if ( myInvoice == null ) {
			myInvoice = getGenerInvoice();
			if ( myInvoice.getType() != GCshOwner.Type.JOB )
				throw new WrongInvoiceTypeException();

			if ( myInvoice == null ) {
				throw new IllegalStateException(
						"No job invoice with id '" + getInvoiceID() + "' for invoice entry with id '" + getID() + "'");
			}
		}

		return new GnuCashJobInvoiceImpl(myInvoice);
	}

	@Override
	public GCshID getAccountID() throws AccountNotFoundException {
		return super.getJobInvcAccountID();
	}

	@Override
	public GnuCashAccount getAccount() throws AccountNotFoundException {
		return getGnuCashFile().getAccountByID(getAccountID());
	}

	@Override
	public FixedPointNumber getPrice() {
		return super.getJobInvcPrice();
	}

	@Override
	public String getPriceFormatted() {
		return super.getJobInvcPriceFormatted();
	}

	// ---------------------------------------------------------------
	
	@Override
	public boolean isTaxable() {
		return super.isJobInvcTaxable();
	}

	@Override
	public GCshTaxTable getTaxTable() throws TaxTableNotFoundException {
		return super.getJobInvcTaxTable();
	}

	@Override
	public FixedPointNumber getApplicableTaxPercent() {
		return super.getJobInvcApplicableTaxPercent();
	}

	@Override
	public String getApplicableTaxPercentFormatted() {
		return super.getJobInvcApplicableTaxPercentFormatted();
	}

	@Override
	public FixedPointNumber getSum() {
		return super.getJobInvcSum();
	}

	@Override
	public FixedPointNumber getSumInclTaxes() {
		return super.getJobInvcSumInclTaxes();
	}

	@Override
	public FixedPointNumber getSumExclTaxes() {
		return super.getJobInvcSumExclTaxes();
	}

	@Override
	public String getSumFormatted() {
		return super.getJobInvcSumExclTaxesFormatted();
	}

	@Override
	public String getSumInclTaxesFormatted() {
		return super.getJobInvcSumInclTaxesFormatted();
	}

	@Override
	public String getSumExclTaxesFormatted() {
		return super.getJobInvcSumExclTaxesFormatted();
	}

	// ---------------------------------------------------------------

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableJobInvoiceEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", invoice-id=");
		buffer.append(getInvoiceID());

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
		} catch (Exception e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
	}

}
