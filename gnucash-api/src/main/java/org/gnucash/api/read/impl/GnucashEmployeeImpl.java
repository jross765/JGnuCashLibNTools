package org.gnucash.api.read.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncEmployee;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.UnknownAccountTypeException;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.aux.GCshAddressImpl;
import org.gnucash.api.read.spec.GnucashEmployeeVoucher;
import org.gnucash.api.read.spec.SpecInvoiceCommon;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GnucashEmployeeImpl extends GnucashObjectImpl 
                                 implements GnucashEmployee 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashEmployeeImpl.class);

    /**
     * the JWSDP-object we are facading.
     */
    private final GncGncEmployee jwsdpPeer;

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
     * @param gncFile the file to register under
     */
    @SuppressWarnings("exports")
    public GnucashEmployeeImpl(final GncGncEmployee peer, final GnucashFile gncFile) {
	super((peer.getEmployeeSlots() == null) ? new ObjectFactory().createSlotsType() : peer.getEmployeeSlots(), gncFile);

	if (peer.getEmployeeSlots() == null) {
	    peer.setEmployeeSlots(getSlots());
	}

	jwsdpPeer = peer;
    }

    // ---------------------------------------------------------------

    /**
     * @return the JWSDP-object we are wrapping.
     */
    @SuppressWarnings("exports")
    public GncGncEmployee getJwsdpPeer() {
	return jwsdpPeer;
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GCshID getID() {
	return new GCshID(jwsdpPeer.getEmployeeGuid().getValue());
    }

    /**
     * {@inheritDoc}
     */
    public String getNumber() {
	return jwsdpPeer.getEmployeeId();
    }

    /**
     * {@inheritDoc}
     */
    public String getUserName() {
	return jwsdpPeer.getEmployeeUsername();
    }

    /**
     * {@inheritDoc}
     */
    public GCshAddress getAddress() {
	return new GCshAddressImpl(jwsdpPeer.getEmployeeAddr(), getGnucashFile());
    }

    /**
     * {@inheritDoc}
     */
    public String getLanguage() {
	return jwsdpPeer.getEmployeeLanguage();
    }

    /**
     * {@inheritDoc}
     */
    public String getNotes() {
	// ::TODO ::CHECK
	// return jwsdpPeer.getEmployeeNotes();
	return "NOT IMPLEMENTED YET";
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

//    /**
//     * {@inheritDoc}
//     */
//    public String getTaxTableID() {
//	GncGncEmployee.EmployeeTaxtable emplTaxtable = jwsdpPeer.getEmployeeTaxtable();
//	if (emplTaxtable == null) {
//	    return null;
//	}
//
//	return emplTaxtable.getValue();
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public GCshTaxTable getTaxTable() {
//	String id = getTaxTableID();
//	if (id == null) {
//	    return null;
//	}
//	return getGnucashFile().getTaxTableByID(id);
//    }

    // ---------------------------------------------------------------

    /**
     * date is not checked so invoiced that have entered payments in the future are
     * considered Paid.
     *
     * @return the current number of Unpaid invoices
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    @Override
    public int getNofOpenVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getGnucashFile().getUnpaidVouchersForEmployee(this).size();
    }

    // -------------------------------------

    /**
     * @return the net sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    public FixedPointNumber getExpensesGenerated() throws UnknownAccountTypeException, IllegalArgumentException {
	return getExpensesGenerated_direct();
    }

    /**
     * @return the net sum of payments for invoices to this client
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    public FixedPointNumber getExpensesGenerated_direct() throws UnknownAccountTypeException, IllegalArgumentException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashEmployeeVoucher vchSpec : getPaidVouchers()) {
//		    if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_EMPLOYEE) ) {
//		      GnucashEmployeeVoucher vchSpec = new GnucashEmployeeVoucherImpl(invcGen); 
		GnucashEmployee empl = vchSpec.getEmployee();
		if (empl.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) vchSpec).getAmountWithoutTaxes());
		}
//            } // if vch type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getExpensesGenerated_direct: Serious error");
	}

	return retval;
    }

    /**
     * @return formatted according to the current locale's currency-format
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see #getExpensesGenerated()
     */
    public String getExpensesGeneratedFormatted() throws UnknownAccountTypeException, IllegalArgumentException {
	return getCurrencyFormat().format(getExpensesGenerated());

    }

    /**
     * @param lcl the locale to format for
     * @return formatted according to the given locale's currency-format
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see #getExpensesGenerated()
     */
    public String getExpensesGeneratedFormatted(final Locale lcl) throws UnknownAccountTypeException, IllegalArgumentException {
	return NumberFormat.getCurrencyInstance(lcl).format(getExpensesGenerated());
    }

    // -------------------------------------

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    public FixedPointNumber getOutstandingValue() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getOutstandingValue_direct();
    }

    /**
     * @return the sum of left to pay Unpaid invoiced
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     */
    public FixedPointNumber getOutstandingValue_direct() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	FixedPointNumber retval = new FixedPointNumber();

	try {
	    for (GnucashEmployeeVoucher vchSpec : getUnpaidVouchers()) {
//            if ( invcGen.getType().equals(GnucashGenerInvoice.TYPE_VENDOR) ) {
//              GnucashEmployeeVoucher vchSpec = new GnucashEmployeeVoucherImpl(invcGen); 
		GnucashEmployee empl = vchSpec.getEmployee();
		if (empl.getID().equals(this.getID())) {
		    retval.add(((SpecInvoiceCommon) vchSpec).getAmountUnpaidWithTaxes());
		}
//            } // if invc type
	    } // for
	} catch (WrongInvoiceTypeException e) {
	    LOGGER.error("getOutstandingValue_direct: Serious error");
	}

	return retval;
    }

    /**
     * @return Formatted according to the current locale's currency-format
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see #getOutstandingValue()
     */
    public String getOutstandingValueFormatted() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getCurrencyFormat().format(getOutstandingValue());
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws UnknownAccountTypeException 
     * @throws 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @see #getOutstandingValue() Formatted according to the given locale's
     *      currency-format
     */
    public String getOutstandingValueFormatted(final Locale lcl) throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return NumberFormat.getCurrencyInstance(lcl).format(getOutstandingValue());
    }

    // -----------------------------------------------------------------

    @Override
    public Collection<GnucashGenerInvoice> getVouchers() throws WrongInvoiceTypeException, IllegalArgumentException {
	Collection<GnucashGenerInvoice> retval = new ArrayList<GnucashGenerInvoice>();

	for ( GnucashEmployeeVoucher invc : getGnucashFile().getVouchersForEmployee(this) ) {
	    retval.add(invc);
	}
	
	return retval;
    }

    @Override
    public Collection<GnucashEmployeeVoucher> getPaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getGnucashFile().getPaidVouchersForEmployee(this);
    }

    @Override
    public Collection<GnucashEmployeeVoucher> getUnpaidVouchers() throws WrongInvoiceTypeException, UnknownAccountTypeException, IllegalArgumentException {
	return getGnucashFile().getUnpaidVouchersForEmployee(this);
    }

    // ------------------------------------------------------------

    public static int getHighestNumber(GnucashEmployee empl) {
	return ((GnucashFileImpl) empl.getGnucashFile()).getHighestEmployeeNumber();
    }

    public static String getNewNumber(GnucashEmployee empl) {
	return ((GnucashFileImpl) empl.getGnucashFile()).getNewEmployeeNumber();
    }

    // -----------------------------------------------------------------

    public String toString() {
	StringBuffer buffer = new StringBuffer();
	buffer.append("GnucashEmployeeImpl [");
	
	buffer.append("id=");
	buffer.append(getID());
	
	buffer.append(", number='");
	buffer.append(getNumber() + "'");
	
	buffer.append(", username='");
	buffer.append(getUserName() + "'");
	
	buffer.append("]");
	return buffer.toString();
    }

}
