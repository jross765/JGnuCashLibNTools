package org.gnucash.api.read;

public class MappingException extends RuntimeException {

    private static final long serialVersionUID = -5988857879950695273L;
    
    // ---------------------------------------------------------------

    public MappingException() {
	super();
    }

    public MappingException(String str) {
	super(str);
    }

}
