package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.BillTermsTypeException;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGCshWritableBillTermsImpl {
    public  static final GCshID BLLTRM_1_ID = TestGCshBillTermsImpl.BLLTRM_1_ID;
    public  static final GCshID BLLTRM_2_ID = TestGCshBillTermsImpl.BLLTRM_2_ID;
    public  static final GCshID BLLTRM_3_ID = TestGCshBillTermsImpl.BLLTRM_3_ID;
    
    // -----------------------------------------------------------------

    private GnuCashWritableFileImpl gcshInFile = null;
    private GnuCashFileImpl gcshOutFile = null;

    private GCshFileStats gcshInFileStats = null;
    private GCshFileStats gcshOutFileStats = null;

    // https://stackoverflow.com/questions/11884141/deleting-file-and-directory-in-junit
    @SuppressWarnings("exports")
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    // -----------------------------------------------------------------

    public static void main(String[] args) throws Exception {
	junit.textui.TestRunner.run(suite());
    }

    @SuppressWarnings("exports")
    public static junit.framework.Test suite() {
	return new JUnit4TestAdapter(TestGCshWritableBillTermsImpl.class);
    }

    @Before
    public void initialize() throws Exception {
	ClassLoader classLoader = getClass().getClassLoader();
	// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
	// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
	InputStream gcshInFileStream = null;
	try {
	    gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
	} catch (Exception exc) {
	    System.err.println("Cannot generate input stream from resource");
	    return;
	}

	try {
	    gcshInFile = new GnuCashWritableFileImpl(gcshInFileStream);
	} catch (Exception exc) {
	    System.err.println("Cannot parse GnuCash in-file");
	    exc.printStackTrace();
	}
    }

    // -----------------------------------------------------------------
    // PART 1: Read existing objects as modifiable ones
    // (and see whether they are fully symmetrical to their read-only
    // counterparts)
    // -----------------------------------------------------------------
    // Cf. TestGCshBillTermsImpl.testxyz
    //
    // Check whether the GCshWritableBillTerms objects returned by
    // GnuCashWritableFileImpl.getWritableTaxTableByID() are actually
    // complete (as complete as returned be GnuCashFileImpl.getBillTermsByID().
    
    @Test
    public void test01_1() throws Exception {
	Collection<GCshWritableBillTerms> bllTrmList = gcshInFile.getWritableBillTerms();

	assertEquals(3, bllTrmList.size());

	// ::TODO: Sort array for predictability
	Object[] bllTrmArr = bllTrmList.toArray();

	// funny, this parent/child relationship full of redundancies...
	assertEquals(BLLTRM_1_ID, ((GCshBillTerms) bllTrmArr[2]).getID());
	assertEquals(BLLTRM_2_ID, ((GCshBillTerms) bllTrmArr[0]).getID());
	assertEquals(BLLTRM_3_ID, ((GCshBillTerms) bllTrmArr[1]).getID());
    }

    @Test
    public void test01_2_1_1() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByID(BLLTRM_1_ID);
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_1_ID, bllTrm.getID());
	assertEquals("sofort", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsDays btDays = bllTrm.getDays();
	assertNotEquals(null, btDays);

	assertEquals(Integer.valueOf(5), btDays.getDueDays());
	assertEquals(null, btDays.getDiscountDays());
	assertEquals(null, btDays.getDiscount());
    }

    @Test
    public void test01_2_1_2() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByName("sofort");
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_1_ID, bllTrm.getID());
	assertEquals("sofort", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsDays btDays = bllTrm.getDays();
	assertNotEquals(null, btDays);

	assertEquals(Integer.valueOf(5), btDays.getDueDays());
	assertEquals(null, btDays.getDiscountDays());
	assertEquals(null, btDays.getDiscount());
    }

    @Test
    public void test01_2_2_1() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByID(BLLTRM_2_ID);
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_2_ID, bllTrm.getID());
	assertEquals("30-10-3", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsDays btDays = bllTrm.getDays();
	assertNotEquals(null, btDays);

	assertEquals(Integer.valueOf(30), btDays.getDueDays());
	assertEquals(Integer.valueOf(10), btDays.getDiscountDays());
	assertEquals(3.0, btDays.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
    }

    @Test
    public void test01_2_2_2() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByName("30-10-3");
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_2_ID, bllTrm.getID());
	assertEquals("30-10-3", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsDays btDays = bllTrm.getDays();
	assertNotEquals(null, btDays);

	assertEquals(Integer.valueOf(30), btDays.getDueDays());
	assertEquals(Integer.valueOf(10), btDays.getDiscountDays());
	assertEquals(3.0, btDays.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
    }

    @Test
    public void test01_2_3_1() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByID(BLLTRM_3_ID);
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_3_ID, bllTrm.getID());
	assertEquals("nächster-monat-mitte", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.PROXIMO, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsProximo btProx = bllTrm.getProximo();
	assertNotEquals(null, btProx);

	assertEquals(Integer.valueOf(15), btProx.getDueDay());
	assertEquals(Integer.valueOf(3), btProx.getDiscountDay());
	assertEquals(2.0, btProx.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
    }

    @Test
    public void test01_2_3_2() throws Exception {
	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByName("nächster-monat-mitte");
	assertNotEquals(null, bllTrm);
	// System.err.println(bllTrm);

	assertEquals(BLLTRM_3_ID, bllTrm.getID());
	assertEquals("nächster-monat-mitte", bllTrm.getName());
	assertEquals(GCshBillTerms.Type.PROXIMO, bllTrm.getType());

	assertEquals(null, bllTrm.getParentID());
	assertEquals(0, bllTrm.getChildren().size());

	GCshBillTermsProximo btProx = bllTrm.getProximo();
	assertNotEquals(null, btProx);

	assertEquals(Integer.valueOf(15), btProx.getDueDay());
	assertEquals(Integer.valueOf(3), btProx.getDiscountDay());
	assertEquals(2.0, btProx.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE);
    }

    // -----------------------------------------------------------------
    // PART 2: Modify existing objects
    // -----------------------------------------------------------------
    // Check whether the GCshWritableBillTerms objects returned by
    // can actually be modified -- both in memory and persisted in file.

    @Test
    public void test02_1() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByID(BLLTRM_1_ID);
	assertNotEquals(null, bllTrm);

	assertEquals(BLLTRM_1_ID, bllTrm.getID());

	// ----------------------------
	// Modify the object

	bllTrm.setName("Ruby Cubey");
	
	try {
	    bllTrm.setType(GCshBillTerms.Type.PROXIMO); // illagal call
	    assertEquals(0, 1);
	} catch ( BillTermsTypeException exc ) {
	    assertEquals(0, 0);
	}
	
	bllTrm.setDescription("Onamata-poeta boum-boum!");

	// ----------------------------
	// Check whether the object can has actually be modified
	// (in memory, not in the file yet).

	test02_1_check_memory(bllTrm);

	// ----------------------------
	// Now, check whether the modified object can be written to the
	// output file, then re-read from it, and whether is is what
	// we expect it is.

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
	// + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			          // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

	test02_1_check_persisted(outFile);
    }

    @Test
    public void test02_2() throws Exception {
	gcshInFileStats = new GCshFileStats(gcshInFile);

	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	GCshWritableBillTerms bllTrm = gcshInFile.getWritableBillTermsByID(BLLTRM_2_ID);
	assertNotEquals(null, bllTrm);

	assertEquals(BLLTRM_2_ID, bllTrm.getID());

	// ----------------------------
	// Modify the object

	bllTrm.setName("Senso Benso");
	bllTrm.setDescription("Une souris verte");
	bllTrm.getWritableDays().setDueDays(31);
	bllTrm.getWritableDays().setDiscountDays(11);
	bllTrm.getWritableDays().setDiscount(new FixedPointNumber(4.1));

	// ----------------------------
	// Check whether the object can has actually be modified
	// (in memory, not in the file yet).

	test02_2_check_memory(bllTrm);

	// ----------------------------
	// Now, check whether the modified object can be written to the
	// output file, then re-read from it, and whether is is what
	// we expect it is.

	File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
	// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
	// + outFile.getPath() + "'");
	outFile.delete(); // sic, the temp. file is already generated (empty),
			          // and the GnuCash file writer does not like that.
	gcshInFile.writeFile(outFile);

	test02_2_check_persisted(outFile);
    }
    
    // ---------------------------------------------------------------

    private void test02_1_check_memory(GCshWritableBillTerms bllTrm) throws Exception {
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	assertEquals(BLLTRM_1_ID, bllTrm.getID()); // unchanged
	assertEquals("Ruby Cubey", bllTrm.getName()); // changed
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType()); // unchanged (sic)
	assertEquals("Onamata-poeta boum-boum!", bllTrm.getDescription()); // changed

	assertEquals(null, bllTrm.getParentID()); // unchanged
	assertEquals(0, bllTrm.getChildren().size()); // unchanged

	GCshBillTermsDays btDays = bllTrm.getDays(); // unchanged
	assertNotEquals(null, btDays); // unchanged

	assertEquals(Integer.valueOf(5), btDays.getDueDays()); // unchanged
	assertEquals(null, btDays.getDiscountDays()); // unchanged
	assertEquals(null, btDays.getDiscount()); // unchanged
    }

    private void test02_1_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnuCashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	GCshBillTerms bllTrm = gcshOutFile.getBillTermsByID(BLLTRM_1_ID);
	assertNotEquals(null, bllTrm);

	assertEquals(BLLTRM_1_ID, bllTrm.getID()); // unchanged
	assertEquals("Ruby Cubey", bllTrm.getName()); // changed
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType()); // unchanged (sic)
	assertEquals("Onamata-poeta boum-boum!", bllTrm.getDescription()); // changed

	assertEquals(null, bllTrm.getParentID()); // unchanged
	assertEquals(0, bllTrm.getChildren().size()); // unchanged

	GCshBillTermsDays btDays = bllTrm.getDays(); // unchanged
	assertNotEquals(null, btDays); // unchanged

	assertEquals(Integer.valueOf(5), btDays.getDueDays()); // unchanged
	assertEquals(null, btDays.getDiscountDays()); // unchanged
	assertEquals(null, btDays.getDiscount()); // unchanged
    }

    // ----------------------------

    private void test02_2_check_memory(GCshWritableBillTerms bllTrm) throws Exception {
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	assertEquals(BLLTRM_2_ID, bllTrm.getID()); // unchanged
	assertEquals("Senso Benso", bllTrm.getName()); // changed
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType()); // unchanged
	assertEquals("Une souris verte", bllTrm.getDescription()); // changed
	
	assertEquals(null, bllTrm.getParentID()); // unchanged
	assertEquals(0, bllTrm.getChildren().size()); // unchanged

	GCshBillTermsDays btDays = bllTrm.getDays(); // unchanged
	assertNotEquals(null, btDays); // unchanged
	      
	assertEquals(Integer.valueOf(31), btDays.getDueDays()); // changed
	assertEquals(Integer.valueOf(11), btDays.getDiscountDays()); // changed
	assertEquals(4.1, btDays.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
    }

    private void test02_2_check_persisted(File outFile) throws Exception {
	gcshOutFile = new GnuCashFileImpl(outFile);
	gcshOutFileStats = new GCshFileStats(gcshOutFile);

	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.RAW));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.COUNTER));
	assertEquals(ConstTest.Stats.NOF_BLLTRM, gcshInFileStats.getNofEntriesBillTerms(GCshFileStats.Type.CACHE));

	GCshBillTerms bllTrm = gcshOutFile.getBillTermsByID(BLLTRM_2_ID);
	assertNotEquals(null, bllTrm);

	assertEquals(BLLTRM_2_ID, bllTrm.getID()); // unchanged
	assertEquals("Senso Benso", bllTrm.getName()); // changed
	assertEquals(GCshBillTerms.Type.DAYS, bllTrm.getType()); // unchanged
	assertEquals("Une souris verte", bllTrm.getDescription()); // changed
	
	assertEquals(null, bllTrm.getParentID()); // unchanged
	assertEquals(0, bllTrm.getChildren().size()); // unchanged

	GCshBillTermsDays btDays = bllTrm.getDays(); // unchanged
	assertNotEquals(null, btDays); // unchanged
	      
	assertEquals(Integer.valueOf(31), btDays.getDueDays()); // changed
	assertEquals(Integer.valueOf(11), btDays.getDiscountDays()); // changed
	assertEquals(4.1, btDays.getDiscount().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
    }

    // -----------------------------------------------------------------
    // PART 3: Create new objects
    // -----------------------------------------------------------------

    // ------------------------------
    // PART 3.1: High-Level
    // ------------------------------

    // ::TODO

    // ------------------------------
    // PART 3.2: Low-Level
    // ------------------------------

    // ::TODO

}
