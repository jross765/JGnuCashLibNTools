package org.gnucash.api.read;

import java.lang.reflect.Field;
import java.util.Locale;

import org.gnucash.api.generated.GncTransaction;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;

/**
 * A single addition or removal of a quantity of an account's accounted-for items 
 * (i.e. commodity), having a specific value, from that account in a transaction.
 * <br>
 * A transaction split never exists alone, but is always grouped in a transaction
 * together with at least one more split.
 * 
 * @see GnuCashTransaction
 */
public interface GnuCashTransactionSplit extends Comparable<GnuCashTransactionSplit>,
												 HasUserDefinedAttributes 
{

  // For the following enumerations cf.:
  //  - https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/Split.h
  //  - https://github.com/GnuCash/gnucash/blob/stable/gnucash/register/ledger-core/split-register.c

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
      INCREASE    ("TRX_SPLT_ACTION_INCREASE"),
      DECREASE    ("TRX_SPLT_ACTION_DECREASE"),
      
      INTEREST    ("TRX_SPLT_ACTION_INTEREST"),
      PAYMENT     ("TRX_SPLT_ACTION_PAYMENT"),
      REBATE      ("TRX_SPLT_ACTION_REBATE"),
      PAYCHECK    ("TRX_SPLT_ACTION_PAYCHECK"),
      CREDIT      ("TRX_SPLT_ACTION_CREDIT"),
      
      ATM_DEPOSIT ("TRX_SPLT_ACTION_ATM_DEPOSIT"),
      ATM_DRAW    ("TRX_SPLT_ACTION_ATM_DRAW"),
      ONLINE      ("TRX_SPLT_ACTION_ONLINE"),
      
      INVOICE     ("TRX_SPLT_ACTION_INVOICE"),
      BILL        ("TRX_SPLT_ACTION_BILL"),
      VOUCHER     ("TRX_SPLT_ACTION_VOUCHER"),
      
      BUY         ("TRX_SPLT_ACTION_BUY"),
      SELL        ("TRX_SPLT_ACTION_SELL"),
      EQUITY      ("TRX_SPLT_ACTION_EQUITY"),
      
      PRICE       ("TRX_SPLT_ACTION_PRICE"),
      FEE         ("TRX_SPLT_ACTION_FEE"),
      DIVIDEND    ("TRX_SPLT_ACTION_DIVIDEND"),
      LTCG        ("TRX_SPLT_ACTION_LTCG"),
      STCG        ("TRX_SPLT_ACTION_STCG"),
      INCOME      ("TRX_SPLT_ACTION_INCOME"),
      DIST        ("TRX_SPLT_ACTION_DIST"),
      SPLIT       ("TRX_SPLT_ACTION_SPLIT");
      
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
	
      public String getLocaleString() {
	  return getLocaleString(Locale.getDefault());
      }

      public String getLocaleString(Locale lcl) {
	  try {
	      Class<?> cls = Class.forName("org.gnucash.api.Const_" + lcl.getLanguage().toUpperCase());
	      Field fld = cls.getDeclaredField(code);
	      return (String) fld.get(null);
	  } catch ( Exception exc ) {
	      throw new MappingException("Could not map string '" + code + "' to locale-specific string");
	  }
      }
		
      // no typo!
      public static Action valueOff(String code) {
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

  // -----------------------------------------------------------------


    /**
     *
     * @return the unique-id to identify this object with across name- and hirarchy-changes
     */
    GCshID getID();

    /**
     *
     * @return the id of the account we transfer from/to.
     */
    GCshID getAccountID();

    /**
     * This may be null if an account-id is specified in
     * the GnuCash file that does not belong to an account.
     * @return the account of the account we transfer from/to.
     */
    GnuCashAccount getAccount();

    /**
     * @return the ID of the transaction this is a split of.
     */
    GCshID getTransactionID();

    /**
     * @return the transaction this is a split of.
     */
    GnuCashTransaction getTransaction();

    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    FixedPointNumber getValue();

    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    String getValueFormatted();
    /**
     * The value is in the currency of the transaction!
     * @param lcl the locale to use
     * @return the value-transfer this represents
     */
    String getValueFormatted(Locale lcl);
    /**
     * The value is in the currency of the transaction!
     * @return the value-transfer this represents
     */
    String getValueFormattedForHTML();
    /**
     * The value is in the currency of the transaction!
     * @param locale the locale to use
     * @return the value-transfer this represents
     */
    String getValueFormattedForHTML(Locale locale);

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     */
    FixedPointNumber getAccountBalance();

    /**
     * @return the balance of the account (in the account's currency)
     *         up to this split.
     */
    String getAccountBalanceFormatted();

    /**
     * @param lcl 
     * @return 
     * @see GnuCashAccount#getBalanceFormatted()
     */
    String getAccountBalanceFormatted(Locale lcl);

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    FixedPointNumber getQuantity();

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    String getQuantityFormatted();

    /**
     * The quantity is in the currency of the account!
     * @param lcl the locale to use
     * @return the number of items added to the account
     */
    String getQuantityFormatted(Locale lcl);

    /**
     * The quantity is in the currency of the account!
     * @return the number of items added to the account
     */
    String getQuantityFormattedForHTML();

    /**
     * The quantity is in the currency of the account!
     * @param lcl the locale to use
     * @return the number of items added to the account
     */
    String getQuantityFormattedForHTML(Locale lcl);

    /**
     * @return the user-defined description for this object
     *         (may contain multiple lines and non-ascii-characters)
     */
    String getDescription();

    public GCshID getLotID();

    /**
     * Get the type of association this split has with
     * an invoice's lot.
     * @return null, or one of the ACTION_xyz values defined
     */
    Action getAction();

    String getActionStr();

}
