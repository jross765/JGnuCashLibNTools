package org.gnucash.api.write.impl.spec;

import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.generated.GncV2;
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
 * Additional supported properties for PropertyChangeListeners:
 * <ul>
 * <li>description</li>
 * <li>price</li>
 * <li>quantity</li>
 * <li>action</li>
 * </ul>
 * Entry-Line in an invoice that can be created or removed.
 */
public class GnucashWritableJobInvoiceEntryImpl extends GnucashWritableGenerInvoiceEntryImpl 
                                                implements GnucashWritableJobInvoiceEntry
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableJobInvoiceEntryImpl.class);

	/**
	 * @param file      the file we belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GncV2.GncBook.GncGncEntry, GnucashFileImpl)
	 */
	@SuppressWarnings("exports")
	public GnucashWritableJobInvoiceEntryImpl(
		final GncV2.GncBook.GncGncEntry jwsdpPeer, 
		final GnucashWritableFileImpl file) {
		super(jwsdpPeer, file);
	}

	/**
	 * @param invc   tne job invoice this entry shall belong to
	 * @param jwsdpPeer the JWSDP-object we are facading.
	 * @see GnucashGenerInvoiceEntryImpl#GnucashInvoiceEntryImpl(GnucashGenerInvoice, GncV2.GncBook.GncGncEntry)
	 */
//	@SuppressWarnings("exports")
//	public GnucashWritableJobInvoiceEntryImpl(
//		final GnucashWritableJobInvoiceImpl invc,
//		final GncV2.GncBook.GncGncEntry jwsdpPeer) {
//		super(invc, jwsdpPeer);
//		
//		this.myInvoice = invc;
//	}

	/**
	 * Create a taxable invoiceEntry.
	 * (It has the tax table of the job with a fallback
	 * to the first tax table found assigned)
	 *
	 * @param invc  the job invoice to add this split to
	 * @param account  the income/expenses-account the money comes from
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
	public GnucashWritableJobInvoiceEntryImpl(
		final GnucashWritableJobInvoiceImpl invc,
		final GnucashAccount account,
		final FixedPointNumber quantity,
		final FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalArgumentException {
		super(invc, 
		      createJobInvoiceEntry_int(invc, account, quantity, price));
		
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
		throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setJobTaxable(val);
	}

	@Override
	public void setTaxTable(GCshTaxTable taxTab)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setJobTaxTable(taxTab);
	}

	@Override
	public void setPrice(String price)
		throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setJobPrice(price);
	}

	@Override
	public void setPrice(FixedPointNumber price) throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, IllegalArgumentException {
	    setJobPrice(price);
	}

}
