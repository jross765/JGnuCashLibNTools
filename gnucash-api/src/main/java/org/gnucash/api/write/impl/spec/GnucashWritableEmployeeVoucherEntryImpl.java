package org.gnucash.api.write.impl.spec;

import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Employee voucher entry that can be modified.
 * 
 * @see GnucashEmployeeVoucherEntry
 * 
 * @see GnucashWritableCustomerInvoiceEntryImpl
 * @see GnucashWritableVendorBillEntryImpl
 * @see GnucashWritableJobInvoiceEntryImpl
 */
public class GnucashWritableEmployeeVoucherEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                     implements GnucashWritableEmployeeVoucherEntry
{
	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeVoucherEntryImpl.class);

	// ---------------------------------------------------------------

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncGncEntry,
	 *      GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableEmployeeVoucherEntryImpl(final GncGncEntry jwsdpPeer, final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * Create a taxable invoiceEntry. (It has the tax table of the employee with a
	 * fallback to the first tax table found assigned)
	 *
	 * @param vch      the employee voucher to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws WrongInvoiceTypeException
	 * @throws TaxTableNotFoundException
	 */
	public GnucashWritableEmployeeVoucherEntryImpl(final GnucashWritableEmployeeVoucherImpl vch,
			final GnucashAccount account, final FixedPointNumber quantity, final FixedPointNumber price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException {
		super(vch, createEmplVchEntry_int(vch, account, quantity, price));

		// Caution: Call addVoucherEntry one level above now
		// (GnucashWritableEmployeeVoucherImpl.createEmplVoucherEntry)
		// vch.addVoucherEntry(this);
		this.myInvoice = vch;
	}

	public GnucashWritableEmployeeVoucherEntryImpl(final GnucashGenerInvoiceEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getGnucashFile());
	}

	public GnucashWritableEmployeeVoucherEntryImpl(final GnucashEmployeeVoucherEntry entry) {
		super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getGnucashFile());
	}

	// ---------------------------------------------------------------

	@Override
	public void setTaxable(boolean val)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setEmplVchTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setEmplVchTaxTable(taxTab);
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
    public void setCustInvcTaxTable(final GCshTaxTable taxTab) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
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
		setEmplVchPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price)
			throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
		setEmplVchPrice(price);
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
