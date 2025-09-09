package org.gnucash.base.basetypes.simple;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A technical entry ID for all GnuCash entities except GnuCashCommodity.
 * (essentially a wrapper for a GUID-string, primarily defined for type safety).
 * 
 * ::TODO: Convert internal value to UUID
 */
public class GCshID {

    private static final Logger LOGGER = LoggerFactory.getLogger(GCshID.class);

    public final static String NULL_ID = "00000000000000000000000000000000";

    // ::MAGIC
    private static final int STANDARD_LENGTH = 32;
    private static final int HEX             = 16;
    
    // -----------------------------------------------------------------

    protected String gcshID;
    private boolean isSet;

    // -----------------------------------------------------------------

    public GCshID() {
    	reset();
    }

    public GCshID(String idStr) throws InvalidGCshIDException {
    	set(idStr.replaceAll("-", ""));
    }

    public GCshID(UUID uuid) throws InvalidGCshIDException {
    	set(uuid.toString().replaceAll("-", ""));
    }

    // -----------------------------------------------------------------

    public void reset() {
    	gcshID = "";
    	isSet = false;
    }

    public String get() throws GCshIDNotSetException {
    	if (!isSet)
    		throw new GCshIDNotSetException();

    	return gcshID;
    }

    public boolean isSet() {
    	return isSet;
    }

    // -----------------------------------------------------------------

    public void set(GCshID value) throws GCshIDNotSetException {
    	set(value.get());
    }

    public void set(String idStr) throws InvalidGCshIDException {
    	this.gcshID = idStr;
    	standardize();
    	validate();
    	isSet = true;
    }

    // -----------------------------------------------------------------

    public void validate() throws InvalidGCshIDException {
    	if ( gcshID.length() != STANDARD_LENGTH )
    		throw new InvalidGCshIDException("No valid GnuCash ID string: '" + gcshID + "': wrong string length");

    	for ( int i = 0; i < STANDARD_LENGTH; i++ ) {
    		if ( ! Character.isDigit(gcshID.charAt(i) ) &&
    			 gcshID.charAt(i) != 'a' &&
    			 gcshID.charAt(i) != 'b' &&
    			 gcshID.charAt(i) != 'c' &&
    			 gcshID.charAt(i) != 'd' &&
    			 gcshID.charAt(i) != 'e' &&
    			 gcshID.charAt(i) != 'f' ) 
    		{
    			LOGGER.error("Char '" + gcshID.charAt(i) + "' is invalid in GCshID '" + gcshID + "'");
    			throw new InvalidGCshIDException("No valid GnuCash ID string: '" + gcshID + "': wrong character at pos " + i);
    		}
    	}
    }

    // -----------------------------------------------------------------

    public void standardize() {
    	gcshID = gcshID.trim().toLowerCase();
    }

    // -----------------------------------------------------------------

    @Override
    public int hashCode() {
    	final int prime = 31;
    	int result = 1;
    	result = prime * result + (isSet ? 1231 : 1237);
    	result = prime * result + ((gcshID == null) ? 0 : gcshID.hashCode());
    	return result;
    }

    @Override
    public boolean equals(Object obj) {
    	if (this == obj)
    		return true;
    	if (obj == null)
    		return false;
    	if (getClass() != obj.getClass())
    		return false;
    	GCshID other = (GCshID) obj;
    	if (isSet != other.isSet)
    		return false;
    	if (gcshID == null) {
    		if (other.gcshID != null)
    			return false;
    	} else if (!gcshID.equals(other.gcshID))
    		return false;
    	return true;
    }

    // -----------------------------------------------------------------

    /**
     * create a GUID for a new element. (GUIDs are globally unique and not tied to a
     * specific kind of entity)
     * 
     * @return the new GnuCash GUID
     */
    public static GCshID getNew() {
    	return new GCshID(UUID.randomUUID());
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
    	if (isSet)
    		return gcshID;
    	else
    		return "(unset)";
    }

}
