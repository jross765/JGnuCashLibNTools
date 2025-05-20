package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashTransaction;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.GnuCashAccountImpl;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.GCshOwnerImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritableGenerInvoice;
import org.gnucash.api.write.GnuCashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillEntryImpl;
import org.gnucash.api.write.impl.spec.GnuCashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnuCashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnuCashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoice;
import org.gnucash.api.write.spec.GnuCashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnuCashWritableVendorBill;
import org.gnucash.api.write.spec.GnuCashWritableVendorBillEntry;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.GCshIDNotSetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;
import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Extension of GnuCashGenerInvoiceImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritableGenerInvoiceImpl extends GnuCashGenerInvoiceImpl 
                                             implements GnuCashWritableGenerInvoice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritableGenerInvoiceImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    protected final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Create an editable invoice facading an existing JWSDP-peer.
     *
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param gcshFile      the file to register under
     * @see GnuCashGenerInvoiceImpl#GnuCashInvoiceImpl(GncGncInvoice,
     *      GnuCashFile)
     */
    @SuppressWarnings("exports")
	public GnuCashWritableGenerInvoiceImpl(
			final GncGncInvoice jwsdpPeer, 
			final GnuCashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

    public GnuCashWritableGenerInvoiceImpl(
    		final GnuCashGenerInvoiceImpl invc, 
    		boolean addEntries, boolean addPayTrx) {
	super(invc.getJwsdpPeer(), invc.getGnuCashFile());

	// Entries
	if ( addEntries ) {
		// Does not work:
//		for ( GnuCashGenerInvoiceEntry entr : invc.getGenerEntries() ) {
//		    addGenerEntry(entr);
//		}
		// This works: 
		for ( GnuCashGenerInvoiceEntry entr : invc.getGnuCashFile().getGenerInvoiceEntries() ) {
		    if ( entr.getGenerInvoiceID().equals(invc.getID()) ) {
			addGenerEntry(entr);
		    }
		}
	}

	// Paying transactions
	if ( addPayTrx ) {
		for ( GnuCashTransaction trx : invc.getGnuCashFile().getTransactions() ) {
		    for ( GnuCashTransactionSplit splt : trx.getSplits() ) {
			GCshID lot = splt.getLotID();
			if ( lot != null ) {
			    GCshID lotID = invc.getLotID();
			    if ( lotID != null && 
				 lotID.equals(lot) ) {
				// Check if it's a payment transaction.
				// If so, add it to the invoice's list of payment transactions.
				if ( splt.getAction() == GnuCashTransactionSplit.Action.PAYMENT ) {
				    addPayingTransaction(splt);
				}
			    } // if lotID
			} // if lot
		    } // for splt
		} // for trx
	}
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

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableGenerInvoiceEntry createGenerEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws TaxTableNotFoundException {
//	System.err.println("GnuCashWritableGenerInvoiceEntry.createGenerEntry");
	
	GnuCashWritableGenerInvoiceEntryImpl entry = new GnuCashWritableGenerInvoiceEntryImpl(
								this, 
								acct, quantity, singleUnitPrice);
	
	addGenerEntry(entry);
	return entry;
    }
    
    // ----------------------------

    /**
     * create and add a new entry.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws TaxTableNotFoundException {

	GnuCashWritableCustomerInvoiceEntryImpl entry = new GnuCashWritableCustomerInvoiceEntryImpl(
								new GnuCashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

        entry.setCustInvcTaxable(false);
        
	addInvcEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws TaxTableNotFoundException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createCustInvcEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnuCashFile().getTaxTableByName(taxTabName);
	    LOGGER.debug("createCustInvcEntry: Found tax table with name '" + taxTabName + "': '" + taxTab.getID() + "'");
	    return createCustInvcEntry(acct,
		                       singleUnitPrice, quantity, 
		                       taxTab);
	}
    }

    /**
     * create and add a new entry.<br/>
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnuCashWritableCustomerInvoiceEntryImpl entry = new GnuCashWritableCustomerInvoiceEntryImpl(
								new GnuCashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().get(0).getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setCustInvcTaxable(false);
	} else {
	    entry.setCustInvcTaxTable(taxTab);
	}
	
	addInvcEntry(entry);
	return entry;
    }

    // ----------------------------

    /**
     * create and add a new entry.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableVendorBillEntry createVendBllEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws TaxTableNotFoundException {
	
	GnuCashWritableVendorBillEntryImpl entry = new GnuCashWritableVendorBillEntryImpl(
								new GnuCashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setVendBllTaxable(false);
	
	addBillEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableVendorBillEntry createVendBllEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws TaxTableNotFoundException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createVendBllEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnuCashFile().getTaxTableByName(taxTabName);
	    LOGGER.debug("createVendBillEntry: Found tax table with name '" + taxTabName + "': '" + taxTab.getID() + "'");
	    return createVendBllEntry(acct,
		                       singleUnitPrice, quantity, 
		                       taxTab);
	}
    }

    /**
     * create and add a new entry.<br/>
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableVendorBillEntry createVendBllEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnuCashWritableVendorBillEntryImpl entry = new GnuCashWritableVendorBillEntryImpl(
								new GnuCashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().get(0).getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setVendBllTaxable(false);
	} else {
	    entry.setVendBllTaxTable(taxTab);
	}
	
	addBillEntry(entry);
	return entry;
    }

    // ----------------------------

    /**
     * create and add a new entry.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws TaxTableNotFoundException {
	
	GnuCashWritableEmployeeVoucherEntryImpl entry = new GnuCashWritableEmployeeVoucherEntryImpl(
								new GnuCashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setEmplVchTaxable(false);
	
	addVoucherEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws TaxTableNotFoundException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createEmplVchEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnuCashFile().getTaxTableByName(taxTabName);
	    LOGGER.debug("createEmplVchEntry: Found tax table with name '" + taxTabName + "': '" + taxTab.getID() + "'");
	    return createEmplVchEntry(acct,
		                      singleUnitPrice, quantity, 
		                      taxTab);
	}
    }

    /**
     * create and add a new entry.<br/>
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     */
    public GnuCashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnuCashWritableEmployeeVoucherEntryImpl entry = new GnuCashWritableEmployeeVoucherEntryImpl(
								new GnuCashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().get(0).getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setEmplVchTaxable(false);
	} else {
	    entry.setEmplVchTaxTable(taxTab);
	}
	
	addVoucherEntry(entry);
	return entry;
    }

    // ----------------------------

    /**
     * create and add a new entry.
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     */
    public GnuCashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
	
	GnuCashWritableJobInvoiceEntryImpl entry = new GnuCashWritableJobInvoiceEntryImpl(
								new GnuCashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setJobInvcTaxable(false);
	
	addJobEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     */
    public GnuCashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createJobInvcEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnuCashFile().getTaxTableByName(taxTabName);
	    LOGGER.debug("createJobInvcEntry: Found tax table with name '" + taxTabName + "': '" + taxTab.getID() + "'");
	    return createJobInvcEntry(acct,
		                      singleUnitPrice, quantity, 
		                      taxTab);
	}
    }

    /**
     * create and add a new entry.<br/>
     *
     * @return an entry using the given Tax-Table
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     */
    public GnuCashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnuCashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnuCashWritableJobInvoiceEntryImpl entry = new GnuCashWritableJobInvoiceEntryImpl(
								new GnuCashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().get(0).getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setJobInvcTaxable(false);
	} else {
	    entry.setJobInvcTaxTable(taxTab);
	}
	
	addJobEntry(entry);
	return entry;
    }

    // -----------------------------------------------------------

    /**
     * Use
     * {@link GnuCashWritableFile#createWritableInvoice(String, GnuCashGenerJob, GnuCashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createCustomerInvoice_int(
	    final GnuCashWritableFileImpl file,
	    final String number, 
	    final GnuCashCustomer cust,
	    final boolean postInvoice,
	    final GnuCashAccountImpl incomeAcct,
	    final GnuCashAccountImpl receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncGncInvoice jwsdpInvc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncGncInvoice.InvoiceGuid invcRef = fact.createGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    jwsdpInvc.setInvoiceGuid(invcRef);
	}
	
	jwsdpInvc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::TODO Do *not* fill with invoice number,
	                                     // but instead with customer's reference number
	jwsdpInvc.setInvoiceActive(1);
	
	// currency
	{
	    GncGncInvoice.InvoiceCurrency currency = fact.createGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    jwsdpInvc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncGncInvoice.InvoiceOpened opened = fact.createGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    jwsdpInvc.setInvoiceOpened(opened);
	}
	
	// owner (customer)
	{
	    GncGncInvoice.InvoiceOwner custRef = fact.createGncGncInvoiceInvoiceOwner();
	    custRef.setOwnerType(GCshOwner.Type.CUSTOMER.getCode());
	    custRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(cust.getID().toString());
		custRef.setOwnerId(ownerIdRef);
	    }
	    jwsdpInvc.setInvoiceOwner(custRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createCustomerInvoice_int: Posting customer invoice " + invcGUID + "...");
	    postCustomerInvoice_int(file, fact,
	                            jwsdpInvc, invcGUID, number, 
	                            cust, 
                                    incomeAcct, receivableAcct,
                                    new FixedPointNumber(0), 
                                    postDate, dueDate);
	} else {
	    LOGGER.debug("createCustomerInvoice_int: NOT posting customer invoice " + invcGUID);
	}
	
	jwsdpInvc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(jwsdpInvc);
	file.setModified(true);
	
	LOGGER.debug("createCustomerInvoice_int: Created new customer invoice (core): " + jwsdpInvc.getInvoiceGuid().getValue());
	
	return jwsdpInvc;
    }

    // ---------------------------------------------------------------

    /**
     * Use
     * {@link GnuCashWritableFile#createWritableInvoice(String, GnuCashGenerJob, GnuCashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createVendorBill_int(
	    final GnuCashWritableFileImpl file,
	    final String number, 
	    final GnuCashVendor vend,
	    final boolean postInvoice,
	    final GnuCashAccountImpl expensesAcct,
	    final GnuCashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncGncInvoice jwsdpInvc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncGncInvoice.InvoiceGuid invcRef = fact.createGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    jwsdpInvc.setInvoiceGuid(invcRef);
	}
	
	jwsdpInvc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::CHECK Doesn't really make sense in a vendor bill
	                                     // And even if: would have to be separate number
	jwsdpInvc.setInvoiceActive(1);
	
	// currency
	{
	    GncGncInvoice.InvoiceCurrency currency = fact.createGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    jwsdpInvc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncGncInvoice.InvoiceOpened opened = fact.createGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    jwsdpInvc.setInvoiceOpened(opened);
	}
	
	// owner (vendor)
	{
	    GncGncInvoice.InvoiceOwner vendRef = fact.createGncGncInvoiceInvoiceOwner();
	    vendRef.setOwnerType(GCshOwner.Type.VENDOR.getCode());
	    vendRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(vend.getID().toString());
		vendRef.setOwnerId(ownerIdRef);
	    }
	    jwsdpInvc.setInvoiceOwner(vendRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createVendorBill_int: Posting vendor bill " + invcGUID + "...");
	    postVendorBill_int(file, fact,
		               jwsdpInvc, invcGUID, number, 
		               vend, 
		               expensesAcct, payableAcct, 
		               new FixedPointNumber(0),
		               postDate, dueDate);
	} else {
	    LOGGER.debug("createVendorBill_int: NOT posting vendor bill " + invcGUID);
	}
	
	jwsdpInvc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(jwsdpInvc);
	file.setModified(true);
	
	LOGGER.debug("createVendorBill_int: Created new vendor bill (core): " + jwsdpInvc.getInvoiceGuid().getValue());
	
	return jwsdpInvc;
    }

    /**
     * Use
     * {@link GnuCashWritableFile#createWritableInvoice(String, GnuCashGenerJob, GnuCashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createEmployeeVoucher_int(
	    final GnuCashWritableFileImpl file,
	    final String number, 
	    final GnuCashEmployee empl,
	    final boolean postInvoice,
	    final GnuCashAccountImpl expensesAcct,
	    final GnuCashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncGncInvoice jwsdpInvc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncGncInvoice.InvoiceGuid invcRef = fact.createGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    jwsdpInvc.setInvoiceGuid(invcRef);
	}
	
	jwsdpInvc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::CHECK Does taht make sense in an employee voucher?
	                                     // And if: wouldn't it have to be a separate number?
	jwsdpInvc.setInvoiceActive(1);
	
	// currency
	{
	    GncGncInvoice.InvoiceCurrency currency = fact.createGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    jwsdpInvc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncGncInvoice.InvoiceOpened opened = fact.createGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    jwsdpInvc.setInvoiceOpened(opened);
	}
	
	// owner (vendor)
	{
	    GncGncInvoice.InvoiceOwner vendRef = fact.createGncGncInvoiceInvoiceOwner();
	    vendRef.setOwnerType(GCshOwner.Type.EMPLOYEE.getCode());
	    vendRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(empl.getID().toString());
		vendRef.setOwnerId(ownerIdRef);
	    }
	    jwsdpInvc.setInvoiceOwner(vendRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createEmployeeVoucher_int: Posting employee voucher " + invcGUID + "...");
	    postEmployeeVoucher_int(file, fact,
		                    jwsdpInvc, invcGUID, number, 
		                    empl, 
		                    expensesAcct, payableAcct, 
		                    new FixedPointNumber(0),
		                    postDate, dueDate);
	} else {
	    LOGGER.debug("createEmployeeVoucher_int: NOT posting employee voucher " + invcGUID);
	}
	
	jwsdpInvc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(jwsdpInvc);
	file.setModified(true);
	
	LOGGER.debug("createEmployeeVoucher_int: Created new employee voucher (core): " + jwsdpInvc.getInvoiceGuid().getValue());
	
	return jwsdpInvc;
    }

    /**
     * Use
     * {@link GnuCashWritableFile#createWritableInvoice(String, GnuCashGenerJob, GnuCashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und Leistungen"
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createJobInvoice_int(
	    final GnuCashWritableFileImpl file,
	    final String number, 
	    final GnuCashGenerJob job,
	    final boolean postInvoice,
	    final GnuCashAccountImpl incExpAcct,
	    final GnuCashAccountImpl recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncGncInvoice jwsdpInvc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncGncInvoice.InvoiceGuid invcRef = fact.createGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    jwsdpInvc.setInvoiceGuid(invcRef);
	}
	
	jwsdpInvc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::TODO ::CHECK Do *not* fill with invoice number,
                                             // but instead with customer's reference number,
	                                     // if it's a customer job (and even then -- the job 
	                                     // itself should contain this number). If it's a 
	                                     // vendor bill, then this does not make sense anyway.
	jwsdpInvc.setInvoiceActive(1);
	
	// currency
	{
	    GncGncInvoice.InvoiceCurrency currency = fact.createGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    jwsdpInvc.setInvoiceCurrency(currency);
	}

	// date opened
	{
	    GncGncInvoice.InvoiceOpened opened = fact.createGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    jwsdpInvc.setInvoiceOpened(opened);
	}
	
	// owner (job)
	{
	    GncGncInvoice.InvoiceOwner jobRef = fact.createGncGncInvoiceInvoiceOwner();
	    jobRef.setOwnerType(GCshOwner.Type.JOB.getCode());
	    jobRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(job.getID().toString());
		jobRef.setOwnerId(ownerIdRef);
	    }
	    jwsdpInvc.setInvoiceOwner(jobRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createJobInvoice_int: Posting job invoice " + invcGUID + "...");
	    postJobInvoice_int(file, fact,
	                       jwsdpInvc, invcGUID, number, 
	                       job, 
                               incExpAcct, recvblPayblAcct, 
		               new FixedPointNumber(0),
                               postDate, dueDate);
	} else {
	    LOGGER.debug("createJobInvoice_int: NOT posting job invoice " + invcGUID);
	}
	
	jwsdpInvc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(jwsdpInvc);
	file.setModified(true);
	
	LOGGER.debug("createJobInvoice_int: Created new job invoice (core): " + jwsdpInvc.getInvoiceGuid().getValue());
	
	return jwsdpInvc;
    }

    
    // ---------------------------------------------------------------

    public void postCustomerInvoice(
	    final GnuCashWritableFile file,
	    GnuCashWritableCustomerInvoice invc,
	    final GnuCashCustomer cust,
	    final GnuCashAccount incomeAcct, 
	    final GnuCashAccount receivableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postCustomerInvoice: Posting customer invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnuCashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getCustInvcAmountWithTaxes();
	LOGGER.debug("postCustomerInvoice: Customer invoice amount: " + amount);
	
	GCshID postTrxID = postCustomerInvoice_int((GnuCashWritableFileImpl) file, fact, 
		                                   getJwsdpPeer(), 
		                                   invc.getID(), invc.getNumber(), 
		                                   cust,
		                                   (GnuCashAccountImpl) incomeAcct, 
		                                   (GnuCashAccountImpl) receivableAcct,
		                                   amount,
		                                   postDate, dueDate);
	LOGGER.info("postCustomerInvoice: Customer invoice " + invc.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postVendorBill(
	    final GnuCashWritableFile file,
	    GnuCashWritableVendorBill bll,
	    final GnuCashVendor vend,
	    final GnuCashAccount expensesAcct, 
	    final GnuCashAccount payableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postVendorBill: Posting vendor bill " + bll.getID() + "...");
	
	ObjectFactory fact = ((GnuCashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = bll.getVendBllAmountWithTaxes();
	LOGGER.debug("postVendorBill: Vendor bill amount: " + amount);
	
	GCshID postTrxID = postVendorBill_int((GnuCashWritableFileImpl) file, fact, 
		                              getJwsdpPeer(), 
		                              bll.getID(), bll.getNumber(), 
		                              vend,
		                              (GnuCashAccountImpl) expensesAcct, 
		                              (GnuCashAccountImpl) payableAcct, 
		                              amount,
		                              postDate, dueDate);
	LOGGER.info("postVendorBill: Vendor bill " + bll.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postEmployeeVoucher(
	    final GnuCashWritableFile file,
	    GnuCashWritableEmployeeVoucher vch,
	    final GnuCashEmployee empl,
	    final GnuCashAccount expensesAcct, 
	    final GnuCashAccount payableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postEmployeeVoucher: Posting employee voucher " + vch.getID() + "...");
	
	ObjectFactory fact = ((GnuCashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = vch.getVendBllAmountWithTaxes();
	LOGGER.debug("postVendorBill: Vendor bill amount: " + amount);
	
	GCshID postTrxID = postEmployeeVoucher_int((GnuCashWritableFileImpl) file, fact, 
  		                              	   getJwsdpPeer(), 
  		                              	   vch.getID(), vch.getNumber(),
  		                              	   empl,
  		                              	   (GnuCashAccountImpl) expensesAcct,
  		                              	   (GnuCashAccountImpl) payableAcct,
  		                              	   amount,
  		                              	   postDate, dueDate);
	LOGGER.info("postEmployeeVoucher: Employee voucher " + vch.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postJobInvoice(
	    final GnuCashWritableFile file,
	    GnuCashWritableJobInvoice invc,
	    final GnuCashGenerJob job,
	    final GnuCashAccount incomeAcct, 
	    final GnuCashAccount receivableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postJobInvoice: Posting job invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnuCashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getJobInvcAmountWithTaxes();
	LOGGER.debug("postJobInvoice: Job invoice amount: " + amount);
	
	GCshID postTrxID = postJobInvoice_int((GnuCashWritableFileImpl) file, fact, 
		           	              getJwsdpPeer(), 
		           	              invc.getID(), invc.getNumber(), 
		           	              job,
		                              (GnuCashAccountImpl) incomeAcct, 
		                              (GnuCashAccountImpl) receivableAcct, 
		                              amount,
		                              postDate, dueDate);
	LOGGER.info("postJobInvoice: Job invoice " + invc.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    // ----------------------------

    private static GCshID postCustomerInvoice_int(
	    final GnuCashWritableFileImpl file,
	    ObjectFactory fact, 
	    GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnuCashCustomer cust,
	    final GnuCashAccountImpl incomeAcct, 
	    final GnuCashAccountImpl receivableAcct,
	    final FixedPointNumber amount,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	// post account
	{
	    GncGncInvoice.InvoicePostacc postAcct = fact.createGncGncInvoiceInvoicePostacc();
	    postAcct.setType(Const.XML_DATA_TYPE_GUID);
	    postAcct.setValue(receivableAcct.getID().toString());
	    invcRef.setInvoicePostacc(postAcct);
	}
	
	// date posted
	{
	    GncGncInvoice.InvoicePosted posted = fact.createGncGncInvoiceInvoicePosted();
	    ZonedDateTime postDateTime = ZonedDateTime.of(
		    LocalDateTime.of(postDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String postDateTimeStr = postDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    posted.setTsDate(postDateTimeStr);
	    invcRef.setInvoicePosted(posted);
	}
	
	// post lot
	GCshID acctLotID = new GCshID();
	{
	    GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
	    postLotRef.setType(Const.XML_DATA_TYPE_GUID);

	    GncAccount.ActLots.GncLot newLot = createInvcPostLot_Customer(file, fact, 
		    					invcGUID, invcNumber, 
		                                        receivableAcct, cust);
	    
	    acctLotID.set(newLot.getLotId().getValue());
	    postLotRef.setValue(acctLotID.toString());
	    
	    invcRef.setInvoicePostlot(postLotRef);
	}
	
	// post transaction
	GCshID postTrxID = null;
	{
	    GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
	    postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
	    
	    
	    GnuCashWritableTransaction postTrx = createPostTransaction(file, fact, 
		    					invcGUID, GCshOwner.Type.CUSTOMER, 
		    					invcNumber, cust.getName(),
		    					incomeAcct, receivableAcct,
		    					acctLotID,
		    					amount, amount,
		    					postDate, dueDate);
	    postTrxID = postTrx.getID();
	    postTrxRef.setValue(postTrxID.toString());

	    invcRef.setInvoicePosttxn(postTrxRef);
	}
	
	return postTrxID;
    }
    
    private static GCshID postVendorBill_int(
	    final GnuCashWritableFileImpl file, 
            ObjectFactory fact, 
            GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnuCashVendor vend,
            final GnuCashAccountImpl expensesAcct, 
            final GnuCashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
        // post account
        {
            GncGncInvoice.InvoicePostacc postAcct = fact.createGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(payableAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncGncInvoice.InvoicePosted posted = fact.createGncGncInvoiceInvoicePosted();
	    ZonedDateTime postDateTime = ZonedDateTime.of(
		    LocalDateTime.of(postDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String postDateTimeStr = postDateTime.format(DATE_OPENED_FORMAT_BOOK);
            posted.setTsDate(postDateTimeStr);
            invcRef.setInvoicePosted(posted);
        }
        
        // post lot
	GCshID acctLotID = new GCshID();
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createBillPostLot_Vendor(file, fact, 
        	    					invcGUID, invcNumber,
        	    					payableAcct, vend);
    
            acctLotID.set(newLot.getLotId().getValue());
            postLotRef.setValue(acctLotID.toString());
            invcRef.setInvoicePostlot(postLotRef);
        }
    
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnuCashWritableTransaction postTrx = createPostTransaction(file, fact, 
        	    					invcGUID, GCshOwner.Type.VENDOR, 
        	    					invcNumber, vend.getName(),
        	    					expensesAcct, payableAcct,  
		    					acctLotID,
		    					amount, amount,
        	    					postDate, dueDate);
            postTrxID = postTrx.getID();
            postTrxRef.setValue(postTrxID.toString());
    
            invcRef.setInvoicePosttxn(postTrxRef);
        }
        
        return postTrxID;
    }

    private static GCshID postEmployeeVoucher_int(
	    final GnuCashWritableFileImpl file, 
            ObjectFactory fact, 
            GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnuCashEmployee empl,
            final GnuCashAccountImpl expensesAcct, 
            final GnuCashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
        // post account
        {
            GncGncInvoice.InvoicePostacc postAcct = fact.createGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(payableAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncGncInvoice.InvoicePosted posted = fact.createGncGncInvoiceInvoicePosted();
	    ZonedDateTime postDateTime = ZonedDateTime.of(
		    LocalDateTime.of(postDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String postDateTimeStr = postDateTime.format(DATE_OPENED_FORMAT_BOOK);
            posted.setTsDate(postDateTimeStr);
            invcRef.setInvoicePosted(posted);
        }
        
        // post lot
	GCshID acctLotID = new GCshID();
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createVoucherPostLot_Employee(file, fact, 
        	    					invcGUID, invcNumber,
        	    					payableAcct, empl);
    
            acctLotID.set(newLot.getLotId().getValue());
            postLotRef.setValue(acctLotID.toString());
            invcRef.setInvoicePostlot(postLotRef);
        }
    
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnuCashWritableTransaction postTrx = createPostTransaction(file, fact, 
        	    					invcGUID, GCshOwner.Type.VENDOR, 
        	    					invcNumber, empl.getUserName(),
        	    					expensesAcct, payableAcct,  
		    					acctLotID,
		    					amount, amount,
        	    					postDate, dueDate);
            postTrxID = postTrx.getID();
            postTrxRef.setValue(postTrxID.toString());
    
            invcRef.setInvoicePosttxn(postTrxRef);
        }
        
        return postTrxID;
    }

    private static GCshID postJobInvoice_int(
	    final GnuCashWritableFileImpl file,
            ObjectFactory fact, 
            GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnuCashGenerJob job,
            final GnuCashAccountImpl incExpAcct, 
            final GnuCashAccountImpl recvblPayblAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
        // post account
        {
            GncGncInvoice.InvoicePostacc postAcct = fact.createGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(recvblPayblAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncGncInvoice.InvoicePosted posted = fact.createGncGncInvoiceInvoicePosted();
	    ZonedDateTime postDateTime = ZonedDateTime.of(
		    LocalDateTime.of(postDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String postDateTimeStr = postDateTime.format(DATE_OPENED_FORMAT_BOOK);
            posted.setTsDate(postDateTimeStr);
            invcRef.setInvoicePosted(posted);
        }
        
        // post lot
	GCshID acctLotID = new GCshID();
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createInvcPostLot_Job(file, fact, 
        	    					invcGUID, invcNumber,
        	                                        recvblPayblAcct, job);
    
            acctLotID.set(newLot.getLotId().getValue());
            postLotRef.setValue(acctLotID.toString());
            invcRef.setInvoicePostlot(postLotRef);
        }
        
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnuCashWritableTransaction postTrx = createPostTransaction(file, fact, 
        	    					invcGUID, job.getOwnerType(), 
        	    					invcNumber, job.getName(),
        	    					incExpAcct, recvblPayblAcct,   
		    					acctLotID,
		    					amount, amount,
        	    					postDate, dueDate);
            postTrxID = postTrx.getID();
            postTrxRef.setValue(postTrxID.toString());
    
            invcRef.setInvoicePosttxn(postTrxRef);
        }
        
        return postTrxID;
    }

    // ----------------------------

    /**
     * @throws WrongOwnerTypeException 
     * @throws IllegalTransactionSplitActionException 
     * @see #GnuCashWritableInvoiceImpl(GnuCashWritableFileImpl, String, String,
     *      GnuCashGenerJob, GnuCashAccountImpl, Date)
     */
    private static GnuCashWritableTransaction createPostTransaction(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final GCshOwner.Type invcOwnerType, 
	    final String invcNumber, 
	    final String descr,
	    final GnuCashAccount fromAcct, // receivable/payable account
	    final GnuCashAccount toAcct,   // income/expense account
	    final GCshID acctLotID,
	    final FixedPointNumber amount,
	    final FixedPointNumber quantity,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, IllegalTransactionSplitActionException {
	if ( invcOwnerType != GCshOwner.Type.CUSTOMER &&
	     invcOwnerType != GCshOwner.Type.VENDOR ) // sic, TYPE_JOB not allowed here
	    throw new WrongOwnerTypeException();
	
	GnuCashWritableTransaction postTrx = file.createWritableTransaction();
	postTrx.setDatePosted(postDate);
	postTrx.setNumber(invcNumber);
	postTrx.setDescription(descr);

	GnuCashWritableTransactionSplit split1 = postTrx.createWritableSplit(fromAcct);
	split1.setValue(amount.copy().negate());
	split1.setQuantity(quantity.copy().negate());
	if ( invcOwnerType == GCshOwner.Type.CUSTOMER )
	    split1.setAction(GnuCashTransactionSplit.Action.INVOICE);
	else if ( invcOwnerType == GCshOwner.Type.VENDOR )
	    split1.setAction(GnuCashTransactionSplit.Action.BILL);
	else if ( invcOwnerType == GCshOwner.Type.EMPLOYEE )
	    split1.setAction(GnuCashTransactionSplit.Action.VOUCHER);
	    
	GnuCashWritableTransactionSplit split2 = postTrx.createWritableSplit(toAcct);
	split2.setValue(amount);
	split2.setQuantity(quantity);
	if ( invcOwnerType == GCshOwner.Type.CUSTOMER )
	    split2.setAction(GnuCashTransactionSplit.Action.INVOICE);
	else if ( invcOwnerType == GCshOwner.Type.VENDOR )
	    split2.setAction(GnuCashTransactionSplit.Action.BILL);
	else if ( invcOwnerType == GCshOwner.Type.EMPLOYEE )
	    split2.setAction(GnuCashTransactionSplit.Action.VOUCHER);
	split2.setLotID(acctLotID); // set reference to account lot, which in turn
	                            // references the invoice
	
	SlotsType slots = postTrx.getJwsdpPeer().getTrnSlots();

	if (slots == null) {
	    slots = factory.createSlotsType();
	    postTrx.getJwsdpPeer().setTrnSlots(slots);
	}

	// add trans-txn-type -slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey(Const.SLOT_KEY_INVC_TRX_TYPE);
	    value.setType(Const.XML_DATA_TYPE_STRING);
	    value.getContent().add(GnuCashTransaction.Type.INVOICE.getCode());

	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}

	// add trans-date-due -slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey(Const.SLOT_KEY_INVC_TRX_DATE_DUE);
	    value.setType(Const.XML_DATA_TYPE_TIMESPEC);
	    ZonedDateTime dueDateTime = ZonedDateTime.of(
		    LocalDateTime.of(dueDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String dueDateTimeStr = dueDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    JAXBElement<String> tsDate = factory.createTsDate(dueDateTimeStr);
	    value.getContent().add(tsDate);
	    
	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}
	
	// add trans-read-only-slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey(Const.SLOT_KEY_INVC_TRX_READ_ONLY);
	    value.setType(Const.XML_DATA_TYPE_STRING);
	    value.getContent().add(Const.getLocaleString("INVC_READ_ONLY_SLOT_TEXT"));
	    
	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}

	// add invoice-slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey(Const.SLOT_KEY_INVC_TYPE);
	    value.setType(Const.XML_DATA_TYPE_FRAME);
	    {
		Slot subslot = factory.createSlot();
		SlotValue subvalue = factory.createSlotValue();

		subslot.setSlotKey(Const.SLOT_KEY_INVC_GUID);
		subvalue.setType(Const.XML_DATA_TYPE_GUID);
		subvalue.getContent().add(invcID.toString());
		subslot.setSlotValue(subvalue);

		value.getContent().add(subslot);
	    }

	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}

	return postTrx;
    }

    // ---------------------------------------------------------------

    private static GncAccount.ActLots.GncLot createInvcPostLot_Customer(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnuCashAccountImpl postAcct,
	    final GnuCashCustomer cust) {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber, 
		                       postAcct, 
                                       GCshOwner.Type.CUSTOMER, GCshOwner.Type.CUSTOMER, // second one is dummy
                                       cust.getID());
    }

    private static GncAccount.ActLots.GncLot createBillPostLot_Vendor(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnuCashAccountImpl postAcct,
	    final GnuCashVendor vend) {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
		                       GCshOwner.Type.VENDOR, GCshOwner.Type.VENDOR, // second one is dummy
		                       vend.getID());
    }

    private static GncAccount.ActLots.GncLot createVoucherPostLot_Employee(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnuCashAccountImpl postAcct,
	    final GnuCashEmployee empl) {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
		                       GCshOwner.Type.EMPLOYEE, GCshOwner.Type.EMPLOYEE, // second one is dummy
		                       empl.getID());
    }

    private static GncAccount.ActLots.GncLot createInvcPostLot_Job(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID,
	    final String invcNumber,
	    final GnuCashAccountImpl postAcct,
	    final GnuCashGenerJob job) {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
                                       GCshOwner.Type.JOB, job.getOwnerType(), // second one is NOT dummy
                                       job.getID());
    }
    
    // ----------------------------

    private static GncAccount.ActLots.GncLot createInvcPostLot_Gener(
	    final GnuCashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnuCashAccountImpl postAcct,
	    final GCshOwner.Type ownerType1, // of invoice (direct)
	    final GCshOwner.Type ownerType2, // of invoice's owner (indirect, only relevant if ownerType1 is JOB)
	    final GCshID ownerID) {

	GncAccount.ActLots acctLots = postAcct.getJwsdpPeer().getActLots();
	if (acctLots == null) {
	    acctLots = factory.createGncAccountActLots();
	    postAcct.getJwsdpPeer().setActLots(acctLots);
	}

	GncAccount.ActLots.GncLot newLot = factory.createGncAccountActLotsGncLot();
	{
	    GncAccount.ActLots.GncLot.LotId id = factory.createGncAccountActLotsGncLotLotId();
	    id.setType(Const.XML_DATA_TYPE_GUID);
	    id.setValue(GCshID.getNew().toString());
	    newLot.setLotId(id);
	}
	newLot.setVersion(Const.XML_FORMAT_VERSION);

	// 2) Add slots to the lot (no, that no typo!)
	{
	    SlotsType slots = factory.createSlotsType();
	    newLot.setLotSlots(slots);
	}

	// 2.1) add title-slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey("title");
	    value.setType(Const.XML_DATA_TYPE_STRING);
	    
	    String contentStr = "(unset)";
	    if ( ownerType1 == GCshOwner.Type.CUSTOMER ) {
		contentStr = GnuCashTransactionSplit.Action.INVOICE.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.VENDOR ) {
		contentStr = GnuCashTransactionSplit.Action.BILL.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.EMPLOYEE ) {
		contentStr = GnuCashTransactionSplit.Action.VOUCHER.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.JOB ) {
		if ( ownerType2 == GCshOwner.Type.CUSTOMER ) {
		    contentStr = GnuCashTransactionSplit.Action.INVOICE.getLocaleString();
    		} else if ( ownerType2 == GCshOwner.Type.VENDOR ) {
		    contentStr = GnuCashTransactionSplit.Action.BILL.getLocaleString();
    		} else if ( ownerType2 == GCshOwner.Type.EMPLOYEE ) {
		    contentStr = GnuCashTransactionSplit.Action.VOUCHER.getLocaleString();
		}
	    }
	    contentStr += " " + invcNumber;  
	    value.getContent().add(contentStr);
	    
	    slot.setSlotValue(value);
	    newLot.getLotSlots().getSlot().add(slot);
	}

	// 2.2) add invoice-slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey(Const.SLOT_KEY_INVC_TYPE);
	    value.setType(Const.XML_DATA_TYPE_FRAME);
	    {
		Slot subslot = factory.createSlot();
		SlotValue subvalue = factory.createSlotValue();

		subslot.setSlotKey(Const.SLOT_KEY_INVC_GUID);
		subvalue.setType(Const.XML_DATA_TYPE_GUID);
		subvalue.getContent().add(invcID.toString());
		subslot.setSlotValue(subvalue);

		value.getContent().add(subslot);
	    }

	    slot.setSlotValue(value);
	    newLot.getLotSlots().getSlot().add(slot);
	}

	acctLots.getGncLot().add(newLot);

	return newLot;
    }

    // ---------------------------------------------------------------

    /**
     * @throws TaxTableNotFoundException
     * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
     */
    protected void removeInvcEntry(final GnuCashWritableGenerInvoiceEntryImpl impl)
	    throws TaxTableNotFoundException {

	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This customer invoice has payments and is not modifiable!");
	}

	this.subtractInvcEntry(impl);
	
	entries.remove(impl);
	
	getWritableGnuCashFile().removeGenerInvoiceEntry(impl);
    }

    /**
     * @throws TaxTableNotFoundException
     * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
     */
    protected void removeBillEntry(final GnuCashWritableGenerInvoiceEntryImpl impl)
	    throws TaxTableNotFoundException {

	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This vendor bill has payments and is not modifiable!");
	}

	// 2) remove generic invoice entry
	getWritableGnuCashFile().getRootElement().getGncBook().getBookElements()
		.remove(impl.getJwsdpPeer());
	getWritableGnuCashFile().setModified(true);
	
	this.subtractBillEntry(impl);
	
	entries.remove(impl);
	
	getWritableGnuCashFile().removeGenerInvoiceEntry(impl);
    }
    
    /**
     * @throws TaxTableNotFoundException
     * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
     */
    protected void removeVoucherEntry(final GnuCashWritableGenerInvoiceEntryImpl impl)
	    throws TaxTableNotFoundException {

	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This employee voucher has payments and is not modifiable!");
	}

	this.subtractVoucherEntry(impl);
	
	entries.remove(impl);
	
	getWritableGnuCashFile().removeGenerInvoiceEntry(impl);
    }
    
    /**
     * @throws TaxTableNotFoundException
     * @see #addInvcEntry(GnuCashGenerInvoiceEntryImpl)
     */
    protected void removeJobEntry(final GnuCashWritableGenerInvoiceEntryImpl impl)
	    throws TaxTableNotFoundException {

	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This job invoice has payments and is not modifiable!");
	}

	this.subtractJobEntry(impl);
	
	entries.remove(impl);
	
	getWritableGnuCashFile().removeGenerInvoiceEntry(impl);
    }
    
    // ---------------------------------------------------------------

    /**
     * Called by
     * {@link GnuCashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generEntr the entry to add to our internal list of invoice-entries
     */
    public void addRawGenerEntry(final GnuCashWritableGenerInvoiceEntryImpl generEntr) {
//	System.err.println("GnuCashWritableGenerInvoiceImpl.addRawGenerEntry " + generEntr.toString());

	if (!isModifiable()) {
	    throw new IllegalArgumentException("This invoice/bill has payments and thus is not modifiable");
	}

	super.addGenerEntry(generEntr);
    }

    /**
     * Called by
     * {@link GnuCashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws TaxTableNotFoundException
     */
    public void addInvcEntry(final GnuCashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.addInvcEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = generInvcEntr.isCustInvcTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getCustInvcSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getCustInvcSumInclTaxes();
	
	GCshID postAcctID = getCustInvcPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if ( generInvcEntr.isCustInvcTaxable() ) {
	    try {
		taxTab = generInvcEntr.getCustInvcTaxTable();
		if (taxTab == null) {
		    throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		}

		updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
		getGnuCashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		LOGGER.error("addInvcEntry: The given customer invoice entry has no i-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnuCashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws TaxTableNotFoundException
     */
    public void addBillEntry(final GnuCashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.addBillEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money to the tax-account

	boolean isTaxable = generInvcEntr.isVendBllTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getVendBllSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getVendBllSumInclTaxes();
	
	GCshID postAcctID = getVendBllPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if ( generInvcEntr.isVendBllTaxable() ) {
	    try {
		taxTab = generInvcEntr.getVendBllTaxTable();
		if (taxTab == null) {
		    throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		}

		updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
		getGnuCashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		LOGGER.error("addBillEntry: The given vendor bill entry has no b-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnuCashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws TaxTableNotFoundException
     */
    public void addVoucherEntry(final GnuCashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.EMPLOYEE )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.addVoucherEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money to the tax-account

	boolean isTaxable = generInvcEntr.isEmplVchTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getEmplVchSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getEmplVchSumInclTaxes();
	
	GCshID postAcctID = getEmplVchPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if ( generInvcEntr.isEmplVchTaxable() ) {
	    try {
		taxTab = generInvcEntr.getEmplVchTaxTable();
		if (taxTab == null) {
		    throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + ")");
		}

		updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
		getGnuCashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + ")";
		LOGGER.error("addVoucherEntry: The given employee voucher entry has no b-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnuCashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnuCashWritableGenerInvoiceImpl, GnuCashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws TaxTableNotFoundException
     */
    public void addJobEntry(final GnuCashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.addJobEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from/to the tax-account

	boolean isTaxable = generInvcEntr.isJobInvcTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getJobInvcSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getJobInvcSumInclTaxes();
	
	GCshID postAcctID = getJobInvcPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if ( generInvcEntr.isJobInvcTaxable() ) {
	    try {
		taxTab = generInvcEntr.getJobInvcTaxTable();
		if (taxTab == null) {
		    throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (entry ID: " + generInvcEntr.getID() + ")");
		}
		
		updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
		getGnuCashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (entry ID: " + generInvcEntr.getID() + ")");
		LOGGER.error("addJobEntry: The given job invoice entry has no b/i-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    // ---------------------------------------------------------------

    protected void subtractInvcEntry(final GnuCashGenerInvoiceEntryImpl entry)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.subtractInvcEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isCustInvcTaxable();
	FixedPointNumber sumExclTaxes = entry.getCustInvcSumExclTaxes().copy().negate();
	FixedPointNumber sumInclTaxes = entry.getCustInvcSumInclTaxes().copy().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryIAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isCustInvcTaxable()) {
	    taxTab = entry.getCustInvcTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (its i-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryITaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getGnuCashFile().setModified(true);
    }

    protected void subtractBillEntry(final GnuCashGenerInvoiceEntryImpl entry)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.subtractBillEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isVendBllTaxable();
	FixedPointNumber sumExclTaxes = entry.getVendBllSumExclTaxes().copy().negate();
	FixedPointNumber sumInclTaxes = entry.getVendBllSumInclTaxes().copy().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isVendBllTaxable()) {
	    taxTab = entry.getVendBllTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (its b-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getGnuCashFile().setModified(true);
    }

    protected void subtractVoucherEntry(final GnuCashGenerInvoiceEntryImpl entry)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.subtractVoucherEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isEmplVchTaxable();
	FixedPointNumber sumExclTaxes = entry.getEmplVchSumExclTaxes().copy().negate();
	FixedPointNumber sumInclTaxes = entry.getEmplVchSumInclTaxes().copy().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isEmplVchTaxable()) {
	    taxTab = entry.getEmplVchTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (its b-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getGnuCashFile().setModified(true);
    }

    protected void subtractJobEntry(final GnuCashGenerInvoiceEntryImpl entry)
	    throws TaxTableNotFoundException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnuCashWritableGenerInvoiceImpl.subtractJobEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isJobInvcTaxable();
	FixedPointNumber sumExclTaxes = entry.getJobInvcSumExclTaxes().copy().negate();
	FixedPointNumber sumInclTaxes = entry.getJobInvcSumInclTaxes().copy().negate();
	
	GCshID postAcctID = new GCshID();
	if ( entry.getGenerInvoice().getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
	    postAcctID.set(entry.getJwsdpPeer().getEntryIAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
	    postAcctID.set(entry.getJwsdpPeer().getEntryBAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnuCashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.EMPLOYEE )
	    postAcctID.set(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isJobInvcTaxable()) {
	    taxTab = entry.getJobInvcTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (its b/i-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "' and '" 
			+ entry.getJwsdpPeer().getEntryITaxtable().getValue() + "' resp.)");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @return the AccountID of the Account to transfer the money from
     */
    protected GCshID getCustInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
	if ( getType() != GnuCashGenerInvoice.TYPE_CUSTOMER &&
	     getType() != GnuCashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return new GCshID(entry.getJwsdpPeer().getEntryIAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     */
    protected GCshID getVendBllPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
	if ( getType() != GnuCashGenerInvoice.TYPE_VENDOR &&
	     getType() != GnuCashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	return new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     */
    protected GCshID getEmplVchPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
	if ( getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE &&
	     getType() != GnuCashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	return new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
*    */
    protected GCshID getJobInvcPostAccountID(final GnuCashGenerInvoiceEntryImpl entry) {
	if ( getType() != GnuCashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	    GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(this);
	    if ( jobInvc.getType() == GnuCashGenerJob.TYPE_CUSTOMER )
		return getCustInvcPostAccountID(entry);
	    else if ( jobInvc.getType() == GnuCashGenerJob.TYPE_VENDOR )
		return getVendBllPostAccountID(entry);
	    
	    return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     */
    private void updateEntry(
	    final GCshTaxTable taxTab, 
	    final boolean isTaxable, 
	    final FixedPointNumber sumExclTaxes,
	    final FixedPointNumber sumInclTaxes, 
	    final GCshID postAcctID) {
	LOGGER.debug("GnuCashWritableGenerInvoiceImpl.updateEntry " 
		+ "isTaxable=" + isTaxable + " "
		+ "post-acct=" + postAcctID + " ");

	GnuCashWritableTransactionImpl postTrx = (GnuCashWritableTransactionImpl) getPostTransaction();
	if (postTrx == null) {
	    return; // invoice may not be posted
	}
	
	if (isTaxable) {
	    updateEntry_taxStuff(taxTab, 
		                 sumExclTaxes, sumInclTaxes, 
		                 postAcctID,
		                 postTrx);
	}

	updateNonTaxableEntry(sumExclTaxes, sumInclTaxes, postAcctID);
	getGnuCashFile().setModified(true);
    }

    private void updateEntry_taxStuff(
	    final GCshTaxTable taxtable, 
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID postAcctID, 
	    GnuCashWritableTransactionImpl postTrx) throws IllegalTransactionSplitActionException {
	// get the first account of the taxTable
	GCshTaxTableEntry taxTableEntry = taxtable.getEntries().get(0);
	GnuCashAccount taxAcct = taxTableEntry.getAccount();
	FixedPointNumber entryTaxAmount = sumInclTaxes.copy().subtract(sumExclTaxes);

	LOGGER.debug("GnuCashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
	    + "post-acct=" + postAcctID + " " 
	    + "tax-acct=" + taxAcct.getQualifiedName() + " "
	    + "entryTaxAmount=" + entryTaxAmount + " "
	    + "#splits=" + postTrx.getSplits().size());

	// failed for subtractEntry assert entryTaxAmount.isPositive() ||
	// entryTaxAmount.equals(new FixedPointNumber());

	boolean postTransactionTaxUpdated = false;
	for (GnuCashTransactionSplit element : postTrx.getSplits()) {
	GnuCashWritableTransactionSplitImpl split = (GnuCashWritableTransactionSplitImpl) element;
	if (split.getAccountID().equals(taxAcct.getID())) {

	    // quantity gets updated automagically
	    // split.setQuantity(split.getQuantity().subtract(entryTaxAmount));
	    split.setValue(split.getValue().subtract(entryTaxAmount));

	    // failed for subtractEntry assert !split.getValue().isPositive();
	    // failed for subtractEntry assert !split.getQuantity().isPositive();

	    LOGGER.info("GnuCashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		    + "updated tax-split=" + split.getID() + " " 
		    + "of account " + split.getAccount().getQualifiedName() + " " 
		    + "to value " + split.getValue());

	    postTransactionTaxUpdated = true;
	    break;
	}
	LOGGER.debug("GnuCashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		+ "ignoring non-tax-split=" + split.getID() + " " 
		+ "of value " + split.getValue() + " "
		+ "and account " + split.getAccount().getQualifiedName());
	}
	
	if (!postTransactionTaxUpdated) {
	GnuCashWritableTransactionSplitImpl split = 
		(GnuCashWritableTransactionSplitImpl) postTrx
			.createWritableSplit(taxAcct);
	split.setQuantity(entryTaxAmount.copy().negate());
	split.setValue(entryTaxAmount.copy().negate());

	// assert !split.getValue().isPositive();
	// assert !split.getQuantity().isPositive();

	split.setAction(GnuCashTransactionSplit.Action.INVOICE);

	LOGGER.info("GnuCashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		+ "created new tax-split=" + split.getID() + " " 
		+ "of value " + split.getValue() + " "
		+ "and account " + split.getAccount().getQualifiedName());
	}
    }

    /**
     * @param sumExclTaxes
     * @param sumInclTaxes
     * @param accountToTransferMoneyFrom
     */
    private void updateNonTaxableEntry(
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID accountToTransferMoneyFrom) {

//	System.err.println("GnuCashWritableGenerInvoiceImpl.updateNonTaxableEntry " 
//		+ "accountToTransferMoneyFrom=" + accountToTransferMoneyFrom);

	GnuCashWritableTransactionImpl postTransaction = (GnuCashWritableTransactionImpl) getPostTransaction();
	if (postTransaction == null) {
	    return; // invoice may not be posted
	}

	// ==============================================================
	// update transaction-split that transferes the sum incl. taxes from the
	// incomeAccount
	// (e.g. "Umsatzerloese 19%")
	GCshID accountToTransferMoneyTo = getPostAccountID();
	boolean postTransactionSumUpdated = false;

	LOGGER.debug("GnuCashWritableGenerInvoiceImpl.updateNonTaxableEntry #splits=" + postTransaction.getSplits().size());

	for (Object element : postTransaction.getSplits()) {
	    GnuCashWritableTransactionSplitImpl split = (GnuCashWritableTransactionSplitImpl) element;
	    if (split.getAccountID().equals(accountToTransferMoneyTo)) {

		FixedPointNumber value = split.getValue();
		split.setQuantity(split.getQuantity().add(sumInclTaxes));
		split.setValue(value.add(sumInclTaxes));
		postTransactionSumUpdated = true;

		LOGGER.info("GnuCashWritableGenerInvoiceImpl.updateNonTaxableEntry updated split " + split.getID());
		break;
	    }
	}

	if (!postTransactionSumUpdated) {
	    GnuCashWritableTransactionSplitImpl split = 
		    (GnuCashWritableTransactionSplitImpl) postTransaction
		    	.createWritableSplit(getGnuCashFile().getAccountByID(accountToTransferMoneyTo));
	    split.setQuantity(sumInclTaxes);
	    split.setValue(sumInclTaxes);
	    split.setAction(GnuCashTransactionSplit.Action.INVOICE);

	    // this split must have a reference to the lot
	    // as has the transaction-split of the whole sum in the
	    // transaction when the invoice is Paid
	    GncTransaction.TrnSplits.TrnSplit.SplitLot lotref = 
		    ((GnuCashFileImpl) getGnuCashFile()).getObjectFactory()
		    	.createGncTransactionTrnSplitsTrnSplitSplitLot();
	    lotref.setType(getJwsdpPeer().getInvoicePostlot().getType());
	    lotref.setValue(getJwsdpPeer().getInvoicePostlot().getValue());
	    split.getJwsdpPeer().setSplitLot(lotref);

	    LOGGER.info("GnuCashWritableGenerInvoiceImpl.updateNonTaxableEntry created split " + split.getID());
	}

	// ==============================================================
	// update transaction-split that transferes the sum incl. taxes to the
	// postAccount
	// (e.g. "Forderungen aus Lieferungen und Leistungen")

	boolean postTransactionNetSumUpdated = false;
	for (GnuCashTransactionSplit element : postTransaction.getSplits()) {
	    GnuCashWritableTransactionSplitImpl split = (GnuCashWritableTransactionSplitImpl) element;
	    if (split.getAccountID().equals(accountToTransferMoneyFrom)) {

		FixedPointNumber value = split.getValue();
		split.setQuantity(split.getQuantity().subtract(sumExclTaxes));
		split.setValue(value.subtract(sumExclTaxes));
		split.getJwsdpPeer().setSplitAction(GnuCashTransactionSplit.Action.INVOICE.getLocaleString());
		postTransactionNetSumUpdated = true;
		break;
	    }
	}
	
	if (!postTransactionNetSumUpdated) {
	    GnuCashWritableTransactionSplitImpl split = 
		    new GnuCashWritableTransactionSplitImpl(
			    postTransaction,
			    getGnuCashFile().getAccountByID(accountToTransferMoneyFrom));
	    split.setQuantity(sumExclTaxes.copy().negate());
	    split.setValue(sumExclTaxes.copy().negate());
	}

	assert postTransaction.isBalanced();
	getGnuCashFile().setModified(true);
    }

    /**
     * @see GnuCashWritableGenerInvoice#isModifiable()
     */
    public boolean isModifiable() {
	return getPayingTransactions().size() == 0;
    }

    /**
     * Throw an IllegalStateException if we are not modifiable.
     *
     * @see #isModifiable()
     */
    protected void attemptChange() {
	if (!isModifiable()) {
	    throw new IllegalStateException(
		    "this invoice is NOT changeable because there already have been made payments for it!");
	}
    }

    // -----------------------------------------------------------

	public void setOwnerID(final GCshID ownID) {
    	if ( ownID == null ) {
    	    throw new IllegalArgumentException("null owner ID given!");
    	}
    	
    	if ( ! ownID.isSet() ) {
    	    throw new IllegalArgumentException("unset owner ID given!");
    	}
    	
		GCshOwner oldOwner = new GCshOwnerImpl(getOwnerID(), GCshOwner.JIType.INVOICE, getGnuCashFile());
		GCshOwner newOwner = new GCshOwnerImpl(ownID, GCshOwner.JIType.INVOICE, getGnuCashFile());
    	if ( oldOwner.getInvcType() != newOwner.getInvcType() )
    		throw new IllegalStateException("Invoice owner type may not change");

		GCshID oldOwnID = getOwnerID();
		if ( oldOwnID.equals(ownID) ) {
			return; // nothing has changed
		}
		
    	try {
        	attemptChange();
			getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(ownID.get());
	    	getGnuCashFile().setModified(true);
		} catch (GCshIDNotSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		// <<insert code to react further to this change here
		PropertyChangeSupport propertyChangeFirer = helper.getPropertyChangeSupport();
		if ( propertyChangeFirer != null ) {
			propertyChangeFirer.firePropertyChange("ownerID", oldOwnID, ownID);
		}
	}

    public void setOwner(GCshOwner own) {
    	if ( own == null ) {
    	    throw new IllegalArgumentException("null owner given!");
    	}
    	
    	if ( own.getJIType() != GCshOwner.JIType.INVOICE )
    		throw new IllegalArgumentException("Wrong GCshOwner JI-type");

    	if ( ! own.getID().isSet() ) {
    	    throw new IllegalArgumentException("unset owner ID given!");
    	}
    	
    	if ( getType() != own.getInvcType() )
    		throw new IllegalStateException("Invoice owner type may not change");
    	
        setOwnerID(own.getID());
    }

    // -----------------------------------------------------------

    /**
     * @see #setDatePosted(String)
     */
    public void setDateOpened(final LocalDate d) {
	if ( d == null ) {
	    throw new IllegalArgumentException("null date given!");
	}

	attemptChange();
	dateOpened = ZonedDateTime.of(d, LocalTime.MIN, ZoneId.systemDefault());
	String dateOpenedStr = dateOpened.format(DATE_OPENED_FORMAT_BOOK);
	getJwsdpPeer().getInvoiceOpened().setTsDate(dateOpenedStr);
	getGnuCashFile().setModified(true);
    }

    /**
     * @see #setDatePosted(String)
     */
    public void setDateOpened(final String d) throws java.text.ParseException {
	if ( d == null ) {
	    throw new IllegalArgumentException("null date string given!");
	}

	if ( d.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty date string given!");
	}

	attemptChange();
	setDateOpened(LocalDate.parse(d, DATE_OPENED_FORMAT));
	getGnuCashFile().setModified(true);
    }

    /**
     * @see #setDateOpened(String)
     */
    public void setDatePosted(final LocalDate d) {
	if ( d == null ) {
	    throw new IllegalArgumentException("null date given!");
	}

	attemptChange();
	datePosted = ZonedDateTime.of(d, LocalTime.MIN, ZoneId.systemDefault());
	getJwsdpPeer().getInvoicePosted().setTsDate(DATE_OPENED_FORMAT.format(d));
	getGnuCashFile().setModified(true);

	// change the date of the transaction too
	GnuCashWritableTransaction postTr = getWritablePostTransaction();
	if (postTr != null) {
	    postTr.setDatePosted(d);
	}
    }

    /**
     * @see GnuCashWritableGenerInvoice#setDatePosted(java.lang.String)
     */
    public void setDatePosted(final String d) throws java.text.ParseException {
	if ( d == null ) {
	    throw new IllegalArgumentException("null date string given!");
	}

	if ( d.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty date string given!");
	}

	setDatePosted(LocalDate.parse(d, DATE_OPENED_FORMAT));
    }
    
    // ---------------------------------------------------------------

    public void setNumber(final String number) {
	if ( number == null ) {
	    throw new IllegalArgumentException("null number given!");
	}

	if ( number.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty number given!");
	}

	attemptChange();
	getJwsdpPeer().setInvoiceId(number);
	getGnuCashFile().setModified(true);
    }

    public void setDescription(final String descr) {
	if ( descr == null ) {
	    throw new IllegalArgumentException("null description given!");
	}

	// Caution: empty string allowed here
//	if ( descr.trim().length() == 0 ) {
//	    throw new IllegalArgumentException("empty description given!");
//	}

	attemptChange();
	getJwsdpPeer().setInvoiceNotes(descr);
	getGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @return 
     *  
     * @see GnuCashGenerInvoice#getPayingTransactions()
     */
    public Collection<GnuCashWritableTransaction> getWritablePayingTransactions() {
	Collection<GnuCashWritableTransaction> trxList = new ArrayList<GnuCashWritableTransaction>();

	for (GnuCashTransaction trx : getPayingTransactions()) {
	    GnuCashWritableTransaction newTrx = new GnuCashWritableTransactionImpl(trx);
	    trxList.add(newTrx);
	}

	return trxList;
    }

    /**
     * @return get a modifiable version of
     *         {@link GnuCashGenerInvoiceImpl#getPostTransaction()}
     */
    public GnuCashWritableTransaction getWritablePostTransaction() {
	GncGncInvoice.InvoicePosttxn invoicePosttxn = jwsdpPeer.getInvoicePosttxn();
	if (invoicePosttxn == null) {
	    return null; // invoice may not be posted
	}
	
	GCshID invcPostTrxID = new GCshID( invoicePosttxn.getValue() );
	return getGnuCashFile().getWritableTransactionByID(invcPostTrxID);
    }
    
    // ---------------------------------------------------------------

    public List<GnuCashWritableGenerInvoiceEntry> getWritableGenerEntries() {
    	ArrayList<GnuCashWritableGenerInvoiceEntry> result = new ArrayList<GnuCashWritableGenerInvoiceEntry>();
    	
    	for ( GnuCashGenerInvoiceEntry entr : getGenerEntries() ) {
    		GnuCashWritableGenerInvoiceEntry newEntr = new GnuCashWritableGenerInvoiceEntryImpl(entr);
    		result.add(newEntr);
    	}
    	
    	return result;
    }

    /**
     * @see #getGenerEntryByID(GCshID)
     */
    public GnuCashWritableGenerInvoiceEntry getWritableGenerEntryByID(final GCshID id) {
	return new GnuCashWritableGenerInvoiceEntryImpl(super.getGenerEntryByID(id));
    }

    // ---------------------------------------------------------------

    /**
     * 
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoice#remove()
     */
    public void remove() throws TaxTableNotFoundException {
    	remove(true);
    }

    /**
     * 
     * @throws TaxTableNotFoundException
     *  
     * @see GnuCashWritableGenerInvoice#remove()
     */
    public void remove(final boolean withEntries) throws TaxTableNotFoundException {

	if (!isModifiable()) {
	    throw new IllegalStateException("This (generic) invoice has payments and cannot be deleted!");
	}

	((GnuCashWritableFileImpl) getGnuCashFile()).removeGenerInvoice(this, withEntries);

    }

    // ---------------------------------------------------------------

    @Override
    public void setURL(final String url) {
    	try {
    		setUserDefinedAttribute(Const.SLOT_KEY_ASSOC_URI, url);
    	} catch (SlotListDoesNotContainKeyException exc ) {
    		addUserDefinedAttribute(Const.XML_DATA_TYPE_STRING, Const.SLOT_KEY_ASSOC_URI, url);
    	}
    }

    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			ObjectFactory fact = getGnuCashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setInvoiceSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 getWritableGnuCashFile(),
										 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 	getWritableGnuCashFile(),
										 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 getWritableGnuCashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(jwsdpPeer.getInvoiceSlots());
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashWritableGenerInvoiceImpl [");

	buffer.append("id=");
	buffer.append(getID());

	buffer.append(", owner-id=");
	buffer.append(getOwnerID());

	buffer.append(", owner-type (dir.)=");
	try {
	    buffer.append(getOwnerType(ReadVariant.DIRECT));
	} catch (WrongInvoiceTypeException e) {
	    // TODO Auto-generated catch block
	    buffer.append("ERROR");
	}

	buffer.append(", number='");
	buffer.append(getNumber() + "'");

	buffer.append(", description='");
	buffer.append(getDescription() + "'");

	buffer.append(", #entries=");
	buffer.append(entries.size());

	buffer.append(", date-opened=");
	try {
	    buffer.append(getDateOpened().toLocalDate().format(DATE_OPENED_FORMAT_PRINT));
	} catch (Exception e) {
	    buffer.append(getDateOpened().toLocalDate().toString());
	}

	buffer.append("]");
	return buffer.toString();
    }

}
