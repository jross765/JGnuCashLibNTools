package org.gnucash.api.write;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.GnucashObject;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;

/**
 * Employee that can be modified
 */
public interface GnucashWritableEmployee extends GnucashEmployee, 
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

    void setUserName(String userName);

    void setAddress(GCshAddress adr);

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
