package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Price that can be modified.
 * 
 * @see GnuCashPrice
 */
public interface GnuCashWritablePrice extends GnuCashPrice, 
                                              GnuCashWritableObject
{

	/**
	 * 
	 * @param qualifID
	 * 
	 * @see #getFromCmdtyCurrQualifID()
	 */
    void setFromCmdtyCurrQualifID(GCshCmdtyCurrID qualifID);

    /**
     * 
     * @param qualifID
     * 
	 * @see #getFromCmdtyCurrQualifID()
     */
    void setFromCommodityQualifID(GCshCmdtyID qualifID);

    /**
     * 
     * @param qualifID
     * 
	 * @see #getFromCmdtyCurrQualifID()
     */
    void setFromCurrencyQualifID(GCshCurrID qualifID);

    /**
     * 
     * @param cmdty
     * 
     * @see #getFromCommodity()
     */
    void setFromCommodity(GnuCashCommodity cmdty);

    /**
     * 
     * @param code
     * 
     * @see #getFromCurrencyCode()
     */
    void setFromCurrencyCode(String code);

    /**
     * 
     * @param curr
     * 
     * @see #getFromCurrency()
     */
    void setFromCurrency(GnuCashCommodity curr);
    
    // ----------------------------

    /**
     * 
     * @param qualifID
     * 
     * @see #getToCurrencyQualifID()
     */
    void setToCurrencyQualifID(GCshCmdtyCurrID qualifID);

    /**
     * 
     * @param qualifID
     * 
     * @see #getToCurrencyQualifID()
     */
    void setToCurrencyQualifID(GCshCurrID qualifID);

    /**
     * 
     * @param code
     * 
     * @see #getToCurrencyQualifID()
     */
    void setToCurrencyCode(String code);

    /**
     * 
     * @param curr
     * 
     * @see #getToCurrencyQualifID()
     */
    void setToCurrency(GnuCashCommodity curr);

    // ----------------------------

    /**
     * 
     * @param date
     * 
     * @see #getDate()
     */
    void setDate(LocalDate date);

    /**
     * 
     * @param dateTime
     * 
     * @see #getDateTime()
     */
    void setDateTime(LocalDateTime dateTime);

    /**
     * 
     * @param src
     * 
     * @see #getSource()
     */
    void setSource(Source src);

    /**
     * 
     * @param type
     * 
     * @see #getType()
     */
    void setType(Type type);

    /**
     * 
     * @param val
     * 
     * @see #getValue()
     */
    void setValue(FixedPointNumber val);

}
