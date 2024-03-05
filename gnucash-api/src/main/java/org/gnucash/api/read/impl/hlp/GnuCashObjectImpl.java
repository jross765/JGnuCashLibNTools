package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnuCashFile;
import org.gnucash.api.read.hlp.GnuCashObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper-Class used to implement functions all gnucash-objects support.
 */
public class GnuCashObjectImpl implements GnuCashObject {

	private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashObjectImpl.class);

	// -----------------------------------------------------------------

	private final GnuCashFile gcshFile;

	// -----------------------------------------------------------------

	public GnuCashObjectImpl(final GnuCashFile gcshFile) {
		super();

		this.gcshFile = gcshFile;
	}

	// -----------------------------------------------------------------

	@Override
	public GnuCashFile getGnuCashFile() {
		return gcshFile;
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		return "GnuCashObjectImpl@" + hashCode();
	}

}
