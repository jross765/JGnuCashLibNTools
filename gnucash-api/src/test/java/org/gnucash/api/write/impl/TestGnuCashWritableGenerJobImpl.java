package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashGenerJobImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritableGenerJob;
import org.gnucash.base.basetypes.simple.GCshGenerJobID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;

public class TestGnuCashWritableGenerJobImpl {
	private static final GCshGenerJobID GENER_JOB_1_ID = TestGnuCashGenerJobImpl.GENER_JOB_1_ID;
	private static final GCshGenerJobID GENER_JOB_2_ID = TestGnuCashGenerJobImpl.GENER_JOB_2_ID;
	private static final GCshGenerJobID GENER_JOB_3_ID = TestGnuCashGenerJobImpl.GENER_JOB_3_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshID newID = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritableGenerJobImpl.class);
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
	// Cf. TestGnuCashGenerJobImpl.test01_1/02_1
	//
	// Check whether the GnuCashWritableGenerJob objects returned by
	// GnuCashWritableFileImpl.getWritableGenerJobByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getGenerJobByID().

	@Test
	public void test01_Cust01() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		assertEquals(GENER_JOB_1_ID, job.getID());
		assertEquals("000001", job.getNumber());
		assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, job.getOwnerType());
		assertEquals("Do more for others", job.getName());
	}

	@Test
	public void test01_Cust02() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		assertEquals(0, job.getPaidInvoices().size());
		assertEquals(1, job.getUnpaidInvoices().size());
	}

	@Test
	public void test01_Cust03() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);

		GCshID custID = new GCshID("f44645d2397946bcac90dff68cc03b76");
		assertEquals(custID, job.getOwnerID());
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_Vend01() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		assertEquals(GENER_JOB_2_ID, job.getID());
		assertEquals("000002", job.getNumber());
		assertEquals(GnuCashGenerJob.TYPE_VENDOR, job.getOwnerType());
		assertEquals("Let's buy help", job.getName());
	}

	@Test
	public void test01_Vend02() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		assertEquals(0, job.getPaidInvoices().size());
		assertEquals(1, job.getUnpaidInvoices().size());
	}

	@Test
	public void test01_Vend03() throws Exception {
		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_2_ID);
		assertNotEquals(null, job);

		GCshID vendID = new GCshID("4f16fd55c0d64ebe82ffac0bb25fe8f5");
		assertEquals(vendID, job.getOwnerID());
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GnuCashWritableGenerJob objects returned by
	// can actually be modified -- both in memory and persisted in file.

	// This part is empty, as the class GnuCashWritableGenerJob is abstract.

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// This part is empty, as the class GnuCashWritableGenerJob is abstract.

	// -----------------------------------------------------------------
	// PART 4: Delete objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 4.1: High-Level
	// ------------------------------

	@Test
	public void test04_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER)); // sic, because not persisted yet
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_1_ID);
		assertNotEquals(null, job);
		assertEquals(GENER_JOB_1_ID, job.getID());

		// Check if modifiable
		assertEquals(1, job.getInvoices().size()); // there are invoices (paid or unpaid, doesn't matter)

		// Variant 1
		try {
			gcshInFile.removeGenerJob(job); // Correctly fails because invoice in not modifiable
			assertEquals(1, 0);
		} catch ( IllegalStateException exc ) {
			assertEquals(0, 0);
		}

		// Variant 2
		// ::TODO: Variant does not exist
//		try {
//			job.remove(); // Correctly fails because invoice in not modifiable
//			assertEquals(1, 0);
//		} catch ( IllegalStateException exc ) {
//			assertEquals(0, 0);
//		}
	}
	
	@Test
	public void test04_2() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER)); // sic, because not persisted yet
		assertEquals(ConstTest.Stats.NOF_GENER_JOB, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));

		GnuCashWritableGenerJob job = gcshInFile.getWritableGenerJobByID(GENER_JOB_3_ID);
		assertNotEquals(null, job);
		assertEquals(GENER_JOB_3_ID, job.getID());

		// Check if modifiable
		assertEquals(0, job.getInvoices().size()); // there are no invoices (paid or unpaid, doesn't matter)

		// Core (variant-specific):
		gcshInFile.removeGenerJob(job);

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test04_2_check_memory(job);

		// ----------------------------
		// Now, check whether the modified object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableGenerJobImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test04_2_check_persisted(outFile);
	}
	
	// ---------------------------------------------------------------

	private void test04_2_check_memory(GnuCashWritableGenerJob job) throws Exception {
		assertEquals(ConstTest.Stats.NOF_GENER_JOB - 1, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB    , gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER)); // sic, because not persisted yet
		assertEquals(ConstTest.Stats.NOF_GENER_JOB - 1, gcshInFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));

		// CAUTION / ::TODO
		// Old Object still exists and is unchanged
		// Exception: no splits any more
		// Don't know what to do about this oddity right now,
		// but it needs to be addressed at some point.
		assertEquals("000003", job.getNumber());
		assertEquals(GnuCashGenerJob.TYPE_CUSTOMER, job.getOwnerType());
		assertEquals("Dummy-Auftrag (Kunde)", job.getName());
		
		// However, the jobomer cannot newly be instantiated any more,
		// just as you would expect.
		try {
			GnuCashWritableGenerJob jobNow1 = gcshInFile.getWritableGenerJobByID(GENER_JOB_3_ID);
			assertEquals(1, 0);
		} catch ( Exception exc ) {
			assertEquals(0, 0);
		}
		// Same for a non non-writable instance. 
		// However, due to design asymmetry, no exception is thrown here,
		// but the method just returns null.
		GnuCashGenerJob jobNow2 = gcshInFile.getGenerJobByID(GENER_JOB_3_ID);
		assertEquals(null, jobNow2);
	}

	private void test04_2_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_GENER_JOB - 1, gcshOutFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB - 1, gcshOutFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_GENER_JOB - 1, gcshOutFileStats.getNofEntriesGenerJobs(GCshFileStats.Type.CACHE));

		// The transaction does not exist any more, just as you would expect.
		// However, no exception is thrown, as opposed to test04_1_check_memory()
		GnuCashGenerJob job = gcshOutFile.getGenerJobByID(GENER_JOB_3_ID);
		assertEquals(null, job); // sic
	}

	// ------------------------------
	// PART 4.2: Low-Level
	// ------------------------------
	
	// ::TODO

}
