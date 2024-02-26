package org.gnucash.api.read.hlp;

import org.gnucash.api.read.GnucashFile;

/**
 * Interface all gnucash-entities implement.
 */
public interface GnucashObject {

    /**
     * @return the file we belong to.
     */
    GnucashFile getGnucashFile();

}
