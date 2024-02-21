package org.gnucash.base.basetypes.simple;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshID {

    private static GCshID gcshID = null;

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestGCshID.class);
    }

    @Before
    public void initialize() throws Exception {
	gcshID = new GCshID();
    }

    // -----------------------------------------------------------------

    @Test
    public void test01() throws Exception {
	gcshID.set("2b5f38b679e848ee8e397a3a43ed0eb2");
	assertEquals("2b5f38b679e848ee8e397a3a43ed0eb2", gcshID.get());
	assertEquals(true, gcshID.isSet());

	gcshID.set("c258aa23358040a08fcfe1efad2a906c");
	assertEquals("c258aa23358040a08fcfe1efad2a906c", gcshID.get());
	assertEquals(true, gcshID.isSet());
    }

    @Test
    public void test02() throws Exception {
	gcshID.set("2B5F38B679E848EE8E397A3A43ED0EB2");
	assertEquals("2b5f38b679e848ee8e397a3a43ed0eb2", gcshID.get());
	assertEquals(true, gcshID.isSet());

	gcshID.set("c258Aa23358040A08fCfe1EfaD2a906C");
	assertEquals("c258aa23358040a08fcfe1efad2a906c", gcshID.get());
	assertEquals(true, gcshID.isSet());
    }

    @Test
    public void test03() throws Exception {
	try {
	    gcshID.set("2b5f38b679e848ee8e397a3a43ed0eb"); // invalid string (too short)
	    assertEquals(1, 0);
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

	try {
	    gcshID.set("2b5f38b679e848ee8e397a3a43ed0eb2a"); // invalid string (too long)
	    assertEquals(1, 0);
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}

	try {
	    gcshID.set("2z5f38b679e848ee8e397a3a43ed0eb"); // invalid string (illegal char, 2nd pos)
	    assertEquals(1, 0);
	} catch (Exception exc) {
	    assertEquals(0, 0);
	}
    }
}
