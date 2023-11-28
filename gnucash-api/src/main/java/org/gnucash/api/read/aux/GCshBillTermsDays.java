package org.gnucash.api.read.aux;

import org.gnucash.api.numbers.FixedPointNumber;

public interface GCshBillTermsDays {

    public Integer getDueDays();

    public Integer getDiscountDays();

    public FixedPointNumber getDiscount();

}
