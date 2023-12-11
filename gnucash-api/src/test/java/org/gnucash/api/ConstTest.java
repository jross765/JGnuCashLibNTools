package org.gnucash.api;

public class ConstTest extends Const
{

    public static final String GCSH_FILENAME     = "test.gnucash";

    public static final String GCSH_FILENAME_IN  = GCSH_FILENAME;

    public static final String GCSH_FILENAME_OUT = "test_out.gnucash";
    
    // ---------------------------------------------------------------
    // counters before write operations
    
    public static final int COUNT_ACCT      = 93;
    public static final int COUNT_TRX       = 12;
    public static final int COUNT_TRX_SPLT  = 31;
    public static final int COUNT_INVC      = 7;
    public static final int COUNT_INVC_ENTR = 14; // !
    public static final int COUNT_CUST      = 3;
    public static final int COUNT_VEND      = 3;
    public static final int COUNT_EMPL      = 1;
    public static final int COUNT_JOB       = 2;
    public static final int COUNT_TAXTAB    = 7;
    public static final int COUNT_BLLTRM    = 3;
    public static final int COUNT_CMDTY     = 6; // !
    public static final int COUNT_PRC       = 9;

}
