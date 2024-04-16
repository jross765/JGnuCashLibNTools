package org.gnucash.api.read.aux;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsDays {

    Integer getDueDays();

    Integer getDiscountDays();

    FixedPointNumber getDiscount();

}
