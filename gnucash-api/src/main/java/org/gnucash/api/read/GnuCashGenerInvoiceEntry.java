package org.gnucash.api.read;

import java.time.ZonedDateTime;
import java.util.Locale;

import org.gnucash.api.Const_LocSpec;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnuCashGenerInvoiceEntry_Cust;
import org.gnucash.api.read.hlp.GnuCashGenerInvoiceEntry_Empl;
import org.gnucash.api.read.hlp.GnuCashGenerInvoiceEntry_Job;
import org.gnucash.api.read.hlp.GnuCashGenerInvoiceEntry_Vend;
import org.gnucash.api.read.hlp.HasUserDefinedAttributes;
import org.gnucash.api.read.spec.GnuCashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.GnuCashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.GnuCashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnuCashVendorBillEntry;
import org.gnucash.base.basetypes.simple.GCshGenerInvcEntrID;
import org.gnucash.base.basetypes.simple.GCshGenerInvcID;

import xyz.schnorxoborx.base.beanbase.MappingException;
import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Entry (line item) of a {@link GnuCashGenerInvoice}
 * <br>
 * Please note that, just as is the case with generic invoice, In GnuCash lingo, 
 * an "invoice entry" is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer invoice entry  ({@link GnuCashCustomerInvoiceEntry})</li>
 *   <li>a vendor bill entry       ({@link GnuCashVendorBillEntry})</li>
 *   <li>an employee voucher entry ({@link GnuCashEmployeeVoucherEntry})</li>
 *   <li>a job invoice entry       ({@link GnuCashJobInvoiceEntry})</li>
 * </ul>
 * Additionally, just as the class {@link GnuCashGenerInvoice}, you normally should avoid to 
 * use this one directly; instead, use one its specialized variants.
 * 
 * @see GnuCashCustomerInvoiceEntry
 * @see GnuCashEmployeeVoucherEntry
 * @see GnuCashVendorBillEntry
 * @see GnuCashJobInvoiceEntry
 */
public interface GnuCashGenerInvoiceEntry extends Comparable<GnuCashGenerInvoiceEntry>,
                                                  GnuCashGenerInvoiceEntry_Cust,
                                                  GnuCashGenerInvoiceEntry_Vend,
                                                  GnuCashGenerInvoiceEntry_Empl,
                                                  GnuCashGenerInvoiceEntry_Job,
                                                  HasUserDefinedAttributes
{

    // Cf. https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/gncEntry.h  
    public enum Action {
      
    	// ::MAGIC (actually kind of "half-magic")
    	JOB      ("INVC_ENTR_ACTION_JOB"),
    	MATERIAL ("INVC_ENTR_ACTION_MATERIAL"),
    	HOURS    ("INVC_ENTR_ACTION_HOURS");
      
    	// ---

    	private String code = "UNSET";
	
    	// ---
	
    	Action(String code) {
    		if ( code == null )
    			throw new IllegalArgumentException("argument <code> is null");
    		
    		if ( code.trim().length() == 0 )
    			throw new IllegalArgumentException("argument <code> is empty");
    		
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
    		if ( lcl == null )
    			throw new IllegalArgumentException("argument <lcl> is null");
    		
    		if ( code.equals("UNSET") )
    			throw new IllegalStateException("<code> is not properly set");
    		
    		try {
    			Locale oldLcl = Locale.getDefault();
    			Locale.setDefault(lcl);
      			String result = Const_LocSpec.getValue(code);
    			Locale.setDefault(oldLcl);
    			return result;
    		} catch ( Exception exc ) {
    			throw new MappingException("Could not map code '" + code + "' to locale-specific string");
    		}
    	}
		
        // No typo!
        public static Action valueOff(String code) {
      	  if ( code == null ) {
      		  throw new IllegalStateException("argument <code> is null");
      	  }
    		
      	  if ( code.trim().length() == 0 ) {
      		  throw new IllegalStateException("argument <code> is empty");
      	  }
    		
      	  for ( Action val : values() ) {
      		  if ( val.getCode().equals(code.trim()) ) {
      			  return val;
      		  }
      	  }
  	    
      	  return null;
        }

        // No typo!
        public static Action valueOfff(String lclStr) {
      	  return valueOfff(lclStr, Locale.getDefault());
        }
        
        public static Action valueOfff(String lclStr, Locale lcl) {
      	  if ( lclStr == null ) {
      		  throw new IllegalArgumentException("argument <lclStr> is null");
      	  }
  		
      	  if ( lclStr.trim().length() == 0 ) {
      		  throw new IllegalArgumentException("argument <lclStr> is empty");
      	  }
  		
      	  if ( lcl == null )
      		  throw new IllegalArgumentException("argument <lcl> is null");
      	  
      	  for ( Action val : values() ) {
      		  if ( val.getLocaleString(lcl).equals(lclStr.trim()) ) {
      			  return val;
      		  }
      	  }
  	    
      	  return null;
        }
    }
  
  // -----------------------------------------------------------------

  /**
   * @return the unique-id to identify this object with across name- and
   *         hirarchy-changes
   */
  GCshGenerInvcEntrID getID();

  /**
   * @return the type of the customer/vendor invoice entry, i.e. the owner type of
   *         the entry's invoice
   */
  GCshOwner.Type getType();

  /**
   *
   * @return the unique-id of the invoice we belong to to
   * @see GnuCashGenerInvoice#getID()
   */
  GCshGenerInvcID getGenerInvoiceID();

  /**
   * @return the invoice this entry belongs to
   */
  GnuCashGenerInvoice getGenerInvoice();

  // ---------------------------------------------------------------

  /**
   * Tries to map the result of {@link #getActionStr()} to the {@link Action} enum.
   * <br>
   * GnuCash encourages (but does not enforce) the use of quasi-standardized 
   * values via drop-down box (as does this lib's maintainer), so that e.t.
   * can be mapped to the enum if the user adheres to GnuCash's recommendation.
   * <br>
   * The result is not locale-specific.
   * 
   * @return One of the enum values, or null, if it cannot be mapped.
   * 
   * @see #getActionStr()
   */
  Action getAction();

  /**
   * The returned text is saved locale-specific. E.g. "Stunden" instead of "hours"
   * for Germany.
   * <br>
   * <b>Using this method is discouraged.</b>
   * Use {@link #getAction()} whenever possible/applicable instead.
   * <br>
   * <b>Note</b>: One might think that having this function in the interface (as opposed
   * to the implementation alone) is counter-productive, as the number of possible
   * values seems to be limited -- in fact, is is not. To be precise, GnuCash <em>encourages</em>
   * the user to use pre-defined values for the action (via drop-down box), but it does 
   * not <em>enforce</em> it. This is why it is advisable to have it in the interface, 
   * <em>combined</em> with the pseudo-standadized version that returns an enum value 
   * (as opposed to the sister project).
   * 
   * @return Locale-specific String such as 'Hours', 'Heures', 'Horas', 'Stunden', etc.
   * 
   * @see #getAction()
   */
  String getActionStr();

  /**
   * @return the number of items of price ${@link #getCustInvcPrice()} and type
   *         ${@link #getAction()}.
   */
  FixedPointNumber getQuantity();

  /**
   * @return the number of items of price ${@link #getCustInvcPrice()} and type
   *         ${@link #getAction()}.
   */
  String getQuantityFormatted();

  /**
   * @return the user-defined date
   */
  ZonedDateTime getDate();

  /**
   * @return the user-defined date
   */
  String getDateFormatted();

  /**
   * @return the user-defined description for this object (may contain multiple
   *         lines and non-ascii-characters)
   */
  String getDescription();

  // ---------------------------------------------------------------

  @SuppressWarnings("exports")
  GncGncEntry getJwsdpPeer();
}
