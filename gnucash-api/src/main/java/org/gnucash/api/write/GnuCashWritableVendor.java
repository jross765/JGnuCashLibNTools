package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Vendor that can be modified.
 * 
 * @see GnuCashVendor
 */
public interface GnuCashWritableVendor extends GnuCashVendor, 
                                               GnuCashWritableObject,
                                               HasWritableUserDefinedAttributes
{

    void remove();
   
    // ---------------------------------------------------------------

    /**
     * @see {@link GnuCashVendor#getNumber()}
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
