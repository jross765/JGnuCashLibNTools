package org.gnucash.api.write.impl.spec;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoiceEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customer invoice entry that can be modified.
 * 
 * @see GnucashCustomerInvoiceEntry
 * 
 * @see GnucashWritableEmployeeVoucherEntryImpl
 * @see GnucashWritableVendorBillEntryImpl
 * @see GnucashWritableJobInvoiceEntryImpl
 */
public class GnucashWritableCustomerInvoiceEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                     implements GnucashWritableCustomerInvoiceEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCustomerInvoiceEntryImpl.class);
	
	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncGncEntry,
	 *      GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableCustomerInvoiceEntryImpl(final GncGncEntry jwsdpPeer, final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the customer with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param invc     the invoice to add this split to
	 * @param account  the income-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 */
	public GnucashWritableCustomerInvoiceEntryImpl(final GnucashWritableCustomerInvoiceImpl invc,
			final GnucashAccount account, final FixedPointNumber quantity, final FixedPointNumber price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException {
		super(invc, createCustInvoiceEntry_int(invc, account, quantity, price));

		// Caution: Call addInvcEntry one level above now
		// (GnucashWritableCustomerInvoiceImpl.createCustInvcEntry)
		// invc.addInvcEntry(this);
		this.myInvoice = invc;
	}

	public GnucashWritableCustomerInvoiceEntryImpl(final GnucashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	public GnucashWritableCustomerInvoiceEntryImpl(final GnucashCustomerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	// ---------------------------------------------------------------

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
	public void setTaxable(boolean val)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setCustInvcTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setCustInvcTaxTable(taxTab);
	}

	// ----------------------------

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
	
	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxable(final boolean val) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
	}
	
	// ----------------------------

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

	/**
	 * Do not use
	 */
	@Override
    public void setJobInvcTaxTable(final GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

    // 	---------------------------------------------------------------
	
	@Override
	public void setPrice(String price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setCustInvcPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setCustInvcPrice(price);
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

	/**
	 * Do not use
	 */
    @Override
    public void setJobInvcPrice(final String n)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		throw new WrongInvoiceTypeException();
    }

}
