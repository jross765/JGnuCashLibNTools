package org.gnucash.api.basetypes.simple;

public class InvalidGCshIDException extends RuntimeException {

    private static final long serialVersionUID = -8293041843807512970L;
    
    public InvalidGCshIDException(String msg) {
	super(msg);
    }

}
