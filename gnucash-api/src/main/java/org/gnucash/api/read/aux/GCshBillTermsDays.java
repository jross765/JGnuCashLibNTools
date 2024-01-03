package org.gnucash.api.read.aux;

import org.gnucash.api.numbers.FixedPointNumber;

public interface GCshBillTermsDays {

    Integer getDueDays();

    Integer getDiscountDays();

    FixedPointNumber getDiscount();

}
