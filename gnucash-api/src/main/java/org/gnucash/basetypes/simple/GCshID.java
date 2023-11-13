package org.gnucash.basetypes.simple;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshID {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(GCshID.class);

    private final static int STANDARD_LENGTH = 32;

    // -----------------------------------------------------------------

    protected String gcshID;
    private boolean isSet;

    // -----------------------------------------------------------------

    public GCshID() {
	reset();
    }

    public GCshID(String idStr) throws InvalidGCshIDException {
	set(idStr);
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

    public void set(GCshID value) throws InvalidGCshIDException, GCshIDNotSetException {
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
	if (gcshID.length() != STANDARD_LENGTH)
	    throw new InvalidGCshIDException("No valid GnuCash ID string: '" + gcshID + "': wrong string length");

	for (int i = 0; i < STANDARD_LENGTH; i++) {
	    if ( ! Character.isDigit(gcshID.charAt(i)) &&
		 gcshID.charAt(i) != 'a' &&
		 gcshID.charAt(i) != 'b' &&
		 gcshID.charAt(i) != 'c' &&
		 gcshID.charAt(i) != 'd' &&
		 gcshID.charAt(i) != 'e' &&
		 gcshID.charAt(i) != 'f' ) 
	    {
		logger.error("Char '" + gcshID.charAt(i) + "' is invalid in GCshID '" + gcshID + "'");
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

    @Override
    public String toString() {
	if (isSet)
	    return gcshID;
	else
	    return "(unset)";
    }

}
