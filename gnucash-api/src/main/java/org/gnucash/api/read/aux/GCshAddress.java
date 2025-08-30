package org.gnucash.api.read.aux;

public interface GCshAddress {

    /**
     *
     * @return name as used in the address
     */
    String getName();

    /**
     *
     * @return first line below the name
     */
    String getLine1();

    /**
     *
     * @return second and last line below the name
     */
    String getLine2();
    /**
     *
     * @return third and last line below the name
     */
    String getLine3();
    /**
     *
     * @return fourth and last line below the name
     */
    String getLine4();

    /**
     *
     * @return telephone
     */
    String getTel();

    /**
     *
     * @return Fax
     */
    String getFax();

    /**
     *
     * @return Email
     */
    String getEmail();
    
    // ---------------------------------------------------------------
    // Old names
    
    /**
     * @see #getAddressName()
     */
    @Deprecated
    String getAddressName();

    /**
     * @see #getLine1()
     */
    @Deprecated
    String getAddressLine1();

    /**
     * @see #getLine2()
     */
    @Deprecated
    String getAddressLine2();
   
    /**
     * @see #getLine3()
     */
    @Deprecated
    String getAddressLine3();
   
    /**
     * @see #getLine4()
     */
    @Deprecated
   String getAddressLine4();
    
}
