package org.gnucash.api.read;

import java.util.Collection;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.read.aux.GCshPrice;

public interface GnucashCommodity {

    /**
     * @return the combination of getNameSpace() and getId(), 
     *         separated by a colon. This is used to make the so-called ID
     *         a real ID (i.e., unique).
     * @throws InvalidCmdtyCurrTypeException 
     */
    GCshCmdtyCurrID getQualifId() throws InvalidCmdtyCurrTypeException;

    /**
     * @return the "extended" code of a commodity
     *         (typically, this is the ISIN in case you have 
     *         a global portfolio; if you have a local portfolio,
     *         this could also be the corresponding regional security/commodity
     *         ID, such as "CUSIP" (USA, Canada), "SEDOL" (UK), or
     *         "WKN" (Germany, Austria, Switzerland)). 
     */
    String getXCode();

    /**
     * @return the name of the currency/security/commodity 
     */
    String getName();

    Integer getFraction();

    // ------------------------------------------------------------

    Collection<GCshPrice> getQuotes() throws InvalidCmdtyCurrTypeException;
    
    GCshPrice getYoungestQuote() throws InvalidCmdtyCurrTypeException;
    
}
