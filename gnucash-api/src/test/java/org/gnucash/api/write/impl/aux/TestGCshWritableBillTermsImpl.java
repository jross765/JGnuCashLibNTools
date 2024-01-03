package org.gnucash.api.write.impl.aux;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.read.impl.aux.TestGCshBillTermsImpl;
import org.gnucash.api.write.aux.GCshWritableBillTerms;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
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

    private GnucashWritableFileImpl gcshInFile = null;
    private GnucashFileImpl gcshOutFile = null;

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
	    gcshInFile = new GnucashWritableFileImpl(gcshInFileStream);
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
    // GnucashWritableFileImpl.getWritableTaxTableByID() are actually
    // complete (as complete as returned be GnucashFileImpl.getBillTermsByID().
    
    @Test
    public void test01_1() throws Exception
    {
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
    public void test01_2_1_1() throws Exception
    {
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
    public void test01_2_1_2() throws Exception
    {
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
    public void test01_2_2_1() throws Exception
    {
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
    public void test01_2_2_2() throws Exception
    {
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
    public void test01_2_3_1() throws Exception
    {
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
    public void test01_2_3_2() throws Exception
    {
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

    // ::TODO

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

//  @AfterClass
//  public void after() throws Exception
//  {
//      FileUtils.delete(outFileGlob);
//  }

}
