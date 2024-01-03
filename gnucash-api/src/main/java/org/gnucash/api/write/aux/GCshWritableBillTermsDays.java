package org.gnucash.api.write.aux;

import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshBillTermsDays;

public interface GCshWritableBillTermsDays extends GCshBillTermsDays {

    void setDueDays(final Integer dueDays);

    void setDiscountDays(final Integer dscntDays);

    void setDiscount(final FixedPointNumber dscnt);

}
