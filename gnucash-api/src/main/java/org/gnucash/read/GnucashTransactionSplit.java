package org.gnucash.read;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;

import org.gnucash.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.basetypes.simple.GCshID;
import org.gnucash.generated.GncTransaction;
import org.gnucash.numbers.FixedPointNumber;

/**
 * This denotes a single addition or removal of some
 * value from one account in a transaction made up of
 * multiple such splits.
 */
public interface GnucashTransactionSplit extends Comparable<GnucashTransactionSplit> {

  // For the following enumerations cf.:
  // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/Split.h

  public enum ReconStatus {
      
      // ::MAGIC
      CREC ("c"), // cleared
      YREC ("y"), // reconciled  
      FREC ("f"), // frozen into accounting period
      NREC ("n"), // not reconciled or cleared
      VREC ("v"); // void
      
      // ---
      
      // Note: The following should be chars, but the method where they are 
      // used is generated and accepts a String.
      
      private String code = "X";

      // ---
      
      ReconStatus(String code) {
	  this.code = code;
      }
      
      // ---
	
      public String getCode() {
	  return code;
      }
	
      // no typo!
      public static ReconStatus valueOff(String code) {
	  for ( ReconStatus reconStat : values() ) {
	      if ( reconStat.getCode().equals(code) ) {
		  return reconStat;
	      }
	  }
	    
	  return null;
      }
  }
    
  public enum Action {
      
      // ::MAGIC (actually kind of "half-magic")
      INVOICE ("TRX_SPLT_ACTION_INVOICE"),
      BILL    ("TRX_SPLT_ACTION_BILL"),
      PAYMENT ("TRX_SPLT_ACTION_PAYMENT"),
      BUY     ("TRX_SPLT_ACTION_BUY"),
      SELL    ("TRX_SPLT_ACTION_SELL");
      
      // ---

      private String code = "UNSET";
	
      // ---
	
      Action(String code) {
	  this.code = code;
      }

      // ---
	
      public String getCode() {
	  return code;
      }
	
      public String getLocaleString() throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  return getLocaleString(Locale.getDefault());
      }

      public String getLocaleString(Locale lcl) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  Class<?> cls = Class.forName("org.gnucash.Const_" + lcl.getLanguage().toUpperCase());
	  Field fld = cls.getDeclaredField(code);
	  return (String) fld.get(null);
      }
		
      // no typo!
      public static Action valueOff(String code) throws NoSuchFieldException, SecurityException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException {
	  for ( Action val : values() ) {
	      if ( val.getLocaleString().equals(code) ) {
		  return val;
	      }
	  }
	    
	  return null;
      }
  }
  
  // Not yet, for future releases:
//  public static final String SPLIT_DATE_RECONCILED    = "date-reconciled";
//  public static final String SPLIT_BALANCE            = "balance";
//  public static final String SPLIT_CLEARED_BALANCE    = "cleared-balance";
//  public static final String SPLIT_RECONCILED_BALANCE = "reconciled-balance";
//  public static final String SPLIT_MEMO               = "memo";
//  public static final String SPLIT_ACTION             = "action";
//  public static final String SPLIT_RECONCILE          = "reconcile-flag";
//  public static final String SPLIT_AMOUNT             = "amount";
//  public static final String SPLIT_SHARE_PRICE        = "share-price";
//  public static final String SPLIT_VALUE              = "value";
//  public static final String SPLIT_TYPE               = "type";
//  public static final String SPLIT_VOIDED_AMOUNT      = "voided-amount";
//  public static final String SPLIT_VOIDED_VALUE       = "voided-value";
//  public static final String SPLIT_LOT                = "lot";
//  public static final String SPLIT_TRANS              = "trans";
//  public static final String SPLIT_ACCOUNT            = "account";
//  public static final String SPLIT_ACCOUNT_GUID       = "account-guid";
//  public static final String SPLIT_ACCT_FULLNAME      = "acct-fullname";
//  public static final String SPLIT_CORR_ACCT_NAME     = "corr-acct-fullname";
//  public static final String SPLIT_CORR_ACCT_CODE     = "corr-acct-code";
  
  // -----------------------------------------------------------------

  @SuppressWarnings("exports")
  GncTransaction.TrnSplits.TrnSplit getJwsdpPeer();

  /**
   * The gnucash-file is the top-level class to contain everything.
   * @return the file we are associated with
   */
  GnucashFile getGnucashFile();
  
  // -----------------------------------------------------------------


    /**
     *
     * @return the unique-id to identify this object with across name- and hirarchy-changes
     */
    String getId();

    /**
     *
     * @return the id of the account we transfer from/to.
     */
    GCshID getAccountID();

    /**
     * This may be null if an account-id is specified in
     * the gnucash-file that does not belong to an account.
     * @return the account of the account we transfer from/to.
     */
    GnucashAccount getAccount();

    /**
     * @return the transaction this is a split of.
     */
    GnucashTransaction getTransaction();


    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    FixedPointNumber getValue();

    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    String getValueFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    /**
     * The value is in the currency of the transaction!
     * @param lcl the locale to use
     * @return the value-transfer this represents
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    String getValueFormatted(Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    String getValueFormattedForHTML() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    /**
     * The value is in the currency of the transaction!
     * @param locale the locale to use
     * @return the value-transfer this represents
     * @throws InvalidCmdtyCurrIDException 
     * @throws InvalidCmdtyCurrTypeException 
     */
    String getValueFormattedForHTML(Locale locale) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     */
    FixedPointNumber getAccountBalance();

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     * @throws InvalidCmdtyCurrTypeException 
     */
    String getAccountBalanceFormatted() throws InvalidCmdtyCurrTypeException;

    /**
     * @throws InvalidCmdtyCurrTypeException 
     * @see GnucashAccount#getBalanceFormatted()
     */
    String getAccountBalanceFormatted(Locale lcl) throws InvalidCmdtyCurrTypeException;

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    FixedPointNumber getQuantity();

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    String getQuantityFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * The quantity is in the currency of the account!
     * @param lcl the locale to use
     * @return the number of items added to the account
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    String getQuantityFormatted(Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    String getQuantityFormattedForHTML() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * The quantity is in the currency of the account!
     * @param lcl the locale to use
     * @return the number of items added to the account
     * @throws InvalidCmdtyCurrTypeException 
     * @throws InvalidCmdtyCurrIDException 
     */
    String getQuantityFormattedForHTML(Locale lcl) throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    /**
     * @return the user-defined description for this object
     *         (may contain multiple lines and non-ascii-characters)
     */
    String getDescription();

    public String getLotID();

      /**
     * Get the type of association this split has with
     * an invoice's lot.
     * @return null, or one of the ACTION_xyz values defined
     */
    String getAction();

    /**
     * @return all keys that can be used with ${@link #getUserDefinedAttribute(String)}}.
     */
    Collection<String> getUserDefinedAttributeKeys();

    /**
     * @param name the name of the user-defined attribute
     * @return the value or null if not set
     */
    String getUserDefinedAttribute(final String name);

}
