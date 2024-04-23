package org.gnucash.api.read.impl.hlp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.gnucash.api.read.impl.GnuCashFileImpl;

public class GnuCashFileImplTestHelper extends GnuCashFileImpl
{
	// ---------------------------------------------------------------

	public GnuCashFileImplTestHelper(final File pFile) throws IOException {
		super(pFile);
	}
	
	public GnuCashFileImplTestHelper(final InputStream is) throws IOException {
		super(is);
	}

	// ---------------------------------------------------------------
	// The methods in this section are For test purposes only

	@SuppressWarnings("exports")
	public FileCommodityManager getCommodityManager() {
		return cmdtyMgr;
	}

	@SuppressWarnings("exports")
	public FilePriceManager getPriceManager() {
		return prcMgr;
	}

}
