package org.gnucash;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase {
    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    public static Test suite() throws Exception {
	TestSuite suite = new TestSuite();

	suite.addTest(org.gnucash.basetypes.TestPackage.suite());
	suite.addTest(org.gnucash.currency.TestPackage.suite());
	suite.addTest(org.gnucash.read.TestPackage.suite());
	suite.addTest(org.gnucash.write.TestPackage.suite());

	return suite;
    }
}
