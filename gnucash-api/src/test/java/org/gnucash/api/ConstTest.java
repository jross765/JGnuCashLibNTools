package org.gnucash.api;

public class ConstTest extends Const {

    public static final String GCSH_FILENAME     = "test.gnucash";

    public static final String GCSH_FILENAME_IN  = GCSH_FILENAME;

    public static final String GCSH_FILENAME_OUT = "test_out.gnucash";
    
    // ---------------------------------------------------------------
    // Stats for above-mentioned GnuCash test file (before write operations)
    
    public class Stats {
    
    	public static final int NOF_ACCT       = 106;
    	public static final int NOF_ACCT_LOT   = 4;
    	
    	public static final int NOF_TRX        = 17;
    	public static final int NOF_TRX_SPLT   = 43;
	
    	public static final int NOF_VEND_BLL   = 4;
    	public static final int NOF_CUST_INVC  = 2;
    	public static final int NOF_JOB_INVC   = 2;
    	public static final int NOF_EMPL_VCH   = 1;
    	
    	public static final int NOF_GENER_INVC       = NOF_VEND_BLL + NOF_CUST_INVC + NOF_JOB_INVC + NOF_EMPL_VCH;
    	public static final int NOF_GENER_INVC_ENTR  = 15;
	
    	public static final int NOF_CUST       = 3;
    	public static final int NOF_VEND       = 3;
    	public static final int NOF_EMPL       = 1;
    	
    	public static final int NOF_CUST_JOB   = 2;
    	public static final int NOF_VEND_JOB   = 2;
    	public static final int NOF_GENER_JOB  = NOF_CUST_JOB + NOF_VEND_JOB;
	
    	public static final int NOF_CMDTY_SEC  = 6;
    	public static final int NOF_CMDTY_CURR = 2;
    	public static final int NOF_CMDTY_ALL  = NOF_CMDTY_SEC + NOF_CMDTY_CURR;
    	public static final int NOF_PRC        = 23;

    	public static final int NOF_TAXTAB     = 7;
    	public static final int NOF_BLLTRM     = 3;
    
    }

}
