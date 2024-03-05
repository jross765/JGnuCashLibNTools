package org.gnucash.api.write;

import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.write.hlp.GnuCashWritableObject;

/**
 * Generic job that can be modified.
 * 
 * @see GnuCashGenerJob
 */
public interface GnuCashWritableGenerJob extends GnuCashGenerJob,
                                                 GnuCashWritableObject
{
    
    void setNumber(String number);

    /**
     * Set the description-text.
     *
     * @param desc the new description
     */
    void setName(String desc);

    /**
     * @param jobActive true is the job is to be (re)activated, false to deactivate
     */
    public void setActive(boolean jobActive);

}
