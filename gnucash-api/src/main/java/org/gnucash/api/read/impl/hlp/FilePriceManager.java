package org.gnucash.api.read.impl.hlp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.GCshCmdtyID;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.GncV2.GncBook.GncPricedb.Price.PriceCommodity;
import org.gnucash.api.generated.GncV2.GncBook.GncPricedb.Price.PriceCurrency;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashFile;
import org.gnucash.api.read.aux.GCshPrice;
import org.gnucash.api.read.impl.GnucashFileImpl;
import org.gnucash.api.read.impl.aux.GCshPriceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilePriceManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);
    
    public static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    // ---------------------------------------------------------------
    
    private GnucashFileImpl gcshFile;

    private GncV2.GncBook.GncPricedb priceDB = null;
    private Map<GCshID, GCshPrice> prcMap    = null;

    // ---------------------------------------------------------------
    
    public FilePriceManager(GnucashFileImpl gcshFile) {
	this.gcshFile = gcshFile;
	init(gcshFile.getRootElement());
    }

    // ---------------------------------------------------------------

    private void init(final GncV2 pRootElement) {
        prcMap = new HashMap<GCshID, GCshPrice>();

        initPriceDB(pRootElement);
        List<GncV2.GncBook.GncPricedb.Price> prices = priceDB.getPrice();
        for ( GncV2.GncBook.GncPricedb.Price jwsdpPrc : prices ) {
            GCshPriceImpl price = createPrice(jwsdpPrc);
            prcMap.put(price.getId(), price);
        }

	LOGGER.debug("init: No. of entries in Price map: " + prcMap.size());
    }

    private void initPriceDB(final GncV2 pRootElement) {
	List<Object> bookElements = pRootElement.getGncBook().getBookElements();
	for ( Object bookElement : bookElements ) {
	    if ( bookElement instanceof GncV2.GncBook.GncPricedb ) {
		priceDB = (GncV2.GncBook.GncPricedb) bookElement;
		return;
	    } 
	}
    }

    /**
     * @param jwsdpInvc the JWSDP-peer (parsed xml-element) to fill our object with
     * @return the new GCshPrice to wrap the given jaxb-object.
     */
    protected GCshPriceImpl createPrice(final GncV2.GncBook.GncPricedb.Price jwsdpPrc) {
	GCshPriceImpl prc = new GCshPriceImpl(jwsdpPrc, gcshFile);
	return prc;
    }

    // ---------------------------------------------------------------

    public void addGenerInvoice(GCshPrice prc) {
	prcMap.put(prc.getId(), prc);
    }

    public void removeGenerInvoice(GCshPrice prc) {
	prcMap.remove(prc.getId());
    }

    // ---------------------------------------------------------------

    public GncV2.GncBook.GncPricedb getPriceDB() {
	return priceDB;
    }

    /**
     * {@inheritDoc}
     */
    public GCshPrice getPriceByID(GCshID prcID) {
        if (prcMap == null) {
	    throw new IllegalStateException("no root-element loaded");
        }
        
        return prcMap.get(prcID);
    }

    /**
     * {@inheritDoc}
     */
    public Collection<GCshPrice> getPrices() {
        if (prcMap == null) {
	    throw new IllegalStateException("no root-element loaded");
        } 

        return prcMap.values();
    }

//    public FixedPointNumber getLatestPrice(final String cmdtyCurrIDStr) throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException {
//      try {
//        // See if it's a currency
//        GCshCurrID currID = new GCshCurrID(cmdtyCurrIDStr);
//	    return getLatestPrice(currID);
//      } catch ( Exception exc ) {
//        // It's a security
//	    GCshCmdtyID cmdtyID = new GCshCmdtyID(GCshCmdtyCurrID.Type.SECURITY_GENERAL, cmdtyCurrIDStr);
//	    return getLatestPrice(cmdtyID);
//      }
//    }
    
    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    public FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return getLatestPrice(cmdtyCurrID, 0);
    }

    /**
     * {@inheritDoc}
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	return getLatestPrice(new GCshCmdtyCurrID(pCmdtySpace, pCmdtyId), 0);
    }

    /**
     * @param pCmdtySpace the namespace for pCmdtyId
     * @param pCmdtyId    the currency-name
     * @param depth       used for recursion. Allways call with '0' for aborting
     *                    recursive quotes (quotes to other then the base- currency)
     *                    we abort if the depth reached 6.
     * @return the latest price-quote in the gnucash-file in the default-currency
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     * @see {@link GnucashFile#getLatestPrice(String, String)}
     * @see #getDefaultCurrencyID()
     */
    private FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID, final int depth) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException {
	if (cmdtyCurrID == null) {
	    throw new IllegalArgumentException("null parameter 'cmdtyCurrID' given");
	}
	// System.err.println("depth: " + depth);

	Date latestDate = null;
	FixedPointNumber latestQuote = null;
	FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
	final int maxRecursionDepth = 5; // ::MAGIC

	for ( GncV2.GncBook.GncPricedb.Price priceQuote : priceDB.getPrice() ) {
	    if (priceQuote == null) {
		LOGGER.warn("getLatestPrice: GnuCash file contains null price-quotes - there may be a problem with JWSDP");
		continue;
	    }
		    
	    PriceCommodity fromCmdtyCurr = priceQuote.getPriceCommodity();
	    PriceCurrency  toCurr        = priceQuote.getPriceCurrency();

	    if ( fromCmdtyCurr == null ) {
		LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without from-commodity/currency: '"
			+ priceQuote.toString() + "'");
		continue;
	    }
				
	    if ( toCurr == null ) {
		LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without to-currency: '"
			+ priceQuote.toString() + "'");
		continue;
	    }

	    try {
		if (fromCmdtyCurr.getCmdtySpace() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without namespace: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
			    
		if (fromCmdtyCurr.getCmdtyId() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without code: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
				    
		if (toCurr.getCmdtySpace() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without namespace: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
					    
		if (toCurr.getCmdtyId() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without code: id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		if (priceQuote.getPriceTime() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without timestamp id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		if (priceQuote.getPriceValue() == null) {
		    LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without value id='"
			    + priceQuote.getPriceId().getValue() + "'");
		    continue;
		}
		    
		/*
		 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
		 * priceQuote.getPriceType() == null) {
		 * LOGGER.warn("getLatestPrice: GnuCash file contains FUND-price-quotes" + " with no type id='"
		 * + priceQuote.getPriceId().getValue() + "'"); continue; }
		 */
		    
		if ( ! ( fromCmdtyCurr.getCmdtySpace().equals(cmdtyCurrID.getNameSpace()) && 
		         fromCmdtyCurr.getCmdtyId().equals(cmdtyCurrID.getCode()) ) ) {
		    continue;
		}
		    
		/*
		 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
		 * (priceQuote.getPriceType() == null ||
		 * !priceQuote.getPriceType().equals("last") )) {
		 * LOGGER.warn("getLatestPrice: ignoring FUND-price-quote of unknown type '" +
		 * priceQuote.getPriceType() + "' expecting 'last' "); continue; }
		 */

		// BEGIN core
		if ( ! toCurr.getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
		    // is commodity
		    if ( depth > maxRecursionDepth ) {
			LOGGER.warn("getLatestPrice: Ignoring price-quote that is not in an ISO4217-currency" 
				+ " but in '" + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId() + "'");
			continue;
		    }
		    factor = getLatestPrice(new GCshCmdtyID(toCurr.getCmdtySpace(), toCurr.getCmdtyId()), depth + 1);
		} else {
		    // is currency
		    if ( ! toCurr.getCmdtyId().equals(gcshFile.getDefaultCurrencyID()) ) {
			if ( depth > maxRecursionDepth ) {
			    LOGGER.warn("Ignoring price-quote that is not in " + gcshFile.getDefaultCurrencyID()
			    + " but in '" + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId() + "'");
			    continue;
			}
			factor = getLatestPrice(new GCshCurrID(toCurr.getCmdtyId()), depth + 1);
		    }
		}
		// END core

		Date date = PRICE_QUOTE_DATE_FORMAT.parse(priceQuote.getPriceTime().getTsDate());

		if (latestDate == null || latestDate.before(date)) {
		    latestDate = date;
		    latestQuote = new FixedPointNumber(priceQuote.getPriceValue());
		    LOGGER.debug("getLatestPrice: getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
		    	+ "') converted " + latestQuote + " <= " + priceQuote.getPriceValue());
		}

	    } catch (NumberFormatException e) {
		LOGGER.error("getLatestPrice: [NumberFormatException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    } catch (ParseException e) {
		LOGGER.error("getLatestPrice: [ParseException] Problem in " + getClass().getName() + " "
			+ cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '"
			+ priceQuote + "'", e);
	    } catch (NullPointerException e) {
		LOGGER.error("getLatestPrice: [NullPointerException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    } catch (ArithmeticException e) {
		LOGGER.error("getLatestPrice: [ArithmeticException] Problem in " + getClass().getName()
			+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
			+ "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
	    }
	} // for priceQuote

	LOGGER.debug("getLatestPrice: " + getClass().getName() + ".getLatestPrice(pCmdtyCurrID='"
		+ cmdtyCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

	if (latestQuote == null) {
	    return null;
	}

	if (factor == null) {
	    factor = new FixedPointNumber(1);
	}

	return factor.multiply(latestQuote);
    }

    // ---------------------------------------------------------------

    public int getNofEntriesPriceMap() {
	return prcMap.size();
    }

}
