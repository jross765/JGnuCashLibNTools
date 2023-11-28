package org.gnucash.api.write.impl;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestPackage extends TestCase
{
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static Test suite() throws Exception
  {
    TestSuite suite = new TestSuite();
    
    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableFileImpl.suite());

    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableAccountImpl.suite());
    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableTransactionImpl.suite());

    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableCustomerImpl.suite());
    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableVendorImpl.suite());
    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableEmployeeImpl.suite());

    suite.addTest(org.gnucash.api.write.impl.TestGnucashWritableCommodityImpl.suite());

    suite.addTest(org.gnucash.api.write.impl.spec.TestPackage.suite());

    return suite;
  }
}