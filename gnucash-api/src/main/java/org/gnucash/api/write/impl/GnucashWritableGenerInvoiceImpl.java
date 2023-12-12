package org.gnucash.api.write.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
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
import org.gnucash.api.read.impl.spec.GnucashJobInvoiceImpl;
import org.gnucash.api.read.spec.GnucashJobInvoice;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.GnucashWritableGenerInvoice;
import org.gnucash.api.write.GnucashWritableGenerInvoiceEntry;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
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
import org.gnucash.api.generated.GncAccount;
import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.OwnerId;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;

/**
 * TODO write a comment what this type does here
 */
public class GnucashWritableGenerInvoiceImpl extends GnucashGenerInvoiceImpl 
                                             implements GnucashWritableGenerInvoice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableGenerInvoiceImpl.class);

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFile getFile() {
	return (GnucashWritableFile) super.getFile();
    }
    
    // ---------------------------------------------------------------

    /**
     * Create an editable invoice facading an existing JWSDP-peer.
     *
     * @param jwsdpPeer the JWSDP-object we are facading.
     * @param file      the file to register under
     * @see GnucashGenerInvoiceImpl#GnucashInvoiceImpl(GncV2.GncBook.GncGncInvoice,
     *      GnucashFile)
     */
    @SuppressWarnings("exports")
    public GnucashWritableGenerInvoiceImpl(
	    final GncV2.GncBook.GncGncInvoice jwsdpPeer, 
	    final GnucashFile file) {
	super(jwsdpPeer, file);
    }

//    /**
//     * @param file the file we are associated with.
//     * @throws WrongOwnerTypeException 
//     * @throws InvalidCmdtyCurrTypeException 
//     * @throws IllegalAccessException 
//     * @throws IllegalArgumentException 
//     * @throws ClassNotFoundException 
//     * @throws SecurityException 
//     * @throws NoSuchFieldException 
//     * @throws IllegalTransactionSplitActionException 
//     */
//    protected GnucashWritableGenerInvoiceImpl(
//	    final GnucashWritableFileImpl file, 
//	    final String number,
//	    final GnucashCustomer cust, 
//	    final GnucashAccountImpl incomeAcct,
//	    final GnucashAccountImpl receivableAcct,
//	    final LocalDate openedDate,
//	    final LocalDate postDate,
//	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
//	super(createCustomerInvoice_int(file, 
//		                    number, cust,
//		                    false,
//		                    incomeAcct, receivableAcct, 
//		                    openedDate, postDate, dueDate),
//	      file);
//    }
//
//    /**
//     * @param file the file we are associated with.
//     * @throws WrongOwnerTypeException 
//     * @throws InvalidCmdtyCurrTypeException 
//     * @throws IllegalAccessException 
//     * @throws IllegalArgumentException 
//     * @throws ClassNotFoundException 
//     * @throws SecurityException 
//     * @throws NoSuchFieldException 
//     * @throws IllegalTransactionSplitActionException 
//     */
//    protected GnucashWritableGenerInvoiceImpl(
//	    final GnucashWritableFileImpl file, 
//	    final String number,
//	    final GnucashVendor vend, 
//	    final GnucashAccountImpl expensesAcct,
//	    final GnucashAccountImpl payableAcct,
//	    final LocalDate openedDate,
//	    final LocalDate postDate,
//	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
//	super(createVendorBill_int(file, 
//		               number, vend,
//		               false,
//		               expensesAcct, payableAcct, 
//		               openedDate, postDate, dueDate),
//	      file);
//    }
//
//    /**
//     * @param file the file we are associated with.
//     * @throws WrongOwnerTypeException 
//     * @throws InvalidCmdtyCurrTypeException 
//     * @throws IllegalAccessException 
//     * @throws IllegalArgumentException 
//     * @throws ClassNotFoundException 
//     * @throws SecurityException 
//     * @throws NoSuchFieldException 
//     * @throws IllegalTransactionSplitActionException 
//     */
//    protected GnucashWritableGenerInvoiceImpl(
//	    final GnucashWritableFileImpl file, 
//	    final String number,
//	    final GnucashEmployee empl, 
//	    final GnucashAccountImpl expensesAcct,
//	    final GnucashAccountImpl payableAcct,
//	    final LocalDate openedDate,
//	    final LocalDate postDate,
//	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
//	super(createEmployeeVoucher_int(file, 
//		               number, empl,
//		               false,
//		               expensesAcct, payableAcct, 
//		               openedDate, postDate, dueDate),
//	      file);
//    }
//
//    /**
//     * @param file the file we are associated with.
//     * @throws WrongOwnerTypeException 
//     * @throws InvalidCmdtyCurrTypeException 
//     * @throws IllegalAccessException 
//     * @throws IllegalArgumentException 
//     * @throws ClassNotFoundException 
//     * @throws SecurityException 
//     * @throws NoSuchFieldException 
//     * @throws IllegalTransactionSplitActionException 
//     */
//    protected GnucashWritableGenerInvoiceImpl(
//	    final GnucashWritableFileImpl file, 
//	    final String number,
//	    final GnucashGenerJob job, 
//	    final GnucashAccountImpl incExpAcct,
//	    final GnucashAccountImpl payblRecvblAcct,
//	    final LocalDate openedDate,
//	    final LocalDate postDate,
//	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
//	super(createJobInvoice_int(file, 
//		               number, job,
//		               false,
//			       incExpAcct, payblRecvblAcct,
//			       openedDate, postDate, dueDate),
//	      file);
//    }

    // ---------------------------------------------------------------

    /**
     * create and add a new entry.
     * 
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableGenerInvoiceEntry createGenerEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	GnucashWritableCustomerInvoiceEntryImpl entry = new GnucashWritableCustomerInvoiceEntryImpl(
								new GnucashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

        entry.setInvcTaxable(false);
        
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createCustInvcEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getFile().getTaxTableByName(taxTabName);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableCustomerInvoiceEntry createCustInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableCustomerInvoiceEntryImpl entry = new GnucashWritableCustomerInvoiceEntryImpl(
								new GnucashWritableCustomerInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setInvcTaxable(false);
	} else {
	    entry.setInvcTaxTable(taxTab);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableVendorBillEntry createVendBillEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	
	GnucashWritableVendorBillEntryImpl entry = new GnucashWritableVendorBillEntryImpl(
								new GnucashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setBillTaxable(false);
	
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableVendorBillEntry createVendBillEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createVendBillEntry(acct,
                                       singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getFile().getTaxTableByName(taxTabName);
	    LOGGER.debug("createVendBillEntry: Found tax table with name '" + taxTabName + "': '" + taxTab.getID() + "'");
	    return createVendBillEntry(acct,
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableVendorBillEntry createVendBillEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableVendorBillEntryImpl entry = new GnucashWritableVendorBillEntryImpl(
								new GnucashWritableVendorBillImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setBillTaxable(false);
	} else {
	    entry.setBillTaxTable(taxTab);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	
	GnucashWritableEmployeeVoucherEntryImpl entry = new GnucashWritableEmployeeVoucherEntryImpl(
								new GnucashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setVoucherTaxable(false);
	
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createEmplVchEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getFile().getTaxTableByName(taxTabName);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableEmployeeVoucherEntry createEmplVchEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableEmployeeVoucherEntryImpl entry = new GnucashWritableEmployeeVoucherEntryImpl(
								new GnucashWritableEmployeeVoucherImpl(this), 
								acct, quantity, singleUnitPrice);
	
	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setVoucherTaxable(false);
	} else {
	    entry.setVoucherTaxTable(taxTab);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	
	GnucashWritableJobInvoiceEntryImpl entry = new GnucashWritableJobInvoiceEntryImpl(
								new GnucashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);
	
	entry.setJobTaxable(false);
	
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final String taxTabName)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTabName == null )
	    throw new IllegalStateException("Tax table name is null");
	
	if ( taxTabName.equals("") ) {
	    // no taxes
	    return createJobInvcEntry(acct,
                                      singleUnitPrice, quantity);
	} else {
	    GCshTaxTable taxTab = getFile().getTaxTableByName(taxTabName);
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableJobInvoiceEntry createJobInvcEntry(
	    final GnucashAccount acct,
	    final FixedPointNumber singleUnitPrice, 
	    final FixedPointNumber quantity, 
	    final GCshTaxTable taxTab)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, UnknownInvoiceTypeException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if ( taxTab == null )
	    throw new IllegalStateException("Tax table is null");
	
	GnucashWritableJobInvoiceEntryImpl entry = new GnucashWritableJobInvoiceEntryImpl(
								new GnucashWritableJobInvoiceImpl(this), 
								acct, quantity, singleUnitPrice);

	if ( taxTab.getEntries().isEmpty() || 
	     taxTab.getEntries().iterator().next().getAmount().equals(new FixedPointNumber()) ) {
	    // no taxes
	    entry.setJobTaxable(false);
	} else {
	    entry.setJobTaxTable(taxTab);
	}
	
	addJobEntry(entry);
	return entry;
    }

    // -----------------------------------------------------------

    /**
     * Use
     * ${@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncV2.GncBook.GncGncInvoice createCustomerInvoice_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashCustomer cust,
	    final boolean postInvoice,
	    final GnucashAccountImpl incomeAcct,
	    final GnucashAccountImpl receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncV2.GncBook.GncGncInvoice invc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceGuid invcRef = fact.createGncV2GncBookGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    invc.setInvoiceGuid(invcRef);
	}
	
	invc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::TODO Do *not* fill with invoice number,
	                                     // but instead with customer's reference number
	invc.setInvoiceActive(1);
	
	// currency
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceCurrency currency = fact.createGncV2GncBookGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    invc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOpened opened = fact.createGncV2GncBookGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    invc.setInvoiceOpened(opened);
	}
	
	// owner (customer)
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOwner custRef = fact.createGncV2GncBookGncGncInvoiceInvoiceOwner();
	    custRef.setOwnerType(GCshOwner.Type.CUSTOMER.getCode());
	    custRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(cust.getID().toString());
		custRef.setOwnerId(ownerIdRef);
	    }
	    invc.setInvoiceOwner(custRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createCustomerInvoice_int: Posting customer invoice " + invcGUID + "...");
	    postCustomerInvoice_int(file, fact,
	                            invc, invcGUID, number, 
	                            cust, 
                                    incomeAcct, receivableAcct,
                                    new FixedPointNumber(0), 
                                    postDate, dueDate);
	} else {
	    LOGGER.debug("createCustomerInvoice_int: NOT posting customer invoice " + invcGUID);
	}
	
	invc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(invc);
	file.setModified(true);
	
	return invc;
    }

    // ---------------------------------------------------------------

    /**
     * Use
     * ${@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncV2.GncBook.GncGncInvoice createVendorBill_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashVendor vend,
	    final boolean postInvoice,
	    final GnucashAccountImpl expensesAcct,
	    final GnucashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncV2.GncBook.GncGncInvoice invc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceGuid invcRef = fact.createGncV2GncBookGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    invc.setInvoiceGuid(invcRef);
	}
	
	invc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::CHECK Doesn't really make sense in a vendor bill
	                                     // And even if: would have to be separate number
	invc.setInvoiceActive(1);
	
	// currency
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceCurrency currency = fact.createGncV2GncBookGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    invc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOpened opened = fact.createGncV2GncBookGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    invc.setInvoiceOpened(opened);
	}
	
	// owner (vendor)
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOwner vendRef = fact.createGncV2GncBookGncGncInvoiceInvoiceOwner();
	    vendRef.setOwnerType(GCshOwner.Type.VENDOR.getCode());
	    vendRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(vend.getID().toString());
		vendRef.setOwnerId(ownerIdRef);
	    }
	    invc.setInvoiceOwner(vendRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createVendorBill_int: Posting vendor bill " + invcGUID + "...");
	    postVendorBill_int(file, fact,
		               invc, invcGUID, number, 
		               vend, 
		               expensesAcct, payableAcct, 
		               new FixedPointNumber(0),
		               postDate, dueDate);
	} else {
	    LOGGER.debug("createVendorBill_int: NOT posting vendor bill " + invcGUID);
	}
	
	invc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(invc);
	file.setModified(true);
	
	return invc;
    }

    /**
     * Use
     * ${@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyFrom e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncV2.GncBook.GncGncInvoice createEmployeeVoucher_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashEmployee empl,
	    final boolean postInvoice,
	    final GnucashAccountImpl expensesAcct,
	    final GnucashAccountImpl payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncV2.GncBook.GncGncInvoice invc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceGuid invcRef = fact.createGncV2GncBookGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    invc.setInvoiceGuid(invcRef);
	}
	
	invc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::CHECK Does taht make sense in an employee voucher?
	                                     // And if: wouldn't it have to be a separate number?
	invc.setInvoiceActive(1);
	
	// currency
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceCurrency currency = fact.createGncV2GncBookGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    invc.setInvoiceCurrency(currency);
	}
	
	// date opened
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOpened opened = fact.createGncV2GncBookGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    invc.setInvoiceOpened(opened);
	}
	
	// owner (vendor)
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOwner vendRef = fact.createGncV2GncBookGncGncInvoiceInvoiceOwner();
	    vendRef.setOwnerType(GCshOwner.Type.EMPLOYEE.getCode());
	    vendRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(empl.getID().toString());
		vendRef.setOwnerId(ownerIdRef);
	    }
	    invc.setInvoiceOwner(vendRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createEmployeeVoucher_int: Posting employee voucher " + invcGUID + "...");
	    postEmployeeVoucher_int(file, fact,
		                    invc, invcGUID, number, 
		                    empl, 
		                    expensesAcct, payableAcct, 
		                    new FixedPointNumber(0),
		                    postDate, dueDate);
	} else {
	    LOGGER.debug("createEmployeeVoucher_int: NOT posting employee voucher " + invcGUID);
	}
	
	invc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(invc);
	file.setModified(true);
	
	return invc;
    }

    /**
     * Use
     * ${@link GnucashWritableFile#createWritableInvoice(String, GnucashGenerJob, GnucashAccount, java.util.Date)}
     * instead of calling this method!
     *
     * @param accountToTransferMoneyTo e.g. "Forderungen aus Lieferungen und
     *                                 Leistungen "
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     */
    protected static GncV2.GncBook.GncGncInvoice createJobInvoice_int(
	    final GnucashWritableFileImpl file,
	    final String number, 
	    final GnucashGenerJob job,
	    final boolean postInvoice,
	    final GnucashAccountImpl incExpAcct,
	    final GnucashAccountImpl recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	ObjectFactory fact = file.getObjectFactory();
	GCshID invcGUID = GCshID.getNew();

	GncV2.GncBook.GncGncInvoice invc = file.createGncGncInvoiceType();

	// GUID
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceGuid invcRef = fact.createGncV2GncBookGncGncInvoiceInvoiceGuid();
	    invcRef.setType(Const.XML_DATA_TYPE_GUID);
	    invcRef.setValue(invcGUID.toString());
	    invc.setInvoiceGuid(invcRef);
	}
	
	invc.setInvoiceId(number);
	// invc.setInvoiceBillingID(number); // ::TODO ::CHECK Do *not* fill with invoice number,
                                             // but instead with customer's reference number,
	                                     // if it's a customer job (and even then -- the job 
	                                     // itself should contain this number). If it's a 
	                                     // vendor bill, then this does not make sense anyway.
	invc.setInvoiceActive(1);
	
	// currency
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceCurrency currency = fact.createGncV2GncBookGncGncInvoiceInvoiceCurrency();
	    currency.setCmdtyId(file.getDefaultCurrencyID());
	    currency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	    invc.setInvoiceCurrency(currency);
	}

	// date opened
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOpened opened = fact.createGncV2GncBookGncGncInvoiceInvoiceOpened();
	    ZonedDateTime openedDateTime = ZonedDateTime.of(
		    LocalDateTime.of(openedDate, LocalTime.MIN),
		    ZoneId.systemDefault());
	    String openedDateTimeStr = openedDateTime.format(DATE_OPENED_FORMAT_BOOK);
	    opened.setTsDate(openedDateTimeStr);
	    invc.setInvoiceOpened(opened);
	}
	
	// owner (job)
	{
	    GncV2.GncBook.GncGncInvoice.InvoiceOwner jobRef = fact.createGncV2GncBookGncGncInvoiceInvoiceOwner();
	    jobRef.setOwnerType(GCshOwner.Type.JOB.getCode());
	    jobRef.setVersion(Const.XML_FORMAT_VERSION);
	    {
		OwnerId ownerIdRef = fact.createOwnerId();
		ownerIdRef.setType(Const.XML_DATA_TYPE_GUID);
		ownerIdRef.setValue(job.getID().toString());
		jobRef.setOwnerId(ownerIdRef);
	    }
	    invc.setInvoiceOwner(jobRef);
	}
	
	if ( postInvoice ) {
	    LOGGER.debug("createJobInvoice_int: Posting job invoice " + invcGUID + "...");
	    postJobInvoice_int(file, fact,
	                       invc, invcGUID, number, 
	                       job, 
                               incExpAcct, recvblPayblAcct, 
		               new FixedPointNumber(0),
                               postDate, dueDate);
	} else {
	    LOGGER.debug("createJobInvoice_int: NOT posting job invoice " + invcGUID);
	}
	
	invc.setVersion(Const.XML_FORMAT_VERSION);

	file.getRootElement().getGncBook().getBookElements().add(invc);
	file.setModified(true);
	
	return invc;
    }

    
    // ---------------------------------------------------------------

    public void postCustomerInvoice(
	    final GnucashWritableFile file,
	    GnucashWritableCustomerInvoice invc,
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct, 
	    final GnucashAccount receivableAcct, 
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	LOGGER.debug("postCustomerInvoice: Posting customer invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getInvcAmountWithTaxes();
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
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	LOGGER.debug("postVendorBill: Posting vendor bill " + bll.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = bll.getBillAmountWithTaxes();
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
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	LOGGER.debug("postEmployeeVoucher: Posting employee voucher " + vch.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = vch.getBillAmountWithTaxes();
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
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	LOGGER.debug("postJobInvoice: Posting job invoice " + invc.getID() + "...");
	
	ObjectFactory fact = ((GnucashWritableFileImpl) file).getObjectFactory();
	
	FixedPointNumber amount = invc.getJobAmountWithTaxes();
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
	    GncV2.GncBook.GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnucashCustomer cust,
	    final GnucashAccountImpl incomeAcct, 
	    final GnucashAccountImpl receivableAcct,
	    final FixedPointNumber amount,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	// post account
	{
	    GncV2.GncBook.GncGncInvoice.InvoicePostacc postAcct = fact.createGncV2GncBookGncGncInvoiceInvoicePostacc();
	    postAcct.setType(Const.XML_DATA_TYPE_GUID);
	    postAcct.setValue(receivableAcct.getID().toString());
	    invcRef.setInvoicePostacc(postAcct);
	}
	
	// date posted
	{
	    GncV2.GncBook.GncGncInvoice.InvoicePosted posted = fact.createGncV2GncBookGncGncInvoiceInvoicePosted();
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
	    GncV2.GncBook.GncGncInvoice.InvoicePostlot postLotRef = fact.createGncV2GncBookGncGncInvoiceInvoicePostlot();
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
	    GncV2.GncBook.GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncV2GncBookGncGncInvoiceInvoicePosttxn();
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
            GncV2.GncBook.GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnucashVendor vend,
            final GnucashAccountImpl expensesAcct, 
            final GnucashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // post account
        {
            GncV2.GncBook.GncGncInvoice.InvoicePostacc postAcct = fact.createGncV2GncBookGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(payableAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncV2.GncBook.GncGncInvoice.InvoicePosted posted = fact.createGncV2GncBookGncGncInvoiceInvoicePosted();
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
            GncV2.GncBook.GncGncInvoice.InvoicePostlot postLotRef = fact.createGncV2GncBookGncGncInvoiceInvoicePostlot();
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
            GncV2.GncBook.GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncV2GncBookGncGncInvoiceInvoicePosttxn();
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
            GncV2.GncBook.GncGncInvoice invcRef,
            final GCshID invcGUID, String invcNumber,
	    final GnucashEmployee empl,
            final GnucashAccountImpl expensesAcct, 
            final GnucashAccountImpl payableAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // post account
        {
            GncV2.GncBook.GncGncInvoice.InvoicePostacc postAcct = fact.createGncV2GncBookGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(payableAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncV2.GncBook.GncGncInvoice.InvoicePosted posted = fact.createGncV2GncBookGncGncInvoiceInvoicePosted();
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
            GncV2.GncBook.GncGncInvoice.InvoicePostlot postLotRef = fact.createGncV2GncBookGncGncInvoiceInvoicePostlot();
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
            GncV2.GncBook.GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncV2GncBookGncGncInvoiceInvoicePosttxn();
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
            GncV2.GncBook.GncGncInvoice invcRef,
	    final GCshID invcGUID, String invcNumber,
	    final GnucashGenerJob job,
            final GnucashAccountImpl incExpAcct, 
            final GnucashAccountImpl recvblPayblAcct, 
	    final FixedPointNumber amount,
            final LocalDate postDate,
            final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
        // post account
        {
            GncV2.GncBook.GncGncInvoice.InvoicePostacc postAcct = fact.createGncV2GncBookGncGncInvoiceInvoicePostacc();
            postAcct.setType(Const.XML_DATA_TYPE_GUID);
            postAcct.setValue(recvblPayblAcct.getID().toString());
            invcRef.setInvoicePostacc(postAcct);
        }
        
        // date posted
        {
            GncV2.GncBook.GncGncInvoice.InvoicePosted posted = fact.createGncV2GncBookGncGncInvoiceInvoicePosted();
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
            GncV2.GncBook.GncGncInvoice.InvoicePostlot postLotRef = fact.createGncV2GncBookGncGncInvoiceInvoicePostlot();
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
            GncV2.GncBook.GncGncInvoice.InvoicePosttxn postTrxRef = fact.createGncV2GncBookGncGncInvoiceInvoicePosttxn();
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
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
	    final LocalDate dueDate) throws WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	    slot.setSlotKey("trans-txn-type");
	    value.setType(Const.XML_DATA_TYPE_STRING);
	    value.getContent().add(GnucashTransaction.Type.INVOICE.getCode());

	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}

	// add trans-date-due -slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey("trans-date-due");
	    value.setType("timespec");
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
	    slot.setSlotKey("trans-read-only");
	    value.setType(Const.XML_DATA_TYPE_STRING);
	    value.getContent().add(Const.getLocaleString("INVC_READ_ONLY_SLOT_TEXT"));
	    
	    slot.setSlotValue(value);
	    slots.getSlot().add(slot);
	}

	// add invoice-slot
	{
	    Slot slot = factory.createSlot();
	    SlotValue value = factory.createSlotValue();
	    slot.setSlotKey("gncInvoice");
	    value.setType("frame");
	    {
		Slot subslot = factory.createSlot();
		SlotValue subvalue = factory.createSlotValue();

		subslot.setSlotKey("invoice-guid");
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
	    final GnucashCustomer cust) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	    final GnucashVendor vend) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	    final GnucashEmployee empl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	    final GnucashGenerJob job) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	    final GCshID ownerID) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
	    slot.setSlotKey("gncInvoice");
	    value.setType("frame");
	    {
		Slot subslot = factory.createSlot();
		SlotValue subvalue = factory.createSlotValue();

		subslot.setSlotKey("invoice-guid");
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeInvcEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeBillEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeVoucherEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see #addInvcEntry(GnucashGenerInvoiceEntryImpl)
     */
    protected void removeJobEntry(final GnucashWritableGenerInvoiceEntryImpl impl)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
     * ${@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     */
    public void addRawGenerEntry(final GnucashWritableGenerInvoiceEntryImpl generEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException {
//	System.err.println("GnucashWritableGenerInvoiceImpl.addRawGenerEntry " + generEntr.toString());

	if (!isModifiable()) {
	    throw new IllegalArgumentException("This invoice/bill has payments and thus is not modifiable");
	}

	super.addGenerEntry(generEntr);
    }

    /**
     * Called by
     * ${@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void addInvcEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addInvcEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = generInvcEntr.isInvcTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getInvcSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getInvcSumInclTaxes();
	
	GCshID postAcctID = getInvcPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if (generInvcEntr.isInvcTaxable()) {
	    taxTab = generInvcEntr.getInvcTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (its i-taxtable-id is '"
			+ generInvcEntr.getJwsdpPeer().getEntryITaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    /**
     * Called by
     * ${@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void addBillEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addBillEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money to the tax-account

	boolean isTaxable = generInvcEntr.isBillTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getBillSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getBillSumInclTaxes();
	
	GCshID postAcctID = getBillPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if (generInvcEntr.isBillTaxable()) {
	    taxTab = generInvcEntr.getBillTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (its b-taxtable-id is '"
			+ generInvcEntr.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    /**
     * Called by
     * ${@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void addVoucherEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addVoucherEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money to the tax-account

	boolean isTaxable = generInvcEntr.isVoucherTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getVoucherSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getVoucherSumInclTaxes();
	
	GCshID postAcctID = getVoucherPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if (generInvcEntr.isVoucherTaxable()) {
	    taxTab = generInvcEntr.getVoucherTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (its b-taxtable-id is '"
			+ generInvcEntr.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    /**
     * Called by
     * ${@link GnucashWritableGenerInvoiceEntryImpl#createCustInvoiceEntry_int(GnucashWritableGenerInvoiceImpl, GnucashAccount, FixedPointNumber, FixedPointNumber)}.
     *
     * @param generInvcEntr the entry to add to our internal list of invoice-entries
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void addJobEntry(final GnucashWritableGenerInvoiceEntryImpl generInvcEntr)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.addJobEntry " + generInvcEntr.toString());

	addRawGenerEntry(generInvcEntr);

	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from/to the tax-account

	boolean isTaxable = generInvcEntr.isJobTaxable();
	FixedPointNumber sumExclTaxes = generInvcEntr.getJobSumExclTaxes();
	FixedPointNumber sumInclTaxes = generInvcEntr.getJobSumInclTaxes();
	
	GCshID postAcctID = getJobPostAccountID(generInvcEntr);

	GCshTaxTable taxTab = null;

	if (generInvcEntr.isJobTaxable()) {
	    taxTab = generInvcEntr.getJobTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (its b/i-taxtable-id is '"
			+ generInvcEntr.getJwsdpPeer().getEntryBTaxtable().getValue() + "' and " 
			+ generInvcEntr.getJwsdpPeer().getEntryITaxtable().getValue() + "' resp.)");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    // ---------------------------------------------------------------

    protected void subtractInvcEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.CUSTOMER &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractInvcEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isInvcTaxable();
	FixedPointNumber sumExclTaxes = entry.getInvcSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getInvcSumInclTaxes().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryIAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isInvcTaxable()) {
	    taxTab = entry.getInvcTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given customer invoice entry has no i-tax-table (its i-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryITaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    protected void subtractBillEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.VENDOR &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractBillEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isBillTaxable();
	FixedPointNumber sumExclTaxes = entry.getBillSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getBillSumInclTaxes().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isBillTaxable()) {
	    taxTab = entry.getBillTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given vendor bill entry has no b-tax-table (its b-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    protected void subtractVoucherEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.EMPLOYEE &&
	     getType() != GCshOwner.Type.JOB ) // ::CHECK
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractVoucherEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfer the money from the tax-account

	boolean isTaxable = entry.isVoucherTaxable();
	FixedPointNumber sumExclTaxes = entry.getVoucherSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getVoucherSumInclTaxes().negate();
	
	GCshID postAcctID = new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isVoucherTaxable()) {
	    taxTab = entry.getVoucherTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given employee voucher entry has no b-tax-table (its b-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "')");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    protected void subtractJobEntry(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GCshOwner.Type.JOB )
	    throw new WrongInvoiceTypeException();
	
//	System.err.println("GnucashWritableGenerInvoiceImpl.subtractJobEntry " + entry.toString());
	// ==============================================================
	// update or add split in PostTransaction
	// that transfers the money from the tax-account

	boolean isTaxable = entry.isJobTaxable();
	FixedPointNumber sumExclTaxes = entry.getJobSumExclTaxes().negate();
	FixedPointNumber sumInclTaxes = entry.getJobSumInclTaxes().negate();
	
	GCshID postAcctID = new GCshID();
	if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.CUSTOMER )
	    postAcctID.set(entry.getJwsdpPeer().getEntryIAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.VENDOR )
	    postAcctID.set(entry.getJwsdpPeer().getEntryBAcct().getValue());
	else if ( entry.getGenerInvoice().getOwnerType(GnucashGenerInvoice.ReadVariant.VIA_JOB) == GCshOwner.Type.EMPLOYEE )
	    postAcctID.set(entry.getJwsdpPeer().getEntryBAcct().getValue());

	GCshTaxTable taxTab = null;

	if (entry.isJobTaxable()) {
	    taxTab = entry.getJobTaxTable();
	    if (taxTab == null) {
		throw new IllegalArgumentException("The given job invoice entry has no b/i-tax-table (its b/i-taxtable-id is '"
			+ entry.getJwsdpPeer().getEntryBTaxtable().getValue() + "' and '" 
			+ entry.getJwsdpPeer().getEntryITaxtable().getValue() + "' resp.)");
	    }
	}

	updateEntry(taxTab, isTaxable, sumExclTaxes, sumInclTaxes, postAcctID);
	getFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     */
    protected GCshID getInvcPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
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
    protected GCshID getBillPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
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
    protected GCshID getVoucherPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException {
	if ( getType() != GnucashGenerInvoice.TYPE_EMPLOYEE &&
	     getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();
	
	return new GCshID(entry.getJwsdpPeer().getEntryBAcct().getValue());
    }

    /**
     * @return the AccountID of the Account to transfer the money from
     * @throws WrongInvoiceTypeException
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    protected GCshID getJobPostAccountID(final GnucashGenerInvoiceEntryImpl entry)
	    throws WrongInvoiceTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if ( getType() != GnucashGenerInvoice.TYPE_JOB )
	    throw new WrongInvoiceTypeException();

	    GnucashJobInvoice jobInvc = new GnucashJobInvoiceImpl(this);
	    if ( jobInvc.getType() == GnucashGenerJob.TYPE_CUSTOMER )
		return getInvcPostAccountID(entry);
	    else if ( jobInvc.getType() == GnucashGenerJob.TYPE_VENDOR )
		return getBillPostAccountID(entry);
	    
	    return null; // Compiler happy
    }

    // ---------------------------------------------------------------

    /**
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    private void updateEntry(
	    final GCshTaxTable taxTab, 
	    final boolean isTaxable, 
	    final FixedPointNumber sumExclTaxes,
	    final FixedPointNumber sumInclTaxes, 
	    final GCshID postAcctID)
	    throws InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
	getFile().setModified(true);
    }

    private void updateEntry_taxStuff(
	    final GCshTaxTable taxtable, 
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID postAcctID, 
	    GnucashWritableTransactionImpl postTrx) throws InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
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
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    private void updateNonTaxableEntry(
	    final FixedPointNumber sumExclTaxes, 
	    final FixedPointNumber sumInclTaxes,
	    final GCshID accountToTransferMoneyFrom) throws InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

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
		    	.createWritableSplit(getFile().getAccountByID(accountToTransferMoneyTo));
	    split.setQuantity(sumInclTaxes);
	    split.setValue(sumInclTaxes);
	    split.setAction(GnucashTransactionSplit.Action.INVOICE);

	    // this split must have a reference to the lot
	    // as has the transaction-split of the whole sum in the
	    // transaction when the invoice is Paid
	    GncTransaction.TrnSplits.TrnSplit.SplitLot lotref = 
		    ((GnucashFileImpl) getFile()).getObjectFactory()
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
			    getFile().getAccountByID(accountToTransferMoneyFrom));
	    split.setQuantity(((FixedPointNumber) sumExclTaxes.clone()).negate());
	    split.setValue(((FixedPointNumber) sumExclTaxes.clone()).negate());
	}

	assert postTransaction.isBalanced();
	getFile().setModified(true);
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
	getFile().setModified(true);
    }

    /**
     * @throws WrongInvoiceTypeException
     */
    public void setVendor(final GnucashVendor vend) throws WrongInvoiceTypeException {
	if ( getType() != GCshOwner.Type.VENDOR )
	    throw new WrongInvoiceTypeException();

	attemptChange();
	getJwsdpPeer().getInvoiceOwner().getOwnerId().setValue(vend.getID().toString());
	getFile().setModified(true);
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
	getFile().setModified(true);
    }

    // -----------------------------------------------------------

    /**
     * @see GnucashWritableGenerInvoice#setDateOpened(LocalDateTime)
     */
    public void setDateOpened(final LocalDate d) {
	attemptChange();
	dateOpened = ZonedDateTime.of(d, LocalTime.MIN, ZoneId.systemDefault());
	String dateOpenedStr = dateOpened.format(DATE_OPENED_FORMAT_BOOK);
	getJwsdpPeer().getInvoiceOpened().setTsDate(dateOpenedStr);
	getFile().setModified(true);
    }

    /**
     * @see GnucashWritableGenerInvoice#setDateOpened(java.lang.String)
     */
    public void setDateOpened(final String d) throws java.text.ParseException {
	attemptChange();
	setDateOpened(LocalDate.parse(d, DATE_OPENED_FORMAT));
	getFile().setModified(true);
    }

    /**
     * @see GnucashWritableGenerInvoice#setDatePosted(LocalDateTime)
     */
    public void setDatePosted(final LocalDate d) {
	attemptChange();
	datePosted = ZonedDateTime.of(d, LocalTime.MIN, ZoneId.systemDefault());
	getJwsdpPeer().getInvoicePosted().setTsDate(DATE_OPENED_FORMAT.format(d));
	getFile().setModified(true);

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
	setDatePosted(LocalDate.parse(d, DATE_OPENED_FORMAT));
    }
    
    // ---------------------------------------------------------------

    public void setNumber(final String number) {
	attemptChange();
	getJwsdpPeer().setInvoiceId(number);
	getFile().setModified(true);
    }

    public void setDescription(final String descr) {
	attemptChange();
	getJwsdpPeer().setInvoiceNotes(descr);
	getFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashGenerInvoice#getPayingTransactions()
     */
    public Collection<GnucashWritableTransaction> getWritablePayingTransactions() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	Collection<GnucashWritableTransaction> trxList = new LinkedList<GnucashWritableTransaction>();

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
	GncV2.GncBook.GncGncInvoice.InvoicePosttxn invoicePosttxn = jwsdpPeer.getInvoicePosttxn();
	if (invoicePosttxn == null) {
	    return null; // invoice may not be posted
	}
	
	GCshID invcPostTrxID = new GCshID( invoicePosttxn.getValue() );
	return getFile().getTransactionByID(invcPostTrxID);
    }

    /**
     * @see GnucashWritableGenerInvoice#getWritableGenerEntryByID(java.lang.String)
     */
    public GnucashWritableGenerInvoiceEntry getWritableGenerEntryByID(final GCshID id) {
	return new GnucashWritableGenerInvoiceEntryImpl(super.getGenerEntryByID(id));
    }

    /**
     * @throws WrongInvoiceTypeException
     * @throws TaxTableNotFoundException
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @see GnucashWritableGenerInvoice#remove()
     */
    public void remove() throws WrongInvoiceTypeException, TaxTableNotFoundException, InvalidCmdtyCurrTypeException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if (!isModifiable()) {
	    throw new IllegalStateException("Invoice has payments and cannot be deleted!");
	}

	// we copy the list because element.remove() modifies it
	Collection<GnucashGenerInvoiceEntry> entries2 = new LinkedList<GnucashGenerInvoiceEntry>();
	entries2.addAll(this.getGenerEntries());
	for (GnucashGenerInvoiceEntry element : entries2) {
	    ((GnucashWritableGenerInvoiceEntry) element).remove();
	}

	GnucashWritableTransaction post = (GnucashWritableTransaction) getPostTransaction();
	if (post != null) {
	    post.remove();
	}

	((GnucashWritableFileImpl) getFile()).removeInvoice(this);

    }

    @Override
    public void setEmployee(GnucashEmployee empl) throws WrongInvoiceTypeException {
	// TODO Auto-generated method stub
	
    }

}
