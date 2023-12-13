package org.gnucash.api.read.aux;

import java.time.LocalDate;

import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyID;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCommodity;

public interface GCshPrice {

    // For the following enums, cf.:
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gnc-pricedb.h
    
    public enum Type {
	
	LAST        ( "last" ),
	TRANSACTION ( "transaction" ),
	UNKNOWN     ( "unknown" );
	
	// ---
	      
	private String code = "UNSET";

	// ---
	      
	Type(String code) {
	    this.code = code;
	}
	      
	// ---
		
	public String getCode() {
	    return code;
	}
		
	// no typo!
	public static Type valueOff(String code) {
	    for ( Type type : values() ) {
		if ( type.getCode().equals(code) ) {
		    return type;
		}
	    }
	    
	    return null;
	}
    }
	
    public enum Source {
	EDIT_DLG          ( "user:price-editor" ), 
	FQ                ( "Finance::Quote" ),
	USER_PRICE        ( "user:price" ),
	XFER_DLG_VAL      ( "user:xfer-dialog" ),
	SPLIT_REG         ( "user:split-register" ),
	SPLIT_IMPORT      ( "user:split-import" ),
	STOCK_SPLIT       ( "user:stock-split" ),
	STOCK_TRANSACTION ( "user:stock-transaction" ),
	INVOICE           ( "user:invoice-post" ),
	TEMP              ( "temporary" ),
	INVALID           ( "invalid" );
	
	// ---
	      
	private String code = "UNSET";

	// ---
	      
	Source(String code) {
	    this.code = code;
	}
	      
	// ---
		
	public String getCode() {
	    return code;
	}
		
	// no typo!
	public static Source valueOff(String code) {
	    for ( Source src : values() ) {
		if ( src.getCode().equals(code) ) {
		    return src;
		}
	    }
	    
	    return null;
	}
    }
	
    // ---------------------------------------------------------------
	
    GCshID getID();

    // ----------------------------

    GCshCmdtyCurrID getFromCmdtyCurrQualifID() throws InvalidCmdtyCurrTypeException;

    GCshCmdtyID getFromCommodityQualifID() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GCshCurrID getFromCurrencyQualifID() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GnucashCommodity getFromCommodity() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;

    String getFromCurrencyCode() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GnucashCommodity getFromCurrency() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;
    
    // ----------------------------

    GCshCurrID getToCurrencyQualifID() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    String getToCurrencyCode() throws InvalidCmdtyCurrTypeException;

    GnucashCommodity getToCurrency() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;

    // ----------------------------

    LocalDate getDate();

    Source getSource();

    Type getType();

    FixedPointNumber getValue();
    
    String getValueFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    
}
