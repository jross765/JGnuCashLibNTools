package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAddress;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

/**
 * Employee that can be modified.
 * 
 * @see GnuCashEmployee
 */
public interface GnuCashWritableEmployee extends GnuCashEmployee, 
                                                 GnuCashWritableObject,
                                                 HasWritableAddress,
                                                 HasWritableUserDefinedAttributes
{

	/**
	 * Deletes the employee.
	 */
    void remove();

    // ---------------------------------------------------------------

    /**
     * Sets the employee's number.
     *  
     * @see {@link GnuCashCustomer#getNumber()}
     * @param number the user-assigned number of this employee (may contain
     *               non-digits)
     */
    void setNumber(String number);

    /**
     * Sets the employee's user name.
     * 
     * @param userName
     */
    void setUserName(String userName);

//    void setAddress(GCshAddress adr);

    // ---------------------------------------------------------------

//    GCshWritableAddress getWritableAddress();

    /**
     * @return the employee's address (rw-object) 
     */
    GCshWritableAddress getAddress();

}
