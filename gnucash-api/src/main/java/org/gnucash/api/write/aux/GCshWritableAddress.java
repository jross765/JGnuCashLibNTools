package org.gnucash.api.write.aux;

import org.gnucash.api.read.aux.GCshAddress;

public interface GCshWritableAddress extends GCshAddress {

    void setAddressName(final String a);

    void setAddressLine1(final String a);

    void setAddressLine2(final String a);

    void setAddressLine3(final String a);

    void setAddressLine4(final String a);

    void setTel(final String a);

    void setFax(final String a);

    void setEmail(final String a);
}
