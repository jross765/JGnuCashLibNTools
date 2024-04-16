package org.gnucash.api.read.aux;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public interface GCshBillTermsProximo {

    Integer getDueDay();

    Integer getDiscountDay();

    FixedPointNumber getDiscount();

}
