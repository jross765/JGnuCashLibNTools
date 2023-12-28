package org.gnucash.api.write;

import org.gnucash.api.read.GnucashGenerJob;

/**
 * Generic job that can be modified
 */
public interface GnucashWritableGenerJob extends GnucashGenerJob 
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
    public void setActive(final boolean jobActive);

}
