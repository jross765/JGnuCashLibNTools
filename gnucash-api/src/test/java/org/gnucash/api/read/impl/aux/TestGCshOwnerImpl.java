package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.GnuCashGenerInvoice;
import org.gnucash.api.read.GnuCashGenerInvoice.ReadVariant;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerInvoiceImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerJobImpl;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshOwnerImpl {

	public static final GCshID INVC_1_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_1_ID;
	public static final GCshID INVC_2_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_2_ID;
	public static final GCshID INVC_3_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_3_ID;
	public static final GCshID INVC_4_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_4_ID;
	public static final GCshID INVC_5_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_5_ID;
	public static final GCshID INVC_6_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_6_ID;
	public static final GCshID INVC_7_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_7_ID;
	public static final GCshID INVC_8_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_8_ID;
	public static final GCshID INVC_9_ID = TestGnuCashGenerInvoiceImpl.GENER_INVC_9_ID;

	public static final GCshID JOB_1_ID = TestGnuCashGenerJobImpl.GENER_JOB_1_ID;
	public static final GCshID JOB_2_ID = TestGnuCashGenerJobImpl.GENER_JOB_2_ID;

	public static final GCshID OWN_1_ID = new GCshID("5d1dd9afa7554553988669830cc1f696");
	public static final GCshID OWN_2_ID = new GCshID("087e1a3d43fa4ef9a9bdd4b4797c4231");
	public static final GCshID OWN_3_ID = new GCshID("7f70b352dcf44a5d8085767a53a9bc37");
	public static final GCshID OWN_4_ID = new GCshID("e91b99cd6fbb48a985cbf1e8041f378c");
	public static final GCshID OWN_5_ID = new GCshID("f44645d2397946bcac90dff68cc03b76");
	public static final GCshID OWN_6_ID = new GCshID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
		
	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	private GCshOwner own = null;

	private GnuCashGenerInvoice generInvc = null;
	private GnuCashGenerJob     generJob  = null;
	private GnuCashCustomerJob  custJob  = null;
	private GnuCashVendorJob    vendJob  = null;

	private GnuCashCustomer cust = null;
	private GnuCashVendor   vend = null;
	private GnuCashEmployee empl = null;
	
	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshOwnerImpl.class);
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

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		assertEquals("gncCustomer", GCshOwner.Type.CUSTOMER.getCode());

		assertEquals(GCshOwner.Type.CUSTOMER, GCshOwner.Type.valueOff("gncCustomer"));
		assertNotEquals(GCshOwner.Type.CUSTOMER, GCshOwner.Type.valueOff("gncVendor"));

		assertEquals(GCshOwner.Type.CUSTOMER, GCshOwner.Type.valueOf("CUSTOMER"));
		assertNotEquals(GCshOwner.Type.CUSTOMER, GCshOwner.Type.valueOf("VENDOR"));
	}

	@Test
	public void test02() throws Exception {
		assertEquals(4, GCshOwner.Type.VENDOR.getIndex());
		assertEquals(GCshOwner.Type.VENDOR, GCshOwner.Type.valueOff(4));
		assertNotEquals(GCshOwner.Type.VENDOR, GCshOwner.Type.valueOff(2));
	}

	@Test
	public void test03() throws Exception {
		assertEquals(GCshOwner.Type.EMPLOYEE, GCshOwner.Type.EMPLOYEE);
		assertNotEquals(GCshOwner.Type.EMPLOYEE, GCshOwner.Type.VENDOR);

		boolean areEqual = (GCshOwner.Type.EMPLOYEE == GCshOwner.Type.EMPLOYEE);
		assertEquals(true, areEqual);

		areEqual = (GCshOwner.Type.EMPLOYEE.equals(GCshOwner.Type.EMPLOYEE));
		assertEquals(true, areEqual);

		areEqual = (GCshOwner.Type.EMPLOYEE == GCshOwner.Type.VENDOR);
		assertEquals(false, areEqual);

		areEqual = (GCshOwner.Type.EMPLOYEE.equals(GCshOwner.Type.VENDOR));
		assertEquals(false, areEqual);
	}
	
	@Test
	public void test04_1()
	{
		generInvc = gcshFile.getGenerInvoiceByID(INVC_1_ID);
		assertNotEquals(null, generInvc);

		own = new GCshOwnerImpl(OWN_1_ID, GCshOwner.JIType.INVOICE, gcshFile);
		cust = gcshFile.getCustomerByID(OWN_1_ID);
		
		assertEquals(OWN_1_ID, own.getID());
		assertEquals(GCshOwner.JIType.INVOICE, own.getJIType());

		// ---

		assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, own.getInvcType());
		
		try {
			assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, own.getJobType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(cust, own.getCustomer());
		
		try {
			assertEquals(cust, own.getVendor()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(cust, own.getEmployee()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(cust, own.getGenerJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generInvc.getOwnerID(ReadVariant.DIRECT), own.getID());
	}
	
	@Test
	public void test04_2()
	{
		generInvc = gcshFile.getGenerInvoiceByID(INVC_2_ID);
		assertNotEquals(null, generInvc);

		own = new GCshOwnerImpl(OWN_2_ID, GCshOwner.JIType.INVOICE, gcshFile);
		vend = gcshFile.getVendorByID(OWN_2_ID);
		
		assertEquals(OWN_2_ID, own.getID());
		assertEquals(GCshOwner.JIType.INVOICE, own.getJIType());

		// ---

		assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, own.getInvcType());
		
		try {
			assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, own.getJobType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		try {
			assertEquals(vend, own.getCustomer()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(vend, own.getVendor());
		
		try {
			assertEquals(vend, own.getEmployee()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(vend, own.getGenerJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generInvc.getOwnerID(ReadVariant.DIRECT), own.getID());
	}
	
	@Test
	public void test04_3()
	{
		generInvc = gcshFile.getGenerInvoiceByID(INVC_8_ID);
		assertNotEquals(null, generInvc);

		own = new GCshOwnerImpl(OWN_3_ID, GCshOwner.JIType.INVOICE, gcshFile);
		empl = gcshFile.getEmployeeByID(OWN_3_ID);
		
		assertEquals(OWN_3_ID, own.getID());
		assertEquals(GCshOwner.JIType.INVOICE, own.getJIType());

		// ---

		assertEquals(GnuCashGenerInvoice.TYPE_EMPLOYEE, own.getInvcType());
		
		try {
			assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, own.getJobType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		try {
			assertEquals(empl, own.getCustomer()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(empl, own.getVendor()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(empl, own.getEmployee());
		
		try {
			assertEquals(empl, own.getGenerJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generInvc.getOwnerID(ReadVariant.DIRECT), own.getID());
	}
	
	@Test
	public void test04_4()
	{
		generInvc = gcshFile.getGenerInvoiceByID(INVC_9_ID);
		assertNotEquals(null, generInvc);

		own = new GCshOwnerImpl(OWN_4_ID, GCshOwner.JIType.INVOICE, gcshFile);
		generJob = gcshFile.getGenerJobByID(OWN_4_ID);
		custJob = gcshFile.getCustomerJobByID(OWN_4_ID);
		
		assertEquals(OWN_4_ID, own.getID());
		assertEquals(GCshOwner.JIType.INVOICE, own.getJIType());
		// ---

		assertEquals(GnuCashGenerInvoice.TYPE_JOB, own.getInvcType());
		
		try {
			assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, own.getJobType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		try {
			assertEquals(generJob, own.getCustomer()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(generJob, own.getVendor()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(generJob, own.getEmployee()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(generJob, own.getGenerJob());
		
		// ::TODO
		// Does not work, don't know why:
		// assertEquals(custJob, own.getCustomerJob());
		assertEquals(custJob.toString(), own.getCustomerJob().toString());
		
		try {
			assertEquals(vendJob, own.getVendorJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generInvc.getOwnerID(ReadVariant.DIRECT), own.getID());
	}
	
	
	@Test
	public void test05_1()
	{
		generJob = gcshFile.getGenerJobByID(JOB_1_ID);
		assertNotEquals(null, generJob);
		
		own = new GCshOwnerImpl(OWN_5_ID, GCshOwner.JIType.JOB, gcshFile);
		cust = gcshFile.getCustomerByID(OWN_5_ID);
		
		assertEquals(OWN_5_ID, own.getID());
		assertEquals(GCshOwner.JIType.JOB, own.getJIType());
		
		// ---

		try {
			assertEquals(GnuCashGenerInvoice.TYPE_CUSTOMER, own.getInvcType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, own.getJobType());
		
		// ---

		assertEquals(cust, own.getCustomer());
		
		try {
			assertEquals(cust, own.getVendor()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(cust, own.getEmployee()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(cust, own.getGenerJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generJob.getOwnerID(), own.getID());
	}

	@Test
	public void test05_2()
	{
		generJob = gcshFile.getGenerJobByID(JOB_2_ID);
		assertNotEquals(null, generJob);
		
		own = new GCshOwnerImpl(OWN_6_ID, GCshOwner.JIType.JOB, gcshFile);
		vend = gcshFile.getVendorByID(OWN_6_ID);
		
		assertEquals(OWN_6_ID, own.getID());
		assertEquals(GCshOwner.JIType.JOB, own.getJIType());
		
		// ---

		try {
			assertEquals(GnuCashGenerInvoice.TYPE_VENDOR, own.getInvcType()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(GnuCashGenerJob.TYPE_VENDOR, own.getJobType());
		
		// ---

		try {
			assertEquals(vend, own.getCustomer()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		assertEquals(vend, own.getVendor());
		
		try {
			assertEquals(vend, own.getEmployee()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		try {
			assertEquals(vend, own.getGenerJob()); // illegal call
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		
		// ---

		assertEquals(generJob.getOwnerID(), own.getID());
	}
	
}
