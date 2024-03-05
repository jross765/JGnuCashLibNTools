package org.gnucash.api.write;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.numbers.FixedPointNumber;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.write.hlp.GnuCashWritableObject;
import org.gnucash.api.read.GnuCashCommodity;

/**
 * Price that can be modified.
 * 
 * @see GnuCashPrice
 */
public interface GnuCashWritablePrice extends GnuCashPrice, 
                                              GnuCashWritableObject
{

    void setFromCmdtyCurrQualifID(GCshCmdtyCurrID qualifID);

    void setFromCommodityQualifID(GCshCmdtyID qualifID);

    void setFromCurrencyQualifID(GCshCurrID qualifID);

    void setFromCommodity(GnuCashCommodity cmdty);

    void setFromCurrencyCode(String code);

    void setFromCurrency(GnuCashCommodity curr);
    
    // ----------------------------

    void setToCurrencyQualifID(GCshCmdtyCurrID qualifID) throws InvalidCmdtyCurrTypeException;

    void setToCurrencyQualifID(GCshCurrID qualifID);

    void setToCurrencyCode(String code);

    void setToCurrency(GnuCashCommodity curr) throws InvalidCmdtyCurrTypeException;

    // ----------------------------

    void setDate(LocalDate date);

    void setDateTime(LocalDateTime dateTime);

    void setSource(Source src);

    void setType(Type type);

    void setValue(FixedPointNumber val);

}
