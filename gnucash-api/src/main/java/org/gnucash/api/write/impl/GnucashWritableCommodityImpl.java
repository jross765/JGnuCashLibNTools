package org.gnucash.api.write.impl;

import org.gnucash.api.Const;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrIDException;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncCommodity;
import org.gnucash.api.read.impl.GnucashCommodityImpl;
import org.gnucash.api.write.GnucashWritableCommodity;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.hlp.GnucashWritableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GnucashCommodityImpl to allow read-write access instead of
 * read-only access.
 */
public class GnucashWritableCommodityImpl extends GnucashCommodityImpl 
                                          implements GnucashWritableCommodity,
                                                     GnucashWritableObject
{
    /**
     * Automatically created logger for debug and error-output.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GnucashWritableCommodityImpl.class);
    
    // ---------------------------------------------------------------

    /**
     * Please use ${@link GnucashWritableFile#createWritableCommodity()}.
     *
     * @param file      the file we belong to
     * @param jwsdpPeer the JWSDP-object we are facading.
     */
    @SuppressWarnings("exports")
    public GnucashWritableCommodityImpl(final GncCommodity jwsdpPeer,
	    final GnucashWritableFileImpl file) {
	super(jwsdpPeer, file);
    }

    /**
     * Please use ${@link GnucashWritableFile#createWritableCommodity()}.
     *
     * @param file the file we belong to
     * @param id   the ID we shall have
     */
    protected GnucashWritableCommodityImpl(final GnucashWritableFileImpl file) {
	super(createCommodity_int(file, GCshID.getNew()), file);
    }

    public GnucashWritableCommodityImpl(GnucashCommodityImpl cmdty) {
	super(cmdty.getJwsdpPeer(), cmdty.getGnucashFile());
    }

    // ---------------------------------------------------------------

    /**
     * Delete this commodity and remove it from the file.
     * @throws InvalidCmdtyCurrIDException 
     * @throws ObjectCascadeException 
     * @throws InvalidCmdtyCurrTypeException 
     *
     * @see GnucashWritableCommodity#remove()
     */
    public void remove() throws InvalidCmdtyCurrTypeException, ObjectCascadeException, InvalidCmdtyCurrIDException {
	GncCommodity peer = getJwsdpPeer();
	(getGnucashFile()).getRootElement().getGncBook().getBookElements().remove(peer);
	(getGnucashFile()).removeCommodity(this);
    }

    // ---------------------------------------------------------------

    /**
     * Creates a new Transaction and add's it to the given gnucash-file Don't modify
     * the ID of the new transaction!
     *
     * @param file the file we will belong to
     * @param guid the ID we shall have
     * @return a new jwsdp-peer already entered into th jwsdp-peer of the file
     */
    protected static GncCommodity createCommodity_int(
	    final GnucashWritableFileImpl file,
	    final GCshID newID) {
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}

	GncCommodity jwsdpCmdty = file.createGncGncCommodityType();

	jwsdpCmdty.setCmdtyFraction(Const.CMDTY_FRACTION_DEFAULT);
	jwsdpCmdty.setVersion(Const.XML_FORMAT_VERSION);
	jwsdpCmdty.setCmdtyName("no name given");
	jwsdpCmdty.setCmdtySpace(GCshCmdtyCurrNameSpace.Exchange.EURONEXT.toString()); // ::TODO : soft
	jwsdpCmdty.setCmdtyId("XYZ"); // ::TODO
	jwsdpCmdty.setCmdtyXcode(Const.CMDTY_XCODE_DEFAULT);

	file.getRootElement().getGncBook().getBookElements().add(jwsdpCmdty);
	file.setModified(true);
	
        LOGGER.debug("createCommodity_int: Created new commodity (core): " + jwsdpCmdty.getCmdtySpace() + ":" + jwsdpCmdty.getCmdtyId());
        
	return jwsdpCmdty;
    }

    // ---------------------------------------------------------------

    @Override
    public void setQualifID(GCshCmdtyCurrID qualifId) throws InvalidCmdtyCurrTypeException {
	if ( qualifId == null ) {
	    throw new IllegalArgumentException("null qualif-ID given!");
	}

	getJwsdpPeer().setCmdtySpace(qualifId.getNameSpace());
	getJwsdpPeer().setCmdtyId(qualifId.getCode());

	getGnucashFile().setModified(true);
    }

    @Override
    public void setXCode(String xCode) {
	if ( xCode == null ) {
	    throw new IllegalArgumentException("null x-code given!");
	}

	if ( xCode.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty x-code given!");
	}

	getJwsdpPeer().setCmdtyXcode(xCode);
	getGnucashFile().setModified(true);
    }

    @Override
    public void setName(String name) {
	if ( name == null ) {
	    throw new IllegalArgumentException("null name given!");
	}

	if ( name.trim().length() == 0 ) {
	    throw new IllegalArgumentException("empty name given!");
	}

	getJwsdpPeer().setCmdtyName(name);
	getGnucashFile().setModified(true);
    }

    @Override
    public void setFraction(Integer fract) {
	if ( fract <= 0 ) {
	    throw new IllegalArgumentException("Fraction is <= 0");
	}
	
	getJwsdpPeer().setCmdtyFraction(fract);
	getGnucashFile().setModified(true);
    }

    // ---------------------------------------------------------------

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnucashWritableFileImpl getWritableGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnucashWritableFileImpl getGnucashFile() {
	return (GnucashWritableFileImpl) super.getGnucashFile();
    }

    // ---------------------------------------------------------------

    /**
     * @see GnucashWritableObject#setUserDefinedAttribute(java.lang.String,
     *      java.lang.String)
     */
    // ::TODO ?
    public void setUserDefinedAttribute(final String name, final String value) {
	// ::EMPTY
    }

    // ::TODO
    public void clean() {
	// helper.cleanSlots();
    }

    // -----------------------------------------------------------------

    @Override
    public String toString() {
	
	String result = "GnucashWritableCommodityImpl [";

	try {
	    result += "qualif-id='" + getQualifID().toString() + "'";
	} catch (InvalidCmdtyCurrTypeException e) {
	    result += "qualif-id=" + "ERROR";
	}
	
	result += ", namespace='" + getNameSpace() + "'"; 
	result += ", name='" + getName() + "'"; 
	result += ", x-code='" + getXCode() + "'"; 
	result += ", fraction=" + getFraction() + "]";
	
	return result;
    }

}
