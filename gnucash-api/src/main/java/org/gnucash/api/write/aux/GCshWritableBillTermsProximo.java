package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsProximo;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GCshWritableBillTermsProximo extends GCshBillTermsProximo {

    void setDueDay(final Integer dueDay);

    void getDiscountDay(final Integer dscntDay);

    void setDiscount(final FixedPointNumber dscnt);

}
