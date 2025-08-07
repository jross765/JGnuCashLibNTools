package org.gnucash.base.basetypes.simple.aux;

import java.util.UUID;

import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.basetypes.simple.InvalidGCshIDException;

public class GCshLotID extends GCshID {

    public GCshLotID() {
    	super();
    }

    public GCshLotID(String idStr) throws InvalidGCshIDException {
   		super(idStr);
    }

    public GCshLotID(UUID uuid) throws InvalidGCshIDException {
   		super(uuid);
    }

    public GCshLotID(GCshID id) {
    	super();
    	try {
    		set(id);
    	} catch ( Exception exc ) {
    		// We do not want to throw an exception here, 
    		// and it is unnecessary anyway.
    		// This branch can only be reached theoretically.
    		System.err.println("GCshLotID: Severe error: Copy constructor");
    	}
    }

}
