package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.spec.GnuCashCustomerJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerInvoice;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.aux.GCshBllTrmID;
import org.gnucash.base.basetypes.simple.aux.GCshTaxTabID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GnuCashCustomerImpl extends GnuCashObjectImpl 
                                 implements GnuCashCustomer 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashCustomerImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncCustomer jwsdpPeer;

    /**
     * The currencyFormat to use for default-formating.<br/>
     * Please access only using {@link #getCurrencyFormat()}.
     *
     * @see #getCurrencyFormat()
     */
    private NumberFormat currencyFormat = null;

    // ---------------------------------------------------------------

    /**
     * @param peer    the JWSDP-object we are facading.
     * @param gcshFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnuCashCustomerImpl(final GncGncCustomer peer, final GnuCashFile gcshFile) {
	super(gcshFile);

//	if (peer.getCustSlots() == null) {
//	    peer.setCustSlots(getJwsdpPeer().getCustSlots());
//	}

	jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncCustomer getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshCustID getID() {
	return new GCshCustID(jwsdpPeer.getCustGuid().getValue());
    }

    /**
     * {@inheritDoc}
     */
    public String getNumber() {
	return jwsdpPeer.getCustId();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return jwsdpPeer.getCustName();
    }

    /**
     * {@inheritDoc}
     */
    public GCshAddress getAddress() {
	return new GCshAddressImpl(jwsdpPeer.getCustAddr(), getGnuCashFile());
    }

    /**
     * {@inheritDoc}
     */
    public GCshAddress getShippingAddress() {
	return new GCshAddressImpl(jwsdpPeer.getCustShipaddr(), getGnuCashFile());
    }

    /**
     * {@inheritDoc}
     */
    public FixedPointNumber getDiscount() {
	if ( jwsdpPeer.getCustDiscount() == null )
	    return null;
	
	return new FixedPointNumber(jwsdpPeer.getCustDiscount());
    }

    /**
     * {@inheritDoc}
     */
    public FixedPointNumber getCredit() {
	if ( jwsdpPeer.getCustCredit() == null )
	    return null;
	
	return new FixedPointNumber(jwsdpPeer.getCustCredit());
    }

    /**
     * {@inheritDoc}
     */
    public String getNotes() {
	return jwsdpPeer.getCustNotes();
    }

    // ---------------------------------------------------------------

    /**
     * @return the currency-format to use if no locale is given.
     */
    protected NumberFormat getCurrencyFormat() {
	if (currencyFormat == null) {
	    currencyFormat = NumberFormat.getCurrencyInstance();
	}

	return currencyFormat;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshTaxTabID getTaxTableID() {
	GncGncCustomer.CustTaxtable custTaxtable = jwsdpPeer.getCustTaxtable();
	if (custTaxtable == null) {
	    return null;
	}

	return new GCshTaxTabID( custTaxtable.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshTaxTable getTaxTable() {
    	GCshTaxTabID taxTabID = getTaxTableID();
	if (taxTabID == null) {
	    return null;
	}
	return getGnuCashFile().getTaxTableByID(taxTabID);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshBllTrmID getTermsID() {
	GncGncCustomer.CustTerms custTerms = jwsdpPeer.getCustTerms();
	if (custTerms == null) {
	    return null;
	}

	return new GCshBllTrmID( custTerms.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshBillTerms getTerms() {
    	GCshBllTrmID termsID = getTermsID();
	if (termsID == null) {
	    return null;
	}
	return getGnuCashFile().getBillTermsByID(termsID);
    }

    // ---------------------------------------------------------------

    /**
     * date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     *
     * @return the current number of Unpaid invoices
     */
    @Override
    public int getNofOpenInvoices() {
	return getGnuCashFile().getUnpaidInvoicesForCustomer_direct(this).size();
    }

    // -------------------------------------

    /**
     * @return the net sum of payments for invoices to this client
     *  
     * @see #getIncomeGenerated_direct()
     * @see #getIncomeGenerated_viaAllJobs()
     */
    public FixedPointNumber getIncomeGenerated(GnuCashGenerInvoice.ReadVariant readVar) {
	if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT )
	    return getIncomeGenerated_direct();
	else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB )
	    return getIncomeGenerated_viaAllJobs();
	
	return null; // Compiler happy
    }

    /**
     * @return the net sum of payments for invoices to this client
     * 
     * @see #getIncomeGenerated_viaAllJobs()
     */
    public FixedPointNumber getIncomeGenerated_direct() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashCustomerInvoice invcSpec : getPaidInvoices_direct()) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
		GnuCashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(invcSpec.getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return the net sum of payments for invoices to this client
     *  
     * @see #getIncomeGenerated_direct()
     */
    public FixedPointNumber getIncomeGenerated_viaAllJobs() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashJobInvoice invcSpec : getPaidInvoices_viaAllJobs()) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
		GnuCashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(invcSpec.getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return formatted according to the current locale's currency-format
     *  
     * @see #getIncomeGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    public String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar) {
	return getCurrencyFormat().format(getIncomeGenerated(readVar));

    }

    /**
     * @param lcl the locale to format for
     * @return formatted according to the given locale's currency-format
     */
    public String getIncomeGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, final Locale lcl) {
	return NumberFormat.getCurrencyInstance(lcl).format(getIncomeGenerated(readVar));
    }

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * 
     * @see #getOutstandingValue_direct()
     * @see #getOutstandingValue_viaAllJobs()
     */
    public FixedPointNumber getOutstandingValue(GnuCashGenerInvoice.ReadVariant readVar) {
	if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT )
	    return getOutstandingValue_direct();
	else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB )
	    return getOutstandingValue_viaAllJobs();
	
	return null; // Compiler happy
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     * @see #getOutstandingValue_viaAllJobs()
     */
    public FixedPointNumber getOutstandingValue_direct() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashCustomerInvoice invcSpec : getUnpaidInvoices_direct()) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
		GnuCashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(invcSpec.getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     *  
     * @see #getOutstandingValue_direct()
     */
    public FixedPointNumber getOutstandingValue_viaAllJobs() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashJobInvoice invcSpec : getUnpaidInvoices_viaAllJobs()) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnuCashCustomerInvoice invcSpec = new GnuCashCustomerInvoiceImpl(invcGen); 
		GnuCashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(invcSpec.getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return Formatted according to the current locale's currency-format
     *  
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    public String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar) {
	return getCurrencyFormat().format(getOutstandingValue(readVar));
    }

    /**
     * @see #getOutstandingValue(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    public String getOutstandingValueFormatted(GnuCashGenerInvoice.ReadVariant readVar, final Locale lcl) {
	return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue(readVar));
    }

    // -----------------------------------------------------------------

    /**
     * @return the jobs that have this customer associated with them.
     */
    public List<GnuCashCustomerJob> getJobs() {

	List<GnuCashCustomerJob> retval = new ArrayList<GnuCashCustomerJob>();

	for ( GnuCashGenerJob jobGener : getGnuCashFile().getGenerJobs() ) {
	    if ( jobGener.getOwnerType() == GnuCashGenerJob.TYPE_CUSTOMER ) {
		GnuCashCustomerJob jobSpec = new GnuCashCustomerJobImpl(jobGener);
		if ( jobSpec.getCustomerID().equals(getID()) ) {
		    retval.add(jobSpec);
		}
	    }
	}

	return retval;
    }

    // -----------------------------------------------------------------

    @Override
    public List<GnuCashGenerInvoice> getInvoices() {
    	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();

	for ( GnuCashCustomerInvoice invc : getGnuCashFile().getInvoicesForCustomer_direct(this) ) {
	    retval.add(invc);
	}
	
	for ( GnuCashJobInvoice invc : getGnuCashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
	    retval.add(invc);
	}
	
	return retval;
    }

    @Override
    public List<GnuCashCustomerInvoice> getPaidInvoices_direct() {
	return getGnuCashFile().getPaidInvoicesForCustomer_direct(this);
    }

    @Override
    public List<GnuCashJobInvoice>      getPaidInvoices_viaAllJobs() {
	return getGnuCashFile().getPaidInvoicesForCustomer_viaAllJobs(this);
    }

    @Override
    public List<GnuCashCustomerInvoice> getUnpaidInvoices_direct() {
	return getGnuCashFile().getUnpaidInvoicesForCustomer_direct(this);
    }

    @Override
    public List<GnuCashJobInvoice>      getUnpaidInvoices_viaAllJobs() {
	return getGnuCashFile().getUnpaidInvoicesForCustomer_viaAllJobs(this);
    }

    // ------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getCustSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getCustSlots());
	}
    
    // ------------------------------------------------------------

    public static int getHighestNumber(GnuCashCustomer cust) {
	return ((GnuCashFileImpl) cust.getGnuCashFile()).getHighestCustomerNumber();
    }

    public static String getNewNumber(GnuCashCustomer cust) {
	return ((GnuCashFileImpl) cust.getGnuCashFile()).getNewCustomerNumber();
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashCustomerImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", number='");
	buffer.append(getNumber() + "'");
	
	buffer.append(", name='");
	buffer.append(getName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
