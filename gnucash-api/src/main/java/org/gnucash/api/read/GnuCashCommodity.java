package org.gnucash.api.read;

import java.util.List;

import org.gnucash.api.read.hlp.GnuCashObject;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;

/**
 * In GnuCash lingo, "commodity" is an umbrella term for
 * <ul>
 *   <li>Currencies</li>
 *   <li>(Real) Securities (shares, bonds, funds, etc.)</li>
 *   <li>Possibly other assets that can be mapped to this GnuCash entity as pseudo-securities, 
 *   such as crypto-currencies, physical precious metals, etc.</li>
 * </ul>
 * <br>
 * Cf. <a href="https://code.gnucash.org/website/docs/v1.6/C/t1784.html">GnuCash manual</a>
 */
public interface GnuCashCommodity extends GnuCashObject,
										  HasUserDefinedAttributes 
{

    /**
     * @return the combination of getNameSpace() and getID(), 
     *         separated by a colon. This is used to make the so-called ID
     *         a real ID (i.e., unique).
     * @throws InvalidCmdtyCurrTypeException 
     */
    GCshCmdtyCurrID getQualifID() throws InvalidCmdtyCurrTypeException;

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

    List<GnuCashPrice> getQuotes() throws InvalidCmdtyCurrTypeException;
    
    GnuCashPrice getYoungestQuote() throws InvalidCmdtyCurrTypeException;
    
}
