package org.gnucash.api.read;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Locale;

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
import org.gnucash.base.basetypes.simple.GCshID;

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

    // For the following enumerations cf.:
    // https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/gncEntry.h  
    public enum Action {
      
	// ::MAGIC (actually kind of "half-magic")
	JOB      ("INVC_ENTR_ACTION_JOB"),
	MATERIAL ("INVC_ENTR_ACTION_MATERIAL"),
	HOURS    ("INVC_ENTR_ACTION_HOURS");
      
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
  
  // -----------------------------------------------------------------

  /**
   * @return the unique-id to identify this object with across name- and
   *         hirarchy-changes
   */
  GCshID getID();

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
  GCshID getGenerInvoiceID();

  /**
   * @return the invoice this entry belongs to
   */
  GnuCashGenerInvoice getGenerInvoice();

  // ---------------------------------------------------------------

  /**
   * The returned text is saved locale-specific. E.g. "Stunden" instead of "hours"
   * for Germany.
   * 
   * @return HOURS or ITEMS, ....
   * 
   */
  Action getAction();

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
