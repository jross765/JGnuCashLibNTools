package org.gnucash.api.write;

import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;

/**
 * Vendor that can be modified
 */
public interface GnucashWritableVendor extends GnucashVendor, 
                                               GnucashWritableObject 
{

    void remove();
   
    // ---------------------------------------------------------------

    /**
     * @see {@link GnucashVendor#getNumber()}
     * @param number the user-assigned number of this Vendor (may contain
     *               non-digits)
     */
    void setNumber(String number);

    void setName(String name);

    void setAddress(GCshAddress adr);

    /**
     * @param notes user-defined notes about the customer (may be null)
     */
    void setNotes(String notes);

    // ---------------------------------------------------------------

    GCshWritableAddress getWritableAddress();

    GCshWritableAddress getAddress();

    // ---------------------------------------------------------------

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);
}
