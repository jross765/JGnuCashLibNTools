package org.gnucash.api;

import java.util.ResourceBundle;

public class Const_LocSpec {

	private static final String BUNDLE_NAME = "org.gnucash.api.const_locspec"; //$NON-NLS-1$

	// -----------------------------------------------------------------

    public static String TRX_SPLT_ACTION_INCREASE;
    public static String TRX_SPLT_ACTION_DECREASE;
    public static String TRX_SPLT_ACTION_INTEREST;
    public static String TRX_SPLT_ACTION_PAYMENT;
    public static String TRX_SPLT_ACTION_REBATE;
    public static String TRX_SPLT_ACTION_PAYCHECK;
    public static String TRX_SPLT_ACTION_CREDIT;
    public static String TRX_SPLT_ACTION_ATM_DEPOSIT;
    public static String TRX_SPLT_ACTION_ATM_DRAW;
    public static String TRX_SPLT_ACTION_ONLINE;
    public static String TRX_SPLT_ACTION_INVOICE;
    public static String TRX_SPLT_ACTION_BILL;
    public static String TRX_SPLT_ACTION_VOUCHER;
    public static String TRX_SPLT_ACTION_BUY;
    public static String TRX_SPLT_ACTION_SELL;
    public static String TRX_SPLT_ACTION_EQUITY;
    public static String TRX_SPLT_ACTION_PRICE;
    public static String TRX_SPLT_ACTION_FEE;
    public static String TRX_SPLT_ACTION_DIVIDEND;
    public static String TRX_SPLT_ACTION_LTCG;
    public static String TRX_SPLT_ACTION_STCG;
    public static String TRX_SPLT_ACTION_INCOME;
    public static String TRX_SPLT_ACTION_DIST;
    public static String TRX_SPLT_ACTION_SPLIT;

    // ----------------------------

    public static String INVC_ENTR_ACTION_JOB;
    public static String INVC_ENTR_ACTION_MATERIAL;
    public static String INVC_ENTR_ACTION_HOURS;

    // ----------------------------

    public static String INVC_READ_ONLY_SLOT_TEXT;

    // -----------------------------------------------------------------

    public static String getValue(String key) {
    	ResourceBundle bndl = ResourceBundle.getBundle(BUNDLE_NAME);
    	return bndl.getString(key);
    }

}
