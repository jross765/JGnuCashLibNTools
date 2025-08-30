package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashCustomerImpl;
import org.gnucash.api.read.impl.TestGnuCashEmployeeImpl;
import org.gnucash.api.read.impl.TestGnuCashVendorImpl;
import org.gnucash.base.basetypes.simple.GCshCustID;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.gnucash.base.basetypes.simple.GCshVendID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshAddressImpl {
	
	// -----------------------------------------------------------------

	public static final GCshCustID CUST_1_ID = TestGnuCashCustomerImpl.CUST_1_ID;
	public static final GCshCustID CUST_2_ID = TestGnuCashCustomerImpl.CUST_2_ID;
	
	public static final GCshVendID VEND_1_ID = TestGnuCashVendorImpl.VEND_1_ID;
	public static final GCshVendID VEND_2_ID = TestGnuCashVendorImpl.VEND_2_ID;

	public static final GCshEmplID EMPL_1ID = TestGnuCashEmployeeImpl.EMPL_1_ID;

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GCshAddress addr = null;

	private GnuCashCustomer cust1 = null;
	private GnuCashVendor   vend1 = null;
	
	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshAddressImpl.class);
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
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
		
		// ---
		
		cust1 = gcshFile.getCustomerByID(CUST_1_ID);
		vend1 = gcshFile.getVendorByID(VEND_1_ID);
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		addr = cust1.getAddress();
		assertNotEquals(null, addr);
		
		assertEquals("Herr SchwervonBegriff", addr.getAddressName());
		
		assertEquals("Nixkapier-Str. 9", addr.getAddressLine1());
		assertEquals("12345 Berlin", addr.getAddressLine2());
		assertEquals("", addr.getAddressLine3());
		assertEquals("", addr.getAddressLine4());
		
		assertEquals("", addr.getTel());
		assertEquals("", addr.getFax());
		assertEquals("", addr.getEmail());
	}

	@Test
	public void test02() throws Exception {
		addr = vend1.getAddress();
		assertNotEquals(null, addr);
		
		assertEquals("Frau Schbörzel-Schnurrenburgen", addr.getAddressName());
		
		assertEquals("Über den Linden 81", addr.getAddressLine1());
		assertEquals("12345 Berlin", addr.getAddressLine2());
		assertEquals("", addr.getAddressLine3());
		assertEquals("", addr.getAddressLine4());
		
		assertEquals("", addr.getTel());
		assertEquals("", addr.getFax());
		assertEquals("", addr.getEmail());
	}

}
