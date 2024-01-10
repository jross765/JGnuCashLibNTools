package org.gnucash.api.write.impl.spec;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashJobInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnucashWritableJobInvoiceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Job invoice entry that can be modified.
 * 
 * @see GnucashJobInvoiceEntry
 * 
 * @see GnucashWritableCustomerInvoiceEntryImpl
 * @see GnucashWritableEmployeeVoucherEntryImpl
 * @see GnucashWritableVendorBillEntryImpl
 */
public class GnucashWritableJobInvoiceEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                implements GnucashWritableJobInvoiceEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableJobInvoiceEntryImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncGncEntry,
	 *      GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableJobInvoiceEntryImpl(final GncGncEntry jwsdpPeer, final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the job with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param invc     the job invoice to add this split to
	 * @param account  the income/expenses-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 */
	public GnucashWritableJobInvoiceEntryImpl(final GnucashWritableJobInvoiceImpl invc, final GnucashAccount account,
			final FixedPointNumber quantity, final FixedPointNumber price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException {
		super(invc, createJobInvoiceEntry_int(invc, account, quantity, price));

		// Caution: Call addJobEntry one level above now
		// (GnucashWritableJobInvoiceImpl.createJobInvcEntry)
		// invc.addJobEntry(this);
		this.myInvoice = invc;
	}

	public GnucashWritableJobInvoiceEntryImpl(final GnucashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	public GnucashWritableJobInvoiceEntryImpl(final GnucashJobInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	// ---------------------------------------------------------------

	/**
	 * @see #getGnucashFile()
	 */
	@Override
	public GnucashWritableFile getWritableGnucashFile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUserDefinedAttribute(String name, String value) {
		// TODO Auto-generated method stub

	}

	// -----------------------------------------------------------

	@Override
	public void setTaxable(boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException,
			UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {
		setJobInvcTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException,
			UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {
		setJobInvcTaxTable(taxTab);
	}

	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setCustInvcTaxable(final boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxable(final boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
	}
	
	/**
	 * Do not use
	 */
	@Override
    public void setVendBllTaxable(final boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
	}
	
	// ----------------------------

	/**
	 * Do not use
	 */
	@Override
    public void setCustInvcTaxTable(final GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
	@Override
    public void setVendBllTaxTable(final GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

    // 	---------------------------------------------------------------
	
	@Override
	public void setPrice(String price) throws WrongInvoiceTypeException, TaxTableNotFoundException,
			UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {
		setJobInvcPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException,
			UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {
		setJobInvcPrice(price);
	}

	/**
	 * Do not use
	 */
    @Override
    public void setCustInvcPrice(final String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setEmplVchPrice(final String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

	/**
	 * Do not use
	 */
    @Override
    public void setVendBllPrice(final String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

}
