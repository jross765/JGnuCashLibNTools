package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Employee that can be modified.
 * 
 * @see GnuCashEmployee
 */
public interface GnuCashWritableEmployee extends GnuCashEmployee, 
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

    void setUserName(String userName);

    void setAddress(GCshAddress adr);

    // ---------------------------------------------------------------

    GCshWritableAddress getWritableAddress();

    GCshWritableAddress getAddress();

}
