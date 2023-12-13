package org.gnucash.api.write.impl.aux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.gnucash.api.Const;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrID;
import org.gnucash.api.basetypes.complex.GCshCmdtyCurrNameSpace;
import org.gnucash.api.basetypes.complex.GCshCmdtyID;
import org.gnucash.api.basetypes.complex.GCshCurrID;
import org.gnucash.api.basetypes.complex.InvalidCmdtyCurrTypeException;
import org.gnucash.api.basetypes.simple.GCshID;
import org.gnucash.api.generated.GncV2;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.numbers.FixedPointNumber;
import org.gnucash.api.read.GnucashCommodity;
import org.gnucash.api.read.impl.aux.GCshPriceImpl;
import org.gnucash.api.write.GnucashWritableFile;
import org.gnucash.api.write.aux.GCshWritablePrice;
import org.gnucash.api.write.impl.GnucashWritableFileImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GCshWritablePriceImpl extends GCshPriceImpl 
                                   implements GCshWritablePrice 
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GCshWritablePriceImpl.class);

    // ---------------------------------------------------------------

    @SuppressWarnings("exports")
    public GCshWritablePriceImpl(
	    final GncV2.GncBook.GncPricedb.Price jwsdpPeer,
	    final GnucashWritableFile file) {
	super(jwsdpPeer, file);
    }

    public GCshWritablePriceImpl(final GnucashWritableFileImpl file) {
	super(createPrice(file, GCshID.getNew()), file);
    }

    // ---------------------------------------------------------------
    
    /**
     * The gnucash-file is the top-level class to contain everything.
     *
     * @return the file we are associated with
     */
    public GnucashWritableFileImpl getWritableFile() {
	return (GnucashWritableFileImpl) file;
    }

    // ---------------------------------------------------------------
    
    // ::TODO
//  public GCshWritablePrice createWritablePrice(final GCshPrice prc) {
//	GCshWritablePriceImpl splt = new GCshWritablePriceImpl(this, prc);
//	addPrice(splt);
//	return splt;
//  }

    private static GncV2.GncBook.GncPricedb.Price createPrice(
	    final GnucashWritableFileImpl file, 
	    final GCshID prcID) {
	
        ObjectFactory factory = file.getObjectFactory();
        
        GncV2.GncBook.GncPricedb.Price prc = file.createGncGncPricedbPriceType();
    
        {
            GncV2.GncBook.GncPricedb.Price.PriceId gncPrcID = factory.createGncV2GncBookGncPricedbPricePriceId();
            gncPrcID.setType(Const.XML_DATA_TYPE_GUID);
            gncPrcID.setValue(prcID.toString());
            prc.setPriceId(gncPrcID);
        }
        
        {
            GncV2.GncBook.GncPricedb.Price.PriceCommodity cmdty = factory.createGncV2GncBookGncPricedbPricePriceCommodity();
            cmdty.setCmdtySpace("xxx");
            cmdty.setCmdtyId("yyy");
            prc.setPriceCommodity(cmdty);
        }
    
        {
            GncV2.GncBook.GncPricedb.Price.PriceCurrency curr = factory.createGncV2GncBookGncPricedbPricePriceCurrency();
            curr.setCmdtySpace(GCshCmdtyCurrNameSpace.CURRENCY);
            curr.setCmdtyId(file.getDefaultCurrencyID());
            prc.setPriceCurrency(curr);
        }
        
        {
            GncV2.GncBook.GncPricedb.Price.PriceTime prcTim = factory.createGncV2GncBookGncPricedbPricePriceTime();
            LocalDate tsDate = LocalDate.now(); // ::TODO
            prcTim.setTsDate(tsDate.toString());
            prc.setPriceTime(prcTim);
        }
        
        prc.setPriceType(Type.LAST.getCode());
        prc.setPriceSource(Source.USER_PRICE.getCode());
        prc.setPriceValue("1");
        
        // file.getRootElement().getGncBook().getBookElements().add(prc);
	GncV2.GncBook.GncPricedb priceDB = file.getPrcMgr().getPriceDB();
	priceDB.getPrice().add(prc);
        file.setModified(true);
    
        return prc;
    }

    // ---------------------------------------------------------------

    @Override
    public void setFromCmdtyCurrQualifID(GCshCmdtyCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableFile().setModified(true);
    }

    @Override
    public void setFromCommodityQualifID(GCshCmdtyID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableFile().setModified(true);
    }

    @Override
    public void setFromCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCommodity().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCommodity().setCmdtyId(qualifID.getCode());
	getWritableFile().setModified(true);
    }

    @Override
    public void setFromCommodity(GnucashCommodity cmdty) {
	setFromCmdtyCurrQualifID(cmdty.getQualifID());
    }

    @Override
    public void setFromCurrencyCode(String code) {
	setFromCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setFromCurrency(GnucashCommodity curr) {
	setFromCommodity(curr);	
    }
    
    // ----------------------------

    @Override
    public void setToCurrencyQualifID(GCshCmdtyCurrID qualifID) {
	if ( ! qualifID.getNameSpace().equals(GCshCmdtyCurrNameSpace.CURRENCY) )
	    throw new InvalidCmdtyCurrTypeException("Is not a currency: " + qualifID.toString());
	
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableFile().setModified(true);
    }

    @Override
    public void setToCurrencyQualifID(GCshCurrID qualifID) {
	jwsdpPeer.getPriceCurrency().setCmdtySpace(qualifID.getNameSpace());
	jwsdpPeer.getPriceCurrency().setCmdtyId(qualifID.getCode());
	getWritableFile().setModified(true);
    }

    @Override
    public void setToCurrencyCode(String code) {
	setToCurrencyQualifID(new GCshCurrID(code));
    }

    @Override
    public void setToCurrency(GnucashCommodity curr) {
	setToCurrencyQualifID(curr.getQualifID());
    }
    
    // ----------------------------

    @Override
    public void setDate(LocalDate date) {
	this.dateTime = ZonedDateTime.of(date, LocalTime.MIN, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableFile().setModified(true);
    }

    @Override
    public void setDateTime(LocalDateTime dateTime) {
	this.dateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
	String datePostedStr = this.dateTime.format(DATE_FORMAT);
	jwsdpPeer.getPriceTime().setTsDate(datePostedStr);
	getWritableFile().setModified(true);
    }

    @Override
    public void setSource(Source src) {
	setSourceStr(src.getCode());
    }

    public void setSourceStr(String str) {
	jwsdpPeer.setPriceSource(str);
	getWritableFile().setModified(true);
    }

    @Override
    public void setType(Type type) {
	setTypeStr(type.getCode());
    }

    public void setTypeStr(String typeStr) {
	jwsdpPeer.setPriceType(typeStr);
	getWritableFile().setModified(true);
    }

    @Override
    public void setValue(FixedPointNumber val) {
	jwsdpPeer.setPriceValue(val.toGnucashString());
	getWritableFile().setModified(true);
    }

}
