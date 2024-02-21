package org.gnucash.api.read;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncGncEntry;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.aux.GCshOwner;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Cust;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Empl;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Job;
import org.gnucash.api.read.hlp.GnucashGenerInvoiceEntry_Vend;
import org.gnucash.api.read.spec.GnucashCustomerInvoiceEntry;
import org.gnucash.api.read.spec.GnucashEmployeeVoucherEntry;
import org.gnucash.api.read.spec.GnucashJobInvoiceEntry;
import org.gnucash.api.read.spec.GnucashVendorBillEntry;
import org.gnucash.api.read.spec.WrongInvoiceTypeException;

/**
 * Entry (line item) of a {@link GnucashGenerInvoice}
 * <br>
 * <br>
 * Please note that, just as is the case with generic invoice, In GnuCash lingo, 
 * an "invoice entry" is a technical umbrella term comprising:
 * <ul>
 *   <li>a customer invoice entry  ({@link GnucashCustomerInvoiceEntry})</li>
 *   <li>a vendor bill entry       ({@link GnucashVendorBillEntry})</li>
 *   <li>an employee voucher entry ({@link GnucashEmployeeVoucherEntry})</li>
 *   <li>a job invoice entry       ({@link GnucashJobInvoiceEntry})</li>
 * </ul>
 * Additionally, just as the class {@link GnucashGenerInvoice}, you normally should avoid to 
 * use this one directly; instead, use one its specialized variants.
 * 
 * @see GnucashCustomerInvoiceEntry
 * @see GnucashEmployeeVoucherEntry
 * @see GnucashVendorBillEntry
 * @see GnucashJobInvoiceEntry
 */
public interface GnucashGenerInvoiceEntry extends Comparable<GnucashGenerInvoiceEntry>,
                                                  GnucashGenerInvoiceEntry_Cust,
                                                  GnucashGenerInvoiceEntry_Vend,
                                                  GnucashGenerInvoiceEntry_Empl,
                                                  GnucashGenerInvoiceEntry_Job
{

    // For the following enumerations cf.:
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gncEntry.h  
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
   * @throws WrongInvoiceTypeException
   */
  GCshOwner.Type getType() throws WrongInvoiceTypeException;

  /**
   *
   * @return the unique-id of the invoice we belong to to
   * @see GnucashGenerInvoice#getID()
   */
  GCshID getGenerInvoiceID();

  /**
   * @return the invoice this entry belongs to
   */
  GnucashGenerInvoice getGenerInvoice();

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
