package org.gnucash.base.basetypes.complex;

public class InvalidCmdtyCurrTypeException extends RuntimeException {

    private static final long serialVersionUID = 6595261905782442716L;
    
    // ---------------------------------------------------------------
    
    public InvalidCmdtyCurrTypeException() {
	super();
    }

    public InvalidCmdtyCurrTypeException(String msg) {
	super(msg);
    }

}
