package org.gnucash.api.currency;

import java.util.Collection;

import org.gnucash.api.numbers.FixedPointNumber;

public interface SimplePriceTable {

    FixedPointNumber getConversionFactor(final String code);

    void setConversionFactor(final String code, final FixedPointNumber factor);

    // ---------------------------------------------------------------

    boolean convertFromBaseCurrency(FixedPointNumber value, final String code);

    boolean convertToBaseCurrency(FixedPointNumber value, final String code);

    // ---------------------------------------------------------------

    Collection<String> getCurrencies();

    void clear();

}
