package org.gnucash.api.write.impl.hlp;

import java.beans.PropertyChangeSupport;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.gnucash.api.read.impl.hlp.HasUserDefinedAttributesImpl;
import org.gnucash.api.read.impl.hlp.SlotListDoesNotContainKeyException;
import org.gnucash.api.write.GnucashWritableFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasWritableUserDefinedAttributesImpl extends HasUserDefinedAttributesImpl 
                                                  // implements HasWritableUserDefinedAttributes
{

	private static final Logger LOGGER = LoggerFactory.getLogger(HasWritableUserDefinedAttributesImpl.class);

	// ---------------------------------------------------------------

	public static void setUserDefinedAttributeCore(final List<Slot> slotList,
			                                       final GnucashWritableFile gcshFile,
			                                       final String name, final String value) {
		if ( ! getUserDefinedAttributeKeysCore(slotList).contains(name) ) {
			throw new SlotListDoesNotContainKeyException();
		}
		
		for ( Slot slt : slotList ) {
			if ( slt.getSlotKey().equals(name) ) {
				LOGGER.debug("setUserDefinedAttributeCore: (name=" + name + ", value='" + value
						+ "') - overwriting existing slot");

				slt.getSlotValue().getContent().clear();
				slt.getSlotValue().getContent().add(value);

				gcshFile.setModified(true);
				return;
			}
		}

		ObjectFactory objectFactory = new ObjectFactory();
		Slot newSlot = objectFactory.createSlot();
		newSlot.setSlotKey(name);
		SlotValue newValue = objectFactory.createSlotValue();
		newValue.setType(Const.XML_DATA_TYPE_STRING);
		newValue.getContent().add(value);
		newSlot.setSlotValue(newValue);
		LOGGER.debug("setUserDefinedAttribute: (name=" + name + ", value=" + value + ") - adding new slot ");

		slotList.add(newSlot);

		gcshFile.setModified(true);
	}

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
