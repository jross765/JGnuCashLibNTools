package org.gnucash.api.read.impl.hlp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
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
import org.gnucash.base.basetypes.simple.GCshPrcID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

public class FilePriceManager {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FilePriceManager.class);
    
    public static final DateFormat PRICE_QUOTE_DATE_FORMAT = new SimpleDateFormat(Const.STANDARD_DATE_FORMAT);

    private static final int RECURS_DEPTH_MAX = 5; // ::MAGIC

    // ---------------------------------------------------------------
    
    protected GnuCashFileImpl gcshFile;

    protected GncPricedb                priceDB = null;
    protected Map<GCshPrcID, GnuCashPrice> prcMap  = null;

    // ---------------------------------------------------------------
    
	public FilePriceManager(GnuCashFileImpl gcshFile) {
		this.gcshFile = gcshFile;
		init(gcshFile.getRootElement());
	}

	// ---------------------------------------------------------------

	private void init(final GncV2 pRootElement) {
		prcMap = new HashMap<GCshPrcID, GnuCashPrice>();

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

	public GncPricedb getPriceDB() {
		return priceDB;
	}

	public GnuCashPrice getPriceByID(GCshPrcID prcID) {
		if ( prcID == null ) {
			throw new IllegalArgumentException("argument <prcID> is null");
		}
		
		if ( ! prcID.isSet() ) {
			throw new IllegalArgumentException("argument <prcID> is not set");
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

	// ---------------------------------------------------------------
	
	public GnuCashPrice getPriceByCmdtyIDDate(final GCshCmdtyID cmdtyID, final LocalDate date) {
		return getPriceByCmdtyCurrIDDate(cmdtyID, date);
	}
	
	public GnuCashPrice getPriceByCurrIDDate(final GCshCurrID currID, final LocalDate date) {
		return getPriceByCmdtyCurrIDDate(currID, date);
	}
	
	public GnuCashPrice getPriceByCurrDate(final Currency curr, final LocalDate date) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return getPriceByCmdtyCurrIDDate(currID, date);
	}

	public GnuCashPrice getPriceByCmdtyCurrIDDate(final GCshCmdtyCurrID cmdtyCurrID, final LocalDate date) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
		
		if ( date == null ) {
			throw new IllegalArgumentException("argument <date> is null");
		}
		
		for ( GnuCashPrice prc : getPricesByCmdtyCurrID(cmdtyCurrID) ) {
			if ( prc.getDate().equals(date) ) {
				return prc;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public Collection<GnuCashPrice> getPrices() {
		if ( prcMap == null ) {
			throw new IllegalStateException("no root-element loaded");
		}

		// Caution: Yes, sorting necessary.
		// Cf. comment in FileAccountManager.getAccounts().
		ArrayList<GnuCashPrice> temp = new ArrayList<GnuCashPrice>(prcMap.values());
		Collections.sort(temp);
		return Collections.unmodifiableCollection(temp);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyID(final GCshCmdtyID cmdtyID) {
		if ( cmdtyID == null ) {
			throw new IllegalArgumentException("argument <cmdtyID> is null");
		}
		
		if ( ! cmdtyID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyID> is not set");
		}
		
		return getPricesByCmdtyCurrID(cmdtyID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurrID(final GCshCurrID currID) {
		if ( currID == null ) {
			throw new IllegalArgumentException("argument <currID> is null");
		}
		
		if ( ! currID.isSet() ) {
			throw new IllegalArgumentException("argument <currID> is not set");
		}
		
		return getPricesByCmdtyCurrID(currID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurr(final Currency curr) {
		if ( curr == null ) {
			throw new IllegalArgumentException("argument <curr> is null");
		}
		
		GCshCurrID currID = new GCshCurrID(curr);
		return getPricesByCmdtyCurrID(currID);
	}
	
	public List<GnuCashPrice> getPricesByCmdtyCurrID(final GCshCmdtyCurrID cmdtyCurrID) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}
		
		List<GnuCashPrice> result = new ArrayList<GnuCashPrice>();

		for ( GnuCashPrice prc : getPrices() ) {
			if ( prc.getFromCmdtyCurrQualifID().toString().equals(cmdtyCurrID.toString()) ) {
				result.add(prc);
			}
		}
		
		Collections.sort(result, Collections.reverseOrder()); // descending, i.e. youngest first
		return Collections.unmodifiableList(result);
	}

	// ---------------------------------------------------------------

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
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
		}

		return getLatestPrice(cmdtyCurrID, 0);
	}

	public FixedPointNumber getLatestPrice(final String pCmdtySpace, final String pCmdtyId) {
		if ( pCmdtySpace == null ) {
			throw new IllegalArgumentException("argument <pCmdtySpace> is null");
		}
		
		if ( pCmdtySpace.trim().equals("") ) {
			throw new IllegalArgumentException("argument <pCmdtySpace> is empty");
		}
		
		if ( pCmdtyId == null ) {
			throw new IllegalArgumentException("argument <pCmdtyId> is null");
		}
		
		if ( pCmdtyId.trim().equals("") ) {
			throw new IllegalArgumentException("argument <pCmdtyId> is empty");
		}
		
		return getLatestPrice(new GCshCmdtyCurrID(pCmdtySpace, pCmdtyId), 0);
	}

	private FixedPointNumber getLatestPrice(final GCshCmdtyCurrID cmdtyCurrID, final int depth) {
		if ( cmdtyCurrID == null ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is null");
		}
		
		if ( ! cmdtyCurrID.isSet() ) {
			throw new IllegalArgumentException("argument <cmdtyCurrID> is not set");
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
							"getLatestPrice: GnuCash file contains price-quotes with from-commodity/currency without name space: id='"
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
							"getLatestPrice: GnuCash file contains price-quotes with to-currency without name space: id='"
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

	// ----------------------------
	
	private List<Price> getPrices_raw() {
		List<Price> result = new ArrayList<Price>();

		for ( Price jwsdpPrc : priceDB.getPrice() ) {
			result.add(jwsdpPrc);
		}

		return result;
	}

	protected Price getPrice_raw(final GCshPrcID prcID) {
		for ( Price jwsdpPrc : getPrices_raw() ) {
			if ( jwsdpPrc.getPriceId().getValue().equals(prcID.toString()) ) {
				return jwsdpPrc;
			}
		}
		
		return null;
	}

	// ---------------------------------------------------------------

	public int getNofEntriesPriceMap() {
		return prcMap.size();
	}

}
