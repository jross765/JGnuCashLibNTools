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
	 * @see #getName()
	 */
    void setName(String name);

    /**
     * 
     * @param a
     * 
     * @see #getLine1()
     */
    void setLine1(String val);

    /**
     * 
     * @param a
     * 
     * @see #getLine2()
     */
    void setLine2(String val);

    /**
     * 
     * @param a
     * 
     * @see #getLine3()
     */
    void setLine3(String val);

    /**
     * 
     * @param a
     * 
     * @see #getLine4()
     */
    void setLine4(String val);

    /**
     * 
     * @param a
     * 
     * @see #getTel()
     */
    void setTel(String tel);

    /**
     * 
     * @param a
     * 
     * @see #getFax()
     */
    void setFax(String fax);

    /**
     * 
     * @param a
     * 
     * @see #getEmail()
     */
    void setEmail(String eml);
    
    // ---------------------------------------------------------------
    // Old names
    
	/**
	 * @see #setName(String)
	 */
    @Deprecated
    void setAddressName(String name);

    /**
     * @see #setLine1(String)
     */
    @Deprecated
    void setAddressLine1(String val);

    /**
     * @see #setLine2(String)
     */
    @Deprecated
    void setAddressLine2(String val);

    /**
     * @see #setLine3(String)
     */
    @Deprecated
    void setAddressLine3(String val);

    /**
     * @see #setLine4(String)
     */
    @Deprecated
    void setAddressLine4(String val);

}
