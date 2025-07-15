package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsProximo;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * @see GCshWritableBillTermsDays
 */
public interface GCshWritableBillTermsProximo extends GCshBillTermsProximo {

	/**
	 * 
	 * @param dueDay
	 * 
	 * @see #getDueDay()
	 */
    void setDueDay(Integer dueDay);

    /**
     * 
     * @param dscntDay
     * 
     * @see #getDiscountDay()
     * @see #setDiscount(FixedPointNumber)
     */
    void setDiscountDay(Integer dscntDay);

    /**
     * 
     * @param dscnt
     * 
     * @see #getDiscount()
     * @see #setDiscountDay(Integer)
     */
    void setDiscount(FixedPointNumber dscnt);

}
