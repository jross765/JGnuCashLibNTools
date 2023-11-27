package org.gnucash.read.impl;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.gnucash.Const;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.GncV2.GncBook.GncGncEntry.EntryBTaxtable;
import org.gnucash.generated.GncV2.GncBook.GncGncEntry.EntryBill;
import org.gnucash.generated.GncV2.GncBook.GncGncEntry.EntryITaxtable;
import org.gnucash.generated.GncV2.GncBook.GncGncEntry.EntryInvoice;
import org.gnucash.generated.ObjectFactory;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerInvoiceEntry;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.IllegalTransactionSplitActionException;
import org.gnucash.read.TaxTableNotFoundException;
import org.gnucash.read.aux.GCshOwner;
import org.gnucash.read.aux.GCshTaxTable;
import org.gnucash.read.aux.GCshTaxTableEntry;
import org.gnucash.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.read.spec.GnucashJobInvoice;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.gnucash.write.impl.UnknownInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of GnucashInvoiceEntry that uses JWSDP.
 */
public class GnucashGenerInvoiceEntryImpl extends GnucashObjectImpl 
                                          implements GnucashGenerInvoiceEntry 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashGenerInvoiceEntryImpl.class);

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_BOOK = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Format of the JWSDP-Field for the entry-date.
     */
    protected static final DateFormat ENTRY_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    // ------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncV2.GncBook.GncGncEntry jwsdpPeer;

    // ------------------------------

    /**
     * @see GnucashGenerInvoice#getDateOpened()
     */
    protected ZonedDateTime date;

    /**
     * The taxtable in the gnucash xml-file. It defines what sales-tax-rates are
     * known.
     */
    private GCshTaxTable myInvcTaxtable;
    private GCshTaxTable myBillTaxtable;

    // ----------------------------

    /**
     * @see #getDateOpenedFormatted()
     * @see #getDatePostedFormatted()
     */
    private DateFormat dateFormat = null;

    /**
     * The numberFormat to use for non-currency-numbers for default-formating.<br/>
     * Please access only using {@link #getNumberFormat()}.
     *
     * @see #getNumberFormat()
     */
    private NumberFormat numberFormat = null;

    /**
     * The numberFormat to use for percentFormat-numbers for default-formating.<br/>
     * Please access only using {@link #getPercentFormat()}.
     *
     * @see #getPercentFormat()
     */
    private NumberFormat percentFormat = null;

    // ---------------------------------------------------------------

    /**
     * This constructor is used when an invoice is created by java-code.
     *
     * @param invc The invoice we belong to.
     * @param peer    the JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GnucashGenerInvoiceEntryImpl(
	    final GnucashGenerInvoice invc, 
	    final GncV2.GncBook.GncGncEntry peer,
	    final boolean addEntrToInvc) {
	super((peer.getEntrySlots() == null) ? new ObjectFactory().createSlotsType() : peer.getEntrySlots(),
		invc.getFile());

	if (peer.getEntrySlots() == null) {
	    peer.setEntrySlots(getSlots());
	}

	this.myInvoice = invc;
	this.jwsdpPeer = peer;

	if ( addEntrToInvc ) {
	    if (invc != null) {
		invc.addGenerEntry(this);
	    }
	}
    }

    /**
     * This code is used when an invoice is loaded from a file.
     *
     * @param gncFile tne file we belong to
     * @param peer    the JWSDP-object we are facading.
     * @see #jwsdpPeer
     */
    @SuppressWarnings("exports")
    public GnucashGenerInvoiceEntryImpl(
	    final GncV2.GncBook.GncGncEntry peer, 
	    final GnucashFileImpl gncFile) {
	super((peer.getEntrySlots() == null) ? new ObjectFactory().createSlotsType() : peer.getEntrySlots(), gncFile);

	if (peer.getEntrySlots() == null) {
	    peer.setEntrySlots(getSlots());
	}

	this.jwsdpPeer = peer;

	// an exception is thrown here if we have an invoice-ID but the invoice does not
	// exist
	GnucashGenerInvoice invc = getGenerInvoice();
	if (invc != null) {
	    // ...so we only need to handle the case of having no invoice-id at all
	    invc.addGenerEntry(this);
	}
    }

    // Copy-constructor
    public GnucashGenerInvoiceEntryImpl(final GnucashGenerInvoiceEntry entry) {
	super(entry.getGenerInvoice().getFile());

	if (entry.getJwsdpPeer().getEntrySlots() == null) {
	    setSlots(new ObjectFactory().createSlotsType());
	} else {
	    setSlots(entry.getJwsdpPeer().getEntrySlots());
	}

	this.myInvoice = entry.getGenerInvoice();
	this.jwsdpPeer = entry.getJwsdpPeer();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshID getId() {
	return new GCshID( jwsdpPeer.getEntryGuid().getValue() );
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    public GCshOwner.Type getType() throws WrongInvoiceTypeException {
	return getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.DIRECT);
    }

    /**
     * MAY RETURN NULL. {@inheritDoc}
     */
    public GCshID getGenerInvoiceID() {
	EntryInvoice entrInvc = null;
	EntryBill entrBill = null;

	try {
	    entrInvc = jwsdpPeer.getEntryInvoice();
	} catch (Exception exc) {
	    // ::EMPTY
	}

	try {
	    entrBill = jwsdpPeer.getEntryBill();
	} catch (Exception exc) {
	    // ::EMPTY
	}

	if (entrInvc == null && entrBill == null) {
	    LOGGER.error("file contains an invoice-entry with GUID=" + getId()
		    + " without an invoice-element (customer) AND " + "without a bill-element (vendor)");
	    return null;
	} else if (entrInvc != null && entrBill == null) {
	    return new GCshID(entrInvc.getValue());
	} else if (entrInvc == null && entrBill != null) {
	    return new GCshID(entrBill.getValue());
	} else if (entrInvc != null && entrBill != null) {
	    LOGGER.error("file contains an invoice-entry with GUID=" + getId()
		    + " with BOTH an invoice-element (customer) and " + "a bill-element (vendor)");
	    return null;
	}

	return null;
    }

    /**
     * The invoice this entry is from.
     */
    protected GnucashGenerInvoice myInvoice;

    /**
     * {@inheritDoc}
     */
    public GnucashGenerInvoice getGenerInvoice() {
	if (myInvoice == null) {
	    GCshID invcId = getGenerInvoiceID();
	    if (invcId != null) {
		myInvoice = getGnucashFile().getGenerInvoiceByID(invcId);
		if (myInvoice == null) {
		    throw new IllegalStateException("No generic invoice with ID '" + getGenerInvoiceID()
			    + "' for invoice entry with ID '" + getId() + "'");
		}
	    }
	}
	return myInvoice;
    }

    // ---------------------------------------------------------------

    /**
     * @param aTaxtable the taxtable to set
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected void setInvcTaxTable(final GCshTaxTable aTaxtable)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	myInvcTaxtable = aTaxtable;
    }

    /**
     * @param aTaxtable the taxtable to set
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected void setBillTaxTable(final GCshTaxTable aTaxtable)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	myBillTaxtable = aTaxtable;
    }

    /**
     * @param aTaxtable the taxtable to set
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws NumberFormatException 
     * @throws IllegalTransactionSplitActionException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected void setVoucherTaxTable(final GCshTaxTable aTaxtable)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, IllegalTransactionSplitActionException, NumberFormatException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	myBillTaxtable = aTaxtable;
    }

    protected void setJobTaxTable(final GCshTaxTable aTaxtable)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	
	if ( getType() == GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    setInvcTaxTable(aTaxtable);
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    setBillTaxTable(aTaxtable);
    }

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    @Override
    public GCshTaxTable getInvcTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.CUSTOMER && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	if (myInvcTaxtable == null) {
	    EntryITaxtable taxTableEntry = jwsdpPeer.getEntryITaxtable();
	    if (taxTableEntry == null) {
		throw new TaxTableNotFoundException();
	    }

	    String taxTableIdStr = taxTableEntry.getValue();
	    if (taxTableIdStr == null) {
		LOGGER.error(
			"getInvcTaxTable: Customer invoice with id '" + getId() + 
			"' is i-taxable but has empty id for the i-taxtable");
		return null;
	    }
	    GCshID taxTableId = new GCshID( taxTableIdStr );
	    myInvcTaxtable = getGnucashFile().getTaxTableByID(taxTableId);

	    if (myInvcTaxtable == null) {
		LOGGER.error("getInvcTaxTable: Customer invoice with id '" + getId() + 
			"' is i-taxable but has an unknown "
			+ "i-taxtable-id '" + taxTableId + "'!");
	    }
	} // myInvcTaxtable == null

	return myInvcTaxtable;
    }

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    @Override
    public GCshTaxTable getBillTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.VENDOR && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	if (myBillTaxtable == null) {
	    EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
	    if (taxTableEntry == null) {
		throw new TaxTableNotFoundException();
	    }

	    String taxTableIdStr = taxTableEntry.getValue();
	    if (taxTableIdStr == null) {
		LOGGER.error("getBillTaxTable: Vendor bill with id '" + getId() + 
			"' is b-taxable but has empty id for the b-taxtable");
		return null;
	    }
	    GCshID taxTableId = new GCshID( taxTableIdStr );
	    myBillTaxtable = getGnucashFile().getTaxTableByID(taxTableId);

	    if (myBillTaxtable == null) {
		LOGGER.error("getBillTaxTable: Vendor bill with id '" + getId() + 
			"' is b-taxable but has an unknown "
			+ "b-taxtable-id '" + taxTableId + "'!");
	    }
	} // myBillTaxtable == null

	return myBillTaxtable;
    }

    /**
     * @return The taxt able in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     * @throws WrongInvoiceTypeException
     */
    @Override
    public GCshTaxTable getVoucherTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.EMPLOYEE && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	if (myBillTaxtable == null) {
	    EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
	    if (taxTableEntry == null) {
		throw new TaxTableNotFoundException();
	    }

	    String taxTableIdStr = taxTableEntry.getValue();
	    if (taxTableIdStr == null) {
		LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getId() + 
			"' is b-taxable but has empty id for the b-taxtable");
		return null;
	    }
	    GCshID taxTableId = new GCshID( taxTableIdStr );
	    myBillTaxtable = getGnucashFile().getTaxTableByID(taxTableId);

	    if (myBillTaxtable == null) {
		LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getId() + 
			"' is b-taxable but has an unknown "
			+ "b-taxtable-id '" + taxTableId + "'!");
	    }
	} // myBillTaxtable == null

	return myBillTaxtable;
    }

    @Override
    public GCshTaxTable getJobTaxTable() throws TaxTableNotFoundException, WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcTaxTable();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillTaxTable();

	return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getInvcApplicableTaxPercent() throws WrongInvoiceTypeException {

	if ( getType() != GnucashGenerInvoice.TYPE_CUSTOMER && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	if (!isInvcTaxable()) {
	    return new FixedPointNumber();
	}

	if (jwsdpPeer.getEntryITaxtable() != null) {
	    if (!jwsdpPeer.getEntryITaxtable().getType().equals(Const.XML_DATA_TYPE_GUID)) {
		LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
			"' has i-taxtable with type='"
			+ jwsdpPeer.getEntryITaxtable().getType() + "' != 'guid'");
	    }
	}

	GCshTaxTable taxTab = null;
	try {
	    taxTab = getInvcTaxTable();
	} catch (TaxTableNotFoundException exc) {
	    LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
		    "' is taxable but JWSDP peer has no i-taxtable-entry! " + 
		    "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	// ::TODO ::CHECK
	// Overly specific code / pseudo-improvement
	// Reasons: 
	// - We should not correct data errors -- why only check here?
	//   There are hundreds of instances where we could check...
	// - For this lib, the data in the GnuCash file is the truth. 
	//   If it happens to be wrong, then it should be corrected, period.
	// - This is not the way GnuCash works. It never just takes a 
	//   tax rate directly, but rather goes via a tax table (entry).
	// - Assuming standard German VAT is overly specific.
//	if (taxTab == null) {
//	    LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
//		    "' is taxable but has an unknown i-taxtable! "
//		    + "Assuming 19%");
//	    return new FixedPointNumber("1900000/10000000");
//	}
	// Instead:
	if (taxTab == null) {
	    LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
		    "' is taxable but has an unknown i-taxtable! "
		    + "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	GCshTaxTableEntry taxTabEntr = taxTab.getEntries().iterator().next();
	// ::TODO ::CHECK
	// Overly specific code / pseudo-improvement
	// Reasons: 
	// - We should not correct data errors -- why only check here?
	//   There are hundreds of instances where we could check...
	// - Assuming standard German VAT is overly specific.
//	if ( ! taxTabEntr.getType().equals(GCshTaxTableEntry.TYPE_PERCENT) ) {
//	    LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
//		    "' is taxable but has a i-taxtable "
//		    + "that is not in percent but in '" + taxTabEntr.getType() + "'! Assuming 19%");
//	    return new FixedPointNumber("1900000/10000000");
//	}
	// Instead:
	// ::TODO
	if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
	    LOGGER.error("getInvcApplicableTaxPercent: Customer invoice entry with id '" + getId() + 
		    "' is taxable but has a i-taxtable of type '" + taxTabEntr.getType() + "'! " + 
		    "NOT IMPLEMENTED YET " +
	            "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	FixedPointNumber val = taxTabEntr.getAmount();

	// the file contains, say, 19 for 19%, we need to convert it to 0,19.
	return ((FixedPointNumber) val.clone()).divideBy(new FixedPointNumber("100"));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getBillApplicableTaxPercent() throws WrongInvoiceTypeException {

	if ( getType() != GnucashGenerInvoice.TYPE_VENDOR && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	if (!isBillTaxable()) {
	    return new FixedPointNumber();
	}

	if (jwsdpPeer.getEntryBTaxtable() != null) {
	    if (!jwsdpPeer.getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID)) {
		LOGGER.error("getBillApplicableTaxPercent: Vendor bill entry with id '" + getId() + "' has b-taxtable with type='"
			+ jwsdpPeer.getEntryBTaxtable().getType() + "' != 'guid'");
	    }
	}

	GCshTaxTable taxTab = null;
	try {
	    taxTab = getBillTaxTable();
	} catch (TaxTableNotFoundException exc) {
	    LOGGER.error("getBillApplicableTaxPercent: Vendor bill entry with id '" + getId() +
		    "' is taxable but JWSDP peer has no b-taxtable-entry! " + 
		    "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	// Cf. getInvcApplicableTaxPercent()
	if (taxTab == null) {
	    LOGGER.error("getBillApplicableTaxPercent: Vendor bill entry with id '" + getId() + 
		    "' is taxable but has an unknown b-taxtable! "
		    + "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	GCshTaxTableEntry taxTabEntr = taxTab.getEntries().iterator().next();
	// ::TODO ::CHECK
	// Cf. getInvcApplicableTaxPercent()
	// ::TODO
	if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
	    LOGGER.error("getBillApplicableTaxPercent: Vendor bill entry with id '" + getId() + 
		    "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "'! " + 
		    "NOT IMPLEMENTED YET " +
	            "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	FixedPointNumber val = taxTabEntr.getAmount();

	// the file contains, say, 19 for 19%, we need to convert it to 0,19.
	return ((FixedPointNumber) val.clone()).divideBy(new FixedPointNumber("100"));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getVoucherApplicableTaxPercent() throws WrongInvoiceTypeException {

	if ( getType() != GnucashGenerInvoice.TYPE_EMPLOYEE && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	if (!isVoucherTaxable()) {
	    return new FixedPointNumber();
	}

	if (jwsdpPeer.getEntryBTaxtable() != null) {
	    if (!jwsdpPeer.getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID)) {
		LOGGER.error("getVoucherApplicableTaxPercent: Employee voucher entry with id '" + getId() + "' has b-taxtable with type='"
			+ jwsdpPeer.getEntryBTaxtable().getType() + "' != 'guid'");
	    }
	}

	GCshTaxTable taxTab = null;
	try {
	    taxTab = getVoucherTaxTable();
	} catch (TaxTableNotFoundException exc) {
	    LOGGER.error("getVoucherApplicableTaxPercent: Employee voucher entry with id '" + getId() +
		    "' is taxable but JWSDP peer has no b-taxtable-entry! " + 
		    "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	// Cf. getInvcApplicableTaxPercent()
	if (taxTab == null) {
	    LOGGER.error("getVoucherApplicableTaxPercent: Employee voucher entry with id '" + getId() + 
		    "' is taxable but has an unknown b-taxtable! "
		    + "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	GCshTaxTableEntry taxTabEntr = taxTab.getEntries().iterator().next();
	// ::TODO ::CHECK
	// Cf. getInvcApplicableTaxPercent()
	// ::TODO
	if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
	    LOGGER.error("getVoucherApplicableTaxPercent: Employee voucher entry with id '" + getId() + 
		    "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "'! " + 
		    "NOT IMPLEMENTED YET " +
	            "Assuming 0%");
	    return new FixedPointNumber("0");
	}

	FixedPointNumber val = taxTabEntr.getAmount();

	// the file contains, say, 19 for 19%, we need to convert it to 0,19.
	return ((FixedPointNumber) val.clone()).divideBy(new FixedPointNumber("100"));

    }

    /**
     * {@inheritDoc}
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public FixedPointNumber getJobApplicableTaxPercent() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcApplicableTaxPercent();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillApplicableTaxPercent();

	return null; // Compiler happy
    }

    // ----------------------------

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getInvcApplicableTaxPercentFormatted() throws WrongInvoiceTypeException {
	FixedPointNumber applTaxPerc = getInvcApplicableTaxPercent();
	if (applTaxPerc == null) {
	    return this.getPercentFormat().format(0);
	}
	return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getBillApplicableTaxPercentFormatted() throws WrongInvoiceTypeException {
	FixedPointNumber applTaxPerc = getBillApplicableTaxPercent();
	if (applTaxPerc == null) {
	    return this.getPercentFormat().format(0);
	}
	return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getVoucherApplicableTaxPercentFormatted() throws WrongInvoiceTypeException {
	FixedPointNumber applTaxPerc = getVoucherApplicableTaxPercent();
	if (applTaxPerc == null) {
	    return this.getPercentFormat().format(0);
	}
	return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobApplicableTaxPercentFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	FixedPointNumber applTaxPerc = getJobApplicableTaxPercent();
	if (applTaxPerc == null) {
	    return this.getPercentFormat().format(0);
	}
	return this.getPercentFormat().format(applTaxPerc);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashGenerInvoiceEntry#getInvcPrice()
     */
    @Override
    public FixedPointNumber getInvcPrice() throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.CUSTOMER && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	return new FixedPointNumber(jwsdpPeer.getEntryIPrice());
    }

    /**
     * @see GnucashGenerInvoiceEntry#getInvcPrice()
     */
    @Override
    public FixedPointNumber getBillPrice() throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.VENDOR && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	return new FixedPointNumber(jwsdpPeer.getEntryBPrice());
    }

    /**
     * @see GnucashGenerInvoiceEntry#getInvcPrice()
     */
    @Override
    public FixedPointNumber getVoucherPrice() throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.EMPLOYEE && 
	     getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	return new FixedPointNumber(jwsdpPeer.getEntryBPrice());
    }

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getInvcPrice()
     */
    @Override
    public FixedPointNumber getJobPrice() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcPrice();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillPrice();

	return null; // Compiler happy
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String getInvcPriceFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getInvcPrice());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getBillPriceFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getBillPrice());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVoucherPriceFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVoucherPrice());
    }

    /**
     * {@inheritDoc}
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobPriceFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobPrice());
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public FixedPointNumber getInvcSum() throws WrongInvoiceTypeException {
	return getInvcPrice().multiply(getQuantity());
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getInvcSumInclTaxes() throws WrongInvoiceTypeException {
	if (jwsdpPeer.getEntryITaxincluded() == 1) {
	    return getInvcSum();
	}

	return getInvcSum().multiply(getInvcApplicableTaxPercent().add(1));
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getInvcSumExclTaxes() throws WrongInvoiceTypeException {

	// System.err.println("debug: GnucashInvoiceEntryImpl.getSumExclTaxes():"
	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
	// getApplicableTaxPercent()="+getApplicableTaxPercent());

	if (jwsdpPeer.getEntryITaxincluded() == 0) {
	    return getInvcSum();
	}

	return getInvcSum().divideBy(getInvcApplicableTaxPercent().add(1));
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public String getInvcSumFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getInvcSum());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getInvcSumInclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getInvcSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getInvcSumExclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getInvcSumExclTaxes());
    }

    // ----------------------------

    /**
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public FixedPointNumber getBillSum() throws WrongInvoiceTypeException {
	return getBillPrice().multiply(getQuantity());
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getBillSumInclTaxes() throws WrongInvoiceTypeException {
	if (jwsdpPeer.getEntryBTaxincluded() == 1) {
	    return getBillSum();
	}

	return getBillSum().multiply(getBillApplicableTaxPercent().add(1));
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getBillSumExclTaxes() throws WrongInvoiceTypeException {

	// System.err.println("debug: GnucashInvoiceEntryImpl.getSumExclTaxes():"
	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
	// getApplicableTaxPercent()="+getApplicableTaxPercent());

	if (jwsdpPeer.getEntryBTaxincluded() == 0) {
	    return getBillSum();
	}

	return getBillSum().divideBy(getBillApplicableTaxPercent().add(1));
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public String getBillSumFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getBillSum());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getBillSumInclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getBillSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getBillSumExclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getBillSumExclTaxes());
    }

    // ----------------------------

    /**
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public FixedPointNumber getVoucherSum() throws WrongInvoiceTypeException {
	return getVoucherPrice().multiply(getQuantity());
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getVoucherSumInclTaxes() throws WrongInvoiceTypeException {
	if (jwsdpPeer.getEntryBTaxincluded() == 1) {
	    return getBillSum();
	}

	return getVoucherSum().multiply(getVoucherApplicableTaxPercent().add(1));
    }

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getVoucherSumExclTaxes() throws WrongInvoiceTypeException {

	// System.err.println("debug: GnucashInvoiceEntryImpl.getSumExclTaxes():"
	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
	// getApplicableTaxPercent()="+getApplicableTaxPercent());

	if (jwsdpPeer.getEntryBTaxincluded() == 0) {
	    return getVoucherSum();
	}

	return getVoucherSum().divideBy(getVoucherApplicableTaxPercent().add(1));
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public String getVoucherSumFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVoucherSum());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getVoucherSumInclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVoucherSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     */
    @Override
    public String getVoucherSumExclTaxesFormatted() throws WrongInvoiceTypeException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVoucherSumExclTaxes());
    }

    // ----------------------------

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public FixedPointNumber getJobSum() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcSum();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillSum();

	return null; // Compiler happy
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getJobSumInclTaxes() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcSumInclTaxes();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillSumInclTaxes();

	return null; // Compiler happy
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getJobSumExclTaxes() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return getInvcSumExclTaxes();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return getBillSumExclTaxes();

	return null; // Compiler happy
    }

    // ----------------------------

    /**
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getInvcSum()
     */
    @Override
    public String getJobSumFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobSum());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobSumInclTaxesFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     * 
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public String getJobSumExclTaxesFormatted() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return ((GnucashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobSumExclTaxes());
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInvcTaxable() throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_CUSTOMER && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return (jwsdpPeer.getEntryITaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBillTaxable() throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_VENDOR && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVoucherTaxable() throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_EMPLOYEE && 
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    @Override
    public boolean isJobTaxable() throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(getGenerInvoice());
	if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_CUSTOMER )
	    return isInvcTaxable();
	else if ( jobInvc.getJobType() == GnucashGenerJob.TYPE_VENDOR )
	    return isBillTaxable();

	return false; // Compiler happy
    }

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoiceEntry#getAction()
     */
    public Action getAction() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	return Action.valueOff( jwsdpPeer.getEntryAction() );
    }

    /**
     * @see GnucashGenerInvoiceEntry#getQuantity()
     */
    public FixedPointNumber getQuantity() {
	String val = getJwsdpPeer().getEntryQty();
	return new FixedPointNumber(val);
    }

    /**
     * {@inheritDoc}
     */
    public String getQuantityFormatted() {
	return getNumberFormat().format(getQuantity());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public ZonedDateTime getDate() {
	if (date == null) {
	    String dateStr = getJwsdpPeer().getEntryDate().getTsDate();
	    try {
		// "2001-09-18 00:00:00 +0200"
		date = ZonedDateTime.parse(dateStr, DATE_FORMAT);
	    } catch (Exception e) {
		IllegalStateException ex = new IllegalStateException("unparsable date '" + dateStr + "' in invoice!");
		ex.initCause(e);
		throw ex;
	    }

	}
	return date;
    }

	/**
	 * @see #getDateOpenedFormatted()
	 * @see #getDatePostedFormatted()
	 * @return the Dateformat to use.
	 */
	protected DateFormat getDateFormat() {
		if ( dateFormat == null ) {
		    if ( ((GnucashGenerInvoiceImpl) getGenerInvoice()).getDateFormat() != null ) {
			dateFormat = ((GnucashGenerInvoiceImpl) getGenerInvoice()).getDateFormat();
		    }
		    else {
			dateFormat = DateFormat.getDateInstance();
		    }
		}

		return dateFormat;
	}

    /**
     * {@inheritDoc}
     */
    public String getDateFormatted() {
	return getDateFormat().format(getDate());
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
	if (getJwsdpPeer().getEntryDescription() == null) {
	    return "";
	}

	return getJwsdpPeer().getEntryDescription();
    }

    /**
     * @return the number-format to use for non-currency-numbers if no locale is
     *         given.
     */
    protected NumberFormat getNumberFormat() {
	if (numberFormat == null) {
	    numberFormat = NumberFormat.getInstance();
	}

	return numberFormat;
    }

    /**
     * @return the number-format to use for percentage-numbers if no locale is
     *         given.
     */
    protected NumberFormat getPercentFormat() {
	if (percentFormat == null) {
	    percentFormat = NumberFormat.getPercentInstance();
	}

	return percentFormat;
    }

    // ---------------------------------------------------------------

    /**
     * @return The JWSDP-Object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncEntry getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public int compareTo(final GnucashGenerInvoiceEntry otherEntr) {
	try {
	    GnucashGenerInvoice otherInvc = otherEntr.getGenerInvoice();
	    if (otherInvc != null && getGenerInvoice() != null) {
		int c = otherInvc.compareTo(getGenerInvoice());
		if (c != 0) {
		    return c;
		}
	    }

	    int c = otherEntr.getId().toString().compareTo(getId().toString());
	    if (c != 0) {
		return c;
	    }

	    if (otherEntr != this) {
		LOGGER.error("Duplicate invoice-entry-id!! " + otherEntr.getId() + " and " + getId());
	    }

	    return 0;

	} catch (Exception e) {
	    LOGGER.error("error comparing", e);
	    return 0;
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("[GnucashGenerInvoiceEntryImpl:");
	buffer.append(" id: ");
	buffer.append(getId());
	buffer.append(" type: ");
	try {
	    buffer.append(getType());
	} catch (WrongInvoiceTypeException e) {
	    buffer.append("ERROR");
	}
	buffer.append(" cust/vend-invoice-id: ");
	buffer.append(getGenerInvoiceID());
//      //      buffer.append(" cust/vend-invoice: ");
//      //      GnucashGenerInvoice invc = getGenerInvoice();
//      //      buffer.append(invoice==null?"null":invc.getName());
	buffer.append(" description: '");
	buffer.append(getDescription() + "'");
	buffer.append(" date: ");
	try {
	    buffer.append(getDate().toLocalDate().format(DATE_FORMAT_PRINT));
	}
	catch (Exception e) {
	    buffer.append(getDate().toLocalDate().toString());
	}
	buffer.append(" action: '");
	try {
	    buffer.append(getAction() + "'");
	} catch (Exception e) {
	    buffer.append("ERROR" + "'");
	}
	buffer.append(" price: ");
	try {
	    if ( getType() == GCshOwner.Type.CUSTOMER ) {
		buffer.append(getInvcPrice());
	    } else if ( getType() == GCshOwner.Type.VENDOR ) {
		buffer.append(getBillPrice());
	    } else if ( getType() == GCshOwner.Type.EMPLOYEE ) {
		buffer.append(getVoucherPrice());
	    } else if ( getType() == GCshOwner.Type.JOB ) {
		try {
		    buffer.append(getJobPrice());
		} catch (Exception e2) {
		    buffer.append("ERROR");
		}
	    } else
		buffer.append("ERROR");
	} catch (WrongInvoiceTypeException e) {
	    buffer.append("ERROR");
	}
	buffer.append(" quantity: ");
	buffer.append(getQuantity());
	buffer.append("]");
	return buffer.toString();
    }

}
