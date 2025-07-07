package org.gnucash.api.write.impl.hlp;

import org.gnucash.api.generated.GncGncBillTerm;
import org.gnucash.api.read.aux.GCshBillTerms;
import org.gnucash.api.read.impl.aux.GCshBillTermsImpl;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.api.write.impl.aux.GCshWritableBillTermsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileBillTermsManager extends org.gnucash.api.read.impl.hlp.FileBillTermsManager {

	protected static final Logger LOGGER = LoggerFactory.getLogger(FileBillTermsManager.class);

	// ---------------------------------------------------------------

	public FileBillTermsManager(GnuCashWritableFileImpl gcshFile) {
		super(gcshFile);
	}

	// ---------------------------------------------------------------

	/*
	 * Creates the writable version of the returned object.
	 */
	@Override
	protected GCshBillTermsImpl createBillTerms(final GncGncBillTerm jwsdpBllTrm) {
		GCshWritableBillTermsImpl wrtblBllTrm = new GCshWritableBillTermsImpl(jwsdpBllTrm, (GnuCashWritableFileImpl) gcshFile);
		LOGGER.debug("createBillTerm: Generated new writable bill terms: " + wrtblBllTrm.getID());
		return wrtblBllTrm;
	}

	// ---------------------------------------------------------------

	public void addBillTerms(GCshBillTerms bllTrm) {
		if ( bllTrm == null ) {
			throw new IllegalArgumentException("argument <bllTrm> is null");
		}
		
		bllTrmMap.put(bllTrm.getID(), bllTrm);
		LOGGER.debug("Added bill terms to cache: " + bllTrm.getID());
	}

	public void removeBillTerms(GCshBillTerms bllTrm) {
		if ( bllTrm == null ) {
			throw new IllegalArgumentException("argument <bllTrm> is null");
		}
		
		bllTrmMap.remove(bllTrm.getID());
		LOGGER.debug("Removed bill terms from cache: " + bllTrm.getID());
	}

}
