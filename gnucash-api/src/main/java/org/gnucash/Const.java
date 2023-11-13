package org.gnucash;

import org.gnucash.basetypes.complex.GCshCmdtyCurrNameSpace;

public class Const
{
  
  public static final String XML_FORMAT_VERSION = "2.0.0";
  
  public static final String XML_DATA_TYPE_GUID   = "guid";
  public static final String XML_DATA_TYPE_STRING = "string";
  
  // -----------------------------------------------------------------
  
  public static final String SLOT_KEY_ASSOC_URI = "assoc_uri";

  // -----------------------------------------------------------------

  public static final String STANDARD_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

  public static final String STANDARD_DATE_FORMAT_BOOK = "yyyy-MM-dd HH:mm:ss";

  // -----------------------------------------------------------------

  public static final double DIFF_TOLERANCE = 0.005;

  // -----------------------------------------------------------------
  // ::TODO
  // The following constants are partially locale-specific. This should be cleaned-up.

  /*
   * This is an ugly ad-hoc solution to be cleaned-up in a future release. 
   * In this particular case, it is the text added to a specific slot in a specific
   * type of transaction (as automatically generated by GnuCash). Obviously, this
   * should be mapped to locale-specific config file entries. 
   */
  public static final String INVC_READ_ONLY_SLOT_TEXT = "Aus einer Rechnung erzeugt. Für Änderungen müssen Sie die Buchung der Rechnung löschen.";

  // -----------------------------------------------------------------

  public static final int CMDTY_FRACTION_DEFAULT = 10000;
  public static final String CMDTY_XCODE_DEFAULT = "DE000000001"; // pseudo-ISIN

}
