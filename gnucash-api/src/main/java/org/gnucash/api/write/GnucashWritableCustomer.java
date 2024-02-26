package org.gnucash.api.write;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Customer that can be modified.
 * 
 * @see GnucashCustomer
 */
public interface GnucashWritableCustomer extends GnucashCustomer, 
                                                 GnucashWritableObject,
                                                 HasWritableUserDefinedAttributes
{

    void remove();

    // ---------------------------------------------------------------

    /**
     * @see {@link GnucashCustomer#getNumber()}
     * @param number the user-assigned number of this customer (may contain
     *               non-digits)
     */
    void setNumber(String number);

    void setName(String name);

    void setDiscount(FixedPointNumber discount);

    void setCredit(FixedPointNumber credit);

    void setAddress(GCshAddress adr);

    void setShippingAddress(GCshAddress adr);
    
    /**
     * @param notes user-defined notes about the customer (may be null)
     */
    void setNotes(String notes);

    // ---------------------------------------------------------------

    GCshWritableAddress getWritableAddress();

    GCshWritableAddress getWritableShippingAddress();

    GCshWritableAddress getAddress();

    GCshWritableAddress getShippingAddress();
    
}
