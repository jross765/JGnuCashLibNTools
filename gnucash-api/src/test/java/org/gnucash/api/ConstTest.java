package org.gnucash.api;

public class ConstTest extends Const {

    public static final String GCSH_FILENAME     = "test.gnucash";

    public static final String GCSH_FILENAME_IN  = GCSH_FILENAME;

    public static final String GCSH_FILENAME_OUT = "test_out.gnucash";
    
    // ---------------------------------------------------------------
    // Stats for above-mentioned GnuCash test file (before write operations)
    
    public class Stats {
    
	public static final int NOF_ACCT       = 102;
	public static final int NOF_TRX        = 14;
	public static final int NOF_TRX_SPLT   = 36;
	
	public static final int NOF_INVC       = 7;
	public static final int NOF_INVC_ENTR  = 14;
	
	public static final int NOF_CUST       = 3;
	public static final int NOF_VEND       = 3;
	public static final int NOF_EMPL       = 1;
	public static final int NOF_JOB        = 2;
	
	public static final int NOF_CMDTY_SEC  = 6;
	public static final int NOF_CMDTY_CURR = 2;
	public static final int NOF_CMDTY_ALL  = NOF_CMDTY_SEC + NOF_CMDTY_CURR;
	public static final int NOF_PRC        = 9;

	public static final int NOF_TAXTAB     = 7;
	public static final int NOF_BLLTRM     = 3;
    
    }

}
