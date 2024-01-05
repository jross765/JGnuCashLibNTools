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
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashEmployeeVoucherEntryImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableEmployeeVoucherEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                     implements GnucashWritableEmployeeVoucherEntry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeVoucherEntryImpl.class);

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncGncEntry, GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableEmployeeVoucherEntryImpl(
		final GncGncEntry jwsdpPeer, 
		final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * @param vch   tne employee voucher this entry shall belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GnucashGenerInvoice, GncGncEntry)
	 */
//	@SuppressWarnings("exports")
//	public GnucashWritableEmployeeVoucherEntryImpl(
//		final GnucashWritableEmployeeVoucherImpl vch,
//		final GncGncEntry jwsdpPeer) {
//		super(vch, jwsdpPeer);
//		
//		this.myInvoice = vch;
//	}

	/**
	 * Create a taxable invoiceEntry.
	 * (It has the tax table of the employee with a fallback
	 * to the first tax table found assigned)
	 *
	 * @param vch  the employee voucher to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getInvcPrice()}}
	 * @throws WrongInvoiceTypeException 
	 * @throws TaxTableNotFoundException 
	 * @throws 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public GnucashWritableEmployeeVoucherEntryImpl(
		final GnucashWritableEmployeeVoucherImpl vch,
		final GnucashAccount account,
		final FixedPointNumber quantity,
		final FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalArgumentException {
		super(vch, 
		      createEmplVchEntry_int(vch, account, quantity, price));
		
		// Caution: Call addVoucherEntry one level above now
		// (GnucashWritableEmployeeVoucherImpl.createEmplVoucherEntry)
		// vch.addVoucherEntry(this);
		this.myInvoice = vch;
	}

	public GnucashWritableEmployeeVoucherEntryImpl(final GnucashGenerInvoiceEntry entry) {
	    super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	public GnucashWritableEmployeeVoucherEntryImpl(final GnucashEmployeeVoucherEntry entry) {
	    super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	// -----------------------------------------------------------

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
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVoucherTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVoucherTaxTable(taxTab);
	}

	@Override
	public void setPrice(String price)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVoucherPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVoucherPrice(price);
	}

}
