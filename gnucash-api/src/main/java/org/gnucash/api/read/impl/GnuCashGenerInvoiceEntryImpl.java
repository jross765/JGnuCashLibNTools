package org.gnucash.api.read.impl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.generated.GncGncEntry.EntryBAcct;
import org.gnucash.api.generated.GncGncEntry.EntryBTaxtable;
import org.gnucash.api.generated.GncGncEntry.EntryBill;
import org.gnucash.api.generated.GncGncEntry.EntryIAcct;
import org.gnucash.api.generated.GncGncEntry.EntryITaxtable;
import org.gnucash.api.generated.GncGncEntry.EntryInvoice;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoiceEntry;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.TaxTableNotFoundException;
import org.gnucash.api.read.UnknownInvoiceTypeException;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.aux.GCshTaxTableEntry;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.spec.GnuCashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.beanbase.IllegalTransactionSplitActionException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Implementation of GnuCashInvoiceEntry that uses JWSDP.
 */
public class GnuCashGenerInvoiceEntryImpl extends GnuCashObjectImpl 
                                          implements GnuCashGenerInvoiceEntry 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashGenerInvoiceEntryImpl.class);

    protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_BOOK = DateTimeFormatter.ofPattern(Const.STANDARD_DATE_FORMAT);
    protected static final DateTimeFormatter DATE_FORMAT_PRINT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Format of the JWSDP-Field for the entry-date.
     */
    protected static final DateFormat ENTRY_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncEntry jwsdpPeer;

    // ------------------------------

    /**
     * @see GnuCashGenerInvoice#getDateOpened()
     */
    protected ZonedDateTime date;

    private GCshAcctID myInvcAcctID;
    private GCshAcctID myBllAcctID;

    /**
     * The tax table in the GnuCash XML file. It defines what sales-tax-rates are
     * known.
     */
    private GCshTaxTable myInvcTaxTable;
    private GCshTaxTable myBillTaxTable;

    // ----------------------------

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
     * @param addEntrToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashGenerInvoiceEntryImpl(
	    final GnuCashGenerInvoice invc, 
	    final GncGncEntry peer,
	    final boolean addEntrToInvc) {
    	super(invc.getGnuCashFile());

//		if (peer.getEntrySlots() == null) {
//	    	peer.setEntrySlots(getJwsdpPeer().getEntrySlots());
//		}

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
     * @param gcshFile the file we belong to
     * @param peer    the JWSDP-object we are facading.
     * @param addEntrToInvc 
     */
    @SuppressWarnings("exports")
    public GnuCashGenerInvoiceEntryImpl(
	    final GncGncEntry peer, 
	    final GnuCashFileImpl gcshFile,
	    final boolean addEntrToInvc) {
    	super(gcshFile);

//		if (peer.getEntrySlots() == null) {
//	    	peer.setEntrySlots(getJwsdpPeer().getEntrySlots());
//		}

    	this.jwsdpPeer = peer;

    	if ( addEntrToInvc ) {
    		// an exception is thrown here if we have an invoice-ID but the invoice does not
    		// exist
    		GnuCashGenerInvoice invc = getGenerInvoice();
    		if (invc != null) {
    			// ...so we only need to handle the case of having no invoice-id at all
    			invc.addGenerEntry(this);
    		}
    	}
    }

    // Copy-constructor
    public GnuCashGenerInvoiceEntryImpl(final GnuCashGenerInvoiceEntry entry) {
    	super(entry.getGenerInvoice().getGnuCashFile());

//		if (entry.getJwsdpPeer().getEntrySlots() == null) {
//			HasUserDefinedAttributesImpl
//				.setSlotsInit(getJwsdpPeer().getEntrySlots(), 
//							  new ObjectFactory().createSlotsType());
//		} else {
//			HasUserDefinedAttributesImpl
//				.setSlotsInit(getJwsdpPeer().getEntrySlots(), 
//							  entry.getJwsdpPeer().getEntrySlots());
//		}

    	this.myInvoice = entry.getGenerInvoice();
    	this.jwsdpPeer = entry.getJwsdpPeer();
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshGenerInvcEntrID getID() {
    	return new GCshGenerInvcEntrID( jwsdpPeer.getEntryGuid().getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshOwner.Type getType() {
    	return getGenerInvoice().getOwnerType(GnuCashGenerInvoice.ReadVariant.DIRECT);
    }

    /**
     * MAY RETURN NULL. {@inheritDoc}
     */
    public GCshGenerInvcID getGenerInvoiceID() {
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

		if ( entrInvc == null && entrBill == null ) {
			LOGGER.error("file contains an invoice-entry with GUID=" + getID()
					+ " without an invoice-element (customer) AND " + "without a bill-element (vendor)");
			return null;
		} else if ( entrInvc != null && entrBill == null ) {
			return new GCshGenerInvcID(entrInvc.getValue());
		} else if ( entrInvc == null && entrBill != null ) {
			return new GCshGenerInvcID(entrBill.getValue());
		} else if ( entrInvc != null && entrBill != null ) {
			LOGGER.error("file contains an invoice-entry with GUID=" + getID()
					+ " with BOTH an invoice-element (customer) and " + "a bill-element (vendor)");
			return null;
		}

		return null;
    }

    /**
     * The invoice this entry is from.
     */
    protected GnuCashGenerInvoice myInvoice;

    /**
     * {@inheritDoc}
     */
    public GnuCashGenerInvoice getGenerInvoice() {
		if ( myInvoice == null ) {
			GCshGenerInvcID invcId = getGenerInvoiceID();
			if ( invcId != null ) {
				myInvoice = getGnuCashFile().getGenerInvoiceByID(invcId);
				if ( myInvoice == null ) {
					throw new IllegalStateException("No generic invoice with ID '" + getGenerInvoiceID()
							+ "' for invoice entry with ID '" + getID() + "'");
				}
			}
		}
		return myInvoice;
    }

    // ---------------------------------------------------------------

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setCustInvcTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.CUSTOMER && 
      		 getType() != GCshOwner.Type.JOB )
       		    throw new WrongInvoiceTypeException();

    	myInvcTaxTable = aTaxtable;
    }

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setVendBllTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.VENDOR && 
   		     getType() != GCshOwner.Type.JOB )
    		    throw new WrongInvoiceTypeException();

    	myBillTaxTable = aTaxtable;
    }

    /**
     * @param aTaxtable the tax table to set
     * @throws TaxTableNotFoundException
     * @throws IllegalTransactionSplitActionException 
     */
    protected void setEmplVchTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, IllegalTransactionSplitActionException {
    	if ( getType() != GCshOwner.Type.EMPLOYEE )
    		    throw new WrongInvoiceTypeException();

    	myBillTaxTable = aTaxtable;
    }

    protected void setJobInvcTaxTable(final GCshTaxTable aTaxtable)
	    throws TaxTableNotFoundException, UnknownInvoiceTypeException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			setCustInvcTaxTable(aTaxtable);
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			setVendBllTaxTable(aTaxtable);
    	}

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getCustInvcTaxTable() throws TaxTableNotFoundException {
    	return getCustInvcTaxTable_int();
    }
    
    private GCshTaxTable getCustInvcTaxTable_int() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myInvcTaxTable == null ) {
			EntryITaxtable taxTableEntry = jwsdpPeer.getEntryITaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getInvcTaxTable: Customer invoice with id '" + getID()
						+ "' is i-taxable but has empty id for the i-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myInvcTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myInvcTaxTable == null ) {
				LOGGER.error("getInvcTaxTable: Customer invoice with id '" + getID()
						+ "' is i-taxable but has an unknown " + "i-taxtable-id '" + taxTableId + "'");
			}
		} // myInvcTaxtable == null

		return myInvcTaxTable;
    }

    /**
     * @return The tax table in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getVendBllTaxTable() throws TaxTableNotFoundException {
    	return getVendBllTaxTable_int();
    }
    
    private GCshTaxTable getVendBllTaxTable_int() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myBillTaxTable == null ) {
			EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getBillTaxTable: Vendor bill with id '" + getID()
						+ "' is b-taxable but has empty id for the b-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myBillTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myBillTaxTable == null ) {
				LOGGER.error("getBillTaxTable: Vendor bill with id '" + getID() + "' is b-taxable but has an unknown "
						+ "b-taxtable-id '" + taxTableId + "'");
			}
		} // myBillTaxtable == null

		return myBillTaxTable;
    }

    /**
     * @return The taxt able in the GnuCash XML file. It defines what sales-tax-rates
     *         are known.
     * @throws TaxTableNotFoundException
     */
    @Override
    public GCshTaxTable getEmplVchTaxTable() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE )
			throw new WrongInvoiceTypeException();

		if ( myBillTaxTable == null ) {
			EntryBTaxtable taxTableEntry = jwsdpPeer.getEntryBTaxtable();
			if ( taxTableEntry == null ) {
				throw new TaxTableNotFoundException();
			}

			String taxTableIdStr = taxTableEntry.getValue();
			if ( taxTableIdStr == null ) {
				LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getID()
						+ "' is b-taxable but has empty id for the b-taxtable");
				return null;
			}
			GCshTaxTabID taxTableId = new GCshTaxTabID(taxTableIdStr);
			myBillTaxTable = getGnuCashFile().getTaxTableByID(taxTableId);

			if ( myBillTaxTable == null ) {
				LOGGER.error("getVoucherTaxTable: Employee voucher with id '" + getID()
						+ "' is b-taxable but has an unknown " + "b-taxtable-id '" + taxTableId + "'");
			}
		} // myBillTaxtable == null

		return myBillTaxTable;
    }

    @Override
    public GCshTaxTable getJobInvcTaxTable() throws TaxTableNotFoundException {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcTaxTable();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllTaxTable();

		return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getCustInvcApplicableTaxPercent() {
    	return getCustInvcApplicableTaxPercent_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private FixedPointNumber getCustInvcApplicableTaxPercent_int() {
		if ( getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
			 getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		if ( !isCustInvcTaxable_int() ) {
			return new FixedPointNumber();
		}

		if ( jwsdpPeer.getEntryITaxtable() != null ) {
			if ( !jwsdpPeer.getEntryITaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + getID()
						+ "' has i-taxtable with type='" + jwsdpPeer.getEntryITaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = getCustInvcTaxTable_int();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + getID()
					+ "' is taxable but JWSDP peer has no i-taxtable-entry! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		// ::CHECK: Still necessary?
		if ( taxTab == null ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + getID()
					+ "' is taxable but has an unknown i-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getCustInvcApplicableTaxPercent: Customer invoice entry with id '" + getID()
					+ "' is taxable but has a i-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getVendBllApplicableTaxPercent() {
    	return getVendBllApplicableTaxPercent_int();
    }
    
    // Important separate it from public variant for
    // getJobInvcApplicableTaxPercent().
    // Else, we would have funny effects.
    private FixedPointNumber getVendBllApplicableTaxPercent_int() {
		if ( getType() != GnuCashGenerInvoice.TYPE_VENDOR && 
			 getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		if ( !isVendBllTaxable_int() ) {
			return new FixedPointNumber();
		}

		if ( jwsdpPeer.getEntryBTaxtable() != null ) {
			if ( !jwsdpPeer.getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + getID()
						+ "' has b-taxtable with type='" + jwsdpPeer.getEntryBTaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = getVendBllTaxTable_int();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + getID()
					+ "' is taxable but JWSDP peer has no b-taxtable-entry! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		// Cf. getInvcApplicableTaxPercent()
		if ( taxTab == null ) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + getID()
					+ "' is taxable but has an unknown b-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getVendBllApplicableTaxPercent: Vendor bill entry with id '" + getID()
					+ "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FixedPointNumber getEmplVchApplicableTaxPercent() {
		if ( getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE )
			throw new WrongInvoiceTypeException();

		if ( !isEmplVchTaxable() ) {
			return new FixedPointNumber();
		}

		if ( jwsdpPeer.getEntryBTaxtable() != null ) {
			if ( !jwsdpPeer.getEntryBTaxtable().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
				LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + getID()
						+ "' has b-taxtable with type='" + jwsdpPeer.getEntryBTaxtable().getType() + "' != 'guid'");
			}
		}

		GCshTaxTable taxTab = null;
		try {
			taxTab = getEmplVchTaxTable();
		} catch (TaxTableNotFoundException exc) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + getID()
					+ "' is taxable but JWSDP peer has no b-taxtable-entry! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		// Cf. getInvcApplicableTaxPercent()
		if ( taxTab == null ) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + getID()
					+ "' is taxable but has an unknown b-taxtable! " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		GCshTaxTableEntry taxTabEntr = taxTab.getEntries().get(0);
		if ( taxTabEntr.getType() == GCshTaxTableEntry.Type.VALUE ) {
			LOGGER.error("getEmplVchApplicableTaxPercent: Employee voucher entry with id '" + getID()
					+ "' is taxable but has a b-taxtable of type '" + taxTabEntr.getType() + "' "
					+ "NOT IMPLEMENTED YET " + "Assuming 0%");
			return new FixedPointNumber("0");
		}

		FixedPointNumber val = taxTabEntr.getAmount();

		// the file contains, say, 19 for 19%, we need to convert it to 0,19.
		return val.copy().divide(new FixedPointNumber("100"));
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public FixedPointNumber getJobInvcApplicableTaxPercent() {
		if ( getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcApplicableTaxPercent_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllApplicableTaxPercent_int();

		return null; // Compiler happy
    }

    // ----------------------------

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getCustInvcApplicableTaxPercentFormatted() {
    	return getCustInvcApplicableTaxPercentFormatted_int();
    }
    
    private String getCustInvcApplicableTaxPercentFormatted_int() {
		FixedPointNumber applTaxPerc = getCustInvcApplicableTaxPercent_int();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getVendBllApplicableTaxPercentFormatted() {
    	return getVendBllApplicableTaxPercentFormatted_int();
    }
    
    private String getVendBllApplicableTaxPercentFormatted_int() {
		FixedPointNumber applTaxPerc = getVendBllApplicableTaxPercent_int();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getEmplVchApplicableTaxPercentFormatted() {
		FixedPointNumber applTaxPerc = getEmplVchApplicableTaxPercent();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    /**
     * @return never null, "0%" if no taxtable is there
     */
    @Override
    public String getJobInvcApplicableTaxPercentFormatted() {
		FixedPointNumber applTaxPerc = getJobInvcApplicableTaxPercent();
		if ( applTaxPerc == null ) {
			return this.getPercentFormat().format(0);
		}
		return this.getPercentFormat().format(applTaxPerc);
    }

    // ---------------------------------------------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getCustInvcPrice() {
    	return getCustInvcPrice_int();
    }
    
    private FixedPointNumber getCustInvcPrice_int() {
    	if ( getType() != GCshOwner.Type.CUSTOMER && 
    		 getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(jwsdpPeer.getEntryIPrice());
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getVendBllPrice() {
    	return getVendBllPrice_int();
    }
    
    private FixedPointNumber getVendBllPrice_int() {
    	if ( getType() != GCshOwner.Type.VENDOR && 
    		 getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(jwsdpPeer.getEntryBPrice());
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getEmplVchPrice() {
    	if ( getType() != GCshOwner.Type.EMPLOYEE )
    		throw new WrongInvoiceTypeException();

    	return new FixedPointNumber(jwsdpPeer.getEntryBPrice());
    }

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcPrice()
     */
    @Override
    public FixedPointNumber getJobInvcPrice() {
    	if ( getType() != GCshOwner.Type.JOB )
    		throw new WrongInvoiceTypeException();

    	GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
    	if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
    		return getCustInvcPrice_int();
    	else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
    		return getVendBllPrice_int();

    	return null; // Compiler happy
    }

    // ----------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcPriceFormatted() {
    	return getCustInvcPriceFormatted_int();
    }
    
    private String getCustInvcPriceFormatted_int() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getCustInvcPrice_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllPriceFormatted() {
    	return getVendBllPriceFormatted_int();
    }
    
    private String getVendBllPriceFormatted_int() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVendBllPrice_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchPriceFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getEmplVchPrice());
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public String getJobInvcPriceFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobInvcPrice());
    }

    // ---------------------------------------------------------------
    
	@Override
	public GCshAcctID getCustInvcAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
			
		return getCustInvcAccountID_int();
	}
	
	private GCshAcctID getCustInvcAccountID_int() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.CUSTOMER && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myInvcAcctID == null ) {
			EntryIAcct jwsdpAcctID = jwsdpPeer.getEntryIAcct();
			if ( jwsdpAcctID == null ) {
				throw new AccountNotFoundException();
			}

			String acctIdStr = jwsdpAcctID.getValue();
			if ( acctIdStr == null ) {
				LOGGER.error("getCustInvcAccountID: Customer invoice entry with id '" + getID()
						+ "' has not i-account-id");
				return null;
			}
			
			myInvcAcctID = new GCshAcctID(acctIdStr);
		}

		return myInvcAcctID;
	}

	@Override
	public GCshAcctID getVendBllAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();
		
		return getVendBllAccountID_int();
	}
	
	private GCshAcctID getVendBllAccountID_int() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.VENDOR && 
			 getType() != GCshOwner.Type.EMPLOYEE && // sic
			 getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		if ( myBllAcctID == null ) {
			EntryBAcct jwsdpAcctID = jwsdpPeer.getEntryBAcct();
			if ( jwsdpAcctID == null ) {
				throw new AccountNotFoundException();
			}

			String acctIdStr = jwsdpAcctID.getValue();
			if ( acctIdStr == null ) {
				LOGGER.error("getVendBllAccountID: Vendor bill entry with id '" + getID() + "' has no b-account-id");
				return null;
			}

			myBllAcctID = new GCshAcctID(acctIdStr);
		}

		return myBllAcctID;
	}

	@Override
	public GCshAcctID getEmplVchAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.EMPLOYEE )
		    throw new WrongInvoiceTypeException();

		return getVendBllAccountID_int(); // sic
	}

	@Override
	public GCshAcctID getJobInvcAccountID() throws AccountNotFoundException {
		if ( getType() != GCshOwner.Type.JOB )
		    throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
		    return getCustInvcAccountID_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
		    return getVendBllAccountID_int();

		return null; // Compiler happy
	}

    // ---------------------------------------------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getCustInvcSum() {
    	return getCustInvcSum_int();
    }
    
    private FixedPointNumber getCustInvcSum_int() {
    	return getCustInvcPrice_int().multiply(getQuantity());
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getCustInvcSumInclTaxes() {
    	return getCustInvcSumInclTaxes_int();
    }
    
    private FixedPointNumber getCustInvcSumInclTaxes_int() {
    	if (jwsdpPeer.getEntryITaxincluded() == 1) {
    		return getCustInvcSum_int();
    	}

    	return getCustInvcSum_int().multiply(getCustInvcApplicableTaxPercent().add(BigDecimal.ONE));
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getCustInvcSumExclTaxes() {
    	return getCustInvcSumExclTaxes_int();
    }
    
    private FixedPointNumber getCustInvcSumExclTaxes_int() {

    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

    	if (jwsdpPeer.getEntryITaxincluded() == 0) {
    		return getCustInvcSum_int();
    	}

    	return getCustInvcSum_int().divide(getCustInvcApplicableTaxPercent().add(BigDecimal.ONE));
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getCustInvcSumFormatted() {
    	return getCustInvcSumFormatted_int();
    }
    
    private String getCustInvcSumFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getCustInvcSum_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcSumInclTaxesFormatted() {
    	return getCustInvcSumInclTaxesFormatted_int();
    }
    
    private String getCustInvcSumInclTaxesFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getCustInvcSumInclTaxes_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCustInvcSumExclTaxesFormatted() {
    	return getCustInvcSumExclTaxesFormatted_int();
    }
    
    public String getCustInvcSumExclTaxesFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getCustInvcSumExclTaxes_int());
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getVendBllSum() {
    	return getVendBllSum_int();
    }
    
    private FixedPointNumber getVendBllSum_int() {
    	return getVendBllPrice().multiply(getQuantity());
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getVendBllSumInclTaxes() {
    	return getVendBllSumInclTaxes_int();
    }
    
    private FixedPointNumber getVendBllSumInclTaxes_int() {
    	if (jwsdpPeer.getEntryBTaxincluded() == 1) {
    		return getVendBllSum_int();
    	}

    	return getVendBllSum_int().multiply(getVendBllApplicableTaxPercent().add(BigDecimal.ONE));
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getVendBllSumExclTaxes() {
    	return getVendBllSumExclTaxes_int();
    }
    
    private FixedPointNumber getVendBllSumExclTaxes_int() {

    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

    	if (jwsdpPeer.getEntryBTaxincluded() == 0) {
    		return getVendBllSum_int();
    	}

    	return getVendBllSum_int().divide(getVendBllApplicableTaxPercent().add(BigDecimal.ONE));
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getVendBllSumFormatted() {
    	return getVendBllSumFormatted_int();
    }
    
    private String getVendBllSumFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVendBllSum_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllSumInclTaxesFormatted() {
    	return getVendBllSumInclTaxesFormatted_int();
    }
    
    private String getVendBllSumInclTaxesFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVendBllSumInclTaxes_int());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendBllSumExclTaxesFormatted() {
    	return getVendBllSumExclTaxesFormatted_int();
    }
    
    private String getVendBllSumExclTaxesFormatted_int() {
		return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getVendBllSumExclTaxes_int());
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getEmplVchSum() {
    	return getEmplVchPrice().multiply(getQuantity());
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getEmplVchSumInclTaxes() {
		if ( jwsdpPeer.getEntryBTaxincluded() == 1 ) {
			return getVendBllSum();
		}

		return getEmplVchSum().multiply(getEmplVchApplicableTaxPercent().add(BigDecimal.ONE));
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getEmplVchSumExclTaxes() {

    	// System.err.println("debug: GnuCashInvoiceEntryImpl.getSumExclTaxes():"
    	// taxIncluded="+jwsdpPeer.getEntryITaxincluded()+" getSum()="+getSum()+"
    	// getApplicableTaxPercent()="+getApplicableTaxPercent());

		if ( jwsdpPeer.getEntryBTaxincluded() == 0 ) {
			return getEmplVchSum();
		}

		return getEmplVchSum().divide(getEmplVchApplicableTaxPercent().add(BigDecimal.ONE));
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getEmplVchSumFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getEmplVchSum());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchSumInclTaxesFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getEmplVchSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEmplVchSumExclTaxesFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getEmplVchSumExclTaxes());
    }

    // ----------------------------

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public FixedPointNumber getJobInvcSum() {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcSum_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllSum_int();

		return null; // Compiler happy
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumInclTaxes()
     */
    @Override
    public FixedPointNumber getJobInvcSumInclTaxes() {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcSumInclTaxes_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllSumInclTaxes_int();

		return null; // Compiler happy
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSumExclTaxes()
     */
    @Override
    public FixedPointNumber getJobInvcSumExclTaxes() {
		if ( getType() != GCshOwner.Type.JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return getCustInvcSumExclTaxes_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return getVendBllSumExclTaxes_int();

		return null; // Compiler happy
    }

    // ----------------------------

    /**
     * @see GnuCashGenerInvoiceEntry#getCustInvcSum()
     */
    @Override
    public String getJobInvcSumFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobInvcSum());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobInvcSumInclTaxesFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobInvcSumInclTaxes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJobInvcSumExclTaxesFormatted() {
    	return ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getCurrencyFormat().format(getJobInvcSumExclTaxes());
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCustInvcTaxable() {
    	return isCustInvcTaxable_int();
    }
    
    private boolean isCustInvcTaxable_int() {
    	if ( getType() != GnuCashGenerInvoice.TYPE_CUSTOMER && 
    		 getType() != GnuCashGenerInvoice.TYPE_JOB )
    		throw new WrongInvoiceTypeException();

    	return (jwsdpPeer.getEntryITaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVendBllTaxable() {
    	return isVendBllTaxable_int();
    }
    
    private boolean isVendBllTaxable_int() {
    	if ( getType() != GnuCashGenerInvoice.TYPE_VENDOR && 
    		 getType() != GnuCashGenerInvoice.TYPE_JOB )
    		throw new WrongInvoiceTypeException();

    	return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmplVchTaxable() {
		if ( getType() != GnuCashGenerInvoice.TYPE_EMPLOYEE )
			throw new WrongInvoiceTypeException();

		return (jwsdpPeer.getEntryBTaxable() == 1);
    }

    /**
     * {@inheritDoc}
     *  
     */
    @Override
    public boolean isJobInvcTaxable() {
		if ( getType() != GnuCashGenerInvoice.TYPE_JOB )
			throw new WrongInvoiceTypeException();

		GnuCashJobInvoice jobInvc = new GnuCashJobInvoiceImpl(getGenerInvoice());
		if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_CUSTOMER )
			return isCustInvcTaxable_int();
		else if ( jobInvc.getJobType() == GnuCashGenerJob.TYPE_VENDOR )
			return isVendBllTaxable_int();

		return false; // Compiler happy
    }

    /**
     *  
     * @see GnuCashGenerInvoiceEntry#getAction()
     */
    public Action getAction() {
    	return Action.valueOff( jwsdpPeer.getEntryAction() );
    }

    /**
     * @see GnuCashGenerInvoiceEntry#getQuantity()
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
		if ( date == null ) {
			String dateStr = getJwsdpPeer().getEntryDate().getTsDate();
			try {
				// "2001-09-18 00:00:00 +0200"
				date = ZonedDateTime.parse(dateStr, DATE_FORMAT);
			} catch (Exception e) {
				IllegalStateException ex = new IllegalStateException("unparsable date '" + dateStr + "' in invoice");
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
		    if ( ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getDateFormat() != null ) {
		    	dateFormat = ((GnuCashGenerInvoiceImpl) getGenerInvoice()).getDateFormat();
		    } else {
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
    public GncGncEntry getJwsdpPeer() {
    	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public int compareTo(final GnuCashGenerInvoiceEntry otherEntr) {
		try {
			GnuCashGenerInvoice otherInvc = otherEntr.getGenerInvoice();
			if ( otherInvc != null && getGenerInvoice() != null ) {
				int c = otherInvc.compareTo(getGenerInvoice());
				if ( c != 0 ) {
					return c;
				}
			}

			int c = otherEntr.getID().toString().compareTo(getID().toString());
			if ( c != 0 ) {
				return c;
			}

			if ( otherEntr != this ) {
				LOGGER.error("Duplicate invoice-entry-id!! " + otherEntr.getID() + " and " + getID());
			}

			return 0;

		} catch (Exception e) {
			LOGGER.error("error comparing", e);
			return 0;
		}
    }
    
    // ---------------------------------------------------------------
    
	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getEntrySlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getEntrySlots());
	}

    // ---------------------------------------------------------------
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("GnuCashGenerInvoiceEntryImpl [");

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

		buffer.append(", account-id=");
		try {
			if ( getType() == GCshOwner.Type.CUSTOMER ) {
				buffer.append(getCustInvcAccountID());
			} else if ( getType() == GCshOwner.Type.VENDOR ) {
				buffer.append(getVendBllAccountID());
			} else if ( getType() == GCshOwner.Type.EMPLOYEE ) {
				buffer.append(getEmplVchAccountID());
			} else if ( getType() == GCshOwner.Type.JOB ) {
				buffer.append(getJobInvcAccountID());
			} else {
				buffer.append("ERROR");
			}
		} catch (Exception e) {
			buffer.append("ERROR");
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
				buffer.append(getJobInvcPrice());
			} else {
				buffer.append("ERROR");
			}
		} catch (WrongInvoiceTypeException e) {
			buffer.append("ERROR");
		}

		buffer.append(", quantity=");
		buffer.append(getQuantity());

		buffer.append("]");
		return buffer.toString();
    }

}
