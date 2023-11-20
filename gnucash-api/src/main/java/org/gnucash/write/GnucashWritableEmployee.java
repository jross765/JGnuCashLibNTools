package org.gnucash.write;

import org.gnucash.numbers.FixedPointNumber;
import org.gnucash.read.GnucashCustomer;
import org.gnucash.read.GnucashEmployee;
import org.gnucash.read.GnucashObject;
import org.gnucash.read.aux.GCshAddress;
import org.gnucash.write.aux.GCshWritableAddress;

/**
 * Customer that can be modified
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
