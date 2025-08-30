package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshAddress;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

public interface GCshWritableAddress extends GCshAddress,
                                             GnuCashWritableObject 
{

	/**
	 * 
	 * @param a
	 * 
	 * @see #getAddressName()
	 */
    void setAddressName(String a);

    /**
     * 
     * @param a
     * 
     * @see #getAddressLine1()
     */
    void setAddressLine1(String a);

    /**
     * 
     * @param a
     * 
     * @see #getAddressLine2()
     */
    void setAddressLine2(String a);

    /**
     * 
     * @param a
     * 
     * @see #getAddressLine3()
     */
    void setAddressLine3(String a);

    /**
     * 
     * @param a
     * 
     * @see #getAddressLine4()
     */
    void setAddressLine4(String a);

    /**
     * 
     * @param a
     * 
     * @see #getTel()
     */
    void setTel(String a);

    /**
     * 
     * @param a
     * 
     * @see #getFax()
     */
    void setFax(String a);

    /**
     * 
     * @param a
     * 
     * @see #getEmail()
     */
    void setEmail(String a);
}
