package org.gnucash.api.write.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID_Exchange;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.GnuCashPrice.Type;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashPriceImpl;
import org.gnucash.api.read.impl.aux.GCshFileStats;
import org.gnucash.api.write.GnuCashWritablePrice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestGnuCashWritablePriceImpl {
	private static final GCshID PRC_1_ID = TestGnuCashPriceImpl.PRC_1_ID;
	private static final GCshID PRC_2_ID = TestGnuCashPriceImpl.PRC_2_ID;
	private static final GCshID PRC_3_ID = TestGnuCashPriceImpl.PRC_3_ID;
	private static final GCshID PRC_4_ID = TestGnuCashPriceImpl.PRC_4_ID;

	private static final GCshID PRC_10_ID = TestGnuCashPriceImpl.PRC_10_ID;
	private static final GCshID PRC_11_ID = TestGnuCashPriceImpl.PRC_11_ID;
	private static final GCshID PRC_12_ID = TestGnuCashPriceImpl.PRC_12_ID;
	private static final GCshID PRC_13_ID = TestGnuCashPriceImpl.PRC_13_ID;

	// -----------------------------------------------------------------

	private GnuCashWritableFileImpl gcshInFile = null;
	private GnuCashFileImpl gcshOutFile = null;

	private GCshFileStats gcshInFileStats = null;
	private GCshFileStats gcshOutFileStats = null;

	private GCshID newID;

	GCshCmdtyID cmdtyID11 = null;
	GCshCmdtyID_Exchange cmdtyID12 = null;

	GCshCmdtyID cmdtyID21 = null;
	GCshCmdtyID_Exchange cmdtyID22 = null;

	GCshCurrID currID1 = null;
	GCshCurrID currID2 = null;

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
		return new JUnit4TestAdapter(TestGnuCashWritablePriceImpl.class);
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

		// ---

		cmdtyID11 = new GCshCmdtyID("EURONEXT", "MBG");
		cmdtyID12 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "MBG");

		cmdtyID21 = new GCshCmdtyID("EURONEXT", "SAP");
		cmdtyID22 = new GCshCmdtyID_Exchange(GCshCmdtyCurrNameSpace.Exchange.EURONEXT, "SAP");

		currID1 = new GCshCurrID("EUR");
		currID2 = new GCshCurrID("USD");
	}

	// -----------------------------------------------------------------
	// PART 1: Read existing objects as modifiable ones
	// (and see whether they are fully symmetrical to their read-only
	// counterparts)
	// -----------------------------------------------------------------
	// Cf. TestGCshPriceImpl.test01/02_x
	//
	// Check whether the GnuCashWritableCustomer objects returned by
	// GCshWritablePriceImpl.getWritableCustomerByID() are actually
	// complete (as complete as returned be GnuCashFileImpl.getPriceByID().

	@Test
	public void test01() throws Exception {
		Collection<GnuCashWritablePrice> prcColl = gcshInFile.getWritablePrices();
		List<GnuCashWritablePrice> prcList = new ArrayList<GnuCashWritablePrice>(prcColl);
		prcList.sort(Comparator.naturalOrder());

		assertEquals(ConstTest.Stats.NOF_PRC, prcList.size());
		assertEquals(PRC_10_ID, prcList.get(0).getID());
		assertEquals(PRC_11_ID, prcList.get(1).getID());
		assertEquals(PRC_12_ID, prcList.get(2).getID());
		assertEquals(PRC_13_ID, prcList.get(3).getID());
	}

	@Test
	public void test01_2_1() throws Exception {
		GnuCashWritablePrice prc = gcshInFile.getWritablePriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID());
		assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(cmdtyID11.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID12.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID11, prc.getFromCommodityQualifID());
		assertNotEquals(cmdtyID12, prc.getFromCommodityQualifID()); // sic
		assertEquals("Mercedes-Benz Group AG", prc.getFromCommodity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(Type.TRANSACTION, prc.getType());
		assertEquals(LocalDate.of(2023, 7, 1), prc.getDate());
		assertEquals(22.53, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prc.getFromCurrencyCode(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_2_2() throws Exception {
		GnuCashWritablePrice prc = gcshInFile.getWritablePriceByID(PRC_2_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_2_ID, prc.getID());
		assertEquals(cmdtyID21.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(cmdtyID21.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID22.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID21, prc.getFromCommodityQualifID());
		assertNotEquals(cmdtyID22, prc.getFromCommodityQualifID()); // sic
		assertEquals("SAP SE", prc.getFromCommodity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(Type.UNKNOWN, prc.getType());
		assertEquals(LocalDate.of(2023, 7, 20), prc.getDate());
		assertEquals(145.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prc.getFromCurrencyCode(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_2_3() throws Exception {
		GnuCashWritablePrice prc = gcshInFile.getWritablePriceByID(PRC_3_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_3_ID, prc.getID());
		assertEquals(cmdtyID21.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(cmdtyID21.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID22.toString(), prc.getFromCommodityQualifID().toString());
		assertEquals(cmdtyID21, prc.getFromCommodityQualifID());
		assertNotEquals(cmdtyID22, prc.getFromCommodityQualifID()); // sic
		assertEquals("SAP SE", prc.getFromCommodity().getName());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(Type.TRANSACTION, prc.getType());
		assertEquals(LocalDate.of(2023, 7, 18), prc.getDate());
		assertEquals(125.0, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE);

		try {
			GCshCurrID dummy = prc.getFromCurrencyQualifID(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			String dummy = prc.getFromCurrencyCode(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			GnuCashCommodity dummy = prc.getFromCurrency(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	@Test
	public void test01_2_4() throws Exception {
		GnuCashWritablePrice prc = gcshInFile.getWritablePriceByID(PRC_4_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_4_ID, prc.getID());
		assertEquals(currID2.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(currID2.toString(), prc.getFromCurrencyQualifID().toString());
		assertEquals("USD", prc.getFromCurrencyCode());
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString());
		assertEquals("EUR", prc.getToCurrencyCode());
		assertEquals(null, prc.getType());
		assertEquals(LocalDate.of(2023, 10, 1), prc.getDate());
		assertEquals(new FixedPointNumber("100/93").doubleValue(), prc.getValue().doubleValue(),
				ConstTest.DIFF_TOLERANCE);

		try {
			GCshCmdtyID dummy = prc.getFromCommodityQualifID(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}

		try {
			GnuCashCommodity dummy = prc.getFromCommodity(); // illegal call in this context
			assertEquals(1, 0);
		} catch (Exception exc) {
			assertEquals(0, 0);
		}
	}

	// -----------------------------------------------------------------
	// PART 2: Modify existing objects
	// -----------------------------------------------------------------
	// Check whether the GCshWritablePrice objects returned by
	// can actually be modified -- both in memory and persisted in file.

	@Test
	public void test02_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		GnuCashWritablePrice prc = gcshInFile.getWritablePriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID());

		// ----------------------------
		// Modify the object

		prc.setDate(LocalDate.of(2019, 1, 1));
		prc.setValue(new FixedPointNumber(21.20));

		// ::TODO not possible yet
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").remove()
		// trx.getSplitByID("7abf90fe15124254ac3eb7ec33f798e7").setXYZ()

		// ----------------------------
		// Check whether the object can has actually be modified
		// (in memory, not in the file yet).

		test02_1_check_memory(prc);

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
		// ::TODO
	}

	private void test02_1_check_memory(GnuCashWritablePrice prc) throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		assertEquals(PRC_1_ID, prc.getID()); // unchanged
		assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString()); // unchanged
		assertEquals(cmdtyID11.toString(), prc.getFromCommodityQualifID().toString()); // unchanged
		assertEquals(cmdtyID12.toString(), prc.getFromCommodityQualifID().toString()); // unchanged
		assertEquals(cmdtyID11, prc.getFromCommodityQualifID()); // unchanged
		assertNotEquals(cmdtyID12, prc.getFromCommodityQualifID()); // unchanged, sic
		assertEquals("Mercedes-Benz Group AG", prc.getFromCommodity().getName()); // unchanged
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString()); // unchanged
		assertEquals("EUR", prc.getToCurrencyCode()); // unchanged
		assertEquals(Type.TRANSACTION, prc.getType()); // unchanged
		assertEquals(LocalDate.of(2019, 1, 1), prc.getDate()); // changed
		assertEquals(21.20, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	private void test02_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_PRC, gcshOutFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshOutFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshOutFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		GnuCashPrice prc = gcshOutFile.getPriceByID(PRC_1_ID);
		assertNotEquals(null, prc);

		assertEquals(PRC_1_ID, prc.getID()); // unchanged
		assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString()); // unchanged
		assertEquals(cmdtyID11.toString(), prc.getFromCommodityQualifID().toString()); // unchanged
		assertEquals(cmdtyID12.toString(), prc.getFromCommodityQualifID().toString()); // unchanged
		assertEquals(cmdtyID11, prc.getFromCommodityQualifID()); // unchanged
		assertNotEquals(cmdtyID12, prc.getFromCommodityQualifID()); // unchanged, sic
		assertEquals("Mercedes-Benz Group AG", prc.getFromCommodity().getName()); // unchanged
		assertEquals("CURRENCY:EUR", prc.getToCurrencyQualifID().toString()); // unchanged
		assertEquals("EUR", prc.getToCurrencyCode()); // unchanged
		assertEquals(Type.TRANSACTION, prc.getType()); // unchanged
		assertEquals(LocalDate.of(2019, 1, 1), prc.getDate()); // changed
		assertEquals(21.20, prc.getValue().doubleValue(), ConstTest.DIFF_TOLERANCE); // changed
	}

	// -----------------------------------------------------------------
	// PART 3: Create new objects
	// -----------------------------------------------------------------

	// ------------------------------
	// PART 3.1: High-Level
	// ------------------------------

	@Test
	public void test03_1_1() throws Exception {
		gcshInFileStats = new GCshFileStats(gcshInFile);

		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		GnuCashWritablePrice prc = gcshInFile.createWritablePrice(cmdtyID11, currID1, LocalDate.of(1910, 5, 1));

		// ----------------------------
		// Check whether the object can has actually be created
		// (in memory, not in the file yet).

		test03_1_1_check_memory(prc);

		// ----------------------------
		// Now, check whether the created object can be written to the
		// output file, then re-read from it, and whether is is what
		// we expect it is.

		File outFile = folder.newFile(ConstTest.GCSH_FILENAME_OUT);
		// System.err.println("Outfile for TestGnuCashWritableCustomerImpl.test01_1: '"
		// + outFile.getPath() + "'");
		outFile.delete(); // sic, the temp. file is already generated (empty),
		// and the GnuCash file writer does not like that.
		gcshInFile.writeFile(outFile);

		test03_1_1_check_persisted(outFile);
	}

	private void test03_1_1_check_memory(GnuCashWritablePrice prc) throws Exception {
		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		newID = prc.getID();
		assertEquals(LocalDate.of(1910, 5, 1), prc.getDate());
		assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(currID1, prc.getToCurrencyQualifID());
	}

	private void test03_1_1_check_persisted(File outFile) throws Exception {
		gcshOutFile = new GnuCashFileImpl(outFile);
		gcshOutFileStats = new GCshFileStats(gcshOutFile);

		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.RAW));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.COUNTER));
		assertEquals(ConstTest.Stats.NOF_PRC + 1, gcshInFileStats.getNofEntriesPrices(GCshFileStats.Type.CACHE));

		GnuCashPrice prc = gcshOutFile.getPriceByID(newID);
		assertNotEquals(null, prc);

		assertEquals(newID, prc.getID());
		assertEquals(LocalDate.of(1910, 5, 1), prc.getDate());
		assertEquals(cmdtyID11.toString(), prc.getFromCmdtyCurrQualifID().toString());
		assertEquals(currID1, prc.getToCurrencyQualifID());
	}

	// ------------------------------
	// PART 3.2: Low-Level
	// ------------------------------

	// ::TODO

}
