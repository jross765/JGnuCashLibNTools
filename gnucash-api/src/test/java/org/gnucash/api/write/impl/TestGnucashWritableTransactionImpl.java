package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashAccount;
import org.gnucash.api.read.GnucashTransaction;
import org.gnucash.api.read.NoEntryFoundException;
import org.gnucash.api.read.GnucashTransactionSplit;
import org.gnucash.api.read.TooManyEntriesFoundException;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.TestGnucashAccountImpl;
import org.gnucash.api.read.impl.TestGnucashTransactionImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnucashWritableAccount;
import org.gnucash.api.write.GnucashWritableTransaction;
import org.gnucash.api.write.GnucashWritableTransactionSplit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnucashWritableTransactionImpl
{
    private static final GCshID TRX_1_ID = TestGnucashTransactionImpl.TRX_1_ID;
    private static final GCshID TRX_2_ID = TestGnucashTransactionImpl.TRX_2_ID;
    
    private static final GCshID ACCT_1_ID = TestGnucashAccountImpl.ACCT_1_ID;
    private static final GCshID ACCT_20_ID = new GCshID("b88e9eca9c73411b947b882d0bf8ec6f"); // Root Account::Aktiva::Sichteinlagen::nicht-KK::Sparkonto

    // -----------------------------------------------------------------

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl         gcshOutFile = null;
    
    private GCshFileStats           gcshInFileStats = null;
    private GCshFileStats           gcshOutFileStats = null;
    
    private GCshID                  newTrxID = null;
    
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
    return new JUnit4TestAdapter(TestGnucashWritableTransactionImpl.class);  
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
  // Cf. TestGnucashTransaction.test01/02
  // 
  // Check whether the GnucashWritableTransaction objects returned by 
  // GnucashWritableFileImpl.getWritableTransactionByID() are actually 
  // complete (as complete as returned be GnucashFileImpl.getTransactionByID().

  @Test
  public void test01_1() throws Exception
  {
    GnucashWritableTransaction trx = gcshInFile.getTransactionByID(TRX_1_ID);
    assertNotEquals(null, trx);
    
    assertEquals(TRX_1_ID, trx.getID());
    assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals("Dividenderl", trx.getDescription());
    assertEquals("2023-08-06T10:59Z", trx.getDatePosted().toString());
    assertEquals("2023-08-06T08:21:44Z", trx.getDateEntered().toString());
        
    assertEquals(3, trx.getSplitsCount());
    assertEquals("7abf90fe15124254ac3eb7ec33f798e7", trx.getSplits().get(0).getID().toString());
    assertEquals("ea08a144322146cea38b39d134ca6fc1", trx.getSplits().get(1).getID().toString());
    assertEquals("5c5fa881869843d090a932f8e6b15af2", trx.getSplits().get(2).getID().toString());
  }
  
  @Test
  public void test01_2() throws Exception
  {
    GnucashWritableTransaction trx = gcshInFile.getTransactionByID(TRX_2_ID);
    assertNotEquals(null, trx);
    
    assertEquals(TRX_2_ID, trx.getID());
    assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE);
    assertEquals("Unfug und Quatsch GmbH", trx.getDescription());
    assertEquals("2023-07-29T10:59Z", trx.getDatePosted().toString());
    assertEquals("2023-09-13T08:36:54Z", trx.getDateEntered().toString());
        
    assertEquals(2, trx.getSplitsCount());
    assertEquals("f2a67737458d4af4ade616a23db32c2e", trx.getSplits().get(0).getID().toString());
    assertEquals("d17361e4c5a14e84be4553b262839a7b", trx.getSplits().get(1).getID().toString());
  }

  // -----------------------------------------------------------------
  // PART 2: Modify existing objects
  // -----------------------------------------------------------------
  // Check whether the GnucashWritableTransaction objects returned by 
  // can actually be modified -- both in memory and persisted in file.

  @Test
  public void test02_1() throws Exception
  {
    gcshInFileStats = new GCshFileStats(gcshInFile);

    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
    
    GnucashWritableTransaction trx = gcshInFile.getTransactionByID(TRX_1_ID);
    assertNotEquals(null, trx);
    
    assertEquals(TRX_1_ID, trx.getID());
    
    // ----------------------------
    // Modify the object
    
    trx.setDescription("Super dividend");
    trx.setDatePosted(LocalDate.of(1970, 1, 1));
    
    // ::TODO not possible yet
    // trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").remove()
    // trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").setXYZ()

    // ----------------------------
    // Check whether the object can has actually be modified 
    // (in memory, not in the file yet).
    
    test02_1_check_memory(trx);
    
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

  private void test02_1_check_memory(GnucashWritableTransaction trx) throws Exception 
  {
    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
    assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));

    assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
    assertEquals("Super dividend", trx.getDescription()); // changed
    assertEquals("1970-01-01T00:00+01:00[Europe/Berlin]", trx.getDatePosted().toString()); // changed
    assertEquals("2023-08-06T08:21:44Z", trx.getDateEntered().toString()); // unchanged
        
    assertEquals(3, trx.getSplitsCount()); // unchanged
    assertEquals("7abf90fe15124254ac3eb7ec33f798e7", trx.getSplits().get(0).getID().toString()); // unchanged
    assertEquals("ea08a144322146cea38b39d134ca6fc1", trx.getSplits().get(1).getID().toString()); // unchanged
    assertEquals("5c5fa881869843d090a932f8e6b15af2", trx.getSplits().get(2).getID().toString()); // unchanged
  }

  private void test02_1_check_persisted(File outFile) throws Exception
  {
     gcshOutFile = new GnucashFileImpl(outFile);
     gcshOutFileStats = new GCshFileStats(gcshOutFile);
     
     assertEquals(ConstTest.Stats.NOF_TRX, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
     assertEquals(ConstTest.Stats.NOF_TRX, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
     assertEquals(ConstTest.Stats.NOF_TRX, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
      
     GnucashTransaction trx = gcshOutFile.getTransactionByID(TRX_1_ID);
     assertNotEquals(null, trx);
     
     assertEquals(TRX_1_ID, trx.getID());
     assertEquals(0.0, trx.getBalance().getBigDecimal().doubleValue(), ConstTest.DIFF_TOLERANCE); // unchanged
     assertEquals("Super dividend", trx.getDescription()); // changed
     assertEquals("1970-01-01T00:00+01:00", trx.getDatePosted().toString()); // changed
     assertEquals("2023-08-06T08:21:44Z", trx.getDateEntered().toString()); // unchanged
         
     assertEquals(3, trx.getSplitsCount()); // unchanged
     assertEquals("7abf90fe15124254ac3eb7ec33f798e7", trx.getSplits().get(0).getID().toString()); // unchanged
     assertEquals("ea08a144322146cea38b39d134ca6fc1", trx.getSplits().get(1).getID().toString()); // unchanged
     assertEquals("5c5fa881869843d090a932f8e6b15af2", trx.getSplits().get(2).getID().toString()); // unchanged
  }
  
  // -----------------------------------------------------------------
  // PART 3: Create new objects
  // -----------------------------------------------------------------
  
  @Test
  public void test03_1() throws Exception
  {
      gcshInFileStats = new GCshFileStats(gcshInFile);
      
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
      
      // ----------------------------
      // Bare naked object
      
      GnucashWritableTransaction trx = gcshInFile.createWritableTransaction();
      assertNotEquals(null, trx);
      newTrxID = trx.getID();
      assertEquals(true, newTrxID.isSet());
      
      // ----------------------------
      // Modify the object
      
      // trx.setType(GnucashTransaction.Type.PAYMENT);
      trx.setDescription("Chattanooga Choo-Choo");
      trx.setCmdtyCurrID(new GCshCurrID("EUR"));
      trx.setDateEntered(LocalDateTime.of(LocalDate.of(2023, 12, 11), LocalTime.of(10, 0)));
      trx.setDatePosted(LocalDate.of(2023, 5, 20));

      GnucashAccount acct1 = gcshInFile.getAccountByID(ACCT_1_ID);
      GnucashAccount acct2 = gcshInFile.getAccountByID(ACCT_20_ID);

      GnucashWritableTransactionSplit splt1 = trx.createWritableSplit(acct1);
      splt1.setAction(GnucashTransactionSplit.Action.DECREASE);
      splt1.setQuantity(new FixedPointNumber(100).negate());
      splt1.setValue(new FixedPointNumber(100).negate());
      splt1.setDescription("Generated by TestGnucashWritableTransactionImpl.test03_1 (1)");

      GnucashWritableTransactionSplit splt2 = trx.createWritableSplit(acct2);
      splt2.setAction(GnucashTransactionSplit.Action.INCREASE);
      splt2.setQuantity(new FixedPointNumber(100));
      splt2.setValue(new FixedPointNumber(100));
      splt2.setDescription("Generated by TestGnucashWritableTransactionImpl.test03_1 (2)");
      
      // ----------------------------
      // Check whether the object has actually been modified 
      // (in memory, not in the file yet).
      
      test03_1_check_memory(trx);
      
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
  
  private void test03_1_check_memory(GnucashWritableTransaction trx) throws Exception 
  {
      assertEquals(ConstTest.Stats.NOF_TRX + 1, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
      // CAUTION: The counter has not been updated yet.
      // This is on purpose
      // ::TODO
      // assertEquals(ConstTest.Stats.NOF_TRX, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
      assertEquals(ConstTest.Stats.NOF_TRX + 1, gcshInFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));

      // assertEquals(GnucashTransaction.Type.PAYMENT, trx.getType());
      assertEquals("Chattanooga Choo-Choo", trx.getDescription());
      assertEquals(new GCshCurrID("EUR").toString(), trx.getCmdtyCurrID().toString());
      assertEquals("2023-12-11T10:00+01:00[Europe/Berlin]", trx.getDateEntered().toString());
      assertEquals("2023-05-20T00:00+02:00[Europe/Berlin]", trx.getDatePosted().toString());
      
      // ---

      assertEquals(0, trx.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
      
      // ---

      assertEquals(2, trx.getSplits().size());
      assertEquals(trx.getSplits().size(), trx.getSplitsCount());
      
      GnucashTransactionSplit splt1 = (GnucashTransactionSplit) trx.getSplits().toArray()[0];
      GnucashTransactionSplit splt2 = (GnucashTransactionSplit) trx.getSplits().toArray()[1];

      assertEquals(ACCT_1_ID, splt1.getAccountID());
      assertEquals(GnucashTransactionSplit.Action.DECREASE, splt1.getAction());
      assertEquals(GnucashTransactionSplit.Action.DECREASE.getLocaleString(), splt1.getActionStr());
      assertEquals(new FixedPointNumber(100).negate(), splt1.getQuantity());
      assertEquals(new FixedPointNumber(100).negate(), splt1.getValue());
      assertEquals("Generated by TestGnucashWritableTransactionImpl.test03_1 (1)", splt1.getDescription());

      assertEquals(ACCT_20_ID, splt2.getAccountID());
      assertEquals(GnucashTransactionSplit.Action.INCREASE, splt2.getAction());
      assertEquals(GnucashTransactionSplit.Action.INCREASE.getLocaleString(), splt2.getActionStr());
      assertEquals(new FixedPointNumber(100), splt2.getQuantity());
      assertEquals(new FixedPointNumber(100), splt2.getValue());
      assertEquals("Generated by TestGnucashWritableTransactionImpl.test03_1 (2)", splt2.getDescription());
  }

  private void test03_1_check_persisted(File outFile) throws Exception
  {
     gcshOutFile = new GnucashFileImpl(outFile);
     gcshOutFileStats = new GCshFileStats(gcshOutFile);
     
     // Here, all 3 stats variants must have been updated
     assertEquals(ConstTest.Stats.NOF_TRX + 1, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.RAW));
     assertEquals(ConstTest.Stats.NOF_TRX + 1, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.COUNTER));
     assertEquals(ConstTest.Stats.NOF_TRX + 1, gcshOutFileStats.getNofEntriesTransactions(GCshFileStats.Type.CACHE));
      
     GnucashTransaction trx = gcshOutFile.getTransactionByID(newTrxID);
     assertNotEquals(null, trx);
     
     // assertEquals(GnucashTransaction.Type.PAYMENT, trx.getType());
     assertEquals("Chattanooga Choo-Choo", trx.getDescription());
     assertEquals(new GCshCurrID("EUR").toString(), trx.getCmdtyCurrID().toString());
     assertEquals("2023-12-11T10:00+01:00", trx.getDateEntered().toString());
     assertEquals("2023-05-20T00:00+02:00", trx.getDatePosted().toString());
     
     // ---

     assertEquals(0, trx.getBalance().doubleValue(), ConstTest.DIFF_TOLERANCE);
     
     // ---

     assertEquals(2, trx.getSplits().size());
     assertEquals(trx.getSplits().size(), trx.getSplitsCount());
     
     GnucashTransactionSplit splt1 = (GnucashTransactionSplit) trx.getSplits().toArray()[0];
     GnucashTransactionSplit splt2 = (GnucashTransactionSplit) trx.getSplits().toArray()[1];

     assertEquals(ACCT_1_ID, splt1.getAccountID());
     assertEquals(GnucashTransactionSplit.Action.DECREASE, splt1.getAction());
     assertEquals(GnucashTransactionSplit.Action.DECREASE.getLocaleString(), splt1.getActionStr());
     assertEquals(new FixedPointNumber(100).negate(), splt1.getQuantity());
     assertEquals(new FixedPointNumber(100).negate(), splt1.getValue());
     assertEquals("Generated by TestGnucashWritableTransactionImpl.test03_1 (1)", splt1.getDescription());

     assertEquals(ACCT_20_ID, splt2.getAccountID());
     assertEquals(GnucashTransactionSplit.Action.INCREASE, splt2.getAction());
     assertEquals(GnucashTransactionSplit.Action.INCREASE.getLocaleString(), splt2.getActionStr());
     assertEquals(new FixedPointNumber(100), splt2.getQuantity());
     assertEquals(new FixedPointNumber(100), splt2.getValue());
     assertEquals("Generated by TestGnucashWritableTransactionImpl.test03_1 (2)", splt2.getDescription());
  }

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }
}
