package org.gnucash.api.read.aux;

import org.gnucash.base.numbers.FixedPointNumber;

public interface GCshBillTermsProximo {

    Integer getDueDay();

    Integer getDiscountDay();

    FixedPointNumber getDiscount();

}
