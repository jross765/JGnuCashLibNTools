package org.gnucash.api;

import java.lang.reflect.Field;
import java.util.Locale;

import xyz.schnorxoborx.base.beanbase.MappingException;

public class Const {
  
  public static final String XML_FORMAT_VERSION = "2.0.0";
  public static final String XML_FORMAT_VERSION_PRICEDB = "1";
  
  public static final String XML_DATA_TYPE_INTEGER  = "integer";
  public static final String XML_DATA_TYPE_STRING   = "string";
  public static final String XML_DATA_TYPE_TIMESPEC = "timespec";
  public static final String XML_DATA_TYPE_GDATE    = "gdate";
  public static final String XML_DATA_TYPE_GUID     = "guid";
  public static final String XML_DATA_TYPE_FRAME    = "frame";
  
  // -----------------------------------------------------------------
  
  public static final String SLOT_KEY_BOOK_COUNTER_FORMATS = "counter_formats";
  public static final String SLOT_KEY_BOOK_COUNTERS      = "counters";
  public static final String SLOT_KEY_BOOK_FEATURES      = "features";
  public static final String SLOT_KEY_BOOK_OPTIONS       = "options";
  public static final String SLOT_KEY_BOOK_REMOVE_COLOR_NOT_SETS_SLOTS = "remove-color-not-set-slots";
  
  public static final String SLOT_KEY_ACCT_PLACEHOLDER          = "placeholder";
  public static final String SLOT_KEY_ACCT_NOTES                = "notes";
  public static final String SLOT_KEY_ACCT_RECONCILE_INFO       = "reconcile-info";
  public static final String SLOT_KEY_ACCT_INCLUDE_CHILDREN     = "include-children";
  public static final String SLOT_KEY_ACCT_LAST_DATE            = "last-date";
  public static final String SLOT_KEY_ACCT_LAST_INTERVAL        = "last-interval";
  public static final String SLOT_KEY_ACCT_LAST_INTERVAL_DAYS   = "days";
  public static final String SLOT_KEY_ACCT_LAST_INTERVAL_MONTHS = "months";
  
  public static final String SLOT_KEY_TRX_DATE_POSTED    = "date-posted";
  public static final String SLOT_KEY_TRX_TRX_TYPE       = "trans-txn-type";
  
  public static final String SLOT_KEY_INVC_TYPE          = "gncInvoice";
  public static final String SLOT_KEY_INVC_GUID          = "invoice-guid";
  public static final String SLOT_KEY_INVC_CREDIT_NOTE   = "credit-note";
  public static final String SLOT_KEY_INVC_TRX_TYPE      = "trans-txn-type";
  public static final String SLOT_KEY_INVC_TRX_DATE_DUE  = "trans-date-due";
  public static final String SLOT_KEY_INVC_TRX_READ_ONLY = "trans-read-only";
  
  public static final String SLOT_KEY_CMDTY_USER_SYMBOL  = "user_symbol";
  
  public static final String SLOT_KEY_ASSOC_URI          = "assoc_uri";
  
  public static final String SLOT_KEY_GAINS_ACCT         = "gains-acct";
  public static final String SLOT_KEY_GAINS_SPLT         = "gains-split";
  public static final String SLOT_KEY_GAINS_SRC          = "gains-source";

  public static final String SLOT_KEY_DUMMY              = "dummy";

  // -----------------------------------------------------------------

  public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

  public static final String STANDARD_DATE_FORMAT_BOOK = "yyyy-MM-dd HH:mm:ss";

  public static final String REDUCED_DATE_FORMAT_BOOK = "yyyy-MM-dd";

  // -----------------------------------------------------------------

  public static final String DEFAULT_CURRENCY = "EUR";

  // -----------------------------------------------------------------

  public static final double DIFF_TOLERANCE = 0.005;

  // -----------------------------------------------------------------

  public static final int CMDTY_FRACTION_DEFAULT = 10000;
  public static final String CMDTY_XCODE_DEFAULT = "DE000000001"; // pseudo-ISIN
  
  // -----------------------------------------------------------------
  // Locale-specific string constants

  public static String getLocaleString(String code) {
	  return getLocaleString(code, Locale.getDefault());
  }

  public static String getLocaleString(String code, Locale lcl) {
      try {
	  Class<?> cls = Class.forName("org.gnucash.api.Const_" + lcl.getLanguage().toUpperCase());
	  Field fld = cls.getDeclaredField(code);
	  return (String) fld.get(null);
      } catch ( Exception exc ) {
	  throw new MappingException("Could not map code '" + code + "' to locale-specific string");
      }
  }

}
