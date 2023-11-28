package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashAccountImpl;
import org.gnucash.api.write.GnucashWritableAccount;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableAccountImpl
{
    private static final GCshID ACCT_1_ID = TestGnucashAccountImpl.ACCT_1_ID;
    private static final GCshID ACCT_2_ID = TestGnucashAccountImpl.ACCT_2_ID;
    private static final GCshID ACCT_3_ID = TestGnucashAccountImpl.ACCT_3_ID;
    private static final GCshID ACCT_4_ID = TestGnucashAccountImpl.ACCT_4_ID;
    private static final GCshID ACCT_5_ID = TestGnucashAccountImpl.ACCT_5_ID;
    private static final GCshID ACCT_6_ID = TestGnucashAccountImpl.ACCT_6_ID;
    private static final GCshID ACCT_7_ID = TestGnucashAccountImpl.ACCT_7_ID;

    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl         gcshOutFile = null;
    
    private String outFileGlobNameAbs = null;
    private File outFileGlob = null;

    // https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
    @SuppressWarnings("exports")
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    
    // -----------------------------------------------------------------
  
  public static void main(String[] args) throws Exception
  {
    junit.textui.TestRunner.run(suite());
  }

  @SuppressWarnings("exports")
  public static junit.framework.Test suite() 
  {
    return new JUnit4TestAdapter(TestGnucashWritableAccountImpl.class);  
  }
  
  @Before
  public void initialize() throws Exception
  {
    ClassLoader classLoader = getClass().getClassLoader();
    // URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
    // System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
    InputStream gcshInFileStream = null;
    try 
    {
      gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
    } 
    catch ( Exception exc ) 
    {
      System.err.println("Cannot generate input stream from resource");
      return;
    }
    
    try
    {
      gcshInFile = new GnucashWritableFileImpl(gcshInFileStream);
    }
    catch ( Exception exc )
    {
      System.err.println("Cannot parse GnuCash in-file");
      exc.printStackTrace();
    }
    
    URL outFileNameAbsURL = classLoader.getResource(ConstTest.GCSH_FILENAME_IN); // sic
//    System.err.println("Out file name (glob, URL): '" + outFileNameAbsURL + "'");
    outFileGlobNameAbs = outFileNameAbsURL.getPath();
    outFileGlobNameAbs = outFileGlobNameAbs.replace(ConstTest.GCSH_FILENAME_IN, ConstTest.GCSH_FILENAME_OUT);
//    System.err.println("Out file name (glob): '" + outFileGlobNameAbs + "'");
    outFileGlob = new File(outFileGlobNameAbs);
  }

  // -----------------------------------------------------------------
  // PART 1: Read existing objects as modifiable ones
  //         (and see whether they are fully symmetrical to their read-only
  //         counterparts)
  // -----------------------------------------------------------------
  // Cf. TestGnucashAccount.test01/02
  // 
  // Check whether the GnucashWritableAccount objects returned by 
  // GnucashWritableFileImpl.getWritableAccountByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getAccountById().

  @Test
  public void test01_1() throws Exception
  {
    GnucashWritableAccount acct = gcshInFile.getAccountByID(ACCT_1_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_1_ID, acct.getId());
    assertEquals(GnucashAccount.Type.BANK, acct.getType());
    assertEquals("Giro RaiBa", acct.getName());
    assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro RaiBa", acct.getQualifiedName());
    assertEquals("Girokonto 1", acct.getDescription());
    assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());
         
    assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountId().toString());

    // ::TODO (throws exception when you try to call that)
//    assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(6, acct.getTransactions().size());
    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getId().toString());
    assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getId().toString());
    assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(2).getId().toString());
    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(3).getId().toString());
    assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(4).getId().toString());
    assertEquals("d465b802d5c940c9bba04b87b63ba23f", acct.getTransactions().get(5).getId().toString());
  }
  
  @Test
  public void test01_2() throws Exception
  {
    GnucashWritableAccount acct = gcshInFile.getAccountByID(ACCT_2_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_2_ID, acct.getId());
    assertEquals(GnucashAccount.Type.ASSET, acct.getType());
    assertEquals("Depot RaiBa", acct.getName());
    assertEquals("Root Account::Aktiva::Depots::Depot RaiBa", acct.getQualifiedName());
    assertEquals("Aktiendepot 1", acct.getDescription());
    assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());
    
    assertEquals("7ee6fe4de6db46fd957f3513c9c6f983", acct.getParentAccountId().toString());

    // ::TODO
    // ::TODO (throws exception when you try to call that)
//    assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(4428.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    // ::TODO
    assertEquals(0, acct.getTransactions().size());
//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getId());
//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getId());
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableAccount objects returned by 
  // can actually be modified -- both in memory and persisted in file.

  // ::TODO

  // -----------------------------------------------------------------
  // PART 3: Create new objects
  // -----------------------------------------------------------------
  
  // ::TODO

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }
}
