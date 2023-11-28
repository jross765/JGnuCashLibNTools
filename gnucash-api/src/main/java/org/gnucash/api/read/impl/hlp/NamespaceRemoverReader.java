package org.gnucash.api.read.impl.hlp;

import java.io.IOException;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * replaces ':' in tag-names and attribute-names by '_' .
 */
public class NamespaceRemoverReader extends Reader {

    protected static final Logger LOGGER = LoggerFactory.getLogger(NamespaceRemoverReader.class);
    
    // ---------------------------------------------------------------

    /**
     * What to read from.
     */
    private Reader input;

    /**
     * How much we have read.
     */
    private long position = 0;

    /**
     * true if we are in a quotation and thus shall not remove any namespaces.
     */
    private boolean isInQuotation = false;

    /**
     * true if we are in a quotation and thus shall remove any namespaces.
     */
    private boolean isInTag = false;

    // ----------------------------
    
    /**
     * For debugging.
     */
    private char[] debugLastTeat = new char[255]; // ::MAGIC

    /**
     * For debugging.
     */
    private int debugLastReatLength = -1;

    // ---------------------------------------------------------------

    /**
     * @param pInput what to read from.
     */
    public NamespaceRemoverReader(final Reader pInput) {
        super();
        input = pInput;
    }

    // ---------------------------------------------------------------

    /**
     * @return How much we have reat.
     */
    public long getPosition() {
        return position;
    }

    /**
     * @return What to read from.
     */
    public Reader getInput() {
        return input;
    }

    /**
     * @param newInput What to read from.
     */
    public void setInput(final Reader newInput) {
        if (newInput == null) {
    	throw new IllegalArgumentException("null not allowed for field this.input");
        }

        input = newInput;
    }

    /**
     * @see java.io.Reader#read(char[], int, int)
     */
    @Override
    public int read(final char[] cbuf, final int off, final int len) throws IOException {

        int reat = input.read(cbuf, off, len);

        logReadBytes(cbuf, off, reat);

        for (int i = off; i < off + reat; i++) {
    	position++;

    	if (isInTag && (cbuf[i] == '"' || cbuf[i] == '\'')) {
    	    toggleIsInQuotation();
    	} else if (cbuf[i] == '<' && !isInQuotation) {
    	    isInTag = true;
    	} else if (cbuf[i] == '>' && !isInQuotation) {
    	    isInTag = false;
    	} else if (cbuf[i] == ':' && isInTag && !isInQuotation) {
    	    cbuf[i] = '_';
    	}

        }

        return reat;
    }

    /**
     * Log the last chunk of bytes reat for debugging-purposes.
     *
     * @param cbuf the data
     * @param off  where to start in cbuf
     * @param reat how much
     */
    private void logReadBytes(final char[] cbuf, final int off, final int reat) {
        debugLastReatLength = Math.min(debugLastTeat.length, reat);
        try {
    	System.arraycopy(cbuf, off, debugLastTeat, 0, debugLastTeat.length);
        } catch (Exception e) {
    	e.printStackTrace();
    	LOGGER.debug("logReadBytes: debugLastReatLength=" + debugLastReatLength + "\n" + "off=" + off + "\n" + "reat=" + reat
    		+ "\n" + "cbuf.length=" + cbuf.length + "\n" + "debugLastTeat.length=" + debugLastTeat.length
    		+ "\n");
        }
    }

    private void toggleIsInQuotation() {
        if (isInQuotation) {
    	isInQuotation = false;
        } else {
    	isInQuotation = true;
        }
    }

    /**
     * @see java.io.Reader#close()
     */
    @Override
    public void close() throws IOException {
        input.close();
    }

}
