package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashAccountImpl;
import org.gnucash.api.write.GnucashWritableAccount;
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
    private GCshID                  newAcctID = null;
    
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
  // complete (as complete as returned be GnucashFileImpl.getAccountByID().

  @Test
  public void test01_1() throws Exception
  {
    GnucashWritableAccount acct = gcshInFile.getAccountByID(ACCT_1_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_1_ID, acct.getID());
    assertEquals(GnucashAccount.Type.BANK, acct.getType());
    assertEquals("Giro RaiBa", acct.getName());
    assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro RaiBa", acct.getQualifiedName());
    assertEquals("Girokonto 1", acct.getDescription());
    assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());
         
    assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

    // ::TODO (throws exception when you try to call that)
//    assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
    
    assertEquals(6, acct.getTransactions().size());
    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
    assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getID().toString());
    assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(2).getID().toString());
    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(3).getID().toString());
    assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(4).getID().toString());
    assertEquals("d465b802d5c940c9bba04b87b63ba23f", acct.getTransactions().get(5).getID().toString());
  }
  
  @Test
  public void test01_2() throws Exception
  {
    GnucashWritableAccount acct = gcshInFile.getAccountByID(ACCT_2_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_2_ID, acct.getID());
    assertEquals(GnucashAccount.Type.ASSET, acct.getType());
    assertEquals("Depot RaiBa", acct.getName());
    assertEquals("Root Account::Aktiva::Depots::Depot RaiBa", acct.getQualifiedName());
    assertEquals("Aktiendepot 1", acct.getDescription());
    assertEquals("CURRENCY:EUR", acct.getCmdtyCurrID().toString());
    
    assertEquals("7ee6fe4de6db46fd957f3513c9c6f983", acct.getParentAccountID().toString());

    // ::TODO
    // ::TODO (throws exception when you try to call that)
//    assertEquals(0.0, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//    assertEquals(4428.0, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);

    // ::TODO
    assertEquals(0, acct.getTransactions().size());
//    assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID());
//    assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(1).getID());
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableAccount objects returned by 
  // can actually be modified -- both in memory and persisted in file.

  @Test
  public void test02_1() throws Exception
  {
    assertEquals(ConstTest.COUNT_ACCT, gcshInFile.getNofEntriesAccountMap());
    
    GnucashWritableAccount acct = gcshInFile.getAccountByID(ACCT_1_ID);
    assertNotEquals(null, acct);
    
    assertEquals(ACCT_1_ID, acct.getID());
    
    // ----------------------------
    // Modify the object
    
    acct.setName("Giro Bossa Nova");
    acct.setDescription("Buffda Duffda Deuf");
    acct.setCmdtyCurrID(new GCshCurrID("CAD"));
    
    // ::TODO not possible yet
    // trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").remove()
    // trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").setXYZ()

    // ----------------------------
    // Check whether the object has actually been modified 
    // (in memory, not in the file yet).
    
    test02_1_check_memory(acct);
    
    // ----------------------------
    // Now, check whether the modified object can be written to the 
    // output file, then re-read from it, and whether is is what
    // we expect it is.
    
    File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
    //  System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
    outFile.delete(); // sic, the temp. file is already generated (empty), 
                      // and the GnuCash file writer does not like that.
    gcshInFile.writeFile(outFile);
  
    test02_1_check_persisted(outFile);
  }

  @Test
  public void test02_2() throws Exception
  {
      // ::TODO
  }

  private void test02_1_check_memory(GnucashWritableAccount acct) throws Exception 
  {
      assertEquals(ConstTest.COUNT_ACCT, gcshInFile.getNofEntriesAccountMap());

      assertEquals(ACCT_1_ID, acct.getID());
      assertEquals(GnucashAccount.Type.BANK, acct.getType());
      assertEquals("Giro Bossa Nova", acct.getName());
      assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro Bossa Nova", acct.getQualifiedName());
      assertEquals("Buffda Duffda Deuf", acct.getDescription());
      assertEquals("CURRENCY:CAD", acct.getCmdtyCurrID().toString());

      assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

      // ::TODO (throws exception when you try to call that)
//      assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//      assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      assertEquals(6, acct.getTransactions().size());
      assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
      assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getID().toString());
      assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(2).getID().toString());
      assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(3).getID().toString());
      assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(4).getID().toString());
      assertEquals("d465b802d5c940c9bba04b87b63ba23f", acct.getTransactions().get(5).getID().toString());
  }

  private void test02_1_check_persisted(File outFile) throws Exception
  {
     gcshOutFile = new GnucashFileImpl(outFile);
     assertEquals(ConstTest.COUNT_ACCT, gcshOutFile.getNofEntriesAccountMap());
      
     GnucashAccount acct = gcshOutFile.getAccountByID(ACCT_1_ID);
     assertNotEquals(null, acct);
     
     assertEquals(ACCT_1_ID, acct.getID());
     assertEquals(GnucashAccount.Type.BANK, acct.getType());
     assertEquals("Giro Bossa Nova", acct.getName());
     assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro Bossa Nova", acct.getQualifiedName());
     assertEquals("Buffda Duffda Deuf", acct.getDescription());
     assertEquals("CURRENCY:CAD", acct.getCmdtyCurrID().toString());

     assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

     // ::TODO (throws exception when you try to call that)
//     assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//     assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
     
     assertEquals(6, acct.getTransactions().size());
     assertEquals("568864bfb0954897ab8578db4d27372f", acct.getTransactions().get(0).getID().toString());
     assertEquals("29557cfdf4594eb68b1a1b710722f991", acct.getTransactions().get(1).getID().toString());
     assertEquals("67796d4f7c924c1da38f7813dbc3a99d", acct.getTransactions().get(2).getID().toString());
     assertEquals("18a45dfc8a6868c470438e27d6fe10b2", acct.getTransactions().get(3).getID().toString());
     assertEquals("ccff780b18294435bf03c6cb1ac325c1", acct.getTransactions().get(4).getID().toString());
     assertEquals("d465b802d5c940c9bba04b87b63ba23f", acct.getTransactions().get(5).getID().toString());
  }
  
  // -----------------------------------------------------------------
  // PART 3: Create new objects
  // -----------------------------------------------------------------
  
  @Test
  public void test03_1() throws Exception
  {
      assertEquals(ConstTest.COUNT_ACCT, gcshInFile.getNofEntriesAccountMap());
      
      // ----------------------------
      // Bare naked object
      
      GnucashWritableAccount acct = gcshInFile.createWritableAccount();
      assertNotEquals(null, acct);
      newAcctID = acct.getID();
      assertEquals(true, newAcctID.isSet());
      
      // ----------------------------
      // Modify the object
      
      acct.setType(GnucashAccount.Type.BANK);
      acct.setParentAccountID(new GCshID("fdffaa52f5b04754901dfb1cf9221494")); // Root Account::Aktiva::Sichteinlagen::KK
      acct.setName("Giro Rhumba");
      acct.setDescription("Cha-cha-cha");
      acct.setCmdtyCurrID(new GCshCurrID("JPY"));
      
      // ----------------------------
      // Check whether the object has actually been modified 
      // (in memory, not in the file yet).
      
      test03_1_check_memory(acct);
      
      // ----------------------------
      // Now, check whether the modified object can be written to the 
      // output file, then re-read from it, and whether is is what
      // we expect it is.
      
      File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
      //  System.err.println("Outfile for TestGnucashWritableCustomerImpl.test01_1: '" + outFile.getPath() + "'");
      outFile.delete(); // sic, the temp. file is already generated (empty), 
                        // and the GnuCash file writer does not like that.
      gcshInFile.writeFile(outFile);
    
      test03_1_check_persisted(outFile);
  }
  
  private void test03_1_check_memory(GnucashWritableAccount acct) throws Exception 
  {
      assertEquals(ConstTest.COUNT_ACCT + 1, gcshInFile.getNofEntriesAccountMap());

      assertEquals(newAcctID, acct.getID());
      assertEquals(GnucashAccount.Type.BANK, acct.getType());
      assertEquals("Giro Rhumba", acct.getName());
      assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro Rhumba", acct.getQualifiedName());
      assertEquals("Cha-cha-cha", acct.getDescription());
      assertEquals("CURRENCY:JPY", acct.getCmdtyCurrID().toString());

      assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

      // ::TODO (throws exception when you try to call that)
//      assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//      assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      assertEquals(0, acct.getTransactions().size());
  }

  private void test03_1_check_persisted(File outFile) throws Exception
  {
     gcshOutFile = new GnucashFileImpl(outFile);
     assertEquals(ConstTest.COUNT_ACCT + 1, gcshOutFile.getNofEntriesAccountMap());
      
     GnucashAccount acct = gcshOutFile.getAccountByID(newAcctID);
     assertNotEquals(null, acct);
     
     assertEquals(newAcctID, acct.getID());
     assertEquals(GnucashAccount.Type.BANK, acct.getType());
     assertEquals("Giro Rhumba", acct.getName());
     assertEquals("Root Account::Aktiva::Sichteinlagen::KK::Giro Rhumba", acct.getQualifiedName());
     assertEquals("Cha-cha-cha", acct.getDescription());
     assertEquals("CURRENCY:JPY", acct.getCmdtyCurrID().toString());

     assertEquals("fdffaa52f5b04754901dfb1cf9221494", acct.getParentAccountID().toString());

     // ::TODO (throws exception when you try to call that)
//     assertEquals(3060.46, acct.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
//     assertEquals(3060.46, acct.getBalanceRecursive().doubleValue(), ConstTest.DIFF_TOLERANCE);
     
     assertEquals(0, acct.getTransactions().size());
  }

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }
}
