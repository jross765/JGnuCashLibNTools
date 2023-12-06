package org.gnucash.api;

import java.lang.reflect.Field;
import java.util.Locale;

public class Const {
  
  public static final String XML_FORMAT_VERSION = "2.0.0";
  
  public static final String XML_DATA_TYPE_GUID   = "guid";
  public static final String XML_DATA_TYPE_STRING = "string";
  
  // -----------------------------------------------------------------
  
  public static final String SLOT_KEY_ASSOC_URI = "assoc_uri";

  // -----------------------------------------------------------------

  public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

  public static final String STANDARD_DATE_FORMAT_BOOK = "yyyy-MM-dd HH:mm:ss";

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
	  throw new RuntimeException("Could not map code '" + code + "' to locale-specific string");
      }
  }

}
