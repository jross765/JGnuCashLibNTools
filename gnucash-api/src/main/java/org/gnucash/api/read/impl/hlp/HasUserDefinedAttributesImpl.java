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

import jakarta.xml.bind.JAXBElement;

public class HasUserDefinedAttributesImpl // implements HasUserDefinedAttributes <-- NO!
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HasUserDefinedAttributesImpl.class);
	
	public static final String HIERARCHY_SEPARATOR = ".";

	// ---------------------------------------------------------------

	public static String getUserDefinedAttributeCore(final SlotsType slots, final String name) {
		if ( slots == null )
			return null;
		
		return getUserDefinedAttributeCore(slots.getSlot(), name);
	}
	
	public static List<String> getUserDefinedAttributeKeysCore(final SlotsType slots) {
		if ( slots == null )
			return null;
		
		return getUserDefinedAttributeKeysCore(slots.getSlot());
	}
    
	// ---------------------------------------------------------------

	private static String getUserDefinedAttributeCore(final List<Slot> slotList,
													  final String name) {
		if ( slotList == null )
			return null;

		if ( name.equals("") )
			return null;

		// NO:
//		if ( ! getUserDefinedAttributeKeysCore(slotList).contains(name) ) {
//			throw new SlotListDoesNotContainKeyException();
//		}
		
		// ---

		String nameFirst = "";
		String nameRest = "";
		if ( name.contains(HIERARCHY_SEPARATOR) ) {
			String[] nameParts = name.split(HIERARCHY_SEPARATOR.replace(".", "\\."));
			nameFirst = nameParts[0];
			if ( nameParts.length > 1 ) {
				for ( int i = 1; i < nameParts.length; i++ ) {
					nameRest += nameParts[i];
					if ( i < nameParts.length - 1 )
						nameRest += HIERARCHY_SEPARATOR;
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
				if ( slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_STRING) || 
					 slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_INTEGER) || 
					 slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GUID) ) {
					List<Object> objList = slot.getSlotValue().getContent();
					if ( objList == null || objList.size() == 0 )
						return null;
					Object valElt = objList.get(0);
					if ( valElt == null )
						return null;
					LOGGER.debug("User-defined attribute for key '" + nameFirst + "' may not be a String."
							+ " It is of type [" + valElt.getClass().getName() + "]");
					if ( valElt instanceof String ) {
						return (String) valElt;
					} else if ( valElt instanceof JAXBElement ) {
						JAXBElement elt = (JAXBElement) valElt;
						return elt.getValue().toString();
					} else {
						LOGGER.error("User-defined attribute for key '" + nameFirst + "' may not be a String."
								+ " It is of UNKNOWN type [" + valElt.getClass().getName() + "]");
						return "ERROR";
					}
				} else if ( slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_GDATE) ) {
					List<Object> objList = slot.getSlotValue().getContent();
					if ( objList == null || objList.size() == 0 )
						return null;
					Object valElt = null;
					for ( Object obj : objList ) {
						if ( obj.getClass().getName().contains("JAXBElement") ) {
							valElt = obj;
						}
					}
					if ( valElt == null )
						return null;
					LOGGER.debug("User-defined attribute for key '" + nameFirst + "' may not be a String."
							+ " It is of type [" + valElt.getClass().getName() + "]");
					if ( valElt instanceof String ) {
						return (String) valElt;
					} else if ( valElt instanceof JAXBElement ) {
						JAXBElement elt = (JAXBElement) valElt;
						return elt.getValue().toString();
					} else {
						LOGGER.error("User-defined attribute for key '" + nameFirst + "' may not be a String."
								+ " It is of UNKNOWN type [" + valElt.getClass().getName() + "]");
						return "ERROR";
					}
				} else if ( slot.getSlotValue().getType().equals(Const.XML_DATA_TYPE_FRAME) ) {
					List<Object> objList = slot.getSlotValue().getContent();
					if ( objList == null || objList.size() == 0 )
						return null;
					List<Slot> subSlots = new ArrayList<Slot>();
					for ( Object obj : objList ) {
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

    protected static List<String> getUserDefinedAttributeKeysCore(final List<Slot> slotList) {
		if ( slotList == null )
			return null;
		
		List<String> retval = new ArrayList<String>();

		for ( Slot slt : slotList ) {
			retval.add(slt.getSlotKey());
		}

		return retval;
	}
    
	// ---------------------------------------------------------------

	// Return slots without the ones with dummy content
	public static List<Slot> getSlotsListClean(final List<Slot> slotList) {
		List<Slot> retval = new ArrayList<Slot>();

		for ( Slot slot : slotList ) {
			if ( ! slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				retval.add(slot);
			}
		}

		return retval;
	}

	// Remove slots with dummy content
	public static void cleanSlots(final List<Slot> slotList) {
		for ( Slot slot : slotList ) {
			if ( ! slot.getSlotKey().equals(Const.SLOT_KEY_DUMMY) ) {
				slotList.remove(slot);
			}
		}
	}
	
	// ---------------------------------------------------------------
	
	// Sic, not in HasWritableUserDefinedAttributes
	//                ========
	public static void setSlotsInit(
			SlotsType currSlots,
			final SlotsType newSlots) {
		if ( newSlots == null ) {
			throw new IllegalArgumentException("argument <newSlots> is null");
		}

		if ( currSlots == newSlots ) {
			return; // nothing has changed
		}
		
		if ( currSlots.getSlot().size() == newSlots.getSlot().size() ) {
			boolean areEqual = true;
			int size = currSlots.getSlot().size();
			for ( int i = 0; i < size; i++ ) {
				if ( ! currSlots.getSlot().get(i).equals(newSlots.getSlot().get(i)) ) {
					areEqual = false;
					LOGGER.debug("setSlotsInit: current and new slots objects are not equal");
				}
			}
			if ( areEqual ) {
				LOGGER.debug("setSlotsInit: current and new slots objects are equal");
				return;
			}
		} else {
			// have changed
			LOGGER.debug("setSlotsInit: current and new slots objects have different sizes");
		}

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
