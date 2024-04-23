package org.gnucash.api.write.impl.hlp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;

import org.gnucash.api.ConstTest;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.junit.Before;
import org.junit.Test;

import junit.framework.JUnit4TestAdapter;

public class TestFileCommodityManager {

	// ---------------------------------------------------------------

	private GnuCashWritableFileImplTestHelper gcshInFile = null;

	private org.gnucash.api.write.impl.hlp.FileCommodityManager mgr = null;

	// -----------------------------------------------------------------

	public static void main(String[] args) throws Exception {
		junit.textui.TestRunner.run(suite());
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestFileCommodityManager.class);
	}

	@Before
	public void initialize() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		// URL gcshFileURL = classLoader.getResource(Const.GCsh_FILENAME);
		// System.err.println("GnuCash test file resource: '" + gcshFileURL + "'");
		InputStream gcshInFileStream = null;
		try {
			gcshInFileStream = classLoader.getResourceAsStream(ConstTest.GCSH_FILENAME_IN);
		} catch (Exception exc) {
			System.err.println("Cannot generate input stream from resource");
			return;
		}

		try {
			gcshInFile = new GnuCashWritableFileImplTestHelper(gcshInFileStream);
		} catch (Exception exc) {
			System.err.println("Cannot parse GnuCash in-file");
			exc.printStackTrace();
		}
	}

	// -----------------------------------------------------------------
	
	@Test
	public void test01() throws Exception {
		mgr = gcshInFile.getCommodityManager();
		
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, mgr.getNofEntriesCommodityMap());
		assertEquals(ConstTest.Stats.NOF_CMDTY_ALL, mgr.getCommodities().size());
	}

	@Test
	public void test02() throws Exception {
		mgr = gcshInFile.getCommodityManager();
		
		Collection<GnuCashCommodity> cmdtyColl = mgr.getCommodities();
		GCshCmdtyCurrID qualifID = new GCshCmdtyCurrID("ISIN", "DE000BASF111");
		GnuCashCommodity cmdty = mgr.getCommodityByQualifID(qualifID);
		assertTrue(cmdtyColl.contains(cmdty));
	}

}
