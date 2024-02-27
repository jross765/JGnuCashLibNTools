package org.gnucash.api.read.impl.hlp;

import java.util.ArrayList;
import java.util.List;

import org.gnucash.api.Const;
import org.gnucash.api.generated.ObjectFactory;
import org.gnucash.api.generated.Slot;
import org.gnucash.api.generated.SlotValue;
import org.gnucash.api.generated.SlotsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HasUserDefinedAttributesImpl // implements HasUserDefinedAttributes
{

	private static final Logger LOGGER = LoggerFactory.getLogger(HasUserDefinedAttributesImpl.class);

	// ---------------------------------------------------------------

	public static String getUserDefinedAttributeCore(final List<Slot> slotList, final String name) {
		if ( slotList == null )
			return null;

		if ( name.equals("") )
			return null;

		// ---

		String nameFirst = "";
		String nameRest = "";
		if ( name.contains(".") ) {
			String[] nameParts = name.split("\\.");
			nameFirst = nameParts[0];
			if ( nameParts.length > 1 ) {
				for ( int i = 1; i < nameParts.length; i++ ) {
					nameRest += nameParts[i];
					if ( i < nameParts.length - 1 )
						nameRest += ".";
				}
			}
		} else {
			nameFirst = name;
		}
//	System.err.println("np1: '" + nameFirst + "'");
//	System.err.println("np2: '" + nameRest + "'");

		// ---
	
		for ( Slot slot : slotList ) {
			if ( slot.getSlotKey().equals(nameFirst) ) {
				if ( slot.getSlotValue().getType().equals("string") || 
					 slot.getSlotValue().getType().equals("integer") || 
					 slot.getSlotValue().getType().equals("guid") ) {
					List<Object> objList = slot.getSlotValue().getContent();
					if ( objList == null || objList.size() == 0 )
						return null;
					Object value = objList.get(0);
					if ( value == null )
						return null;
					if ( !(value instanceof String) ) {
						LOGGER.error("User-defined attribute for key '" + nameFirst + "' may not be a String."
								+ " It is of type [" + value.getClass().getName() + "]");
					}
					return value.toString();
				} else if ( slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
					List<Slot> subSlots = new ArrayList<Slot>();
					for ( Object obj : slot.getSlotValue().getContent() ) {
						if ( obj instanceof Slot ) {
							Slot subSlot = (Slot) obj;
							subSlots.add(subSlot);
						}
					}
					return getUserDefinedAttributeCore(subSlots, nameRest);
				} else {
					LOGGER.error("getUserDefinedAttributeCore: Unknown slot type");
					return "NOT IMPLEMENTED YET";
				}
			} // if slot-key
		} // for slot

		return null;
	}

    public static List<String> getUserDefinedAttributeKeysCore(final List<Slot> slotList) {
		List<String> retval = new ArrayList<String>();

		for ( Slot slt : slotList ) {
			retval.add(slt.getSlotKey());
		}

		return retval;
	}
    
	// -----------------------------------------------------------------

	/**
	 * Return slots without the ones with dummy content
	 * 
	 * @return
	 */
	public static List<Slot> getSlotsListClean(final List<Slot> slotList) {
		List<Slot> retval = new ArrayList<Slot>();

		for ( Slot slot : slotList ) {
			if ( ! slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				retval.add(slot);
			}
		}

		return retval;
	}

	/**
	 * Remove slots with dummy content
	 */
	public static void cleanSlots(final List<Slot> slotList) {
		for ( Slot slot : slotList ) {
			if ( ! slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				slotList.remove(slot);
			}
		}
	}
	
	// ---------------------------------------------------------------
	
    /**
     * @param newSlots The slots to set.
     */
	// Sic, not in HasWritableUserDefinedAttributes
	//                ========
	public static void setSlotsInit(
			SlotsType currSlots,
			final SlotsType newSlots) {
		if ( newSlots == null ) {
			throw new IllegalArgumentException("null 'slots' given!");
		}

		SlotsType oldSlots = currSlots;
		if ( oldSlots == newSlots ) {
			return; // nothing has changed
		}
		// ::TODO Check with equals as well
		currSlots = newSlots;

		// we have an xsd-problem saving empty slots so we add a dummy-value
		if ( newSlots.getSlot().isEmpty() ) {
			ObjectFactory objectFactory = new ObjectFactory();

			SlotValue value = objectFactory.createSlotValue();
			value.setType(Const.XML_DATA_TYPE_STRING);
			value.getContent().add(Const.SLOT_KEY_DUMMY);

			Slot slot = objectFactory.createSlot();
			slot.setSlotKey(Const.SLOT_KEY_DUMMY);
			slot.setSlotValue(value);

			newSlots.getSlot().add(slot);
		}

		// <<insert code to react further to this change here
//		PropertyChangeSupport ptyChgFirer = myPtyChg;
//		if ( ptyChgFirer != null ) {
//			ptyChgFirer.firePropertyChange("slots", oldSlots, newSlots);
//		}
	}

}
