package org.gnucash.read.aux;

import org.gnucash.generated.GncV2;

// ::TODO ::CHECK 
// Really interface? Or shouldn't it just be a wrapper class
// for the enums?
public interface GCshOwner {

    public enum JIType { // ::TODO in search of a better name...
	      INVOICE,
	      JOB,
	      UNSET
    }

    // For the following enum, cf.:
    // https://github.com/Gnucash/gnucash/blob/stable/libgnucash/engine/gncOwner.h

    public enum Type {
	
	// ::MAGIC
	CUSTOMER  (2, "gncCustomer"),
	JOB       (3, "gncJob"),
	VENDOR    (4, "gncVendor"),
	EMPLOYEE  (5, "gncEmployee"),
	
	NONE      (0, "NONE"),
	UNDEFINED (1, "UNDEFINED");
	
	// ---

	private int    index = -1;
	private String code = "UNSET";
	
	// ---
	
	Type(int index, String code) {
	    this.index = index;
	    this.code = code;
	}

	// ---
	
	public int getIndex() {
	    return index;
	}

	public String getCode() {
	    return code;
	}
	
	// no typo!
	public static Type valueOff(int index) {
	    for ( Type type : values() ) {
		if ( type.getIndex() == index ) {
		    return type;
		}
	    }
	    
	    return null;
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
    
    // -----------------------------------------------------------------
    // ::TODO: ::CHECK: 
    // Are the following really needed?
  
    public JIType getJIType();

    public Type getInvcType() throws WrongOwnerJITypeException;
    
    public String getId() throws OwnerJITypeUnsetException;
    
    // -----------------------------------------------------------------
    
    @SuppressWarnings("exports")
    GncV2.GncBook.GncGncInvoice.InvoiceOwner getInvcOwner() throws WrongOwnerJITypeException;

    @SuppressWarnings("exports")
    GncV2.GncBook.GncGncJob.JobOwner getJobOwner() throws WrongOwnerJITypeException;
    
}
