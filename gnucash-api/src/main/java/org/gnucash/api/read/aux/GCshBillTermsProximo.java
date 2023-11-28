package org.gnucash.api.read.aux;

import org.gnucash.api.numbers.FixedPointNumber;

public interface GCshBillTermsProximo {

    public Integer getDueDay();

    public Integer getDiscountDay();

    public FixedPointNumber getDiscount();

}
