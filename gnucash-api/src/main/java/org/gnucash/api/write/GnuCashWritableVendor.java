package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAddress;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Vendor that can be modified.
 * 
 * @see GnuCashVendor
 */
public interface GnuCashWritableVendor extends GnuCashVendor, 
                                               GnuCashWritableObject,
                                               HasWritableAddress,
                                               HasWritableUserDefinedAttributes
{

	/** 
	 * Deletes the vendor.
	 */
    void remove();
   
    // ---------------------------------------------------------------

    /**
     * Sets the vendor's number.
     * 
     * @see {@link GnuCashVendor#getNumber()}
     * @param number the user-assigned number of this vendor (may contain
     *               non-digits)
     */
    void setNumber(String number);

    /**
     * Sets the vendor's name.
     * 
     * @param name
     */
    void setName(String name);

//    void setAddress(GCshAddress adr);

    /**
     * Sets the vendor's notes.
     * 
     * @param notes user-defined notes about the vendor (may be null)
     */
    void setNotes(String notes);

    // ---------------------------------------------------------------

//    GCshWritableAddress getWritableAddress();

    /**
     * @return the vendor's address (rw-object) 
     */
    GCshWritableAddress getAddress();

}
