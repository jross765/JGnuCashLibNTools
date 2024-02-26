package org.gnucash.api.write;

import org.gnucash.api.read.GnucashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Vendor that can be modified.
 * 
 * @see GnucashVendor
 */
public interface GnucashWritableVendor extends GnucashVendor, 
                                               GnucashWritableObject,
                                               HasWritableUserDefinedAttributes
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

}
