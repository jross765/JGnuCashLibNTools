package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.aux.GCshWritableAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.write.hlp.HasWritableAddress;
import org.gnucash.api.write.hlp.HasWritableUserDefinedAttributes;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Customer that can be modified.
 * 
 * @see GnuCashCustomer
 */
public interface GnuCashWritableCustomer extends GnuCashCustomer, 
                                                 GnuCashWritableObject,
                                                 HasWritableAddress,
                                                 HasWritableUserDefinedAttributes
{
	/**
	 * Deletes the customer.
	 */
    void remove();

    // ---------------------------------------------------------------

    /**
     * Sets the customer's number.
     * 
     * @see {@link GnuCashCustomer#getNumber()}
     * @param number the user-assigned number of this customer (may contain
     *               non-digits)
     */
    void setNumber(String number);

    /**
     * Sets the customer's name.
     *  
     * @param name
     */
    void setName(String name);

    /**
     * Sets the customer's discount.
     * 
     * @param discount
     */
    void setDiscount(FixedPointNumber discount);

    /**
     * Sets the customer's credit.
     * 
     * @param credit
     */
    void setCredit(FixedPointNumber credit);

    /**
     * Sets the customer's notes.
     * 
     * @param notes user-defined notes about the customer (may be null)
     */
    void setNotes(String notes);

    // ---------------------------------------------------------------

    /**
     * @return the customer's shipping address (rw-object) 
     */
    GCshWritableAddress getWritableShippingAddress();
    
    GCshWritableAddress createWritableShippingAddress();
    
	void removeShippingAddress(GCshWritableAddress impl);

    void setShippingAddress(GCshAddress adr);
}
