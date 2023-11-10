package org.gnucash.write;

import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashObject;
import org.gnucash.read.aux.GCshAddress;
import org.gnucash.write.aux.GCshWritableAddress;

/**
 * Customer that can be modified
 */
public interface GnucashWritableCustomer extends GnucashCustomer, 
                                                 GnucashWritableObject 
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
    
    // ---------------------------------------------------------------

    /**
     * @param name  the name of the user-defined attribute
     * @param value the value or null if not set
     * @see {@link GnucashObject#getUserDefinedAttribute(String)}
     */
    void setUserDefinedAttribute(final String name, final String value);
}
