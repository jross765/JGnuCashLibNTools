package org.gnucash.api.write.impl.spec;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashGenerInvoice;
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
import org.gnucash.api.generated.GncV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Additional supported properties for PropertyChangeListeners:
 * <ul>
 * <li>description</li>
 * <li>price</li>
 * <li>quantity</li>
 * <li>action</li>
 * </ul>
 * Entry-Line in an invoice that can be created or removed.
 */
public class GnucashWritableEmployeeVoucherEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                     implements GnucashWritableEmployeeVoucherEntry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableEmployeeVoucherEntryImpl.class);

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncV2.GncBook.GncGncEntry, GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableEmployeeVoucherEntryImpl(
		final GncV2.GncBook.GncGncEntry jwsdpPeer, 
		final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * @param bll   tne employee bill this entry shall belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GnucashGenerInvoice, GncV2.GncBook.GncGncEntry)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableEmployeeVoucherEntryImpl(
		final GnucashWritableEmployeeVoucherImpl bll,
		final GncV2.GncBook.GncGncEntry jwsdpPeer) {
		super(bll, jwsdpPeer);
		
		this.myInvoice = bll;
	}

	/**
	 * Create a taxable invoiceEntry.
	 * (It has the tax table of the employee with a fallback
	 * to the first tax table found assigned)
	 *
	 * @param bll  the employee bill to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getInvcPrice()}}
	 * @throws WrongInvoiceTypeException 
	 * @throws TaxTableNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public GnucashWritableEmployeeVoucherEntryImpl(
		final GnucashWritableEmployeeVoucherImpl bll,
		final GnucashAccount account,
		final FixedPointNumber quantity,
		final FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
		super(bll, 
		      createEmplVchEntry_int(bll, account, quantity, price));
		
		// Caution: Call addVoucherEntry one level above now
		// (GnucashWritableEmployeeVoucherImpl.createEmplVoucherEntry)
		// bll.addVoucherEntry(this);
		this.myInvoice = bll;
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
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	    setVoucherTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	    setVoucherTaxTable(taxTab);
	}

	@Override
	public void setPrice(String price)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	    setVoucherPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	    setVoucherPrice(price);
	}

}
