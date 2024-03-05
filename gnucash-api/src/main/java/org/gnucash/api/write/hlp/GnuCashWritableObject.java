package org.gnucash.api.write.hlp;

import org.gnucash.api.write.GnuCashWritableFile;

/**
 * Interface that all interfaces for writable gnucash-entities shall implement
 */
public interface GnuCashWritableObject {

    /**
     * @return the File we belong to.
     */
    GnuCashWritableFile getWritableGnuCashFile();

}
