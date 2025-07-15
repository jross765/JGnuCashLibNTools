package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsDays;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * @see GCshWritableBillTermsProximo
 */
public interface GCshWritableBillTermsDays extends GCshBillTermsDays {

	/**
	 * 
	 * @param dueDays
	 * 
	 * @see #getDueDays()
	 */
    void setDueDays(Integer dueDays);

    /**
     * 
     * @param dscntDays
     * 
     * @see #getDiscountDays()
     * @see #setDiscount(FixedPointNumber)
     */
    void setDiscountDays(Integer dscntDays);

    /**
     * 
     * @param dscnt
     * 
     * @see #getDiscount()
     * @see #setDiscountDays(Integer)
     */
    void setDiscount(FixedPointNumber dscnt);

}
