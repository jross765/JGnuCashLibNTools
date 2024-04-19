package org.gnucash.api.read.aux;

import org.gnucash.api.read.GnuCashCustomer;
import org.gnucash.api.read.GnuCashEmployee;
import org.gnucash.api.read.GnuCashGenerJob;
import org.gnucash.api.read.GnuCashVendor;
import org.gnucash.api.read.spec.GnuCashCustomerJob;
import org.gnucash.api.read.spec.GnuCashVendorJob;
import org.gnucash.base.basetypes.simple.GCshID;

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
    // https://github.com/GnuCash/gnucash/blob/stable/libgnucash/engine/gncOwner.h

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

    public Type getInvcType();
    
    public Type getJobType();
    
    public GCshID getID();
    
    // -----------------------------------------------------------------
    
    public GnuCashCustomer    getCustomer();

    public GnuCashVendor      getVendor();

    public GnuCashEmployee    getEmployee();

    // ----------------------------

    public GnuCashGenerJob    getGenerJob();

    public GnuCashCustomerJob getCustomerJob();

    public GnuCashVendorJob   getVendorJob();
}
