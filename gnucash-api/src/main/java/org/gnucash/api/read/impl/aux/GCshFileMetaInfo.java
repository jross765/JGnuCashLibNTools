package org.gnucash.api.read.impl.aux;

import org.gnucash.api.generated.GncV2;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshFileMetaInfo {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(GCshFileMetaInfo.class);

	// ---------------------------------------------------------------

	private GnuCashFileImpl gcshFile = null;
	
	private GncV2 rootElt = null;

	// ---------------------------------------------------------------

	public GCshFileMetaInfo(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		this.rootElt = gcshFile.getRootElement();
	}

	// ---------------------------------------------------------------

	public String getSchemaVersion() {
		return "2"; // ::MAGIC
	}

	public String getBookVersion() {
		return rootElt.getGncBook().getVersion();
	}

	public String getBookID() {
		return rootElt.getGncBook().getBookId().getValue();
	}

}
