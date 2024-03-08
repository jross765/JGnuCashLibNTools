package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.gnucash.api.read.aux.GCshOwner;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshOwnerImpl {

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshOwnerImpl.class);
	}

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
}
