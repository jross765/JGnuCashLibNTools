package org.gnucash.api.write.hlp;

import org.gnucash.api.write.GnucashWritableFile;

/**
 * Interface that all interfaces for writable gnucash-entities shall implement
 */
public interface GnucashWritableObject {

    /**
     * @return the File we belong to.
     */
    GnucashWritableFile getWritableGnucashFile();

}
