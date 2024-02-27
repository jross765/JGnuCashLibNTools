package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GCshWritableBillTermsProximo extends GCshBillTermsProximo {

    void setDueDay(Integer dueDay);

    void getDiscountDay(Integer dscntDay);

    void setDiscount(FixedPointNumber dscnt);

}
