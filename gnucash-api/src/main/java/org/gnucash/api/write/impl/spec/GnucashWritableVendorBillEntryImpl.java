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
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.gnucash.api.write.impl.GnucashWritableGenerInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnucashWritableVendorBillEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashVendorBillEntryImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableVendorBillEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                implements GnucashWritableVendorBillEntry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableVendorBillEntryImpl.class);

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncGncEntry, GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableVendorBillEntryImpl(
		final GncGncEntry jwsdpPeer, 
		final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * @param bll   tne vendor bill this entry shall belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GnucashGenerInvoice, GncGncEntry)
	 */
//	@SuppressWarnings("exports")
//	public GnucashWritableVendorBillEntryImpl(
//		final GnucashWritableVendorBillImpl bll,
//		final GncGncEntry jwsdpPeer) {
//		super(bll, jwsdpPeer);
//		
//		this.myInvoice = bll;
//	}

	/**
	 * Create a taxable invoiceEntry.
	 * (It has the tax table of the vendor with a fallback
	 * to the first tax table found assigned)
	 *
	 * @param bll  the vendor bill to add this split to
	 * @param account  the expenses-account the money comes from
	 * @param quantity see ${@link GnucashGenerInvoiceEntry#getQuantity()}
	 * @param price    see ${@link GnucashGenerInvoiceEntry#getCustInvcPrice()}}
	 * @throws WrongInvoiceTypeException 
	 * @throws TaxTableNotFoundException 
	 * @throws 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchFieldException 
	 */
	public GnucashWritableVendorBillEntryImpl(
		final GnucashWritableVendorBillImpl bll,
		final GnucashAccount account,
		final FixedPointNumber quantity,
		final FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalArgumentException {
		super(bll, 
		      createVendBillEntry_int(bll, account, quantity, price));
		
		// Caution: Call addBillEntry one level above now
		// (GnucashWritableVendorBillImpl.createVendBillEntry)
		// bll.addBillEntry(this);
		this.myInvoice = bll;
	}

	public GnucashWritableVendorBillEntryImpl(final GnucashGenerInvoiceEntry entry) {
	    super(entry.getJwsdpPeer(), (GnucashWritableFileImpl) entry.getGenerInvoice().getFile());
	}

	public GnucashWritableVendorBillEntryImpl(final GnucashVendorBillEntry entry) {
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
	    setVendBllTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVendBllTaxTable(taxTab);
	}

	@Override
	public void setPrice(String price)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVendBllPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setVendBllPrice(price);
	}

}
