package org.gnucash.api.write.impl.hlp;

import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.gnucash.api.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * Helper lass needed for writing GnuCash-Files that are binary-identical to
 * what GnuCash itself writes.
 */
public class WritingContentHandler implements ContentHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WritingContentHandler.class);

    // ::MAGIC
    private static final int LAST_WAS_OPEN_ELEMENT   = 1;
    private static final int LAST_WAS_CLOSE_ELEMENT  = 2;
    private static final int LAST_WAS_CHARACTER_DATA = 3;

    private static final int MAX_DEPTH_1 = 4;
    private static final int MAX_DEPTH_2 = 6;

    // ---------------------------------------------------------------

    private final Writer wrt;

    // ----------------------------

    private final String encodeme[] = new String[] { "&", ">", "<", "\"" };
    private final String encoded[]  = new String[] { "&amp;", "&gt;", "&lt;", "&quot;" };

    // ----------------------------

    int depth = 0;
    int last_was = 0;
    private char[] spaces;
    
    boolean isGUID = false;
    boolean isSlotvalueTypeString = false;
    boolean isTrnDescription = false;
    boolean insideGncTemplateTransactions = false;

    // ---------------------------------------------------------------

    public WritingContentHandler(final Writer wrt) {
    	this.wrt = wrt;
    }

    // ---------------------------------------------------------------

	public void endDocument() throws SAXException {
		try {
			wrt.write("\n\n");
			wrt.write("<!-- Local variables: -->\n");
			wrt.write("<!-- mode: xml        -->\n");
			wrt.write("<!-- End: Written by JGnuCashLib, " + LocalDateTime.now() + " -->\n");
		} catch (IOException e) {
			LOGGER.error("endDocument: Problem", e);
		}

	}

	public void startDocument() throws SAXException {
		try {
			wrt.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
		} catch (IOException e) {
			LOGGER.error("startDocument: Problem", e);
		}
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
		try {
			if ( last_was == LAST_WAS_OPEN_ELEMENT ) {
				wrt.write(">");
			}

			if ( last_was == LAST_WAS_CLOSE_ELEMENT ) {
				return;
			}

			// make sure GUIDs are written with non-capital letters
			if ( isGUID ) {
				String s = new String(ch, start, length);
				wrt.write(s.toLowerCase());
			} else {

				StringBuffer sb = new StringBuffer();
				sb.append(ch, start, length);

				for ( int j = 0; j < encodeme.length; j++ ) {
					int index = 0;
					while ( (index = sb.indexOf(encodeme[j], index)) != -1 ) {
						sb.replace(index, index + encodeme[j].length(), encoded[j]);
						index += encoded[j].length() - encodeme[j].length() + 1;
					}

				}

				// String s = sb.toString();
				// if(s.indexOf("bis 410") != -1) {
				// System.err.println(s+"---"+Integer.toHexString(s.charAt(s.length()-1)));
				// }

				wrt.write(sb.toString());
			}

			last_was = LAST_WAS_CHARACTER_DATA;
		} catch (IOException e) {
			LOGGER.error("characters: Problem", e);
		}

	}

	public void ignorableWhitespace(final char[] ch, final int start, final int length) {
		/*
		 * try { writer.write(ch, start, length); last_was = LAST_WAS_CHARACTERDATA; }
		 * catch (IOException e) { LOGGER.error("ignorableWhitespace: Problem", e);
		 * }
		 */

	}

	public void endPrefixMapping(final String prefix) throws SAXException {
		LOGGER.debug("endPrefixMapping: prefix='" + prefix + "')");

	}

	public void skippedEntity(final String name) throws SAXException {
		LOGGER.debug("skippedEntity: name='" + name + "')");

	}

	public void setDocumentLocator(final Locator locator) {

	}

	public void processingInstruction(final String target, final String data) throws SAXException {
		try {
			wrt.write("<?" + target);
			if ( data != null ) {
				wrt.write(data);
			}

			wrt.write("?>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
		LOGGER.debug("startPrefixMapping: prefix='" + prefix + "')");
	}

	public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
		try {
			// create <slot:value type="string"></slot:value> instead of <slot:value
			// type="string"/>
			if ( ( isTrnDescription || 
				   isSlotvalueTypeString ) && 
				 last_was != LAST_WAS_CHARACTER_DATA ) {
				characters(new char[0], 0, 0);
			}

			if ( qName.equals("gnc_template-transactions") ) {
				insideGncTemplateTransactions = false;
			}

			depth -= 2;

			if ( last_was == LAST_WAS_CLOSE_ELEMENT ) {
				wrt.write("\n");
				writeSpaces();
				wrt.write("</" + qName + ">");
			}

			if ( last_was == LAST_WAS_OPEN_ELEMENT ) {
				wrt.write("/>");
			}

			if ( last_was == LAST_WAS_CHARACTER_DATA ) {
				wrt.write("</" + qName + ">");
			}

			last_was = LAST_WAS_CLOSE_ELEMENT;
		} catch (IOException e) {
			LOGGER.error("endElement: Problem", e);
		}

	}

	public void startElement(final String namespaceURI, final String localName, final String qName,
			final Attributes atts) throws SAXException {
		try {
			if ( last_was == LAST_WAS_OPEN_ELEMENT ) {
				wrt.write(">\n");
				writeSpaces();

			}

			if ( last_was == LAST_WAS_CLOSE_ELEMENT ) {
				wrt.write("\n");
				writeSpaces();
			}

			wrt.write("<" + qName);

			if ( qName.equals("gnc_template-transactions") ) {
				insideGncTemplateTransactions = true;
			}

			isTrnDescription = qName.equals("trn_description");
			isGUID = false;
			isSlotvalueTypeString = false;
			for ( int i = 0; i < atts.getLength(); i++ ) {
				wrt.write(" " + atts.getQName(i) + "=\"" + atts.getValue(i) + "\"");

				if ( atts.getQName(i).equals("type") && atts.getValue(i).equals(Const.XML_DATA_TYPE_GUID) ) {
					isGUID = true;
				}

				if ( qName.equals("slot_value") && atts.getQName(i).equals("type") && 
					 atts.getValue(i).equals(Const.XML_DATA_TYPE_STRING) ) {
					isSlotvalueTypeString = true;
				}

			}
			depth += 2;

			last_was = LAST_WAS_OPEN_ELEMENT;
		} catch (IOException e) {
			LOGGER.error("startElement: Problem", e);
		}

	}

	// ---------------------------------------------------------------

	private void writeSpaces() throws IOException {

		if ( insideGncTemplateTransactions ) {
			if ( depth < MAX_DEPTH_2 ) {
				return;
			}

			wrt.write(getSpaces(), 0, depth - 6);
			return;
		}

		if ( depth < MAX_DEPTH_1 ) {
			return;
		}

		wrt.write(getSpaces(), 0, depth - 4);
	}

	protected char[] getSpaces() {
		if ( spaces == null || 
			 spaces.length < depth ) {
 			spaces = new char[depth];
			Arrays.fill(spaces, ' ');
		}

		return spaces;
	}
}
