package org.gnucash.api.write.impl;

import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.gnucash.api.Const;
import org.gnucash.api.generated.GncPricedb;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Price;
import org.gnucash.api.read.GnuCashCommodity;
import org.gnucash.api.read.impl.GnuCashPriceImpl;
import org.gnucash.api.write.GnuCashWritableFile;
import org.gnucash.api.write.GnuCashWritablePrice;
import org.gnucash.api.write.impl.hlp.GnuCashWritableObjectImpl;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.base.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.base.basetypes.complex.GCshCmdtyID;
import org.gnucash.base.basetypes.complex.GCshCurrID;
import org.gnucash.base.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.base.basetypes.simple.GCshID;
import org.gnucash.base.numbers.FixedPointNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extension of GCshPriceImpl to allow read-write access instead of
 * read-only access.
 */
public class GnuCashWritablePriceImpl extends GnuCashPriceImpl 
                                      implements GnuCashWritablePrice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GnuCashWritablePriceImpl.class);

    // ---------------------------------------------------------------

    /**
     * Our helper to implement the GnuCashWritableObject-interface.
     */
    private final GnuCashWritableObjectImpl helper = new GnuCashWritableObjectImpl(getWritableGnuCashFile(), this);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GnuCashWritablePriceImpl(
	    final Price jwsdpPeer,
	    final GnuCashWritableFile file) {
	super(jwsdpPeer, file);
    }

    public GnuCashWritablePriceImpl(final GnuCashWritableFileImpl file) {
	super(createPrice_int(file, GCshID.getNew()), file);
    }

    public GnuCashWritablePriceImpl(GnuCashPriceImpl prc) {
	super(prc.getJwsdpPeer(), prc.getGnuCashFile());
    }

    // ---------------------------------------------------------------

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getWritableGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    @Override
    public GnuCashWritableFileImpl getGnuCashFile() {
	return (GnuCashWritableFileImpl) super.getGnuCashFile();
    }

    // ---------------------------------------------------------------
    
    // ::TODO
//  public GCshWritablePrice createWritablePrice(final GCshPrice prc) {
//	GCshWritablePriceImpl splt = new GCshWritablePriceImpl(this, prc);
//	addPrice(splt);
//	return splt;
//  }

    private static Price createPrice_int(
	    final GnuCashWritableFileImpl file, 
	    final GCshID newID) {
	
		if ( newID == null ) {
			throw new IllegalArgumentException("null ID given");
		}

		if ( ! newID.isSet() ) {
			throw new IllegalArgumentException("unset ID given");
		}
		
        ObjectFactory factory = file.getObjectFactory();
        
        Price prc = file.createGncGncPricedbPriceType();
    
        {
            Price.PriceId gncPrcID = factory.createPricePriceId();
            gncPrcID.setType(Const.XML_DATA_TYPE_GUID);
            gncPrcID.setValue(newID.toString());
            prc.setPriceId(gncPrcID);
        }
        
        {
            Price.PriceCommodity cmdty = factory.createPricePriceCommodity();
            cmdty.setCmdtySpace("xxx");
            cmdty.setCmdtyId("yyy");
            prc.setPriceCommodity(cmdty);
        }
    
        {
            Price.PriceCurrency curr = factory.createPricePriceCurrency();
            curr.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            curr.setCmdtyId(file.getDefaultCurrencyID());
            prc.setPriceCurrency(curr);
        }
        
        {
            Price.PriceTime prcTim = factory.createPricePriceTime();
            LocalDate tsDate = LocalDate.now(); // ::TODO
            prcTim.setTsDate(tsDate.toString());
            prc.setPriceTime(prcTim);
        }
        
        prc.setPriceType(Type.LAST.getCode());
        prc.setPriceSource(Source.USER_PRICE.getCode());
        prc.setPriceValue("1");
        
        // file.getRootElement().getGncBook().getBookElements().add(prc);
        GncPricedb priceDB = file.getPrcMgr().getPriceDB();
	priceDB.getPrice().add(prc);
        file.setModified(true);
    
        return prc;
    }

    // ---------------------------------------------------------------

    @Override
    public void setFromCmdtyCurrQualifID(GCshCmdtyCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setFromCommodityQualifID(GCshCmdtyID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setFromCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setFromCommodity(GnuCashCommodity cmdty) {
	setFromCmdtyCurrQualifID(cmdty.getQualifID());
    }

    @Override
    public void setFromCurrencyCode(String code) {
	setFromCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setFromCurrency(GnuCashCommodity curr) {
	setFromCommodity(curr);	
    }
    
    // ----------------------------

    @Override
    public void setToCurrencyQualifID(GCshCmdtyCurrID qualifID) {
	if ( ! qualifID.getNameSpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) )
	    throw new InvalidCmdtyCurrTypeException("Is not a currency: " + qualifID.toString());
	
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setToCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setToCurrencyCode(String code) {
	setToCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setToCurrency(GnuCashCommodity curr) {
	setToCurrencyQualifID(curr.getQualifID());
    }
    
    // ----------------------------

    @Override
    public void setDate(LocalDate date) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, date);
	}
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
	LocalDate oldDate = getDate();
	this.dateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableGnuCashFile().setModified(true);

	PropertyChangeSupport propertyChangeSupport = helper.getPropertyChangeSupport();
	if (propertyChangeSupport != null) {
	    propertyChangeSupport.firePropertyChange("price", oldDate, dateTime);
	}
    }

    @Override
    public void setSource(Source src) {
	setSourceStr(src.getCode());
    }

    public void setSourceStr(String str) {
	jwsdpPeer.setPriceSource(str);
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setType(Type type) {
	setTypeStr(type.getCode());
    }

    public void setTypeStr(String typeStr) {
	jwsdpPeer.setPriceType(typeStr);
	getWritableGnuCashFile().setModified(true);
    }

    @Override
    public void setValue(FixedPointNumber val) {
	jwsdpPeer.setPriceValue(val.toGnuCashString());
	getWritableGnuCashFile().setModified(true);
    }

    // ---------------------------------------------------------------
    
    @Override
    public String toString() {
	String result = "GnuCashWritablePriceImpl [";
	
	result += "id=" + getID();
	
	try {
	    result += ", cmdty-qualif-id='" + getFromCmdtyCurrQualifID() + "'";
	} catch (InvalidCmdtyCurrTypeException e) {
	    result += ", cmdty-qualif-id=" + "ERROR";
	}
	
	try {
	    result += ", curr-qualif-id='" + getToCurrencyQualifID() + "'";
	} catch (Exception e) {
	    result += ", curr-qualif-id=" + "ERROR";
	}
	
	result += ", date=" + getDate(); 
	
	try {
	    result += ", value=" + getValueFormatted();
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    result += ", value=" + "ERROR";
	}
	
	result += ", type=" + getType();
	result += ", source=" + getSource(); 

	result += "]"; 

	return result;
    }
    
}
