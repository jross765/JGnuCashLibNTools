package org.gnucash.basetypes.complex;

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
    
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCmdtyCurrID.suite());
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCurrID.suite());
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCmdtyID.suite());
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCmdtyID_Exchange.suite());
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCmdtyID_MIC.suite());
    suite.addTest(org.gnucash.basetypes.complex.TestGCshCmdtyID_SecIdType.suite());

    return suite;
  }
}
