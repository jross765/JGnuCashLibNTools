package org.gnucash.api.write.aux;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshBillTermsProximo;

public interface GCshWritableBillTermsProximo extends GCshBillTermsProximo {

    void setDueDay(final Integer dueDay);

    void getDiscountDay(final Integer dscntDay);

    void setDiscount(final FixedPointNumber dscnt);

}
