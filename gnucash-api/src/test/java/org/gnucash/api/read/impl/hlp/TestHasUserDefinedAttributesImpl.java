package org.gnucash.api.read.impl.hlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.GnucashGenerInvoice;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.aux.GCshTaxTable;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestHasUserDefinedAttributesImpl {
	public static final GCshID ACCT_1_ID = new GCshID( "b3741e92e3b9475b9d5a2dc8254a8111" ); // SAP
	public static final GCshID ACCT_2_ID = new GCshID( "3b0e56552514420da0e2cec300f64ce6" ); // Depots

	public static final GCshID TRX_1_ID = new GCshID( "18a45dfc8a6868c470438e27d6fe10b2" );
	public static final GCshID TRX_2_ID = new GCshID( "cc9fe6a245df45ba9b494660732a7755" );
	public static final GCshID TRX_3_ID = new GCshID( "d465b802d5c940c9bba04b87b63ba23f" );
		
	public static final GCshID INVC_2_ID = new GCshID( "8de4467c17e04bb2895fb68cc07fc4df" );
	public static final GCshID INVC_3_ID = new GCshID( "169331c9860642cf84b04f3e3151058a" );

	// -----------------------------------------------------------------

    private GnucashFile gcshFile = null;
    private GCshTaxTable taxTab = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
    	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
    	return new JUnit4TestAdapter(TestHasUserDefinedAttributesImpl.class);
    }

    @Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnucashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

    // -----------------------------------------------------------------
    // Account
    // -----------------------------------------------------------------

    // No slots
    @Test
    public void test_acct_01() throws Exception {
    	GnucashAccount acct = gcshFile.getAccountByID(ACCT_1_ID);
    	assertNotEquals(null, acct);
    	
    	assertEquals(null, acct.getUserDefinedAttributeKeys());
    }

    // One slot
    @Test
    public void test_acct_02() throws Exception {
    	GnucashAccount acct = gcshFile.getAccountByID(ACCT_2_ID);
    	assertNotEquals(null, acct);
    	
    	assertNotEquals(null, acct.getUserDefinedAttributeKeys());
    	assertEquals(1, acct.getUserDefinedAttributeKeys().size());
    	assertEquals(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER, acct.getUserDefinedAttributeKeys().get(0));
    	assertEquals("true", acct.getUserDefinedAttribute(ConstTest.SLOT_KEY_ACCT_PLACEHOLDER));
    }
    
    // Account, several slots
    // Such a case does not exist, at least not with "organic" data

    // -----------------------------------------------------------------
    // Transaction
    // -----------------------------------------------------------------

    // No slots
    @Test
    public void test_trx_01() throws Exception {
    	GnucashTransaction trx = gcshFile.getTransactionByID(TRX_1_ID);
    	assertNotEquals(null, trx);
    	
    	assertEquals(null, trx.getUserDefinedAttributeKeys());
    }

    // One slot
    @Test
    public void test_trx_02() throws Exception {
    	GnucashTransaction trx = gcshFile.getTransactionByID(TRX_2_ID);
    	assertNotEquals(null, trx);
    	
    	assertNotEquals(null, trx.getUserDefinedAttributeKeys());
    	assertEquals(1, trx.getUserDefinedAttributeKeys().size());
    	assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(0));
    	assertEquals("2023-07-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
    }
    
    // Several slots
    @Test
    public void test_trx_03() throws Exception {
    	GnucashTransaction trx = gcshFile.getTransactionByID(TRX_3_ID);
    	assertNotEquals(null, trx);
    	
    	assertNotEquals(null, trx.getUserDefinedAttributeKeys());
    	assertEquals(2, trx.getUserDefinedAttributeKeys().size());
    	assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
    	assertEquals(ConstTest.SLOT_KEY_TRX_DATE_POSTED, trx.getUserDefinedAttributeKeys().get(1));
    	assertEquals("https://my.transaction.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
    	assertEquals("2023-10-01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_TRX_DATE_POSTED));
    }
    
    // -----------------------------------------------------------------
    // Invoice
    // -----------------------------------------------------------------

    // No slots
    // Such a case does not exist, at least not with "organic" data

//  // One slot
    @Test
    public void test_invc_02() throws Exception {
    	GnucashGenerInvoice invc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
    	assertNotEquals(null, invc);
  	
    	assertNotEquals(null, invc.getUserDefinedAttributeKeys());
    	assertEquals(1, invc.getUserDefinedAttributeKeys().size());
    	assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, invc.getUserDefinedAttributeKeys().get(0));
    	assertEquals("0", invc.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
    }

    // Several slots
    @Test
    public void test_invc_03() throws Exception {
    	GnucashGenerInvoice trx = gcshFile.getGenerInvoiceByID(INVC_3_ID);
    	assertNotEquals(null, trx);
  	
    	assertNotEquals(null, trx.getUserDefinedAttributeKeys());
    	assertEquals(2, trx.getUserDefinedAttributeKeys().size());
    	assertEquals(ConstTest.SLOT_KEY_ASSOC_URI, trx.getUserDefinedAttributeKeys().get(0));
    	assertEquals(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE, trx.getUserDefinedAttributeKeys().get(1));
    	assertEquals("https://my.job.invoice.link.01", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_ASSOC_URI));
    	assertEquals("0", trx.getUserDefinedAttribute(ConstTest.SLOT_KEY_INVC_CREDIT_NOTE));
    }

//	for ( GnucashGenerInvoice invc : gcshFile.getGenerInvoices() ) {
//	if ( invc.getUserDefinedAttributeKeys() != null ) {
//		if ( invc.getUserDefinedAttributeKeys().size() == 1 ) {
//			System.err.println("yyy1: " + invc.getID() );
//		}
//	}
//}
}
