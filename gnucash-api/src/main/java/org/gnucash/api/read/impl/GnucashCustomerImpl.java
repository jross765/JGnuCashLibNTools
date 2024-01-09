package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncCustomer;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashGenerJob;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.read.impl.hlp.GnucashObjectImpl;
import org.gnucash.api.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.api.read.spec.GnucashCustomerInvoice;
import org.gnucash.api.read.spec.GnucashCustomerJob;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashCustomerImpl extends GnucashObjectImpl 
                                 implements GnucashCustomer 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashCustomerImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    private final GncGncCustomer jwsdpPeer;

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
    public GnucashCustomerImpl(final GncGncCustomer peer, final GnucashFile gcshFile) {
	super((peer.getCustSlots() == null) ? new ObjectFactory().createSlotsType() : peer.getCustSlots(), gcshFile);

	if (peer.getCustSlots() == null) {
	    peer.setCustSlots(getSlots());
	}

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
    public GCshID getID() {
	return new GCshID(jwsdpPeer.getCustGuid().getValue());
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
	return new GCshAddressImpl(jwsdpPeer.getCustAddr(), getGnucashFile());
    }

    /**
     * {@inheritDoc}
     */
    public GCshAddress getShippingAddress() {
	return new GCshAddressImpl(jwsdpPeer.getCustShipaddr(), getGnucashFile());
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
    public GCshID getTaxTableID() {
	GncGncCustomer.CustTaxtable custTaxtable = jwsdpPeer.getCustTaxtable();
	if (custTaxtable == null) {
	    return null;
	}

	return new GCshID( custTaxtable.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshTaxTable getTaxTable() {
	GCshID id = getTaxTableID();
	if (id == null) {
	    return null;
	}
	return getGnucashFile().getTaxTableByID(id);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshID getTermsID() {
	GncGncCustomer.CustTerms custTerms = jwsdpPeer.getCustTerms();
	if (custTerms == null) {
	    return null;
	}

	return new GCshID( custTerms.getValue() );
    }

    /**
     * {@inheritDoc}
     */
    public GCshBillTerms getTerms() {
	GCshID id = getTermsID();
	if (id == null) {
	    return null;
	}
	return getGnucashFile().getBillTermsByID(id);
    }

    // ---------------------------------------------------------------

    /**
     * date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     *
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     *  
     */
    @Override
    public int getNofOpenInvoices() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return getGnucashFile().getUnpaidInvoicesForCustomer_direct(this).size();
    }

    // -------------------------------------

    /**
     * @return the net sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getIncomeGenerated(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException {
	if ( readVar == GnucashGenerInvoice.ReadVariant.DIRECT )
	    return getIncomeGenerated_direct();
	else if ( readVar == GnucashGenerInvoice.ReadVariant.VIA_JOB )
	    return getIncomeGenerated_viaAllJobs();
	
	return null; // Compiler happy
    }

    /**
     * @return the net sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getIncomeGenerated_direct() throws UnknownAccountTypeException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashCustomerInvoice invcSpec : getPaidInvoices_direct()) {
//		    if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnucashCustomerInvoice invcSpec = new GnucashCustomerInvoiceImpl(invcGen); 
		GnucashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getIncomeGenerated_direct: Serious error");
	}

	return retval;
    }

    /**
     * @return the net sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getIncomeGenerated_viaAllJobs() throws UnknownAccountTypeException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashJobInvoice invcSpec : getPaidInvoices_viaAllJobs()) {
//		    if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_CUSTOMER) ) {
//		      GnucashCustomerInvoice invcSpec = new GnucashCustomerInvoiceImpl(invcGen); 
		GnucashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountWithoutTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getIncomeGenerated_viaAllJobs: Serious error");
	}

	return retval;
    }

    /**
     * @return formatted according to the current locale's currency-format
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated()
     */
    public String getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException {
	return getCurrencyFormat().format(getIncomeGenerated(readVar));

    }

    /**
     * @param lcl the locale to format for
     * @return formatted according to the given locale's currency-format
     * @throws UnknownAccountTypeException 
     *  
     * @see #getIncomeGenerated()
     */
    public String getIncomeGeneratedFormatted(GnucashGenerInvoice.ReadVariant readVar, final Locale lcl) throws UnknownAccountTypeException {
	return NumberFormat.getCurrencyInstance(lcl).format(getIncomeGenerated(readVar));
    }

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getOutstandingValue(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException {
	if ( readVar == GnucashGenerInvoice.ReadVariant.DIRECT )
	    return getOutstandingValue_direct();
	else if ( readVar == GnucashGenerInvoice.ReadVariant.VIA_JOB )
	    return getOutstandingValue_viaAllJobs();
	
	return null; // Compiler happy
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getOutstandingValue_direct() throws UnknownAccountTypeException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashCustomerInvoice invcSpec : getUnpaidInvoices_direct()) {
//            if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnucashCustomerInvoice invcSpec = new GnucashCustomerInvoiceImpl(invcGen); 
		GnucashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getOutstandingValue_direct: Serious error");
	}

	return retval;
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws UnknownAccountTypeException 
     *  
     */
    public FixedPointNumber getOutstandingValue_viaAllJobs() throws UnknownAccountTypeException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashJobInvoice invcSpec : getUnpaidInvoices_viaAllJobs()) {
//            if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_CUSTOMER) ) {
//              GnucashCustomerInvoice invcSpec = new GnucashCustomerInvoiceImpl(invcGen); 
		GnucashCustomer cust = invcSpec.getCustomer();
		if (cust.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) invcSpec).getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getOutstandingValue_viaAllJobs: Serious error");
	}

	return retval;
    }

    /**
     * @return Formatted according to the current locale's currency-format
     * @throws UnknownAccountTypeException 
     *  
     * @see #getOutstandingValue()
     */
    public String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar) throws UnknownAccountTypeException {
	return getCurrencyFormat().format(getOutstandingValue(readVar));
    }

    /**
     * @throws UnknownAccountTypeException 
     *  
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    public String getOutstandingValueFormatted(GnucashGenerInvoice.ReadVariant readVar, final Locale lcl) throws UnknownAccountTypeException {
	return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue(readVar));
    }

    // -----------------------------------------------------------------

    /**
     * @return the jobs that have this customer associated with them.
     * @throws WrongInvoiceTypeException 
     * @see GnucashCustomer#getGenerJobs()
     */
    public java.util.Collection<GnucashCustomerJob> getJobs() throws WrongInvoiceTypeException {

	List<GnucashCustomerJob> retval = new ArrayList<GnucashCustomerJob>();

	for ( GnucashGenerJob jobGener : getGnucashFile().getGenerJobs() ) {
	    if ( jobGener.getOwnerType() == GnucashGenerJob.TYPE_CUSTOMER ) {
		GnucashCustomerJob jobSpec = new GnucashCustomerJobImpl(jobGener);
		if ( jobSpec.getCustomerID().equals(getID()) ) {
		    retval.add(jobSpec);
		}
	    }
	}

	return retval;
    }

    // -----------------------------------------------------------------

    @Override
    public Collection<GnucashGenerInvoice> getInvoices() throws WrongInvoiceTypeException {
	Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();

	for ( GnucashCustomerInvoice invc : getGnucashFile().getInvoicesForCustomer_direct(this) ) {
	    retval.add(invc);
	}
	
	for ( GnucashJobInvoice invc : getGnucashFile().getInvoicesForCustomer_viaAllJobs(this) ) {
	    retval.add(invc);
	}
	
	return retval;
    }

    @Override
    public Collection<GnucashCustomerInvoice> getPaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return getGnucashFile().getPaidInvoicesForCustomer_direct(this);
    }

    @Override
    public Collection<GnucashJobInvoice>      getPaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return getGnucashFile().getPaidInvoicesForCustomer_viaAllJobs(this);
    }

    @Override
    public Collection<GnucashCustomerInvoice> getUnpaidInvoices_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return getGnucashFile().getUnpaidInvoicesForCustomer_direct(this);
    }

    @Override
    public Collection<GnucashJobInvoice>      getUnpaidInvoices_viaAllJobs() throws WrongInvoiceTypeException, UnknownAccountTypeException {
	return getGnucashFile().getUnpaidInvoicesForCustomer_viaAllJobs(this);
    }

    // ------------------------------------------------------------

    public static int getHighestNumber(GnucashCustomer cust) {
	return ((GnucashFileImpl) cust.getGnucashFile()).getHighestCustomerNumber();
    }

    public static String getNewNumber(GnucashCustomer cust) {
	return ((GnucashFileImpl) cust.getGnucashFile()).getNewCustomerNumber();
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashCustomerImpl [");
	
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
