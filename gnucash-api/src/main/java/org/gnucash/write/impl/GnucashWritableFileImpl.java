package org.gnucash.write.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.gnucash.Const;
import org.gnucash.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncAccount;
import org.gnucash.generated.GncBudget;
import org.gnucash.generated.GncCountData;
import org.gnucash.generated.GncTransaction;
import org.gnucash.generated.GncV2;
import org.gnucash.generated.Slot;
import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashAccount;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashFile;
import org.gnucash.read.GnucashGenerInvoice;
import org.gnucash.read.GnucashGenerInvoiceEntry;
import org.gnucash.read.GnucashGenerJob;
import org.gnucash.read.GnucashTransaction;
import org.gnucash.read.GnucashVendor;
import org.gnucash.read.IllegalTransactionSplitActionException;
import org.gnucash.read.NoEntryFoundException;
import org.gnucash.read.TooManyEntriesFoundException;
import org.gnucash.read.UnknownAccountTypeException;
import org.gnucash.read.aux.GCshPrice;
import org.gnucash.read.aux.GCshTaxTable;
import org.gnucash.read.impl.GnucashAccountImpl;
import org.gnucash.read.impl.GnucashCustomerImpl;
import org.gnucash.read.impl.GnucashEmployeeImpl;
import org.gnucash.read.impl.GnucashFileImpl;
import org.gnucash.read.impl.GnucashTransactionImpl;
import org.gnucash.read.impl.GnucashVendorImpl;
import org.gnucash.read.impl.aux.GCshTaxTableImpl;
import org.gnucash.read.impl.aux.WrongOwnerTypeException;
import org.gnucash.read.impl.hlp.FileAccountManager;
import org.gnucash.read.impl.spec.GnucashCustomerJobImpl;
import org.gnucash.read.spec.WrongInvoiceTypeException;
import org.gnucash.write.GnucashWritableAccount;
import org.gnucash.write.GnucashWritableCommodity;
import org.gnucash.write.GnucashWritableCustomer;
import org.gnucash.write.GnucashWritableEmployee;
import org.gnucash.write.GnucashWritableFile;
import org.gnucash.write.GnucashWritableGenerInvoice;
import org.gnucash.write.GnucashWritableGenerJob;
import org.gnucash.write.GnucashWritableTransaction;
import org.gnucash.write.GnucashWritableTransactionSplit;
import org.gnucash.write.GnucashWritableVendor;
import org.gnucash.write.impl.spec.GnucashWritableCustomerInvoiceImpl;
import org.gnucash.write.impl.spec.GnucashWritableCustomerJobImpl;
import org.gnucash.write.impl.spec.GnucashWritableEmployeeVoucherImpl;
import org.gnucash.write.impl.spec.GnucashWritableJobInvoiceImpl;
import org.gnucash.write.impl.spec.GnucashWritableVendorBillImpl;
import org.gnucash.write.impl.spec.GnucashWritableVendorJobImpl;
import org.gnucash.write.spec.GnucashWritableCustomerInvoice;
import org.gnucash.write.spec.GnucashWritableCustomerJob;
import org.gnucash.write.spec.GnucashWritableEmployeeVoucher;
import org.gnucash.write.spec.GnucashWritableJobInvoice;
import org.gnucash.write.spec.GnucashWritableVendorBill;
import org.gnucash.write.spec.GnucashWritableVendorJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

/**
 * Implementation of GnucashWritableFile based on GnucashFileImpl.
 * @see GnucashFileImpl
 */
public class GnucashWritableFileImpl extends GnucashFileImpl 
                                     implements GnucashWritableFile 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableFileImpl.class);

    // ::MAGIC
    private static final int    HEX      = 16;
    private static final String CODEPAGE = "UTF-8";

    // ---------------------------------------------------------------

    /**
     * true if this file has been modified.
     */
    private boolean modified = false;

    /**
     * @see {@link #getLastWriteTime()}
     */
    private long lastWriteTime = 0;

    // ---------------------------------------------------------------

    /**
     * @param file the file to load
     * @throws IOException on bsic io-problems such as a FileNotFoundException
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     */
    public GnucashWritableFileImpl(final File file) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super(file);
	setModified(false);
    }

    public GnucashWritableFileImpl(final InputStream is) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super(is);
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public GnucashWritableFile getWritableGnucashFile() {
	return this;
    }

    // ---------------------------------------------------------------

    /**
     * @return true if this file has been modified
     */
    public boolean isModified() {
	return modified;
    }

    /**
     * @return the time in ms (compatible with File.lastModified) of the last
     *         write-operation
     */
    public long getLastWriteTime() {
	return lastWriteTime;
    }

    /**
     * @param pModified true if this file has been modified false after save, load
     *                  or undo of changes
     */
    public void setModified(final boolean pModified) {
	// boolean old = this.modified;
	modified = pModified;
	// if (propertyChange != null)
	// propertyChange.firePropertyChange("modified", old, pModified);
    }

    /**
     * Keep the count-data up to date. The count-data is re-calculated on the fly
     * before writing but we like to keep our internal model up-to-date just to be
     * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type the type to set it for
     */
    protected void incrementCountDataFor(final String type) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
		gncCountData.setValue(gncCountData.getValue() + 1);
		setModified(true);
	    }
	}
    }

    /**
     * Keep the count-data up to date. The count-data is re-calculated on the fly
     * before writing but we like to keep our internal model up-to-date just to be
     * defensive. <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type the type to set it for
     */
    protected void decrementCountDataFor(final String type) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (Iterator<GncCountData> iter = l.iterator(); iter.hasNext();) {
	    GncCountData gncCountData = (GncCountData) iter.next();

	    if (type.equals(gncCountData.getCdType())) {
		gncCountData.setValue(gncCountData.getValue() - 1);
		setModified(true);
	    }
	}
    }

    /**
     * keep the count-data up to date.
     * <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     *
     * @param type  the type to set it for
     * @param count the value
     */
    protected void setCountDataFor(final String type, final int count) {

	if (type == null) {
	    throw new IllegalArgumentException("null type given");
	}

	List<GncCountData> l = getRootElement().getGncBook().getGncCountData();
	for (GncCountData gncCountData : l) {
	    if (type.equals(gncCountData.getCdType())) {
		gncCountData.setValue(count);
		setModified(true);
	    }
	}
    }

    /**
     * Calculate and set the correct valued for all the following count-data.<br/>
     * Also check the that only valid elements are in the book-element and that they
     * have the correct order.
     * <p>
     * <gnc:count-data cd:type="commodity">2</gnc:count-data>
     * <gnc:count-data cd:type="account">394</gnc:count-data>
     * <gnc:count-data cd:type="transaction">1576</gnc:count-data>
     * <gnc:count-data cd:type="schedxaction">4</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncCustomer">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncJob">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncTaxTable">2</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncInvoice">5</gnc:count-data>
     * <gnc:count-data cd:type="gnc:GncEntry">18</gnc:count-data>
     */
    private void checkAllCountData() {
    
        int cntCommodity = 0;
        int cntAccount = 0;
        int cntTransaction = 0;
        int cntCustomer = 0;
        int cntVendor = 0;
        int cntJob = 0;
        int cntTaxTable = 0;
        int cntInvoice = 0;
        int cntIncEntry = 0;
        int cntBillTerm = 0;
        
        /**
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GncTemplateTransactions} {@link GncGncInvoice} {@link GncGncEntry}
         * {@link GncGncJob} {@link GncGncTaxTable} {@link GncCommodity}
         * {@link GncGncCustomer} {@link GncSchedxaction} {@link GncBudget}
         * {@link GncAccount} {@link GncPricedb} {@link GncTransaction}
         */
        List<Object> bookElements = getRootElement().getGncBook().getBookElements();
        for (Object element : bookElements) {
            if (element instanceof GncV2.GncBook.GncCommodity) {
        	cntCommodity++;
            } else if (element instanceof GncAccount) {
        	cntAccount++;
            } else if (element instanceof GncTransaction) {
        	cntTransaction++;
            } else if (element instanceof GncV2.GncBook.GncGncCustomer) {
        	cntCustomer++;
            } else if (element instanceof GncV2.GncBook.GncGncVendor) {
        	cntVendor++;
            } else if (element instanceof GncV2.GncBook.GncGncJob) {
        	cntJob++;
            } else if (element instanceof GncV2.GncBook.GncGncTaxTable) {
        	cntTaxTable++;
            } else if (element instanceof GncV2.GncBook.GncGncInvoice) {
        	cntInvoice++;
            } else if (element instanceof GncV2.GncBook.GncGncEntry) {
        	cntIncEntry++;
            } else if (element instanceof GncV2.GncBook.GncTemplateTransactions) {
        	// ::TODO
            } else if (element instanceof GncV2.GncBook.GncSchedxaction) {
        	// ::TODO
            } else if (element instanceof GncBudget) {
        	// ::TODO
            } else if (element instanceof GncV2.GncBook.GncPricedb) {
        	// ::TODO
            } else if (element instanceof GncV2.GncBook.GncGncEmployee) {
        	// ::TODO
            } else if (element instanceof GncV2.GncBook.GncGncBillTerm) {
        	cntBillTerm++;
            } else {
        	throw new IllegalStateException("Unecpected element in GNC:Book found! <" + element.toString() + ">");
            }
        }
    
        setCountDataFor("commodity", cntCommodity);
        setCountDataFor("account", cntAccount);
        setCountDataFor("transaction", cntTransaction);
        setCountDataFor("gnc:GncCustomer", cntCustomer);
        setCountDataFor("gnc:GncVendor", cntVendor);
        setCountDataFor("gnc:GncJob", cntJob);
        setCountDataFor("gnc:GncTaxTable", cntTaxTable);
        setCountDataFor("gnc:GncInvoice", cntInvoice);
        setCountDataFor("gnc:GncEntry", cntIncEntry);
        setCountDataFor("gnc:GncBillTerm", cntBillTerm);
        
        // make sure the correct sort-order of the entity-types is obeyed in writing.
        // (we do not enforce this in the xml-schema to allow for reading out of order
        // files)
        java.util.Collections.sort(bookElements, new BookElementsSorter());
    }

    // ---------------------------------------------------------------

    /**
     * @return all TaxTables defined in the book
     * @see {@link GCshTaxTable}
     */
    @Override
    public Collection<GCshTaxTable> getTaxTables() {
	if (taxTablesById == null) {

	    taxTablesById = new HashMap<GCshID, GCshTaxTable>();
	    List<Object> bookElements = this.getRootElement().getGncBook().getBookElements();
	    for (Object bookElement : bookElements) {
		if (bookElement instanceof GncV2.GncBook.GncGncTaxTable) {
		    GncV2.GncBook.GncGncTaxTable jwsdpPeer = (GncV2.GncBook.GncGncTaxTable) bookElement;
		    GCshTaxTableImpl gnucashTaxTable = new GCshTaxTableImpl(jwsdpPeer, this);
		    taxTablesById.put(gnucashTaxTable.getId(), gnucashTaxTable);
		}
	    }
	}

	return taxTablesById.values();
    }

    /**
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see {@link GnucashFileImpl#loadFile(java.io.File)}
     */
    @Override
    protected void loadFile(final File pFile) throws IOException, InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super.loadFile(pFile);
	lastWriteTime = Math.max(pFile.lastModified(), System.currentTimeMillis());
    }

    /**
     * @see GnucashWritableFile#writeFile(java.io.File)
     */
    public void writeFile(final File file) throws IOException {

	if (file == null) {
	    throw new IllegalArgumentException("null not allowed for field this file");
	}

	if (file.exists()) {
	    throw new IllegalArgumentException("Given file '" + file.getAbsolutePath() + "' does exist!");
	}

	checkAllCountData();

	setFile(file);

	OutputStream out = new FileOutputStream(file);
	out = new BufferedOutputStream(out);
	if (file.getName().endsWith(".gz")) {
	    out = new GZIPOutputStream(out);
	}

	Writer writer = new NamespaceAdderWriter(new OutputStreamWriter(out, CODEPAGE));
	try {
	    JAXBContext context = getJAXBContext();
	    Marshaller marsh = context.createMarshaller();

	    // marsh.marshal(getRootElement(), writer);
	    // marsh.marshal(getRootElement(), new PrintWriter( System.out ) );
	    marsh.marshal(getRootElement(), new WritingContentHandler(writer));

	    setModified(false);
	} catch (JAXBException e) {
	    LOGGER.error(e.getMessage(), e);
	}
	finally {
	    writer.close();
	}
	
	out.close();
	
	lastWriteTime = Math.max(file.lastModified(), System.currentTimeMillis());
    }

    
    
    // ---------------------------------------------------------------

    /**
     * @return the underlying JAXB-element
     * @see GnucashWritableFile#getRootElement()
     */
    @SuppressWarnings("exports")
    @Override
    public GncV2 getRootElement() {
	return super.getRootElement();
    }

    /**
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws ClassNotFoundException 
     * @throws NoSuchFieldException 
     * @see GnucashFileImpl#setRootElement(GncV2)
     */
    @SuppressWarnings("exports")
    @Override
    public void setRootElement(final GncV2 rootElement) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException, NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
	super.setRootElement(rootElement);
    }

    // ---------------------------------------------------------------

    /**
     * create a GUID for a new element. (guids are globally unique and not tied to a
     * specific kind of entity)
     * 
     * ::TODO: Change implementation: use Apache commons UUID class.
     *
     * @return the new gnucash-guid
     */
    public String createGUID() {

	int len = "74e492edf60d6a28b6c1d01cc410c058".length();

	StringBuffer sb = new StringBuffer(Long.toHexString(System.currentTimeMillis()));

	while (sb.length() < len) {
	    sb.append(Integer.toHexString((int) (Math.random() * HEX)).charAt(0));
	}

	return sb.toString();
    }

    /**
     */
    protected GncTransaction createGncTransaction() {
	GncTransaction retval = getObjectFactory().createGncTransaction();
	incrementCountDataFor("transaction");
	return retval;
    }

    /**
     */
    protected GncTransaction.TrnSplits.TrnSplit createGncTransactionTypeTrnSplitsTypeTrnSplitType() {
	GncTransaction.TrnSplits.TrnSplit retval = getObjectFactory().createGncTransactionTrnSplitsTrnSplit();
	// incrementCountDataFor();
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncInvoice createGncGncInvoiceType() {
	GncV2.GncBook.GncGncInvoice retval = getObjectFactory().createGncV2GncBookGncGncInvoice();
	incrementCountDataFor("gnc:GncInvoice");
	return retval;
    }

    /**
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncEntry createGncGncEntryType() {
	GncV2.GncBook.GncGncEntry retval = getObjectFactory().createGncV2GncBookGncGncEntry();
	incrementCountDataFor("gnc:GncEntry");
	return retval;
    }

    // ----------------------------

    /**
     */
    protected GncV2.GncBook.GncGncCustomer createGncGncCustomerType() {
	GncV2.GncBook.GncGncCustomer retval = getObjectFactory().createGncV2GncBookGncGncCustomer();
	incrementCountDataFor("gnc:GncCustomer");
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncVendor createGncGncVendorType() {
	GncV2.GncBook.GncGncVendor retval = getObjectFactory().createGncV2GncBookGncGncVendor();
	incrementCountDataFor("gnc:GncVendor");
	return retval;
    }

    /**
     */
    protected GncV2.GncBook.GncGncEmployee createGncGncEmployeeType() {
	GncV2.GncBook.GncGncEmployee retval = getObjectFactory().createGncV2GncBookGncGncEmployee();
	incrementCountDataFor("gnc:GncEmployee");
	return retval;
    }
    
    // ----------------------------

    /**
     * @return the jaxb-job
     */
    @SuppressWarnings("exports")
    public GncV2.GncBook.GncGncJob createGncGncJobType() {
	GncV2.GncBook.GncGncJob retval = getObjectFactory().createGncV2GncBookGncGncJob();
	incrementCountDataFor("gnc:GncJob");
	return retval;
    }

    protected GncV2.GncBook.GncCommodity createGncGncCommodityType() {
	GncV2.GncBook.GncCommodity retval = getObjectFactory().createGncV2GncBookGncCommodity();
	incrementCountDataFor("gnc:GncCommodity");
	return retval;
    }
    
    // ---------------------------------------------------------------

    /**
     * @see GnucashFile#getCustomerByID(java.lang.String)
     */
    @Override
    public GnucashWritableCustomer getCustomerByID(final GCshID custID) {
	GnucashCustomer cust = super.getCustomerByID(custID);
	return new GnucashWritableCustomerImpl((GnucashCustomerImpl) cust);
    }

    /**
     * @see GnucashFile#getCustomerByID(java.lang.String)
     */
    @Override
    public GnucashWritableVendor getVendorByID(final GCshID vendID) {
	GnucashVendor vend = super.getVendorByID(vendID);
	return new GnucashWritableVendorImpl((GnucashVendorImpl) vend);
    }

    /**
     * @see GnucashFile#getCustomerByID(java.lang.String)
     */
    @Override
    public GnucashWritableEmployee getEmployeeByID(final GCshID emplID) {
	GnucashEmployee empl = super.getEmployeeByID(emplID);
	return new GnucashWritableEmployeeImpl((GnucashEmployeeImpl) empl);
    }

    // ---------------------------------------------------------------

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see FileAccountManager#createAccount(GncAccount)
     */
    protected GnucashAccount createAccount(final GncAccount jwsdpAccount) {
	GnucashAccount account = new GnucashWritableAccountImpl(jwsdpAccount, this);
	return account;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see GnucashFileImpl#createGenerInvoice(GncV2.GncBook.GncGncInvoice)
     */
    protected GnucashGenerInvoice createGenerInvoice(final GncV2.GncBook.GncGncInvoice jwsdpInvoice) {
	GnucashGenerInvoice invoice = new GnucashWritableGenerInvoiceImpl(jwsdpInvoice, this);
	return invoice;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @param jwsdpInvcEntr the xml-object to represent in the entry.
     * @return a new invoice-entry, already registred with this file.
     * @see GnucashFileImpl#createGenerInvoiceEntry(GncV2.GncBook.GncGncEntry)
     */
    @Override
    protected GnucashGenerInvoiceEntry createGenerInvoiceEntry(final GncV2.GncBook.GncGncEntry jwsdpInvcEntr) {
	GnucashGenerInvoiceEntry entry = new GnucashWritableGenerInvoiceEntryImpl(jwsdpInvcEntr, this);
	return entry;
    }

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see GnucashFileImpl#createGenerJob(GncV2.GncBook.GncGncJob)
     */
    @Override
    protected GnucashCustomerJobImpl createGenerJob(final GncV2.GncBook.GncGncJob jwsdpJob) {
	GnucashCustomerJobImpl job = new GnucashWritableCustomerJobImpl(jwsdpJob, this);
	return job;
    }
    
    // ----------------------------

    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see GnucashFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    protected GnucashCustomerImpl createCustomer(final GncV2.GncBook.GncGncCustomer jwsdpCust) {
	GnucashCustomerImpl cust = new GnucashWritableCustomerImpl(jwsdpCust, this);
	return cust;
    }

    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see GnucashFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    protected GnucashVendorImpl createVendor(final GncV2.GncBook.GncGncVendor jwsdpVend) {
	GnucashVendorImpl vend = new GnucashWritableVendorImpl(jwsdpVend, this);
	return vend;
    }

    /**
     * {@inheritDoc}
     *
     * @param jwsdpCust the jwsdp-object the customer shall wrap
     * @return the new customer
     * @see GnucashFileImpl#createCustomer(GncV2.GncBook.GncGncCustomer)
     */
    protected GnucashEmployeeImpl createEmployee(final GncV2.GncBook.GncGncEmployee jwsdpEmpl) {
	GnucashEmployeeImpl empl = new GnucashWritableEmployeeImpl(jwsdpEmpl, this);
	return empl;
    }

    // ----------------------------

    /**
     * This overridden method creates the writable version of the returned object.
     *
     * @see GnucashFileImpl#createTransaction(GncTransaction)
     */
    @Override
    protected GnucashTransactionImpl createTransaction(final GncTransaction jwsdpTrx) {
	GnucashTransactionImpl account = new GnucashWritableTransactionImpl(jwsdpTrx, this);
	return account;
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableFile#getWritableTransactions()
     */
    @SuppressWarnings("unchecked")
    public Collection<? extends GnucashWritableTransaction> getWritableTransactions() {
	return (Collection<? extends GnucashWritableTransaction>) getTransactions();
    }

    /**
     * @see GnucashWritableFile#getTransactionByID(java.lang.String)
     */
    @Override
    public GnucashWritableTransaction getTransactionByID(final GCshID id) {
	return (GnucashWritableTransaction) super.getTransactionByID(id);
    }

    // ---------------------------------------------------------------

    /**
     * Used by GnucashTransactionImpl.createTransaction to add a new Transaction to
     * this file.
     *
     * @see GnucashTransactionImpl#createSplit(GncTransaction.TrnSplits.TrnSplit)
     */
    protected void addTransaction(final GnucashTransactionImpl impl) {
	incrementCountDataFor("transaction");

	getRootElement().getGncBook().getBookElements().add(impl.getJwsdpPeer());
	setModified(true);
	transactionID2transaction.put(impl.getId(), impl);

    }

    /**
     * @param impl what to remove
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void removeTransaction(final GnucashWritableTransaction impl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	Collection<GnucashWritableTransactionSplit> c = new LinkedList<GnucashWritableTransactionSplit>();
	c.addAll(impl.getWritingSplits());
	for (GnucashWritableTransactionSplit element : c) {
	    element.remove();
	}

	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableTransactionImpl) impl).getJwsdpPeer());
	setModified(true);
	transactionID2transaction.remove(impl.getId());

    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableFile#getWritableGenerJobs()
     */
    public Collection<GnucashWritableGenerJob> getWritableGenerJobs() {

	Collection<GnucashGenerJob> jobList = getGenerJobs();
	if (jobList == null) {
	    throw new IllegalStateException("getGenerJobs() returned null");
	}
	
	Collection<GnucashWritableGenerJob> retval = new ArrayList<GnucashWritableGenerJob>(jobList.size());
	for (GnucashGenerJob job : jobList) {
	    retval.add((GnucashWritableGenerJob) job);
	}
	return retval;
    }

    /**
     * @param jobID the id of the job to fetch
     * @return A changeable version of the job or null of not found.
     * @see GnucashFile#getGenerJobByID(GCshID)
     * @see GnucashWritableFile#getGenerJobByID(GCshID)
     */
    @Override
    public GnucashWritableGenerJob getGenerJobByID(final GCshID jobID) {
	return (GnucashWritableGenerJob) super.getGenerJobByID(jobID);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#getWritableGenerJobs()
     */
    public Collection<GnucashWritableGenerInvoice> getWritableGenerInvoices() {

	Collection<GnucashGenerInvoice> invcList = getGenerInvoices();
	if (invcList == null) {
	    throw new IllegalStateException("getWritableGenerInvoice() returned null");
	}
	
	Collection<GnucashWritableGenerInvoice> retval = new ArrayList<GnucashWritableGenerInvoice>(invcList.size());
	for (GnucashGenerInvoice invc : invcList) {
	    retval.add((GnucashWritableGenerInvoice) invc);
	}
	return retval;
    }

    /**
     * @param id the unique invoice-id
     * @return A changeable version of the Invoice or null if not found.
     * @see GnucashFile#getGenerInvoiceByID(GCshID)
     */
    @Override
    public GnucashWritableGenerInvoice getGenerInvoiceByID(final GCshID id) {
	return (GnucashWritableGenerInvoice) super.getGenerInvoiceByID(id);
    }

    /**
     * @param type the type to look for
     * @return A changeable version of all accounts of that type.
     * @throws UnknownAccountTypeException 
     * @see {@link GnucashWritableFile#getAccountsByType(String)}
     */
    public Collection<GnucashWritableAccount> getAccountsByType(final GnucashAccount.Type type) throws UnknownAccountTypeException {
	Collection<GnucashWritableAccount> retval = new LinkedList<GnucashWritableAccount>();
	for (GnucashWritableAccount acct : getWritableAccounts()) {

	    if (acct.getType() == null) {
		if (type == null) {
		    retval.add(acct);
		}
	    } else if (acct.getType() == type ) {
		retval.add(acct);
	    }

	}
	return retval;
    }

    /**
     * @param id the unique account-id
     * @return A changeable version of the account or null if not found.
     * @see GnucashFile#getAccountByID(GCshID)
     */
    @Override
    public GnucashWritableAccount getAccountByID(final GCshID id) {
	try {
	    return new GnucashWritableAccountImpl(super.getAccountByID(id));
	} catch ( Exception exc ) {
	    LOGGER.error("getAccountByID: Could not instantiate writable account object from read-only account object (ID: )" + id);
	    throw new RuntimeException("Could not instantiate writable account object from read-only account object (ID: )" + id);
	}
    }

    /**
     * @param name the name of the account
     * @return A changeable version of the first account with that name.
     * @see GnucashFile#getAccountsByName(String)
     */
    @Override
    public GnucashWritableAccount getAccountByNameUniq(final String name, final boolean qualif) throws NoEntryFoundException, TooManyEntriesFoundException {
	return (GnucashWritableAccount) super.getAccountByNameUniq(name, qualif);
    }

    /**
     * Add a new currency.<br/>
     * If the currency already exists, add a new price-quote for it.
     *
     * @param pCmdtySpace        the namespace (e.g. "GOODS" or "CURRENCY")
     * @param pCmdtyId           the currency-name
     * @param conversionFactor   the conversion-factor from the base-currency (EUR).
     * @param pCmdtyNameFraction number of decimal-places after the comma
     * @param pCmdtyName         common name of the new currency
     */
    public void addCurrency(final String pCmdtySpace, final String pCmdtyId, final FixedPointNumber conversionFactor,
	    final int pCmdtyNameFraction, final String pCmdtyName) {

	if (conversionFactor == null) {
	    throw new IllegalArgumentException("null conversionFactor given");
	}
	if (pCmdtySpace == null) {
	    throw new IllegalArgumentException("null comodity-space given");
	}
	if (pCmdtyId == null) {
	    throw new IllegalArgumentException("null comodity-id given");
	}
	if (pCmdtyName == null) {
	    throw new IllegalArgumentException("null comodity-name given");
	}
	if (getCurrencyTable().getConversionFactor(pCmdtySpace, pCmdtyId) == null) {

	    GncV2.GncBook.GncCommodity newCurrency = getObjectFactory().createGncV2GncBookGncCommodity();
	    newCurrency.setCmdtyFraction(pCmdtyNameFraction);
	    newCurrency.setCmdtySpace(pCmdtySpace);
	    newCurrency.setCmdtyId(pCmdtyId);
	    newCurrency.setCmdtyName(pCmdtyName);
	    newCurrency.setVersion(Const.XML_FORMAT_VERSION);
	    getRootElement().getGncBook().getBookElements().add(newCurrency);
	    incrementCountDataFor("commodity");
	}
	// add price-quote
	GncV2.GncBook.GncPricedb.Price.PriceCommodity currency = new GncV2.GncBook.GncPricedb.Price.PriceCommodity();
	currency.setCmdtySpace(pCmdtySpace);
	currency.setCmdtyId(pCmdtyId);

	GncV2.GncBook.GncPricedb.Price.PriceCurrency baseCurrency = getObjectFactory()
		.createGncV2GncBookGncPricedbPricePriceCurrency();
	baseCurrency.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
	baseCurrency.setCmdtyId(getDefaultCurrencyID());

	GncV2.GncBook.GncPricedb.Price newQuote = getObjectFactory().createGncV2GncBookGncPricedbPrice();
	newQuote.setPriceSource("JGnucashLib");
	newQuote.setPriceId(getObjectFactory().createGncV2GncBookGncPricedbPricePriceId());
	newQuote.getPriceId().setType(Const.XML_DATA_TYPE_GUID);
	newQuote.getPriceId().setValue(createGUID());
	newQuote.setPriceCommodity(currency);
	newQuote.setPriceCurrency(baseCurrency);
	newQuote.setPriceTime(getObjectFactory().createGncV2GncBookGncPricedbPricePriceTime());
	newQuote.getPriceTime().setTsDate(PRICE_QUOTE_DATE_FORMAT.format(new Date()));
	newQuote.setPriceType("last");
	newQuote.setPriceValue(conversionFactor.toGnucashString());

	List<Object> bookElements = getRootElement().getGncBook().getBookElements();
	for (Object element : bookElements) {
	    if (element instanceof GncV2.GncBook.GncPricedb) {
		GncV2.GncBook.GncPricedb prices = (GncV2.GncBook.GncPricedb) element;
		prices.getPrice().add(newQuote);
		getCurrencyTable().setConversionFactor(pCmdtySpace, pCmdtyId, conversionFactor);
		return;
	    }
	}
	throw new IllegalStateException("No priceDB in Book in Gnucash-file");
    }

    // ---------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    public GnucashWritableTransaction createWritableTransaction() {
	return new GnucashWritableTransactionImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    public GnucashWritableTransaction createWritableTransaction(final String id) {
	return new GnucashWritableTransactionImpl(this);
    }

    // ----------------------------

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @throws  
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableCustomerInvoice createWritableCustomerInvoice(
	    final String number, 
	    final GnucashCustomer cust,
	    final GnucashAccount incomeAcct,
	    final GnucashAccount receivableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	GnucashWritableCustomerInvoice retval = 
		new GnucashWritableCustomerInvoiceImpl(
			this, 
			number, cust,
			(GnucashAccountImpl) incomeAcct, 
			(GnucashAccountImpl) receivableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableVendorBill createWritableVendorBill(
	    final String number, 
	    final GnucashVendor vend,
	    final GnucashAccount expensesAcct,
	    final GnucashAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	GnucashWritableVendorBill retval = 
		new GnucashWritableVendorBillImpl(
			this, 
			number, vend,
			(GnucashAccountImpl) expensesAcct, 
			(GnucashAccountImpl) payableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableEmployeeVoucher createWritableEmployeeVoucher(
	    final String number, 
	    final GnucashEmployee empl,
	    final GnucashAccount expensesAcct,
	    final GnucashAccount payableAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate) throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (empl == null) {
	    throw new IllegalArgumentException("null empl given");
	}

	GnucashWritableEmployeeVoucher retval = 
		new GnucashWritableEmployeeVoucherImpl(
			this, 
			number, empl,
			(GnucashAccountImpl) expensesAcct, 
			(GnucashAccountImpl) payableAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    /**
     * FOR USE BY EXTENSIONS ONLY!
     * 
     * @throws WrongInvoiceTypeException
     * @throws WrongOwnerTypeException 
     * @throws InvalidCmdtyCurrTypeException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalTransactionSplitActionException 
     * @see GnucashWritableFile#createWritableTransaction()
     */
    public GnucashWritableJobInvoice createWritableJobInvoice(
	    final String number, 
	    final GnucashGenerJob job,
	    final GnucashAccount incExpAcct,
	    final GnucashAccount recvblPayblAcct,
	    final LocalDate openedDate,
	    final LocalDate postDate,
	    final LocalDate dueDate)
	    throws WrongInvoiceTypeException, WrongOwnerTypeException, InvalidCmdtyCurrTypeException, IllegalTransactionSplitActionException, NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	if (job == null) {
	    throw new IllegalArgumentException("null job given");
	}

	GnucashWritableJobInvoice retval = 
		new GnucashWritableJobInvoiceImpl(
			this, 
			number, job,
			(GnucashAccountImpl) incExpAcct, 
			(GnucashAccountImpl) recvblPayblAcct, 
			openedDate, postDate, dueDate);

	super.invcMgr.addGenerInvoice(retval);
	return retval;
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableCustomer()
     */
    public GnucashWritableCustomer createWritableCustomer() {
	GnucashWritableCustomerImpl cust = new GnucashWritableCustomerImpl(this);
	super.custMgr.addCustomer(cust);
	return cust;
    }

    /**
     * @param cust the customer to remove
     */
    public void removeCustomer(final GnucashWritableCustomer cust) {
	super.custMgr.removeCustomer(cust);
	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableCustomerImpl) cust).getJwsdpPeer());
	setModified(true);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableVendor()
     */
    public GnucashWritableVendor createWritableVendor() {
	GnucashWritableVendorImpl vend = new GnucashWritableVendorImpl(this);
	super.vendMgr.addVendor(vend);
	return vend;
    }

    /**
     * @param impl the vendor to remove
     */
    public void removeVendor(final GnucashWritableVendor vend) {
	super.vendMgr.removeVendor(vend);
	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableVendorImpl) vend).getJwsdpPeer());
	setModified(true);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableEmployee()
     */
    public GnucashWritableEmployee createWritableEmployee() {
	GnucashWritableEmployeeImpl empl = new GnucashWritableEmployeeImpl(this);
	super.emplMgr.addEmployee(empl);
	return empl;
    }

    /**
     * @param empl the employee to remove
     */
    public void removeEmployee(final GnucashWritableEmployee empl) {
	emplMgr.removeEmployee(empl);
	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableEmployeeImpl) empl).getJwsdpPeer());
	setModified(true);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
     */
    public GnucashWritableCustomerJob createWritableCustomerJob(
	    final GnucashCustomer cust, 
	    final String number,
	    final String name) {
	if (cust == null) {
	    throw new IllegalArgumentException("null customer given");
	}

	GnucashWritableCustomerJobImpl job = new GnucashWritableCustomerJobImpl(this, cust, number, name);
	super.jobID2job.put(job.getId(), job);
	return job;
    }

    /**
     * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
     */
    public GnucashWritableVendorJob createWritableVendorJob(
	    final GnucashVendor vend, 
	    final String number,
	    final String name) {
	if (vend == null) {
	    throw new IllegalArgumentException("null vendor given");
	}

	GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(this, vend, number, name);
	super.jobID2job.put(job.getId(), job);
	return job;
    }

//    /**
//     * @see GnucashWritableFile#createWritableCustomerJob(GnucashCustomer)
//     */
//    public GnucashWritableEmployeeJob createWritableEmployeeJob(
//	    final GnucashEmployee vend, 
//	    final String number,
//	    final String name) {
//	if (vend == null) {
//	    throw new IllegalArgumentException("null vendor given");
//	}
//
//	GnucashWritableVendorJobImpl job = new GnucashWritableVendorJobImpl(this, vend, number, name);
//	super.jobID2job.put(job.getId(), job);
//	return job;
//    }

    /**
     * @param impl what to remove
     */
    public void removeGenerJob(final GnucashWritableGenerJob impl) {
	jobID2job.remove(impl.getId());
	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableCustomerJobImpl) impl).getJwsdpPeer());
	setModified(true);
    }

    // ----------------------------

    /**
     * @see GnucashWritableFile#createWritableAccount()
     */
    public GnucashWritableAccount createWritableAccount() {
	GnucashWritableAccount acct = new GnucashWritableAccountImpl(this);
	super.acctMgr.addAccount(acct);
	return acct;
    }

    /**
     * @param impl what to remove
     */
    public void removeAccount(final GnucashWritableAccount impl) {
	if (impl.getTransactionSplits().size() > 0) {
	    throw new IllegalStateException("cannot remove account while it contains transaction-splits!");
	}

	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableAccountImpl) impl).getJwsdpPeer());
	setModified(true);
	super.acctMgr.removeAccount(impl);
    }

    /**
     * @return a read-only collection of all accounts
     */
    public Collection<GnucashWritableAccount> getWritableAccounts() {
	TreeSet<GnucashWritableAccount> retval = new TreeSet<GnucashWritableAccount>();
	for (GnucashAccount account : getAccounts()) {
	    retval.add((GnucashWritableAccount) account);
	}
	return retval;
    }

    /**
     * @return a read-only collection of all accounts that have no parent
     * @throws UnknownAccountTypeException 
     */
    @SuppressWarnings("unchecked")
    public Collection<? extends GnucashWritableAccount> getWritableRootAccounts() throws UnknownAccountTypeException {
	return (Collection<? extends GnucashWritableAccount>) getRootAccounts();
    }

    // ----------------------------

    /**
     * @param jnr the job-number to look for.
     * @return the (first) jobs that have this number or null if not found
     */
    public GnucashWritableGenerJob getGenerJobByNumber(final String jnr) {
	if (jobID2job == null) {
	    throw new IllegalStateException("no root-element loaded");
	}

	for (GnucashGenerJob gnucashJob : jobID2job.values()) {
	    GnucashWritableGenerJob job = (GnucashWritableGenerJob) gnucashJob;
	    if (job.getNumber().equals(jnr)) {
		return job;
	    }
	}
	return null;

    }

    /**
     * @param impl an invoice to remove
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     */
    public void removeInvoice(final GnucashWritableGenerInvoiceImpl impl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {

	if (impl.getPayingTransactions().size() > 0) {
	    throw new IllegalArgumentException("cannot remove this invoice! It has payments!");
	}

	GnucashTransaction postTransaction = impl.getPostTransaction();
	if (postTransaction != null) {
	    ((GnucashWritableTransaction) postTransaction).remove();
	}

	super.invcMgr.removeGenerInvoice(impl);
	getRootElement().getGncBook().getBookElements().remove(impl.getJwsdpPeer());
	this.decrementCountDataFor("gnc:GncInvoice");
	setModified(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserDefinedAttribute(final String aName, final String aValue) {
	List<Slot> slots = getRootElement().getGncBook().getBookSlots().getSlot();
	for (Slot slot : slots) {
	    if (slot.getSlotKey().equals(aName)) {
		slot.getSlotValue().getContent().clear();
		slot.getSlotValue().getContent().add(aValue);
		return;
	    }
	}
	// create new slot
	Slot newSlot = getObjectFactory().createSlot();
	newSlot.setSlotKey(aName);
	newSlot.setSlotValue(getObjectFactory().createSlotValue());
	newSlot.getSlotValue().getContent().add(aValue);
	newSlot.getSlotValue().setType("string");
	getRootElement().getGncBook().getBookSlots().getSlot().add(newSlot);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.gnucash.write.jwsdpimpl.GnucashFileImpl#getRootAccounts()
     */
    @Override
    public Collection<? extends GnucashAccount> getRootAccounts() throws UnknownAccountTypeException {
	// TODO Auto-generated method stub
	Collection<? extends GnucashAccount> rootAccounts = super.getRootAccounts();
	if (rootAccounts.size() > 1) {
	    GnucashAccount root = null;
	    StringBuilder roots = new StringBuilder();
	    for (GnucashAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if ( gnucashAccount.getType() != null && 
	             gnucashAccount.getType() == GnucashAccount.Type.ROOT ) {
		    root = gnucashAccount;
		    continue;
		}
		roots.append(gnucashAccount.getId()).append("=\"").append(gnucashAccount.getName()).append("\" ");
	    }
	    LOGGER.warn("File has more then one root-account! Attaching excess accounts to root-account: "
		    + roots.toString());
	    LinkedList<GnucashAccount> rootAccounts2 = new LinkedList<GnucashAccount>();
	    rootAccounts2.add(root);
	    for (GnucashAccount gnucashAccount : rootAccounts) {
		if (gnucashAccount == null) {
		    continue;
		}
		if (gnucashAccount == root) {
		    continue;
		}
		((GnucashWritableAccount) gnucashAccount).setParentAccount(root);

	    }
	    rootAccounts = rootAccounts2;
	}
	return rootAccounts;
    }
    
    // ---------------------------------------------------------------

    @Override
    public GnucashWritableCommodity createWritableCommodity() {
	GnucashWritableCommodityImpl cmdty = new GnucashWritableCommodityImpl(this);
//	try {
//	    super.cmdtyQualifID2Cmdty.put(cmdty.getQualifId().toString(), cmdty);
//	    super.cmdtyXCode2QualifID.put(cmdty.getXCode(), cmdty.getQualifId().toString());
//	} catch ( Exception exc ) {
//	    LOGGER.error("createWritableCommodity: Could not add new commodity to cache");
//	}
	return cmdty;	
    }

    @Override
    public void removeCommodity(GnucashWritableCommodity cmdty) throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException {
	if ( cmdty.getQualifId().toString().
		startsWith(GCshCmdtyCurrNameSpace.CURRENCY + GCshCmdtyCurrID.SEPARATOR) )
	    throw new IllegalArgumentException("Currency commodities may not be removed");
	
	if ( existPriceObjects(cmdty) )
	{
	    LOGGER.error("Commodity with ID '" + cmdty.getQualifId() + "' cannot be removed because " + 
	                 "there are price objects in the Price DB that depend on it");
	    throw new ObjectCascadeException();
	}
	
	cmdtyQualifID2Cmdty.remove(cmdty.getQualifId().toString());
	
	for ( String xCode : cmdtyXCode2QualifID.keySet() ) {
	    if ( cmdtyQualifID2Cmdty.get(xCode).equals(cmdty.getQualifId().toString()) )
		cmdtyXCode2QualifID.remove(xCode);
	}

	getRootElement().getGncBook().getBookElements().remove(((GnucashWritableCommodityImpl) cmdty).getJwsdpPeer());
	setModified(true);
    }

    private boolean existPriceObjects(GnucashWritableCommodity cmdty) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	int counter = 0;
	for ( GCshPrice price : getPrices() ) {
	    if ( price.getFromCommodity().getQualifId().
		    equals(cmdty.getQualifId()) ) {
		counter++;
	    }
	}
	
	if ( counter > 0 )
	    return true;
	else
	    return false;
    }

}
