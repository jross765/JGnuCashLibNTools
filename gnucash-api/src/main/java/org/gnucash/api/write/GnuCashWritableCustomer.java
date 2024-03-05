package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * Customer that can be modified.
 * 
 * @see GnuCashCustomer
 */
public interface GnuCashWritableCustomer extends GnuCashCustomer, 
                                                 GnuCashWritableObject,
                                                 HasWritableUserDefinedAttributes
{

    void remove();

    // ---------------------------------------------------------------

    /**
     * @see {@link GnuCashCustomer#getNumber()}
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
