package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.generated.GncGncVendor;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.read.impl.hlp.GnuCashObjectImpl;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.spec.GnuCashVendorJobImpl;
import org.gnucash.api.read.spec.GnuCashJobInvoice;
import org.gnucash.api.read.spec.GnuCashVendorBill;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class GnuCashVendorImpl extends GnuCashObjectImpl 
                               implements GnuCashVendor 
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashVendorImpl.class);

    // ---------------------------------------------------------------

    /**
     * the JWSDP-object we are facading.
     */
    protected final GncGncVendor jwsdpPeer;

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
    public GnuCashVendorImpl(final GncGncVendor peer, final GnuCashFile gcshFile) {
	super(gcshFile);

//	if (peer.getVendorSlots() == null) {
//	    peer.setVendorSlots(getJwsdpPeer().getVendorSlots());
//	}

	jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncVendor getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshID getID() {
	return new GCshID(jwsdpPeer.getVendorGuid().getValue());
    }

    /**
     * {@inheritDoc}
     */
    public String getNumber() {
	return jwsdpPeer.getVendorId();
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
	return jwsdpPeer.getVendorName();
    }

    /**
     * {@inheritDoc}
     */
    public GCshAddress getAddress() {
	return new GCshAddressImpl(jwsdpPeer.getVendorAddr(), getGnuCashFile());
    }

    /**
     * {@inheritDoc}
     */
    public String getNotes() {
	return jwsdpPeer.getVendorNotes();
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
    public GCshID getTaxTableID() {
	GncGncVendor.VendorTaxtable vendTaxtable = jwsdpPeer.getVendorTaxtable();
	if (vendTaxtable == null) {
	    return null;
	}

	return new GCshID( vendTaxtable.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshTaxTable getTaxTable() {
	GCshID id = getTaxTableID();
	if (id == null) {
	    return null;
	}
	return getGnuCashFile().getTaxTableByID(id);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshID getTermsID() {
	GncGncVendor.VendorTerms vendTerms = jwsdpPeer.getVendorTerms();
	if (vendTerms == null) {
	    return null;
	}

	return new GCshID( vendTerms.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshBillTerms getTerms() {
	GCshID id = getTermsID();
	if (id == null) {
	    return null;
	}
	return getGnuCashFile().getBillTermsByID(id);
    }

    // ---------------------------------------------------------------

    /**
     * date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     *
     * @return the current number of Unpaid invoices
     */
    @Override
    public int getNofOpenBills() {
	return getGnuCashFile().getUnpaidBillsForVendor_direct(this).size();
    }

    // -------------------------------------

    /**
     * @return the net sum of payments for invoices to this client
     */
    public FixedPointNumber getExpensesGenerated(GnuCashGenerInvoice.ReadVariant readVar) {
	if ( readVar == GnuCashGenerInvoice.ReadVariant.DIRECT )
	    return getExpensesGenerated_direct();
	else if ( readVar == GnuCashGenerInvoice.ReadVariant.VIA_JOB )
	    return getExpensesGenerated_viaAllJobs();
	
	return null; // Compiler happy
    }

    /**
     * @return the net sum of payments for invoices to this client
     */
    public FixedPointNumber getExpensesGenerated_direct() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashVendorBill bllSpec : getPaidBills_direct()) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
		GnuCashVendor vend = bllSpec.getVendor();
		if (vend.getID().equals(this.getID())) {
		    retval.add(bllSpec.getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return the net sum of payments for invoices to this client
     */
    public FixedPointNumber getExpensesGenerated_viaAllJobs() {
	FixedPointNumber retval = new FixedPointNumber();

	    for (GnuCashJobInvoice bllSpec : getPaidBills_viaAllJobs()) {
//		    if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//		      GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
		GnuCashVendor vend = bllSpec.getVendor();
		if (vend.getID().equals(this.getID())) {
		    retval.add(bllSpec.getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for

	return retval;
    }

    /**
     * @return formatted according to the current locale's currency-format
     *  
     * @see #getExpensesGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    public String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar) {
	return getCurrencyFormat().format(getExpensesGenerated(readVar));

    }

    /**
     * @param lcl the locale to format for
     * @return formatted according to the given locale's currency-format
     *  
     * @see #getExpensesGenerated(org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant)
     */
    public String getExpensesGeneratedFormatted(GnuCashGenerInvoice.ReadVariant readVar, final Locale lcl) {
	return NumberFormat.getCurrencyInstance(lcl).format(getExpensesGenerated(readVar));
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

	    for (GnuCashVendorBill bllSpec : getUnpaidBills_direct()) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
		GnuCashVendor vend = bllSpec.getVendor();
		if (vend.getID().equals(this.getID())) {
		    retval.add(bllSpec.getAmountUnpaidWithTaxes());
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

	    for (GnuCashJobInvoice bllSpec : getUnpaidBills_viaAllJobs()) {
//            if ( invcGen.getType().equals(GnuCashGenerInvoice.TYPE_VENDOR) ) {
//              GnuCashVendorBill bllSpec = new GnuCashVendorBillImpl(invcGen); 
		GnuCashVendor vend = bllSpec.getVendor();
		if (vend.getID().equals(this.getID())) {
		    retval.add(bllSpec.getAmountUnpaidWithTaxes());
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
     * @return the jobs that have this vendor associated with them.
     */
    public List<GnuCashVendorJob> getJobs() {

	List<GnuCashVendorJob> retval = new ArrayList<GnuCashVendorJob>();

	for ( GnuCashGenerJob jobGener : getGnuCashFile().getGenerJobs() ) {
	    if ( jobGener.getOwnerType() == GnuCashGenerJob.TYPE_VENDOR ) {
		GnuCashVendorJob jobSpec = new GnuCashVendorJobImpl(jobGener);
		if ( jobSpec.getVendorID().equals(getID()) ) {
		    retval.add(jobSpec);
		}
	    }
	}

	return retval;
    }

    // -----------------------------------------------------------------

    @Override
    public List<GnuCashGenerInvoice> getBills() {
    	List<GnuCashGenerInvoice> retval = new ArrayList<GnuCashGenerInvoice>();

	for ( GnuCashVendorBill invc : getGnuCashFile().getBillsForVendor_direct(this) ) {
	    retval.add(invc);
	}
	
	for ( GnuCashJobInvoice invc : getGnuCashFile().getBillsForVendor_viaAllJobs(this) ) {
	    retval.add(invc);
	}
	
	return retval;
    }

    @Override
    public List<GnuCashVendorBill> getPaidBills_direct() {
	return getGnuCashFile().getPaidBillsForVendor_direct(this);
    }

    @Override
    public List<GnuCashJobInvoice> getPaidBills_viaAllJobs() {
	return getGnuCashFile().getPaidBillsForVendor_viaAllJobs(this);
    }

    @Override
    public List<GnuCashVendorBill> getUnpaidBills_direct() {
	return getGnuCashFile().getUnpaidBillsForVendor_direct(this);
    }

    @Override
    public List<GnuCashJobInvoice> getUnpaidBills_viaAllJobs() {
	return getGnuCashFile().getUnpaidBillsForVendor_viaAllJobs(this);
    }

    // ------------------------------------------------------------

	@Override
	public String getUserDefinedAttribute(String name) {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeCore(jwsdpPeer.getVendorSlots(), name);
	}

	@Override
	public List<String> getUserDefinedAttributeKeys() {
		return HasUserDefinedAttributesImpl
					.getUserDefinedAttributeKeysCore(jwsdpPeer.getVendorSlots());
	}

    // ------------------------------------------------------------

    public static int getHighestNumber(GnuCashVendor vend) {
	return ((GnuCashFileImpl) vend.getGnuCashFile()).getHighestVendorNumber();
    }

    public static String getNewNumber(GnuCashVendor vend) {
	return ((GnuCashFileImpl) vend.getGnuCashFile()).getNewVendorNumber();
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnuCashVendorImpl [");
	
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
