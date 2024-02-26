package org.gnucash.api.write;

import org.gnucash.api.read.GnucashCustomer;
import org.gnucash.api.read.GnucashEmployee;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Employee that can be modified.
 * 
 * @see GnucashEmployee
 */
public interface GnucashWritableEmployee extends GnucashEmployee, 
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

    void setUserName(String userName);

    void setAddress(GCshAddress adr);

    // ---------------------------------------------------------------

    GCshWritableAddress getWritableAddress();

    GCshWritableAddress getAddress();

}
