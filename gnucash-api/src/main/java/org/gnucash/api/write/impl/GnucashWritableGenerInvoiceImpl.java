package org.gnucash.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.gnucash.api.Const;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncGncInvoice;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerInvoiceEntry;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.IllegalTransactionSplitActionException;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.aux.WrongOwnerJITypeException;
import org.gnucash.api.read.impl.GnucashAccountImpl;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceEntryImpl;
import org.gnucash.api.read.impl.GnucashGenerInvoiceImpl;
import org.gnucash.api.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.GnucashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.gnucash.api.write.impl.hlp.GnucashWritableObjectImpl;
import org.gnucash.api.write.impl.hlp.HasWritableUserDefinedAttributesImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherEntryImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableJobInvoiceEntryImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableJobInvoiceImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorBillEntryImpl;
import org.gnucash.api.write.impl.spec.GnucashWritableVendorBillImpl;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.api.write.spec.GnucashWritableCustomerInvoiceEntry;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.api.write.spec.GnucashWritableEmployeeVoucherEntry;
import org.gnucash.api.write.spec.GnucashWritableJobInvoice;
import org.gnucash.api.write.spec.GnucashWritableJobInvoiceEntry;
import org.gnucash.api.write.spec.GnucashWritableVendorBill;
import org.gnucash.api.write.spec.GnucashWritableVendorBillEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;

/**
 * Extension of GnucashGenerInvoiceImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableGenerInvoiceImpl extends GnucashGenerInvoiceImpl 
                                             implements GnucashWritableGenerInvoice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableGenerInvoiceImpl.class);

    // ---------------------------------------------------------------
    
    /**
     * Our helper to implement the GnucashWritableObject-interface.
     */
    protected final GnucashWritableObjectImpl helper = new GnucashWritableObjectImpl(getWritableGnucashFile(), this);

    // ---------------------------------------------------------------

    /**
     * Create an editable invoice facading an existing JWSDP-peer.
     *
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param gcshFile      the file to register under
     * @see GnucashGenerInvoiceImpl#GnucashInvoiceImpl(GncGncInvoice,
     *      GnucashFile)
     */
    @SuppressWarnings("exports")
	public GnucashWritableGenerInvoiceImpl(
			final GncGncInvoice jwsdpPeer, 
			final GnucashFile gcshFile) {
		super(jwsdpPeer, gcshFile);
	}

    public GnucashWritableGenerInvoiceImpl(final GnucashGenerInvoiceImpl invc) {
	super(invc.getJwsdpPeer(), invc.getGnucashFile());

	// Entries
	// Does not work:
//	for ( GnucashGenerInvoiceEntry entr : invc.getGenerEntries() ) {
//	    addGenerEntry(entr);
//	}
	// This works: 
	for ( GnucashGenerInvoiceEntry entr : invc.getGnucashFile().getGenerInvoiceEntries() ) {
	    if ( entr.getGenerInvoiceID().equals(invc.getID()) ) {
		addGenerEntry(entr);
	    }
	}

	// Paying transactions
	for ( GnucashTransaction trx : invc.getGnucashFile().getTransactions() ) {
	    for ( GnucashTransactionSplit splt : trx.getSplits() ) {
		GCshID lot = splt.getLotID();
		if ( lot != null ) {
		    GCshID lotID = invc.getLotID();
		    if ( lotID != null && 
			 lotID.equals(lot) ) {
			// Check if it's a payment transaction.
			// If so, add it to the invoice's list of payment transactions.
			if ( splt.getAction() == GnucashTransactionSplit.Action.PAYMENT ) {
			    addPayingTransaction(splt);
			}
		    } // if lotID
		} // if lot
	    } // for splt
	} // for trx
    }

    // ---------------------------------------------------------------

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getWritableGnucashFile() {
    	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getGnucashFile() {
    	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableGenerInvoiceEntry createGenerEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException {
//	System.err.println("GnucashWritableGenerInvoiceEntry.createGenerEntry");
	
	GnucashWritableGenerInvoiceEntryImpl entry = new GnucashWritableGenerInvoiceEntryImpl(
								this, 
								acct, quantity, singleUnitPrice);
	
	addGenerEntry(entry);
	return entry;
    }
    
    // ----------------------------

    /**
     * create and add a new entry.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	GnucashWritableCustomerInvoiceEntryImpl entry = new GnucashWritableCustomerInvoiceEntryImpl(
								new GnucashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

        entry.setCustInvcTaxable(false);
        
	addInvcEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createCustInvcEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnucashFile().getTaxTableByName(taxTabName);
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableCustomerInvoiceEntryImpl entry = new GnucashWritableCustomerInvoiceEntryImpl(
								new GnucashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableVendorBillEntry createVendBllEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	
	GnucashWritableVendorBillEntryImpl entry = new GnucashWritableVendorBillEntryImpl(
								new GnucashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setVendBllTaxable(false);
	
	addBillEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableVendorBillEntry createVendBllEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createVendBllEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnucashFile().getTaxTableByName(taxTabName);
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableVendorBillEntry createVendBllEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableVendorBillEntryImpl entry = new GnucashWritableVendorBillEntryImpl(
								new GnucashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	
	GnucashWritableEmployeeVoucherEntryImpl entry = new GnucashWritableEmployeeVoucherEntryImpl(
								new GnucashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setEmplVchTaxable(false);
	
	addVoucherEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createEmplVchEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnucashFile().getTaxTableByName(taxTabName);
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableEmployeeVoucherEntryImpl entry = new GnucashWritableEmployeeVoucherEntryImpl(
								new GnucashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {
	
	GnucashWritableJobInvoiceEntryImpl entry = new GnucashWritableJobInvoiceEntryImpl(
								new GnucashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setJobInvcTaxable(false);
	
	addJobEntry(entry);
	return entry;
    }
    
    /**
     * create and add a new entry.<br/>
     * The entry will use the accounts of the SKR03.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createJobInvcEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getGnucashFile().getTaxTableByName(taxTabName);
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws UnknownInvoiceTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableJobInvoiceEntryImpl entry = new GnucashWritableJobInvoiceEntryImpl(
								new GnucashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
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
     * {@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createCustomerInvoice_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashCustomer cust,
	    final boolean postInvoice,
	    final GnucashAccountImpl incomeAcct,
	    final GnucashAccountImpl receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {

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
     * {@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createVendorBill_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashVendor vend,
	    final boolean postInvoice,
	    final GnucashAccountImpl expensesAcct,
	    final GnucashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {

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
     * {@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createEmployeeVoucher_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashEmployee empl,
	    final boolean postInvoice,
	    final GnucashAccountImpl expensesAcct,
	    final GnucashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {

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
     * {@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und Leistungen"
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncGncInvoice createJobInvoice_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashGenerJob job,
	    final boolean postInvoice,
	    final GnucashAccountImpl incExpAcct,
	    final GnucashAccountImpl recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {

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
	    final GnucashWritableFile file,
	    GnucashWritableCustomerInvoice invc,
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct, 
	    final GnucashAccount receivableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postCustomerInvoice: Posting customer invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getCustInvcAmountWithTaxes();
	LOGGER.debug("postCustomerInvoice: Customer invoice amount: " + amount);
	
	GCshID postTrxID = postCustomerInvoice_int((GnucashWritableFileImpl) file, fact, 
		                                   getJwsdpPeer(), 
		                                   invc.getID(), invc.getNumber(), 
		                                   cust,
		                                   (GnucashAccountImpl) incomeAcct, 
		                                   (GnucashAccountImpl) receivableAcct,
		                                   amount,
		                                   postDate, dueDate);
	LOGGER.info("postCustomerInvoice: Customer invoice " + invc.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postVendorBill(
	    final GnucashWritableFile file,
	    GnucashWritableVendorBill bll,
	    final GnucashVendor vend,
	    final GnucashAccount expensesAcct, 
	    final GnucashAccount payableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postVendorBill: Posting vendor bill " + bll.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = bll.getVendBllAmountWithTaxes();
	LOGGER.debug("postVendorBill: Vendor bill amount: " + amount);
	
	GCshID postTrxID = postVendorBill_int((GnucashWritableFileImpl) file, fact, 
		                              getJwsdpPeer(), 
		                              bll.getID(), bll.getNumber(), 
		                              vend,
		                              (GnucashAccountImpl) expensesAcct, 
		                              (GnucashAccountImpl) payableAcct, 
		                              amount,
		                              postDate, dueDate);
	LOGGER.info("postVendorBill: Vendor bill " + bll.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postEmployeeVoucher(
	    final GnucashWritableFile file,
	    GnucashWritableEmployeeVoucher vch,
	    final GnucashEmployee empl,
	    final GnucashAccount expensesAcct, 
	    final GnucashAccount payableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postEmployeeVoucher: Posting employee voucher " + vch.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = vch.getVendBllAmountWithTaxes();
	LOGGER.debug("postVendorBill: Vendor bill amount: " + amount);
	
	GCshID postTrxID = postEmployeeVoucher_int((GnucashWritableFileImpl) file, fact, 
  		                              	   getJwsdpPeer(), 
  		                              	   vch.getID(), vch.getNumber(),
  		                              	   empl,
  		                              	   (GnucashAccountImpl) expensesAcct,
  		                              	   (GnucashAccountImpl) payableAcct,
  		                              	   amount,
  		                              	   postDate, dueDate);
	LOGGER.info("postEmployeeVoucher: Employee voucher " + vch.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    public void postJobInvoice(
	    final GnucashWritableFile file,
	    GnucashWritableJobInvoice invc,
	    final GnucashGenerJob job,
	    final GnucashAccount incomeAcct, 
	    final GnucashAccount receivableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	LOGGER.debug("postJobInvoice: Posting job invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getJobInvcAmountWithTaxes();
	LOGGER.debug("postJobInvoice: Job invoice amount: " + amount);
	
	GCshID postTrxID = postJobInvoice_int((GnucashWritableFileImpl) file, fact, 
		           	              getJwsdpPeer(), 
		           	              invc.getID(), invc.getNumber(), 
		           	              job,
		                              (GnucashAccountImpl) incomeAcct, 
		                              (GnucashAccountImpl) receivableAcct, 
		                              amount,
		                              postDate, dueDate);
	LOGGER.info("postJobInvoice: Job invoice " + invc.getID() + " posted with Tranaction ID " + postTrxID);
    }
    
    // ----------------------------

    private static GCshID postCustomerInvoice_int(
	    final GnucashWritableFileImpl file,
	    ObjectFactory fact, 
	    GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnucashCustomer cust,
	    final GnucashAccountImpl incomeAcct, 
	    final GnucashAccountImpl receivableAcct,
	    final FixedPointNumber amount,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
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
	String acctLotID = "(unset)";
	{
	    GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
	    postLotRef.setType(Const.XML_DATA_TYPE_GUID);

	    GncAccount.ActLots.GncLot newLot = createInvcPostLot_Customer(file, fact, 
		    					invcGUID, invcNumber, 
		                                        receivableAcct, cust);
	    
	    acctLotID = newLot.getLotId().getValue();
	    postLotRef.setValue(acctLotID);
	    
	    invcRef.setInvoicePostlot(postLotRef);
	}
	
	// post transaction
	GCshID postTrxID = null;
	{
	    GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
	    postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
	    
	    
	    GnucashWritableTransaction postTrx = createPostTransaction(file, fact, 
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
	    final GnucashWritableFileImpl file, 
            ObjectFactory fact, 
            GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnucashVendor vend,
            final GnucashAccountImpl expensesAcct, 
            final GnucashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
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
	String acctLotID = "(unset)";
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createBillPostLot_Vendor(file, fact, 
        	    					invcGUID, invcNumber,
        	    					payableAcct, vend);
    
	    acctLotID = newLot.getLotId().getValue();
            postLotRef.setValue(acctLotID);
            invcRef.setInvoicePostlot(postLotRef);
        }
    
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnucashWritableTransaction postTrx = createPostTransaction(file, fact, 
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
	    final GnucashWritableFileImpl file, 
            ObjectFactory fact, 
            GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnucashEmployee empl,
            final GnucashAccountImpl expensesAcct, 
            final GnucashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
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
	String acctLotID = "(unset)";
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createVoucherPostLot_Employee(file, fact, 
        	    					invcGUID, invcNumber,
        	    					payableAcct, empl);
    
	    acctLotID = newLot.getLotId().getValue();
            postLotRef.setValue(acctLotID);
            invcRef.setInvoicePostlot(postLotRef);
        }
    
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnucashWritableTransaction postTrx = createPostTransaction(file, fact, 
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
	    final GnucashWritableFileImpl file,
            ObjectFactory fact, 
            GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnucashGenerJob job,
            final GnucashAccountImpl incExpAcct, 
            final GnucashAccountImpl recvblPayblAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
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
	String acctLotID = "(unset)";
        {
            GncGncInvoice.InvoicePostlot postLotRef = fact.createGncGncInvoiceInvoicePostlot();
            postLotRef.setType(Const.XML_DATA_TYPE_GUID);
    
            GncAccount.ActLots.GncLot newLot = createInvcPostLot_Job(file, fact, 
        	    					invcGUID, invcNumber,
        	                                        recvblPayblAcct, job);
    
	    acctLotID = newLot.getLotId().getValue();
            postLotRef.setValue(acctLotID);
            invcRef.setInvoicePostlot(postLotRef);
        }
        
        // post transaction
        GCshID postTrxID = null;
        {
            GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncGncInvoiceInvoicePosttxn();
            postTrxRef.setType(Const.XML_DATA_TYPE_GUID);
            
            GnucashWritableTransaction postTrx = createPostTransaction(file, fact, 
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
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws IllegalTransactionSplitActionException 
     * @see #GnucashWritableInvoiceImpl(GnucashWritableFileImpl, String, String,
     *      GnucashGenerJob, GnucashAccountImpl, Date)
     */
    private static GnucashWritableTransaction createPostTransaction(
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final GCshOwner.Type invcOwnerType, 
	    final String invcNumber, 
	    final String descr,
	    final GnucashAccount fromAcct, // receivable/payable account
	    final GnucashAccount toAcct,   // income/expense account
	    final String acctLotID,
	    final FixedPointNumber amount,
	    final FixedPointNumber quantity,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	if ( invcOwnerType != GCshOwner.Type.CUSTOMER &&
	     invcOwnerType != GCshOwner.Type.VENDOR ) // sic, TYPE_JOB not allowed here
	    throw new WrongOwnerTypeException();
	
	GnucashWritableTransaction postTrx = file.createWritableTransaction();
	postTrx.setDatePosted(postDate);
	postTrx.setNumber(invcNumber);
	postTrx.setDescription(descr);

	GnucashWritableTransactionSplit split1 = postTrx.createWritableSplit(fromAcct);
	split1.setValue(amount.negate());
	split1.setQuantity(quantity.negate());
	if ( invcOwnerType == GCshOwner.Type.CUSTOMER )
	    split1.setAction(GnucashTransactionSplit.Action.INVOICE);
	else if ( invcOwnerType == GCshOwner.Type.VENDOR )
	    split1.setAction(GnucashTransactionSplit.Action.BILL);
	else if ( invcOwnerType == GCshOwner.Type.EMPLOYEE )
	    split1.setAction(GnucashTransactionSplit.Action.VOUCHER);
	    
	GnucashWritableTransactionSplit split2 = postTrx.createWritableSplit(toAcct);
	split2.setValue(amount);
	split2.setQuantity(quantity);
	if ( invcOwnerType == GCshOwner.Type.CUSTOMER )
	    split2.setAction(GnucashTransactionSplit.Action.INVOICE);
	else if ( invcOwnerType == GCshOwner.Type.VENDOR )
	    split2.setAction(GnucashTransactionSplit.Action.BILL);
	else if ( invcOwnerType == GCshOwner.Type.EMPLOYEE )
	    split2.setAction(GnucashTransactionSplit.Action.VOUCHER);
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
	    value.getContent().add(GnucashTransaction.Type.INVOICE.getCode());

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
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnucashAccountImpl postAcct,
	    final GnucashCustomer cust) throws IllegalArgumentException {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber, 
		                       postAcct, 
                                       GCshOwner.Type.CUSTOMER, GCshOwner.Type.CUSTOMER, // second one is dummy
                                       cust.getID());
    }

    private static GncAccount.ActLots.GncLot createBillPostLot_Vendor(
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnucashAccountImpl postAcct,
	    final GnucashVendor vend) throws IllegalArgumentException {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
		                       GCshOwner.Type.VENDOR, GCshOwner.Type.VENDOR, // second one is dummy
		                       vend.getID());
    }

    private static GncAccount.ActLots.GncLot createVoucherPostLot_Employee(
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnucashAccountImpl postAcct,
	    final GnucashEmployee empl) throws IllegalArgumentException {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
		                       GCshOwner.Type.EMPLOYEE, GCshOwner.Type.EMPLOYEE, // second one is dummy
		                       empl.getID());
    }

    private static GncAccount.ActLots.GncLot createInvcPostLot_Job(
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID,
	    final String invcNumber,
	    final GnucashAccountImpl postAcct,
	    final GnucashGenerJob job) throws IllegalArgumentException {
	return createInvcPostLot_Gener(file, factory, 
		                       invcID, invcNumber,
		                       postAcct, 
                                       GCshOwner.Type.JOB, job.getOwnerType(), // second one is NOT dummy
                                       job.getID());
    }
    
    // ----------------------------

    private static GncAccount.ActLots.GncLot createInvcPostLot_Gener(
	    final GnucashWritableFileImpl file,
	    final ObjectFactory factory, 
	    final GCshID invcID, 
	    final String invcNumber,
	    final GnucashAccountImpl postAcct,
	    final GCshOwner.Type ownerType1, // of invoice (direct)
	    final GCshOwner.Type ownerType2, // of invoice's owner (indirect, only relevant if ownerType1 is JOB)
	    final GCshID ownerID) throws IllegalArgumentException {

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
		contentStr = GnucashTransactionSplit.Action.INVOICE.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.VENDOR ) {
		contentStr = GnucashTransactionSplit.Action.BILL.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.EMPLOYEE ) {
		contentStr = GnucashTransactionSplit.Action.VOUCHER.getLocaleString();
	    } else if ( ownerType1 == GCshOwner.Type.JOB ) {
		if ( ownerType2 == GCshOwner.Type.CUSTOMER ) {
		    contentStr = GnucashTransactionSplit.Action.INVOICE.getLocaleString();
    		} else if ( ownerType2 == GCshOwner.Type.VENDOR ) {
		    contentStr = GnucashTransactionSplit.Action.BILL.getLocaleString();
    		} else if ( ownerType2 == GCshOwner.Type.EMPLOYEE ) {
		    contentStr = GnucashTransactionSplit.Action.VOUCHER.getLocaleString();
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
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeInvcEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This customer invoice has payments and is not modifiable!");
	}

	this.subtractInvcEntry(impl);
	entries.remove(impl);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeBillEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This vendor bill has payments and is not modifiable!");
	}

	this.subtractBillEntry(impl);
	entries.remove(impl);
    }
    
    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeVoucherEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This employee voucher has payments and is not modifiable!");
	}

	this.subtractVoucherEntry(impl);
	entries.remove(impl);
    }
    
    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeJobEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	if (!isModifiable()) {
	    throw new IllegalStateException("This job invoice has payments and is not modifiable!");
	}

	this.subtractJobEntry(impl);
	entries.remove(impl);
    }
    
    // ---------------------------------------------------------------

    /**
     * Called by
     * {@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     */
    public void addRawGenerEntry(final GnucashWritableGenerInvoiceEntryImpl generEntr)
	    throws WrongInvoiceTypeException {
//	System.err.println("GnucashWritableGenerInvoiceImpl.addRawGenerEntry " + generEntr.toString());

	if (!isModifiable()) {
	    throw new IllegalArgumentException("This invoice/bill has payments and thus is not modifiable");
	}

	super.addGenerEntry(generEntr);
    }

    /**
     * Called by
     * {@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public void addInvcEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addInvcEntry " + generInvcEntr.toString());

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
		getGnucashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		LOGGER.error("addInvcEntry: The given customer invoice entry has no i-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public void addBillEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addBillEntry " + generInvcEntr.toString());

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
		getGnucashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + "')");
		LOGGER.error("addBillEntry: The given vendor bill entry has no b-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public void addVoucherEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.EMPLOYEE )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addVoucherEntry " + generInvcEntr.toString());

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
		getGnucashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (entry ID: " + generInvcEntr.getID() + ")";
		LOGGER.error("addVoucherEntry: The given employee voucher entry has no b-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    /**
     * Called by
     * {@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     */
    public void addJobEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addJobEntry " + generInvcEntr.toString());

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
		getGnucashFile().setModified(true);
	    } catch ( TaxTableNotFoundException exc ) {
		// throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (entry ID: " + generInvcEntr.getID() + ")");
		LOGGER.error("addJobEntry: The given job invoice entry has no b/i-tax-table (entry ID: " + generInvcEntr.getID()  + ")");
	    }
	}
    }

    // ---------------------------------------------------------------

    protected void subtractInvcEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractInvcEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isCustInvcTaxable();
	FixedPointNumber sumExclTaxes = entry.getCustInvcSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getCustInvcSumInclTaxes().negate();
	
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
	getGnucashFile().setModified(true);
    }

    protected void subtractBillEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractBillEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isVendBllTaxable();
	FixedPointNumber sumExclTaxes = entry.getVendBllSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getVendBllSumInclTaxes().negate();
	
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
	getGnucashFile().setModified(true);
    }

    protected void subtractVoucherEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractVoucherEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isEmplVchTaxable();
	FixedPointNumber sumExclTaxes = entry.getEmplVchSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getEmplVchSumInclTaxes().negate();
	
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
	getGnucashFile().setModified(true);
    }

    protected void subtractJobEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractJobEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isJobInvcTaxable();
	FixedPointNumber sumExclTaxes = entry.getJobInvcSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getJobInvcSumInclTaxes().negate();
	
	GCshID postAcctID = new GCshID();
	if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
	    postAcctID.set(entry.getJwsdpPeer().getEntryIAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
	    postAcctID.set(entry.getJwsdpPeer().getEntryBAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.EMPLOYEE )
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
	getGnucashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     */
    protected GCshID getCustInvcPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_CUSTOMER &&
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return new GCshID(entry.getJwsdpPeer().getEntryIAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     */
    protected GCshID getVendBllPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_VENDOR &&
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	return new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     */
    protected GCshID getEmplVchPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_EMPLOYEE &&
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	return new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     */
    protected GCshID getJobInvcPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getType() == GnucashGenerJob.TYPE_CUSTOMER )
		return getCustInvcPostAccountID(entry);
	    else if ( jobInvc.getType() == GnucashGenerJob.TYPE_VENDOR )
		return getVendBllPostAccountID(entry);
	    
	    return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     */
    private void updateEntry(
	    final GCshTaxTable taxTab, 
	    final boolean isTaxable, 
	    final FixedPointNumber sumExclTaxes,
	    final FixedPointNumber sumInclTaxes, 
	    final GCshID postAcctID)
	    throws InvalidCmdtyCurrTypeException {
	LOGGER.debug("GnucashWritableGenerInvoiceImpl.updateEntry " 
		+ "isTaxable=" + isTaxable + " "
		+ "post-acct=" + postAcctID + " ");

	GnucashWritableTransactionImpl postTrx = (GnucashWritableTransactionImpl) getPostTransaction();
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
	getGnucashFile().setModified(true);
    }

    private void updateEntry_taxStuff(
	    final GCshTaxTable taxtable, 
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID postAcctID, 
	    GnucashWritableTransactionImpl postTrx) throws InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException {
	// get the first account of the taxTable
	GCshTaxTableEntry taxTableEntry = taxtable.getEntries().iterator().next();
	GnucashAccount taxAcct = taxTableEntry.getAccount();
	FixedPointNumber entryTaxAmount = ((FixedPointNumber) sumInclTaxes.clone()).subtract(sumExclTaxes);

	LOGGER.debug("GnucashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
	    + "post-acct=" + postAcctID + " " 
	    + "tax-acct=" + taxAcct.getQualifiedName() + " "
	    + "entryTaxAmount=" + entryTaxAmount + " "
	    + "#splits=" + postTrx.getSplits().size());

	// failed for subtractEntry assert entryTaxAmount.isPositive() ||
	// entryTaxAmount.equals(new FixedPointNumber());

	boolean postTransactionTaxUpdated = false;
	for (GnucashTransactionSplit element : postTrx.getSplits()) {
	GnucashWritableTransactionSplitImpl split = (GnucashWritableTransactionSplitImpl) element;
	if (split.getAccountID().equals(taxAcct.getID())) {

	    // quantity gets updated automagically
	    // split.setQuantity(split.getQuantity().subtract(entryTaxAmount));
	    split.setValue(split.getValue().subtract(entryTaxAmount));

	    // failed for subtractEntry assert !split.getValue().isPositive();
	    // failed for subtractEntry assert !split.getQuantity().isPositive();

	    LOGGER.info("GnucashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		    + "updated tax-split=" + split.getID() + " " 
		    + "of account " + split.getAccount().getQualifiedName() + " " 
		    + "to value " + split.getValue());

	    postTransactionTaxUpdated = true;
	    break;
	}
	LOGGER.debug("GnucashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		+ "ignoring non-tax-split=" + split.getID() + " " 
		+ "of value " + split.getValue() + " "
		+ "and account " + split.getAccount().getQualifiedName());
	}
	
	if (!postTransactionTaxUpdated) {
	GnucashWritableTransactionSplitImpl split = 
		(GnucashWritableTransactionSplitImpl) postTrx
			.createWritableSplit(taxAcct);
	split.setQuantity(((FixedPointNumber) entryTaxAmount.clone()).negate());
	split.setValue(((FixedPointNumber) entryTaxAmount.clone()).negate());

	// assert !split.getValue().isPositive();
	// assert !split.getQuantity().isPositive();

	split.setAction(GnucashTransactionSplit.Action.INVOICE);

	LOGGER.info("GnucashWritableGenerInvoiceImpl.updateEntry_taxStuff " 
		+ "created new tax-split=" + split.getID() + " " 
		+ "of value " + split.getValue() + " "
		+ "and account " + split.getAccount().getQualifiedName());
	}
    }

    /**
     * @param sumExclTaxes
     * @param sumInclTaxes
     * @param accountToTransferMoneyFrom
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     */
    private void updateNonTaxableEntry(
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID accountToTransferMoneyFrom) throws InvalidCmdtyCurrTypeException {

//	System.err.println("GnucashWritableGenerInvoiceImpl.updateNonTaxableEntry " 
//		+ "accountToTransferMoneyFrom=" + accountToTransferMoneyFrom);

	GnucashWritableTransactionImpl postTransaction = (GnucashWritableTransactionImpl) getPostTransaction();
	if (postTransaction == null) {
	    return; // invoice may not be posted
	}

	// ==============================================================
	// update transaction-split that transferes the sum incl. taxes from the
	// incomeAccount
	// (e.g. "Umsatzerloese 19%")
	GCshID accountToTransferMoneyTo = getPostAccountID();
	boolean postTransactionSumUpdated = false;

	LOGGER.debug("GnucashWritableGenerInvoiceImpl.updateNonTaxableEntry #splits=" + postTransaction.getSplits().size());

	for (Object element : postTransaction.getSplits()) {
	    GnucashWritableTransactionSplitImpl split = (GnucashWritableTransactionSplitImpl) element;
	    if (split.getAccountID().equals(accountToTransferMoneyTo)) {

		FixedPointNumber value = split.getValue();
		split.setQuantity(split.getQuantity().add(sumInclTaxes));
		split.setValue(value.add(sumInclTaxes));
		postTransactionSumUpdated = true;

		LOGGER.info("GnucashWritableGenerInvoiceImpl.updateNonTaxableEntry updated split " + split.getID());
		break;
	    }
	}

	if (!postTransactionSumUpdated) {
	    GnucashWritableTransactionSplitImpl split = 
		    (GnucashWritableTransactionSplitImpl) postTransaction
		    	.createWritableSplit(getGnucashFile().getAccountByID(accountToTransferMoneyTo));
	    split.setQuantity(sumInclTaxes);
	    split.setValue(sumInclTaxes);
	    split.setAction(GnucashTransactionSplit.Action.INVOICE);

	    // this split must have a reference to the lot
	    // as has the transaction-split of the whole sum in the
	    // transaction when the invoice is Paid
	    GncTransaction.TrnSplits.TrnSplit.SplitLot lotref = 
		    ((GnucashFileImpl) getGnucashFile()).getObjectFactory()
		    	.createGncTransactionTrnSplitsTrnSplitSplitLot();
	    lotref.setType(getJwsdpPeer().getInvoicePostlot().getType());
	    lotref.setValue(getJwsdpPeer().getInvoicePostlot().getValue());
	    split.getJwsdpPeer().setSplitLot(lotref);

	    LOGGER.info("GnucashWritableGenerInvoiceImpl.updateNonTaxableEntry created split " + split.getID());
	}

	// ==============================================================
	// update transaction-split that transferes the sum incl. taxes to the
	// postAccount
	// (e.g. "Forderungen aus Lieferungen und Leistungen")

	boolean postTransactionNetSumUpdated = false;
	for (GnucashTransactionSplit element : postTransaction.getSplits()) {
	    GnucashWritableTransactionSplitImpl split = (GnucashWritableTransactionSplitImpl) element;
	    if (split.getAccountID().equals(accountToTransferMoneyFrom)) {

		FixedPointNumber value = split.getValue();
		split.setQuantity(split.getQuantity().subtract(sumExclTaxes));
		split.setValue(value.subtract(sumExclTaxes));
		split.getJwsdpPeer().setSplitAction(GnucashTransactionSplit.Action.INVOICE.getLocaleString());
		postTransactionNetSumUpdated = true;
		break;
	    }
	}
	
	if (!postTransactionNetSumUpdated) {
	    GnucashWritableTransactionSplitImpl split = 
		    new GnucashWritableTransactionSplitImpl(
			    postTransaction,
			    getGnucashFile().getAccountByID(accountToTransferMoneyFrom));
	    split.setQuantity(((FixedPointNumber) sumExclTaxes.clone()).negate());
	    split.setValue(((FixedPointNumber) sumExclTaxes.clone()).negate());
	}

	assert postTransaction.isBalanced();
	getGnucashFile().setModified(true);
    }

    /**
     * @see GnucashWritableGenerInvoice#isModifiable()
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

    // ::TODO
//	void setOwnerID(String ownerID) {
//	    GCshOwner owner = new GCshOwner(GCshOwner.JIType.INVOICE, ownerID);
//	    getJwsdpPeer().setInvoiceOwner(new GCShOwner(xxx));
//	}

    public void setOwner(GCshOwner owner) throws WrongOwnerJITypeException {
	if (owner.getJIType() != GCshOwner.JIType.INVOICE)
	    throw new WrongOwnerJITypeException();

	getJwsdpPeer().setInvoiceOwner(owner.getInvcOwner());
    }

    // ------------------------

    /**
     * @throws WrongInvoiceTypeException
     */
    public void setCustomer(final GnucashCustomer cust) throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.CUSTOMER )
	    throw new WrongInvoiceTypeException();

	attemptChange();
	getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(cust.getID().toString());
	getGnucashFile().setModified(true);
    }

    /**
     * @throws WrongInvoiceTypeException
     */
    public void setVendor(final GnucashVendor vend) throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.VENDOR )
	    throw new WrongInvoiceTypeException();

	attemptChange();
	getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(vend.getID().toString());
	getGnucashFile().setModified(true);
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashWritableGenerInvoice#setGenerJob(GnucashGenerJob)
     */
    public void setGenerJob(final GnucashGenerJob job) throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	attemptChange();
	getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(job.getID().toString());
	getGnucashFile().setModified(true);
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
	getGnucashFile().setModified(true);
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
	getGnucashFile().setModified(true);
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
	getGnucashFile().setModified(true);

	// change the date of the transaction too
	GnucashWritableTransaction postTr = getWritablePostTransaction();
	if (postTr != null) {
	    postTr.setDatePosted(d);
	}
    }

    /**
     * @see GnucashWritableGenerInvoice#setDatePosted(java.lang.String)
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
	getGnucashFile().setModified(true);
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
	getGnucashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @return 
     * @throws IllegalArgumentException 
     *  
     * @see GnucashGenerInvoice#getPayingTransactions()
     */
    public Collection<GnucashWritableTransaction> getWritablePayingTransactions() throws IllegalArgumentException {
	Collection<GnucashWritableTransaction> trxList = new ArrayList<GnucashWritableTransaction>();

	for (GnucashTransaction trx : getPayingTransactions()) {
	    GnucashWritableTransaction newTrx = new GnucashWritableTransactionImpl(trx);
	    trxList.add(newTrx);
	}

	return trxList;
    }

    /**
     * @return get a modifiable version of
     *         {@link GnucashGenerInvoiceImpl#getPostTransaction()}
     */
    public GnucashWritableTransaction getWritablePostTransaction() {
	GncGncInvoice.InvoicePosttxn invoicePosttxn = jwsdpPeer.getInvoicePosttxn();
	if (invoicePosttxn == null) {
	    return null; // invoice may not be posted
	}
	
	GCshID invcPostTrxID = new GCshID( invoicePosttxn.getValue() );
	return getGnucashFile().getWritableTransactionByID(invcPostTrxID);
    }

    /**
     * @see #getGenerEntryByID(GCshID)
     */
    public GnucashWritableGenerInvoiceEntry getWritableGenerEntryByID(final GCshID id) {
	return new GnucashWritableGenerInvoiceEntryImpl(super.getGenerEntryByID(id));
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalArgumentException 
     *  
     * @see GnucashWritableGenerInvoice#remove()
     */
    public void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException {

	if (!isModifiable()) {
	    throw new IllegalStateException("Invoice has payments and cannot be deleted!");
	}

	// we copy the list because element.remove() modifies it
	Collection<GnucashGenerInvoiceEntry> entries2 = new ArrayList<GnucashGenerInvoiceEntry>();
	entries2.addAll(this.getGenerEntries());
	for (GnucashGenerInvoiceEntry element : entries2) {
	    ((GnucashWritableGenerInvoiceEntry) element).remove();
	}

	GnucashWritableTransaction post = (GnucashWritableTransaction) getPostTransaction();
	if (post != null) {
	    post.remove();
	}

	((GnucashWritableFileImpl) getGnucashFile()).removeGenerInvoice(this);

    }

    @Override
    public void setEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException {
	// TODO Auto-generated method stub
	
    }
    
    // ---------------------------------------------------------------

    @Override
	public void addUserDefinedAttribute(final String type, final String name, final String value) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			ObjectFactory fact = getGnucashFile().getObjectFactory();
			SlotsType newSlotsType = fact.createSlotsType();
			jwsdpPeer.setInvoiceSlots(newSlotsType);
		}
		
		HasWritableUserDefinedAttributesImpl
			.addUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 getWritableGnucashFile(),
										 type, name, value);
	}

    @Override
	public void removeUserDefinedAttribute(final String name) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.removeUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 	getWritableGnucashFile(),
										 	name);
	}

    @Override
	public void setUserDefinedAttribute(final String name, final String value) {
		if ( jwsdpPeer.getInvoiceSlots() == null ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		HasWritableUserDefinedAttributesImpl
			.setUserDefinedAttributeCore(jwsdpPeer.getInvoiceSlots(),
										 getWritableGnucashFile(),
										 name, value);
	}

	public void clean() {
		HasWritableUserDefinedAttributesImpl.cleanSlots(jwsdpPeer.getInvoiceSlots());
	}

    // ---------------------------------------------------------------

    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashWritableGenerInvoiceImpl [");

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
