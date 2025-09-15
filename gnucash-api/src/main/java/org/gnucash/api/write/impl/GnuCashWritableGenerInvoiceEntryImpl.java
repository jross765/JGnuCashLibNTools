package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashCustomerInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorBillImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceEntryImpl;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashGenerInvoiceEntryImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableGenerInvoiceEntryImpl extends GnuCashGenerInvoiceEntryImpl 
                                                  implements GnuCashWritableGenerInvoiceEntry 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableGenerInvoiceEntryImpl.class);

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    /**
     * @see {@link #getGenerInvoice()}
     */
    private GnuCashWritableGenerInvoice invoice;

    // -----------------------------------------------------------

    /**
     * @see {@link #GnuCashWritableInvoiceEntryImpl(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}
     */
    protected static GncGncEntry createCustInvoiceEntry_int(
	    final GnuCashWritableGenerInvoiceImpl invc, // important: NOT GnuCashWritableCustomerInvoiceImpl
	    final GnuCashAccount acct, 
	    final FixedPointNumber quantity, 
	    final FixedPointNumber price) {
		if ( invc.getType() != GCshOwner.Type.CUSTOMER && 
			 invc.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		// Note: count-data is not updated here, but rather
		// when the file is finally written.
		// Cf. GnuCashWritableFileImpl.checkAllCountData().

		if ( !invc.isModifiable() ) {
			throw new IllegalArgumentException("The given customer invoice has payments and is thus not modifiable");
		}

		GnuCashWritableFileImpl gcshWFile = (GnuCashWritableFileImpl) invc.getGnuCashFile();
		ObjectFactory factory = gcshWFile.getObjectFactory();

		GncGncEntry entry = createGenerInvoiceEntryCommon(invc, gcshWFile, factory);

		{
			GncGncEntry.EntryIAcct iacct = factory.createGncGncEntryEntryIAcct();
			iacct.setType(Const.XML_DATA_TYPE_GUID);
			iacct.setValue(acct.getID().toString());
			entry.setEntryIAcct(iacct);
		}

		entry.setEntryIDiscHow("PRETAX");
		entry.setEntryIDiscType("PERCENT");

		{
			GncGncEntry.EntryInvoice inv = factory.createGncGncEntryEntryInvoice();
			inv.setType(Const.XML_DATA_TYPE_GUID);
			inv.setValue(invc.getID().toString());
			entry.setEntryInvoice(inv);
		}

		entry.setEntryIPrice(price.toGnuCashString());
		entry.setEntryITaxable(1);
		entry.setEntryITaxincluded(0);

		{
			// TODO: use not the first but the default taxtable
			GncGncEntry.EntryITaxtable taxTabRef = factory.createGncGncEntryEntryITaxtable();
			taxTabRef.setType(Const.XML_DATA_TYPE_GUID);

			GCshTaxTable taxTab = null;

			// If customer has a tax table, then assign it to
			// then customer invoice entry.
			GnuCashCustomer cust = null;
			if ( invc.getType() == GCshOwner.Type.CUSTOMER ) {
				GnuCashCustomerInvoice custInvc = new GnuCashCustomerInvoiceImpl((GnuCashGenerInvoice) invc);
				cust = custInvc.getCustomer();
			} else if ( invc.getType() == GCshOwner.Type.JOB ) {
				GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl((GnuCashGenerInvoice) invc);
				GnuCashCustomerJob custJob = jobInvc.getCustJob();
				cust = custJob.getCustomer();
			}
			if ( cust != null ) {
				taxTab = cust.getTaxTable();
			}

			// Else: Use first tax table found
			if ( taxTab == null ) {
				taxTab = invc.getGnuCashFile().getTaxTables().iterator().next();
			}

			/*
			 * GncV2Type.GncBookType.GncGncTaxTableType taxtable =
			 * (GncV2Type.GncBookType.GncGncTaxTableType) ((GnuCashFileImpl)
			 * invoice.getGnuCashFile()).getRootElement().getGncBook().getGncGncTaxTable().
			 * get(0);
			 * 
			 * taxtableref.setValue(taxtable.getTaxtableGuid().getValue());
			 */
			taxTabRef.setValue(taxTab.getID().toString());
			entry.setEntryITaxtable(taxTabRef);
		}

		entry.setEntryQty(quantity.toGnuCashString());
		entry.setVersion(Const.XML_FORMAT_VERSION);

		invc.getGnuCashFile().getRootElement().getGncBook().getBookElements().add(entry);
		invc.getGnuCashFile().setModified(true);

		LOGGER.debug("createCustInvoiceEntry_int: Created new customer invoice entry (core): "
				+ entry.getEntryGuid().getValue());

		return entry;
    }

    /**
     * @see {@link #GnuCashWritableInvoiceEntryImpl(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}
     */
    protected static GncGncEntry createVendBillEntry_int(
	    final GnuCashWritableGenerInvoiceImpl invc, // important: NOT GnuCashWritableVendorBillImpl
	    final GnuCashAccount acct, 
	    final FixedPointNumber quantity, 
	    final FixedPointNumber price) {
		if ( invc.getType() != GCshOwner.Type.VENDOR && 
			 invc.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		// Note: count-data is not updated here, but rather
		// when the file is finally written.
		// Cf. GnuCashWritableFileImpl.checkAllCountData().

		if ( !invc.isModifiable() ) {
			throw new IllegalArgumentException("The given vendor bill has payments and is thus not modifiable");
		}

		GnuCashWritableFileImpl gcshWFile = (GnuCashWritableFileImpl) invc.getGnuCashFile();
		ObjectFactory factory = gcshWFile.getObjectFactory();

		GncGncEntry entry = createGenerInvoiceEntryCommon(invc, gcshWFile, factory);

		{
			GncGncEntry.EntryBAcct iacct = factory.createGncGncEntryEntryBAcct();
			iacct.setType(Const.XML_DATA_TYPE_GUID);
			iacct.setValue(acct.getID().toString());
			entry.setEntryBAcct(iacct);
		}

		{
			GncGncEntry.EntryBill bll = factory.createGncGncEntryEntryBill();
			bll.setType(Const.XML_DATA_TYPE_GUID);
			bll.setValue(invc.getID().toString());
			entry.setEntryBill(bll);
		}

		entry.setEntryBPrice(price.toGnuCashString());
		entry.setEntryBTaxable(1);
		entry.setEntryBTaxincluded(0);

		{
			// TODO: use not the first but the default taxtable
			GncGncEntry.EntryBTaxtable taxTabRef = factory.createGncGncEntryEntryBTaxtable();
			taxTabRef.setType(Const.XML_DATA_TYPE_GUID);

			GCshTaxTable taxTab = null;

			// If vendor has a tax table, then assign it to
			// then vendor bill.entry.
			GnuCashVendor vend = null;
			if ( invc.getType() == GCshOwner.Type.VENDOR ) {
				GnuCashVendorBill vendInvc = new GnuCashVendorBillImpl((GnuCashGenerInvoice) invc);
				vend = vendInvc.getVendor();
			} else if ( invc.getType() == GCshOwner.Type.JOB ) {
				GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl((GnuCashGenerInvoice) invc);
				GnuCashVendorJob vendJob = jobInvc.getVendJob();
				vend = vendJob.getVendor();
			}
			if ( vend != null ) {
				taxTab = vend.getTaxTable();
			}

			// Else: Use first tax table found
			if ( taxTab == null ) {
				taxTab = invc.getGnuCashFile().getTaxTables().iterator().next();
			}

			/*
			 * GncV2Type.GncBookType.GncGncTaxTableType taxtable =
			 * (GncV2Type.GncBookType.GncGncTaxTableType) ((GnuCashFileImpl)
			 * invoice.getGnuCashFile()).getRootElement().getGncBook().getGncGncTaxTable().
			 * get(0);
			 * 
			 * taxtableref.setValue(taxtable.getTaxtableGuid().getValue());
			 */
			taxTabRef.setValue(taxTab.getID().toString());
			entry.setEntryBTaxtable(taxTabRef);
		}

		entry.setEntryQty(quantity.toGnuCashString());
		entry.setVersion(Const.XML_FORMAT_VERSION);

		invc.getGnuCashFile().getRootElement().getGncBook().getBookElements().add(entry);
		invc.getGnuCashFile().setModified(true);

		LOGGER.debug(
				"createVendBillEntry_int: Created new customer bill entry (core): " + entry.getEntryGuid().getValue());

		return entry;
    }

    /**
     * @see {@link #GnuCashWritableInvoiceEntryImpl(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}
     */
    protected static GncGncEntry createEmplVchEntry_int(
	    final GnuCashWritableGenerInvoiceImpl invc, // important: NOT GnuCashWritableEmployeeVoucherImpl
	    final GnuCashAccount acct, 
	    final FixedPointNumber quantity, 
	    final FixedPointNumber price) {
		if ( invc.getType() != GCshOwner.Type.EMPLOYEE )
			throw new WrongInvoiceTypeException();

		// Note: count-data is not updated here, but rather
		// when the file is finally written.
		// Cf. GnuCashWritableFileImpl.checkAllCountData().

		if ( !invc.isModifiable() ) {
			throw new IllegalArgumentException("The given employee voucher has payments and is thus not modifiable");
		}

		GnuCashWritableFileImpl gcshWFile = (GnuCashWritableFileImpl) invc.getGnuCashFile();
		ObjectFactory factory = gcshWFile.getObjectFactory();

		GncGncEntry entry = createGenerInvoiceEntryCommon(invc, gcshWFile, factory);

		{
			GncGncEntry.EntryBAcct iacct = factory.createGncGncEntryEntryBAcct();
			iacct.setType(Const.XML_DATA_TYPE_GUID);
			iacct.setValue(acct.getID().toString());
			entry.setEntryBAcct(iacct);
		}

		{
			GncGncEntry.EntryBill bll = factory.createGncGncEntryEntryBill();
			bll.setType(Const.XML_DATA_TYPE_GUID);
			bll.setValue(invc.getID().toString());
			entry.setEntryBill(bll);
		}

		entry.setEntryBPrice(price.toGnuCashString());
		entry.setEntryBTaxable(1);
		entry.setEntryBTaxincluded(0);

		{
			// TODO: use not the first but the default taxtable
			GncGncEntry.EntryBTaxtable taxTabRef = factory.createGncGncEntryEntryBTaxtable();
			taxTabRef.setType(Const.XML_DATA_TYPE_GUID);

			GCshTaxTable taxTab = null;

			// Caution: As opposed to the customers and vendors,
			// employees do not have a tax table.
			// Therefore, we cannot apply the generic rule "if customer/vendor
			// has a tax table..." from the methods createCustInvcEntry_int() and
			// createVendBillEntry_int(), resp.

			// Use first tax table found
			if ( taxTab == null ) {
				taxTab = invc.getGnuCashFile().getTaxTables().iterator().next();
			}

			/*
			 * GncV2Type.GncBookType.GncGncTaxTableType taxtable =
			 * (GncV2Type.GncBookType.GncGncTaxTableType) ((GnuCashFileImpl)
			 * invoice.getGnuCashFile()).getRootElement().getGncBook().getGncGncTaxTable().
			 * get(0);
			 * 
			 * taxtableref.setValue(taxtable.getTaxtableGuid().getValue());
			 */
			taxTabRef.setValue(taxTab.getID().toString());
			entry.setEntryBTaxtable(taxTabRef);
		}

		entry.setEntryQty(quantity.toGnuCashString());
		entry.setVersion(Const.XML_FORMAT_VERSION);

		invc.getGnuCashFile().getRootElement().getGncBook().getBookElements().add(entry);
		invc.getGnuCashFile().setModified(true);

		LOGGER.debug("createEmplVchEntry_int: Created new employee voucher entry (core): "
				+ entry.getEntryGuid().getValue());

		return entry;
    }

    /**
     * @see {@link #GnuCashWritableInvoiceEntryImpl(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}
     */
    protected static GncGncEntry createJobInvoiceEntry_int(
	    final GnuCashWritableGenerInvoiceImpl invc, // important: NOT GnuCashWritableJobInvoiceImpl
	    final GnuCashAccount acct, 
	    final FixedPointNumber quantity, 
	    final FixedPointNumber price) {
		if ( invc.getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
		
		// Note: count-data is not updated here, but rather
		// when the file is finally written.
		// Cf. GnuCashWritableFileImpl.checkAllCountData().

		if ( !invc.isModifiable() ) {
			throw new IllegalArgumentException("The given job invoice has payments and is thus not modifiable");
		}

		if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
			return createCustInvoiceEntry_int(invc, acct, quantity, price);
		else if ( invc.getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
			return createVendBillEntry_int(invc, acct, quantity, price);

		return null; // Compiler happy
	}

	private static GncGncEntry createGenerInvoiceEntryCommon(final GnuCashWritableGenerInvoiceImpl invc,
			final GnuCashWritableFileImpl gcshWrtblFile, final ObjectFactory factory) {
		// Note: count-data is not updated here, but rather
		// when the file is finally written.
		// Cf. GnuCashWritableFileImpl.checkAllCountData().

		if ( !invc.isModifiable() ) {
			throw new IllegalArgumentException("The given invoice has payments and is" + " thus not modifiable");
		}

		GncGncEntry entry = gcshWrtblFile.createGncGncEntryType();

		{
			GncGncEntry.EntryGuid guid = factory.createGncGncEntryEntryGuid();
			guid.setType(Const.XML_DATA_TYPE_GUID);
			guid.setValue(GCshID.getNew().toString());
			entry.setEntryGuid(guid);
		}

		entry.setEntryAction(Action.HOURS.getLocaleString());

		{
			GncGncEntry.EntryDate entryDate = factory.createGncGncEntryEntryDate();
			ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
			String dateTimeStr = dateTime.format(DATE_FORMAT_BOOK);
			entryDate.setTsDate(dateTimeStr);
			entry.setEntryDate(entryDate);
		}

		entry.setEntryDescription("no description");

		{
			GncGncEntry.EntryEntered entered = factory.createGncGncEntryEntryEntered();
			ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
			String dateTimeStr = dateTime.format(DATE_FORMAT_BOOK);
			entered.setTsDate(dateTimeStr);
			entry.setEntryEntered(entered);
		}

		return entry;
    }
    
    // ---------------------------------------------------------------

    /**
     * @param gnucashFile the file we belong to
     * @param jwsdpPeer   the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableGenerInvoiceEntryImpl(
	    final GncGncEntry jwsdpPeer,
	    final GnuCashWritableFileImpl gnucashFile) {
    	super(jwsdpPeer, gnucashFile, true);
    }

    /**
     * @param invc   the invoice this entry shall belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnuCashWritableGenerInvoiceEntryImpl(
	    final GnuCashWritableGenerInvoiceImpl invc,
	    final GncGncEntry jwsdpPeer) {
    	super(invc, jwsdpPeer, true);

    	this.invoice = invc;
    }

    /**
     * @param invc   the invoice this entry shall belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param addEntrToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashWritableGenerInvoiceEntryImpl(
	    final GnuCashWritableGenerInvoiceImpl invc,
	    final GncGncEntry jwsdpPeer,
	    final boolean addEntrToInvc) {
    	super(invc, jwsdpPeer, addEntrToInvc);

    	this.invoice = invc;
    }

    /**
     * Create a taxable invoiceEntry. (It has the taxtable of the customer with a
     * fallback to the first taxtable found assigned)
     *
     * @param invoice  the invoice to add this split to
     * @param account  the income-account the money comes from
     * @param quantity see ${@link GnuCashGenerInvoiceEntry#getQuantity()}
     * @param price    see ${@link GnuCashGenerInvoiceEntry#getCustInvcPrice()}}
     * @throws TaxTableNotFoundException
     *  
     */
    public GnuCashWritableGenerInvoiceEntryImpl(
	    final GnuCashWritableGenerInvoiceImpl invoice,
	    final GnuCashAccount account, 
	    final FixedPointNumber quantity, 
	    final FixedPointNumber price)
	    throws TaxTableNotFoundException {
    	super(invoice, 
    		  createCustInvoiceEntry_int(invoice, account, quantity, price), 
    		  true);

    	invoice.addRawGenerEntry(this);
    	this.invoice = invoice;
   	}

    public GnuCashWritableGenerInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
    	super(entry.getGenerInvoice(), entry.getJwsdpPeer(), false); // <-- last one: important!
    }

    // -----------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getEntrySlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setEntrySlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getEntrySlots(),
										 getWritableGnuCashFile(),
										 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getEntrySlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getEntrySlots(),
										 	getWritableGnuCashFile(),
										 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getEntrySlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getEntrySlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(jwsdpPeer.getEntrySlots());
	}

    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public GnuCashWritableGenerInvoice getGenerInvoice() {
    	if (invoice == null) {
    		invoice = (GnuCashWritableGenerInvoice) super.getGenerInvoice();
    	}
    	
    	return invoice;
    }

    /**
     * {@inheritDoc}
     */
    public void setDate(final LocalDate date) {
    	if ( date == null ) {
    		throw new IllegalArgumentException("argument <date> is null");
    	}
	
    	if (!this.getGenerInvoice().isModifiable()) {
    		throw new IllegalStateException("This (generic) invoice has payments and is not modifiable");
    	}
    	ZonedDateTime oldDate = getDate();
    	ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(date, LocalTime.MIN),
						  ZoneId.systemDefault());
    	String dateTimeStr = dateTime.format(DATE_FORMAT_BOOK);
    	getJwsdpPeer().getEntryDate().setTsDate(dateTimeStr);

    	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
    	if (propertyChangeSupport != null) {
    		propertyChangeSupport.firePropertyChange("date", oldDate, date);
    	}
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(final String descr) {
    	if ( descr == null ) {
    		throw new IllegalArgumentException("argument <descr> is null");
    	}

	
//		if ( descr.trim().length() == 0 ) {
//	    	throw new IllegalArgumentException("argument <descr> is null");
//		}

    	if (!this.getGenerInvoice().isModifiable()) {
    		throw new IllegalStateException("This Invoice has payments and is not modifiable");
    	}
		String oldDescr = getDescription();
		getJwsdpPeer().setEntryDescription(descr);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if (propertyChangeSupport != null) {
			propertyChangeSupport.firePropertyChange("description", oldDescr, descr);
		}
    }

    // -----------------------------------------------------------

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashGenerInvoiceEntry#isCustInvcTaxable()
     */
    @Override
    public void setCustInvcTaxable(final boolean val) throws TaxTableNotFoundException {
    	if ( getType() != GCshOwner.Type.CUSTOMER && 
    		 getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractInvcEntry(this);
	
    	setCustInvcTaxable_core(val);
	
    	((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addInvcEntry(this);
    }

    private void setCustInvcTaxable_core(final boolean val) {
    	if ( getType() != GCshOwner.Type.CUSTOMER && 
    		 getType() != GCshOwner.Type.JOB )
		    	throw new WrongInvoiceTypeException();

    	if (val) {
    		getJwsdpPeer().setEntryITaxable(1);
    	} else {
    		getJwsdpPeer().setEntryITaxable(0);
    	}
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TaxTableNotFoundException
     */
    public void setCustInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractInvcEntry(this);

		super.setCustInvcTaxTable(taxTab);
		setCustInvcTaxTable_core(taxTab);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addInvcEntry(this);
    }

    private void setCustInvcTaxTable_core(final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( taxTab == null ) {
			getJwsdpPeer().setEntryITaxable(0);
		} else {
			getJwsdpPeer().setEntryITaxable(1);
			if ( getJwsdpPeer().getEntryITaxtable() == null ) {
				getJwsdpPeer().setEntryITaxtable(((GnuCashWritableFileImpl) getGenerInvoice().getGnuCashFile())
						.getObjectFactory().createGncGncEntryEntryITaxtable());
				getJwsdpPeer().getEntryITaxtable().setType(Const.XML_DATA_TYPE_GUID);
			}
			getJwsdpPeer().getEntryITaxtable().setValue(taxTab.getID().toString());
		}
    }

    // ------------------------

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashGenerInvoiceEntry#isCustInvcTaxable()
     */
    public void setVendBllTaxable(final boolean val) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractBillEntry(this);

		setVendBllTaxable_core(val);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addBillEntry(this);

	}

	private void setVendBllTaxable_core(final boolean val) {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( val ) {
			getJwsdpPeer().setEntryBTaxable(1);
		} else {
			getJwsdpPeer().setEntryBTaxable(0);
		}
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TaxTableNotFoundException
     */
    public void setVendBllTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractBillEntry(this);

		super.setVendBllTaxTable(taxTab);
		setVendBllTaxTable_core(taxTab);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addBillEntry(this);
    }

    private void setVendBllTaxTable_core(final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( taxTab == null ) {
			getJwsdpPeer().setEntryBTaxable(0);
		} else {
			getJwsdpPeer().setEntryBTaxable(1);
			if ( getJwsdpPeer().getEntryBTaxtable() == null ) {
				getJwsdpPeer().setEntryBTaxtable(((GnuCashWritableFileImpl) getGenerInvoice().getGnuCashFile())
						.getObjectFactory().createGncGncEntryEntryBTaxtable());
				getJwsdpPeer().getEntryBTaxtable().setType(Const.XML_DATA_TYPE_GUID);
			}
			getJwsdpPeer().getEntryBTaxtable().setValue(taxTab.getID().toString());
		}
    }

    /**
     * @param val 
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashGenerInvoiceEntry#isCustInvcTaxable()
     */
    public void setEmplVchTaxable(final boolean val) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractVoucherEntry(this);

		setEmplVchTaxable_core(val);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addVoucherEntry(this);
    }

    private void setEmplVchTaxable_core(final boolean val) {
		if ( getType() != GCshOwner.Type.EMPLOYEE && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( val ) {
			getJwsdpPeer().setEntryBTaxable(1);
		} else {
			getJwsdpPeer().setEntryBTaxable(0);
		}
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TaxTableNotFoundException
     */
    public void setEmplVchTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractVoucherEntry(this);

		super.setEmplVchTaxTable(taxTab);
		setEmplVchTaxTable_core(taxTab);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addVoucherEntry(this);
    }

    private void setEmplVchTaxTable_core(final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( taxTab == null ) {
			getJwsdpPeer().setEntryBTaxable(0);
		} else {
			getJwsdpPeer().setEntryBTaxable(1);
			if ( getJwsdpPeer().getEntryBTaxtable() == null ) {
				getJwsdpPeer().setEntryBTaxtable(((GnuCashWritableFileImpl) getGenerInvoice().getGnuCashFile())
						.getObjectFactory().createGncGncEntryEntryBTaxtable());
				getJwsdpPeer().getEntryBTaxtable().setType(Const.XML_DATA_TYPE_GUID);
			}
			getJwsdpPeer().getEntryBTaxtable().setValue(taxTab.getID().toString());
		}
    }

    // ------------------------
    
    /**
     * @param val 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     *  
     * @see GnuCashGenerInvoiceEntry#isCustInvcTaxable()
     */
    public void setJobInvcTaxable(final boolean val) throws TaxTableNotFoundException, UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractJobEntry(this);

		setJobInvcTaxable_core(val);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addJobEntry(this);
    }

    private void setJobInvcTaxable_core(final boolean val) throws UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
			setCustInvcTaxable_core(val);
		else if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
			setVendBllTaxable_core(val);
		else
			throw new UnknownInvoiceTypeException();
    }

    /**
     * {@inheritDoc}
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     */
    public void setJobInvcTaxTable(final GCshTaxTable taxTab) throws TaxTableNotFoundException, UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractJobEntry(this);

		super.setJobInvcTaxTable(taxTab);
		setJobInvcTaxTable_core(taxTab);

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addJobEntry(this);
    }

    private void setJobInvcTaxTable_core(final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
			setCustInvcTaxTable_core(taxTab);
		else if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
			setVendBllTaxTable_core(taxTab);
		else
			throw new UnknownInvoiceTypeException();
    }

    // -----------------------------------------------------------

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    public void setCustInvcPrice(final String n)
	    throws TaxTableNotFoundException {
    	this.setCustInvcPrice(new FixedPointNumber(n));
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    public void setCustInvcPrice(final FixedPointNumber price) throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( !this.getGenerInvoice().isModifiable() ) {
			throw new IllegalStateException("This customer invoice has payments and is not modifiable");
		}

		FixedPointNumber oldPrice = getCustInvcPrice();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractInvcEntry(this);

		getJwsdpPeer().setEntryIPrice(price.toGnuCashString());

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addInvcEntry(this);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null ) {
			propertyChangeSupport.firePropertyChange("price", oldPrice, price);
		}
    }

    public void setCustInvcPriceFormatted(final String n)
	    throws TaxTableNotFoundException {
    	this.setCustInvcPrice(new FixedPointNumber(n));
    }

    // ------------------------

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setVendBllPrice(final String n)
	    throws TaxTableNotFoundException {
    	this.setVendBllPrice(new FixedPointNumber(n));
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setVendBllPrice(final FixedPointNumber price)
	    throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( !this.getGenerInvoice().isModifiable() ) {
			throw new IllegalStateException("This vendor bill has payments and is not modifiable");
		}

		FixedPointNumber oldPrice = getVendBllPrice();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractBillEntry(this);

		getJwsdpPeer().setEntryBPrice(price.toGnuCashString());

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addBillEntry(this);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null ) {
			propertyChangeSupport.firePropertyChange("price", oldPrice, price);
		}
    }

    public void setVendBllPriceFormatted(final String n)
	    throws TaxTableNotFoundException {
    	this.setVendBllPrice(new FixedPointNumber(n));
    }

    // ------------------------

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setEmplVchPrice(final String n)
	    throws TaxTableNotFoundException {
    	this.setEmplVchPrice(new FixedPointNumber(n));
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setEmplVchPrice(final FixedPointNumber price)
	    throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( !this.getGenerInvoice().isModifiable() ) {
			throw new IllegalStateException("This employee voucher has payments and is not modifiable");
		}

		FixedPointNumber oldPrice = getEmplVchPrice();

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractVoucherEntry(this);

		getJwsdpPeer().setEntryBPrice(price.toGnuCashString());

		((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addVoucherEntry(this);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null ) {
			propertyChangeSupport.firePropertyChange("price", oldPrice, price);
		}
    }

    public void setEmplVchPriceFormatted(final String n)
	    throws TaxTableNotFoundException {
    	this.setEmplVchPrice(new FixedPointNumber(n));
    }

    // ------------------------

    /**
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setJobInvcPrice(final String n)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		// ::TODO: not quite so -- call "core" variant, as with setTaxable/setTaxTable
		if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
			setCustInvcPrice(n);
		else if ( getGenerInvoice().getOwnerType(ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
			setVendBllPrice(n);
		else
			throw new UnknownInvoiceTypeException();
    }

    /**
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setCustInvcPrice(FixedPointNumber)
     */
    @Override
    public void setJobInvcPrice(final FixedPointNumber price)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		// ::TODO: not quite so -- call "core" variant, as with setTaxable/setTaxTable
		GnuCashWritableJobInvoiceEntry jobInvcEntr = new GnuCashWritableJobInvoiceEntryImpl(this);
		if ( jobInvcEntr.getType() == GnuCashGenerJob.TYPE_CUSTOMER )
			setCustInvcPrice(price);
		else if ( jobInvcEntr.getType() == GnuCashGenerJob.TYPE_VENDOR )
			setVendBllPrice(price);
		else
			throw new UnknownInvoiceTypeException();
    }

    public void setJobInvcPriceFormatted(final String n)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
    	this.setJobInvcPrice(new FixedPointNumber(n));
    }

    // -----------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public void setAction(final Action act) {
		setActionStr(act.getLocaleString());
    }

    /**
     * {@inheritDoc}
     */
    public void setActionStr(final String actStr) {
		if ( actStr == null ) {
			throw new IllegalArgumentException("argument <actStr> is null");
		}

		if ( actStr.trim().length() == 0 ) {
			throw new IllegalArgumentException("argument <actStr> is empty");
		}

		if ( ! this.getGenerInvoice().isModifiable() ) {
			throw new IllegalStateException("This (generic) invoice has payments and is not modifiable");
		}

		String oldActStr = getActionStr();
		getJwsdpPeer().setEntryAction(actStr);

		PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
		if ( propertyChangeSupport != null ) {
			propertyChangeSupport.firePropertyChange("generInvcEntrAction", oldActStr, actStr);
		}
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setQuantity(FixedPointNumber)
     */
    public void setQuantity(final String n) throws TaxTableNotFoundException {
    	FixedPointNumber fp = new FixedPointNumber(n);
    	LOGGER.debug("setQuantity('" + n + "') - setting to " + fp.toGnuCashString());
    	this.setQuantity(fp);
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setQuantityFormatted(String)
     */
    public void setQuantityFormatted(final String n) throws TaxTableNotFoundException {
    	FixedPointNumber fp = new FixedPointNumber(n);
    	LOGGER.debug("setQuantityFormatted('" + n + "') - setting to " + fp.toGnuCashString());
    	this.setQuantity(fp);
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#setQuantity(FixedPointNumber)
     */
    public void setQuantity(final FixedPointNumber qty)
	    throws TaxTableNotFoundException {
    	if (!this.getGenerInvoice().isModifiable()) {
    		throw new IllegalStateException("This (generic) invoice has payments and is not modifiable");
    	}

    	FixedPointNumber oldQty = getQuantity();

    	((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).subtractInvcEntry(this);
    	getJwsdpPeer().setEntryQty(qty.toGnuCashString());
    	((GnuCashWritableGenerInvoiceImpl) getGenerInvoice()).addInvcEntry(this);

    	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
    	if (propertyChangeSupport != null) {
    		propertyChangeSupport.firePropertyChange("quantity", oldQty, qty);
    	}
    }

    /**
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoiceEntry#remove()
     */
    public void remove() throws TaxTableNotFoundException {
		if ( !this.getGenerInvoice().isModifiable() ) {
			throw new IllegalStateException("This (generic) invoice has payments and is not modifiable");
		}
		GnuCashWritableGenerInvoiceImpl gcshWrtblInvcImpl = ((GnuCashWritableGenerInvoiceImpl) getGenerInvoice());

		if ( getType() == GCshOwner.Type.VENDOR )
			gcshWrtblInvcImpl.removeBillEntry(this);
		else if ( getType() == GCshOwner.Type.CUSTOMER )
			gcshWrtblInvcImpl.removeInvcEntry(this);
		else if ( getType() == GCshOwner.Type.JOB )
			gcshWrtblInvcImpl.removeJobEntry(this);
		else if ( getType() == GCshOwner.Type.EMPLOYEE )
			gcshWrtblInvcImpl.removeVoucherEntry(this);
    }

    /**
     * {@inheritDoc}
     */
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
    	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }
    
    // ---------------------------------------------------------------

    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashWritableGenerInvoiceEntryImpl [");

		buffer.append("id=");
		buffer.append(getID());

		buffer.append(", type=");
		try {
			buffer.append(getType());
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", invoice-id=");
		buffer.append(getGenerInvoiceID());

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

		buffer.append(", price=");
		try {
			if ( getType() == GCshOwner.Type.CUSTOMER ) {
				buffer.append(getCustInvcPrice());
			} else if ( getType() == GCshOwner.Type.VENDOR ) {
				buffer.append(getVendBllPrice());
			} else if ( getType() == GCshOwner.Type.EMPLOYEE ) {
				buffer.append(getEmplVchPrice());
			} else if ( getType() == GCshOwner.Type.JOB ) {
				try {
					buffer.append(getJobInvcPrice());
				} catch (Exception e2) {
					buffer.append("ERROR");
				}
			} else
				buffer.append("ERROR");
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
    }

}
