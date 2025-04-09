package org.gnucash.apiext.secacct;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.gnucash.api.read.GnuCashAccount;
import org.gnucash.api.read.GnuCashTransactionSplit;
import org.gnucash.api.write.GnuCashWritableTransaction;
import org.gnucash.api.write.GnuCashWritableTransactionSplit;
import org.gnucash.api.write.impl.GnuCashWritableFileImpl;
import org.gnucash.apiext.secacct.SecuritiesAccountTransactionManager.StockSplitVar;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.tuples.AcctIDAmountPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xyz.schnorxoborx.base.numbers.FixedPointNumber;

/**
 * Collection of simplified, high-level access functions to a GnuCash file for
 * managing buy/sell lots in a securities account (brokerage account).
 * <br>
 * These methods are sort of "macros" for the low-level access functions
 * in the "API" module.
 */
public class SecuritiesAccountLotManager {
    
    // ---------------------------------------------------------------
    
    // Logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SecuritiesAccountLotManager.class);
    
    // ----------------------------

    // ::EMPTY

    // ---------------------------------------------------------------
    
}
