package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsDays;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshWritableBillTermsDays extends GCshBillTermsDays {

    void setDueDays(Integer dueDays);

    void setDiscountDays(Integer dscntDays);

    void setDiscount(FixedPointNumber dscnt);

}
