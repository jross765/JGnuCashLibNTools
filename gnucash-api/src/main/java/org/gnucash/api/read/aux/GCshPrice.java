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

    // Cf. https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gnc-pricedb.h
    public enum Source {
	EDIT_DLG,         // "user:price-editor"
	FQ,               // "Finance::Quote"
	USER_PRICE,       // "user:price"
	XFER_DLG_VAL,     // "user:xfer-dialog"
	SPLIT_REG,        // "user:split-register"
	SPLIT_IMPORT,     // "user:split-import"
	STOCK_SPLIT,      // "user:stock-split"
	STOCK_TRANSACTION,// "user:stock-transaction"
	INVOICE,          // "user:invoice-post"
	TEMP,             // "temporary"
	INVALID,          // "invalid"    
    }
	
    // ---------------------------------------------------------------
	
    GCshID getId();

    // ----------------------------

    GCshCmdtyCurrID getFromCmdtyCurrQualifId() throws InvalidCmdtyCurrTypeException;

    GCshCmdtyID getFromCommodityQualifId() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GCshCurrID getFromCurrencyQualifId() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GnucashCommodity getFromCommodity() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;

    String getFromCurrencyCode() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    GnucashCommodity getFromCurrency() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;
    
    // ----------------------------

    GCshCurrID getToCurrencyQualifId() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;

    String getToCurrencyCode() throws InvalidCmdtyCurrTypeException;

    GnucashCommodity getToCurrency() throws InvalidCmdtyCurrIDException, InvalidCmdtyCurrTypeException;

    // ----------------------------

    LocalDate getDate();

    String getSource();

    String getType();

    FixedPointNumber getValue();
    
    String getValueFormatted() throws InvalidCmdtyCurrTypeException, InvalidCmdtyCurrIDException;
    
}
