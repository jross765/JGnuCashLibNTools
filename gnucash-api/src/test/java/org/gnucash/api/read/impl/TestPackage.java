package org.gnucash.api.read.impl;

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
    
    suite.addTest(org.gnucash.api.read.impl.TestGnucashFileImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashAccountImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashCommodityImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashTransactionImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashTransactionSplitImpl.suite());
    
    suite.addTest(org.gnucash.api.read.impl.TestGnucashCustomerImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashVendorImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashEmployeeImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashGenerJobImpl.suite());
    
    suite.addTest(org.gnucash.api.read.impl.TestGnucashGenerInvoiceImpl.suite());
    suite.addTest(org.gnucash.api.read.impl.TestGnucashGenerInvoiceEntryImpl.suite());
    
    suite.addTest(org.gnucash.api.read.impl.aux.TestPackage.suite());
    suite.addTest(org.gnucash.api.read.impl.spec.TestPackage.suite());

    return suite;
  }
}