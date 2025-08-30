package org.gnucash.api.read.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucher;
import org.gnucash.base.basetypes.simple.GCshEmplID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashEmployeeImpl {
	
	public static final GCshEmplID EMPL_1_ID = new GCshEmplID("7f70b352dcf44a5d8085767a53a9bc37");
	public static final GCshEmplID EMPL_2_ID = new GCshEmplID("b7af86fc0f3e4b16a23424bc5d150cd1");

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GnuCashEmployee empl = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGnuCashEmployeeImpl.class);
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
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_1() throws Exception {
		empl = gcshFile.getEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(EMPL_1_ID, empl.getID());
		assertEquals("000001", empl.getNumber());
		assertEquals("otwist", empl.getUserName());
		assertEquals("Oliver Twist", empl.getAddress().getName());
	}

	@Test
	public void test02_1() throws Exception {
		empl = gcshFile.getEmployeeByID(EMPL_1_ID);
		assertNotEquals(null, empl);

		assertEquals(1, empl.getNofOpenVouchers());

		assertEquals(0, empl.getPaidVouchers().size());

		assertEquals(1, empl.getUnpaidVouchers().size());
		List<GnuCashEmployeeVoucher> vchList = empl.getUnpaidVouchers();
		Collections.sort(vchList);
		assertEquals("8de4467c17e04bb2895fb68cc07fc4df",
				((GnuCashEmployeeVoucher) vchList.toArray()[0]).getID().toString());

		//    vchList = empl.getPaidVouchers_direct();
		//    Collections.sort(vchList);
		//    assertEquals("xxx", 
		//                 ((GnuCashVendorBill) vchList.toArray()[0]).getID() );
	}
}
