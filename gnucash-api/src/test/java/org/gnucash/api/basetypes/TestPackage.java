package org.gnucash.api.basetypes;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite();

	suite.addTest(org.gnucash.api.basetypes.simple.TestPackage.suite());
	suite.addTest(org.gnucash.api.basetypes.complex.TestPackage.suite());

	return suite;
    }
}
