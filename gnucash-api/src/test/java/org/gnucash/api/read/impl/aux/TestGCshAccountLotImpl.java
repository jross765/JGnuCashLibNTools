package org.gnucash.api.read.impl.aux;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.aux.GCshAccountLot;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.TestGnuCashAccountImpl;
import org.gnucash.base.basetypes.simple.GCshAcctID;
import org.gnucash.base.basetypes.simple.GCshLotID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestGCshAccountLotImpl {
	public static final GCshAcctID ACCT_8_ID = TestGnuCashAccountImpl.ACCT_8_ID; // stocck account SAP
	public static final GCshLotID ACCTLOT_1_ID = new GCshLotID("ef15827f413a4eeeaf8a7b492252b443"); // bei Aktienkonto SAP

	// -----------------------------------------------------------------

	private GnuCashFile gcshFile = null;
	// private GCshAccountLot acctLot = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	@SuppressWarnings("exports")
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestGCshAccountLotImpl.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCSH_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshFileStream = null;
		try {
			gcshFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshFile = new GnuCashFileImpl(gcshFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------

	@Test
	public void test01() throws Exception {
		GnuCashAccount stockAcct = gcshFile.getAccountByID(ACCT_8_ID);
		List<GCshAccountLot> lotList = stockAcct.getLots();

		assertEquals(1, lotList.size());
	}

	@Test
	public void test02() throws Exception {
		GnuCashAccount stockAcct = gcshFile.getAccountByID(ACCT_8_ID);
		GCshAccountLot lot = stockAcct.getLotByID(ACCTLOT_1_ID);

		assertEquals("Charge 0", lot.getTitle());
		assertEquals("Zur korrekten Vorbereitung des Jahresabschlusses ist ein vollständiges Abbilden der Posten-Logik notwendig.", lot.getNotes());
	}
}
