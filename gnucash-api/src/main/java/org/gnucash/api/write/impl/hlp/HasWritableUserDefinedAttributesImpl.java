package org.gnucash.api.write.impl.hlp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.SlotListAlreadyContainsKeyException;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnuCashWritableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.bind.JAXBElement;

public class HasWritableUserDefinedAttributesImpl extends HasUserDefinedAttributesImpl 
                                                  // implements HasWritableUserDefinedAttributes
{

	private static final Logger LOGGER = LoggerFactory.getLogger(HasWritableUserDefinedAttributesImpl.class);

	protected static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(Const.REDUCED_DATE_FORMAT_BOOK);

	// ---------------------------------------------------------------

	public static void addUserDefinedAttributeCore(SlotsType slots,
												   final GnuCashWritableFile gcshFile,
												   final String type, final String name, final String value) {
		if ( slots == null )
			throw new IllegalArgumentException("argument <slots> is null");
		
		if ( gcshFile == null )
			throw new IllegalArgumentException("argument <gcshFile> is null");
		
		if ( type == null )
			throw new IllegalArgumentException("argument <type> is null");
		
		if ( type.trim().length() == 0 )
			throw new IllegalArgumentException("argument <type> is empty");
		
		if ( name == null )
			throw new IllegalArgumentException("argument <name> is null");
		
		if ( name.trim().length() == 0 )
			throw new IllegalArgumentException("argument <vend> is empty");

		if ( value == null )
			throw new IllegalArgumentException("argument <value> is null");
		
		// CAUTION: Yes, that's valid
//		if ( value.trim().length() == 0 )
//			throw new IllegalArgumentException("argument <value> is empty");
		
		// This makes sure that the slots list is initialized
		// in case it had has been null.
		@SuppressWarnings("unused")
		List<Slot> dummy = slots.getSlot();
		
		addUserDefinedAttributeCore(slots.getSlot(), gcshFile, 
					                type, name, value);
	}
	
	public static void removeUserDefinedAttributeCore(SlotsType slots, 
													  final GnuCashWritableFile gcshFile,
													  final String name) {
		if ( slots == null )
			throw new IllegalArgumentException("argument <slots> null");

		if ( gcshFile == null )
			throw new IllegalArgumentException("argument <gcshFile> is null");

		if ( name == null )
			throw new IllegalArgumentException("argument <name> is null");

		if ( name.trim().length() == 0 )
			throw new IllegalArgumentException("argument <vend> is empty");

		// This makes sure that the slots list is initialized
		// in case it had has been null.
		@SuppressWarnings("unused")
		List<Slot> dummy = slots.getSlot();

		removeUserDefinedAttributeCore(slots.getSlot(), gcshFile, name);
	}

	public static void setUserDefinedAttributeCore(SlotsType slots,
            									   final GnuCashWritableFile gcshFile,
            									   final String name, final String value) {
		if ( slots == null )
			throw new IllegalArgumentException("argument <slots> is null");
		
		// This makes sure that the slots list is initialized
		// in case it had has been null.
		@SuppressWarnings("unused")
		List<Slot> dummy = slots.getSlot();
		
		setUserDefinedAttributeCore(slots.getSlot(), gcshFile, name, value);
	}
	
	// ---------------------------------------------------------------

	private static void addUserDefinedAttributeCore(List<Slot> slotList,
			                                        final GnuCashWritableFile gcshFile,
			                                        final String type, final String name, 
			                                        final String value) {
		if ( slotList == null )
			throw new IllegalArgumentException("argument <slotList> is null");
		
		if ( gcshFile == null )
			throw new IllegalArgumentException("argument <gcshFile> is null");
		
		if ( type == null )
			throw new IllegalArgumentException("argument <type> is null");
		
		if ( type.trim().length() == 0 )
			throw new IllegalArgumentException("argument <slotList> is empty");
		
		if ( name == null )
			throw new IllegalArgumentException("argument <name> is null");
		
		if ( name.trim().length() == 0 )
			throw new IllegalArgumentException("argument <slotList> is empty");

		if ( value == null )
			throw new IllegalArgumentException("argument <values> is null");
		
		// CAUTION: Yes, that's valid
//		if ( value.trim().length() == 0 )
//			throw new IllegalArgumentException("argument <value> is null");

		if ( getUserDefinedAttributeKeysCore(slotList).contains(name) )
			throw new SlotListAlreadyContainsKeyException();
		
		ObjectFactory fact = new ObjectFactory();
		Slot newSlot = fact.createSlot();
		newSlot.setSlotKey(name);
		SlotValue newValue = fact.createSlotValue();
		newValue.setType(Const.XML_DATA_TYPE_STRING);
		newValue.getContent().add(value);
		newSlot.setSlotValue(newValue);
		LOGGER.debug("addUserDefinedAttributeCore: (name=" + name + ", value=" + value + ") - adding new slot ");

		slotList.add(newSlot);

		gcshFile.setModified(true);
	}

	private static void removeUserDefinedAttributeCore(List<Slot> slotList,
            										   final GnuCashWritableFile gcshFile,
            										   final String name) {
		if ( slotList == null )
			throw new IllegalArgumentException("argument <slotList> is null");

		if ( gcshFile == null )
			throw new IllegalArgumentException("argument <gcshFile> is null");

		if ( name == null )
			throw new IllegalArgumentException("argument <name> is null");

		if ( name.trim().length() == 0 )
			throw new IllegalArgumentException("argument <name> is empty");

		if ( ! getUserDefinedAttributeKeysCore(slotList).contains(name) )
			throw new SlotListDoesNotContainKeyException();

		for ( Slot slt : slotList ) {
			if ( slt.getSlotKey().equals(name) ) {
				LOGGER.debug("removeUserDefinedAttributeCore: (name=" + name
						+ "') - removing existing slot");

				slotList.remove(slt);

				gcshFile.setModified(true);
				return;
			}
		}
	}

	private static void setUserDefinedAttributeCore(List<Slot> slotList,
            									    final GnuCashWritableFile gcshFile,
            									    final String name, final String value) {
		if ( slotList == null )
			throw new IllegalArgumentException("argument <slotList> is null");

		if ( gcshFile == null )
			throw new IllegalArgumentException("argument <gcshFile> is null");

		if ( name == null )
			throw new IllegalArgumentException("argument <name> is null");
		
		if ( name.trim().length() == 0 )
			throw new IllegalArgumentException("argument <slotList> is empty");

		if ( value == null )
			throw new IllegalArgumentException("argument <value> is null");
		
		// CAUTION: Yes, that's valid
//		if ( value.trim().length() == 0 )
//			throw new IllegalArgumentException("argument <value> is null");
		
		if ( ! getUserDefinedAttributeKeysCore(slotList).contains(name) )
			throw new SlotListDoesNotContainKeyException();

		for ( Slot slt : slotList ) {
			if ( slt.getSlotKey().equals(name) ) {
				if ( slt.getSlotValue().getType().equals(Const.XML_DATA_TYPE_STRING) ||
					 slt.getSlotValue().getType().equals(Const.XML_DATA_TYPE_INTEGER) ||
					 slt.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
					LOGGER.debug("setUserDefinedAttributeCore: (name=" + name + ", value='" + value
							+ "') - overwriting existing slot");

					slt.getSlotValue().getContent().clear();
					slt.getSlotValue().getContent().add(value);
				} else if ( slt.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GDATE)  ) {
					List<Object> objList = slt.getSlotValue().getContent();
					if ( objList == null || objList.size() == 0 ) {
						LOGGER.error("setUserDefinedAttributeCore: Not found (2.1)");
						throw new SlotListAlreadyContainsKeyException();
					}
					Object valElt = null;
					for ( Object obj : objList ) {
						if ( obj.getClass().getName().contains("JAXBElement") ) {
							valElt = obj;
						}
					}
					if ( valElt == null ) {
						LOGGER.error("setUserDefinedAttributeCore: Not found (2.2)");
						throw new SlotListAlreadyContainsKeyException();
					}
					LOGGER.debug("User-defined attribute for key '" + name + "' may not be a String."
							+ " It is of type [" + valElt.getClass().getName() + "]");
					if ( valElt instanceof String ) {
						slt.getSlotValue().getContent().clear();
						slt.getSlotValue().getContent().add(value);
					} else if ( valElt instanceof JAXBElement ) {
						// JAXBElement<LocalDate> elt = (JAXBElement<LocalDate>) valElt;
						JAXBElement<Date> elt = (JAXBElement<Date>) valElt;
						// JAXBElement<String> elt = (JAXBElement<String>) valElt;
						
						LocalDate date = LocalDate.parse(value); 
						// String dateStr = dateTime.format(DATE_FORMAT);
						Date dat = java.util.Date.from(date.atStartOfDay()
				                .atZone(ZoneId.systemDefault())
				                .toInstant()); 

						// elt.setValue(date);
						// elt.setValue(new Date(date.getYear(), date.getMonthValue(), date.getDayOfMonth()));
						elt.setValue(dat);
						// elt.setValue(date.toString());
					} else {
						LOGGER.error("User-defined attribute for key '" + name + "' may not be a String."
								+ " It is of UNKNOWN type [" + valElt.getClass().getName() + "]");
						throw new SlotListAlreadyContainsKeyException();
					}
				}

				gcshFile.setModified(true);
				return;
			}
		}
	}

	// ---------------------------------------------------------------

	// Remove slots with dummy content
	public static void cleanSlots(SlotsType slots) {
		if ( slots == null )
			return;

		for ( Slot slot : slots.getSlot() ) {
			if ( slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				slots.getSlot().remove(slot);
				break;
			}
		}
	}
}
