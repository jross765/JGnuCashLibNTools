package org.gnucash.api.read.impl.hlp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.Price;
import org.gnucash.api.generated.Price.PriceCommodity;
import org.gnucash.api.generated.Price.PriceCurrency;
import org.gnucash.api.read.GnuCashPrice;
import org.gnucash.api.read.impl.GnuCashFileImpl;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.simple.GCshID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class FilePriceManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);
    
    public static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    private static final int RECURS_DEPTH_MAX = 5; // ::MAGIC

    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    private GncPricedb                priceDB = null;
    private Map<GCshID, GnuCashPrice> prcMap  = null;

    // ---------------------------------------------------------------
    
	public FilePriceManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		prcMap = new HashMap<GCshID, GnuCashPrice>();

		initPriceDB(pRootElement);
		List<Price> prices = priceDB.getPrice();
		for ( Price jwsdpPrc : prices ) {
			GnuCashPriceImpl price = createPrice(jwsdpPrc);
			prcMap.put(price.getID(), price);
		}

		LOGGER.debug("init: No. of entries in Price map: " + prcMap.size());
	}

	private void initPriceDB(final GncV2 pRootElement) {
		List<Object> bookElements = pRootElement.getGncBook().getBookElements();
		for ( Object bookElement : bookElements ) {
			if ( bookElement instanceof GncPricedb ) {
				priceDB = (GncPricedb) bookElement;
				return;
			}
		}
	}

	protected GnuCashPriceImpl createPrice(final Price jwsdpPrc) {
		GnuCashPriceImpl prc = new GnuCashPriceImpl(jwsdpPrc, gcshFile);
		LOGGER.debug("Generated new price: " + prc.getID());
		return prc;
	}

	// ---------------------------------------------------------------

	public void addPrice(GnuCashPrice prc) {
		if ( prc == null ) {
			throw new IllegalArgumentException("null prc given");
		}
		
		prcMap.put(prc.getID(), prc);
		LOGGER.debug("Added price to cache: " + prc.getID());
	}

	public void removePrice(GnuCashPrice prc) {
		if ( prc == null ) {
			throw new IllegalArgumentException("null prc given");
		}
		
		prcMap.remove(prc.getID());
		LOGGER.debug("Removed price from cache: " + prc.getID());
	}

	// ---------------------------------------------------------------

	public GncPricedb getPriceDB() {
		return priceDB;
	}

	public GnuCashPrice getPriceByID(GCshID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("null prive ID given");
		}
		
		if ( ! prcID.isSet() ) {
			throw new IllegalArgumentException("unset price ID given");
		}
		
		if ( prcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		GnuCashPrice retval = prcMap.get(prcID);
		if ( retval == null ) {
			LOGGER.error("getPriceByID: No Price with ID '" + prcID + "'. " + "We know " + prcMap.size() + " prices.");
		}
		
		return retval;
	}

	public Collection<GnuCashPrice> getPrices() {
		if ( prcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		return Collections.unmodifiableCollection(prcMap.values());
	}

//    public FixedPointNumber getLatestPrice(final String cmdtyCurrIDStr) {
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

	public FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("null commodity/currency ID given");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("unset commodity/currency ID given");
		}

		return getLatestPrice(cmdtyCurrID, 0);
	}

	public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) {
		if ( pCmdtySpace == null ) {
			throw new IllegalArgumentException("null commodity space given");
		}
		
		if ( pCmdtySpace.trim().equals("") ) {
			throw new IllegalArgumentException("empty commodity space given");
		}
		
		if ( pCmdtyId == null ) {
			throw new IllegalArgumentException("null commodity ID given");
		}
		
		if ( pCmdtyId.trim().equals("") ) {
			throw new IllegalArgumentException("empty commodity ID given");
		}
		
		return getLatestPrice(new GCshCmdtyCurrID(pCmdtySpace, pCmdtyId), 0);
	}

	private FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID, final int depth) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("null commodity/currency ID given");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("unset commodity/currency ID given");
		}

		// System.err.println("depth: " + depth);

		Date latestDate = null;
		FixedPointNumber latestQuote = null;
		FixedPointNumber factor = new FixedPointNumber(1); // factor is used if the quote is not to our base-currency
		final int maxRecursionDepth = RECURS_DEPTH_MAX;

		for ( Price priceQuote : priceDB.getPrice() ) {
			if ( priceQuote == null ) {
				LOGGER.warn(
						"getLatestPrice: GnuCash file contains null price-quotes - there may be a problem with JWSDP");
				continue;
			}

			PriceCommodity fromCmdtyCurr = priceQuote.getPriceCommodity();
			PriceCurrency toCurr = priceQuote.getPriceCurrency();

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
				if ( fromCmdtyCurr.getCmdtySpace() == null ) {
					LOGGER.warn(
							"getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without namespace: id='"
									+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( fromCmdtyCurr.getCmdtyId() == null ) {
					LOGGER.warn(
							"getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without code: id='"
									+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( toCurr.getCmdtySpace() == null ) {
					LOGGER.warn(
							"getLatestPrice: GnuCash file contains price-quotes with to-currency without namespace: id='"
									+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( toCurr.getCmdtyId() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes with to-currency without code: id='"
							+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( priceQuote.getPriceTime() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without timestamp id='"
							+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				if ( priceQuote.getPriceValue() == null ) {
					LOGGER.warn("getLatestPrice: GnuCash file contains price-quotes without value id='"
							+ priceQuote.getPriceId().getValue() + "'");
					continue;
				}

				/*
				 * if (priceQuote.getPriceCommodity().getCmdtySpace().equals("FUND") &&
				 * priceQuote.getPriceType() == null) {
				 * LOGGER.warn("getLatestPrice: GnuCash file contains FUND-price-quotes" +
				 * " with no type id='" + priceQuote.getPriceID().getValue() + "'"); continue; }
				 */

				if ( !(fromCmdtyCurr.getCmdtySpace().equals(cmdtyCurrID.getNameSpace())
						&& fromCmdtyCurr.getCmdtyId().equals(cmdtyCurrID.getCode())) ) {
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
				if ( !toCurr.getCmdtySpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) ) {
					// is commodity
					if ( depth > maxRecursionDepth ) {
						LOGGER.warn("getLatestPrice: Ignoring price-quote that is not in an ISO4217-currency"
								+ " but in '" + toCurr.getCmdtySpace() + ":" + toCurr.getCmdtyId() + "'");
						continue;
					}
					factor = getLatestPrice(new GCshCmdtyID(toCurr.getCmdtySpace(), toCurr.getCmdtyId()), depth + 1);
				} else {
					// is currency
					if ( !toCurr.getCmdtyId().equals(gcshFile.getDefaultCurrencyID()) ) {
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

				if ( latestDate == null || latestDate.before(date) ) {
					latestDate = date;
					latestQuote = new FixedPointNumber(priceQuote.getPriceValue());
					LOGGER.debug("getLatestPrice: getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString()
							+ "') converted " + latestQuote + " <= " + priceQuote.getPriceValue());
				}

			} catch (NumberFormatException e) {
				LOGGER.error("getLatestPrice: [NumberFormatException] Problem in " + getClass().getName()
						+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '"
						+ priceQuote + "'", e);
			} catch (ParseException e) {
				LOGGER.error("getLatestPrice: [ParseException] Problem in " + getClass().getName() + " "
						+ cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '" + priceQuote + "'", e);
			} catch (NullPointerException e) {
				LOGGER.error("getLatestPrice: [NullPointerException] Problem in " + getClass().getName()
						+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '"
						+ priceQuote + "'", e);
			} catch (ArithmeticException e) {
				LOGGER.error("getLatestPrice: [ArithmeticException] Problem in " + getClass().getName()
						+ ".getLatestPrice(pCmdtyCurrID='" + cmdtyCurrID.toString() + "')! Ignoring a bad price-quote '"
						+ priceQuote + "'", e);
			}
		} // for priceQuote

		LOGGER.debug("getLatestPrice: " + getClass().getName() + ".getLatestPrice(pCmdtyCurrID='"
				+ cmdtyCurrID.toString() + "')= " + latestQuote + " from " + latestDate);

		if ( latestQuote == null ) {
			return null;
		}

		if ( factor == null ) {
			factor = new FixedPointNumber(1);
		}

		return factor.multiply(latestQuote);
	}

	// ---------------------------------------------------------------

	public int getNofEntriesPriceMap() {
		return prcMap.size();
	}

}
