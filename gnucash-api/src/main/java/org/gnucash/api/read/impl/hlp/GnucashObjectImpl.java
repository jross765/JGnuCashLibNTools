package org.gnucash.api.read.impl.hlp;

import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.hlp.GnucashObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper-Class used to implement functions all gnucash-objects support.
 */
public class GnucashObjectImpl implements GnucashObject {

	private static final Logger LOGGER = LoggerFactory.getLogger(GnucashObjectImpl.class);

	// -----------------------------------------------------------------

	private final GnucashFile gcshFile;

	// -----------------------------------------------------------------

	public GnucashObjectImpl(final GnucashFile gcshFile) {
		super();

		this.gcshFile = gcshFile;
	}

	// -----------------------------------------------------------------

	@Override
	public GnucashFile getGnucashFile() {
		return gcshFile;
	}

	// -----------------------------------------------------------------

	@Override
	public String toString() {
		return "GnucashObjectImpl@" + hashCode();
	}

}
