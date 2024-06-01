package org.gnucash.api.currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;

import org.gnucash.api.ConstTest;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class TestSimpleCommodityQuoteTable {
	private GnuCashFile gcshFile = null;
	private ComplexPriceTable complPriceTab = null;
	private SimplePriceTable simplPriceTab = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestSimpleCommodityQuoteTable.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL kmmFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + kmmFileURL + "'");
		InputStream kmmFileStream = null;
		try {
			kmmFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(kmmFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		assertEquals(2, simplPriceTab.getCurrencies().size());
		assertEquals(145.0, simplPriceTab.getConversionFactor("SAP").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(11.265, simplPriceTab.getConversionFactor("MBG").doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test01_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		assertEquals(2, simplPriceTab.getCurrencies().size());
		assertEquals(53.58, simplPriceTab.getConversionFactor("FR0000120644").doubleValue(), ConstTest.DIFF_TOLERANCE);
		assertEquals(43.255, simplPriceTab.getConversionFactor("DE000BASF111").doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_1() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber val = new FixedPointNumber("101.0");
		assertEquals(true, simplPriceTab.convertToBaseCurrency(val, "SAP"));
		assertEquals(14645, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("101.0");
		assertEquals(true, simplPriceTab.convertToBaseCurrency(val, "MBG"));
		assertEquals(1137.765, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_2() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber val = new FixedPointNumber("14645");
		assertEquals(true, simplPriceTab.convertFromBaseCurrency(val, "SAP"));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("1137.765");
		assertEquals(true, simplPriceTab.convertFromBaseCurrency(val, "MBG"));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_3() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber val = new FixedPointNumber("101.0");
		assertEquals(true, simplPriceTab.convertToBaseCurrency(val, "FR0000120644"));
		assertEquals(5411.58, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("101.0");
		assertEquals(true, simplPriceTab.convertToBaseCurrency(val, "DE000BASF111"));
		assertEquals(4368.755, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}

	@Test
	public void test02_4() throws Exception {
		complPriceTab = gcshFile.getCurrencyTable();
		assertNotEquals(null, complPriceTab);

		simplPriceTab = complPriceTab.getByNamespace(GCshCmdtyCurrNameSpace.SecIdType.ISIN.toString());
		assertNotEquals(null, simplPriceTab);

		FixedPointNumber val = new FixedPointNumber("5411.58");
		assertEquals(true, simplPriceTab.convertFromBaseCurrency(val, "FR0000120644"));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);

		val = new FixedPointNumber("4368.755");
		assertEquals(true, simplPriceTab.convertFromBaseCurrency(val, "DE000BASF111"));
		assertEquals(101.0, val.doubleValue(), ConstTest.DIFF_TOLERANCE);
	}
}
