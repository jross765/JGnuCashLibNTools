package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshBillTermsDays;
import org.gnucash.base.numbers.FixedPointNumber;

public interface GCshWritableBillTermsDays extends GCshBillTermsDays {

    void setDueDays(final Integer dueDays);

    void setDiscountDays(final Integer dscntDays);

    void setDiscount(final FixedPointNumber dscnt);

}
